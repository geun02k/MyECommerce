package com.myecommerce.MyECommerce.service.payment;

import com.myecommerce.MyECommerce.dto.payment.*;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.exception.PaymentException;
import com.myecommerce.MyECommerce.repository.payment.PaymentRepository;
import com.myecommerce.MyECommerce.service.order.OrderTxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.*;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PgClient pgClient;

    private final OrderTxService orderTxService;
    private final PaymentTxService paymentTxService;

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
            payment = paymentTxService.updatePaymentToInProgress(
                    payment.getId(), pgResponse.getData()); // 결제상태 READY -> IN_PROGRESS로 변경
        }

        return ResponsePaymentDto.from(payment, pgResponse);
    }

    /** 결제 생성 웹훅 처리 - 결제 상태 변경해 결제 종료 **/
    public void handlePgWebHook(PgApprovalResult pgApprovalResult) {
        // 1. transactionId로 승인할 결제 조회
        Payment payment =
                findPaymentByPgTransactionId(pgApprovalResult.getPgTransactionId());

        Long paymentId = payment.getId();
        Long orderId = payment.getOrder().getId();

        // 2. 종결된 결제의 중복 웹훅 무시 (멱등성 검증: 동일 요청을 여러번 보내도 결과는 동일하도록 함.)
        if (payment.isTerminal()) {
            return; // 예외가 안 터지면 Spring은 200 OK를 보내 pg 승인결과 반영 재요청 받지 않게 종료.
        }

        // 3. 결제 승인 결과 반영 (상태 기반 업데이트로 동시성 제어)
        int updateCnt = paymentTxService.updatePgApprovalResult(paymentId, pgApprovalResult);
        if (updateCnt <= 0) {
            return; // Spring은 200 OK를 보내 pg 승인결과 반영 재요청 받지 않게 종료.
        }

        // 4. 결제승인 시 주문 결제완료 처리
        try {
            orderTxService.updatePaidOrderStatus(orderId, paymentId);

        } catch (Exception e) {
            // TODO: 별도 보상 이벤트/스케줄러 작업 필요
            // Order 처리 실패 시 Payment 승인 결과를 롤백하지 않고 재처리를 위한 로그 기록
            log.error("Payment 승인 완료 후 Order 결제완료 상태 업데이트 실패 - orderId: {}",
                    payment.getOrder().getId(), e);
        }
    }

    // transactionId로 결제(Payment) 객체 조회
    private Payment findPaymentByPgTransactionId(String pgTransactionId) {
        return paymentRepository.findByPgTransactionIdWithOrder(pgTransactionId)
                .orElseThrow(() ->
                        new PaymentException(PG_TRANSACTION_ID_NOT_EXISTS));
    }

}
