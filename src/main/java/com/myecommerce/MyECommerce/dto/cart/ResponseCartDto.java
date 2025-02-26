package com.myecommerce.MyECommerce.dto.cart;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCartDto {

    private Long productId;

    private String productName;

    private Long optionId;

    private String optionName;

    private BigDecimal price;

    private int quantity;

}
