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

    @Size(min=1, max=100, message = "상품코드는 최소 1자, 최대 100자로 제한됩니다.")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9\\-]*$", message = "상품코드는 영문자, 숫자만 사용 가능합니다.\n특수문자는 -만 허용하며 첫 글자로 사용 불가합니다.")
    private String code;

    @Size(min=1, max=200, message = "상품명은 최소 1자, 최대 200자로 제한됩니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9][a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9\\s]*$", message = "상품명에 특수문자 사용이 불가합니다.")
    private String name;

    @EnumValid(enumClass = ProductionCategoryType.class)
    private ProductionCategoryType category;

    private String description;

    @Valid
    private List<RequestProductionOptionDto> options;

}
