package com.myecommerce.MyECommerce.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PgError {

    private String code;
    private String message;

}
