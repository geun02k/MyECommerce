package com.myecommerce.MyECommerce.service.payment;

import com.myecommerce.MyECommerce.dto.payment.*;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.exception.PaymentException;
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import com.myecommerce.MyECommerce.repository.payment.PaymentRepository;
import com.myecommerce.MyECommerce.type.PaymentMethodType;
import com.myecommerce.MyECommerce.type.PaymentStatusType;
import com.myecommerce.MyECommerce.type.PgProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.*;
import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.APPROVED;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.FAILED;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Service
@RequiredArgsConstructor
public class PaymentTxService {

    private final PaymentPolicy paymentPolicy;

    private final PgClient pgClient;

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    /** 정책검증 후 결제 Entity 생성 **/
    @Transactional(propagation = REQUIRES_NEW)
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

        // PG 요청 가능한 결제 단건 추출
        Payment payment = filterPgRequestAvailablePayment(
                paymentList, paymentMethod);

        if (payment == null) {
            // 결제 생성
            Payment newPayment = Payment.createPayment(order, paymentMethod, pgProvider);
            payment = paymentRepository.save(newPayment);
        }

        return payment;
    }

    /** 결제 도메인에 PG 요청 결과 반영 (결제번호, 결제상태 셋팅) **/
    @Transactional(propagation = REQUIRES_NEW)
    protected Payment updatePaymentToInProgress(Long paymentId, PgResult pgResult) {
        // 결제 다건 조회 (비관적 락)
        Payment targetPayment = paymentRepository.findLockedById(paymentId)
                .orElseThrow();
        // 결제상태 IN_PROGRESS로 변경
        targetPayment.requestPgPayment(pgResult);
        // 상태변경한 Payment 반환
        return targetPayment;
    }

    // 결제 승인 및 실패 처리
    @Transactional(propagation = REQUIRES_NEW)
    int updatePgApprovalResult(Long paymentId, PgApprovalResult pgApprovalResult) {
        // transactionId로 승인할 결제 조회
        Payment payment = findPaymentByIdWithOrder(paymentId);
        PaymentStatusType approvalStatus = pgApprovalResult.getApprovalStatus();

        // 조건부로 결제상태 우선변경 (동시성 제어, JPA 더티체킹 전 수행을 위해 우선 실행)
        int updateCnt = 0;
        if (approvalStatus == APPROVED || approvalStatus == FAILED) {
            updateCnt = paymentRepository.approveIfInProgress(
                    payment.getId(), approvalStatus);
        }

        // 추가정보 변경
        if (updateCnt > 0) {
            if (approvalStatus == APPROVED) {
                payment.approve(pgApprovalResult); // 결제 완료

            } else if (approvalStatus == FAILED) {
                payment.fail(pgApprovalResult);    // 재결제 시도가능
            }
        }

        return updateCnt;
    }

    /** 결제 조회 **/
    public Payment findPaymentByIdWithOrder(Long paymentId) {
        // payment.approve()에서 order를 조회하기 때문에 fetch join 사용해 한번에 조회
        return paymentRepository.findByIdWithOrder(paymentId)
                .orElseThrow(() -> new PaymentException(PAYMENT_NOT_FOUND));
    }

    // PG 요청 가능한 결제 반환
    private Payment filterPgRequestAvailablePayment(
            List<Payment> paymentList, PaymentMethodType requestPaymentMethod) {

        for (Payment payment : paymentList) {
            boolean isPgRequestAvailable =
                    paymentPolicy.isPaymentAvailablePgRequestAboutRequest(
                            payment, requestPaymentMethod, pgClient.getProvider());

            if(isPgRequestAvailable) {
                return payment;
            }
        }

        return null;
    }

}
