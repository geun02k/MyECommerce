package com.myecommerce.MyECommerce.service.payment;

import com.myecommerce.MyECommerce.dto.payment.*;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.exception.PaymentException;
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import com.myecommerce.MyECommerce.repository.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.*;
import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PgClient pgClient;

    private final PaymentTxService paymentTxService;

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    /** 결제 생성 - 결제 시작 **/
    public ResponsePaymentDto startPayment(RequestPaymentDto requestPaymentDto,
                                           Member member) {
        // 1. 정책검증 / 결제 Entity 반환
        Payment payment = paymentTxService.createPayment(requestPaymentDto, member); // 결제상태 READY

        // 2-1. PG 결제대행사에 결제 요청
        PgApiResponse<PgResult> pgResponse = pgClient.requestPayment(payment);
        if (pgResponse.isSuccess()) {
            // 2-2. 결제 도메인에 PG 요청 결과 반영 (결제번호, 결제상태 셋팅)
            // 결제상태 READY -> IN_PROGRESS로 변경
            payment = paymentTxService.updatePaymentToInProgress(
                    payment.getId(), pgResponse.getData());
        }

        return ResponsePaymentDto.from(payment, pgResponse);
    }

    /** 결제 생성 웹훅 처리 - 결제 상태 변경해 결제 종료 **/
    @Transactional
    public void handlePgWebHook(PgApprovalResult pgApprovalResult) {
        // 1. transactionId로 승인할 결제 조회
        Payment payment =
                findPaymentByPgTransactionId(pgApprovalResult.getPgTransactionId());

        // 2. 종결된 결제의 중복 웹훅 무시 (멱등성 검증: 동일 요청을 여러번 보내도 결과는 동일해야 함)
        if (payment.isTerminal()) { // 결제 상태 종결 여부 반환
            return; // 예외가 안 터지면 Spring은 200 OK를 보내 pg 승인결과 반영 재요청 받지 않게 종료.
        }

        // 결제 승인, 실패 처리
        updatePaymentApprove(payment, pgApprovalResult);

        // 주문상태 변경 (웹 훅 결과 반영)
        updatePaidOrderStatus(payment);
    }

    // transactionId로 결제(Payment) 객체 조회
    private Payment findPaymentByPgTransactionId(String pgTransactionId) {
        return paymentRepository.findByPgTransactionId(pgTransactionId)
                .orElseThrow(() ->
                        new PaymentException(PG_TRANSACTION_ID_NOT_EXISTS));
    }

    // 결제 승인 및 실패 처리
    private void updatePaymentApprove(Payment payment,
                                      PgApprovalResult pgApprovalResult) {
        if (pgApprovalResult.getApprovalStatus() == APPROVED) {
            payment.approve(pgApprovalResult); // 결제 완료
        } else {
            payment.fail(pgApprovalResult); // 재결제 시도가능
        }
    }

    // 주문상태 변경 (웹 훅 결과 반영)
    private void updatePaidOrderStatus(Payment payment) {
        Long orderId = payment.getOrder().getId();
        // 결제완료되지 않은 주문 조회
        Order order = orderRepository.findByIdAndOrderStatus(orderId, CREATED)
                .orElseThrow(() -> new PaymentException(ORDER_STATUS_NOT_CREATED));

        // 주문 결제완료 처리
        order.paid(payment);
    }

}
