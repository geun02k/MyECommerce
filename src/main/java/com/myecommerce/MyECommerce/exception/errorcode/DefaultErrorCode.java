package com.myecommerce.MyECommerce.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DefaultErrorCode implements CommonErrorCode {

    // 서버 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류입니다.\n관리자에게 문의하세요."),

    // 유효성검증 오류
    INVALID_VALUE(HttpStatus.BAD_REQUEST.value(), "유효하지않은 값입니다.")
    ;

    private final int statusCode; // http 상태코드
    private final String errorMessage; // 에러메시지
}
