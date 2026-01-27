package com.myecommerce.MyECommerce.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCartDetailDto {

    private Long productId;

    private String productCode;

    private String productName;

    private Long optionId;

    private String optionCode;

    private String optionName;

    private BigDecimal price;

    private int quantity;

    // 조회 전용 필드
    private boolean outOfStock; // 품절여부
    private int availableQuantity; // 구매가능수량

}
