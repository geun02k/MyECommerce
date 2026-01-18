package com.myecommerce.MyECommerce.dto.product;

import com.myecommerce.MyECommerce.type.ProductSaleStatusType;
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
public class RequestModifyProductDto {

    @NotNull(message = "{validation.product.id.not.null}")
    @Positive(message = "{validation.product.id.positive}")
    private Long id;

    @Size(max = 4000, message = "{validation.product.description.size}")
    private String description;

    @NotNull(message = "{validation.product.category.not.null}")
    @EnumValid(enumClass = ProductSaleStatusType.class,
            message = "{validation.product.sale.status.enum.invalid}")
    private ProductSaleStatusType saleStatus;

    @Size(max = 100, message = "{validation.product.option.size}")
    @Valid
    private List<RequestModifyProductOptionDto> options;

}
