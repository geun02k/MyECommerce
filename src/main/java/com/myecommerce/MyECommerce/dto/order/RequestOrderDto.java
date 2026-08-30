package com.myecommerce.MyECommerce.dto.order;


import com.myecommerce.MyECommerce.type.OrderPathType;
import com.myecommerce.MyECommerce.validation.EnumValid;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestOrderDto {

    @NotNull(message = "{validation.order.path.type.not.null}")
    @EnumValid(enumClass = OrderPathType.class,
               message = "{validation.order.path.type.enum.valid}")
    OrderPathType orderPathType; // 주문 경로

    @NotEmpty(message = "{validation.order.item.not.empty}")
    @Size(max = 100, message = "{validation.order.item.size}")
    @Valid
    @Builder.Default // 필드 초기화 방식을 유지하면서 빌더 패턴에서도 safe-default를 보장
    List<RequestOrderItemDto> orderItems = new ArrayList<>(); // 주문 상품옵션 및 수량

}
