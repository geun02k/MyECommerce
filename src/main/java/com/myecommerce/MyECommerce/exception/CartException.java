package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.CartErrorCode;
import lombok.Getter;

@Getter
public class CartException extends BaseAbstractException {

    private final CartErrorCode errorCode;

    public CartException(CartErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
