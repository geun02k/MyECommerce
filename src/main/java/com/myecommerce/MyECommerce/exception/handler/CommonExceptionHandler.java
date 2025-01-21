package com.myecommerce.MyECommerce.exception.handler;

import com.myecommerce.MyECommerce.exception.BaseAbstractException;
import com.myecommerce.MyECommerce.exception.model.CommonErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<CommonErrorResponse> commonHandler(BaseAbstractException e) {
        // 응답 객체 생성
        CommonErrorResponse errorResponse = CommonErrorResponse.builder()
                .statusCode(e.getStatusCode())
                .errorCode(e.getErrorCode())
                .errorMessage(e.getErrorMessage())
                .build();

        // 응답 객체 반환
        // HttpStatus에 상태코드를 담아서 errorResponse와 함께 Http 응답으로 내려보낸다.
        return new ResponseEntity<>(errorResponse,
                Objects.requireNonNull(HttpStatus.resolve(e.getStatusCode())));
    }
}

