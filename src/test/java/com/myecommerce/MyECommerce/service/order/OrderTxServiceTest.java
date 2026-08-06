package com.myecommerce.MyECommerce.service.order;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderTxServiceTest {

    /* ----------------------------------
        주문 결제처리 정상 시나리오 Tests
       ---------------------------------- */

    // updatePaidOrderStatus() 검증
    // paymentTxService.findPaymentById()가 APPROVED 상태인 엔티티를 반환하게 stub
    // TODO: 존재하지 않는 OrderId 전달 시 예외 발생 검증
    // TODO: 존재하지 않는 PaymentId 전달 시 예외 발생 검증
    // TODO: 결제와 주문 금액 차이 발생 시 예외 발생
    // TODO: 유효성 검증 - 결제승인(APPROVED)되지 않은 경우 주문상태 미변경
    // TODO: 결제승인(APPROVED)된 경우 주문 결제처리

}