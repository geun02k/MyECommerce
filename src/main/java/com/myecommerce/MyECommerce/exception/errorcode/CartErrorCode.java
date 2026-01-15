package com.myecommerce.MyECommerce.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CartErrorCode implements CommonErrorCode {

    // 장바구니 validation check
    CART_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST.value(),
            "error.cart.size.exceeded")
    ;

    private final int statusCode;
    private final String errorMessage;
}
