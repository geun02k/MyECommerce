package com.myecommerce.MyECommerce.service.product;

import com.myecommerce.MyECommerce.dto.product.*;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.mapper.*;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import com.myecommerce.MyECommerce.service.stock.StockCacheService;
import com.myecommerce.MyECommerce.type.ProductCategoryType;
import com.myecommerce.MyECommerce.type.ProductSaleStatusType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.myecommerce.MyECommerce.type.ProductCategoryType.WOMEN_CLOTHING;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.DISCONTINUED;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductPolicy productionPolicy;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductOptionRepository productOptionRepository;
    @Mock
    private ServiceProductMapper serviceProductMapper;
    @Mock
    private StockCacheService stockCacheService;

    @InjectMocks
    private ProductService productService;

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 회원 */
    Member seller() {
        return Member.builder().id(1L).build();
    }

    /** 유효한 수정할 상품 옵션 요청 */
    RequestModifyProductOptionDto requestUpdateOption() {
        return RequestModifyProductOptionDto.builder()
                .id(1L)
                .optionCode("existingOptionCode")
                .quantity(10)
                .build();
    }
    /** 유효한 등록할 상품 옵션 요청 */
    RequestModifyProductOptionDto requestInsertOption() {
        return RequestModifyProductOptionDto.builder()
                .optionCode("optionCode")
                .quantity(20)
                .build();
    }

    /** Service 전용 상품 DTO */
    ServiceProductDto serviceProductDto(ProductSaleStatusType saleStatus,
                                        List<RequestModifyProductOptionDto> options) {
        List<ServiceProductOptionDto> serviceOptions = new ArrayList<>();
        for (RequestModifyProductOptionDto option : options) {
            serviceOptions.add(ServiceProductOptionDto.builder()
                    .id(option.getId())
                    .optionCode(option.getOptionCode())
                    .quantity(option.getQuantity())
                    .build());
        }

        return ServiceProductDto.builder()
                .id(5L)
                .description("수정한 상품 설명입니다.")
                .saleStatus(saleStatus)
                .options(serviceOptions)
                .build();
    }

    /** 수정할 상품 Entity */
    Product onSaleProductEntity() {
        return Product.builder()
                .id(5L)
                .code("productCode")
                .description("description")
                .saleStatus(ON_SALE)
                .options(new ArrayList<>(List.of(
                        ProductOption.builder()
                                .id(1L)
                                .optionCode("existingOptionCode")
                                .quantity(1)
                                .build())))
                .build();
    }
    /** 수정할 상품옵션 Entity */
    ProductOption updateProductOptionEntity() {
        return ProductOption.builder()
                .id(1L)
                .quantity(10)
                .build();
    }
    /** 등록할 상품옵션 Entity */
    ProductOption insertProductOptionEntity() {
        return ProductOption.builder()
                .optionCode("optionCode")
                .quantity(20)
                .build();
    }

    /* ------------------
        Helper Method
       ------------------ */

    /** 옵션 ID에 대한 옵션 반환 (null 전달 시 신규 옵션 반환) */
    ServiceProductOptionDto filterOption(ServiceProductDto product,
                                         Long optionId) {
        return product.getOptions().stream()
                .filter(option ->
                        Objects.equals(option.getId(), optionId))
                .findFirst()
                .orElseThrow();
    }
    ProductOption filterOption(Product product, Long optionId) {
        return product.getOptions().stream()
                .filter(option ->
                        Objects.equals(option.getId(), optionId))
                .findFirst()
                .orElseThrow();
    }

    /* ----------------------
        상품등록 Tests
       ---------------------- */

    @Test
    @DisplayName("상품등록 성공")
    void successSaveProduction() {
        // given
        String productionCode = "RM-JK-D11S51";
        String productionName = "제 품 명";
        ProductCategoryType category = WOMEN_CLOTHING;
        String description = "설명";
        ProductSaleStatusType saleStatus = ON_SALE;

        String optionCode = "S-BL";
        String optionName = "스몰사이즈 블루컬러";
        BigDecimal price = BigDecimal.valueOf(67900);
        int quantity = 30;
        int seq = 1;

        Long memberId = 1L;

        // 요청 상품옵션 DTO 목록
        RequestProductOptionDto requestOptionDto = RequestProductOptionDto.builder()
                .optionCode(optionCode)
                .optionName(optionName)
                .price(price)
                .quantity(quantity)
                .build();
        // 요청 상품 DTO
        RequestProductDto requestProductionDto =
                RequestProductDto.builder()
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

        // Service DTO
        ServiceProductOptionDto serviceOptionDto =
                ServiceProductOptionDto.builder()
                        .optionCode(optionCode)
                        .optionName(optionName)
                        .price(price)
                        .quantity(quantity)
                        .build();
        ServiceProductDto serviceProductionDto =
                ServiceProductDto.builder()
                        .code(productionCode)
                        .name(productionName)
                        .category(category)
                        .options(Collections.singletonList(serviceOptionDto))
                        .build();
        ProductOption expectedEntityServiceOptionDto =
                ProductOption.builder()
                        .optionCode(optionCode)
                        .optionName(optionName)
                        .price(price)
                        .quantity(quantity)
                        .build();
        Product expectedEntityOfServiceProductionDto =
                Product.builder()
                        .code(productionCode)
                        .name(productionName)
                        .category(category)
                        .options(Collections.singletonList(
                                expectedEntityServiceOptionDto))
                        .build();

        // 저장할 상품옵션 Entity
        ProductOption optionEntity = ProductOption.builder()
                .optionCode(optionCode)
                .optionName(optionName)
                .price(price)
                .quantity(quantity)
                .build();
        // 저장할 상품 Entity
        Product productionEntity  = Product.builder()
                .seller(member.getId())
                .code(requestProductionDto.getCode())
                .name(requestProductionDto.getName())
                .category(requestProductionDto.getCategory())
                .description(requestProductionDto.getDescription())
                .saleStatus(saleStatus)
                .build();

        // 저장된 상품 Entity
        Product expectedProductionEntity = Product.builder()
                .id(1L)
                .seller(productionEntity.getSeller())
                .code(productionEntity.getCode())
                .name(productionEntity.getName())
                .category(productionEntity.getCategory())
                .description(productionEntity.getDescription())
                .saleStatus(productionEntity.getSaleStatus())
                .options(null)
                .build();
        // 저장된 상품옵션 Entity
        ProductOption expectedOptionEntity = ProductOption.builder()
                .id(1L)
                .optionCode(optionEntity.getOptionCode())
                .optionName(optionEntity.getOptionName())
                .price(optionEntity.getPrice())
                .quantity(optionEntity.getQuantity())
                .product(expectedProductionEntity)
                .build();
        // response 상품 DTO
        ResponseProductDto expectedProductionDto = ResponseProductDto.builder()
                .id(1L)
                .seller(member.getId())
                .code(productionCode)
                .name(productionName)
                .category(category)
                .description(description)
                .saleStatus(ON_SALE)
                .build();

        given(serviceProductMapper.toServiceDto(requestProductionDto))
                .willReturn(serviceProductionDto);
        given(serviceProductMapper.toEntity(serviceProductionDto))
                .willReturn(expectedEntityOfServiceProductionDto);

        ArgumentCaptor<Product> productionCaptor =
                ArgumentCaptor.forClass(Product.class);
        ArgumentCaptor<ProductOption> optionCaptor =
                ArgumentCaptor.forClass(ProductOption.class);
        // stub(가설) : productionRepository.save() 실행 시 expectedProductionEntity 반환 예상.
        given(productRepository.save(productionCaptor.capture()))
                .willReturn(expectedProductionEntity);
        // stub(가설) : productionOptionRepository.save() 실행 시
        // expectedProductionEntity 정보를 포함한 ProductOption Entity 반환 예상.
        given(productOptionRepository.save(optionCaptor.capture()))
                .willReturn(expectedOptionEntity);

        // stub(가설) : productionMapper.toDto() 실행 시 productionEntity에 대한 DTO 반환 예상.
        given(serviceProductMapper.toDto(expectedProductionEntity))
                .willReturn(expectedProductionDto);

        // when
        ResponseProductDto response =
                productService.registerProduct(requestProductionDto, member);

        // then
        verify(productionPolicy, times(1))
                .validateRegister(serviceProductionDto, member);
        verify(productRepository, times(1))
                .save(productionCaptor.capture());
        verify(productOptionRepository, times(1))
                .save(optionCaptor.capture());
        // 상품 재고 등록 여부 검증
        verify(stockCacheService, times(1))
                .saveProductStock(eq(expectedProductionEntity));
        // 상품 전달인자 검증
        Product capturedProduction = productionCaptor.getValue();
        assertNull(capturedProduction.getId());
        assertEquals(productionCode, capturedProduction.getCode());
        assertEquals(productionName, capturedProduction.getName());
        assertEquals(WOMEN_CLOTHING, capturedProduction.getCategory());
        assertEquals(ON_SALE, capturedProduction.getSaleStatus());
        assertEquals(member.getId(), capturedProduction.getSeller());
        assertNull(capturedProduction.getOptions());
        // 상품옵션 전달인자 검증
        ProductOption capturedOption = optionCaptor.getAllValues().get(0);
        assertNull(capturedOption.getId());
        assertEquals(optionCode, capturedOption.getOptionCode());
        assertEquals(quantity, capturedOption.getQuantity());
        assertEquals(price, capturedOption.getPrice());

        // 상품 검증
        assertEquals(requestProductionDto.getCode(), response.getCode());
        assertEquals(requestProductionDto.getName(), response.getName());
        assertEquals(requestProductionDto.getCategory(), response.getCategory());
        assertEquals(requestProductionDto.getDescription(), response.getDescription());
        assertEquals(member.getId(), response.getSeller());
        assertEquals(productionEntity.getSaleStatus(), response.getSaleStatus());
        // 상품옵션검증
        RequestProductOptionDto reqOptFromReqProduction = requestProductionDto.getOptions().get(0);
        assertEquals(reqOptFromReqProduction.getOptionCode(), expectedOptionEntity.getOptionCode());
        assertEquals(reqOptFromReqProduction.getOptionName(), expectedOptionEntity.getOptionName());
        assertEquals(reqOptFromReqProduction.getPrice(), expectedOptionEntity.getPrice());
        assertEquals(reqOptFromReqProduction.getQuantity(), expectedOptionEntity.getQuantity());
        assertEquals(expectedProductionEntity, expectedOptionEntity.getProduct());
        assertEquals(expectedOptionEntity.getId(), expectedOptionEntity.getProduct().getId());
    }

    /* ----------------------
        상품수정 Tests
       ---------------------- */

    @Test
    @DisplayName("상품수정 성공 - 판매중 유지 상품 수정 시 상품/옵션 변경 후 재고 등록")
    void modifyProduct_shouldUpdateProductAndSaveStock_whenProductOnSale() {
        // given
        // 요청 상품옵션 DTO 목록
        RequestModifyProductOptionDto requestUpdateOption = requestUpdateOption();
        RequestModifyProductOptionDto requestInsertOption = requestInsertOption();
        // 요청 상품 DTO
        RequestModifyProductDto requestProduct =
                RequestModifyProductDto.builder()
                        .id(5L)
                        .description("수정한 상품 설명입니다.")
                        .saleStatus(ON_SALE) // 판매중 유지
                        .options(List.of(requestUpdateOption, requestInsertOption))
                        .build();
        // 요청 회원 DTO
        Member member = seller();

        Product targetProduct = onSaleProductEntity();
        ServiceProductDto serviceProductDto =
                serviceProductDto(ON_SALE, requestProduct.getOptions());
        ServiceProductOptionDto insertOptionDto =
                filterOption(serviceProductDto, null);
        ServiceProductOptionDto updateOptionDto =
                filterOption(serviceProductDto, 1L);

        // requestDto -> ServiceDto 변환
        given(serviceProductMapper.toServiceDto(requestProduct))
                .willReturn(serviceProductDto);
        // 요청한 셀러 상품 단건 조회 (반환 결과는 dirty checking 대상)
        given(productRepository.findByIdAndSeller(
                requestProduct.getId(), member.getId()))
                .willReturn(Optional.of(targetProduct));
        // 수정, 신규 옵션 DTO -> Entity로 변환 (옵션값 변경 직전)
        given(serviceProductMapper.toOptionEntity(updateOptionDto))
                .willReturn(updateProductOptionEntity());
        given(serviceProductMapper.toOptionEntity(insertOptionDto))
                .willReturn(insertProductOptionEntity());

        // when
        productService.modifyProduct(requestProduct, member);

        // then
        // 정책 검증 여부 검증
        verify(productionPolicy, times(1))
                .validateModify(any(Product.class), anyList());
        // 상품 재고 등록 여부 검증
        verify(stockCacheService, times(1))
                .saveProductStock(targetProduct);
        // 상품 재고 삭제 여부 검증
        verify(stockCacheService, never()).deleteProductStock(any());

        // 상품 판매상태, 설명 / 신규, 수정 옵션 수량 검증 (옵션 변경이 실제로 반영되었는지 확인)
        // 1. 상품 수정 검증
        assertEquals(requestProduct.getDescription(), targetProduct.getDescription());
        assertEquals(requestProduct.getSaleStatus(), targetProduct.getSaleStatus());
        // 2. 상품옵션 수정 검증
        ProductOption responseUpdatedOption = filterOption(targetProduct, 1L);
        assertEquals(requestUpdateOption.getQuantity(), responseUpdatedOption.getQuantity());
        // 3. 상품옵션 신규등록 검증 (JPA 더티체킹으로, 신규 생성되어야하는 아이디는 미검증)
        ProductOption responseInsertedOption = filterOption(targetProduct, null);
        assertEquals(requestInsertOption.getQuantity(), responseInsertedOption.getQuantity());
    }

    @Test
    @DisplayName("상품수정 성공 - 상품 판매중단으로 변경 시 상품/옵션 변경 후 재고 삭제")
    @Transactional
    void modifyProduct_shouldUpdateProductAndDeleteCacheStock_whenProductDisContinued() {
        // given
        // 요청 상품옵션 DTO 목록
        RequestModifyProductOptionDto requestUpdateOption = requestUpdateOption();
        RequestModifyProductOptionDto requestInsertOption = requestInsertOption();
        // 요청 상품 DTO
        RequestModifyProductDto requestProduct =
                RequestModifyProductDto.builder()
                        .id(5L)
                        .description("수정한 상품 설명입니다.")
                        .saleStatus(DISCONTINUED) // 판매중단으로 변경
                        .options(List.of(requestUpdateOption, requestInsertOption))
                        .build();
        // 요청 회원 DTO
        Member member = seller();

        Product targetProduct = onSaleProductEntity();
        ServiceProductDto serviceProductDto =
                serviceProductDto(DISCONTINUED, requestProduct.getOptions());
        ServiceProductOptionDto insertOptionDto =
                filterOption(serviceProductDto, null);
        ServiceProductOptionDto updateOptionDto =
                filterOption(serviceProductDto, 1L);

        // requestDto -> ServiceDto 변환
        given(serviceProductMapper.toServiceDto(requestProduct))
                .willReturn(serviceProductDto);
        // 요청한 셀러 상품 단건 조회 (반환 결과는 dirty checking 대상)
        given(productRepository.findByIdAndSeller(
                requestProduct.getId(), member.getId()))
                .willReturn(Optional.of(targetProduct));
        // 수정, 신규 옵션 DTO -> Entity로 변환 (옵션값 변경 직전)
        given(serviceProductMapper.toOptionEntity(updateOptionDto))
                .willReturn(updateProductOptionEntity());
        given(serviceProductMapper.toOptionEntity(insertOptionDto))
                .willReturn(insertProductOptionEntity());

        // when
        productService.modifyProduct(requestProduct, member);

        // then
        verify(productionPolicy, times(1))
                .validateModify(any(Product.class), anyList());
        // 상품 재고 등록 여부 검증
        verify(stockCacheService, times(1)).deleteProductStock(targetProduct);
        verify(stockCacheService, never()).saveProductStock(targetProduct);

        // 상품 판매상태, 설명 / 신규, 수정 옵션 수량 검증 (옵션 변경이 실제로 반영되었는지 확인)
        // 1. 상품 수정 검증
        assertEquals(requestProduct.getDescription(), targetProduct.getDescription());
        assertEquals(requestProduct.getSaleStatus(), targetProduct.getSaleStatus());
        // 2. 상품옵션 수정 검증
        ProductOption responseUpdatedOption = filterOption(targetProduct, 1L);
        assertEquals(requestUpdateOption.getQuantity(), responseUpdatedOption.getQuantity());
        // 3. 상품옵션 신규등록 검증 (JPA 더티체킹으로, 신규 생성되어야하는 아이디는 미검증)
        ProductOption responseInsertedOption = filterOption(targetProduct, null);
        assertEquals(requestInsertOption.getQuantity(), responseInsertedOption.getQuantity());
    }

}