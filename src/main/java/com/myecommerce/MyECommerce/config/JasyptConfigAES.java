package com.myecommerce.MyECommerce.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableEncryptableProperties //  Spring 환경 전체에서 암호화 가능한 속성을 활성화
public class JasyptConfigAES {

    // jasyptEncryptorAES로 Bean을 등록 -> application.properties의 jasypt bean으로 등록 시 사용.
    @Bean("jasyptEncryptorAES")
    public StringEncryptor stringEncryptor() {
        // PBE (Password Based Encryption)
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        config.setPassword("my-e-commerce-project-jasypt-secret-key"); // 암호화키 (필수)
        config.setAlgorithm("PBEWithHMACSHA512AndAES_256"); // 알고리즘
        config.setKeyObtentionIterations("1000"); // 반복할 해싱 횟수
        config.setPoolSize("1"); // 인스턴스 pool
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); // salt 생성 클래스
        config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
        config.setStringOutputType("base64"); // 인코딩 방식

        encryptor.setConfig(config);

        return encryptor;
    }
}
