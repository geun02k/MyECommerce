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
import com.myecommerce.MyECommerce.exception.PaymentException;
import com.myecommerce.MyECommerce.integration.config.TestAuditingConfig;
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import com.myecommerce.MyECommerce.repository.member.MemberAuthorityRepository;
import com.myecommerce.MyECommerce.repository.member.MemberRepository;
import com.myecommerce.MyECommerce.repository.payment.PaymentRepository;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import com.myecommerce.MyECommerce.service.payment.PaymentService;
import com.myecommerce.MyECommerce.type.PaymentStatusType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.PaymentMethodType.CARD;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.APPROVED;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.IN_PROGRESS;
import static com.myecommerce.MyECommerce.type.PgProviderType.MOCK_PG;
import static com.myecommerce.MyECommerce.type.ProductCategoryType.WOMEN_CLOTHING;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestAuditingConfig.class)
public class PaymentPgApiConsistencyTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
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
    private Payment webhookTargetPayment;
    private Payment savedApprovedPayment;

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

            paymentRepository.deleteById(webhookTargetPayment.getId());
            paymentRepository.deleteById(savedApprovedPayment.getId());
            orderRepository.deleteById(savedOrder.getId());
            productRepository.deleteById(productId);
            memberAuthorityRepository.deleteByMemberId(savedCustomer.getId());
            memberRepository.deleteById(savedCustomer.getId());
        });
    }

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

    /** PG 결제승인된 결제 등록 */
    Payment savePaidPayment(Order order, String pgTransactionId) {
        // PG 결제요청 된 결제 생성 (IN_PROGRESS)
        Payment payment = saveInProgressPayment(order, pgTransactionId);

        // PG 결제승인 (IN_PROGRESS -> APPROVED)
        PgApprovalResult pgApprovalResult =
                pgApprovalResult(APPROVED, pgTransactionId);
        payment.approve(pgApprovalResult);

        return paymentRepository.save(payment);
    }

    /** PG 승인 결과 생성 */
    PgApprovalResult pgApprovalResult(PaymentStatusType approvalStatus, String pgTransactionId) {
        return PgApprovalResult.builder()
                .pgTransactionId(pgTransactionId)
                .approvalStatus(approvalStatus)
                .paidAmount(new BigDecimal("10000"))
                .vatAmount(new BigDecimal("9091"))
                .build();
    }

    /** 주문 결제완료 상태로 변경 */
    void changeOrderToPaid(Order order) {
        savedApprovedPayment = savePaidPayment(order, "paidPgTransactionId");
        order.paid(savedApprovedPayment);
        orderRepository.save(order);
    }

    /* ---------------------
        PG 결제승인 웹훅 Test
       --------------------- */

    // Order 실패 -> Payment rollback 확인
    // 주문 변경 실패 시 결제 승인 내역까지 함께 롤백되어 PG 승인 완료와 내부 DB 미승인 간의 상태 불일치가 일어나는 기존 한계를 검증
    @Test
    @DisplayName("PG 결제승인 웹훅 실패 - 주문 처리 실패로 Payment 승인까지 rollback 되는 트랜잭션 정합성 문제 재현")
    void handlePgWebHook_shouldRollbackPaymentApproval_whenOrderUpdateFailed() {
        // given
        // 주문
        Order order = savedOrder;
        // 웹훅 대상 Payment
        webhookTargetPayment = saveInProgressPayment(order, "pgTransactionId");
        // PG 결제승인 요청값
        PgApprovalResult request = PgApprovalResult.builder()
                .pgTransactionId("pgTransactionId")
                .paidAmount(new BigDecimal("10000"))
                .approvalStatus(APPROVED)
                .approvalAt(LocalDateTime.now())
                .build();

        // Order 상태 변경 실패 조건 생성
        changeOrderToPaid(order);

        // when
        // PG 결제승인 웹훅 실행
        assertThrows(PaymentException.class, () ->
                paymentService.handlePgWebHook(request));

        // then
        // payment가 승인되지 않음을 검증
        Payment rollbackPayment =
                paymentRepository.findById(webhookTargetPayment.getId())
                        .orElseThrow();
        assertEquals(IN_PROGRESS, rollbackPayment.getPaymentStatus());
    }
} 
