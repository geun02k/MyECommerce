package com.myecommerce.MyECommerce.service.member;

import com.myecommerce.MyECommerce.config.JwtAuthenticationProvider;
import com.myecommerce.MyECommerce.dto.member.RequestMemberDto;
import com.myecommerce.MyECommerce.dto.member.RequestSignInMemberDto;
import com.myecommerce.MyECommerce.dto.member.ResponseMemberDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.mapper.MemberMapper;
import com.myecommerce.MyECommerce.repository.member.MemberAuthorityRepository;
import com.myecommerce.MyECommerce.repository.member.MemberRepository;
import com.myecommerce.MyECommerce.service.redis.RedisSingleDataService;
import com.myecommerce.MyECommerce.type.MemberAuthorityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.util.*;

import static com.myecommerce.MyECommerce.type.MemberAuthorityType.SELLER;
import static com.myecommerce.MyECommerce.type.RedisNamespaceType.LOGIN;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private JwtAuthenticationProvider jwtAuthenticationProvider;

    @Mock
    private RedisSingleDataService redisSingleDataService;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberAuthorityRepository memberAuthorityRepository;

    @InjectMocks
    private MemberService memberService;

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 회원 권한 */
    MemberAuthority memberAuthority(MemberAuthorityType memberAuthority) {
        return MemberAuthority.builder()
                .authority(memberAuthority)
                .build();
    }

    /** 회원 Entity */
    Member memberEntity(String encodingPassword,
                        RequestMemberDto requestMemberDto) {
        return Member.builder()
                .password(encodingPassword)
                .userId(requestMemberDto.getUserId())
                .name(requestMemberDto.getName())
                .telephone(requestMemberDto.getTelephone())
                .address(requestMemberDto.getAddress())
                .delYn('N')
                .build();
    }

    /** 회원 응답 */
    ResponseMemberDto responseMemberDto(Member member) {
        return ResponseMemberDto.builder()
                .id(member.getId())
                .password(member.getPassword())
                .userId(member.getUserId())
                .name(member.getName())
                .telephone(member.getTelephone())
                .address(member.getAddress())
                .delYn(member.getDelYn())
                .build();
    }

    /* ---------------------------
        회원가입 성공 Tests
       --------------------------- */

    @Test
    @DisplayName("회원가입 성공 - 회원 권한 및 회원 정보 저장 후 회원 정보 반환")
    void saveMember_shouldSaveMemberAndAuthority_whenValidMember() {
        // given
        String phoneNumber = "01011112222";
        String password = "123456789";
        String encodedPassword = "encode123456789";
        // 회원 권한 (판매자 권한)
        MemberAuthority sellerAuthority = memberAuthority(SELLER);
        // 회원
        RequestMemberDto requestMemberDto = RequestMemberDto.builder()
                .userId("sky")
                .password(password)
                .name("김하늘")
                .telephone(phoneNumber)
                .address("서울 동작구 보라매로5가길 16 보라매아카데미타워 7층")
                .build();

        // 전화번호 중복 조회
        given(memberRepository.findByTelephone(phoneNumber)).willReturn(Optional.empty());
        // 비밀번호 암호화
        given(passwordEncoder.encode(password)).willReturn(encodedPassword);
        // 회원 Dto -> Entity 변환
        Member memberEntity = memberEntity(encodedPassword, requestMemberDto);
        given(memberMapper.toEntity(requestMemberDto)).willReturn(memberEntity);
        // 회원 저장
        given(memberRepository.save(memberEntity))
                .willAnswer(invocation -> invocation.getArgument(0));
        // 회원 권한 저장
        given(memberAuthorityRepository.save(sellerAuthority))
                .willAnswer(invocation -> invocation.getArgument(0));
        // 회원 Entity -> response Dto 변환
        given(memberMapper.toDto(memberEntity)).willReturn(responseMemberDto(memberEntity));

        // when
        ResponseMemberDto response =
                memberService.saveMember(requestMemberDto, List.of(sellerAuthority));

        // then
        // 비밀번호 암호화 검증
        verify(passwordEncoder).encode(password);
        // 회원 및 권한 저장 검증
        verify(memberRepository).save(memberEntity);
        verify(memberAuthorityRepository).save(sellerAuthority);

        // 회원 저장 검증
        assertEquals("sky", memberEntity.getUserId());
        assertEquals(encodedPassword, memberEntity.getPassword());
        assertEquals("김하늘", memberEntity.getName());
        assertEquals("01011112222", memberEntity.getTelephone());
        assertEquals("서울 동작구 보라매로5가길 16 보라매아카데미타워 7층", memberEntity.getAddress());
        assertEquals('N', memberEntity.getDelYn());

        // 회원 권한 저장 검증
        assertEquals(SELLER, sellerAuthority.getAuthority());
        assertSame(memberEntity, sellerAuthority.getMember());

        // 응답 검증
        assertEquals("sky", response.getUserId());
        assertEquals("01011112222", response.getTelephone());
        assertEquals('N', response.getDelYn());
    }

    // TODO: 회원ID 존재 시 예외 발생 (MEMBER_ALREADY_REGISTERED)
    // TODO: 전화번호 정규화 - 전화번호 구분자 포함 시 숫자만 추출하여 정상 저장
    // TODO: 전화번호 길이 - 전화번호 길이 10, 11자리가 아니면 예외 발생 (TELEPHONE_LENGTH_LIMITED)
    // TODO: 전화번호 패턴 - 전화번호 패턴 불일치 시 예외 발생 (TELEPHONE_PATTERN_INVALID)
    // TODO: 전화번호 중복 - 전화번호 중복 등록 시 예외 발생 (TELEPHONE_ALREADY_REGISTERED)
    // TODO: 비밀번호 길이 - 비밀번호 길이가 8자 미만, 100자 초과이면 예외 발생 (PASSWORD_LENGTH_LIMITED)
    // TODO: 이름 trim - 이름에 공백 포함 시 공백 제거 검증

    @Test
    @DisplayName("로그인성공")
    void successSignIn() {
        // given
        Long id = 1L;
        String userId = "sky";
        String password = "12345678";
        String encodedPassword = "encode12345678";
        String name = "김하늘";
        String telephone = "01011112222";
        String address = "서울 동작구 보라매로5가길 16 보라매아카데미타워 7층";
        Character delYn = 'N';

        // 조회할 회원 DTO 객체 생성
        RequestSignInMemberDto member =
                new RequestSignInMemberDto(userId, password);
        // 조회된 회원권한 Entity 객체 생성
        List<MemberAuthority> expectRoleList =
                Collections.singletonList(MemberAuthority.builder()
                .id(id)
                .authority(SELLER)
                .build());

        // 반환될 토큰 값
        String token = "TOKEN";

        long nowTimeMs = new Date().getTime();
        Date expirationDate = new Date(nowTimeMs + (1000L * 60 * 10)); // 현재시간+10분
        long validTimeMs = expirationDate.getTime() - nowTimeMs;

        // stub(가설) : memberRepository.findByUserIdAndDelYn() 실행 시 빈값 반환 예상.
        given(memberRepository.findByUserIdAndDelYn(any(), any()))
                .willReturn(Optional.ofNullable(Member.builder()
                        .id(id)
                        .userId(userId)
                        .password(encodedPassword)
                        .name(name)
                        .telephone(telephone)
                        .address(address)
                        .delYn(delYn)
                        .roles(expectRoleList)
                        .build()));

        // stub(가설) : passwordEncoder.matches() 실행 시 두 값이 일치하여 true 반환 예상.
        given(passwordEncoder.matches(any(), any()))
                .willReturn(true);

        // stub(가설) : jwtAuthenticationProvider.createToken() 실행 시 JWT 토큰 생성해 문자열로 반환 예상.
        given(jwtAuthenticationProvider.createToken(any(Member.class)))
                .willReturn(token);

        // stub(가설) : jwtAuthenticationProvider.getExpirationDateFromToken() 실행 시
        // 토큰의 만료시간인 현재시간+10분 값 반환 예상.
        given(jwtAuthenticationProvider.getExpirationDateFromToken(token))
                .willReturn(expirationDate);

        // when
        String returnToken = memberService.signIn(member);

        // then
        // 토큰이 redis에 1번 등록됨.(1번 수행됨)
        verify(redisSingleDataService, times(1))
                .saveSingleDataWithDuration(eq(LOGIN), eq(token), eq(null),
                        argThat(duration ->
                                duration.compareTo(Duration.ofMillis(validTimeMs)) <= 0));
        // 생성된 토큰이 반환 토큰값과 동일.
        assertEquals(token, returnToken);

    }

    @Test
    @DisplayName("로그아웃성공")
    void successSignOut() {
        // given
        // 토큰
        String token = "TOKEN";
        String authorization = "Bearer " + token;

        // stub(가설) : jwtAuthenticationProvider.parseToken() 실행 시
        // TOKEN_PREFIX가 제외된 토큰값인 "TOKEN" 반환 예상.
        given(jwtAuthenticationProvider.parseToken(authorization))
                .willReturn(token);

        // stub(가설) : redisSingleDataService.getAndDeleteSingleData() 실행 시
        // key값인 token의 value값인 "LOGIN" 반환 예상
        given(redisSingleDataService.deleteSingleData(eq(LOGIN), eq(token)))
                .willReturn("LOGIN");

        // when
        memberService.signOut(authorization);

        // then
        // redis에 등록된 토큰 삭제 후 조회 1번 수행됨
        verify(redisSingleDataService, times(1))
                .deleteSingleData(eq(LOGIN), eq(token));
    }
}