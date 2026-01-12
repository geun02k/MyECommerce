package com.myecommerce.MyECommerce.dto.production;

import com.myecommerce.MyECommerce.type.ProductionCategoryType;
import com.myecommerce.MyECommerce.validation.EnumValid;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestProductionDto {

    @Size(min=1, max=100, message ="{validation.product.code.size}")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9\\-]*$",
            message = "{validation.product.code.pattern}")
    private String code;

    @Size(min=1, max=200, message = "{validation.product.name.size}")
    @Pattern(regexp = "^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9][a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9\\s]*$",
            message = "{validation.product.name.pattern}")
    private String name;

    @EnumValid(enumClass = ProductionCategoryType.class,
            message = "{validation.product.category.enum.invalid}")
    private ProductionCategoryType category;

    private String description;

    @Valid
    private List<RequestProductionOptionDto> options;

}
