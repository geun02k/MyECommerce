package com.myecommerce.MyECommerce.service.order;

import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.exception.PaymentException;
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import com.myecommerce.MyECommerce.service.payment.PaymentTxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.ORDER_STATUS_NOT_CREATED;
import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.APPROVED;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
@RequiredArgsConstructor
public class OrderTxService {

    private final PaymentTxService paymentTxService;

    private final OrderRepository orderRepository;

    // 주문상태 결제완료로 변경
    @Transactional(propagation = REQUIRES_NEW)
    public void updatePaidOrderStatus(Long orderId, Long paymentId) {
        // 1. 결제 조회
        Payment payment = paymentTxService.findPaymentByIdWithOrder(paymentId);
        // 2. 결제승인 검증
        if(payment.getPaymentStatus() != APPROVED) {
            return;
        }
        // TODO: payment 조회 시 fetch join으로 order 가져오기 때문에 조회하지 않고 if 사용 고려하기
        // TODO: 주문 상태 검증을 paid()에서 수행하므로 중복 제거 고려하기
        // 3. 결제완료되지 않은 주문 조회
        Order order = orderRepository.findByIdAndOrderStatus(orderId, CREATED)
                .orElseThrow(() -> new PaymentException(ORDER_STATUS_NOT_CREATED));
        // 4. 주문 결제완료 처리
        order.paid(payment);
    }

}
