package com.myecommerce.MyECommerce.service.payment;

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
import com.myecommerce.MyECommerce.type.MemberAuthorityType;
import com.myecommerce.MyECommerce.type.PaymentMethodType;
import com.myecommerce.MyECommerce.type.PaymentStatusType;
import com.myecommerce.MyECommerce.type.PgProviderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.*;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.PaymentMethodType.CARD;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.*;
import static com.myecommerce.MyECommerce.type.PgProviderType.MOCK_PG;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;

class PaymentPolicyTest {

    PaymentPolicy paymentPolicy = new PaymentPolicy();

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 회원 */
    Member member(Long id, MemberAuthorityType memberAuthorityType) {
        return Member.builder()
                .id(id)
                .roles(List.of(MemberAuthority.builder()
                        .authority(memberAuthorityType)
                        .build()))
                .build();
    }
    Member member(MemberAuthorityType memberAuthorityType) {
        return member(1L, memberAuthorityType);
    }

    /** 권한없는 회원 */
    Member memberOfEmptyRole() {
        return Member.builder()
                .roles(List.of())
                .build();
    }

    /** 고객권한 회원 */
    Member customer(Long id) {
        return member(id, CUSTOMER);
    }
    Member customer() {
        return customer(1L);
    }

    /** 등록된 상품 옵션 */
    ProductOption registeredOption() {
        Product product = Product.builder()
                .saleStatus(ON_SALE)
                .build();

        return ProductOption.builder()
                .quantity(100)
                .price(new BigDecimal("10000"))
                .product(product)
                .build();
    }

    /** 주문 생성 */
    Order order(Member member) {
        OrderItem orderItem =
                OrderItem.createOrderItem(registeredOption(), 1);
        return Order.createOrder(List.of(orderItem), member);
    }

    /** PG 요청된 결제 객체 생성 */
    Payment pgRequestedPayment(Order order) {
        // 결제 생성 (결제 상태는 READY)
        Payment payment = Payment.createPayment(order, CARD, MOCK_PG);
        // PG 결제요청
        PgResult pgRequestResult = PgResult.builder()
                .pgTransactionId("pgTransactionId")
                .build();
        payment.requestPgPayment(pgRequestResult); // 결제상태 = IN_PROGRESS

        return payment;
    }

    /** PG 승인실패된 결제 생성 - 결제상태 FAILED */
    Payment failedPayment(Order order) {
        // pg 요청된 결제 생성 (IN_PROGRESS)
        Payment payment = pgRequestedPayment(order);
        // PG 결제 승인 실패
        PgApprovalResult pgApprovalResult = pgApprovalResult(FAILED);
        payment.fail(pgApprovalResult);

        return payment;
    }

    /** PG 승인된 결제 생성 - 결제상태 APPROVED */
    Payment approvedPayment(Order order) {
        // pg 요청된 결제 생성 (IN_PROGRESS)
        Payment payment = pgRequestedPayment(order);
        // PG 결제 승인
        PgApprovalResult pgApprovalResult = pgApprovalResult(APPROVED);
        payment.approve(pgApprovalResult);

        return payment;
    }

    /** PG 승인취소된 결제 생성 - 결제상태 CANCELED */
    Payment canceledPayment(Order order) {
        // pg 요청된 결제 생성 (IN_PROGRESS)
        Payment payment = pgRequestedPayment(order);
        // PG 결제 승인취소 (현재 로직에서 미지원으로 강제변경)
        ReflectionTestUtils.setField(payment, "paymentStatus", CANCELED);

        return payment;
    }

    /** PG 승인 결과 생성 */
    PgApprovalResult pgApprovalResult(PaymentStatusType approvalStatus) {
        return PgApprovalResult.builder()
                .pgTransactionId("pgTransactionId")
                .approvalStatus(approvalStatus)
                .paidAmount(new BigDecimal("10000"))
                .build();
    }

    /* ------------------
        Helper Method
       ------------------ */

    /* ----------------------
        결제 사전 정책 Tests
       ---------------------- */

    @Test
    @DisplayName("결제생성 사전 정책 통과 - 유효한 요청 시 정책 통과")
    void preValidateCreate_shouldPass_whenValidMember() {
        // given
        Member member = member(CUSTOMER); // 고객 권한 회원

        // when
        // then
        assertDoesNotThrow(() -> paymentPolicy.preValidateCreate(member));
    }

    @Test
    @DisplayName("결제생성 사전 정책 실패 - 비회원 결제 차단")
    void validateCreate_shouldThrowException_whenAccessNotMember() {
        // given
        Member notMember = null; // 회원 정보 없음

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                paymentPolicy.preValidateCreate(notMember));
        assertEquals(MEMBER_NOT_LOGGED_IN, e.getErrorCode());
    }

    @Test
    @DisplayName("결제생성 사전 정책 실패 - 고객 외 권한자 결제 차단")
    void preValidateCreate_shouldThrowException_whenHaveNotCustomerRole() {
        // given
        // 요청 고객
        Member invalidMember = memberOfEmptyRole(); // 고객 권한 없음

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                paymentPolicy.preValidateCreate(invalidMember));
        assertEquals(PAYMENT_CUSTOMER_ONLY, e.getErrorCode());
    }

    /* ----------------------
        결제 정책 Tests
       ---------------------- */

    @Test
    @DisplayName("결제생성 정책 통과 - 유효한 요청 시 정책 통과")
    void validateCreate_shouldPass_whenValidAll() {
        // given
        // 주문 결제 요청 회원
        Member member = customer();
        // 주문
        Order order = order(member);
        // 주문에 대한 결제
        Payment payment = Payment.createPayment(
                order, PaymentMethodType.CARD, PgProviderType.MOCK_PG);

        // when
        // then
        assertDoesNotThrow(() ->
                paymentPolicy.validateCreate(List.of(payment), order, member));
    }

    @Test
    @DisplayName("결제생성 정책 실패 - 본인 주문이 아니면 결제 차단")
    void validateCreate_shouldThrowException_whenAccessNotOrderOwner() {
        // given
        // 결제 요청 회원
        Member requestMember = customer(1L);
        // 주문한 회원
        Member orderMember = customer(5L);
        // 주문
        Order order = order(orderMember);
        // 주문에 대한 결제
        Payment payment = Payment.createPayment(
                order, PaymentMethodType.CARD, PgProviderType.MOCK_PG);

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                paymentPolicy.validateCreate(List.of(payment), order, requestMember));
        assertEquals(PAYMENT_ACCESS_AVAILABLE_ONLY_BUYER, e.getErrorCode());
    }

    @Test
    @DisplayName("결제생성 정책 통과 - FAILED 결제 존재 시 신규 결제생성 가능")
    void validateCreate_shouldPass_whenPaymentStatusIsFailed() {
        // given
        // 주문 결제 요청 회원
        Member member = customer();
        // 주문
        Order order = order(member);
        // 주문에 대한 결제 (PG 결제실패)
        Payment payment = failedPayment(order);

        // when
        // then
        assertDoesNotThrow(() ->
                paymentPolicy.validateCreate(List.of(payment), order, member));
    }

    @Test
    @DisplayName("결제생성 정책 실패 - APPROVED 결제 존재 시 신규 결제생성 차단")
    void validateCreate_shouldThrowException_whenPaymentStatusIsApproved() {
        // given
        // 주문 결제 요청 회원
        Member member = customer();
        // 주문
        Order order = order(member);
        // 주문에 대한 결제 (PG 결제승인)
        Payment invalidPayment = approvedPayment(order);

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                paymentPolicy.validateCreate(List.of(invalidPayment), order, member));
        assertEquals(PAYMENT_ALREADY_COMPLETED, e.getErrorCode());
    }

    @Test
    @DisplayName("결제생성 정책 실패 - CANCELED 결제 존재 시 신규 결제생성 차단")
    void validateCreate_shouldThrowException_whenPaymentStatusIsCanceled() {
        // given
        // 주문 결제 요청 회원
        Member member = customer();
        // 주문
        Order order = order(member);
        // 주문에 대한 결제 (PG 결제승인)
        Payment invalidPayment = canceledPayment(order);

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                paymentPolicy.validateCreate(List.of(invalidPayment), order, member));
        assertEquals(PAYMENT_ALREADY_COMPLETED, e.getErrorCode());
    }

    // TODO: 결제생성 정책 실패 - IN_PROGRESS 결제 존재 시 신규 결제생성 차단 (validateCreate())

    /* ------------------------------------
        PG 요청 가능한 유효한 결제 판단 Tests
       ------------------------------------ */

    // TODO: 결제생성 정책 실패 - 결제 수단, PG사, 결제 상태에 따른 boolean 반환 (isPaymentAvailablePgRequestAboutRequest())

}