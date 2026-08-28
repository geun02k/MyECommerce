package com.myecommerce.MyECommerce.type;

import lombok.AllArgsConstructor;

// 주문경로 도메인
@AllArgsConstructor
public enum OrderPathType {
    CART("장바구니에서 주문"),
    DIRECT("상품 상세 페이지에서 주문")
    ;

    private final String description;
}
