package com.myecommerce.MyECommerce.dto.production;

import com.myecommerce.MyECommerce.type.ProductionCategoryType;
import com.myecommerce.MyECommerce.type.ProductionOrderByStdType;
import com.myecommerce.MyECommerce.validation.EnumValid;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestSearchProductionDto {

    @EnumValid(enumClass = ProductionOrderByStdType.class,
            message = "{validation.product.order.by.std.enum.invalid}")
    private ProductionOrderByStdType orderByStd;

    @EnumValid(enumClass = ProductionCategoryType.class,
            message = "{validation.product.category.enum.invalid}")
    private ProductionCategoryType category;

    @Pattern(regexp = "^[^\\s]+$",
            message = "{validation.product.keyword.pattern}") // 공백 외 최소 한 글자 이상 입력
    private String keyword;

    // Pageable 객체관련 필드
    private int page = 0;

    private int size = 5;

    public Pageable getPageable() {
        return PageRequest.of(page, size);
    }

}