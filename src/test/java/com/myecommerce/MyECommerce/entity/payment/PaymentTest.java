package com.myecommerce.MyECommerce.entity.payment;

import com.myecommerce.MyECommerce.dto.payment.PgApprovalResult;
import com.myecommerce.MyECommerce.dto.payment.PgResult;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.order.OrderItem;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.exception.PaymentException;
import com.myecommerce.MyECommerce.service.payment.PgClient;
import com.myecommerce.MyECommerce.service.payment.TestPgClientImpl;
import com.myecommerce.MyECommerce.type.PaymentMethodType;
import com.myecommerce.MyECommerce.type.PgProviderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.*;
import static com.myecommerce.MyECommerce.type.PaymentMethodType.CARD;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.*;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 고객 생성 */
    Member member() {
        return Member.builder()
                .id(5L)
                .build();
    }

    /** 상품옵션 생성 */
    ProductOption productOption() {
        return ProductOption.builder()
                .price(new BigDecimal("10000"))
                .quantity(10)
                .product(Product.builder()
                        .saleStatus(ON_SALE)
                        .build())
                .build();
    }

    /** PG 결제대행사 반환 */
    PgProviderType pgProvider() {
        PgClient pgClient = new TestPgClientImpl();
        return pgClient.getProvider();
    }

    /** 주문 생성 */
    private Order order() {
        // 주문 상품옵션
        ProductOption productOption = productOption();
        // 주문요청 상품옵션 수량
        int orderQuantity = 1;
        // 주문 물품 목록
        OrderItem item = OrderItem.createOrderItem(productOption, orderQuantity);
        // 주문자
        Member member = member();

        return Order.createOrder(List.of(item), member);
    }

    /** 결제 완료된 주문 생성 */
    private Order paidOrder() {
        // 주문 상품옵션
        ProductOption productOption = productOption();
        // 주문요청 상품옵션 수량
        int orderQuantity = 3;
        // 주문 물품 목록
        OrderItem item = OrderItem.createOrderItem(productOption, orderQuantity);
        // 주문자
        Member member = member();

        // 주문 생성
        Order paidOrder = Order.createOrder(List.of(item), member);
        // 주문에 대한 결제 생성
        Payment payment = approvedPayment();
        // 주문상태 결제완료로 변경
        paidOrder.paid(payment);

        return paidOrder;
    }

    /** 결제 객체 생성 - 결제상태 READY */
    Payment readyPayment() {
        // 등록된 주문 (주문 상태는 CREATED)
        Order order = order();
        // 요청 결제 방식
        PaymentMethodType requestMethod = CARD;
        // 회사와 결제 계약된 PG사
        PgProviderType pgProvider = pgProvider();

        // 결제 객체 생성
        return Payment.createPayment(order, requestMethod, pgProvider);
    }

    /** PG 요청한 결제 객체 생성 - 결제상태 IN_PROGRESS */
    Payment inProgressPayment() {
        // 결제 객체 생성
        Payment payment = readyPayment();
        // PG 요청 결과
        PgResult pgResult = pgResult();
        // 결제 객체에 PG 요청 결과 반영
        payment.requestPgPayment(pgResult);

        return payment;
    }

    /** PG 승인된 결제 생성 - 결제상태 APPROVED */
    Payment approvedPayment() {
        // 등록된 주문 (주문 상태는 CREATED)
        Order order = order();
        // 요청 결제 방식
        PaymentMethodType requestMethod = CARD;
        // 회사와 결제 계약된 PG사
        PgProviderType pgProvider = pgProvider();

        // 결제 생성 (READY)
        Payment payment = Payment.createPayment(order, requestMethod, pgProvider);
        // PG 결제 요청 (READY -> IN_)
        PgResult pgResult = PgResult.builder()
                .pgTransactionId("pgTransactionId")
                .build();
        payment.requestPgPayment(pgResult);
        // PG 결제 승인
        PgApprovalResult pgApprovalResult = PgApprovalResult.builder()
                .approvalStatus(APPROVED)
                .paidAmount(new BigDecimal("10000"))
                .build();
        payment.approve(pgApprovalResult);

        return payment;
    }
    /* ------------------
        Helper Method
       ------------------ */

    /* ----------------------
        결제 생성 Tests
       ---------------------- */

    @Test
    @DisplayName("결제 객체 생성 성공")
    void createPayment_shouldPaymentStatusIsReady_whenOrderStatusIsCreated() {
        // given
        // 등록된 주문 (주문 상태는 CREATED)
        Order order = order();
        // 요청 결제 방식
        PaymentMethodType requestMethod = CARD;
        // 회사와 결제 계약된 PG사
        PgProviderType pgProvider = pgProvider();

        // when
        Payment payment =
                Payment.createPayment(order, requestMethod, pgProvider);

        // then
        assertEquals(order, payment.getOrder());
        assertNotNull(payment.getPaymentCode());
        assertEquals(requestMethod, payment.getPaymentMethod());
        assertEquals(pgProvider, payment.getPgProvider());
        assertEquals(READY, payment.getPaymentStatus()); // 결제 생성 상태
    }

    @Test
    @DisplayName("결제 객체 생성 실패 - 주문상태가 CREATED(주문생성)가 아닌 경우 예외발생")
    void createPayment_shouldNotCreatePayment_whenOrderStatusNotCreated() {
        // given
        // 등록된 주문 (주문 상태는 PAID)
        Order invalidOrder = paidOrder();
        // 요청 결제 방식
        PaymentMethodType requestMethod = CARD;
        // 회사와 결제 계약된 PG사
        PgProviderType pgProvider = pgProvider();

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                Payment.createPayment(invalidOrder, requestMethod, pgProvider));
        assertEquals(ORDER_STATUS_NOT_CREATED, e.getErrorCode()); // 주문이 CREATED인 경우만 객체 생성 가능
    }

    /* ----------------------
        결제상태 전이 Tests
       ---------------------- */

    @Test
    @DisplayName("결제상태 READY -> IN_PROGRESS 전이 성공 - PG 요청 응답 반환 시 결제 요청상태로 변경 및 트랜잭션 ID 저장")
    void requestPgPayment_shouldChangeInProgressPaymentStatus_whenPaymentStatusIsReady() {
        // given
        // 결제 객체 생성 (결제상태 READY)
        Payment payment = readyPayment();
        // PG 요청 결과
        PgResult pgResult = PgResult.builder()
                .pgTransactionId("pgTransactionId")
                .build();

        // when
        payment.requestPgPayment(pgResult);

        // then
        assertEquals(IN_PROGRESS, payment.getPaymentStatus());
        assertEquals(pgResult.getPgTransactionId(), payment.getPgTransactionId());
    }

    @Test
    @DisplayName("결제상태 READY -> IN_PROGRESS 전이 실패 - PG 결제 응답 미존재 시 예외발생")
    void requestPgPayment_shouldThrowException_whenPgRequestResultNotExists() {
        // given
        // 결제 객체 생성 (결제상태 READY)
        Payment payment = readyPayment();

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                payment.requestPgPayment(null)); // PG 결제요청 응답 없으므로 null 전달
        assertEquals(PG_REQUEST_FAILED, e.getErrorCode());
    }

    @Test
    @DisplayName("결제상태 READY -> IN_PROGRESS 전이 실패 - PG 결제 응답의 트랜잭션 ID 미존재 시 예외발생")
    void requestPgPayment_shouldThrowException_whenTransactionIdNotExistsAtPgRequestResult() {
        // 결제 객체 생성 (결제상태 READY)
        Payment payment = readyPayment();
        // PG 요청 결과
        PgResult invalidPgResult = PgResult.builder()
                .pgTransactionId(null) // 트랜잭션 ID 미존재
                .build();

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                payment.requestPgPayment(invalidPgResult));
        assertEquals(PG_RESPONSE_TRANSACTION_ID_NOT_EXISTS, e.getErrorCode());
    }

    @Test
    @DisplayName("결제상태 READY -> IN_PROGRESS 전이 실패 - 기존 결제 상태가 READY가 아니면 예외발생")
    void requestPgPayment_shouldThrowException_whenOriginalPaymentStatusIsNotReady() {
        // given
        Payment invalidPayment = approvedPayment(); // 기존 결제 상태 APPROVED
        PgResult pgResult = pgResult();

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                invalidPayment.requestPgPayment(pgResult));
        assertEquals(PAYMENT_STATUS_NOT_READY, e.getErrorCode());
    }

    @Test
    @DisplayName("결제상태 IN_PROGRESS -> APPROVED 전이 성공 - PG 결제 승인 성공 시 승인상태로 변경 및 지불금액, 부가세 저장")
    void approve_shouldChangeApprovedPaymentStatusAndSetApprovedAmountAndVat_whenPaymentStatusIsInProgress() {
        // given
        // PG 결제 진행중인 결제 객체
        Payment payment = inProgressPayment();
        // PG 승인 결과
        PgApprovalResult pgApprovalResult = PgApprovalResult.builder()
                .pgTransactionId("pgTransactionId")
                .approvalStatus(APPROVED) // PG 승인
                .paidAmount(new BigDecimal("10000"))
                .vatAmount(new BigDecimal("1000"))
                .build();

        // when
        payment.approve(pgApprovalResult);

        // then
        assertEquals(APPROVED, payment.getPaymentStatus()); // 결제 승인
        assertEquals(pgApprovalResult.getPaidAmount(), payment.getApprovedAmount());
        assertEquals(pgApprovalResult.getVatAmount(), payment.getVatAmount());
    }

    @Test
    @DisplayName("결제상태 IN_PROGRESS -> APPROVED 전이 실패 - PG 승인 응답의 트랜잭션 ID 불일치 시 예외발생")
    void approve_shouldThrowException_whenPgTransactionIdMismatches() {
        // given
        Payment payment = inProgressPayment(); // PG 트랜잭션 ID = pgTransactionId
        PgApprovalResult invalidPgApprovalResult = PgApprovalResult.builder()
                .pgTransactionId("invalidPgTransactionId") // PG 트랜잭션 ID = invalidPgTransactionId
                .build();

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                payment.approve(invalidPgApprovalResult));
        assertEquals(PG_TRANSACTION_ID_MISMATCH, e.getErrorCode());
    }

    @Test
    @DisplayName("결제상태 IN_PROGRESS -> APPROVED 전이 실패 - 기존 결제 상태가 IN_PROGRESS 상태가 아니면 예외발생")
    void approve_shouldThrowException_whenOriginalPaymentStatusIsNotInProgress() {
        // given
        Payment invalidPayment = approvedPayment(); // 승인된 결제
        PgApprovalResult pgApprovalResult = pgApprovalResult();

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                invalidPayment.approve(pgApprovalResult));
        assertEquals(PAYMENT_STATUS_NOT_IN_PROGRESS, e.getErrorCode());
    }

    @Test
    @DisplayName("결제상태 IN_PROGRESS -> APPROVED 전이 실패 - 주문, 결제 금액 불일치 시 예외발생")
    void approve_shouldThrowException_whenOrderAmountAndPaidAmountAreConsistency() {
        // given
        Payment payment = inProgressPayment(); // 주문금액 10000
        PgApprovalResult invalidPgApprovalResult = PgApprovalResult.builder()
                .pgTransactionId("pgTransactionId")
                .approvalStatus(APPROVED)
                .paidAmount(new BigDecimal("50000")) // 결제금액 50000
                .build();

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                payment.approve(invalidPgApprovalResult));
        assertEquals(PAYMENT_AMOUNT_INCONSISTENCY, e.getErrorCode());
    }

    @Test
    @DisplayName("결제상태 IN_PROGRESS -> FAILED 전이 성공 - PG 승인 실패 시 결제 승인실패 상태로 변경")
    void fail_shouldChangeFailedPaymentStatus_whenPaymentStatusIsInProgress() {
        // given
        Payment payment = inProgressPayment();
        // PG 승인 결과
        PgApprovalResult pgApprovalResult = PgApprovalResult.builder()
                .pgTransactionId("pgTransactionId")
                .approvalStatus(FAILED) // PG 승인 실패
                .paidAmount(new BigDecimal("10000"))
                .vatAmount(new BigDecimal("1000"))
                .build();

        // when
        payment.fail(pgApprovalResult);

        // then
        assertEquals(FAILED, payment.getPaymentStatus()); // 결제 승인 실패
    }

    // 결제 상태 FAILED로 전이 불가 - 기존 결제 상태가 IN_PROGRESS 상태가 아닐 때

    /* ----------------------
        결제상태 판단 Tests
       ---------------------- */

    // 결제 종결 여부 반환 성공
    // PG 결제 요청 가능 상태 여부 반환 성공
}