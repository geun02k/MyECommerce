package com.myecommerce.MyECommerce.service.product;

import com.myecommerce.MyECommerce.dto.production.ServiceProductionDto;
import com.myecommerce.MyECommerce.dto.production.ServiceProductionOptionDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.exception.ProductionException;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static com.myecommerce.MyECommerce.exception.errorcode.ProductionErrorCode.*;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.SELLER;
import static com.myecommerce.MyECommerce.type.ProductionSaleStatusType.DELETION;
import static com.myecommerce.MyECommerce.type.ProductionSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductionPolicyTest {

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
    ServiceProductionDto createProductionForInsert() {
        ServiceProductionOptionDto option =
                createOptionForInsert("optionCode");
        return ServiceProductionDto.builder()
                        .id(null)
                        .code("code")
                        .saleStatus(null)
                        .options(new ArrayList<>(List.of(option)))
                        .build();
    }

    /** 신규옵션 */
    ServiceProductionOptionDto createOptionForInsert(String optionCode) {
        return ServiceProductionOptionDto.builder()
                .id(null)
                .optionCode(optionCode)
                .build();
    }

    /** 옵션 미포함 상품 */
    ServiceProductionDto createProductionWithoutOptionsForInsert() {
        return ServiceProductionDto.builder()
                .id(null)
                .code("code")
                .saleStatus(null)
                .options(Collections.emptyList())
                .build();
    }

    /** 판매자 권한 사용자 */
    Member seller() {
        return Member.builder()
                .id(1L)
                .roles(List.of(MemberAuthority.builder()
                        .authority(SELLER)
                        .build()))
                .build();
    }

    /* ----------------------
        상품등록정책 Tests
       ---------------------- */

    @Test
    @DisplayName("상품등록정책통과")
    void validateRegister_shouldPass_WhenAllValid() {
        // given
        ServiceProductionOptionDto option = ServiceProductionOptionDto.builder()
                .id(null)
                .optionCode("optionCode")
                .build();
        ServiceProductionDto production = ServiceProductionDto.builder()
                .id(null)
                .code("code")
                .saleStatus(null)
                .options(new ArrayList<>(List.of(option)))
                .build();

        given(productRepository.findBySellerAndCode(anyLong(), anyString()))
                .willReturn(Optional.empty());
        given(productOptionRepository
                .findByProductCodeAndOptionCodeIn(anyString(), anyList()))
                .willReturn(Collections.emptyList());

        // when
        // then
        assertDoesNotThrow(() ->
                productPolicy.validateRegister(production, seller()));
    }

    @Test
    @DisplayName("상품등록정책실패 - 판매자별 상품코드 중복 발생 시 예외발생")
    void validationRegister_shouldThrowException_whenDuplicatedProductionCode() {
        // given
        ServiceProductionDto production = createProductionForInsert();
        Member seller = seller();

        // 이미 등록된 동일 상품코드 존재
        given(productRepository
                .findBySellerAndCode(seller.getId(), production.getCode()))
                .willReturn(Optional.of(Product.builder()
                                .id(1L)
                                .code(production.getCode())
                                .build()));

        // when
        // then
        ProductionException exception =
                assertThrows(ProductionException.class, () ->
                        productPolicy.validateRegister(production, seller));
        assertEquals(PRODUCT_CODE_ALREADY_REGISTERED, exception.getErrorCode());
    }

    @Test
    @DisplayName("상품등록정책실패 - 중복된 옵션코드 입력 시 예외발생")
    void validateRegister_shouldThrowException_whenDuplicatedOptionCodeRequest() {
        // given
        ServiceProductionDto production = createProductionWithoutOptionsForInsert();
        production.setOptions(List.of(
                createOptionForInsert("optionCode01"),
                createOptionForInsert("optionCode01"))); // 중복
        Member seller = seller();

        given(productRepository.
                findBySellerAndCode(seller.getId(), production.getCode()))
                .willReturn(Optional.empty());

        // when
        // then
        ProductionException exception =
                assertThrows(ProductionException.class, () ->
                        productPolicy.validateRegister(production, seller));
        assertEquals(PRODUCT_OPTION_CODE_DUPLICATED, exception.getErrorCode());
    }

    @Test
    @DisplayName("상품등록정책실패 - 이미 등록된 옵션코드 입력 시 예외발생")
    void validateRegister_shouldThrowException_whenAlreadyRegisteredOptionCode() {
       // given
        ServiceProductionDto production = createProductionForInsert();
        Member seller = seller();

        given(productRepository.
                findBySellerAndCode(seller.getId(), production.getCode()))
                .willReturn(Optional.empty());
        // 이미 등록된 기존 동일 옵션코드 존재
        given(productOptionRepository.
                findByProductCodeAndOptionCodeIn(
                        production.getCode(), List.of("optionCode")))
                .willReturn(List.of(Product.builder()
                                    .code("code")
                                    .options(List.of(ProductOption.builder()
                                            .optionCode("optionCode")
                                            .build()))
                                    .build()));

        // when
        // then
        ProductionException exception =
                assertThrows(ProductionException.class, () ->
                        productPolicy.validateRegister(production, seller));
        assertEquals(PRODUCT_OPTION_CODE_ALREADY_REGISTERED, exception.getErrorCode());
    }

    /* ----------------------
        상품수정정책 Tests
       ---------------------- */

    @Test
    @DisplayName("상품수정정책 통과")
    void validateModify_shouldPass_whenAllValid() {
        // given
        Product production = Product.builder()
                .code("code")
                .saleStatus(ON_SALE)
                .build();
        ServiceProductionOptionDto insertOption  =
                ServiceProductionOptionDto.builder()
                        .id(null)
                        .optionCode("insertOptionCode")
                        .build();

        given(productOptionRepository.findByProductCodeAndOptionCodeIn(
                "code", List.of("insertOptionCode")))
                .willReturn(Collections.emptyList());

        // when
        // then
        assertDoesNotThrow(() ->
                productPolicy.validateModify(production, List.of(insertOption)));
    }

    @Test
    @DisplayName("상품수정정책 실패 - 판매상태가 삭제이면 예외발생")
    void validateModify_shouldFail_whenDeletionSaleStatus() {
        // given
        Product deletedProduction = Product.builder()
                .saleStatus(DELETION).build();

        // when
        // then
        ProductionException e = assertThrows(ProductionException.class, () ->
                productPolicy.validateModify(
                        deletedProduction, Collections.emptyList()));
        assertEquals(PRODUCT_ALREADY_DELETED, e.getErrorCode());
    }

    @Test
    @DisplayName("상품수정정책 실패 - 신규 옵션 중 옵션코드 중복되면 예외발생")
    void validateModify_shouldFail_whenDuplicatedOptionCode() {
        // given
        Product production = Product.builder()
                .saleStatus(ON_SALE)
                .build();
        List<ServiceProductionOptionDto> invalidOptions = List.of(
                createOptionForInsert("optionCode01"),
                 createOptionForInsert("optionCode01")); // 중복

        // when
        // then
        ProductionException e = assertThrows(ProductionException.class, () ->
                productPolicy.validateModify(production, invalidOptions));
        assertEquals(PRODUCT_OPTION_CODE_DUPLICATED, e.getErrorCode());
    }

    @Test
    @DisplayName("상품수정정책 실패 - 신규 옵션 중 이미 등록된 옵션코드를 추가하면 예외발생")
    void validateModify_shouldFail_whenAlreadyRegisteredOptionCode() {
        // given
        Product production = Product.builder()
                .code("code")
                .saleStatus(ON_SALE)
                .build();
        ServiceProductionOptionDto invalidOption =
                createOptionForInsert("insertOptionCode");

        // 이미 등록된 기존 동일 옵션코드 존재
        given(productOptionRepository.
                findByProductCodeAndOptionCodeIn(
                        production.getCode(), List.of("insertOptionCode")))
                .willReturn(List.of(Product.builder()
                        .code("code")
                        .options(List.of(ProductOption.builder()
                                .optionCode("insertOptionCode")
                                .build()))
                        .build()));

        // when
        // then
        ProductionException e = assertThrows(ProductionException.class, () ->
                productPolicy.validateModify(
                        production, List.of(invalidOption)));
        assertEquals(PRODUCT_OPTION_CODE_ALREADY_REGISTERED, e.getErrorCode());
    }
}