package com.myecommerce.MyECommerce.service.member;

import com.myecommerce.MyECommerce.config.JwtAuthenticationProvider;
import com.myecommerce.MyECommerce.dto.MemberDto;
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
    public MemberDto saveMember(MemberDto member, List<MemberAuthority> authorities) {
        // validation check
        saveMemberValidationCheck(member);

        // 공백문자 제거
        member.setName(member.getName().trim());
        member.setTelephone(member.getTelephone().trim());
        // 비밀번호 암호화
        member.setPassword(passwordEncoder.encode(member.getPassword().trim()));

        // 회원정보등록
        Member savedMember = memberRepository.save(memberMapper.toEntity(member));
        // 회원권한정보등록
        authorities.forEach(authority -> {
                    authority.setMember(savedMember);
                    memberAuthorityRepository.save(authority);
        });

        // 회원정보반환
        return memberMapper.toDto(savedMember);
    }

    /** 로그인 검증 */
    public MemberDto authenticateMember(MemberDto memberDto) {
        // 1. 사용자ID로 사용자 조회
        Member member = memberRepository.findByUserId(memberDto.getUserId())
                .orElseThrow(() -> new MemberException(USER_NOT_FOUND));

        // 2. 비밀번호 검증
        if(!passwordEncoder.matches(memberDto.getPassword(), member.getPassword())) {
            throw new MemberException(MISMATCH_PASSWORD);
        }

        // 3. 회원정보 반환
        return memberMapper.toDto(member);
    }

    /** 로그아웃 **/
    public void signOut(String authorization) {
        // 1. 헤더에서 토큰정보 가져오기
        String token = jwtAuthenticationProvider.parseToken(authorization);

        // 2. Access Token 유효시간 가져와 blackList로 저장
        Date expirationDate = jwtAuthenticationProvider.getExpirationDateFromToken(token);
        Date now = new Date();
        long validTime = expirationDate.getTime() - now.getTime();
        redisSingleDataService.saveSingleData(token, "blacklist", Duration.ofMillis(validTime));
    }

    // 회원가입 validation check
    private void saveMemberValidationCheck(MemberDto member) {
        // 회원 객체 존재여부 validation check
        if(ObjectUtils.isEmpty(member)) {
            throw new MemberException(EMPTY_MEMBER_INFO);
        }

        // id 존재여부 validation check
        if(!ObjectUtils.isEmpty(member.getId())) {
            throw new MemberException(ALREADY_REGISTERED_MEMBER);
        }

        // 비밀번호 validation check
        // 글자수
        if (ObjectUtils.isEmpty(member.getPassword().trim())
                || 8 > member.getPassword().length()
                || member.getPassword().length() > 100) {
            throw new MemberException(LIMIT_PASSWORD_CHARACTERS_FROM_8_TO_100);
        }

        // 전화번호 validation check
        // 전화번호 중복등록 체크
        String realPhoneNumber = member.getTelephone().trim().replaceAll("-", "");
        Optional<Member> memberEntityIncludeTel =
                memberRepository.findByTelephone(realPhoneNumber);
        if(!ObjectUtils.isEmpty(memberEntityIncludeTel)) {
            throw new MemberException(ALREADY_REGISTERED_PHONE_NUMBER);
        }
    }

}
