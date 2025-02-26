package com.myecommerce.MyECommerce.dto.cart;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestCartDto {

    @NotNull(message = "상품 옵션을 선택해주세요.")
    @Min(value = 1, message = "유효하지 않은 상품 옵션입니다.")
    private Long optionId;

    @Min(value = 1, message = "옵션의 판매가능 수량을 입력해주세요.")
    private int quantity;

}
