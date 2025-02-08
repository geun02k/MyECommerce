package com.myecommerce.MyECommerce.dto.production;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestProductionOptionDto {

    @Size(min=1, max=100, message = "옵션코드는 최소 1자, 최대 100자로 제한됩니다.")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9\\-]*$", message = "옵션코드는 영문자, 숫자만 사용 가능합니다.\n특수문자는 -만 허용하며 첫 글자로 사용 불가합니다.")
    private String optionCode;

    @Size(min=1, max=200, message = "옵션명은 최소 1자, 최대 200자로 제한됩니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9][a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9\\s]*$", message = "옵션명에 특수문자 사용이 불가합니다.")
    private String optionName;

    @NotNull(message = "해당 옵션의 가격을 입력해주세요.")
    @DecimalMin(value = "1", message = "해당 옵션의 가격 입력해주세요.")
    private BigDecimal price;

    @Min(value = 1, message = "해당 옵션의 판매가능 수량을 입력해주세요.")
    private int quantity;

    @Min(value = 1, message = "해당 옵션의 순서를 입력해주세요.")
    private int seq;

}
