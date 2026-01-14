package com.myecommerce.MyECommerce.exception.errorcode;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProductionErrorCode implements CommonErrorCode {

    // 상품 validation check
    ALREADY_REGISTERED_CODE(HttpStatus.BAD_REQUEST.value(),
            "error.product.already.registered.code"),

    // 상품옵션 validation check
    ALREADY_REGISTERED_OPTION_CODE(HttpStatus.BAD_REQUEST.value(),
            "error.product.already.registered.option.code"),
    ENTER_DUPLICATED_OPTION_CODE(HttpStatus.BAD_REQUEST.value(),
            "error.product.enter.duplicated.option.code"),

    // 상품수정 validation check
    NO_EDIT_PERMISSION(HttpStatus.BAD_REQUEST.value(),
            "error.product.no.edit.permission"),
    NO_EDIT_DELETION_STATUS(HttpStatus.BAD_REQUEST.value(),
            "error.product.no.edit.deletion.status"),
    NO_EDIT_FOR_NOT_EXIST_OPTION(HttpStatus.BAD_REQUEST.value(),
            "error.product.for.not.exist.option"),

    // 상품상세조회
    NOT_EXIST_PRODUCT(HttpStatus.BAD_REQUEST.value(),
            "error.product.not.exist.product")
    ;

    private final int statusCode;
    private final String errorMessage;
}
