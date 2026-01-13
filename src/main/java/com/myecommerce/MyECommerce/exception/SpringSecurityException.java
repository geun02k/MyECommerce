package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.SpringSecurityErrorCode;
import lombok.Getter;

@Getter
public class SpringSecurityException extends BaseAbstractException {

    private final SpringSecurityErrorCode errorCode;

    public SpringSecurityException(SpringSecurityErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
