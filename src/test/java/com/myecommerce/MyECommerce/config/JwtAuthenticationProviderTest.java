package com.myecommerce.MyECommerce.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.type.MemberAuthorityType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationProviderTest {

    @InjectMocks
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    // 실제 테스트에 사용할 암호화된 비밀키
    private static final String secretKey =
            "wHxn0kbl6XB8v/KL3m+vnO0cRY3fzkj0OW9xnzNzQgE=";
    // JWT 토큰 생성을 위한 평문 비밀키
    private SecretKey decodedSecretKey;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // JwtAuthenticationProvider의 SECRET_KEY 필드를 수동으로 설정
        // ReflectionTestUtils를 사용하여 private 필드에 접근하고 값을 설정
        ReflectionTestUtils.setField(
                jwtAuthenticationProvider, "SECRET_KEY", secretKey);
    }

    @Test
    @DisplayName("JWT토큰생성 성공")
    void successCreateTokenTest() {
        // given
        Long id = 1L;
        String userPk = "testUser";
        String name = "테스트유저";
        List<MemberAuthority> authorities = Arrays.asList(
                MemberAuthority.builder()
                        .id(1L)
                        .authority(MemberAuthorityType.SELLER)
                        .build()); // 예시로 권한을 하나 추가
        // private 메서드 호출을 위한 ReflectionTestUtils
        // getDecodedSecretKey() 메서드를 직접 호출하여 SecretKey를 생성
        decodedSecretKey = ReflectionTestUtils.invokeMethod(
                jwtAuthenticationProvider, "getDecodedSecretKey");

        // when
        // 실제 토큰을 생성
        String token = jwtAuthenticationProvider.createToken(
                id, userPk, name, authorities);
        // System.out.println(token);

        // then
        // JWT 생성 과정에서 토큰이 실제로 생성되었는지 확인
        // JWT를 디코딩하여 내용이 올바른지 검사
        Claims claims = Jwts.parser()
                .setSigningKey(decodedSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 토큰이 null이 아니어야 함을 검증
        ObjectMapper objectMapper = new ObjectMapper();
        // claims.get("authorities")는 List<Map> 형태일 것으로 예상
        List<Map<String, Object>> authoritiesMaps =
                (List<Map<String, Object>>) claims.get("authorities");
        // List<Map<String, Object>> -> List<MemberAuthority>로 변환
        List<MemberAuthority> authorityList =
                objectMapper.convertValue(authoritiesMaps,
                        // 제네릭 타입을 List<MemberAuthority>로 설정
                        objectMapper.getTypeFactory()
                                .constructCollectionType(List.class, MemberAuthority.class));

        assertNotNull(token, "Token should not be null");
        assertEquals(String.valueOf(id), claims.getSubject()); // id
        assertEquals(userPk, claims.get("userId").toString()); // userId
        assertEquals(name, claims.get("name").toString()); // name
        assertEquals(1, authorityList.size());
        assertEquals(MemberAuthorityType.SELLER, authorityList.get(0).getAuthority());
    }

    @Test
    @DisplayName("JWT비밀키생성 성공")
    void successDecodeSecretKeyTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // given
        // JwtAuthenticationProvider 클래스에서 getDecodedSecretKey 메서드를 찾기
        Method method = JwtAuthenticationProvider.class.getDeclaredMethod("getDecodedSecretKey");
        // private 메서드 접근을 허용
        method.setAccessible(true);

        // when
        // JwtAuthenticationProvider 인스턴스에서 getDecodedSecretKey 메서드 호출
        SecretKey secretKey = (SecretKey) method.invoke(jwtAuthenticationProvider);

        // then
        assertNotNull(secretKey, "SecretKey should not be null");
        assertInstanceOf(SecretKey.class, secretKey, "Returned object should be an instance of SecretKey");
    }
}