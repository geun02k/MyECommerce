package com.myecommerce.MyECommerce.dto.payment;

import com.myecommerce.MyECommerce.type.PaymentStatusType;
import com.myecommerce.MyECommerce.validation.EnumValid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "{validation.payment.pg.transaction.id.not.blank}")
    private String pgTransactionId;

    @DecimalMin(value = "0",
                message = "{validation.payment.paid.amount.decimal.min}")
    private BigDecimal paidAmount;

    @DecimalMin(value = "0",
                message = "{validation.payment.vat.amount.decimal.min}")
    private BigDecimal vatAmount;

    @NotNull(message = "{validation.payment.paid.amount.not.null}")
    @EnumValid(enumClass = PaymentStatusType.class,
               message = "{validation.payment.approval.status.enum.valid}")
    private PaymentStatusType approvalStatus;

    @NotNull(message = "{validation.payment.paid.amount.not.null}")
    private LocalDateTime approvalAt;

}
