package com.myecommerce.MyECommerce.config;

import com.myecommerce.MyECommerce.security.exception.handler.CustomAccessDeniedHandler;
import com.myecommerce.MyECommerce.security.exception.handler.CustomAuthenticationEntryPoint;
import com.myecommerce.MyECommerce.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT 를 이용한 인증을 위한 필터
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 스프링 시큐리티 인증,인가 예외처리 핸들러
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    /** 인증관련설정: 자원별 접근권한 설정 */
    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 기본 로그인 페이지 비활성화
                .httpBasic(HttpBasicConfigurer::disable)

                // CSRF 보호를 비활성화
                // : HTTP POST로 직접 엔드포인트 호출 시 기본적으로는 CSRF 보호를 비활성화 필요.
                .csrf(AbstractHttpConfigurer::disable)

                // JWT 토큰으로 사용자 인증 진행하기에 웹 서버가 사용자의 상태를 계속해서 기억하지 않도록 함.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 접근경로허가
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                            // 회원가입, 로그인 경로에 대해 모든인원 접근허용
                            .requestMatchers("/member/signup/**", "/member/signin/**").permitAll()
                            // 그 외 경로에 대해 인증필요
                            .anyRequest().authenticated())

                // ID, Password 기반 유저 인증 처리하는 UsernamePasswordAuthenticationFilter 필터 호출 전에
                // jwt 토큰  -> 시큐리티 컨텍스트 인증정보로 변환을 위한 사용자정의 jwtAuthenticationFilter 필터 추가.
                .addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 스프링 시큐리티 예외처리
                .exceptionHandling(exception ->
                        exception
                                .authenticationEntryPoint(this.customAuthenticationEntryPoint)
                                .accessDeniedHandler(this.customAccessDeniedHandler));

        return http.build();
    }

    /** 비밃런호 암호화 구현체 정의 및 빈등록 **/
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
