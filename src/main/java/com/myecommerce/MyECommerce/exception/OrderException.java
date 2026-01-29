package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.OrderErrorCode;

public class OrderException extends BaseAbstractException {

    public OrderException(OrderErrorCode errorCode) {
        super(errorCode);
    }

    public OrderException(OrderErrorCode errorCode, Object... messageArgs) {
        super(errorCode, messageArgs);
    }

}
