package com.myecommerce.MyECommerce.config;

import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {

    @Value("${spring.jwt.secret-key}")
    private String SECRET_KEY;
    private static final long TOKEN_VALID_TIME = 1000L * 60 * 60 * 24;

    /** 토큰생성 */
    public String createToken(Long id, String userPk, String name, List<MemberAuthority> authorities) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(id))
                .claim("userId", userPk)
                .claim("name", name)
                .claim("authorities", authorities)
                .issuedAt(now) // 발급시간
                .expiration(new Date(now.getTime() + TOKEN_VALID_TIME)) // 만료시간
                .signWith(getDecodedSecretKey()) // 서명 (HMAC SHA-256 알고리즘 이용해 서명)
                .compact(); // 최종적으로 JWT 문자열 생성
    }

    // JWT 서명 검증을 위한 비밀키 생성
    private SecretKey getDecodedSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}