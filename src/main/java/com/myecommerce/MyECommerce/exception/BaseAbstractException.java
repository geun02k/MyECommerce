package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.CommonErrorCode;

public abstract class BaseAbstractException extends RuntimeException {

    // 에러상태코드 반환
    abstract public int getStatusCode();
    // 에러코드 반환
    abstract public CommonErrorCode getErrorCode();
    // 에러코드에 대한 에러메시지 반환
    abstract public String getErrorMessage();

}
