package com.myecommerce.MyECommerce.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements CommonErrorCode {

    // 결제 validation check
    ORDER_STATUS_NOT_CREATED(HttpStatus.CONFLICT.value(),
            "error.payment.order.status.not.created"),

    // PG 결제 validation check
    PG_REQUEST_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error.payment.pg.request.failed"),
    PG_RESPONSE_TRANSACTION_ID_NOT_EXISTS(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error.payment.pg.response.transaction.not.exists"),
    PAYMENT_STATUS_NOT_READY(HttpStatus.CONFLICT.value(),
            "error.payment.status.not.ready"),
    PAYMENT_STATUS_NOT_IN_PROGRESS(HttpStatus.CONFLICT.value(),
            "error.payment.status.not.in.progress"),
    PAYMENT_AMOUNT_INCONSISTENCY(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error.payment.amount.inconsistency")
    ;

    private final int statusCode;
    private final String errorMessage;
}
