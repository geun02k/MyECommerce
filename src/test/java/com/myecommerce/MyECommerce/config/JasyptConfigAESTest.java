package com.myecommerce.MyECommerce.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JasyptConfigAESTest {
    // 단위 테스트로서의 의미
    // StandardPBEStringEncryptor 그 자체가 라이브러리로서 잘 동작하는지,
    // 내가 설정한 PBEWithHMACSHA512AndAES_256 알고리즘이 현재 환경(JDK 등)에서 지원되는지 등 확인가능
    @Test
    @DisplayName("jasypt문자열암호화 성공")
    void successStringEncrypting() {
        // given
        String value = "test-value";
        // when
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("my_test_secret_key");
        encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
        encryptor.setIvGenerator(new RandomIvGenerator());

        String encryptedValue = encryptor.encrypt(value);

        // then
        assertNotEquals(value, encryptedValue);
    }

}