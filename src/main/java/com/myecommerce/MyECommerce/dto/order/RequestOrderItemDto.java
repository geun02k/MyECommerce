package com.myecommerce.MyECommerce.dto.order;


import jakarta.validation.constraints.*;
import lombok.*;

// TODO: DTO 클래스명 RequestOrderItemDto로 변경 (위 클래스의 집합체인 상위 DTO는 RequestOrderDto로 변경)

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestOrderItemDto {

    @NotNull(message = "{validation.order.product.option.id.not.null}")
    @Positive(message = "{validation.order.product.option.id.positive}")
    private Long productOptionId;

    @NotNull(message = "{validation.order.quantity.not.null}")
    @Min(value = 1, message = "{validation.order.quantity.min}")
    @Max(value = 50, message = "{validation.order.quantity.max}")
    private int quantity;

}
