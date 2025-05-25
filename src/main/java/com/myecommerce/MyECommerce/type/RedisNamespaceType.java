package com.myecommerce.MyECommerce.type;

import lombok.AllArgsConstructor;

// Redis Namespace 도메인
@AllArgsConstructor
public enum RedisNamespaceType {
    LOGIN("유효한 로그인 토큰"),
    CART("장바구니"),
    STOCK("상품재고")
    ;

    private final String description;
}
