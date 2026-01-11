package com.myecommerce.MyECommerce.dto.cart;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCartDto {

    @NotNull(message = "{validation.cart.option.id.not.null}")
    @Min(value = 1, message = "{validation.cart.option.id.min}")
    private Long optionId;

    @Min(value = 1, message = "{validation.cart.quantity.min}")
    private int quantity;

}
