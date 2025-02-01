package com.myecommerce.MyECommerce.config;

import com.myecommerce.MyECommerce.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT 를 이용한 인증을 위한 필터
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /** 인증관련설정: 자원별 접근권한 설정 */
    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호를 비활성화
                // : HTTP POST로 직접 엔드포인트 호출 시 기본적으로는 CSRF 보호를 비활성화 필요.
                .csrf(AbstractHttpConfigurer::disable)
                // 접근경로허가
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                            // 회원가입, 로그인 경로에 대해 모든인원 접근허용
                            .requestMatchers("/member/signup/**", "/member/signin/**").permitAll()
                            // 그 외 경로에 대해 인증필요
                            .anyRequest().authenticated())
                // ID, Password 기반 유저 인증 처리하는 UsernamePasswordAuthenticationFilter 필터 호출 전에
                // jwt 토큰  -> 시큐리티 컨텍스트 인증정보로 변환을 위한 사용자정의 jwtAuthenticationFilter 필터 추가.
                .addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /** 비밃런호 암호화 구현체 정의 및 빈등록 **/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
