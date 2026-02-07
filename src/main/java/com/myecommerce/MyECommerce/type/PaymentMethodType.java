package com.myecommerce.MyECommerce.type;

import lombok.AllArgsConstructor;

// 결제방법 도메인
@AllArgsConstructor
public enum PaymentMethodType {
    CARD("카드"),
    BANK_TRANSFER("계좌이체 / 무통장입금"),
    KAKAO_PAY("카카오 페이"),
    NAVER_PAY("네이버 페이")
    ;

    private final String description;
}
