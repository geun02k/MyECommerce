package com.myecommerce.MyECommerce.entity.payment;

import com.myecommerce.MyECommerce.dto.payment.PgApprovalResult;
import com.myecommerce.MyECommerce.dto.payment.PgResult;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.order.OrderItem;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.exception.PaymentException;
import com.myecommerce.MyECommerce.type.PaymentMethodType;
import com.myecommerce.MyECommerce.type.PaymentStatusType;
import com.myecommerce.MyECommerce.type.PgProviderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

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
    ProductOption productOption(BigDecimal price) {
        return ProductOption.builder()
                .price(price)
                .quantity(10)
                .product(Product.builder()
                        .saleStatus(ON_SALE)
                        .build())
                .build();
    }

    /** PG 결제대행사 반환 */
    PgProviderType pgProvider() {
        return PgProviderType.MOCK_PG;
    }

    /** 주문 생성 */
    private Order order(BigDecimal orderAmount) {
        // 주문 상품옵션
        ProductOption productOption = productOption(orderAmount);
        // 주문 물품 목록
        OrderItem item = OrderItem.createOrderItem(productOption, 1);

        return Order.createOrder(List.of(item), member());
    }
    private Order order() {
        return order(new BigDecimal("10000"));
    }

    /** 결제 완료된 주문 생성 */
    private Order paidOrder() {
        // 주문 생성
        Order order = order();
        // 주문상태 결제완료로 변경
        order.paid(approvedPayment());
        return order;
    }

    /** 결제 객체 생성 - 결제상태 READY */
    Payment readyPayment(Order order) {
        // 요청 결제 방식
        PaymentMethodType requestMethod = CARD;
        // 회사와 결제 계약된 PG사
        PgProviderType pgProvider = pgProvider();

        // 결제 객체 생성
        return Payment.createPayment(order, requestMethod, pgProvider);
    }
    Payment readyPayment() {
        return readyPayment(order());
    }

    /** PG 요청한 결제 객체 생성 - 결제상태 IN_PROGRESS */
    Payment inProgressPayment(Order order, String pgTransactionId) {
        // 결제 객체 생성
        Payment payment = readyPayment(order);
        // PG 요청 결과
        PgResult pgResult = pgResult(pgTransactionId);
        // 결제 객체에 PG 요청 결과 반영
        payment.requestPgPayment(pgResult);
        return payment;
    }
    Payment inProgressPayment(Order order) {
        return inProgressPayment(order, "pgTransactionId");
    }
    Payment inProgressPayment(String pgTransactionId) {
        return inProgressPayment(order(), pgTransactionId);
    }
    Payment inProgressPayment() {
        return inProgressPayment(order(),"pgTransactionId");
    }

    /** PG 승인된 결제 생성 - 결제상태 APPROVED */
    Payment approvedPayment() {
        // PG 결제 요청한 결제
        Payment payment = inProgressPayment();
        // PG 결제 승인
        PgApprovalResult pgApprovalResult = pgApprovalResult(APPROVED);
        payment.approve(pgApprovalResult);
        return payment;
    }

    /** PG 승인실패된 결제 생성 - 결제상태 FAILED */
    Payment failedPayment() {
        // PG 결제 요청한 결제
        Payment payment = inProgressPayment();
        // PG 결제 승인
        PgApprovalResult pgApprovalResult = pgApprovalResult(FAILED);
        payment.fail(pgApprovalResult);
        return payment;
    }

    /** PG 승인취소된 결제 생성 - 결제상태 CANCELED */
    Payment canceledPayment() {
        // PG 결제 요청한 결제
        Payment payment = inProgressPayment();
        // PG 결제 승인취소 (현재 로직에서 미지원으로 강제변경)
        ReflectionTestUtils.setField(payment, "paymentStatus", CANCELED);
        return payment;
    }

    /** PG 요청 결과 생성 */
    PgResult pgResult(String pgTransactionId) {
        return PgResult.builder()
                .pgTransactionId(pgTransactionId)
                .build();
    }
    PgResult pgResult() {
        return pgResult("pgTransactionId");
    }

    /** PG 승인 결과 생성 */
    PgApprovalResult pgApprovalResult(String pgTransactionId,
                                      PaymentStatusType approvalStatus,
                                      BigDecimal paidAmount) {
        return PgApprovalResult.builder()
                .pgTransactionId(pgTransactionId)
                .approvalStatus(approvalStatus)
                .paidAmount(paidAmount)
                .build();
    }
    PgApprovalResult pgApprovalResult(PaymentStatusType approvalStatus) {
        return pgApprovalResult("pgTransactionId", approvalStatus, new BigDecimal("10000"));
    }
    PgApprovalResult pgApprovalResult(String pgTransactionId) {
        return pgApprovalResult(pgTransactionId, APPROVED, new BigDecimal("10000"));
    }
    PgApprovalResult pgApprovalResult(BigDecimal paidAmount) {
        return pgApprovalResult("pgTransactionId", APPROVED, paidAmount);
    }

    /* ------------------
        Helper Method
       ------------------ */

    /* ----------------------
        결제 생성 Tests
       ---------------------- */

    @Test
    @DisplayName("결제 객체 생성 성공 - 주문이 CREATED이면 결제 생성")
    void createPayment_shouldCreatePayment_whenOrderStatusIsCreated() {
        // given
        // 등록된 주문 (주문 상태는 CREATED)
        Order order = order();
        // 요청 결제 방식
        PaymentMethodType requestMethod = CARD;
        // 회사와 결제 계약된 PG사
        PgProviderType pgProvider = pgProvider();

        // when
        Payment payment = Payment.createPayment(order, requestMethod, pgProvider);

        // then
        assertEquals(READY, payment.getPaymentStatus()); // 결제 생성 상태
        assertSame(order, payment.getOrder());           // 주문과 연관관계 검증
        assertEquals(requestMethod, payment.getPaymentMethod());
        assertEquals(pgProvider, payment.getPgProvider());
        assertTrue(payment.getPaymentCode().startsWith(order.getOrderNumber()));
    }

    @Test
    @DisplayName("결제 객체 생성 실패 - 주문상태가 CREATED가 아닌 경우 예외발생")
    void createPayment_shouldThrowException_whenOrderStatusNotCreated() {
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
    @DisplayName("READY -> IN_PROGRESS 성공 - PG 요청 성공 시 상태는 IN_PROGRESS로 변경")
    void requestPgPayment_shouldChangeToInProgress_whenPgRequestSucceeds() {
        // given
        // 결제 객체 생성 (결제상태 READY)
        Payment payment = readyPayment();
        // PG 요청 결과
        PgResult pgResult = pgResult("pgTransactionId");

        // when
        payment.requestPgPayment(pgResult);

        // then
        assertEquals(IN_PROGRESS, payment.getPaymentStatus());
        assertEquals("pgTransactionId", payment.getPgTransactionId());
    }

    @Test
    @DisplayName("READY -> IN_PROGRESS 실패 - PG 결제 응답 미존재 시 결제요청 실패")
    void requestPgPayment_shouldThrowException_whenPgResponseIsNull() {
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
    @DisplayName("READY -> IN_PROGRESS 실패 - PG 결제 응답의 트랜잭션 ID 미존재 시 결제요청 실패")
    void requestPgPayment_shouldThrowException_whenTransactionIdIsNull() {
        // 결제 객체 생성 (결제상태 READY)
        Payment payment = readyPayment();
        // PG 요청 결과
        PgResult invalidPgResult = pgResult(null); // 트랜잭션 ID 미존재

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                payment.requestPgPayment(invalidPgResult));
        assertEquals(PG_RESPONSE_TRANSACTION_ID_NOT_EXISTS, e.getErrorCode());
    }

    @Test
    @DisplayName("READY -> IN_PROGRESS 실패 - 기존 결제 상태가 READY가 아니면 결제요청 실패")
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
    @DisplayName("IN_PROGRESS -> APPROVED 성공 - PG 결제 승인 성공 시 상태는 APPROVED로 변경")
    void approve_shouldChangeToApproved_whenPgApprovalSucceeds() {
        // given
        // PG 결제 진행중인 결제 객체
        Payment payment = inProgressPayment("pgTransactionId");
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
        assertEquals(new BigDecimal("10000"), payment.getApprovedAmount());
        assertEquals(new BigDecimal("1000"), payment.getVatAmount());
    }

    @Test
    @DisplayName("IN_PROGRESS -> APPROVED 실패 - PG 응답에서 트랜잭션 ID 미존재 시 결제승인 실패")
    void approve_shouldThrowException_whenPgTransactionIdNotExists() {
        // given
        Payment payment = inProgressPayment();
        PgApprovalResult invalidPgApprovalResult = PgApprovalResult.builder()
                .pgTransactionId(null) // PG 트랜잭션 ID 미존재
                .build();

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                payment.approve(invalidPgApprovalResult));
        assertEquals(PG_RESPONSE_TRANSACTION_ID_NOT_EXISTS, e.getErrorCode());
    }

    @Test
    @DisplayName("IN_PROGRESS -> APPROVED 실패 - 트랜잭션 ID 불일치 시 결제승인 실패")
    void approve_shouldThrowException_whenPgTransactionIdMismatches() {
        // given
        Payment payment = inProgressPayment("pgTransactionId");
        PgApprovalResult invalidPgApprovalResult =
                pgApprovalResult("invalidPgTransactionId");

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                payment.approve(invalidPgApprovalResult));
        assertEquals(PG_TRANSACTION_ID_MISMATCH, e.getErrorCode());
    }

    @Test
    @DisplayName("IN_PROGRESS -> APPROVED 실패 - 기존 결제 상태가 IN_PROGRESS 상태가 아니면 결제승인 실패")
    void approve_shouldThrowException_whenOriginalPaymentStatusIsNotInProgress() {
        // given
        Payment invalidPayment = approvedPayment(); // 승인된 결제
        PgApprovalResult pgApprovalResult = pgApprovalResult(APPROVED);

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                invalidPayment.approve(pgApprovalResult));
        assertEquals(PAYMENT_STATUS_NOT_IN_PROGRESS, e.getErrorCode());
    }

    @Test
    @DisplayName("IN_PROGRESS -> APPROVED 실패 - 결제 금액 불일치 시 결제승인 실패")
    void approve_shouldThrowException_whenPaidAmountMismatch() {
        // given
        BigDecimal orderAmount = new BigDecimal("10000"); // 주문금액 10000
        BigDecimal paidAmount = new BigDecimal("50000");  // 결제금액 50000
        Order order = order(orderAmount);

        Payment payment = inProgressPayment(order);
        PgApprovalResult invalidPgApprovalResult = pgApprovalResult(paidAmount);

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                payment.approve(invalidPgApprovalResult));
        assertEquals(PAYMENT_AMOUNT_MISMATCH, e.getErrorCode());
    }

    @Test
    @DisplayName("IN_PROGRESS -> FAILED 성공 - PG 결제승인 실패 시 상태는 FAILED로 변경")
    void fail_shouldChangeToFailed_whenPgApprovalFails() {
        // given
        Payment payment = inProgressPayment();
        // PG 승인 결과
        PgApprovalResult pgApprovalResult = pgApprovalResult(FAILED);

        // when
        payment.fail(pgApprovalResult);

        // then
        assertEquals(FAILED, payment.getPaymentStatus()); // 결제 승인 실패
    }

    @Test
    @DisplayName("IN_PROGRESS -> FAILED 실패 - PG 응답에서 트랜잭션 ID 미존재 시 결제실패 불가")
    void fail_shouldThrowException_whenPgTransactionIdNotExists() {
        // given
        Payment payment = inProgressPayment();
        PgApprovalResult invalidPgApprovalResult = PgApprovalResult.builder()
                .pgTransactionId(null) // PG 트랜잭션 ID 미존재
                .build();

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                payment.fail(invalidPgApprovalResult));
        assertEquals(PG_RESPONSE_TRANSACTION_ID_NOT_EXISTS, e.getErrorCode());
    }

    @Test
    @DisplayName("IN_PROGRESS -> FAILED 실패 - 트랜잭션 ID 불일치 시 결제실패 불가")
    void fail_shouldThrowException_whenPgTransactionIdMismatches() {
        // given
        Payment payment = inProgressPayment("pgTransactionId");
        PgApprovalResult invalidPgApprovalResult =
                pgApprovalResult("invalidPgTransactionId");

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                payment.fail(invalidPgApprovalResult));
        assertEquals(PG_TRANSACTION_ID_MISMATCH, e.getErrorCode());
    }

    @Test
    @DisplayName("IN_PROGRESS -> FAILED 실패 - 기존 결제 상태가 IN_PROGRESS 상태가 아니면 결제실패 불가")
    void fail_shouldThrowException_whenOriginalPaymentStatusIsNotInProgress() {
        // given
        Payment invalidPayment = approvedPayment(); // 승인된 결제
        PgApprovalResult pgApprovalResult = pgApprovalResult(APPROVED);

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                invalidPayment.fail(pgApprovalResult));
        assertEquals(PAYMENT_STATUS_NOT_IN_PROGRESS, e.getErrorCode());
    }

    /* ----------------------
        결제상태 판단 Tests
       ---------------------- */

    @Test
    @DisplayName("결제 종결여부 판단 성공 - 결제상태가 결제승인이면 결제과정 종결")
    void isTerminal_shouldReturnTrue_whenPaymentStatusIsApproved() {
        // given
        Payment approvedPayment = approvedPayment(); // PG 결제승인된 결제
        // when
        boolean isTerminal = approvedPayment.isTerminal();
        // then
        assertTrue(isTerminal);
    }

    @Test
    @DisplayName("결제 종결여부 판단 성공 - 결제상태가 결제실패이면 결제과정 종결")
    void isTerminal_shouldReturnTrue_whenPaymentStatusIsFailed() {
        // given
        Payment failedPayment = failedPayment(); // PG 결제실패한 결제
        // when
        boolean isTerminal = failedPayment.isTerminal();
        // then
        assertTrue(isTerminal);
    }

    @Test
    @DisplayName("결제 종결여부 판단 성공 - 결제상태가 결제취소이면 결제과정 종결")
    void isTerminal_shouldReturnTrue_whenPaymentStatusIsCanceled() {
        // given
        Payment canceledPayment = canceledPayment(); // PG 결제취소된 결제
        // when
        boolean isTerminal = canceledPayment.isTerminal();
        // then
        assertTrue(isTerminal);
    }

    @Test
    @DisplayName("결제 종결여부 판단 실패 - 결제상태가 결제요청이면 결제과정 미종결")
    void isTerminal_shouldReturnFalse_whenPaymentStatusIsInProgress() {
        // given
        Payment inProgressPayment = inProgressPayment(); // PG 요청된 결제
        // when
        boolean isTerminal = inProgressPayment.isTerminal();
        // then
        assertFalse(isTerminal);
    }

    @Test
    @DisplayName("PG 요청 가능여부 판단 성공 - 결제상태가 준비이면 PG 요청 가능")
    void isPgRequestAvailable_shouldReturnTrue_whenPaymentStatusIsReady() {
        // given
        Payment readyPayment = readyPayment();
        // when
        boolean isPgRequestAvailable = readyPayment.isPgRequestAvailable();
        // then
        assertTrue(isPgRequestAvailable);
    }

    @Test
    @DisplayName("PG 요청 가능여부 판단 실패 - 결제상태가 준비가 아니면 PG 요청 불가능")
    void isPgRequestAvailable_shouldReturnFalse_whenPaymentStatusIsNotReady() {
        // given
        Payment inProgressPayment = inProgressPayment();
        // when
        boolean isPgRequestAvailable = inProgressPayment.isPgRequestAvailable();
        // then
        assertFalse(isPgRequestAvailable);
    }

    @Test
    @DisplayName("PG 승인요청 가능여부 판단 성공 - 결제상태가 승인대기중이면 승인요청 가능")
    void isPgApproveRequestAvailable_shouldReturnTrue_whenPaymentStatusIsInProgress() {
        // given
        Payment inProgressPayment = inProgressPayment();
        // when
        boolean isApproveRequestAvailable =
                inProgressPayment.isPgApproveRequestAvailable();
        // then
        assertTrue(isApproveRequestAvailable);
    }

    @Test
    @DisplayName("PG 승인요청 가능여부 판단 실패 - 결제상태가 결제승인이면 승인요청 불가 판단")
    void isPgApproveRequestAvailable_shouldReturnFalse_whenPaymentStatusIsApproved() {
        // given
        Payment approvedPayment = approvedPayment();
        // when
        boolean isApproveRequestAvailable =
                approvedPayment.isPgApproveRequestAvailable();
        // then
        assertFalse(isApproveRequestAvailable);
    }

    //  TODO: 승인여부 판단 테스트 작성
}