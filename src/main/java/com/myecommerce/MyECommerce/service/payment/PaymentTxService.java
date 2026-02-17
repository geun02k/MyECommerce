package com.myecommerce.MyECommerce.service.payment;

import com.myecommerce.MyECommerce.dto.payment.*;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.exception.PaymentException;
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import com.myecommerce.MyECommerce.repository.payment.PaymentRepository;
import com.myecommerce.MyECommerce.type.PaymentMethodType;
import com.myecommerce.MyECommerce.type.PgProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.*;
import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;

@Service
@RequiredArgsConstructor
public class PaymentTxService {

    private final PaymentPolicy paymentPolicy;

    private final PgClient pgClient;

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    /** 정책검증 후 결제 Entity 생성 **/
    @Transactional
    protected Payment createPayment(RequestPaymentDto requestPaymentDto,
                                    Member member) {
        // 사전 정책 검증
        paymentPolicy.preValidateCreate(member);

        PaymentMethodType paymentMethod = requestPaymentDto.getPaymentMethod();
        Long orderId = requestPaymentDto.getOrderId();
        PgProviderType pgProvider = pgClient.getProvider();

        // 주문 조회 (비관적 락)
        Order order = orderRepository
                .findLockedByIdAndOrderStatus(orderId, CREATED)
                .orElseThrow(() -> new PaymentException(PAYMENT_ORDER_NOT_EXISTS));

        // 결제 다건 조회 (비관적 락)
        List<Payment> paymentList =
                paymentRepository.findLockedAllByOrderId(orderId);

        // 조회 후 정책 검증
        paymentPolicy.validateCreate(paymentList, order, member);

        // PG 승인 요청 가능한 결제 단건 추출
        Payment payment = filterApproveRequestAvailablePayment(
                paymentList, paymentMethod);

        if (payment == null) {
            // 결제 생성
            Payment newPayment = Payment.createPayment(order, paymentMethod, pgProvider);
            payment = paymentRepository.save(newPayment);
        }

        return payment;
    }

    /** 결제 도메인에 PG 요청 결과 반영 (결제번호, 결제상태 셋팅) **/
    @Transactional
    protected void updatePaymentToInProgress(Payment payment, PgResult pgResult) {
        payment.requestPgPayment(pgResult); // 결제상태 IN_PROGRESS
    }

    // 승인 요청 가능한 결제 반환
    private Payment filterApproveRequestAvailablePayment(
            List<Payment> paymentList, PaymentMethodType requestPaymentMethod) {

        for (Payment payment : paymentList) {
            boolean isApproveRequestAvailable =
                    paymentPolicy.isPaymentAvailablePgRequestAboutRequest(
                            payment, requestPaymentMethod, pgClient.getProvider());

            if(isApproveRequestAvailable) {
                return payment;
            }
        }

        return null;
    }

}
