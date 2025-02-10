package com.myecommerce.MyECommerce.exception.errorcode;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProductionErrorCode implements CommonErrorCode {

    // 상품 객체 validation check
    ALREADY_REGISTERED_CODE(HttpStatus.BAD_REQUEST.value(), "이미 등록된 상품코드입니다."),

    // 상품옵션 객체 validation check
    ALREADY_REGISTERED_OPTION_CODE(HttpStatus.BAD_REQUEST.value(), "이미 등록된 상품옵션코드입니다.")

    ;


    private final int statusCode;
    private final String errorMessage;
}
