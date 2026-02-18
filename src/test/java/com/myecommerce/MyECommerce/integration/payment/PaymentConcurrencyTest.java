package com.myecommerce.MyECommerce.integration.payment;

import com.myecommerce.MyECommerce.dto.payment.RequestPaymentDto;
import com.myecommerce.MyECommerce.dto.payment.ResponsePaymentDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.order.OrderItem;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.exception.PaymentException;
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
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.PaymentMethodType.CARD;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.IN_PROGRESS;
import static com.myecommerce.MyECommerce.type.PgProviderType.MOCK_PG;
import static com.myecommerce.MyECommerce.type.ProductCategoryType.WOMEN_CLOTHING;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestAuditingConfig.class)
public class PaymentConcurrencyTest {

    private static final Logger log = LoggerFactory.getLogger(PaymentStartIntegrationTest.class);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /* ------------------
        Test Fixtures
       ------------------ */

    private Member savedCustomer;
    private Order savedOrder;
    private List<Long> paymentIds;

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MemberAuthorityRepository memberAuthorityRepository;

    @BeforeEach
    void setUp() {
        // 결제를 위한 회원 등록
        savedCustomer = saveCustomer();
        // 결제를 위한 주문 등록
        savedOrder = saveOrder();
    }

    @AfterEach
    void cleanUp() {
        transactionTemplate.executeWithoutResult(status -> {
            Long productId = savedOrder.getItems().get(0).getProduct().getId();

            paymentRepository.deleteAllById(paymentIds);
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

    /** 등록한 회원 조회 */
    Member findMemberWithAuthorities() {
        // 회원 조회
        Member member = memberRepository.findById(savedCustomer.getId())
                .orElseThrow();
        // 회원 권한 조회 및 추가
        List<MemberAuthority> authorities =
                memberAuthorityRepository.findByMemberId(savedCustomer.getId());
        member.setRoles(authorities);

        return member;
    }

    /* ------------------
        결제시작 Test
       ------------------ */

    @Test
    @DisplayName("결제시작 성공 - 동일 주문에 대한 여러 건의 결제 시도 시 결제 1건만 실행되고 나머지는 실패")
    void startPayment_shouldSuccessOneOfPayment_whenConcurrentRequestsForSameOrderOccur()
            throws InterruptedException {
        // given
        RequestPaymentDto request = RequestPaymentDto.builder()
                .orderId(savedOrder.getId())
                .paymentMethod(CARD)
                .build();

        // 트랜잭션 생성
        int threadCount = 10;
        // 동시에 실행될 스레드 풀
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        // 모든 스레드가 끝날 때까지 대기 (해당 코드 없으면 테스트가 중간에 끝남)
        CountDownLatch latch = new CountDownLatch(threadCount);

        paymentIds = Collections.synchronizedList(new ArrayList<>());
        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

        // when
        // TODO: 가독성 향상을 위해 try 내부 로직 메서드로 추출 고려 ( executePaymentRequest(request, exceptions) )
        for (int i = 0; i < threadCount; i++) {
            // 여러 트랜잭션 동시 실행 (각 submit은 독립 트랜잭션)
            executor.submit(() -> {
                try {
                    Member member = findMemberWithAuthorities();

                    // 주문 생성
                    ResponsePaymentDto response =
                            paymentService.startPayment(request, member);
                    // 데이터 일괄 삭제를 위한 주문 키 추가
                    paymentIds.add(response.getPaymentId());

                } catch (Exception e) {
                    exceptions.add(e);
                    log.error(e.getMessage(), e);

                } finally {
                    latch.countDown();
                }
            });
        }

        // 메인인 테스트 스레드 대기 -> 모든 결제 완료 후 검증가능
        latch.await();
        // 스레드풀 자원 종료
        executor.shutdown();

        // then
        // 성공 검증
        assertEquals(1, paymentIds.size()); // 10번 정상 실행
        // 중복 생성 방지 검증
        assertEquals(1, paymentIds.stream().distinct().count()); // 객체는 1개만 생성
        // DB 검증
        List<Payment> payments = paymentRepository.findByOrderIdAndPaymentMethodAndPgProvider(
                request.getOrderId(), CARD, MOCK_PG);
        assertEquals(1, payments.size()); // 객체는 1개만 생성
        assertEquals(IN_PROGRESS, payments.get(0).getPaymentStatus());
        // 실패 검증 - 예외 발생 확인: 나머지 스레드는 PAYMENT_STATUS_NOT_READY
        assertEquals(threadCount-1, exceptions.size());
        assertTrue(exceptions.stream()
                .allMatch(ex -> ex instanceof PaymentException));
    }

}
