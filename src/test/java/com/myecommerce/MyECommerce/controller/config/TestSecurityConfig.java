package com.myecommerce.MyECommerce.controller.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestSecurityConfig {
    /** 인증관련설정: 자원별 접근권한 설정 */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호를 비활성화
                // : HTTP POST로 직접 엔드포인트 호출 시 기본적으로는 CSRF 보호를 비활성화 필요.
                .csrf(AbstractHttpConfigurer::disable)

                // 접근경로허가
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().permitAll());

        return http.build();
    }
}
