package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.SpringSecurityErrorCode;

public class SpringSecurityException extends BaseAbstractException {

    public SpringSecurityException(SpringSecurityErrorCode errorCode) {
        super(errorCode);
    }
}
