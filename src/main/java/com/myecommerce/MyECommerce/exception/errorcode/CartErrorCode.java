package com.myecommerce.MyECommerce.exception.errorcode;


import com.myecommerce.MyECommerce.service.cart.CartService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CartErrorCode implements CommonErrorCode {

    // 장바구니 validation check
    LIMIT_CART_MAX_SIZE(HttpStatus.BAD_REQUEST.value(), "장바구니에는 최대 " + CartService.CART_MAX_SIZE + "건만 추가 가능합니다.")
    ;

    private final int statusCode;
    private final String errorMessage;
}
