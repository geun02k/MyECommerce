package com.myecommerce.MyECommerce.dto.product;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProductOptionDto {
    private Long id;
    private String optionCode;
    private String optionName;
    private BigDecimal price;
    private int quantity;
}
