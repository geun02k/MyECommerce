package com.myecommerce.MyECommerce.dto.cart;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCartDto {

    @NotNull(message = "{validation.cart.product.option.id.not.null}")
    @Positive(message = "{validation.cart.product.option.id.positive}")
    private Long productOptionId;

    @NotNull(message = "{validation.cart.quantity.not.null}")
    @Min(value = 1, message = "{validation.cart.quantity.min}")
    @Max(value = 50, message = "{validation.cart.quantity.max}")
    // TODO: 장바구니 기존 수량과 합산해서 MAX_OPTION_QTY(50) 초과시 예외 처리 필요
    private int quantity;

}
