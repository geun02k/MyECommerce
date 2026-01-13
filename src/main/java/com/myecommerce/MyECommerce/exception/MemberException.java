package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.MemberErrorCode;
import lombok.Getter;

@Getter
public class MemberException extends BaseAbstractException {

    private final MemberErrorCode errorCode;

    public MemberException(MemberErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
