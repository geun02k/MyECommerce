package com.myecommerce.MyECommerce.dto.product;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestModifyProductOptionDto {

    private Long id;

    @NotBlank(message = "{validation.product.option.code.not.blank}")
    @Size(min=1, max=100, message = "{validation.product.option.code.size}")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9\\-]*$",
            message = "{validation.product.option.code.pattern}")
    private String optionCode;

    @NotBlank(message = "{validation.product.option.name.not.blank}")
    @Size(min=1, max=200, message = "{validation.product.option.name.size}")
    @Pattern(regexp = "^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9][a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9\\s]*$",
            message = "{validation.product.option.name.pattern}")
    private String optionName;

    @NotNull(message = "{validation.product.option.price.not.null}")
    @DecimalMin(value = "1",
            message = "{validation.product.option.price.decimal.min}")
    private BigDecimal price;

    @Min(value = 1, message = "{validation.product.option.quantity.min}")
    @Max(value = 9999, message = "{validation.product.option.quantity.max}")
    private int quantity;

}