package com.myecommerce.MyECommerce.exception.handler;

import com.myecommerce.MyECommerce.exception.BaseAbstractException;

import com.myecommerce.MyECommerce.exception.errorcode.DefaultErrorCode;
import com.myecommerce.MyECommerce.exception.errorcode.SpringSecurityErrorCode;
import com.myecommerce.MyECommerce.exception.model.CommonErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Locale;
import java.util.Objects;

import static com.myecommerce.MyECommerce.exception.errorcode.DefaultErrorCode.INTERNAL_SERVER_ERROR;
import static com.myecommerce.MyECommerce.exception.errorcode.DefaultErrorCode.INVALID_VALUE;
import static com.myecommerce.MyECommerce.exception.errorcode.SpringSecurityErrorCode.ACCESS_DENIED;
import static com.myecommerce.MyECommerce.exception.errorcode.SpringSecurityErrorCode.INVALID_TOKEN;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class CommonExceptionHandler {

    private final MessageSource messageSource;
    private static final Locale DEFAULT_LOCALE = Locale.KOREAN;

    /** 사용자정의예외 예외처리 **/
    @ExceptionHandler(BaseAbstractException.class)
    protected ResponseEntity<CommonErrorResponse> commonHandler(BaseAbstractException e) {
        // 에러 메시지 생성
        String errorMessage = createMessage(
                e.getErrorCode().getErrorMessage(), e.getMessageArgs());

        // 응답 객체 생성
        CommonErrorResponse errorResponse = CommonErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .errorMessage(errorMessage)
                .build();

        log.warn(e.getErrorCode().getErrorMessage(), e);

        // 응답 객체 반환
        // HttpStatus에 상태코드를 담아서 errorResponse와 함께 Http 응답으로 내려보낸다.
        return new ResponseEntity<>(errorResponse,
                Objects.requireNonNull(HttpStatus.resolve(e.getErrorCode().getStatusCode())));
    }

    /** DTO 유효성검사 예외처리 **/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<CommonErrorResponse> methodArgumentNotValidExceptionHandler(
            MethodArgumentNotValidException e) {
        // 서버에러객체생성
        DefaultErrorCode errorCode = INVALID_VALUE;
        // 에러 메시지 생성
        String errorCodeMessage = createMessage(errorCode.getErrorMessage());
        // 디폴트 에러 메시지 (에러메시지 여러개일 수 있음, BindingResult에서 가져옴)
        // 이건 에러를 하나만 가져오나?
        String defaultErrorMessage = e.getBindingResult().getFieldError().getDefaultMessage() == null ?
                "" : "\n" + e.getBindingResult().getFieldError().getDefaultMessage() ;
//        // 이건 발생한 전체에러 가져오는듯
//        String defaultErrorMessage = Optional.ofNullable(e.getBindingResult().getFieldError())
//                .map(fieldError -> "\n" + messageSource.getMessage(
//                        fieldError, LocaleContextHolder.getLocale()))
//                .orElse("");

        // 응답 객체 생성
        CommonErrorResponse errorResponse = CommonErrorResponse.builder()
                .errorCode(errorCode) // DefaultErrorCode.INVALID_VALUE
                .errorMessage(errorCodeMessage + defaultErrorMessage)
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
        // 에러 메시지 생성
        String errorCodeMessage = createMessage(errorCode.getErrorMessage());
        String errorMessage = e.getConstraintViolations().stream()
                .findFirst()
                .map(ConstraintViolation::getMessage)
                .orElse(errorCodeMessage);

        // 응답 객체 생성
        CommonErrorResponse errorResponse = CommonErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
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
        // 에러 메시지 생성
        String errorMessage = createMessage(errorCode.getErrorMessage());

        // 응답 객체 생성
        CommonErrorResponse errorResponse = CommonErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
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
        // 에러 메시지 생성
        String errorMessage = createMessage(errorCode.getErrorMessage());

        // 응답 객체 생성
        CommonErrorResponse errorResponse = CommonErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
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
        // 에러 메시지 생성
        String errorMessage = createMessage(errorCode.getErrorMessage());

        // 응답 객체 생성
        CommonErrorResponse errorResponse = CommonErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();

        log.warn(errorCode.getErrorMessage(), e);

        // 응답 객체 반환
        // HttpStatus에 상태코드를 담아서 errorResponse와 함께 Http 응답으로 내려보낸다.
        return new ResponseEntity<>(errorResponse,
                Objects.requireNonNull(HttpStatus.resolve(errorCode.getStatusCode())));
    }

    // 전달인자 포함 메시지 생성
    private String createMessage(String messageKey, Object... messageArgs) {
        return messageSource.getMessage(
                messageKey,
                messageArgs,
                DEFAULT_LOCALE
        );
    }

}

