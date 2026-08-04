package com.myecommerce.MyECommerce.service.order;

import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.exception.PaymentException;
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.ORDER_STATUS_NOT_CREATED;
import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
@RequiredArgsConstructor
public class OrderTxService {

    private final OrderRepository orderRepository;

    // 주문상태 결제완료로 변경
    @Transactional(propagation = REQUIRES_NEW)
    public void updatePaidOrderStatus(Payment payment) {
        Long orderId = payment.getOrder().getId();
        // 결제완료되지 않은 주문 조회
        Order order = orderRepository.findByIdAndOrderStatus(orderId, CREATED)
                .orElseThrow(() -> new PaymentException(ORDER_STATUS_NOT_CREATED));
        // 주문 결제완료 처리
        order.paid(payment);
    }

}
