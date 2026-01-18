package com.myecommerce.MyECommerce.dto.product;

import com.myecommerce.MyECommerce.type.ProductCategoryType;
import com.myecommerce.MyECommerce.type.ProductSaleStatusType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSearchDetailProductDto {

    private Long id;

    private Long seller;

    private String code;

    private String name;

    private ProductCategoryType category;

    private String description;

    private ProductSaleStatusType saleStatus;

    private Long createId;

    private LocalDateTime createDt;

    private Long updateId;

    private LocalDateTime updateDt;

    private List<ResponseProductOptionDto> options;

}
