package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.MemberErrorCode;

public class MemberException extends BaseAbstractException {

    public MemberException(MemberErrorCode errorCode) {
        super(errorCode);
    }

}
