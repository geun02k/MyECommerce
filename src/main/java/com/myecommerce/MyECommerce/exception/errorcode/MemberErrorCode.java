package com.myecommerce.MyECommerce.exception.errorcode;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements CommonErrorCode {

    // 회원 객체 validation check
    EMPTY_MEMBER_INFO(HttpStatus.BAD_REQUEST.value(), "회원정보가 존재하지 않습니다."),

    // 회원ID validation check
    ALREADY_REGISTERED_MEMBER(HttpStatus.BAD_REQUEST.value(), "이미 존재하는 회원입니다."),

    // 사용자명 validation check
    LIMIT_NAME_CHARACTERS_FROM_1_TO_50(HttpStatus.BAD_REQUEST.value(), "사용자명 길이는 최소 1자, 최대 50자로 제한됩니다."),
    ONLY_SUPPORT_ENGLISH_AND_KOREAN(HttpStatus.BAD_REQUEST.value(), "사용자명은 영문자, 한글만 사용 가능합니다."),

    // 비밀번호 validation check
    LIMIT_PASSWORD_CHARACTERS_FROM_8_TO_100(HttpStatus.BAD_REQUEST.value(), "비밀번호는 최소 8자 이상 최대 100자 이하입니다."),

    // 전화번호 validation check
    INVALID_PHONE_NUMBER(HttpStatus.BAD_REQUEST.value(), "유효하지 않은 전화번호입니다."),
    ALREADY_REGISTERED_PHONE_NUMBER(HttpStatus.BAD_REQUEST.value(), "이미 등록된 전화번호입니다.")
    ;

    private final int statusCode;
    private final String errorMessage;
}
