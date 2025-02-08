package com.myecommerce.MyECommerce.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidValidator implements ConstraintValidator<EnumValid, Enum> {

    private EnumValid annotation;
    
    // isValid(Object, ConstraintValidatorContext) 호출을 준비하기 위해 검증기를 초기화
    // 주어진 제약 선언에 대한 제약 애너테이션이 전달
    @Override
    public void initialize(EnumValid constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    // 실제 Validation에 사용할 검증코드 구현.
    @Override
    public boolean isValid(Enum value, ConstraintValidatorContext context) {
        boolean isValid = false;
        // 데이터로 뭐가 담겨올지 모르니 Object료 선언
        Object[] enumValues = this.annotation.enumClass().getEnumConstants();

        if(enumValues != null) {
            for(Object enumValue : enumValues) {
                if(value == enumValue) {
                    isValid = true;
                    break;
                }
            }
        }

        return isValid;
    }
}
