package com.myecommerce.MyECommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        memberDto.setAuthorities(Collections.singletonList(authority));

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
                        .authorities(Collections.singletonList(authority))
                        .build());

        // then
        mockMvc.perform(post("/member/signup/seller")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.password").value("encode123456789"))
                .andExpect(jsonPath("$.authorities[0].authority").value("SELLER"));
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
        memberDto.setAuthorities(Collections.singletonList(authority));

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
                        .authorities(Collections.singletonList(authority))
                        .build());

        // then
        mockMvc.perform(post("/member/signup/customer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(memberDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.password").value("encode123456789"))
                .andExpect(jsonPath("$.authorities[0].authority").value("CUSTOMER"));
    }
}