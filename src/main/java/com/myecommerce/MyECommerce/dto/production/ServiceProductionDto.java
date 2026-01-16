package com.myecommerce.MyECommerce.dto.production;

import com.myecommerce.MyECommerce.type.ProductionCategoryType;
import com.myecommerce.MyECommerce.type.ProductionSaleStatusType;
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
    private ProductionCategoryType category;
    private String description;
    private ProductionSaleStatusType saleStatus;
    private List<ServiceProductionOptionDto> options;
}
