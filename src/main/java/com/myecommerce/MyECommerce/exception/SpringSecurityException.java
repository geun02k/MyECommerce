package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.SpringSecurityErrorCode;
import lombok.Getter;

@Getter
public class SpringSecurityException extends BaseAbstractException {

    private final SpringSecurityErrorCode errorCode;
    private final String errorMessage;
    private final int statusCode;

    public SpringSecurityException(SpringSecurityErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getErrorMessage();
        this.statusCode = errorCode.getStatusCode();
    }
}
