package com.myecommerce.MyECommerce.dto.cart;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCartDto {

    @NotBlank(message = "{validation.cart.product.id.not.blank}")
    private String productCode;

    @NotBlank(message = "{validation.cart.option.id.not.blank}")
    private String optionCode;

    @NotNull(message = "{validation.cart.quantity.not.null}")
    @Min(value = 1, message = "{validation.cart.quantity.min}")
    @Max(value = 50, message = "{validation.cart.quantity.max}")
    // TODO: 장바구니 기존 수량과 합산해서 MAX_OPTION_QTY(50) 초과시 예외 처리 필요
    private int quantity;

}
