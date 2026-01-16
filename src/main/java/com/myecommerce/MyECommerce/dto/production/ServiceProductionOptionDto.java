package com.myecommerce.MyECommerce.dto.production;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProductionOptionDto {
    private Long id;
    private String optionCode;
    private String optionName;
    private BigDecimal price;
    private int quantity;
}
