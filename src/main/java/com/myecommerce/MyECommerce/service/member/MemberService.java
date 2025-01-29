package com.myecommerce.MyECommerce.service.member;

import com.myecommerce.MyECommerce.dto.MemberDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.exception.MemberException;
import com.myecommerce.MyECommerce.mapper.MemberMapper;
import com.myecommerce.MyECommerce.repository.member.MemberAuthorityRepository;
import com.myecommerce.MyECommerce.repository.member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.myecommerce.MyECommerce.exception.errorcode.MemberErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;

    private final MemberMapper memberMapper;

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

        // 사용자명 validation check
        // 글자수
        if (ObjectUtils.isEmpty(member.getName().trim())
                || member.getName().length() > 50) {
            throw new MemberException(LIMIT_NAME_CHARACTERS_FROM_1_TO_50);
        }
        // 특수문자, 숫자 제외
        String namePattern = "[^a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ]";
        if(Pattern.matches(namePattern, member.getName())) {
            throw new MemberException(ONLY_SUPPORT_ENGLISH_AND_KOREAN);
        }

        // 비밀번호 validation check
        // 글자수
        if (ObjectUtils.isEmpty(member.getPassword().trim())
                || 8 > member.getPassword().length()
                || member.getPassword().length() > 100) {
            throw new MemberException(LIMIT_PASSWORD_CHARACTERS_FROM_8_TO_100);
        }

        // 전화번호 validation check
        String realPhoneNumber = member.getTelephone().trim().replaceAll("-", "");
        // 휴대폰번호가 010,011,017,017,018,019로 시작하고 숫자만으로 총 10 또는 11자리인지 확인
        String phonePattern = "^01[016789]\\d{7,8}$";

        if (ObjectUtils.isEmpty(realPhoneNumber)
                || !(Pattern.matches(phonePattern, realPhoneNumber))) {
            throw new MemberException(INVALID_PHONE_NUMBER);
        }
        // 전화번호 중복등록 체크
        Optional<Member> memberEntityIncludeTel =
                memberRepository.findByTelephone(realPhoneNumber);
        if(!ObjectUtils.isEmpty(memberEntityIncludeTel)) {
            throw new MemberException(ALREADY_REGISTERED_PHONE_NUMBER);
        }
    }

}
