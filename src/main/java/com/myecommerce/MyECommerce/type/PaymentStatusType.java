package com.myecommerce.MyECommerce.type;

import lombok.AllArgsConstructor;

// 결제상태 도메인
@AllArgsConstructor
public enum PaymentStatusType {
    READY("결제 생성 완료, PG 요청 전"),
    IN_PROGRESS("PG 승인 대기 중"), // 웹훅 응답 대기
    APPROVED("PG 승인 완료"),
    FAILED("PG 승인 실패"),
    CANCELED("PG 결제 취소")
    ;

    private final String description;
}
