package com.myecommerce.MyECommerce.service.member;

import com.myecommerce.MyECommerce.config.JwtAuthenticationProvider;
import com.myecommerce.MyECommerce.dto.member.RequestMemberDto;
import com.myecommerce.MyECommerce.dto.member.RequestSignInMemberDto;
import com.myecommerce.MyECommerce.dto.member.ResponseMemberDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.exception.MemberException;
import com.myecommerce.MyECommerce.mapper.MemberMapper;
import com.myecommerce.MyECommerce.repository.member.MemberAuthorityRepository;
import com.myecommerce.MyECommerce.repository.member.MemberRepository;
import com.myecommerce.MyECommerce.service.redis.RedisSingleDataService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.myecommerce.MyECommerce.exception.errorcode.MemberErrorCode.*;
import static com.myecommerce.MyECommerce.type.RedisNamespaceType.LOGIN;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;

    private final MemberMapper memberMapper;

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    private final RedisSingleDataService redisSingleDataService;

    private final MemberRepository memberRepository;
    private final MemberAuthorityRepository memberAuthorityRepository;

    /**
     * 회원가입
     * @param member 신규 회원정보를 가진 MemberDto 객체
     * @return 신규 회원가입한 회원정보를 담은 MemberDto 객체
     */
    @Transactional
    public ResponseMemberDto saveMember(RequestMemberDto member,
                                        List<MemberAuthority> authorities) {
        // 사전 validation check
        saveMemberPreValidationCheck(member);
        // 전화번호 정책 check 및 정규화
        String normalizedTelephone =
                validateAndNormalizeTelephonePolicy(member.getTelephone());
        // 비밀번호 정책 check
        String encodedPassword =
                validateAndEncodePasswordPolicy(member.getPassword());

        // 정책 검증값 반영
        Member memberEntity = memberMapper.toEntity(member);
        memberEntity.setName(member.getName().trim()); // 공백문자 제거
        memberEntity.setTelephone(normalizedTelephone);
        memberEntity.setPassword(encodedPassword);

        // 회원정보등록
        Member savedMember = memberRepository.save(memberEntity);
        // 회원권한정보등록
        authorities.forEach(authority -> {
                    authority.setMember(savedMember);
                    memberAuthorityRepository.save(authority);
        });

        // 회원정보반환
        return memberMapper.toDto(savedMember);
    }

    /** 로그인 **/
    public String signIn(RequestSignInMemberDto memberDto) {
        // 1. 사용자ID 검증 (사용자 조회)
        Member member = memberRepository.findByUserIdAndDelYn(memberDto.getUserId(), 'N')
                .orElseThrow(() -> new MemberException(USER_NOT_FOUND));

        // 2. 비밀번호 검증
        if(!passwordEncoder.matches(memberDto.getPassword(), member.getPassword())) {
            throw new MemberException(PASSWORD_MISMATCHED);
        }

        // 3. JWT 토큰 생성
        String token = jwtAuthenticationProvider.createToken(member);

        // 4. Redis에 유효한 토큰 저장
        this.saveLoginTokenInRedis(token);

        // 5. 토큰 반환
        return token;
    }

    /** 로그아웃 **/
    public void signOut(String authorization) {
        // 1. 헤더에서 토큰정보 가져오기
        String token = jwtAuthenticationProvider.parseToken(authorization);

        // 2. Redis에서 유효한 토큰 삭제
        if (ObjectUtils.isEmpty(redisSingleDataService.deleteSingleData(LOGIN, token))) {
            throw new MemberException(USER_ALREADY_SIGNED_OUT);
        }
    }

    // 회원가입 validation check
    private void saveMemberPreValidationCheck(RequestMemberDto member) {
        // 회원 객체 존재여부 check (계약 방어, 프로그래밍 오류)
        if(member == null) {
            throw new IllegalArgumentException("RequestMemberDto must not be null.");
        }

        // id 존재여부 check (회원가입 유스케이스의 비즈니스 규칙)
        if(member.getId() != null) {
            throw new MemberException(MEMBER_ALREADY_REGISTERED);
        }

    }

    // Redis에 로그인 시 생성된 토큰 저장
    private void saveLoginTokenInRedis(String token) {
        Date expirationDate = jwtAuthenticationProvider.getExpirationDateFromToken(token);
        Date now = new Date();
        long validTime = expirationDate.getTime() - now.getTime();

        // Token을 LOGIN value를 가지도록 저장
        redisSingleDataService.saveSingleDataWithDuration(
                LOGIN, token, null, Duration.ofMillis(validTime));
    }

    private String validateAndEncodePasswordPolicy(String password) {
        // 비밀번호 보안 정책 (길이가 너무 짧지 않을 것)
        if (8 > password.length() || password.length() > 100) {
            throw new MemberException(PASSWORD_LENGTH_LIMITED);
        }

        // 비밀번호 암호화
        return passwordEncoder.encode(password);
    }

    private String validateAndNormalizeTelephonePolicy(String telephone) {
        final String PHONE_MEMBER_REGEX = "^01[016789]\\d{7,8}$";

        // 1. 데이터 정규화 - 숫자만 남김
        String normalizedTel = normalizeTelephone(telephone);

        // 2. 전화번호 길이 제한
        if(!(10 <= normalizedTel.length() && normalizedTel.length() <= 11)) {
            throw new MemberException(TELEPHONE_LENGTH_LIMITED);
        }
        // 3. 전화번호 패턴 검증
        if (!normalizedTel.matches(PHONE_MEMBER_REGEX)) {
            throw new MemberException(TELEPHONE_PATTERN_INVALID);
        }

        // 4. 전화번호 중복등록 체크
        Optional<Member> memberEntityIncludeTel =
                memberRepository.findByTelephone(normalizedTel);
        if(memberEntityIncludeTel.isPresent()) {
            throw new MemberException(TELEPHONE_ALREADY_REGISTERED);
        }

        return normalizedTel;
    }

    private String normalizeTelephone(String telephone) {
        return telephone.replaceAll("[^0-9]", "");
    }

}
