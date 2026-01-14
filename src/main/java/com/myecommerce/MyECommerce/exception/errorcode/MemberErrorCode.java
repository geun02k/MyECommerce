package com.myecommerce.MyECommerce.exception.errorcode;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements CommonErrorCode {

    // 회원 객체 validation check
    EMPTY_MEMBER_INFO(HttpStatus.BAD_REQUEST.value(),
            "error.member.empty.member.info"),

    // 회원ID validation check
    ALREADY_REGISTERED_MEMBER(HttpStatus.BAD_REQUEST.value(),
            "error.member.already.registered.member"),

    // 사용자명 validation check

    // 비밀번호 validation check
    LIMIT_PASSWORD_CHARACTERS_FROM_8_TO_100(HttpStatus.BAD_REQUEST.value(),
            "error.member.limit.password.characters.from.8.to.100"),

    // 전화번호 validation check
    ALREADY_REGISTERED_PHONE_NUMBER(HttpStatus.BAD_REQUEST.value(),
            "error.member.already.registered.phone.number"),

    // login validation check
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST.value(),
            "error.member.user.not.found"),
    MISMATCH_PASSWORD(HttpStatus.BAD_REQUEST.value(),
            "error.member.mismatch.password"),

    // logout validation check
    ALREADY_SIGN_OUT_USER(HttpStatus.BAD_REQUEST.value(),
            "error.member.already.sign.out.user")
    ;


    private final int statusCode;
    private final String errorMessage;
}
