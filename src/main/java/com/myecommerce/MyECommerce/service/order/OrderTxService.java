package com.myecommerce.MyECommerce.service.order;

import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.exception.PaymentException;
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import com.myecommerce.MyECommerce.service.payment.PaymentTxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.*;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.APPROVED;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
@RequiredArgsConstructor
public class OrderTxService {

    private final PaymentTxService paymentTxService;

    // 주문상태 결제완료로 변경
    @Transactional(propagation = REQUIRES_NEW)
    public void updatePaidOrderStatus(Long orderId, Long paymentId) {
        // 1. 결제 조회
        Payment payment = paymentTxService.findPaymentByIdWithOrder(paymentId);
        // 2. 결제승인 검증
        if(payment.getPaymentStatus() != APPROVED) {
            return;
        }
        // 3. 결제완료되지 않은 주문 조회
        Order order = payment.getOrder();
        if(order == null || !Objects.equals(order.getId(), orderId)) {
            throw new PaymentException(PAYMENT_ORDER_MISMATCH_INTERNAL_ERROR);
        }
        // 4. 주문 결제완료 처리
        order.paid(payment);
    }

}
