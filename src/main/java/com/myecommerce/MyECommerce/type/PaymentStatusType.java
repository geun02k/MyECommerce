package com.myecommerce.MyECommerce.type;

import lombok.AllArgsConstructor;

// 결제상태 도메인
@AllArgsConstructor
public enum PaymentStatusType {
    IN_PROGRESS("승인 대기 중"), // 결제 생성 직전 상태 (웹훅 응답 대기)
    SUCCESS("승인 완료"),
    FAILED("승인 실패"),
    CANCELED("결제 취소")
    ;

    private final String description;
}
