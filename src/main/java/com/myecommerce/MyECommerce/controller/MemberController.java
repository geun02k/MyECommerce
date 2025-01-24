package com.myecommerce.MyECommerce.controller;

import com.myecommerce.MyECommerce.dto.MemberDto;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.service.member.MemberService;
import com.myecommerce.MyECommerce.type.MemberAuthorityType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 판매자 회원가입 post /member/signup/seller
     **/
    @PostMapping("/signup/seller")
    public ResponseEntity<MemberDto> signUpSeller(@RequestBody MemberDto member) {
        // 권한추가
        List<MemberAuthority> authorities = new ArrayList<>();
        authorities.add(MemberAuthority.builder()
                .authority(MemberAuthorityType.SELLER)
                .build());
        member.setAuthorities(authorities);

        // 회웍정보등록
        return ResponseEntity.ok(memberService.saveMember(member));
    }

    /**
     * 고객 회원가입 post /member/signup/customer
     **/
    @PostMapping("/signup/customer")
    public ResponseEntity<MemberDto> signUpCustomer(@RequestBody MemberDto member) {
        // 권한추가
        List<MemberAuthority> authorities = new ArrayList<>();
        authorities.add(MemberAuthority.builder()
                .authority(MemberAuthorityType.CUSTOMER)
                .build());
        member.setAuthorities(authorities);

        // 회웍정보등록
        return ResponseEntity.ok(memberService.saveMember(member));
    }

    /**
     * 회원 로그인 post /member/signin
     **/

    /**
     * 회원 조회 get /member
     **/

    /**
     * 회원 수정 put /member
     **/

    /**
     * 회원 삭제 delete /member
     **/

}
