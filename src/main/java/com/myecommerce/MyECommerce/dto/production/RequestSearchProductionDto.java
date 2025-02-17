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

    @EnumValid(enumClass = ProductionOrderByStdType.class)
    private ProductionOrderByStdType orderByStd;

    @EnumValid(enumClass = ProductionCategoryType.class)
    private ProductionCategoryType category;

    @Pattern(regexp = "^[^\\s]+$", message = "검색어를 입력하세요.") // 공백 이외 최소 한글자이상 입력
    private String keyword;

    // Pageable 객체관련 필드
    private int page = 0;

    private int size = 5;

    public Pageable getPageable() {
        return PageRequest.of(page, size);
    }

}