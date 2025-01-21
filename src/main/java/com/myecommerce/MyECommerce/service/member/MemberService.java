package com.myecommerce.MyECommerce.service.member;

import com.myecommerce.MyECommerce.dto.MemberDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     * @param member 신규 회원정보를 가진 MemberDto 객체
     * @return 신규 회원가입한 회원정보를 담은 MemberDto 객체
     */
    //@Transactional
    public MemberDto saveMember(MemberDto member) {
        // validation check
        if(!saveMemberValidationCheck(member)) {
            return null;
        }
        // 회원정보등록
        Member savedMember = memberRepository.save(member.toEntity(member));

        // 회원정보반환
        MemberDto returnMemberDto = new MemberDto();
        returnMemberDto.toDto(savedMember);
        return returnMemberDto;
    }

    // 회원가입 validation check
    private boolean saveMemberValidationCheck(MemberDto member) {
        // 회원 객체 존재여부 validation check
        if(ObjectUtils.isEmpty(member)) {
            System.out.println("회원정보가 존재하지 않습니다.");
            return false;
        }

        // id 존재여부 validation check
        if(!ObjectUtils.isEmpty(member.getId())) {
            System.out.println("이미 존재하는 회원입니다.");
            return false;
        }

        // 사용자명 validation check
        // 글자수
        if (ObjectUtils.isEmpty(member.getName().trim())
                || member.getName().length() > 50) {
            System.out.println("사용자명 길이는 최소 1자, 최대 50자로 제한됩니다.");
            return false;
        }
        // 특수문자, 숫자 제외
        String namePattern = "[^a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ]";
        if(!Pattern.matches(namePattern, member.getName())) {
            System.out.println("사용자명은 영문자, 한글만 사용 가능합니다.");
            return false;
        }

        // 비밀번호 validation check
        // 글자수
        if (ObjectUtils.isEmpty(member.getPassword().trim())
                || 8 > member.getPassword().length()
                || member.getPassword().length() > 100) {
            System.out.println("비밀번호는 최소 8자 이상 최대 100자 이하입니다.");
            return false;
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
            System.out.println("유효하지 않은 전화번호입니다.");
            return false;
        }
        // 전화번호 중복등록 체크
        Optional<Member> memberEntityIncludeTel =
                memberRepository.findByTel1AndTel2AndTel3(member.getTel1(),
                        member.getTel2(),
                        member.getTel3());
        if(!ObjectUtils.isEmpty(memberEntityIncludeTel)) {
            System.out.println("이미 등록된 전화번호입니다.");
            return false;
        }

        return true;
    }
}
