package com.myecommerce.MyECommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.config.JwtAuthenticationProvider;
import com.myecommerce.MyECommerce.config.SecurityConfig;
import com.myecommerce.MyECommerce.controller.config.TestSecurityConfig;
import com.myecommerce.MyECommerce.dto.product.RequestProductDto;
import com.myecommerce.MyECommerce.dto.product.RequestProductOptionDto;
import com.myecommerce.MyECommerce.dto.product.ResponseProductDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.exception.ProductException;
import com.myecommerce.MyECommerce.security.filter.JwtAuthenticationFilter;
import com.myecommerce.MyECommerce.service.product.ProductService;
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

import static com.myecommerce.MyECommerce.exception.errorcode.ProductErrorCode.PRODUCT_CODE_ALREADY_REGISTERED;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.SELLER;
import static com.myecommerce.MyECommerce.type.ProductCategoryType.WOMEN_CLOTHING;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
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
        controllers = ProductController.class,
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
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 판매자 권한 사용자 */
    private Member seller() {
        return Member.builder()
                .id(1L)
                .roles(List.of(MemberAuthority.builder()
                        .authority(SELLER)
                        .build()))
                .build();
    }

    /** 유효한 상품 요청 */
    private RequestProductDto validRequestProduct() {
        return RequestProductDto.builder()
                .code("productCode")
                .name("상 품 명")
                .category(WOMEN_CLOTHING)
                .options(Collections.singletonList(
                        RequestProductOptionDto.builder()
                                .optionCode("optionCode")
                                .optionName("상품 옵션명")
                                .price(BigDecimal.valueOf(67900))
                                .quantity(30)
                                .build()))
                .build();
    }

    /** 상품 얘상 응답 */
    private ResponseProductDto serviceResponseProduct() {
        return ResponseProductDto.builder()
                .id(1L)
                .seller(1L)
                .code("productCode")
                .category(WOMEN_CLOTHING)
                .saleStatus(ON_SALE)
                .build();
    }

    /* ----------------------
        상품등록 Tests
       ---------------------- */

    @Test
    @DisplayName("상품등록 성공")
    void registerProduct_shouldReturnOk_WhenValidProduct() throws Exception {
        // given
        // 요청 회원 DTO
        Member member = seller();
        // 요청 상품 DTO
        RequestProductDto request = validRequestProduct();
        // 응답 상품 DTO
        ResponseProductDto response = serviceResponseProduct();

        given(productService.registerProduct(
                any(RequestProductDto.class), any(Member.class)))
                .willReturn(response);

        // when
        // then
        mockMvc.perform(post("/product")
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
    @DisplayName("상품상세조회 실패 - 상품코드 형식오류 발생 시 예외발생")
    public void registerProduct_shouldReturnBadRequest_WhenInvalidCode() throws Exception {
        // given
        // 요청 상품 DTO
        RequestProductDto invalidRequest = RequestProductDto.builder()
                .code("!!INVALID!!") // @Pattern 위반
                .name("정상 상품명")
                .category(WOMEN_CLOTHING)
                .build();

        // when
        // then
        // 1. 400 에러 발생 & 호출 메시지 검증
        mockMvc.perform(post("/product")
                        .with(user(seller()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value("INVALID_VALUE"));
        // 2. service 미호출 검증
        verify(productService, never()).registerProduct(any(), any());
    }

    @Test
    @DisplayName("상품상세조회 실패 - 유효하지않은 Enum 값 입력 시 예외발생")
    public void registerProduct_shouldReturnBadRequest_whenInvalidEnumType() throws Exception {
        // given
        // 요청 상품 DTO
        RequestProductDto invalidRequest = RequestProductDto.builder()
                .code("validCode")
                .name("정상 상품명")
                .category(null)
                .build();

        // when
        // then
        mockMvc.perform(post("/product")
                        .with(user(seller()))
                        .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value("INVALID_VALUE"));
        verify(productService, never()).registerProduct(any(), any());
    }

    @Test
    @DisplayName("상품상세조회 실패 - 옵션 유효성 미검증으로 DTO Validation 예외 발생 시 에러 응답 반환")
    public void registerProduct_shouldReturnBadRequest_whenInvalidOption()
            throws Exception {
        // given
        RequestProductOptionDto invalidOption =
                RequestProductOptionDto.builder()
                        .optionCode("optionCode")
                        .optionName("optionName")
                        .price(BigDecimal.valueOf(1000))
                        .quantity(0) // @Min 위반
                        .build();
        RequestProductDto request = RequestProductDto.builder()
                .code("validCode")
                .name("정상 상품명")
                .category(WOMEN_CLOTHING)
                .options(List.of(invalidOption))
                .build();

        // when
        // then
        mockMvc.perform(post("/product")
                        .with(user(seller()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value("INVALID_VALUE"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("유효하지 않은 값입니다.\n해당 옵션의 판매가능 수량은 1 이상이어야 합니다."));

        verify(productService, never()).registerProduct(any(), any());
    }

    @Test
    @DisplayName("상품상세조회 실패 - 상품코드중복 비즈니스 예외 발생 시 에러 응답 반환")
    public void registerProduct_shouldReturnConflict_whenAlreadyRegisteredProduct()
            throws Exception {
        // given
        RequestProductDto request = validRequestProduct();

        given(productService.registerProduct(any(), any(Member.class)))
                .willThrow(new ProductException(PRODUCT_CODE_ALREADY_REGISTERED));

        // when
        // then
        mockMvc.perform(post("/product")
                        .with(user(seller()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode")
                        .value("PRODUCT_CODE_ALREADY_REGISTERED"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("이미 등록된 상품코드입니다."));
    }

    @Test
    @DisplayName("상품등록 실패 - 상품에 대해 상품옵션 최소 1건 미포함 시 예외발생")
    public void registerProduct_shouldReturnBadRequest_whenProductWithoutOptionRegister()
            throws Exception {
        // given
        RequestProductDto request = RequestProductDto.builder()
                .code("code")
                .name("상품명")
                .category(WOMEN_CLOTHING)
                .options(null)
                .build();
        // when
        // then
        mockMvc.perform(post("/product")
                        .with(user(seller()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value("INVALID_VALUE"));
    }

    /* ----------------------
        상품상세조회 Tests
       ---------------------- */

    @Test
    @DisplayName("상품상세조회 실패 - 음수 pathVariable Validation 예외 발생 시 에러 응답 반환")
    public void searchProductDetail_shouldReturnBadRequest_whenNegativeId() throws Exception {
        // given
        Long invalidProductId = -1L;
        // when
        // then
        mockMvc.perform(get("/product/{id}", invalidProductId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value("INVALID_VALUE"))
                .andExpect(jsonPath("$.errorMessage")
                        .value("유효하지 않은 상품 ID 입니다."));
        verify(productService, never()).searchDetailProduct(invalidProductId);
    }

}