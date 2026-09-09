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
import org.mockito.Spy;
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

    // 실제 객체를 바로 생성 (MapStruct가 만든 구현체 사용)
    @Spy
    private final ServiceProductMapper serviceProductMapper = ServiceProductMapper.INSTANCE;
    
    @Mock
    private ProductPolicy productPolicy;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductOptionRepository productOptionRepository;
    @Mock
    private StockCacheService stockCacheService;

    @InjectMocks
    private ProductService productService;

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 회원 */
    Member seller(Long id) {
        return Member.builder().id(id).build();
    }

    /** 요청 상품 */
    RequestModifyProductDto requestProductDto(Long id,
                                              ProductSaleStatusType saleStatus,
                                              RequestModifyProductOptionDto optionDto) {
        return RequestModifyProductDto.builder()
                .id(id)
                .saleStatus(saleStatus)
                .options(List.of(optionDto))
                .build();
    }
    RequestModifyProductDto requestProductDto(Long id) {
        return requestProductDto(id, null, new RequestModifyProductOptionDto());
    }

    /** 유효한 수정할 상품 옵션 요청 */
    RequestModifyProductOptionDto requestUpdateOption(Long id, int quantity) {
        return RequestModifyProductOptionDto.builder()
                .id(id)
                .optionCode("existingOptionCode")
                .quantity(quantity)
                .build();
    }
    /** 유효한 등록할 상품 옵션 요청 */
    RequestModifyProductOptionDto requestInsertOption(String optionCode, int quantity) {
        return RequestModifyProductOptionDto.builder()
                .optionCode(optionCode)
                .quantity(quantity)
                .build();
    }

    /** 등록되어있던 기존 상품 옵션 Entity */
    ProductOption originOption(Long id, int quantity) {
        return ProductOption.builder()
                .id(id)
                .quantity(quantity)
                .build();
    }

    /** 수정할 상품 Entity - 판매중인 상태 */
    Product originOnSaleProduct(Long id, Long sellerId, String description,
                                ProductOption option) {
        return Product.builder()
                .id(id)
                .seller(sellerId)
                .description(description)
                .saleStatus(ON_SALE)
                .options(new ArrayList<>(List.of(option))) // 가변리스트전달
                .build();
    }
    Product originOnSaleProduct(Long id, Long sellerId, String description) {
        return originOnSaleProduct(id, sellerId, description, originOption(1L, 50));
    }

    /** 수정할 상품 Entity */
    Product originProduct(Long id, ProductSaleStatusType saleStatus) {
        return Product.builder()
                .id(id)
                .saleStatus(saleStatus)
                .options(List.of())
                .build();
    }

    /* ------------------
        Helper Method
       ------------------ */

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
    @DisplayName("상품등록 성공 - 신규 상품 및 상품옵션 등록 후 재고 등록")
    void registerProduct_shouldInsertProductAndOption_whenValidProduct() {
        // given
        // 요청 상품옵션 DTO
        RequestProductOptionDto requestOptionDto = RequestProductOptionDto.builder()
                .optionCode("S-BL")
                .optionName("스몰사이즈 블루컬러")
                .price(new BigDecimal("67900"))
                .quantity(30)
                .build();
        // 요청 상품 DTO
        RequestProductDto requestProductDto = RequestProductDto.builder()
                .code("RM-JK-D11S51")
                .name("제 품 명")
                .category(WOMEN_CLOTHING)
                .options(List.of(requestOptionDto))
                .build();
        // 요청 회원 DTO
        Member member = seller(10L);

        // 상품 저장
        given(productRepository.save(any()))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        ResponseProductDto response =
                productService.registerProduct(requestProductDto, member);

        // then
        // 정책 실행 검증
        verify(productPolicy).validateRegister(any(ServiceProductDto.class), eq(member));
        // 상품 저장 검증
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        // 상품옵션 저장 검증
        ArgumentCaptor<ProductOption> optionCaptor = ArgumentCaptor.forClass(ProductOption.class);
        Product capturedProduct = productCaptor.getValue();
        verify(productOptionRepository).save(optionCaptor.capture());
        // 상품 캐시 재고 등록 검증
        verify(stockCacheService).saveProductStock(capturedProduct);

        // 상품 전달인자 검증
        assertEquals(ON_SALE, capturedProduct.getSaleStatus());
        assertEquals(10L, capturedProduct.getSeller());
        assertEquals("RM-JK-D11S51", capturedProduct.getCode());
        assertEquals("제 품 명", capturedProduct.getName());
        assertEquals(WOMEN_CLOTHING, capturedProduct.getCategory());

        // 상품옵션 전달인자 검증
        ProductOption capturedOption = optionCaptor.getValue();
        assertSame(capturedProduct, capturedOption.getProduct());

        // 응답 검증
        assertEquals(ON_SALE, response.getSaleStatus());
        assertEquals(10L, response.getSeller());
        assertEquals("RM-JK-D11S51", response.getCode());
    }

    // TODO: 상품등록 실패 - 정책 검증 실패 시 상품 등록 불가

    /* ----------------------
        상품수정 Tests
       ---------------------- */

    @Test
    @DisplayName("상품수정 성공 - 상품이 판매중이면 상품의 설명 및 상태 수정")
    void modifyProduct_shouldUpdateProduct_whenProductOnSale() {
        // given
        Long productId = 5L;
        Long sellerId = 10L;
        // 요청 상품 DTO
        RequestModifyProductDto requestProduct = RequestModifyProductDto.builder()
                .id(productId)
                .description("수정한 상품 설명입니다.")
                .saleStatus(ON_SALE) // 판매중 유지
                .build();
        // 요청 회원 DTO
        Member member = seller(sellerId);

        // 요청 상품 조회
        Product targetProduct = originOnSaleProduct(
                productId, sellerId, "설명입니다.");
        given(productRepository.findByIdAndSeller(productId, sellerId))
                .willReturn(Optional.of(targetProduct));

        // when
        ResponseProductDto response = productService.modifyProduct(requestProduct, member);

        // then
        // 상품 조회 검증
        verify(productRepository).findByIdAndSeller(productId, sellerId);
        // 정책 실행 검증
        verify(productPolicy).validateModify(any(Product.class), anyList());

        // 상품 수정 검증
        assertEquals(productId, targetProduct.getId());
        assertEquals(sellerId, targetProduct.getSeller());
        assertEquals(ON_SALE, targetProduct.getSaleStatus());
        assertEquals("수정한 상품 설명입니다.", targetProduct.getDescription());

        // 응답 검증
        assertEquals(productId, response.getId());
        assertEquals(sellerId, response.getSeller());
    }

    @Test
    @DisplayName("상품수정 성공 - 상품이 판매중이면 옵션 수량 변경 후 재고 등록")
    void modifyProduct_shouldUpdateOptionAndSaveStock_whenProductOnSale() {
        // given
        Long productId = 5L;
        Long sellerId = 10L;
        Long optionId = 1L;
        int originOptionQuantity = 10;  // 기존 옵션 수량
        int requestOptionQuantity = 50; // 수정 요청한 옵션 수량
        // 요청 상품옵션 DTO 목록
        RequestModifyProductOptionDto requestUpdateOption =
                requestUpdateOption(optionId, requestOptionQuantity);
        // 요청 상품 DTO
        RequestModifyProductDto requestProduct =
                requestProductDto(productId, ON_SALE, requestUpdateOption);
        // 요청 회원 DTO
        Member member = seller(sellerId);

        // 요청 상품 조회
        ProductOption originOption = originOption(optionId, originOptionQuantity);
        Product targetProduct = originOnSaleProduct(
                productId, sellerId, "설명입니다.", originOption);
        given(productRepository.findByIdAndSeller(productId, sellerId))
                .willReturn(Optional.of(targetProduct));

        // when
        productService.modifyProduct(requestProduct, member);

        // then
        // 상품 조회 검증
        verify(productRepository).findByIdAndSeller(productId, sellerId);
        // 정책 실행 검증
        verify(productPolicy).validateModify(any(Product.class), anyList());
        // 상품 재고 등록 검증
        verify(stockCacheService).saveProductStock(targetProduct);
        verify(stockCacheService, never()).deleteProductStock(any());

        // 상품옵션 수정 검증
        ProductOption updatedOption = filterOption(targetProduct, optionId);
        assertEquals(1L, updatedOption.getId());
        assertEquals(50, updatedOption.getQuantity());
    }

    @Test
    @DisplayName("상품수정 성공 - 상품이 판매중이면 신규 옵션 등록 후 재고 등록")
    void modifyProduct_shouldInsertOptionAndSaveStock_whenProductOnSale() {
        // given
        Long productId = 5L;
        Long sellerId = 10L;
        // 요청 상품옵션 DTO 목록
        RequestModifyProductOptionDto requestInsertOption =
                requestInsertOption("optionCode", 20);
        // 요청 상품 DTO
        RequestModifyProductDto requestProduct =
                requestProductDto(productId, ON_SALE, requestInsertOption);
        // 요청 회원 DTO
        Member member = seller(10L);

        // 요청 상품 조회
        Product targetProduct = originOnSaleProduct(
                productId, sellerId, "설명입니다.");
        given(productRepository.findByIdAndSeller(productId, sellerId))
                .willReturn(Optional.of(targetProduct));

        // when
        productService.modifyProduct(requestProduct, member);

        // then
        // 상품 조회 검증
        verify(productRepository).findByIdAndSeller(productId, sellerId);
        // 정책 실행 검증
        verify(productPolicy).validateModify(any(Product.class), anyList());
        // 상품 재고 등록 검증
        verify(stockCacheService).saveProductStock(targetProduct);
        verify(stockCacheService, never()).deleteProductStock(any());

        // 상품옵션 신규등록 검증
        ProductOption insertedOption = filterOption(targetProduct, null);
        assertEquals("optionCode", insertedOption.getOptionCode());
        assertEquals(20, insertedOption.getQuantity());
        assertSame(insertedOption.getProduct(), targetProduct);
    }

    @Test
    @DisplayName("상품수정 성공 - 상품 판매중단으로 변경 시 상품 수정 후 캐시 재고 삭제")
    void modifyProduct_shouldUpdateProductAndDeleteCacheStock_whenProductDisContinued() {
        // given
        Long productId = 5L;
        Long sellerId = 10L;
        Long updateOptionId = 1L;
        int originOptionQuantity = 0;  // 기존 옵션 수량
        int updateOptionQuantity = 10;  // 수정 요청한 옵션 수량
        // 요청 상품옵션 DTO 목록
        RequestModifyProductOptionDto requestUpdateOption =
                requestUpdateOption(updateOptionId, updateOptionQuantity);
        RequestModifyProductOptionDto requestInsertOption =
                requestInsertOption("optionCode", 20);
        // 요청 상품 DTO
        RequestModifyProductDto requestProduct =
                RequestModifyProductDto.builder()
                        .id(productId)
                        .description("수정한 상품 설명입니다.")
                        .saleStatus(DISCONTINUED) // 판매중단으로 변경
                        .options(List.of(requestUpdateOption, requestInsertOption))
                        .build();
        // 요청 회원 DTO
        Member member = seller(sellerId);

        // 요청 상품 조회
        ProductOption originOption =
                originOption(updateOptionId, originOptionQuantity);
        Product targetProduct = originOnSaleProduct(
                productId, sellerId, "설명입니다.", originOption);
        given(productRepository.findByIdAndSeller(productId, sellerId))
                .willReturn(Optional.of(targetProduct));

        // when
        productService.modifyProduct(requestProduct, member);

        // then
        // 상품 조회 검증
        verify(productRepository).findByIdAndSeller(productId, sellerId);
        // 정책 실행 검증
        verify(productPolicy).validateModify(any(Product.class), anyList());
        // 상품 재고 등록 검증
        verify(stockCacheService, never()).saveProductStock(targetProduct);
        verify(stockCacheService).deleteProductStock(targetProduct);

        // 1. 상품 수정 검증
        assertEquals("수정한 상품 설명입니다.", targetProduct.getDescription());
        assertEquals(DISCONTINUED, targetProduct.getSaleStatus());
        // 2. 상품옵션 수정 검증
        ProductOption responseUpdatedOption = filterOption(targetProduct, updateOptionId);
        assertEquals(10, responseUpdatedOption.getQuantity());
        // 3. 상품옵션 신규등록 검증
        ProductOption responseInsertedOption = filterOption(targetProduct, null);
        assertEquals(20, responseInsertedOption.getQuantity());
    }

    @Test
    @DisplayName("상품수정 실패 - 이미 상품 판매종료인 경우 수정 불가") // 정책검증실패
    void modifyProduct_shouldFail_whenAlreadyProductDeleted() {
        // given
        Long sellerId = 10L;
        Long productId = 5L;
        RequestModifyProductDto requestProduct = requestProductDto(productId);
        Member member = seller(sellerId);

        // 요청 상품 조회 (판매종료 상태)
        Product deletionProduct = originProduct(productId, DELETION);
        given(productRepository.findByIdAndSeller(productId, sellerId))
                .willReturn(Optional.of(deletionProduct));
        // 정책에서 예외 발생
        doThrow(new ProductException(PRODUCT_ALREADY_DELETED))
                .when(productPolicy)
                .validateModify(eq(deletionProduct), anyList());

        // when
        // then
        ProductException e = assertThrows(ProductException.class, () ->
                productService.modifyProduct(requestProduct, member));
        assertEquals(PRODUCT_ALREADY_DELETED, e.getErrorCode());
    }

    // TODO: 일치하는 상품 조회 실패 시 상품수정 불가 (PRODUCT_EDIT_FORBIDDEN)
    // TODO: 기존 등록된 상품 옵션이 아닌 경우 상품수정 불가 (PRODUCT_OPTION_NOT_EXIST)
}