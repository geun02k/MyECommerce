package com.myecommerce.MyECommerce.service.product;

import com.myecommerce.MyECommerce.dto.product.*;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.exception.ProductException;
import com.myecommerce.MyECommerce.mapper.*;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import com.myecommerce.MyECommerce.service.stock.StockCacheService;
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

import static com.myecommerce.MyECommerce.exception.errorcode.ProductErrorCode.PRODUCT_ALREADY_DELETED;
import static com.myecommerce.MyECommerce.type.ProductCategoryType.WOMEN_CLOTHING;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductPolicy productPolicy;
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

    /** 요청 옵션 목록 -> Service 전용 옵션 목록 */
    List<ServiceProductOptionDto> serviceProductOptionDtoList(
            List<RequestProductOptionDto> requestOptions) {

        List<ServiceProductOptionDto> serviceOptions = new ArrayList<>();

        for (RequestProductOptionDto option : requestOptions) {
            serviceOptions.add(
                    ServiceProductOptionDto.builder()
                            .optionCode(option.getOptionCode())
                            .optionName(option.getOptionName())
                            .price(option.getPrice())
                            .quantity(option.getQuantity())
                            .build());
        }
        return serviceOptions;
    }

    /**
     * Service DTO 생성
     * - 옵션 분기 (수정/신규) 판단용
     * - 상품 상태 변경 로직 진입용
     */
    ServiceProductDto serviceProductDto(ProductSaleStatusType saleStatus,
                                        List<RequestModifyProductOptionDto> options) {
        // 옵션 타입 변환
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

    /** 등록 Service 전용 상품 DTO */
    ServiceProductDto serviceProductDto(RequestProductDto request) {
        List<ServiceProductOptionDto> options =
                serviceProductOptionDtoList(request.getOptions());

        return ServiceProductDto.builder()
                .code(request.getCode())
                .name(request.getName())
                .category(request.getCategory())
                .options(options)
                .build();
    }

    /** 수정 Service 전용 상품 DTO (판매중 상태 유지) */
    ServiceProductDto serviceProductDto(List<RequestModifyProductOptionDto> options) {
        return this.serviceProductDto(ON_SALE, options);
    }

    /** 등록할 옵션 Entity */
    List<ProductOption> notInsertedProductEntity(
            List<ServiceProductOptionDto> serviceOptions) {

        List<ProductOption> options = new ArrayList<>();
        for(ServiceProductOptionDto option : serviceOptions) {
            options.add(
                    ProductOption.builder()
                            .optionCode(option.getOptionCode())
                            .optionName(option.getOptionName())
                            .price(option.getPrice())
                            .quantity(option.getQuantity())
                            .build());
        }
        return options;
    }

    /** 등록할 상품 Entity */
    Product notInsertedProductEntity(ServiceProductDto product) {
        return Product.builder()
                .code(product.getCode())
                .name(product.getName())
                .category(product.getCategory())
                .options(notInsertedProductEntity(product.getOptions()))
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

    /** 상품 response DTO 반환 */
    ResponseProductDto responseProductDto(Product product) {
        return ResponseProductDto.builder()
                .id(product.getId())
                .seller(product.getSeller())
                .code(product.getCode())
                .name(product.getName())
                .category(product.getCategory())
                .saleStatus(product.getSaleStatus())
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
    @DisplayName("상품등록 성공 - 유효한 상품 정보 요청 시 신규 상품과 상품옵션 등록")
    void registerProduct_shouldInsertProductAndOption_whenValidProduct() {
        // given
        // 요청 상품옵션 DTO
        RequestProductOptionDto requestOptionDto = RequestProductOptionDto.builder()
                .optionCode("S-BL")
                .optionName("스몰사이즈 블루컬러")
                .price(BigDecimal.valueOf(67900))
                .quantity(30)
                .build();
        // 요청 상품 DTO
        RequestProductDto requestProductDto = RequestProductDto.builder()
                .code("RM-JK-D11S51")
                .name("제 품 명")
                .category(WOMEN_CLOTHING)
                .options(Collections.singletonList(requestOptionDto))
                .build();
        // 요청 회원 DTO
        Member member = seller();

        // serviceProductMapper.toServiceDto() 예상 반환 결과
        ServiceProductDto serviceProductDto = serviceProductDto(requestProductDto);
        // serviceProductMapper.toEntity() 예상 반환 결과
        Product expectedProduct = notInsertedProductEntity(serviceProductDto);

        // 저장된 상품 Entity
        Product insertedProduct = Product.builder()
                .id(1L)
                .seller(member.getId())
                .code(requestProductDto.getCode())
                .name(requestProductDto.getName())
                .category(requestProductDto.getCategory())
                .saleStatus(ON_SALE)
                .options(null)
                .build();
        // 저장된 상품옵션 Entity
        ProductOption insertedOptionEntity = ProductOption.builder()
                .id(1L)
                .optionCode(requestOptionDto.getOptionCode())
                .optionName(requestOptionDto.getOptionName())
                .price(requestOptionDto.getPrice())
                .quantity(requestOptionDto.getQuantity())
                .product(insertedProduct)
                .build();

        // response 상품 DTO
        ResponseProductDto responseProduct = responseProductDto(insertedProduct);

        given(serviceProductMapper.toServiceDto(requestProductDto))
                .willReturn(serviceProductDto);
        given(serviceProductMapper.toEntity(serviceProductDto))
                .willReturn(expectedProduct);
        given(productRepository.save(any())).willReturn(insertedProduct);
        given(productOptionRepository.save(any())).willReturn(insertedOptionEntity);
        given(serviceProductMapper.toDto(insertedProduct)).willReturn(responseProduct);

        // when
        productService.registerProduct(requestProductDto, member);

        // then
        // 저장 여부 검증
        ArgumentCaptor<Product> productionCaptor = ArgumentCaptor.forClass(Product.class);
        ArgumentCaptor<ProductOption> optionCaptor = ArgumentCaptor.forClass(ProductOption.class);
        verify(productRepository, times(1))
                .save(productionCaptor.capture());
        verify(productOptionRepository, times(1))
                .save(optionCaptor.capture());

        // 상품 전달인자 검증
        Product capturedProduct = productionCaptor.getValue();
        assertEquals(requestProductDto.getCode(), capturedProduct.getCode());
        assertEquals(requestProductDto.getName(), capturedProduct.getName());
        assertEquals(requestProductDto.getCategory(), capturedProduct.getCategory());
        assertEquals(ON_SALE, capturedProduct.getSaleStatus());
        assertEquals(member.getId(), capturedProduct.getSeller());
        assertNull(capturedProduct.getOptions());
        // 상품옵션 전달인자 검증
        ProductOption capturedOption = optionCaptor.getAllValues().get(0);
        assertEquals(1L, capturedOption.getProduct().getId());
        assertEquals(requestOptionDto.getOptionCode(), capturedOption.getOptionCode());
        assertEquals(requestOptionDto.getQuantity(), capturedOption.getQuantity());
        assertEquals(requestOptionDto.getPrice(), capturedOption.getPrice());
    }

    // TODO: 상품등록 성공 - 정책 검증 위임
    // verify(productPolicy, times(1)).validateRegister(serviceProductDto, member);

    // TODO: 상품등록 성공 - 상품 캐시 재고 등록 위임
    // verify(stockCacheService, times(1)).saveProductStock(eq(insertedProduct));

    // TODO: 상품등록 실패 - 정책 검증 실패 시 상품 등록 불가

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
        verify(productPolicy, times(1))
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
        // 정책 검증 여부 검증
        verify(productPolicy, times(1))
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

    @Test
    @DisplayName("상품수정 실패 - 이미 상품 판매종료인 경우 수정 불가")
    void modifyProduct_shouldFail_whenAlreadyProductDeleted() {
        // given
        RequestModifyProductDto requestProduct =
                RequestModifyProductDto.builder()
                        .id(5L)
                        .options(List.of(requestUpdateOption()))
                        .build();
        Member member = seller();

        ServiceProductDto serviceProductDto =
                serviceProductDto(requestProduct.getOptions());
        // 요청 상품의 기존 상태 (이미 판매 종료된 상품)
        Product targetProduct = Product.builder()
                .saleStatus(DELETION) // 이미 판매종료
                .options(new ArrayList<>(List.of(
                        ProductOption.builder().id(1L).build())))
                .build();

        // requestDto -> ServiceDto 변환
        given(serviceProductMapper.toServiceDto(requestProduct))
                .willReturn(serviceProductDto);
        // 요청한 셀러 상품 단건 조회
        given(productRepository.findByIdAndSeller(
                requestProduct.getId(), member.getId()))
                .willReturn(Optional.of(targetProduct));
        // 정책에서 예외 발생
        doThrow(new ProductException(PRODUCT_ALREADY_DELETED))
                .when(productPolicy)
                .validateModify(eq(targetProduct), anyList());

        // when
        // then
        ProductException e = assertThrows(ProductException.class, () ->
                productService.modifyProduct(requestProduct, member));
        assertEquals(PRODUCT_ALREADY_DELETED, e.getErrorCode());
    }

}