package com.myecommerce.MyECommerce.service.payment;

import com.myecommerce.MyECommerce.dto.payment.PgApprovalResult;
import com.myecommerce.MyECommerce.dto.payment.PgResult;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.type.PaymentStatusType;
import com.myecommerce.MyECommerce.type.PgProviderType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.myecommerce.MyECommerce.type.PgProviderType.MOCK_PG;

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
    public PgResult requestPayment(Payment payment) {
        return PgResult.builder()
                .pgTransactionId(createPgTransactionId())
                .redirectUrl("redirectUrl")
                .build();
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
