package com.myecommerce.MyECommerce.service.payment;

import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.exception.PaymentException;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.*;
import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.MEMBER_NOT_LOGGED_IN;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.*;

@Component
public class PaymentPolicy {

    /** 결제생성 사전 정책 **/
    public void preValidateCreate(Member member) {
        // 1, 서비스 정책 검증 (받아도 되는 요청인가?)
        // 접근 제한 정책
        validatePaymentAccessPolicy(member);

        // 2. 도메인 규칙 검증 (서비스 로직 수행 가능한가?)
    }

    /** 결제생성 정책 **/
    public void validateCreate(List<Payment> paymentList,
                               Order order, Member member) {
        // 3. DB 조회 필요 도메인 규칙 검증
        validatePaymentOnlyOwnAccess(order, member);
        validateApproveRequestAvailablePayment(paymentList);
    }

    // 결제 접근 권한 제한 정책
    private void validatePaymentAccessPolicy(Member member) {
        // 1. 비회원 제한
        if(member == null) {
            throw new PaymentException(MEMBER_NOT_LOGGED_IN);
        }

        // 2. 고객 외 권한 접근 제한
        boolean hasCustomerRole = false;

        hasCustomerRole = member.getRoles().stream()
                .map(MemberAuthority::getAuthority)
                .anyMatch(authority ->
                        authority.equals(CUSTOMER));

        if (!hasCustomerRole) {
            throw new PaymentException(PAYMENT_CUSTOMER_ONLY);
        }
    }

    // 본인의 주문만 결제가능
    private void validatePaymentOnlyOwnAccess(Order order, Member member) {
        if(!order.getBuyer().getId().equals(member.getId())) {
            throw new PaymentException(PAYMENT_ACCESS_AVAILABLE_ONLY_BUYER);
        }
    }

    // 결제 승인된 경우 결제 생성 불가 (승인, 취소 등 PG 승인된 경우)
    private void validateApproveRequestAvailablePayment(List<Payment> paymentList) {
        boolean hasTerminalPayment = paymentList.stream()
                .anyMatch(payment ->
                        payment.getPaymentStatus() == APPROVED ||
                        payment.getPaymentStatus() == CANCELED);

        if (hasTerminalPayment) {
            throw new PaymentException(PAYMENT_ALREADY_COMPLETED);
        }
    }

}
