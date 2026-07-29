package com.myecommerce.MyECommerce.dto.order;


import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestOrderDto {

    @NotNull(message = "{validation.order.product.option.id.not.null}")
    @Positive(message = "{validation.order.product.option.id.positive}")
    private Long productOptionId;

    @NotNull(message = "{validation.order.quantity.not.null}")
    @Min(value = 1, message = "{validation.order.quantity.min}")
    @Max(value = 50, message = "{validation.order.quantity.max}")
    private int quantity;

}
