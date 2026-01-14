package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.CommonErrorCode;
import lombok.Getter;

@Getter
public abstract class BaseAbstractException extends RuntimeException {

    private static final Object[] EMPTY_ARGS = new Object[0];

    // 에러코드 반환(HTTP 상태코드, 에러메시지)
    private final CommonErrorCode errorCode;
    // 에러 메시지 전달인자
    private final Object[] messageArgs;

    protected BaseAbstractException(CommonErrorCode errorCode) {
        this(errorCode, EMPTY_ARGS);
    }

    protected BaseAbstractException(CommonErrorCode errorCode, Object... messageArgs) {
        this.errorCode = errorCode;
        this.messageArgs = messageArgs == null ? EMPTY_ARGS : messageArgs;
    }

}
