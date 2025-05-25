package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.CartErrorCode;
import lombok.Getter;

@Getter
public class CartException extends BaseAbstractException {

    private final CartErrorCode errorCode;
    private final String errorMessage;
    private final int statusCode;

    public CartException(CartErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getErrorMessage();
        this.statusCode = errorCode.getStatusCode();
    }

}
