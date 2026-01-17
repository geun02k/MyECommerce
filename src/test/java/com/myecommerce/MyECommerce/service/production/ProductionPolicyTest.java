package com.myecommerce.MyECommerce.service.production;

import com.myecommerce.MyECommerce.dto.production.ServiceProductionDto;
import com.myecommerce.MyECommerce.dto.production.ServiceProductionOptionDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.production.Production;
import com.myecommerce.MyECommerce.entity.production.ProductionOption;
import com.myecommerce.MyECommerce.exception.ProductionException;
import com.myecommerce.MyECommerce.repository.production.ProductionOptionRepository;
import com.myecommerce.MyECommerce.repository.production.ProductionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.myecommerce.MyECommerce.exception.errorcode.ProductionErrorCode.*;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.SELLER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductionPolicyTest {

    @Mock
    ProductionRepository productionRepository;
    @Mock
    ProductionOptionRepository productionOptionRepository;

    @InjectMocks
    ProductionPolicy productionPolicy;

    /* ------------------
        Test Fixtures
       ------------------ */

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

    ServiceProductionOptionDto createOptionForInsert(String optionCode) {
        return ServiceProductionOptionDto.builder()
                .id(null)
                .optionCode(optionCode)
                .build();
    }

    ServiceProductionDto createProductionWithoutOptionsForInsert() {
        return ServiceProductionDto.builder()
                .id(null)
                .code("code")
                .saleStatus(null)
                .options(Collections.emptyList())
                .build();
    }

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

        given(productionRepository.findBySellerAndCode(anyLong(), anyString()))
                .willReturn(Optional.empty());
        given(productionOptionRepository
                .findByProductionCodeAndOptionCodeIn(anyString(), anyList()))
                .willReturn(Collections.emptyList());

        // when
        // then
        assertDoesNotThrow(() ->
                productionPolicy.validateRegister(production, seller()));
    }

    @Test
    @DisplayName("상품등록정책실패 - 판매자별 상품코드 중복 발생 시 예외발생")
    void validationRegister_shouldThrowException_whenDuplicatedProductionCode() {
        // given
        ServiceProductionDto production = createProductionForInsert();
        Member seller = seller();

        // 이미 등록된 동일 상품코드 존재
        given(productionRepository
                .findBySellerAndCode(seller.getId(), production.getCode()))
                .willReturn(Optional.of(Production.builder()
                                .id(1L)
                                .code(production.getCode())
                                .build()));

        // when
        // then
        ProductionException exception =
                assertThrows(ProductionException.class, () ->
                        productionPolicy.validateRegister(production, seller));
        assertEquals(PRODUCT_CODE_ALREADY_REGISTERED, exception.getErrorCode());
    }

    @Test
    @DisplayName("상품등록정책실패 - 중복된 옵션코드 입력 시 예외발생")
    void validateRegister_shouldThrowException_whenDuplicatedOptionCodeRequest() {
        // given
        ServiceProductionDto production = createProductionWithoutOptionsForInsert();
        ServiceProductionOptionDto option =
                createOptionForInsert("optionCode01");
        ServiceProductionOptionDto duplicatedOption =
                createOptionForInsert("optionCode01"); // 중복
        production.setOptions(List.of(option, duplicatedOption));
        Member seller = seller();

        given(productionRepository.
                findBySellerAndCode(seller.getId(), production.getCode()))
                .willReturn(Optional.empty());

        // when
        // then
        ProductionException exception =
                assertThrows(ProductionException.class, () ->
                        productionPolicy.validateRegister(production, seller));
        assertEquals(PRODUCT_OPTION_CODE_DUPLICATED, exception.getErrorCode());
    }

    @Test
    @DisplayName("상품등록정책실패 - 이미 등록된 옵션코드 입력 시 예외발생")
    void validateRegister_shouldThrowException_whenAlreadyRegisteredOptionCode() {
       // given
        ServiceProductionDto production = createProductionForInsert();
        Member seller = seller();

        given(productionRepository.
                findBySellerAndCode(seller.getId(), production.getCode()))
                .willReturn(Optional.empty());
        // 이미 등록된 기존 동일 옵션코드 존재
        given(productionOptionRepository.
                findByProductionCodeAndOptionCodeIn(
                        production.getCode(), List.of("optionCode")))
                .willReturn(List.of(Production.builder()
                                    .code("code")
                                    .options(List.of(ProductionOption.builder()
                                            .optionCode("optionCode")
                                            .build()))
                                    .build()));

        // when
        // then
        ProductionException exception =
                assertThrows(ProductionException.class, () ->
                        productionPolicy.validateRegister(production, seller));
        assertEquals(PRODUCT_OPTION_CODE_ALREADY_REGISTERED, exception.getErrorCode());
    }

}