package com.myecommerce.MyECommerce.controller.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableMethodSecurity
public class TestSecurityConfig {
    /** 인증관련설정: 자원별 접근권한 설정 */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호를 비활성화
                // : HTTP POST로 직접 엔드포인트 호출 시 기본적으로는 CSRF 보호를 비활성화 필요.
                .csrf(AbstractHttpConfigurer::disable)

                // 접근경로허가
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                // 회원가입, 로그인 경로에 대해 모든인원 접근허용
                                .requestMatchers("/member/signup/**",
                                        "/member/signin/**").permitAll()
                                .requestMatchers(HttpMethod.GET,  "/production").permitAll()
                                .requestMatchers(HttpMethod.GET,  "/production/*").permitAll()
                                // 그 외 경로에 대해 인증필요
                                .anyRequest().authenticated());

        return http.build();
    }
}
