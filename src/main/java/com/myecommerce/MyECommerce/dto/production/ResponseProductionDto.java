package com.myecommerce.MyECommerce.dto.production;

import com.myecommerce.MyECommerce.type.ProductionCategoryType;
import com.myecommerce.MyECommerce.type.ProductionSaleStatusType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseProductionDto {

    private Long id;

    private Long seller;

    private String code;

    private String name;

    private ProductionCategoryType category;

    private String description;

    private ProductionSaleStatusType saleStatus;

    private Long createId;

    private LocalDateTime createDt;

    private Long updateId;

    private LocalDateTime updateDt;

}
