package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.ProductionErrorCode;

public class ProductionException extends BaseAbstractException {

    public ProductionException(ProductionErrorCode errorCode) {
        super(errorCode);
    }

    public ProductionException(ProductionErrorCode errorCode, Object... messageArgs) {
        super(errorCode, messageArgs);
    }
}
