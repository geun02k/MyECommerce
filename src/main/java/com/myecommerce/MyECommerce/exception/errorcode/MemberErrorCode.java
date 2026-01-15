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
    MEMBER_ALREADY_REGISTERED(HttpStatus.CONFLICT.value(),
            "error.member.already.registered"),

    // 비밀번호 validation check
    PASSWORD_LENGTH_LIMITED(HttpStatus.BAD_REQUEST.value(),
            "error.member.password.length.limited"),

    // 전화번호 validation check
    TELEPHONE_ALREADY_REGISTERED(HttpStatus.CONFLICT.value(),
            "error.member.telephone.already.registered"),

    // login validation check
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(),
            "error.member.user.not.found"),
    PASSWORD_MISMATCHED(HttpStatus.BAD_REQUEST.value(),
            "error.member.password.mismatched"),

    // logout validation check
    USER_ALREADY_SIGNED_OUT(HttpStatus.CONFLICT.value(),
            "error.member.user.already.signed.out")
    ;


    private final int statusCode;
    private final String errorMessage;
}
