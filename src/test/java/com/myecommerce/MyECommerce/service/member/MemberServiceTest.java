package com.myecommerce.MyECommerce.service.member;

import com.myecommerce.MyECommerce.dto.MemberDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.repository.member.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MemberRepository memberRepository;

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
        // 저장할 회원객체생성
        MemberDto memberDto = MemberDto.builder()
                .userId("sky")
                .password("123456789")
                .name("김하늘")
                .tel1("010")
                .tel2("1234")
                .tel3("1234")
                .address("서울 동작구 보라매로5가길 16 보라매아카데미타워 7층")
                .build();
        // 저장된 회원객체생성
        MemberDto savedReturnMemberDto = MemberDto.builder()
                .id(1L)
                .userId("sky")
                .password("encode12345678")
                .name("김하늘")
                .tel1("010")
                .tel2("1234")
                .tel3("1234")
                .address("서울 동작구 보라매로5가길 16 보라매아카데미타워 7층")
                .delYn('N')
                .build();

        // stub(가설) : memberRepository.findByTel1AndTel2AndTel3() 실행 시 빈값 반환 예상.
        given(memberRepository.findByTel1AndTel2AndTel3(any(), any(), any()))
                .willReturn(Optional.empty());

        // stub(가설) : passwordEncoder.encode() 실행 시 encode12345678 반환 예상.
        given(passwordEncoder.encode(any()))
                .willReturn("encode12345678");

        // stub(가설) : memberRepository.save() 실행 시 memberDto 데이터 반환 예상.
        given(memberRepository.save(any()))
                .willReturn(memberDto.toEntity(memberDto));
        // 조회내용캡쳐
        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);

        // when
        MemberDto savedMember = memberService.saveMember(memberDto);

        // then
        // memberRepository.save() 실행 여부 체크
        verify(memberRepository, times(1)).save(captor.capture());

        assertNotNull(savedMember);
        assertNotNull(savedReturnMemberDto.getId());
        assertEquals(1L, savedReturnMemberDto.getId());
        assertEquals("sky", savedReturnMemberDto.getUserId());
        assertEquals("encode12345678", savedReturnMemberDto.getPassword());
        assertEquals("김하늘", savedReturnMemberDto.getName());
        assertEquals("010", savedReturnMemberDto.getTel1());
        assertEquals("1234", savedReturnMemberDto.getTel2());
        assertEquals("1234", savedReturnMemberDto.getTel3());
        assertEquals("서울 동작구 보라매로5가길 16 보라매아카데미타워 7층", savedReturnMemberDto.getAddress());
        assertEquals('N', savedReturnMemberDto.getDelYn());
    }

}