package com.myecommerce.MyECommerce.service.payment;

import com.myecommerce.MyECommerce.dto.payment.PgApiResponse;
import com.myecommerce.MyECommerce.dto.payment.PgApprovalResult;
import com.myecommerce.MyECommerce.dto.payment.PgResult;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.exception.PaymentException;
import com.myecommerce.MyECommerce.type.PaymentStatusType;
import com.myecommerce.MyECommerce.type.PgProviderType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.PG_API_ERROR;
import static com.myecommerce.MyECommerce.type.PgProviderType.MOCK_PG;

/** 외부 PG API 호출 Handler **/
@Slf4j
@Component
// @Profile("local")
public class TestPgClientImpl implements PgClient {

    private final static PgProviderType PG_PROVIDER = MOCK_PG;

    /** PG 결제대행사 코드 반환 **/
    @Override
    public PgProviderType getProvider() {
        return PG_PROVIDER;
    }

    /** 결제 요청 **/
    @Override
    public PgApiResponse<PgResult> requestPayment(Payment payment) {
        BigDecimal price = payment.getOrder().getTotalPrice();

        // 외부 api 호출 부분
        try {
            // 결제 실패 응답 mock
            if (price.compareTo(BigDecimal.ZERO) <= 0) {
                return PgApiResponse.fail(
                        "INVALID_AMOUNT", "결제 금액이 올바르지 않습니다.");
            }

        // 외부 api 요청 후 네트워크 등 Exception 발생
        } catch (Exception e) {
            log.error("PG API 호출 실패", e);
            throw new PaymentException(PG_API_ERROR);
        }

       // 결제 성공 응답 mock
        PgResult pgResult = PgResult.builder()
                .pgTransactionId(createPgTransactionId())
                .redirectUrl("redirectUrl")
                .build();
        return PgApiResponse.success(pgResult);
    }

    /** 결제 승인 **/
    @Override
    public PgApprovalResult approvePayment(Payment payment) {

        if(payment.getPgTransactionId() == null) {
            throw new IllegalArgumentException("Payment transaction id is null");
        }

        BigDecimal paymentAmount = payment.getOrder().getTotalPrice();
        BigDecimal paymentVat = payment.getOrder().getTotalPrice()
                .divide(BigDecimal.TEN, 2, RoundingMode.HALF_UP);

        return PgApprovalResult.builder()
                .pgTransactionId(payment.getPgTransactionId())
                .approvalStatus(PaymentStatusType.APPROVED)
                .approvalAt(LocalDateTime.now())
                .paidAmount(paymentAmount)
                .vatAmount(paymentVat)
                .build();
    }

    // 결제 트랜잭션 ID 생성
    private String createPgTransactionId() {
        return UUID.randomUUID().toString().substring(0, 7);
    }
}
