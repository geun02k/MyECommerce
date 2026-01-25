package com.myecommerce.MyECommerce.type;

import lombok.AllArgsConstructor;

// 상품판매상태 도메인
@AllArgsConstructor
public enum ProductSaleStatusType {
    ON_SALE("판매중"),
    DISCONTINUED("판매중단"),
    DELETION("판매종료")
    ;

    private final String description;
}
