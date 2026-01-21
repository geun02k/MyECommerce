package com.myecommerce.MyECommerce.service.product;

import com.myecommerce.MyECommerce.dto.product.ServiceProductDto;
import com.myecommerce.MyECommerce.dto.product.ServiceProductOptionDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.exception.ProductException;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import com.myecommerce.MyECommerce.type.ProductSaleStatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.myecommerce.MyECommerce.exception.errorcode.ProductErrorCode.*;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.DELETION;

@Component
@RequiredArgsConstructor
public class ProductPolicy {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    /** 등록 정책 검증 **/
    public void validateRegister(ServiceProductDto productDto,
                                 Member member) {
        // 판매자별 상품코드 중복체크 정책
        enforceProductCodeUniquenessPolicy(
                member.getId(), productDto.getCode());
        // 옵션 최소 1건 필수 검증
        enforceAtLeastOneOptionPolicy(productDto.getOptions());
        // 상품옵션목록 중복체크 정책
        enforceOptionCodeUniquenessPolicy(
                productDto.getCode(), productDto.getOptions());
    }

    /** 수정 정책 검증 **/
    public void validateModify(Product product,
                               List<ServiceProductOptionDto> insertOptionList) {
        // 판매상태 정책 검증
        enforceProductModifySaleStatusPolicy(product.getSaleStatus());
        // 신규 등록할 상품옵션 중복 검증
        enforceOptionCodeUniquenessPolicy(
                product.getCode(), insertOptionList);
    }

    // 상품코드 유일성 검증 정책 (판매자별 상품코드 중복체크)
    private void enforceProductCodeUniquenessPolicy(Long sellerId,
                                                    String productCode) {
        productRepository.findBySellerAndCode(sellerId, productCode)
                .ifPresent(existingProduction -> {
                    throw new ProductException(PRODUCT_CODE_ALREADY_REGISTERED);
                });
    }

    // 옵션 최소 1건 필수 입력 검증 정책
    private void enforceAtLeastOneOptionPolicy(
            List<ServiceProductOptionDto> options) {
        if(options == null || options.isEmpty()) {
            throw new ProductException(OPTION_AT_LEAST_ONE_REQUIRED);
        }
    }

    // 신규 옵션 코드 유일성 검증 정책 (상품옵션 중복체크)
    private void enforceOptionCodeUniquenessPolicy(
            String productCode, List<ServiceProductOptionDto> optionDtoList) {
        // 중복코드 제거된 옵션코드목록 set
        Set<String> deduplicatedOptionCodes = optionDtoList.stream()
                .map(ServiceProductOptionDto::getOptionCode)
                .collect(Collectors.toSet());

        // 1. 입력받은 옵션코드목록 중 중복 옵션코드 체크
        if (deduplicatedOptionCodes.size() != optionDtoList.size()) {
            throw new ProductException(PRODUCT_OPTION_CODE_DUPLICATED);
        }

        // 2. 이미 등록된 옵션코드와 중복 체크
        List<Product> duplicatedOptions =
                productOptionRepository.findByProductCodeAndOptionCodeIn(
                        productCode, deduplicatedOptionCodes.stream().toList());
        if (!duplicatedOptions.isEmpty()) {
            throw new ProductException(PRODUCT_OPTION_CODE_ALREADY_REGISTERED);
        }
    }

    // 상품 수정 시 판매상태 정책
    private void enforceProductModifySaleStatusPolicy(ProductSaleStatusType saleStatus) {
        if (saleStatus == DELETION) {
            throw new ProductException(PRODUCT_ALREADY_DELETED);
        }
    }

}
