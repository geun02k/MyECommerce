package com.myecommerce.MyECommerce.service.payment;

import com.myecommerce.MyECommerce.dto.payment.PgResult;
import com.myecommerce.MyECommerce.dto.payment.RequestPaymentDto;
import com.myecommerce.MyECommerce.dto.payment.ResponsePaymentDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import com.myecommerce.MyECommerce.repository.payment.PaymentRepository;
import com.myecommerce.MyECommerce.type.PaymentMethodType;
import com.myecommerce.MyECommerce.type.PaymentStatusType;
import com.myecommerce.MyECommerce.type.PgProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentPolicy paymentPolicy;

    private final PgClient pgClient;

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    /** 결제 생성 - 결제 시작 **/
    public ResponsePaymentDto startPayment(RequestPaymentDto requestPaymentDto,
                                           Member member) {
        // 1. 정책검증 / 결제 Entity 반환
        Payment payment = createPayment(requestPaymentDto, member); // 결제상태 READY

        PgResult pgResult;
        try {
            // 2-1. PG 결제대행사에 결제 요청
            pgResult = pgClient.requestPayment(payment);
            // 2-2. 결제 도메인에 PG 요청 결과 반영 (결제번호, 결제상태 셋팅)
            updatePaymentToInProgress(payment, pgResult);

        } catch (Exception e) {
            // READY 유지 (의도적으로 아무 것도 안 함)
            // TODO: 예외처리 방법 변경 고민 필요
            throw e;
        }

        return ResponsePaymentDto.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .paymentStatus(payment.getPaymentStatus())
                .pgResult(pgResult)
                .build();
    }

    // 정책검증 후 결제 Entity 생성
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
                .orElseThrow();

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
            payment = Payment.createPayment(order, paymentMethod, pgProvider);
            paymentRepository.save(payment);
        }

        return payment;
    }

    // 승인 요청 가능한 결제 반환
    private Payment filterApproveRequestAvailablePayment(List<Payment> paymentList,
                                                  PaymentMethodType requestPaymentMethod) {
        for (Payment payment : paymentList) {
            PaymentStatusType paymentStatus = payment.getPaymentStatus();
            PaymentMethodType paymentMethod = payment.getPaymentMethod();
            PgProviderType pgProvider = payment.getPgProvider();

            // PG 승인 요청 가능한 결제 상태인가?
            boolean isStatusOfApproveAvailable =
                    !(paymentStatus == APPROVED || paymentStatus == CANCELED);
            // 요청 결제방식과 동일한가?
            boolean isRequestMethod = paymentMethod == requestPaymentMethod;
            // 회사가 계약한 결제대행사와 동일한가?
            boolean isPgClient = pgProvider == pgClient.getProvider();

            if(isStatusOfApproveAvailable && isRequestMethod && isPgClient) {
                return payment;
            }
        }

        return null;
    }

    // 결제 도메인에 PG 요청 결과 반영 (결제번호, 결제상태 셋팅)
    @Transactional
    protected void updatePaymentToInProgress(Payment payment, PgResult pgResult) {
        payment.requestPgPayment(pgResult); // 결제상태 IN_PROGRESS
    }

}
