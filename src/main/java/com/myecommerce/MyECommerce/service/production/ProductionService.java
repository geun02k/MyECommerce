package com.myecommerce.MyECommerce.service.production;

import com.myecommerce.MyECommerce.dto.production.*;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.production.Production;
import com.myecommerce.MyECommerce.entity.production.ProductionOption;
import com.myecommerce.MyECommerce.exception.ProductionException;
import com.myecommerce.MyECommerce.mapper.ProductionMapper;
import com.myecommerce.MyECommerce.mapper.ProductionOptionMapper;
import com.myecommerce.MyECommerce.repository.production.ProductionOptionRepository;
import com.myecommerce.MyECommerce.repository.production.ProductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.myecommerce.MyECommerce.exception.errorcode.ProductionErrorCode.*;
import static com.myecommerce.MyECommerce.type.ProductionSaleStatusType.ON_SALE;

@Service
@RequiredArgsConstructor
public class ProductionService {

    private final ProductionMapper productionMapper;
    private final ProductionOptionMapper productionOptionMapper;

    private final ProductionRepository productionRepository;
    private final ProductionOptionRepository productionOptionRepository;

    /** 상품등록 **/
    @Transactional
    public ResponseProductionDto registerProduction(RequestProductionDto requestProductionDto,
                                                Member member) {
        // 상품등록
        Production savedProduction = saveProduction(requestProductionDto, member);

        // 상품옵션등록
        saveProductionOption(requestProductionDto.getOptions(), savedProduction);

        // 상품, 상품옵션목록 반환
        return productionMapper.toDto(savedProduction);
    }

    private Production saveProduction(RequestProductionDto requestProductionDto, Member member) {
        // 상품 dto -> entity 변환
        Production productionEntity = productionMapper.toEntity(requestProductionDto);
        productionEntity.setSeller(member.getId());
        productionEntity.setSaleStatus(ON_SALE);
        productionEntity.setOptions(null);

        // 판매자별 상품코드 중복체크
        checkIfProductionCodeExists(member.getId(), requestProductionDto.getCode());

        // 상품 등록
        return productionRepository.save(productionEntity);
    }

    private void saveProductionOption(List<RequestProductionOptionDto> options, Production savedProduction) {
        // 상품옵션목록 dto -> entity 변환
        List<ProductionOption> optionEntityList = options.stream()
                .map(productionOptionMapper::toEntity)
                .toList();

        optionEntityList.forEach(optionEntity -> {
            // 상품옵션목록의 JPA 연관관계를 위해 옵션에 상품객체 셋팅
            optionEntity.setProduction(savedProduction);
            // 상품옵션목록 중복체크
            checkIfOptionCodeExists(optionEntity);
            // 상품옵션목록 등록
            productionOptionRepository.save(optionEntity);
        });
    }

    private void checkIfProductionCodeExists(Long sellerId, String code) {
        productionRepository.findBySellerAndCode(sellerId, code)
                .ifPresent(existingProduction -> {
                    throw new ProductionException(ALREADY_REGISTERED_CODE);
                });
    }

    private void checkIfOptionCodeExists(ProductionOption option) {
        // 상품코드 하위의 옵션코드 중복 체크
        productionOptionRepository.findByProductionIdAndOptionCode(
                        option.getProduction().getId(), option.getOptionCode())
                .ifPresent(existingOption -> {
                    throw new ProductionException(ALREADY_REGISTERED_OPTION_CODE);
                });
    }
}
