package com.myecommerce.MyECommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.dto.production.RequestProductionDto;
import com.myecommerce.MyECommerce.dto.production.RequestProductionOptionDto;
import com.myecommerce.MyECommerce.dto.production.ResponseProductionDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.service.production.ProductionService;
import com.myecommerce.MyECommerce.type.ProductionCategoryType;
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

import java.math.BigDecimal;
import java.util.Collections;

import static com.myecommerce.MyECommerce.type.ProductionCategoryType.WOMEN_CLOTHING;
import static com.myecommerce.MyECommerce.type.ProductionSaleStatusType.ON_SALE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductionControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private ProductionService productionService;

    @InjectMocks
    private ProductionController productionController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(productionController).build();
    }

    @Test
    @DisplayName("상품등록 성공")
    void successRegisterProduction() throws Exception {
        // given
        String productionCode = "RM-JK-D11S51";
        String productionName = "제 품 명";
        ProductionCategoryType category = WOMEN_CLOTHING;
        String description = "설명";

        String optionCode = "S-BL";
        String optionName = "스몰사이즈 블루컬러";
        BigDecimal price = BigDecimal.valueOf(67900);
        int quantity = 30;
        int seq = 1;

        Long memberId = 1L;

        // 요청 상품옵션 DTO 목록
        RequestProductionOptionDto requestOptionDto = RequestProductionOptionDto.builder()
                .optionCode(optionCode)
                .optionName(optionName)
                .price(price)
                .quantity(quantity)
                .seq(seq)
                .build();
        // 요청 상품 DTO
        RequestProductionDto requestProductionDto =
                RequestProductionDto.builder()
                        .code(productionCode)
                        .name(productionName)
                        .category(category)
                        .description(description)
                        .options(Collections.singletonList(requestOptionDto))
                        .build();
        // 요청 회원 DTO
        Member member = Member.builder()
                .id(memberId)
                .build();

        // 응답 상품 DTO
        ResponseProductionDto response = ResponseProductionDto.builder()
                .id(1L)
                .seller(member.getId())
                .code(productionCode)
                .name(productionName)
                .category(category)
                .description(description)
                .saleStatus(ON_SALE)
                .options(null)
                .build();

        given(productionService.registerProduction(
                any(RequestProductionDto.class), any(Member.class)))
                .willReturn(response);

        // when
        // then
        mockMvc.perform(post("/production")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestProductionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.seller").value(member.getId()))
                .andExpect(jsonPath("$.code").value(requestProductionDto.getCode()))
                .andExpect(jsonPath("$.name").value(requestProductionDto.getName()))
                .andExpect(jsonPath("$.category").value(WOMEN_CLOTHING.toString()))
                .andExpect(jsonPath("$.description").value(requestProductionDto.getDescription()))
                .andExpect(jsonPath("$.saleStatus").value(ON_SALE.toString()));
    }

}