package com.myecommerce.MyECommerce.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnumValidValidator.class)
@Documented
public @interface EnumValid {
    // 아래의 메서드는 어노테이션읭 옵션으로 사용됨.

    String message() default "유효하지 않은 도메인입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // enum 타입인 클래스를 얻기위해 선언.
    Class<? extends java.lang.Enum<?>> enumClass();

}
