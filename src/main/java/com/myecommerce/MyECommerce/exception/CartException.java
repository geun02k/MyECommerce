package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.CartErrorCode;

public class CartException extends BaseAbstractException {

    public CartException(CartErrorCode errorCode) {
        super(errorCode);
    }

    public CartException(CartErrorCode errorCode, Object... messageArgs) {
        super(errorCode, messageArgs);
    }

}
