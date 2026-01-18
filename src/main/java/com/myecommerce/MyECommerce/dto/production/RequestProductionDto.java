package com.myecommerce.MyECommerce.dto.production;

import com.myecommerce.MyECommerce.type.ProductionCategoryType;
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
public class RequestProductionDto {

    @NotBlank(message = "{validation.product.code.not.blank}")
    @Size(min=1, max=100, message ="{validation.product.code.size}")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9\\-]*$",
            message = "{validation.product.code.pattern}")
    private String code;

    @NotBlank(message = "{validation.product.name.not.blank}")
    @Size(min=1, max=200, message = "{validation.product.name.size}")
    @Pattern(regexp = "^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9][a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9\\s]*$",
            message = "{validation.product.name.pattern}")
    private String name;

    @NotNull(message = "{validation.product.category.not.null}")
    @EnumValid(enumClass = ProductionCategoryType.class,
            message = "{validation.product.category.enum.invalid}")
    private ProductionCategoryType category;

    @Size(max = 4000, message = "{validation.product.description.size}")
    private String description;

    @Size(max = 100, message = "{validation.product.option.size}")
    @Valid
    private List<RequestProductionOptionDto> options;

}
