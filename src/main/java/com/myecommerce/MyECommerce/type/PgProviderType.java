package com.myecommerce.MyECommerce.type;

import lombok.AllArgsConstructor;

// PG 결제대행사 도메인 (1건이지만 의미 명확화를 위해 Enum 생성)
@AllArgsConstructor
public enum PgProviderType {
    MOCK_PG("테스트용 PG사")
    // TOSS("토스") // 실제 PG사 연동 시
    ;

    private final String description;
}
