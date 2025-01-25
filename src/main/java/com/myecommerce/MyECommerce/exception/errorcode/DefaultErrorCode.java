package com.myecommerce.MyECommerce.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DefaultErrorCode implements CommonErrorCode {

    // login validation check
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 오류입니다.\n관리자에게 문의하세요.")
    ;

    private final int statusCode; // http 상태코드
    private final String errorMessage; // 에러메시지
}
