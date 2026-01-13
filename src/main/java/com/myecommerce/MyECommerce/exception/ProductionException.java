package com.myecommerce.MyECommerce.exception;

import com.myecommerce.MyECommerce.exception.errorcode.ProductionErrorCode;
import lombok.Getter;

@Getter
public class ProductionException extends BaseAbstractException {

    private final ProductionErrorCode errorCode;

    public ProductionException(ProductionErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
