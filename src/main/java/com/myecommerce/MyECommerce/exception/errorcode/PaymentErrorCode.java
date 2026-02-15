package com.myecommerce.MyECommerce.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements CommonErrorCode {

    // 결제 접근 제한 validation check
    MEMBER_NOT_LOGGED_IN(HttpStatus.UNAUTHORIZED.value(),
            "error.payment.member.not.logged.in"),
    PAYMENT_CUSTOMER_ONLY(HttpStatus.FORBIDDEN.value(),
            "error.payment.customer.only"),
    PAYMENT_ACCESS_AVAILABLE_ONLY_BUYER(HttpStatus.FORBIDDEN.value(),
            "error.payment.available.only.buyer"),

    // 결제 validation check
    PAYMENT_ORDER_NOT_EXISTS(HttpStatus.BAD_REQUEST.value(),
            "error.payment.order.not.exists"),
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
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error.payment.amount.mismatch"),
    PAYMENT_ALREADY_COMPLETED(HttpStatus.CONFLICT.value(),
            "error.payment.already.completed"),
    PG_TRANSACTION_ID_NOT_EXISTS(HttpStatus.BAD_REQUEST.value(),
            "error.payment.pg.transaction.id.not.exists"),
    PAYMENT_NOT_APPROVED(HttpStatus.CONFLICT.value(),
            "error.payment.not.approved"),
    PG_TRANSACTION_ID_MISMATCH(HttpStatus.CONFLICT.value(),
            "error.payment.pg.transaction.id.mismatch"),
    PG_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error.payment.pg.api.error")
    ;

    private final int statusCode;
    private final String errorMessage;
}
