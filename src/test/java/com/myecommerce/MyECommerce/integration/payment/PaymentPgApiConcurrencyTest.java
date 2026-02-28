package com.myecommerce.MyECommerce.integration.payment;

import com.myecommerce.MyECommerce.dto.payment.PgApprovalResult;
import com.myecommerce.MyECommerce.dto.payment.PgResult;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.order.OrderItem;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.integration.config.TestAuditingConfig;
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import com.myecommerce.MyECommerce.repository.member.MemberAuthorityRepository;
import com.myecommerce.MyECommerce.repository.member.MemberRepository;
import com.myecommerce.MyECommerce.repository.payment.PaymentRepository;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import com.myecommerce.MyECommerce.service.payment.PaymentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;
import static com.myecommerce.MyECommerce.type.OrderStatusType.PAID;
import static com.myecommerce.MyECommerce.type.PaymentMethodType.CARD;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.APPROVED;
import static com.myecommerce.MyECommerce.type.PgProviderType.MOCK_PG;
import static com.myecommerce.MyECommerce.type.ProductCategoryType.WOMEN_CLOTHING;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestAuditingConfig.class)
public class PaymentPgApiConcurrencyTest {

    private static final Logger log = LoggerFactory.getLogger(PaymentPgApiConcurrencyTest.class);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @MockitoSpyBean
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /* ------------------
        Test Fixtures
       ------------------ */

    private Member savedCustomer;
    private Order savedOrder;
    private Payment savedPayment;

    @BeforeEach
    void setUp() {
        // 결제를 위한 회원 등록
        savedCustomer = saveCustomer();
        // 결제를 위한 주문 등록
        savedOrder = saveOrder();
//        // PG 승인요청한 결제 등록
//        savedPayment = saveInProgressPayment(savedOrder);
    }

    @AfterEach
    void cleanUp() {
        transactionTemplate.executeWithoutResult(status -> {
            Long productId = savedOrder.getItems().get(0).getProduct().getId();

            paymentRepository.deleteById(savedPayment.getId());
            orderRepository.deleteById(savedOrder.getId());
            productRepository.deleteById(productId);
            memberAuthorityRepository.deleteByMemberId(savedCustomer.getId());
            memberRepository.deleteById(savedCustomer.getId());
        });
    }

    /* ------------------
        Helper Methods
       ------------------ */

    /** 주문자 등록 */
    Member saveCustomer() {
        Member member = Member.builder()
                .userId("customer")
                .password("password")
                .name("name")
                .telephone("01011112222")
                .address("address")
                .build();
        Member savedCustomer = memberRepository.save(member);

        MemberAuthority authority = MemberAuthority.builder()
                .authority(CUSTOMER)
                .member(savedCustomer)
                .build();
        memberAuthorityRepository.save(authority);

        return memberRepository.findById(savedCustomer.getId()).orElseThrow();
    }

    /** 주문 등록 */
    Order saveOrder() {
        // 상품등록
        Product savedProduct = saveProduct();
        ProductOption savedOption = savedProduct.getOptions().get(0);

        // 주문물품 생성
        OrderItem orderItem =
                OrderItem.createOrderItem(savedOption, 1);

        // 주문 생성 및 등록
        Order order = Order.createOrder(List.of(orderItem), savedCustomer);
        return orderRepository.save(order);
    }

    /** 상품 등록 */
    Product saveProduct() {
        Product product = Product.builder()
                .code("productCode")
                .name("productName")
                .saleStatus(ON_SALE)
                .category(WOMEN_CLOTHING)
                .seller(10L)
                .build();

        ProductOption productOption = ProductOption.builder()
                .optionCode("optionCode")
                .optionName("optionName")
                .price(new BigDecimal("10000"))
                .quantity(10)
                .product(product)
                .build();

        product.setOptions(List.of(productOption));

        return productRepository.save(product);
    }

    /** PG 결제요청 결제 등록 */
    Payment saveInProgressPayment(Order order, String pgTransactionId) {
        // 결제 생성 (READY)
        Payment payment = Payment.createPayment(order, CARD, MOCK_PG);

        // PG 결제요청 (READY -> IN_PROGRESS)
        PgResult pgResult = PgResult.builder()
                .pgTransactionId(pgTransactionId)
                .build();
        payment.requestPgPayment(pgResult);

        // 결제 저장
        return paymentRepository.save(payment);
    }

    /** PG 결제승인 웹훅 동시성 실행
     *  : 지저분한 기술적 코드를 메서드로 분리 */
    void executeConcurrentHandlerWebhooks(PgApprovalResult request) throws Exception {
        // 트랜잭션 생성
        int threadCount = 10;
        // 동시에 실행될 스레드 풀
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        // 모든 스레드가 끝날 때까지 대기 (해당 코드 없으면 테스트가 중간에 끝남)
        CountDownLatch readyLatch = new CountDownLatch(threadCount); // 준비 완료 신호
        CountDownLatch startSignal = new CountDownLatch(1);        // 동시 출발 신호
        CountDownLatch doneLatch = new CountDownLatch(threadCount);  // 종료 대기 신호

        // when
        for (int i = 0; i < threadCount; i++) {
            // 여러 트랜잭션 동시 실행 (각 submit은 독립 트랜잭션)
            executor.submit(() -> {
                try {
                    startSignal.await(); // 모든 스레드가 준비될 때까지 대기
                    // 주문 생성
                    paymentService.handlePgWebHook(request);

                } catch (Exception e) {
                    log.error(e.getMessage(), e);

                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();    // 모든 스레드가 생성될 때까지 메인인 테스트 스레드 대기
        startSignal.countDown(); // "탕!" 하고 일제히 출발
        doneLatch.await();     // 모든 스레드 종료 대기
        // 스레드풀 자원 종료
        executor.shutdown();
    }

    /* ---------------------
        PG 결제승인 웹훅 Test
       --------------------- */

    @Test
    @DisplayName("PG 결제승인 웹훅 성공 - 동일 주문에 대한 여러 건의 PG 결제승인 웹훅 시도 시 결제 1건만 반영")
    void handlePgWebHook_shouldSuccess_whenConcurrentRequestsForSamePaymentOccur()
            throws Exception {
        // given
        // 주문
        Order createdOrder = savedOrder;
        // 주문에 대해 PG 결제요청된 결제
        savedPayment = saveInProgressPayment(createdOrder, "pgTransactionId");

        // PG 결제승인 요청값
        PgApprovalResult request = PgApprovalResult.builder()
                .pgTransactionId("pgTransactionId")
                .paidAmount(new BigDecimal("10000"))
                .approvalStatus(APPROVED)
                .approvalAt(LocalDateTime.now())
                .build();

        // when
        // PG 결제승인 웹훅 동시성 실행
        executeConcurrentHandlerWebhooks(request);

        // then
        // 결제상태 검증
        Payment resultPayment = paymentRepository.findByPgTransactionIdWithOrder("pgTransactionId").orElseThrow();
        assertEquals(APPROVED, resultPayment.getPaymentStatus());

        // 주문상태 검증
        Long resultOrderId = resultPayment.getOrder().getId();
        Order resultOrder = orderRepository.findById(resultOrderId).orElseThrow();
        assertEquals(PAID, resultOrder.getOrderStatus());

        // 주문 더티체킹을 위한 조회 횟수 검증
        verify(orderRepository, times(1))
                .findByIdAndOrderStatus(any(), eq(CREATED));
    }
}
