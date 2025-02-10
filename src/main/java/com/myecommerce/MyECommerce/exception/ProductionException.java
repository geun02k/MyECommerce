package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.ProductionErrorCode;
import lombok.Getter;

@Getter
public class ProductionException extends BaseAbstractException {

    private final ProductionErrorCode errorCode;
    private final String errorMessage;
    private final int statusCode;

    public ProductionException(ProductionErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getErrorMessage();
        this.statusCode = errorCode.getStatusCode();
    }

}
