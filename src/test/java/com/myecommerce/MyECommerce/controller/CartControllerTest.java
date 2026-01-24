package com.myecommerce.MyECommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.config.JwtAuthenticationProvider;
import com.myecommerce.MyECommerce.config.SecurityConfig;
import com.myecommerce.MyECommerce.controller.config.TestSecurityConfig;
import com.myecommerce.MyECommerce.dto.cart.RequestCartDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.security.filter.JwtAuthenticationFilter;
import com.myecommerce.MyECommerce.service.cart.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.myecommerce.MyECommerce.type.MemberAuthorityType.SELLER;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = CartController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        SecurityConfig.class,
                        JwtAuthenticationFilter.class,
                        JwtAuthenticationProvider.class
                }
        )
)
@Import(TestSecurityConfig.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 유효한 장바구니 추가 요청 */
    RequestCartDto requestCartDto() {
        return RequestCartDto.builder()
                .productCode("productCode")
                .optionCode("optionCode")
                .quantity(5)
                .build();
    }

    /* ----------------------
        장바구니 상품추가 Tests
       ---------------------- */

    @Test
    @DisplayName("장바구니추가 실패 - 고객 외 권한 접근 시 예외발생")
    void addCart_shouldReturnForbidden_whenAccessNotCustomer() throws Exception {
        // given
        RequestCartDto request = requestCartDto();
        Member invalidMember = Member.builder()
                .roles(List.of(MemberAuthority.builder()
                        .authority(SELLER).build())).build();; // 고객아님

        // when
        // then
        mockMvc.perform(post("/cart")
                        .with(user(invalidMember))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode")
                        .value("ACCESS_DENIED"));
        verify(cartService, never()).addCart(request, invalidMember);
    }
}