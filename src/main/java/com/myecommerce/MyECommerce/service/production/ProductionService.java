package com.myecommerce.MyECommerce.service.production;

import com.myecommerce.MyECommerce.dto.production.*;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.production.Production;
import com.myecommerce.MyECommerce.entity.production.ProductionOption;
import com.myecommerce.MyECommerce.exception.ProductionException;
import com.myecommerce.MyECommerce.mapper.ModifyProductionOptionMapper;
import com.myecommerce.MyECommerce.mapper.ProductionMapper;
import com.myecommerce.MyECommerce.mapper.ProductionOptionMapper;
import com.myecommerce.MyECommerce.repository.production.ProductionOptionRepository;
import com.myecommerce.MyECommerce.repository.production.ProductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.myecommerce.MyECommerce.exception.errorcode.ProductionErrorCode.*;
import static com.myecommerce.MyECommerce.type.ProductionSaleStatusType.DELETION;
import static com.myecommerce.MyECommerce.type.ProductionSaleStatusType.ON_SALE;

@Service
@RequiredArgsConstructor
public class ProductionService {

    private final ProductionMapper productionMapper;
    private final ProductionOptionMapper productionOptionMapper;

    private final ModifyProductionOptionMapper modifyProductionOptionMapper;

    private final ProductionRepository productionRepository;
    private final ProductionOptionRepository productionOptionRepository;

    /** 상품등록 **/
    @Transactional
    public ResponseProductionDto registerProduction(RequestProductionDto requestProductionDto,
                                                Member member) {
        // 상품 dto -> entity 변환
        Production production = productionMapper.toEntity(requestProductionDto);

        // 상품옵션목록 dto -> entity 변환
        List<ProductionOption> optionList =
                requestProductionDto.getOptions().stream()
                        .map(productionOptionMapper::toEntity)
                        .toList();

        // 판매자별 상품코드 중복체크
        checkIfProductionCodeExists(member.getId(), requestProductionDto.getCode());

        // 상품옵션목록 중복체크
        checkIfOptionCodeExists(optionList, production.getCode());

        // 상품등록
        Production savedProduction = saveProduction(production, member);

        // 상품옵션등록
        saveProductionOption(optionList, savedProduction);

        // 상품, 상품옵션목록 반환
        return productionMapper.toDto(savedProduction);
    }

    /** 상품수정 **/
    @Transactional
    public ResponseProductionDto modifyProduction(RequestModifyProductionDto requestProductionDto,
                                                  Member member) {
        // 기존 옵션목록 dto -> entity 변환
        List<ProductionOption> requestUpdateOptionList =
                requestProductionDto.getOptions().stream()
                        .filter(option ->
                                option.getId() != null && option.getId() > 0)
                        .map(modifyProductionOptionMapper::toEntity)
                        .toList();

        // 신규 옵션목록 dto -> entity 변환
        List<ProductionOption> requestInsertOptionList =
                requestProductionDto.getOptions().stream()
                        .filter(option ->
                                option.getId() == null || option.getId() <= 0)
                        .map(modifyProductionOptionMapper::toEntity)
                        .toList();

        // 상품 판매자 체크
        Production production = productionRepository.findByIdAndSeller(
                        requestProductionDto.getId(), member.getId())
                .orElseThrow(() -> new ProductionException(NO_EDIT_PERMISSION));

        // 상품 기존 판매상태 체크
        if (production.getSaleStatus() == DELETION) {
            throw new ProductionException(NO_EDIT_DELETION_STATUS);
        }

        // 상품옵션목록 중복체크
        checkIfOptionCodeExists(requestInsertOptionList, production.getCode());

        // 상품 설명, 판매상태 변경
        production.setDescription(requestProductionDto.getDescription());
        production.setSaleStatus(requestProductionDto.getSaleStatus());

        // 상품옵션 수량 변경
        // 기존 데이터와 id가 일치하는 입력데이터 찾아 값 입력
        for (int i = 0; i < requestUpdateOptionList.size(); i++) {
            for(int j = 0; j < production.getOptions().size(); j++) {

                if(production.getOptions().get(j).getId().equals(requestUpdateOptionList.get(i).getId())) {
                    production.getOptions().get(j).setQuantity(requestUpdateOptionList.get(i).getQuantity());
                    break;
                }
            }
        }

        // 신규 상품옵션 추가
        requestInsertOptionList.forEach(option -> {
            // 상품옵션목록의 JPA 연관관계를 위해 옵션에 상품객체 셋팅
            option.setProduction(production);
            // 조회한 상품옵션목록에 신규옵션 추가
            production.getOptions().add(option);
        });

        // 상품, 상품옵션목록 반환
        return productionMapper.toDto(production);
    }


    // 상품 insert
    private Production saveProduction(Production production, Member member) {
        production.setSeller(member.getId());
        production.setSaleStatus(ON_SALE);
        production.setOptions(null);

        // 상품 등록
        return productionRepository.save(production);
    }

    // 상품옵션 insert
    private void saveProductionOption(List<ProductionOption> optionList, Production savedProduction) {
        optionList.forEach(option -> {
            // 상품옵션목록의 JPA 연관관계를 위해 옵션에 상품객체 셋팅
            option.setProduction(savedProduction);
            // 상품옵션목록 등록
            productionOptionRepository.save(option);
        });
    }

    // 상품 중복체크
    private void checkIfProductionCodeExists(Long sellerId, String code) {
        productionRepository.findBySellerAndCode(sellerId, code)
                .ifPresent(existingProduction -> {
                    throw new ProductionException(ALREADY_REGISTERED_CODE);
                });
    }

    // 상품옵션 중복체크
    private void checkIfOptionCodeExists(List<ProductionOption> options, String productionCode) {
        // 중복코드 제거된 옵션코드목록 set
        Set<String> optionCodeSet = options.stream()
                .map(ProductionOption::getOptionCode)
                .collect(Collectors.toSet());

        // 1. 입력받은 옵션코드목록 중 중복데이터 체크
        if (optionCodeSet.size() != options.size()) {
            throw new ProductionException(ENTER_DUPLICATED_OPTION_CODE);
        }

        // 2. 입력받은 옵션코드목록과 등록된 옵션코드목록 중복 체크
        if (!productionOptionRepository.findByProductionCodeAndOptionCodeIn(
                        productionCode, optionCodeSet.stream().toList())
                .isEmpty()) {
            throw new ProductionException(ALREADY_REGISTERED_OPTION_CODE);
        }
    }

}
