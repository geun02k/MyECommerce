package com.myecommerce.MyECommerce.dto.payment;

import com.myecommerce.MyECommerce.type.PaymentStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PgWebHookRequestDto {

    private String pgTransactionId;

    private BigDecimal paidAmount;

    private BigDecimal vatAmount;

    private PaymentStatusType approvalStatus;

    private LocalDateTime approvalAt;

}
