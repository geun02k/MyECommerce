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

    @NotNull(message = "{validation.product.id.not.null}")
    @Min(value = 1, message = "{validation.product.id.min}")
    private Long id;

    private String description;

    @EnumValid(enumClass = ProductionSaleStatusType.class,
            message = "{validation.product.sale.status.enum.invalid}")
    private ProductionSaleStatusType saleStatus;

    @Valid
    private List<RequestModifyProductionOptionDto> options;

}
