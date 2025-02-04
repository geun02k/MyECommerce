package com.myecommerce.MyECommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.config.JwtAuthenticationProvider;
import com.myecommerce.MyECommerce.dto.MemberDto;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.service.member.MemberService;
import com.myecommerce.MyECommerce.type.MemberAuthorityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    @Test
    @DisplayName("판매자회원가입성공")
    void successSignUpSeller() throws Exception {
        // given
        // 저장할 회원객체생성
        MemberDto memberDto = MemberDto.builder()
                .userId("sky")
                .password("123456789")
                .name("김하늘")
                .telephone("01011112222")
                .address("서울 동작구 보라매로5가길 16 보라매아카데미타워 7층")
                .build();
        // 권한 설정
        MemberAuthority authority = new MemberAuthority();
        authority.setAuthority(MemberAuthorityType.SELLER);

        // when
        when(memberService.saveMember(any(MemberDto.class), anyList()))
                .thenReturn(MemberDto.builder()
                        .id(1L)
                        .userId("sky")
                        .password("encode123456789")
                        .name("김하늘")
                        .telephone("01011112222")
                        .address("서울 동작구 보라매로5가길 16 보라매아카데미타워 7층")
                        .delYn('N')
                        .roles(Collections.singletonList(authority))
                        .build());

        // then
        mockMvc.perform(post("/member/signup/seller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.password").value("encode123456789"))
                .andExpect(jsonPath("$.roles[0].authority").value("SELLER"));
    }

    @Test
    @DisplayName("고객회원가입성공")
    void successSignUpCustomer() throws Exception {
        // given
        // 저장할 회원객체생성
        MemberDto memberDto = MemberDto.builder()
                .userId("sky")
                .password("123456789")
                .name("김하늘")
                .telephone("01011112222")
                .address("서울 동작구 보라매로5가길 16 보라매아카데미타워 7층")
                .build();
        // 권한 설정
        MemberAuthority authority = new MemberAuthority();
        authority.setAuthority(MemberAuthorityType.CUSTOMER);

        // when
        when(memberService.saveMember(any(MemberDto.class), anyList()))
                .thenReturn(MemberDto.builder()
                        .id(1L)
                        .userId("sky")
                        .password("encode123456789")
                        .name("김하늘")
                        .telephone("01011112222")
                        .address("서울 동작구 보라매로5가길 16 보라매아카데미타워 7층")
                        .delYn('N')
                        .roles(Collections.singletonList(authority))
                        .build());

        // then
        mockMvc.perform(post("/member/signup/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.password").value("encode123456789"))
                .andExpect(jsonPath("$.roles[0].authority").value("CUSTOMER"));
    }

    @Test
    @DisplayName("로그인성공")
    void successSignIn() throws Exception {
        // given
        String userId = "sky";
        String password = "12345678";
        // 조회할 회원 DTO 객체
        MemberDto member= MemberDto.builder()
                .userId(userId)
                .password(password)
                .build();

        // when
        given(memberService.signIn(any(MemberDto.class)))
                .willReturn("token");

        // then
        // 3. 토큰 반환
        String contentAsString = mockMvc.perform(post("/member/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk())
                .andDo(print())  // 응답 내용 출력 (optional)
                .andReturn()  // 결과 반환
                .getResponse()  // MockHttpServletResponse 객체 반환
                .getContentAsString();// 응답 본문을 String으로 가져오기
        assertEquals("token", contentAsString);  // 원하는 값과 비교
    }

    @Test
    @DisplayName("로그아웃성공")
    void successSignOut() throws Exception {
        // given

        // when
        doNothing().when(memberService).signOut(anyString());

        // then
        mockMvc.perform(post("/member/signout")
                        .header("authorization", "Bearer TOKEN"))
                .andExpect(status().isOk())
                .andExpect(content().string("SUCCESS"));

        verify(memberService, times(1)).signOut(anyString());

    }

}