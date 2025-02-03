package com.myecommerce.MyECommerce.controller;

import com.myecommerce.MyECommerce.config.JwtAuthenticationProvider;
import com.myecommerce.MyECommerce.dto.MemberDto;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.service.member.MemberService;
import com.myecommerce.MyECommerce.type.MemberAuthorityType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    private final MemberService memberService;

    /**
     * 판매자 회원가입 post /member/signup/seller
     **/
    @PostMapping("/signup/seller")
    public ResponseEntity<MemberDto> signUpSeller(@Valid @RequestBody MemberDto member) {
        // 권한추가
        List<MemberAuthority> authorities = new ArrayList<>();
        authorities.add(MemberAuthority.builder()
                                    .authority(MemberAuthorityType.SELLER)
                                    .build());

        // 회웍정보등록
        return ResponseEntity.ok(memberService.saveMember(member, authorities));
    }

    /**
     * 고객 회원가입 post /member/signup/customer
     **/
    @PostMapping("/signup/customer")
    public ResponseEntity<MemberDto> signUpCustomer(@Valid @RequestBody MemberDto member) {
        // 권한추가
        List<MemberAuthority> authorities = new ArrayList<>();
        authorities.add(MemberAuthority.builder()
                                    .authority(MemberAuthorityType.CUSTOMER)
                                    .build());

        // 회웍정보등록
        return ResponseEntity.ok(memberService.saveMember(member, authorities));
    }

    /**
     * 회원 로그인 post /member/signin
     **/
    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody MemberDto memberDto) {
        // 1. 아이디, 패스워드 일치여부 확인
        MemberDto authenticatedMember = memberService.authenticateMember(memberDto);

        // 2. JWT 토큰 생성
        String token = jwtAuthenticationProvider.createToken(authenticatedMember);

        // 3. 토큰 반환
        return ResponseEntity.ok(token);
    }

    /**
     * 회원 로그아웃 post /member/signout
     **/
    @PostMapping("/signout")
    public ResponseEntity<String> signOut(@RequestHeader Map<String, Object> headers) {
        // 로그아웃
        memberService.signOut(headers.get("authorization").toString());

        return ResponseEntity.ok("SUCCESS");
    }

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
