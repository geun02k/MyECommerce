package com.myecommerce.MyECommerce.config;

import com.myecommerce.MyECommerce.dto.MemberDto;
import com.myecommerce.MyECommerce.service.member.SignInAuthenticationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {

    @Value("${spring.jwt.secret-key}")
    private String SECRET_KEY;
    private static final long TOKEN_VALID_TIME = 1000L * 60 * 60 * 24;

    private final SignInAuthenticationService authenticationService;

    /** 토큰생성 */
    public String createToken(MemberDto member) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(member.getId()))
                .claim("userId", member.getUserId())
                .claim("name", member.getName())
                .claim("roles", member.getRoles())
                .issuedAt(now) // 발급시간
                .expiration(new Date(now.getTime() + TOKEN_VALID_TIME)) // 만료시간
                .signWith(getDecodedSecretKey()) // 서명 (HMAC SHA-256 알고리즘 이용해 서명)
                .compact(); // 최종적으로 JWT 문자열 생성
    }

    /**
     * 토큰 만료시간 유효성 검증 (토큰의 페이로드 정보 추출해 만료여부확인)
     * @param token 토큰값
     * @return true/false
     */
    public boolean checkTokenExpirationTime(String token) {
        // 토큰값이 비어있으면 유효하지 않은 토큰 -> false 반환
        if(!StringUtils.hasText(token)) return false;

        try {
            return !Objects.requireNonNull(parseClaims(token))
                    .getExpiration().before(new Date());

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * JWT토큰정보 -> 스프링시큐리티 인증정보로 변환
     * @param jwt JWT 토큰
     * @return Authentication 스프링에서 지원해주는 형태의 토큰 반환
     */
    @Transactional
    public Authentication getSpringSecurityAuthentication(String jwt) {
        // 사용자 조회
        // : 다형성을 이용해 부모클래스가 자식클래스를 담고있다.
        //   즉 반환값으로 Member 객체를 담아 반환한다.
        //   그렇기 때문에 Member 클래스에서
        //   부모메서드를 오버라이딩한 getAuthorities() 메서드를 호출하게 된다.
        UserDetails userDetails =
                authenticationService.loadUserByUsername(getUserIdFromToken(jwt));
        // 스프링에서 지원해주는 형태의 토큰으로 변경.
        // 유저정보, 비밀번호, 권한정보 전달. (토큰정보에 비밀번호 포함하지 않도록 했으므로 비밀번호 빈값)
        // 권한정보는
        return new UsernamePasswordAuthenticationToken(
                userDetails, "", userDetails.getAuthorities());
    }

    // JWT 서명 검증을 위한 비밀키 생성
    private SecretKey getDecodedSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // request header에서 토큰정보 가져오기
    public String parseToken(String authorization) {
        // 인증타입
        final String TOKEN_PREFIX = "Bearer ";

        // 토큰 유효성 검사
        if(ObjectUtils.isEmpty(authorization) || !authorization.startsWith(TOKEN_PREFIX)) {
            return null;
        }

        // prefix를 제외한 실제 JWT 토큰값만 반환
        return authorization.substring(TOKEN_PREFIX.length());
    }

    // 토큰으로부터 클레임정보 가져오기
    private Claims parseClaims(String token) {
        try {
            // Jwts.parser()로 토큰을 열기위한 도구 세팅!
            // (파서)verifyWith(key)로 서명을 검증할 키를 설정하고
            // (키)build()로 최종적으로 토큰을 열기위한 도구 세팅 완료
            // (JwtParser 객체 생성)parseSignedClaims(jwt)로 jwt문자열을 열어서
            // 서명이 잘 되었는지 확인!
            return Jwts.parser()
                    .verifyWith(getDecodedSecretKey()).build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (Exception e) {
            return null;
        }
    }

    // 토큰에서 회원 userId 추출
    public String getUserIdFromToken(String token) {
        return Objects.requireNonNull(this.parseClaims(token))
                .get("userId", String.class);
    }

    // 토큰에서 만료시간 추출
    public Date getExpirationDateFromToken(String token) {
        return Objects.requireNonNull(this.parseClaims(token)).getExpiration();
    }

}