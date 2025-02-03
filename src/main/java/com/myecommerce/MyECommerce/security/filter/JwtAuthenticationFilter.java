package com.myecommerce.MyECommerce.security.filter;


import com.myecommerce.MyECommerce.config.JwtAuthenticationProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // 토큰 헤더정보
    // ex) Authorization : Bearer 토큰값

    // 1. 토큰정보를 가지는 사용자정의 헤더키
    //    토큰은 HTTP 프로토콜에서 헤더에 포함되는 정보.
    //    어떤 기준으로 토큰을 주고받을지 결정 = 헤더의 키를 결정.
    public static final String TOKEN_HEADER = "Authorization";
    // 2. 인증타입
    //    JWT 토큰을 사용하는 경우에는 토큰 앞에 Bearer를 붙여준다.
    public static final String TOKEN_PREFIX = "Bearer ";

    private final JwtAuthenticationProvider tokenProvider;

    /**
     * 토큰 유효성 검증 필터
     * : token 정보가 유효히면 SecurityContext에 인증정보 추가.
     *   아니면 다음 필터 진행.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // 1. 토큰값 반환
        String token = resolveTokenFromRequest(request);

        // 2. 토큰값 만료시간 유효성 검증
        if (tokenProvider.isValidTokenExpirationTime(token)) {
            // 3. JWT 토큰정보 -> 스프링 시큐리티 인증정보로 변환
            //    : SignInService.loadUserByUsername() 메서드를 통해 조회 후 인증정보 생성
            Authentication auth = tokenProvider.getSpringSecurityAuthentication(token);
            // 4. 시큐리티 컨텍스트에 인증정보 추가
            SecurityContextHolder.getContext().setAuthentication(auth);

            // 사용자 요청 경로 로그 남기기
            log.info(String.format("[%s] -> %s authenticated -> ",
                    tokenProvider.getUserIdFromToken(token),
                    request.getRequestURI()));
        }

        // 다음 필터가 연속적으로 실행될 수 있도록 함.
        // 다음 필터 호출하지 않으면 여기서 멈춤.
        filterChain.doFilter(request, response);
    }

    // 토큰값 반환 (reqeust header에서 토큰정보 get)
    private String resolveTokenFromRequest(HttpServletRequest request) {
        // 헤더 정보에서 key에 해당하는 value 값 반환
        String token = request.getHeader(TOKEN_HEADER);

        // 토큰 유효성 검사
        if(ObjectUtils.isEmpty(token) || !token.startsWith(TOKEN_PREFIX)) {
            return null;
        }

        // prefix를 제외한 실제 JWT 토큰값만 반환
        return token.substring(TOKEN_PREFIX.length());
    }
}
