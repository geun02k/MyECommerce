package com.myecommerce.MyECommerce.dto.product;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseProductOptionDto {

    private Long id;

    private String optionCode;

    private String optionName;

    private BigDecimal price;

    private int quantity;

    private Long createId;

    private LocalDateTime createDt;

    private Long updateId;

    private LocalDateTime updateDt;

}
