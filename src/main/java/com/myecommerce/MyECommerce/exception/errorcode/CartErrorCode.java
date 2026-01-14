package com.myecommerce.MyECommerce.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CartErrorCode implements CommonErrorCode {

    // 장바구니 validation check
    LIMIT_CART_MAX_SIZE(HttpStatus.BAD_REQUEST.value(), "error.cart.limit.max.size")
    ;

    private final int statusCode;
    private final String errorMessage;
}
