package com.myecommerce.MyECommerce.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JasyptConfigAESTest {

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