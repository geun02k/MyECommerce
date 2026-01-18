package com.myecommerce.MyECommerce.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DefaultErrorCode implements CommonErrorCode {

    // 서버 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error.default.interval.server.error"),

    // 유효성검증 오류
    INVALID_VALUE(HttpStatus.BAD_REQUEST.value(),
            "error.default.invalid.value")
    ;

    private final int statusCode; // http 상태코드
    private final String errorMessage; // 에러메시지
}
