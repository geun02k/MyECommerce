package com.myecommerce.MyECommerce.type;

import lombok.AllArgsConstructor;

// 상품정렬기준 도메인
@AllArgsConstructor
public enum ProductionOrderByStdType {
    ORDER_BY_ACCURACY("정확도순"),
    ORDER_BY_REGISTRATION("등록순"),
    ORDER_BY_HIGHEST_PRICE("가격높은순"),
    ORDER_BY_LOWEST_PRICE("가격낮은순"),
    ;

    private final String description;
}
