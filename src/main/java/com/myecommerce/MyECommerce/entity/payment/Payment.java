package com.myecommerce.MyECommerce.entity.payment;

import com.myecommerce.MyECommerce.dto.payment.PgApprovalResult;
import com.myecommerce.MyECommerce.dto.payment.PgResult;
import com.myecommerce.MyECommerce.entity.BaseEntity;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.exception.PaymentException;
import com.myecommerce.MyECommerce.type.PaymentMethodType;
import com.myecommerce.MyECommerce.type.PaymentStatusType;
import com.myecommerce.MyECommerce.type.PgProviderType;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.UUID;

import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.*;
import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.*;

@Entity
@Getter
@Slf4j
@Builder(access = AccessLevel.PRIVATE) // Builder 접근 제한
@AllArgsConstructor(access = AccessLevel.PRIVATE) //  Lombok Builder의 내부 구현으로 @Builder 사용 시 필수
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA(Hibernate)만 리플렉션으로 접근 가능
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id", nullable = false)
    private Order order;

    @Column(unique = true, nullable = false)
    private String paymentCode; // 결제 코드

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethodType paymentMethod; // 결제 방법

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PgProviderType pgProvider; // PG 결제대행사

    @Column(unique = true)
    private String pgTransactionId; // PG 결제번호

    @Builder.Default
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal approvedAmount = BigDecimal.ZERO; // 결제 승인된 금액

    @Builder.Default
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal vatAmount = BigDecimal.ZERO; // 부가세

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatusType paymentStatus; // 결제 상태

    /* ----------------------
        Method
       ---------------------- */

    /** 결제 생성 **/
    public static Payment createPayment(Order order,
                                        PaymentMethodType paymentMethod,
                                        PgProviderType pgProvider) {
        // 도메인 정책 검증
        validateOrderStatus(order);

        // pgTransactionId, approvedAmount, vatAmount -> pg 승인 후 입력됨.
        return Payment.builder()
                .order(order)
                .paymentCode(createPaymentCode(order.getOrderNumber())) // PG API 요청용
                .paymentMethod(paymentMethod)
                .pgProvider(pgProvider)
                .paymentStatus(READY) // 결제 생성
                .build();
    }

    /** PG 결제 요청 결과 반영 **/
    public void requestPgPayment(PgResult pgResult) {
        // 결제 응답 없으면 결제 불가 처리 (현재 결제 창 종료 필요)
        if (pgResult == null) {
            throw new PaymentException(PG_REQUEST_FAILED);
        }

        // 결제 트랜잭션 ID 미존재 시 결제 불가 처리 (현재 결제 창 종료 필요)
        if (pgResult.getPgTransactionId() == null ||
                pgResult.getPgTransactionId().isEmpty()) {
            throw new PaymentException(PG_RESPONSE_TRANSACTION_ID_NOT_EXISTS);
        }

        // READY 상태에서만 PG 결제요청 가능
        if (this.paymentStatus != READY) {
           throw new PaymentException(PAYMENT_STATUS_NOT_READY);
        }

        this.pgTransactionId = pgResult.getPgTransactionId();
        this.paymentStatus = IN_PROGRESS; // 승인 대기 중
    }

    /** PaymentStatus - 승인성공 상태 전이
     *  PG 결제 웹훅 승인결과 반영 **/
    public void approve(PgApprovalResult pgResult) {
        // PG 승인 대기 중일 때만 상태 변경 가능
        if (this.paymentStatus != IN_PROGRESS) {
            throw new PaymentException(PAYMENT_STATUS_NOT_IN_PROGRESS);
        }
        // 금액 검증
        validatePaidAmount(pgResult);
        // 승인결과 반영
        this.approvedAmount = pgResult.getPaidAmount();
        this.vatAmount = pgResult.getVatAmount();
        this.paymentStatus = APPROVED; // 승인 완료
    }

    /** PaymentStatus - 승인성공 상태 전이
     *  PG 결제 웹훅 실패 **/
    public void fail() {
        // PG 승인 대기 중일 때만 상태 변경 가능
        if (this.paymentStatus != IN_PROGRESS) {
            throw new PaymentException(PAYMENT_STATUS_NOT_IN_PROGRESS);
        }
        this.paymentStatus = PaymentStatusType.FAILED; // 승인 실패
    }

    /** 결제 종결 여부 반환 **/
    public boolean isTerminal() {
        // 결제승인, 결제취소, 결재실패의 경우 결제 상태 종결
        return this.paymentStatus == APPROVED ||
                this.paymentStatus == CANCELED ||
                this.paymentStatus == FAILED;
    }

    // 주문 생성된 경우만 결제가능
    private static void validateOrderStatus(Order order) {
        if(order.getOrderStatus() != CREATED) {
            throw new PaymentException(ORDER_STATUS_NOT_CREATED);
        }
    }

    // 금액 일치여부 검증
    private void validatePaidAmount(PgApprovalResult pgResult) {
        BigDecimal orderAmount = this.order.getTotalPrice();
        BigDecimal paidAmount = pgResult.getPaidAmount();
        if (orderAmount.compareTo(paidAmount) != 0) {
            // 결제 금액 불일치는 비즈니스 에러가 아닌 사고 -> 로그 출력과 가능하다면 알람 설정
            log.error("Payment amount mismatch. orderId={}, paid={}, expected={}",
                    order.getId(), paidAmount, orderAmount);
            throw new PaymentException(PAYMENT_AMOUNT_INCONSISTENCY);
        }
    }

    // 결제코드 생성
    private static String createPaymentCode(String orderNumber) {
        // 주문번호 + 하이픈 + 결제코드 8자리
        return orderNumber + "-" +
                UUID.randomUUID().toString().substring(0,8).toUpperCase();
    }
}
