package com.myecommerce.MyECommerce.dto.cart;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCartDto {

    private Long productId;

    private String productName;

    private Long optionId;

    private String optionName;

    private BigDecimal price;

    private int quantity;

    private LocalDate expiryDate;
}
