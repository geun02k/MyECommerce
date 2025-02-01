package com.myecommerce.MyECommerce.service.member;

import com.myecommerce.MyECommerce.dto.MemberDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.mapper.MemberMapper;
import com.myecommerce.MyECommerce.repository.member.MemberAuthorityRepository;
import com.myecommerce.MyECommerce.repository.member.MemberRepository;
import com.myecommerce.MyECommerce.type.MemberAuthorityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MemberMapper memberMapper;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MemberAuthorityRepository memberAuthorityRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("비밀번호암호화성공")
    void passwordEncode() {
        // given
        String rawPassword = "12345678";
        // stub(가설) : passwordEncoder.encode() 실행 시 expectEncodePassword 반환 예상.
        String expectEncodePassword = "encode12345678";
        given(passwordEncoder.encode(any()))
                .willReturn(expectEncodePassword);

        // when
        String encodingPassword = passwordEncoder.encode(rawPassword);

        // then
        assertEquals(expectEncodePassword, encodingPassword);
    }

    @Test
    @DisplayName("회원가입성공")
    void successSaveMember() {
        // given
        // 저장할 회원권한생성
        List<MemberAuthority> memberAuthorityList = new ArrayList<>();
        memberAuthorityList.add(MemberAuthority.builder()
                                    .authority(MemberAuthorityType.SELLER)
                                    .build());
        // 저장할 회원객체생성
        MemberDto memberDto = MemberDto.builder()
                .userId("sky")
                .password("123456789")
                .name("김하늘")
                .telephone("01011112222")
                .address("서울 동작구 보라매로5가길 16 보라매아카데미타워 7층")
                .build();

        // 저장된 회원 DTO객체 생성
        MemberDto expectMemberDto = MemberDto.builder()
                .id(1L)
                .userId("sky")
                .password("encode12345678")
                .name("김하늘")
                .telephone("01011112222")
                .address("서울 동작구 보라매로5가길 16 보라매아카데미타워 7층")
                .delYn('N')
                .build();
        // 저장된 회원 Entity객체 생성
        Member expectMemberEntity = Member.builder()
                .id(1L)
                .userId("sky")
                .password("encode12345678")
                .name("김하늘")
                .telephone("01011112222")
                .address("서울 동작구 보라매로5가길 16 보라매아카데미타워 7층")
                .delYn('N')
                .build();
        // 저장된 회원권한 Entity객체 생성
        List<MemberAuthority> expectAuthorityList = new ArrayList<>();
        expectAuthorityList.add(MemberAuthority.builder()
                .id(1L)
                .member(expectMemberEntity)
                .authority(MemberAuthorityType.SELLER)
                .build());

        // stub(가설) : memberRepository.findByTel1AndTel2AndTel3() 실행 시 빈값 반환 예상.
        given(memberRepository.findByTelephone(any()))
                .willReturn(Optional.empty());

        // stub(가설) : passwordEncoder.encode() 실행 시 encode12345678 반환 예상.
        given(passwordEncoder.encode(any()))
                .willReturn("encode12345678");

        // stub(가설) : 저장된 회원정보 Dto를 Entity로 변환 예상.
        given(memberMapper.toEntity(any(MemberDto.class)))  // MemberDto -> Member 변환
                .willReturn(expectMemberEntity);
        // stub(가설) : 저장된 회원정보 Entity를 Dto로 변환 예상.
        given(memberMapper.toDto(any(Member.class)))  // Member -> MemberDto 변환
                .willReturn(expectMemberDto);

        // stub(가설) : memberRepository.save() 실행 시 memberDto 데이터 반환 예상.
        given(memberRepository.save(any(Member.class)))
                .willReturn(expectMemberEntity);

        // stub(가설) : memberAuthorityRepository.save() 실행 시 memberDto 데이터 반환 예상.
        given(memberAuthorityRepository.save(any(MemberAuthority.class)))
                .willReturn(expectAuthorityList.get(0));

        // when
        MemberDto savedMember = memberService.saveMember(memberDto, memberAuthorityList);

        // then
        assertNotNull(savedMember);
        assertNotNull(savedMember.getId());
        assertEquals(1L, savedMember.getId());
        assertEquals("sky", savedMember.getUserId());
        assertEquals("encode12345678", savedMember.getPassword());
        assertEquals("김하늘", savedMember.getName());
        assertEquals("01011112222", savedMember.getTelephone());
        assertEquals("서울 동작구 보라매로5가길 16 보라매아카데미타워 7층", savedMember.getAddress());
        assertEquals('N', savedMember.getDelYn());
    }

    @Test
    @DisplayName("로그인성공")
    void successSignIn() {
        Long id = 1L;
        String userId = "sky";
        String password = "12345678";
        String encodedPassword = "encode12345678";
        String name = "김하늘";
        String telephone = "01011112222";
        String address = "서울 동작구 보라매로5가길 16 보라매아카데미타워 7층";
        Character delYn = 'N';

        // given
        // 조회할 회원 DTO 객체 생성
        MemberDto member = MemberDto.builder()
                .userId(userId)
                .password(password)
                .build();
        // 조회된 회원권한 Entity 객체 생성
        List<MemberAuthority> expectRoleList =
                Collections.singletonList(MemberAuthority.builder()
                .id(id)
                .authority(MemberAuthorityType.SELLER)
                .build());

        // stub(가설) : memberRepository.findByUserId() 실행 시 빈값 반환 예상.
        given(memberRepository.findByUserId(any()))
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

        // stub(가설) : 저장된 회원정보 Entity를 Dto로 변환 예상.
        given(memberMapper.toDto(any(Member.class)))  // Member -> MemberDto 변환
                .willReturn(MemberDto.builder()
                        .id(id)
                        .userId(userId)
                        .password(encodedPassword)
                        .name(name)
                        .telephone(telephone)
                        .address(address)
                        .delYn(delYn)
                        .roles(expectRoleList)
                        .build());

        // when
        MemberDto searchedMember = memberService.authenticateMember(member);

        // then
        assertEquals(id, searchedMember.getId());
        assertEquals(userId, searchedMember.getUserId());
        assertEquals(encodedPassword, searchedMember.getPassword());
        assertEquals(name, searchedMember.getName());
        assertEquals(telephone, searchedMember.getTelephone());
        assertEquals(address, searchedMember.getAddress());
        assertEquals(delYn, searchedMember.getDelYn());
        assertEquals(expectRoleList.get(0).getAuthority(), searchedMember.getRoles().get(0).getAuthority());
    }
}