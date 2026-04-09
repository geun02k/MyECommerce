package com.myecommerce.MyECommerce.utils.generator;

import com.myecommerce.MyECommerce.config.JasyptConfigAES;
import org.jasypt.encryption.StringEncryptor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * [주의] 이 클래스는 테스트 목적이 아닌 설정값 생성을 위한 로컬 도구입니다.
 * application.properties에 "ENC(생성된 값)"을 복사하여 사용하세요.
**/
@DisplayName("[Tool] 설정 정보 암호화 값 생성")
class SecretPropertyGenerator {
    // 암호화가 필요한 평문 정보 (사용자별 상이)
    private static final String DB_USER_NAME = "userName";
    private static final String DB_PASSWORD = "password";

    @Test
    @Disabled("필요할 때만 수동으로 실행하여 암호화 값을 생성하세요.")
    @DisplayName("[Tool] application.properties 암호화 값 추출")
    void generateEncryptedProperties() {
        JasyptConfigAES config = new JasyptConfigAES();
        StringEncryptor encryptor = config.stringEncryptor();

        // 암호화가 필요한 평문 정보
        System.out.println("\n===== Jasypt Encrypted Results =====");
        printEncrypted("spring.datasource.username", DB_USER_NAME, encryptor);
        printEncrypted("spring.datasource.password", DB_PASSWORD, encryptor);
        System.out.println("===== Jasypt Encrypted Results =====\n");
    }

    private void printEncrypted(String key, String plainText, StringEncryptor encryptor) {
        String encryptedText = encryptor.encrypt(plainText);
        System.out.printf("%s=ENC(%s)%n", key, encryptedText);
    }

}