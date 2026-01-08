package com.myecommerce.MyECommerce.exception.handler;

import com.myecommerce.MyECommerce.exception.BaseAbstractException;
import com.myecommerce.MyECommerce.exception.errorcode.DefaultErrorCode;
import com.myecommerce.MyECommerce.exception.errorcode.SpringSecurityErrorCode;
import com.myecommerce.MyECommerce.exception.model.CommonErrorResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;

import static com.myecommerce.MyECommerce.exception.errorcode.DefaultErrorCode.INTERNAL_SERVER_ERROR;
import static com.myecommerce.MyECommerce.exception.errorcode.DefaultErrorCode.INVALID_VALUE;
import static com.myecommerce.MyECommerce.exception.errorcode.SpringSecurityErrorCode.ACCESS_DENIED;
import static com.myecommerce.MyECommerce.exception.errorcode.SpringSecurityErrorCode.INVALID_TOKEN;

@Slf4j
@ControllerAdvice
public class CommonExceptionHandler {

    /** 사용자정의예외 예외처리 **/
    @ExceptionHandler(BaseAbstractException.class)
    protected ResponseEntity<CommonErrorResponse> commonHandler(BaseAbstractException e) {
        // 응답 객체 생성
        CommonErrorResponse errorResponse = CommonErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .errorMessage(e.getErrorMessage())
                .build();

        log.warn(e.getErrorMessage(), e);

        // 응답 객체 반환
        // HttpStatus에 상태코드를 담아서 errorResponse와 함께 Http 응답으로 내려보낸다.
        return new ResponseEntity<>(errorResponse,
                Objects.requireNonNull(HttpStatus.resolve(e.getStatusCode())));
    }

    /** DTO 유효성검사 예외처리 **/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<CommonErrorResponse> methodArgumentNotValidExceptionHandler(
            MethodArgumentNotValidException e) {
        // 서버에러객체생성
        DefaultErrorCode errorCode = INVALID_VALUE;
        // 디폴트 에러 메시지 (에러메시지 여러개일 수 있음, BindingResult에서 가져옴)
        String defaultErrorMessage = e.getBindingResult().getFieldError().getDefaultMessage() == null ?
                "" : "\n" + e.getBindingResult().getFieldError().getDefaultMessage() ;

        // 응답 객체 생성
        CommonErrorResponse errorResponse = CommonErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorCode.getErrorMessage() + defaultErrorMessage)
                .build();

        log.warn(errorCode.getErrorMessage(), e);

        // 응답 객체 반환
        // HttpStatus에 상태코드를 담아서 errorResponse와 함께 Http 응답으로 내려보낸다.
        return new ResponseEntity<>(errorResponse,
                Objects.requireNonNull(HttpStatus.resolve(errorCode.getStatusCode())));
    }

    /** Bean Validation 단계에서 발생한 제약 위반 예외처리 **/
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<CommonErrorResponse> constraintViolationExceptionHandler(
            ConstraintViolationException e) {
        // 서버에러객체생성
        DefaultErrorCode errorCode = INVALID_VALUE;
        // 응답 객체 생성
        CommonErrorResponse errorResponse = CommonErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorCode.getErrorMessage())
                .build();

        log.warn(errorCode.getErrorMessage(), e);

        // 응답 객체 반환
        // HttpStatus에 상태코드를 담아서 errorResponse와 함께 Http 응답으로 내려보낸다.
        return new ResponseEntity<>(errorResponse,
                Objects.requireNonNull(HttpStatus.resolve(errorCode.getStatusCode())));
    }

    /** 스프링 시큐리티 인증 예외처리 **/
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CommonErrorResponse> handleAuthenticationException(
            AuthenticationException e) {
        // 서버에러객체생성
        SpringSecurityErrorCode errorCode = INVALID_TOKEN;

        // 응답 객체 생성
        CommonErrorResponse errorResponse = CommonErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorCode.getErrorMessage())
                .build();

        log.warn(errorCode.getErrorMessage(), e);

        // 응답 객체 반환
        // HttpStatus에 상태코드를 담아서 errorResponse와 함께 Http 응답으로 내려보낸다.
        return new ResponseEntity<>(errorResponse,
                Objects.requireNonNull(HttpStatus.resolve(errorCode.getStatusCode())));
    }

    /** 스프링 시큐리티 인가 예외처리 **/
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CommonErrorResponse> handleAccessDeniedException(
            AccessDeniedException e) {
        // 서버에러객체생성
        SpringSecurityErrorCode errorCode = ACCESS_DENIED;

        // 응답 객체 생성
        CommonErrorResponse errorResponse = CommonErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorCode.getErrorMessage())
                .build();

        log.warn(errorCode.getErrorMessage(), e);

        // 응답 객체 반환
        // HttpStatus에 상태코드를 담아서 errorResponse와 함께 Http 응답으로 내려보낸다.
        return new ResponseEntity<>(errorResponse,
                Objects.requireNonNull(HttpStatus.resolve(errorCode.getStatusCode())));
    }

    /** 기타 예외처리 **/
    @ExceptionHandler
    protected ResponseEntity<CommonErrorResponse> defaultHandler(Exception e) {
        // 서버에러객체생성
        DefaultErrorCode errorCode = INTERNAL_SERVER_ERROR;

        // 응답 객체 생성
        CommonErrorResponse errorResponse = CommonErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorCode.getErrorMessage())
                .build();

        log.warn(errorCode.getErrorMessage(), e);

        // 응답 객체 반환
        // HttpStatus에 상태코드를 담아서 errorResponse와 함께 Http 응답으로 내려보낸다.
        return new ResponseEntity<>(errorResponse,
                Objects.requireNonNull(HttpStatus.resolve(errorCode.getStatusCode())));
    }
}

