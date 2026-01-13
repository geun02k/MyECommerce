package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.CommonErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class BaseAbstractException extends RuntimeException {

    // 에러코드 반환(HTTP 상태코드, 에러메시지)
    private final CommonErrorCode errorCode;

}
