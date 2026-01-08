package com.myecommerce.MyECommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.config.JwtAuthenticationProvider;
import com.myecommerce.MyECommerce.config.SecurityConfig;
import com.myecommerce.MyECommerce.controller.config.TestSecurityConfig;
import com.myecommerce.MyECommerce.dto.production.RequestProductionDto;
import com.myecommerce.MyECommerce.dto.production.RequestProductionOptionDto;
import com.myecommerce.MyECommerce.dto.production.ResponseProductionDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.security.filter.JwtAuthenticationFilter;
import com.myecommerce.MyECommerce.service.production.ProductionService;
import com.myecommerce.MyECommerce.type.MemberAuthorityType;
import com.myecommerce.MyECommerce.type.ProductionCategoryType;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static com.myecommerce.MyECommerce.type.ProductionCategoryType.WOMEN_CLOTHING;
import static com.myecommerce.MyECommerce.type.ProductionSaleStatusType.ON_SALE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ProductionController.class,
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
class ProductionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductionService productionService;

    /* ------------------
        Test Fixtures
       ------------------ */

    private Member seller() {
        return Member.builder()
                .id(1L)
                .roles(List.of(MemberAuthority.builder()
                        .id(1L)
                        .authority(MemberAuthorityType.SELLER)
                        .build()))
                .build();
    }

    private RequestProductionDto validRequestProduction() {
        return RequestProductionDto.builder()
                .code("productionCode")
                .name("상 품 명")
                .category(WOMEN_CLOTHING)
                .options(Collections.singletonList(
                        RequestProductionOptionDto.builder()
                                .optionCode("optionCode")
                                .optionName("상품 옵션명")
                                .price(BigDecimal.valueOf(67900))
                                .quantity(30)
                                .build()))
                .build();
    }

    private ResponseProductionDto serviceResponseProduction() {
        return ResponseProductionDto.builder()
                .id(1L)
                .seller(1L)
                .code("productionCode")
                .category(WOMEN_CLOTHING)
                .saleStatus(ON_SALE)
                .build();
    }

    /* ----------------------
        Tests
       ---------------------- */

    @Test
    @DisplayName("상품등록 성공")
    void successRegisterProduction() throws Exception {
        // given
        // 요청 상품 DTO
        RequestProductionDto requestProductionDto = validRequestProduction();
        // 요청 회원 DTO
        Member member = seller();
        // 응답 상품 DTO
        ResponseProductionDto response = serviceResponseProduction();

        given(productionService.registerProduction(
                any(RequestProductionDto.class), any(Member.class)))
                .willReturn(response);

        // when
        // then
        mockMvc.perform(post("/production")
                        .with(user(member))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestProductionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.seller").value(member.getId()))
                .andExpect(jsonPath("$.code").value(requestProductionDto.getCode()))
                .andExpect(jsonPath("$.category").value(WOMEN_CLOTHING.toString()))
                .andExpect(jsonPath("$.saleStatus").value(ON_SALE.toString()));
    }

}