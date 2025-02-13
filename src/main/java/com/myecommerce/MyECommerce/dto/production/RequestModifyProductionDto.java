package com.myecommerce.MyECommerce.dto.production;

import com.myecommerce.MyECommerce.type.ProductionSaleStatusType;
import com.myecommerce.MyECommerce.validation.EnumValid;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestModifyProductionDto {

    @NotNull
    @Min(1)
    private Long id;

    private String description;

    @EnumValid(enumClass = ProductionSaleStatusType.class)
    private ProductionSaleStatusType saleStatus;

    @Valid
    private List<RequestModifyProductionOptionDto> options;

}
