package com.myecommerce.MyECommerce.type;

import lombok.AllArgsConstructor;

// 주문상태 도메인
@AllArgsConstructor
public enum OrderStatusType {
    CREATED("주문완료"),  // 결제, 취소 가능
    CANCELED("주문취소"), // 결제불가
    PAID("결제완료")      // 취소불가
    ;

    private final String description;
}
