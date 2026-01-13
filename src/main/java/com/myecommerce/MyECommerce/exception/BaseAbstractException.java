package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.CommonErrorCode;

public abstract class BaseAbstractException extends RuntimeException {

    // 에러코드 반환
    abstract public CommonErrorCode getErrorCode();

}
