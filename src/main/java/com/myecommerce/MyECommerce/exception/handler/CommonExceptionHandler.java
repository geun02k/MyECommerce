package com.myecommerce.MyECommerce.exception.handler;

import com.myecommerce.MyECommerce.exception.BaseAbstractException;
import com.myecommerce.MyECommerce.exception.errorcode.DefaultErrorCode;
import com.myecommerce.MyECommerce.exception.model.CommonErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

import static com.myecommerce.MyECommerce.exception.errorcode.DefaultErrorCode.INTERNAL_SERVER_ERROR;

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

    @ExceptionHandler
    protected ResponseEntity<CommonErrorResponse> defaultHandler(Exception e) {
        // 서버에러객체생성
        DefaultErrorCode errorCode = INTERNAL_SERVER_ERROR;

        // 응답 객체 생성
        CommonErrorResponse errorResponse = CommonErrorResponse.builder()
                .statusCode(errorCode.getStatusCode())
                .errorCode(errorCode)
                .errorMessage(errorCode.getErrorMessage())
                .build();

        // 응답 객체 반환
        // HttpStatus에 상태코드를 담아서 errorResponse와 함께 Http 응답으로 내려보낸다.
        return new ResponseEntity<>(errorResponse,
                Objects.requireNonNull(HttpStatus.resolve(errorCode.getStatusCode())));
    }
}

