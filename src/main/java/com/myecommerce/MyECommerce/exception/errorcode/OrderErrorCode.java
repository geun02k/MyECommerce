package com.myecommerce.MyECommerce.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrderErrorCode implements CommonErrorCode {

    // 주문 접근 제한 validation check
    MEMBER_NOT_LOGGED_IN(HttpStatus.UNAUTHORIZED.value(),
            "error.order.member.not.logged.in"),
    ORDER_CUSTOMER_ONLY(HttpStatus.FORBIDDEN.value(),
            "error.order.customer.only"),

    // 주문 validation check
    ORDER_ITEM_REQUEST_DUPLICATED(HttpStatus.BAD_REQUEST.value(),
            "error.order.item.request.duplicated"),
    ORDER_ITEM_MIN_QUANTITY_BELOW(HttpStatus.BAD_REQUEST.value(),
            "error.order.item.min.quantity.below"),
    ORDER_ITEM_MAX_QUANTITY_EXCEEDED(HttpStatus.BAD_REQUEST.value(), // 정책기반 수량제한 (고정값)
            "error.order.item.max.quantity.exceeded"),
    ORDER_COUNT_EXCEEDED(HttpStatus.CONFLICT.value(),
            "error.order.count.exceeded"),
    PRODUCT_NOT_REGISTERED(HttpStatus.BAD_REQUEST.value(),
            "error.order.product.not.registered"),
    PRODUCT_OPTION_NOT_REGISTERED(HttpStatus.BAD_REQUEST.value(),
            "error.order.product.option.not.registered"),
    PRODUCT_OPTION_OUT_OF_STOCK(HttpStatus.CONFLICT.value(),
            "error.order.product.option.out.of.stock"),
    ORDER_AVAILABLE_QUANTITY_EXCEEDED(HttpStatus.CONFLICT.value(), // 재고/상태기반 수량제한 (가변값)
            "error.order.available.quantity.exceeded"),

    // 주문 물품 validation check
    ITEM_ALREADY_ORDERED(HttpStatus.CONFLICT.value(),
            "error.order.item.already_ordered"),
    ORDER_ITEM_PRICE_INVALID(HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error.order.item.price.invalid"),
    ;

    private final int statusCode;
    private final String errorMessage;
}
