package com.myecommerce.MyECommerce.service.product;

import com.myecommerce.MyECommerce.dto.product.ServiceProductDto;
import com.myecommerce.MyECommerce.dto.product.ServiceProductOptionDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.exception.ProductException;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import com.myecommerce.MyECommerce.type.ProductSaleStatusType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static com.myecommerce.MyECommerce.exception.errorcode.ProductErrorCode.*;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.DELETION;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductPolicyTest {

    @Mock
    ProductRepository productRepository;
    @Mock
    ProductOptionRepository productOptionRepository;

    @InjectMocks
    ProductPolicy productPolicy;

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 신규옵션 포함 상품 */
    ServiceProductDto productDto(String code, ServiceProductOptionDto... option) {
        return ServiceProductDto.builder()
                .code(code)
                .options(List.of(option))
                .build();
    }
    ServiceProductDto productDto(String code) {
        return productDto(code, optionDto("optionCode"));
    }

    /** 신규옵션 */
    ServiceProductOptionDto optionDto(String optionCode) {
        return ServiceProductOptionDto.builder()
                .optionCode(optionCode)
                .price(new BigDecimal("10000"))
                .build();
    }

    /** 옵션 미포함 상품 */
    ServiceProductDto productWithoutOptions() {
        return ServiceProductDto.builder()
                .code("code")
                .options(Collections.emptyList())
                .build();
    }

    /** 판매자 권한 사용자 */
    Member seller(Long id) {
        return Member.builder()
                .id(id)
                .build();
    }
    Member seller() {
        return seller(1L);
    }

    /** 상품 */
    Product product(Long sellerId, String productCode, ProductSaleStatusType productSaleStatus) {
        return Product.builder()
                .seller(sellerId)
                .code(productCode)
                .saleStatus(productSaleStatus)
                .build();
    }
    Product product(Long sellerId, String productCode) {
        return product(sellerId, productCode, null);
    }
    Product product(ProductSaleStatusType productSaleStatus) {
        return product(null, null, productSaleStatus);
    }

    /** 단건 상품옵션을 포함하는 상품 */
    Product productWithOption(Long sellerId, String productCode, String optionCode) {
        return Product.builder()
                .seller(sellerId)
                .code(productCode)
                .options(List.of(ProductOption.builder()
                                            .optionCode(optionCode)
                                            .build()))
                .build();
    }
    Product productWithOption(String productCode, String optionCode) {
        return productWithOption(null, productCode, optionCode);
    }

    /* ----------------------
        상품등록정책 Tests
       ---------------------- */

    @Test
    @DisplayName("상품등록정책 통과")
    void validateRegister_shouldPass_WhenAllValid() {
        // given
        Long sellerId = 1L;
        String productCode = "code";
        String optionCode = "optionCode";

        ServiceProductDto product = productDto(productCode, optionDto(optionCode));
        Member seller = seller(sellerId);

        // 중복 상품 미존재
        given(productRepository.findBySellerAndCode(sellerId, productCode))
                .willReturn(Optional.empty());
        // 중복 상품옵션 미존재
        given(productOptionRepository.findBySellerAndProductCodeAndOptionCodeIn(
                sellerId, productCode, List.of(optionCode)))
                .willReturn(Collections.emptyList());

        // when
        // then
        assertDoesNotThrow(() -> productPolicy.validateRegister(product, seller));
    }

    @Test
    @DisplayName("상품등록정책 실패 - 판매자별 상품코드 중복 발생 시 예외발생")
    void validateRegister_shouldFail_whenDuplicatedProductCode() {
        // given
        Long sellerId = 1L;
        String productCode = "code";

        ServiceProductDto product = productDto(productCode);
        Member seller = seller(sellerId);

        // 이미 등록된 동일 상품코드 존재
        given(productRepository.findBySellerAndCode(sellerId, productCode))
                .willReturn(Optional.of(product(sellerId, productCode)));

        // when
        // then
        ProductException exception = assertThrows(ProductException.class, () ->
                productPolicy.validateRegister(product, seller));
        assertEquals(PRODUCT_CODE_ALREADY_REGISTERED, exception.getErrorCode());
    }

    @Test
    @DisplayName("상품등록정책실패 - 상품 옵션 미입력 시 예외발생")
    void validateRegister_shouldFail_whenProductWithoutOptionRegister() {
        // given
        ServiceProductDto invalidProduct = productWithoutOptions(); // 옵션없는 상품
        Member seller = seller();

        // when
        // then
        ProductException e = assertThrows(ProductException.class, () ->
                productPolicy.validateRegister(invalidProduct, seller));
        assertEquals(OPTION_AT_LEAST_ONE_REQUIRED, e.getErrorCode());
    }

    // TODO: 옵션 금액이 0 미만이면 예외 발생 (OPTION_PRICE_NOT_POSITIVE)

    @Test
    @DisplayName("상품등록정책실패 - 중복된 옵션코드 입력 시 예외발생")
    void validateRegister_shouldFail_whenDuplicatedOptionCodeRequest() {
        // given
        Long sellerId = 1L;
        String productCode = "code";

        ServiceProductOptionDto[] options = {
                optionDto("optionCode01"),
                optionDto("optionCode01") // 중복된 옵션코드
        };
        ServiceProductDto product = productDto(productCode, options);
        Member seller = seller(sellerId);

        given(productRepository.findBySellerAndCode(sellerId, productCode))
                .willReturn(Optional.empty());

        // when
        // then
        ProductException exception = assertThrows(ProductException.class, () ->
                productPolicy.validateRegister(product, seller));
        assertEquals(PRODUCT_OPTION_CODE_DUPLICATED, exception.getErrorCode());
    }

    @Test
    @DisplayName("상품등록정책 실패 - 이미 등록된 옵션코드 입력 시 예외발생")
    void validateRegister_shouldFail_whenAlreadyRegisteredOptionCode() {
       // given
        Long sellerId = 1L;
        String productCode = "code";
        String optionCode = "optionCode";

        ServiceProductDto product = productDto(productCode, optionDto(optionCode));
        Member seller = seller(sellerId);

        given(productRepository.findBySellerAndCode(sellerId, productCode))
                .willReturn(Optional.empty());
        // 이미 등록된 기존 동일 옵션코드 존재
        Product duplicatedProduct = productWithOption(sellerId, productCode, optionCode);
        given(productOptionRepository.findBySellerAndProductCodeAndOptionCodeIn(
                sellerId, productCode, List.of(optionCode)))
                .willReturn(List.of(duplicatedProduct));

        // when
        // then
        ProductException exception = assertThrows(ProductException.class, () ->
                productPolicy.validateRegister(product, seller));
        assertEquals(PRODUCT_OPTION_CODE_ALREADY_REGISTERED, exception.getErrorCode());
    }

    /* ----------------------
        상품수정정책 Tests
       ---------------------- */

    @Test
    @DisplayName("상품수정정책 통과")
    void validateModify_shouldPass_whenAllValid() {
        // given
        Long sellerId = 1L;
        String productCode = "code";
        String insertOptionCode = "insertOptionCode";

        Product product = product(sellerId, productCode, ON_SALE);
        ServiceProductOptionDto insertOption  = optionDto(insertOptionCode);

        // 중복 상품옵션 미존재
        given(productOptionRepository.findBySellerAndProductCodeAndOptionCodeIn(
                sellerId, productCode, List.of(insertOptionCode)))
                .willReturn(Collections.emptyList());

        // when
        // then
        assertDoesNotThrow(() ->
                productPolicy.validateModify(product, List.of(insertOption)));
    }

    // TODO: 상품수정정책 통과 - 신규옵션을 미입력 시 정책 통과 (List.of()전달)

    @Test
    @DisplayName("상품수정정책 실패 - 판매상태가 삭제이면 예외발생")
    void validateModify_shouldFail_whenDeletionSaleStatus() {
        // given
        Product deletedProduct = product(DELETION);

        // when
        // then
        ProductException e = assertThrows(ProductException.class, () ->
                productPolicy.validateModify(deletedProduct, List.of()));
        assertEquals(PRODUCT_ALREADY_DELETED, e.getErrorCode());
    }

    @Test
    @DisplayName("상품수정정책 실패 - 신규 옵션 중 옵션코드가 중복되면 예외발생")
    void validateModify_shouldFail_whenDuplicatedOptionCode() {
        // given
        Product product = product(ON_SALE);
        List<ServiceProductOptionDto> duplicatedOptions = List.of(
                optionDto("optionCode01"),
                optionDto("optionCode01")); // 중복

        // when
        // then
        ProductException e = assertThrows(ProductException.class, () ->
                productPolicy.validateModify(product, duplicatedOptions));
        assertEquals(PRODUCT_OPTION_CODE_DUPLICATED, e.getErrorCode());
    }

    @Test
    @DisplayName("상품수정정책 실패 - 신규 옵션 중 이미 등록된 옵션코드를 추가하면 예외발생")
    void validateModify_shouldFail_whenAlreadyRegisteredOptionCode() {
        // given
        Long sellerId = 1L;
        String productCode = "code";
        String registeredOptionCode = "insertOptionCode";

        Product product = product(sellerId, productCode, ON_SALE);
        ServiceProductOptionDto alreadyRegisteredOption = optionDto(registeredOptionCode);

        // 이미 등록된 기존 동일 옵션코드 존재
        Product alreadyRegisteredProduct = productWithOption(productCode, registeredOptionCode);
        given(productOptionRepository.findBySellerAndProductCodeAndOptionCodeIn(
                sellerId, productCode, List.of(registeredOptionCode)))
                .willReturn(List.of(alreadyRegisteredProduct));

        // when
        // then
        ProductException e = assertThrows(ProductException.class, () ->
                productPolicy.validateModify(product, List.of(alreadyRegisteredOption)));
        assertEquals(PRODUCT_OPTION_CODE_ALREADY_REGISTERED, e.getErrorCode());
    }

}