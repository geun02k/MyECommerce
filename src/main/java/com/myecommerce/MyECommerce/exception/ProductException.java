package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.ProductErrorCode;

public class ProductException extends BaseAbstractException {

    public ProductException(ProductErrorCode errorCode) {
        super(errorCode);
    }

    public ProductException(ProductErrorCode errorCode, Object... messageArgs) {
        super(errorCode, messageArgs);
    }
}
