package com.myecommerce.MyECommerce.service.production;

import com.myecommerce.MyECommerce.dto.production.ServiceProductionDto;
import com.myecommerce.MyECommerce.dto.production.ServiceProductionOptionDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.production.Production;
import com.myecommerce.MyECommerce.exception.ProductionException;
import com.myecommerce.MyECommerce.repository.production.ProductionOptionRepository;
import com.myecommerce.MyECommerce.repository.production.ProductionRepository;
import com.myecommerce.MyECommerce.type.ProductionSaleStatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.myecommerce.MyECommerce.exception.errorcode.ProductionErrorCode.*;
import static com.myecommerce.MyECommerce.type.ProductionSaleStatusType.DELETION;

@Component
@RequiredArgsConstructor
public class ProductionPolicy {

    private final ProductionRepository productionRepository;
    private final ProductionOptionRepository productionOptionRepository;

    /** 등록 정책 검증 **/
    public void validateRegister(ServiceProductionDto productionDto,
                                 Member member) {
        // 판매자별 상품코드 중복체크 정책
        enforceProductionCodeUniquenessPolicy(
                member.getId(), productionDto.getCode());
        // 상품옵션목록 중복체크 정책
        enforceOptionCodeUniquenessPolicy(
                productionDto.getCode(), productionDto.getOptions());
    }

    /** 수정 정책 검증 **/
    public void validateModify(Production production,
                               List<ServiceProductionOptionDto> insertOptionList) {
        // 판매상태 정책 검증
        enforceProductModifySaleStatusPolicy(production.getSaleStatus());
        // 신규 등록할 상품옵션 중복 검증
        enforceOptionCodeUniquenessPolicy(
                production.getCode(), insertOptionList);
    }

    // 상품코드 유일성 검증 정책 (판매자별 상품코드 중복체크)
    private void enforceProductionCodeUniquenessPolicy(Long sellerId,
                                                       String productionCode) {
        productionRepository.findBySellerAndCode(sellerId, productionCode)
                .ifPresent(existingProduction -> {
                    throw new ProductionException(PRODUCT_CODE_ALREADY_REGISTERED);
                });
    }

    // 신규 옵션 코드 유일성 검증 정책 (상품옵션 중복체크)
    private void enforceOptionCodeUniquenessPolicy(
            String productionCode, List<ServiceProductionOptionDto> optionDtoList) {
        // 중복코드 제거된 옵션코드목록 set
        Set<String> deduplicatedOptionCodes = optionDtoList.stream()
                .map(ServiceProductionOptionDto::getOptionCode)
                .collect(Collectors.toSet());

        // 1. 입력받은 옵션코드목록 중 중복 옵션코드 체크
        if (deduplicatedOptionCodes.size() != optionDtoList.size()) {
            throw new ProductionException(PRODUCT_OPTION_CODE_DUPLICATED);
        }

        // 2. 이미 등록된 옵션코드와 중복 체크
        List<Production> duplicatedOptions =
                productionOptionRepository.findByProductionCodeAndOptionCodeIn(
                        productionCode, deduplicatedOptionCodes.stream().toList());
        if (!duplicatedOptions.isEmpty()) {
            throw new ProductionException(PRODUCT_OPTION_CODE_ALREADY_REGISTERED);
        }
    }

    // 상품 수정 시 판매상태 정책
    private void enforceProductModifySaleStatusPolicy(ProductionSaleStatusType saleStatus) {
        if (saleStatus == DELETION) {
            throw new ProductionException(PRODUCT_ALREADY_DELETED);
        }
    }

}
