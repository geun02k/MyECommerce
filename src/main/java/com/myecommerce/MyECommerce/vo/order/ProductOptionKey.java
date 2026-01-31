package com.myecommerce.MyECommerce.vo.order;

// VO (Value Object 생성) : 객체 자체로 의미를 가짐, 불변
public record ProductOptionKey (
        Long productId,
        String optionCode
) {}
