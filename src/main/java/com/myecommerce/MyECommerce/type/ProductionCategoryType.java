package com.myecommerce.MyECommerce.type;

import lombok.AllArgsConstructor;

// 상품분류 도메인
@AllArgsConstructor
public enum ProductionCategoryType {
    WOMEN_CLOTHING("여성의류"),
    MANS_CLOTHING("남성의류"),
    FASHION_GOODS("패션잡화"),
    BEAUTY("뷰티"),
    ELECTRONICS("전자제품"),
    OUTDOOR("아웃도어"),
    BABY_PRODUCTION("유아용품"),
    ETC("기타"),
    ;

    private final String description;
}
