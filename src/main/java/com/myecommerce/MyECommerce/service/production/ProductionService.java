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
import com.myecommerce.MyECommerce.type.ProductionOrderByStdType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.myecommerce.MyECommerce.exception.errorcode.ProductionErrorCode.*;
import static com.myecommerce.MyECommerce.type.ProductionOrderByStdType.*;
import static com.myecommerce.MyECommerce.type.ProductionSaleStatusType.DELETION;
import static com.myecommerce.MyECommerce.type.ProductionSaleStatusType.ON_SALE;

@Service
@RequiredArgsConstructor
public class ProductionService {

    private final static int PRODUCTION_NAME_LENGTH = 200;

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
        // 상품 조회 및 상품 판매자 체크
        Production production = getProductionEntityByIdAndSeller(
                requestProductionDto.getId(), member.getId());

        // validation check
        updateProductionValidationCheck(production, requestProductionDto);

        // 수정할 옵션목록 dto -> entity 변환
        List<ProductionOption> requestUpdateOptionList =
                convertToUpdateOptionEntities(requestProductionDto);
        // 신규 저장 옵션목록 dto -> entity 변환
        List<ProductionOption> requestInsertOptionList =
                convertToInsertOptionEntities(requestProductionDto);

        // 상품 설명, 판매상태 변경
        updateProduction(production, requestProductionDto);
        // 기존 상품옵션 수량 변경
        updateOptions(requestUpdateOptionList, production);
        // 신규 상품옵션 추가
        insertOptions(requestInsertOptionList, production);

        // 상품, 상품옵션목록 반환
        return productionMapper.toDto(production);
    }

    /** 상품목록조회 **/
    public Page<ResponseProductionDto> searchProductionList(
            RequestSearchProductionDto requestDto) {
        // 키워드 200자로 제한
        String limitedKeyword = getLimitedKeyword(requestDto.getKeyword());

        // 정렬순서에 따른 상품목록조회
        Page<Production> productionPage = getSortedProductions(requestDto, limitedKeyword);

        // entity -> dto로 변환
        return  productionPage.map(productionMapper::toDto);
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

    // 수정할 옵션목록 필터링해 dto -> entity 변환
    private List<ProductionOption> convertToUpdateOptionEntities(
            RequestModifyProductionDto requestProductionDto) {
        return requestProductionDto.getOptions().stream()
                .filter(option ->
                        option.getId() != null && option.getId() > 0)
                .map(modifyProductionOptionMapper::toEntity)
                .toList();
    }

    // 신규 저장할 옵션목록 필터링해 dto -> entity 변환
    private List<ProductionOption> convertToInsertOptionEntities(
            RequestModifyProductionDto requestProductionDto) {
        return requestProductionDto.getOptions().stream()
                .filter(option ->
                        option.getId() == null || option.getId() <= 0)
                .map(modifyProductionOptionMapper::toEntity)
                .toList();
    }

    // 상품수정 validation check
    private void updateProductionValidationCheck(Production production,
                                                 RequestModifyProductionDto requestProductionDto) {
        // 상품 기존 판매상태 체크
        if (production.getSaleStatus() == DELETION) {
            throw new ProductionException(NO_EDIT_DELETION_STATUS);
        }

        // 상품옵션목록 중복체크
        List<ProductionOption> requestInsertOptionList =
                convertToInsertOptionEntities(requestProductionDto);
        checkIfOptionCodeExists(requestInsertOptionList, production.getCode());

        // 수정 요청 옵션 중 잘못된 옵션ID 체크
        List<ProductionOption> requestUpdateOptionList =
                convertToUpdateOptionEntities(requestProductionDto);

        // 1. 기존옵션ID목록 Set으로 변환
        Set<Long> productionOptionIds = production.getOptions().stream()
                .map(ProductionOption::getId)
                .collect(Collectors.toSet());

        // 2. 요청옵션ID목록이 기존옵션ID목록에 모두 포함되는지 확인
        boolean isExistAllOptionIds = requestUpdateOptionList.stream()
                .allMatch(option -> productionOptionIds.contains(option.getId()));

        // 3. 포함되지않는 경우에 대한 예외처리
        if (!isExistAllOptionIds) {
            throw new ProductionException(NO_EDIT_FOR_NOT_EXIST_OPTION);
        }
    }

    // 상품ID, 셀러ID와 일치하는 상품 단건 조회
    private Production getProductionEntityByIdAndSeller(Long productionId, Long sellerId) {
        return productionRepository.findByIdAndSeller(productionId, sellerId)
                .orElseThrow(() -> new ProductionException(NO_EDIT_PERMISSION));
    }

    // 상품 Entity 데이터 변경
    private void updateProduction(Production production,
                                  RequestModifyProductionDto requestModifyProductionDto) {
        production.setDescription(requestModifyProductionDto.getDescription());
        production.setSaleStatus(requestModifyProductionDto.getSaleStatus());
    }

    // 상품 Entity의 필드인 상품옵션 Entity의 수량 변경
    private void updateOptions(List<ProductionOption> requestUpdateOptionList,
                               Production production) {
        // 기존상품옵션 MAP으로 매핑
        Map<Long, ProductionOption> originOptionMap = production.getOptions().stream()
                .collect(Collectors.toMap(
                        ProductionOption::getId, option -> option));

        // 기존 데이터와 id가 일치하는 입력데이터 찾아 값 입력
        for (ProductionOption requestOption : requestUpdateOptionList) {
            ProductionOption foundOption = originOptionMap.get(requestOption.getId());

            if (foundOption != null) {
                foundOption.setQuantity(requestOption.getQuantity());
            } else {
                throw new ProductionException(NO_EDIT_FOR_NOT_EXIST_OPTION);
            }
        }
    }

    // 상품 Entity의 필드인 상품옵션 Entity에 신규옵션 추가
    private void insertOptions(List<ProductionOption> requestInsertOptionList, Production production) {
        requestInsertOptionList.forEach(option -> {
            // 상품옵션목록의 JPA 연관관계를 위해 옵션에 상품객체 셋팅
            option.setProduction(production);
            // 조회한 상품옵션목록에 신규옵션 추가
            production.getOptions().add(option);
        });
    }

    // 상품명 조회 키워드 자릿수 제한해 반환
    private String getLimitedKeyword(String keyword) {
        return keyword.length() > PRODUCTION_NAME_LENGTH ?
                keyword.substring(0, PRODUCTION_NAME_LENGTH) : keyword;
    }

    // keyword를 포함하는 상품정보 페이지 조회
    private Page<Production> getSortedProductions(RequestSearchProductionDto requestDto,
                                                  String keyword) {
        Page<Production> productionPage;
        ProductionOrderByStdType orderByStd = requestDto.getOrderByStd();
        Pageable pageable = requestDto.getPageable();

        if (orderByStd == ORDER_BY_LOWEST_PRICE) {
            productionPage = productionRepository
                    .findByNameOrderByPrice(keyword, pageable);

        } else if (orderByStd == ORDER_BY_HIGHEST_PRICE) {
            productionPage = productionRepository
                    .findByNameOrderByPriceDesc(keyword, pageable);

        } else if (orderByStd == ORDER_BY_REGISTRATION) {
            productionPage = productionRepository
                    .findByNameLikeAndSaleStatusOrderByCreateDt(
                            keyword, ON_SALE, pageable);

        } else { // 기본 정확도순 정렬
            productionPage = productionRepository
                    .findByNameOrderByAccuracyDesc(keyword, pageable);
        }

        return productionPage;
    }

}
