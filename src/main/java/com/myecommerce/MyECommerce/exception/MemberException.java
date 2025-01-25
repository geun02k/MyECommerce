package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.MemberErrorCode;
import lombok.Getter;

@Getter
public class MemberException extends BaseAbstractException {

    private final MemberErrorCode errorCode;
    private final String errorMessage;
    private final int statusCode;

    public MemberException(MemberErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getErrorMessage();
        this.statusCode = errorCode.getStatusCode();
    }

}
