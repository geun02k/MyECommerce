package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode;

public class PaymentException extends BaseAbstractException {

    public PaymentException(PaymentErrorCode errorCode) {
        super(errorCode);
    }

    public PaymentException(PaymentErrorCode errorCode, Object... messageArgs) {
        super(errorCode, messageArgs);
    }

}
