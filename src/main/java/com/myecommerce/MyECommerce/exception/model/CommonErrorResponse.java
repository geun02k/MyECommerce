package com.myecommerce.MyECommerce.exception.model;

import com.myecommerce.MyECommerce.exception.errorcode.CommonErrorCode;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonErrorResponse {

    // HTTP 상태코드
    private int statusCode;
    // 발생에러코드
    private CommonErrorCode errorCode;
    // 예외메세지
    private String errorMessage;

}