package com.myecommerce.MyECommerce.service.member;

import com.myecommerce.MyECommerce.dto.MemberDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.exception.MemberException;
import com.myecommerce.MyECommerce.mapper.MemberMapper;
import com.myecommerce.MyECommerce.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;
import java.util.regex.Pattern;

import static com.myecommerce.MyECommerce.exception.errorcode.MemberErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;

    private final MemberMapper memberMapper;

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     * @param member 신규 회원정보를 가진 MemberDto 객체
     * @return 신규 회원가입한 회원정보를 담은 MemberDto 객체
     */
    //@Transactional
    public MemberDto saveMember(MemberDto member) {
        // validation check
        saveMemberValidationCheck(member);

        // 공백문자 제거
        member.setName(member.getName().trim());
        member.setTel1(member.getTel1().trim());
        member.setTel2(member.getTel2().trim());
        member.setTel3(member.getTel3().trim());
        // 비밀번호 암호화
        member.setPassword(passwordEncoder.encode(member.getPassword().trim()));

        // 회원정보등록
        Member savedMember = memberRepository.save(memberMapper.toEntity(member));

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
        String tel1Pattern = "^01[016789]";
        String tel2Pattern = "^\\d{3,4}$";
        if (ObjectUtils.isEmpty(member.getTel1().trim())
                || ObjectUtils.isEmpty(member.getTel2().trim())
                || ObjectUtils.isEmpty(member.getTel3().trim())
                || !(Pattern.matches(tel1Pattern, member.getTel1()))
                || !(Pattern.matches(tel2Pattern, member.getTel2()))
                || !(Pattern.matches(tel2Pattern, member.getTel3()))) {
            throw new MemberException(INVALID_PHONE_NUMBER);
        }
        // 전화번호 중복등록 체크
        Optional<Member> memberEntityIncludeTel =
                memberRepository.findByTel1AndTel2AndTel3(member.getTel1(),
                        member.getTel2(),
                        member.getTel3());
        if(!ObjectUtils.isEmpty(memberEntityIncludeTel)) {
            throw new MemberException(ALREADY_REGISTERED_PHONE_NUMBER);
        }
    }

}
