package com.myecommerce.MyECommerce.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SpringSecurityErrorCode implements CommonErrorCode {

    // login validation check
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(),
            "error.spring.security.invalid.token"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN.value(),
            "error.spring.security.access.denied")
    ;

    private final int statusCode;
    private final String errorMessage;
}
