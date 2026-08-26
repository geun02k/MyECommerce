package com.myecommerce.MyECommerce.dto.payment;

import com.myecommerce.MyECommerce.type.PaymentMethodType;
import com.myecommerce.MyECommerce.validation.EnumValid;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestPaymentDto {

    @NotNull(message = "{validation.payment.order.id.not.blank}")
    @Min(value = 1, message = "{validation.payment.order.id.min}")
    private Long orderId;

    @NotNull(message = "{validation.payment.method.not.null}")
    @EnumValid(enumClass = PaymentMethodType.class,
               message = "{validation.payment.method.enum.valid}")
    private PaymentMethodType paymentMethod;

}
