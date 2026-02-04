package com.myecommerce.MyECommerce.dto.order;


import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestOrderDto {

    @NotNull(message = "{validation.order.product.id.not.null}")
    @Positive(message = "{validation.order.product.id.positive}")
    private Long productId;

    @NotBlank(message = "{validation.order.option.code.not.blank}")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9\\-]*$",
            message = "{validation.order.option.code.pattern}")
    private String optionCode;

    @NotNull(message = "{validation.order.quantity.not.null}")
    @Min(value = 1, message = "{validation.order.quantity.min}")
    @Max(value = 50, message = "{validation.order.quantity.max}")
    private int quantity;

}
