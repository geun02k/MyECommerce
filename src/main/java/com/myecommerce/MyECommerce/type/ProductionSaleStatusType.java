package com.myecommerce.MyECommerce.type;

import lombok.AllArgsConstructor;

// 상품판매상태 도메인
@AllArgsConstructor
public enum ProductionSaleStatusType {
    ON_SALE("판매중"),
    DELETION("삭제")
    ;

    private final String description;
}
