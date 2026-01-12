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

    @Size(min=1, max=100, message = "{validation.product.option.code.size}")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9\\-]*$",
            message = "{validation.product.option.code.pattern}")
    private String optionCode;

    @Size(min=1, max=200, message = "{validation.product.option.name.size}")
    @Pattern(regexp = "^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9][a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9\\s]*$",
            message = "{validation.product.option.name.pattern}")
    private String optionName;

    @NotNull(message = "{validation.product.option.price.not.null}")
    @DecimalMin(value = "1",
            message = "{validation.product.option.price.decimal.min}")
    private BigDecimal price;

    @Min(value = 1, message = "validation.product.option.quantity.min")
    private int quantity;

}
