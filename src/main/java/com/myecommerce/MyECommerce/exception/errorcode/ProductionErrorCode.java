package com.myecommerce.MyECommerce.exception.errorcode;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProductionErrorCode implements CommonErrorCode {

    // 상품 validation check
    ALREADY_REGISTERED_CODE(HttpStatus.BAD_REQUEST.value(), "이미 등록된 상품코드입니다."),

    // 상품옵션 validation check
    ALREADY_REGISTERED_OPTION_CODE(HttpStatus.BAD_REQUEST.value(), "이미 등록된 상품옵션코드입니다."),
    ENTER_DUPLICATED_OPTION_CODE(HttpStatus.BAD_REQUEST.value(), "중복된 상품옵션코드 입력은 불가합니다."),

    // 상품수정 validation check
    NO_EDIT_PERMISSION(HttpStatus.BAD_REQUEST.value(), "본인이 등록한 상품만 수정 가능합니다."),
    NO_EDIT_DELETION_STATUS(HttpStatus.BAD_REQUEST.value(), "판매종료된 상품에 대해 수정 불가합니다.")
    ;


    private final int statusCode;
    private final String errorMessage;
}
