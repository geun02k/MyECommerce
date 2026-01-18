package com.myecommerce.MyECommerce.dto.product;

import com.myecommerce.MyECommerce.type.ProductCategoryType;
import com.myecommerce.MyECommerce.type.ProductOrderByStdType;
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
    @EnumValid(enumClass = ProductOrderByStdType.class,
            message = "{validation.product.order.by.std.enum.invalid}")
    private ProductOrderByStdType orderByStd;

    @NotNull(message = "{validation.product.category.not.null}")
    @EnumValid(enumClass = ProductCategoryType.class,
            message = "{validation.product.category.enum.invalid}")
    private ProductCategoryType category;

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