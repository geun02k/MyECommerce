package com.myecommerce.MyECommerce.dto.product;

import com.myecommerce.MyECommerce.type.ProductCategoryType;
import com.myecommerce.MyECommerce.type.ProductSaleStatusType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceProductionDto {
    private Long id;
    private String code;
    private String name;
    private ProductCategoryType category;
    private String description;
    private ProductSaleStatusType saleStatus;
    private List<ServiceProductionOptionDto> options;
}
