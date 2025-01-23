package com.myecommerce.MyECommerce.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    /** 인증관련설정: 자원별 접근권한 설정 */
    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호를 비활성화
                // : HTTP POST로 직접 엔드포인트 호출 시 기본적으로는 CSRF 보호를 비활성화 필요.
                .csrf(AbstractHttpConfigurer::disable)
                // 접근경로허가
                .authorizeHttpRequests((authorizeRequests) ->
                        // 모든 경로에 대해 접근허용
                        authorizeRequests.anyRequest().permitAll());
        return http.build();
    }

    /** 비밃런호 암호화 구현체 정의 및 빈등록 **/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
