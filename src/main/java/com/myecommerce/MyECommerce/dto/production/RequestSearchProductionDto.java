package com.myecommerce.MyECommerce.dto.production;

import com.myecommerce.MyECommerce.type.ProductionCategoryType;
import com.myecommerce.MyECommerce.type.ProductionOrderByStdType;
import com.myecommerce.MyECommerce.validation.EnumValid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestSearchProductionDto {

    @NotNull(message = "{validation.product.order.by.std.not.null}")
    @EnumValid(enumClass = ProductionOrderByStdType.class,
            message = "{validation.product.order.by.std.enum.invalid}")
    private ProductionOrderByStdType orderByStd;

    @NotNull(message = "{validation.product.category.not.null}")
    @EnumValid(enumClass = ProductionCategoryType.class,
            message = "{validation.product.category.enum.invalid}")
    private ProductionCategoryType category;

    @NotBlank(message = "{validation.product.keyword.not.blank}")
    @Size(max = 200, message = "{validation.product.keyword.size}")
    private String keyword;

    // Pageable 객체관련 필드
    @Min(value = 0, message = "{validation.product.page.min}")
    private int page = 0;

    @Min(value = 1, message = "{validation.product.size.min}")
    @Max(value = 100, message = "{validation.product.size.max}")
    private int size = 5;

    public Pageable getPageable() {
        return PageRequest.of(page, size);
    }

}