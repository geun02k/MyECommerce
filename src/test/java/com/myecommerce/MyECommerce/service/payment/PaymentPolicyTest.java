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
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;

class PaymentPolicyTest {

    PaymentPolicy paymentPolicy = new PaymentPolicy();

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 고객권한 사용자 */
    Member customer() {
        return Member.builder()
                .id(1L)
                .userId("customer")
                .roles(List.of(MemberAuthority.builder()
                        .authority(CUSTOMER)
                        .build()))
                .build();
    }

    /** 등록된 상품 옵션 */
    ProductOption registeredOption() {
        Product registeredProduct = Product.builder()
                .saleStatus(ON_SALE)
                .build();

        return ProductOption.builder()
                .quantity(100)
                .price(new BigDecimal("10000"))
                .product(registeredProduct)
                .build();
    }

    /** 주문 생성 */
    Order order() {
        ProductOption registeredOption = registeredOption();
        OrderItem orderItem =
                OrderItem.createOrderItem(registeredOption, 1);
        Member member = customer();
        return Order.createOrder(List.of(orderItem), member);
    }

    Order order(Member member) {
        ProductOption registeredOption = registeredOption();
        OrderItem orderItem =
                OrderItem.createOrderItem(registeredOption, 1);

        return Order.createOrder(List.of(orderItem), member);
    }

    /** PG 결제대행사 반환 */
    PgProviderType pgProvider() {
        return PgProviderType.MOCK_PG;
    }

    /** PG 승인실패된 결제 생성 - 결제상태 FAILED */
    Payment failedPayment() {
        // 등록된 주문 (주문 상태는 CREATED)
        Order order = order();
        // 요청 결제 방식
        PaymentMethodType requestMethod = CARD;
        // 회사와 결제 계약된 PG사
        PgProviderType pgProvider = pgProvider();

        // 결제 생성 (READY)
        Payment payment = Payment.createPayment(order, requestMethod, pgProvider);
        // PG 결제 요청 (READY -> IN_PROGRESS)
        PgResult pgResult = pgResult();
        payment.requestPgPayment(pgResult);
        // PG 결제 승인
        PgApprovalResult pgApprovalResult = pgApprovalResult(FAILED);
        payment.fail(pgApprovalResult);

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
        // PG 결제 요청 (READY -> IN_PROGRESS)
        PgResult pgResult = pgResult();
        payment.requestPgPayment(pgResult);
        // PG 결제 승인
        PgApprovalResult pgApprovalResult = pgApprovalResult(APPROVED);
        payment.approve(pgApprovalResult);

        return payment;
    }

    /** PG 승인취소된 결제 생성 - 결제상태 CANCELED */
    Payment canceledPayment() {
        // 등록된 주문 (주문 상태는 CREATED)
        Order order = order();
        // 요청 결제 방식
        PaymentMethodType requestMethod = CARD;
        // 회사와 결제 계약된 PG사
        PgProviderType pgProvider = pgProvider();

        // 결제 생성 (READY)
        Payment payment = Payment.createPayment(order, requestMethod, pgProvider);
        // PG 결제 요청 (READY -> IN_PROGRESS)
        PgResult pgResult = pgResult();
        payment.requestPgPayment(pgResult);
        // PG 결제 승인취소 (현재 로직에서 미지원으로 강제변경)
        ReflectionTestUtils.setField(payment, "paymentStatus", CANCELED);

        return payment;
    }

    /** PG 요청 결과 생성 */
    PgResult pgResult() {
        return PgResult.builder()
                .pgTransactionId("pgTransactionId")
                .build();
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
        Member member = Member.builder()
                .userId("customer")
                .roles(List.of(MemberAuthority.builder()
                        .authority(CUSTOMER)
                        .build()))
                .build();

        // when
        // then
        assertDoesNotThrow(() -> paymentPolicy.preValidateCreate(member));
    }

    @Test
    @DisplayName("결제생성 사전 정책 실패 - 비회원 결제 차단")
    void validateCreate_shouldThrowException_whenAccessNotMember() {
        // given
        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                paymentPolicy.preValidateCreate(null)); // 회원 비로그인
        assertEquals(MEMBER_NOT_LOGGED_IN, e.getErrorCode());
    }

    @Test
    @DisplayName("결제생성 사전 정책 실패 - 고객 외 권한자 결제 차단")
    void preValidateCreate_shouldThrowException_whenHaveNotCustomerRole() {
        // given
        // 요청 고객
        Member invalidMember = Member.builder()
                .roles(List.of())
                .build(); // 고객 권한 없음

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
        Member member = Member.builder()
                .id(1L)
                .build();
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
        Member requestMember = Member.builder()
                .id(1L)
                .build();
        // 주문한 회원
        Member orderMember = Member.builder()
                .id(5L)
                .build();
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
        Payment payment = failedPayment();

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
        Payment invalidPayment = approvedPayment();

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
        Payment invalidPayment = canceledPayment();

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                paymentPolicy.validateCreate(List.of(invalidPayment), order, member));
        assertEquals(PAYMENT_ALREADY_COMPLETED, e.getErrorCode());
    }

}