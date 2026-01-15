package com.myecommerce.MyECommerce.exception.errorcode;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProductionErrorCode implements CommonErrorCode {

    // 상품 validation check
    PRODUCT_CODE_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST.value(),
            "error.product.code.already.registered"),

    // 상품옵션 validation check
    PRODUCT_OPTION_CODE_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST.value(),
            "error.product.option.code.already.registered"),
    PRODUCT_OPTION_CODE_DUPLICATED(HttpStatus.BAD_REQUEST.value(),
            "error.product.option.code.duplicated"),

    // 상품수정 validation check
    PRODUCT_EDIT_FORBIDDEN(HttpStatus.BAD_REQUEST.value(),
            "error.product.edit.forbidden"),
    PRODUCT_ALREADY_DELETED(HttpStatus.BAD_REQUEST.value(),
            "error.product.already.deleted"),
    PRODUCT_OPTION_NOT_EXIST(HttpStatus.BAD_REQUEST.value(),
            "error.product.option.not.exist"),

    // 상품상세조회
    PRODUCT_NOT_EXIST(HttpStatus.BAD_REQUEST.value(),
            "error.product.not.exist")
    ;

    private final int statusCode;
    private final String errorMessage;
}
