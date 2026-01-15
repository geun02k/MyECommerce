package com.myecommerce.MyECommerce;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import java.util.Locale;

@SpringBootTest
public class MessageSourceTest {

    @Autowired
    MessageSource messageSource;

    @Test
    @DisplayName("messages.properties key로 메시지 조회 성공")
    public void message_key_is_resolved() {
        // given
        // when
        String message = messageSource.getMessage(
                "error.cart.cart.size.exceeded",
                new Object[]{50},
                Locale.getDefault()
        );

        // then
        Assertions.assertTrue(message.contains("장바구니에는 최대 50건만 추가 가능합니다."));
    }
}
