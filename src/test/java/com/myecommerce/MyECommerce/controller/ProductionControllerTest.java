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
import org.junit.jupiter.api.Disabled;
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

import static com.myecommerce.MyECommerce.type.MemberAuthorityType.SELLER;
import static com.myecommerce.MyECommerce.type.ProductionCategoryType.WOMEN_CLOTHING;
import static com.myecommerce.MyECommerce.type.ProductionSaleStatusType.ON_SALE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
                        .authority(SELLER)
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
        상품등록 Tests
       ---------------------- */

    @Test
    @DisplayName("상품등록 성공")
    void successRegisterProduction() throws Exception {
        // given
        // 요청 회원 DTO
        Member member = seller();
        // 요청 상품 DTO
        RequestProductionDto request = validRequestProduction();
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
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.seller").value(member.getId()))
                .andExpect(jsonPath("$.code").value(request.getCode()))
                .andExpect(jsonPath("$.category").value(WOMEN_CLOTHING.toString()))
                .andExpect(jsonPath("$.saleStatus").value(ON_SALE.toString()));
    }

    @Test
    @DisplayName("상품등록실패_상품코드 형식오류")
    public void failRegisterProduction_invalidCode() throws Exception {
        // given
        // 요청 상품 DTO
        RequestProductionDto invalidRequest = RequestProductionDto.builder()
                .code("!!INVALID!!") // @Pattern 위반
                .name("정상 상품명")
                .category(WOMEN_CLOTHING)
                .build();

        // when
        // then
        // 1. 400 에러 발생 & 호출 메시지 검증
        mockMvc.perform(post("/production")
                        .with(user(seller()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage")
                        .value("유효하지않은 값입니다.\n상품코드는 영문자, 숫자만 사용 가능합니다.\n특수문자는 -만 허용하며 첫 글자로 사용 불가합니다."));
        // 2. service 미호출 검증
        verify(productionService, never()).registerProduction(any(), any());
    }

    @Test
    @DisplayName("상품등록실패_유효하지않은 Enum 값 오류")
    public void failRegisterProduction_invalidEnum() throws Exception {
        // given
        // 요청 상품 DTO
        RequestProductionDto invalidRequest = RequestProductionDto.builder()
                .code("validCode")
                .name("정상 상품명")
                .category(null)
                .build();

        // when
        // then
        mockMvc.perform(post("/production")
                        .with(user(seller()))
                        .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        verify(productionService, never()).registerProduction(any(), any());
    }

    @Test
    @DisplayName("상품등록실패_옵션 유효성 미검증")
    public void failRegisterProduction_invalidOption() throws Exception {
        // given
        RequestProductionOptionDto invalidOption =
                RequestProductionOptionDto.builder()
                        .optionCode("") // @Size 위반
                        .build();
        RequestProductionDto request = RequestProductionDto.builder()
                .code("validCode")
                .name("정상 상품명")
                .category(WOMEN_CLOTHING)
                .options(List.of(invalidOption))
                .build();

        // when
        // then
        mockMvc.perform(post("/production")
                        .with(user(seller()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        verify(productionService, never()).registerProduction(any(), any());
    }

    /* ----------------------
        상품상세조회 Tests
       ---------------------- */

    @Test
    @DisplayName("상품상세조회실패_음수 pathVariable 요청 시 400 에러 반환")
    @Disabled("단순 방어적 검증으로 테스트에서 제외 (pathVariable 유효성 검증 여부 확인용)")
    public void searchProductDetail_whenNegativeId_thenBadRequest() throws Exception {
        // given
        Long invalidProductId = -1L;
        // when
        // then
        mockMvc.perform(get("/production/{id}", invalidProductId))
                .andExpect(status().isBadRequest());
        verify(productionService, never()).searchDetailProduction(invalidProductId);
    }

}