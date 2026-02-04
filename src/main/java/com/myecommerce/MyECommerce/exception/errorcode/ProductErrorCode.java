package com.myecommerce.MyECommerce.exception.errorcode;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProductErrorCode implements CommonErrorCode {

    // 상품 validation check
    PRODUCT_CODE_ALREADY_REGISTERED(HttpStatus.CONFLICT.value(),
            "error.product.code.already.registered"),
    PRODUCT_NOT_ON_SALE(HttpStatus.BAD_REQUEST.value(),
            "error.product.not.on.sale"),

    // 상품옵션 validation check
    OPTION_AT_LEAST_ONE_REQUIRED(HttpStatus.BAD_REQUEST.value(),
            "error.product.option.at.least.one_required"),
    PRODUCT_OPTION_CODE_ALREADY_REGISTERED(HttpStatus.CONFLICT.value(),
            "error.product.option.code.already.registered"),
    PRODUCT_OPTION_CODE_DUPLICATED(HttpStatus.BAD_REQUEST.value(),
            "error.product.option.code.duplicated"),
    OPTION_PRICE_NOT_POSITIVE(HttpStatus.BAD_REQUEST.value(),
            "error.product.option.price.not.positive"),

    // 상품수정 validation check
    PRODUCT_EDIT_FORBIDDEN(HttpStatus.FORBIDDEN.value(),
            "error.product.edit.forbidden"),
    PRODUCT_ALREADY_DELETED(HttpStatus.CONFLICT.value(),
            "error.product.already.deleted"),
    PRODUCT_OPTION_NOT_EXIST(HttpStatus.NOT_FOUND.value(),
            "error.product.option.not.exist"),

    // 상품상세조회
    PRODUCT_NOT_EXIST(HttpStatus.NOT_FOUND.value(),
            "error.product.not.exist")
    ;

    private final int statusCode;
    private final String errorMessage;
}
