package com.myecommerce.MyECommerce.service.production;

import com.myecommerce.MyECommerce.dto.production.*;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.production.Production;
import com.myecommerce.MyECommerce.entity.production.ProductionOption;
import com.myecommerce.MyECommerce.exception.ProductionException;
import com.myecommerce.MyECommerce.mapper.*;
import com.myecommerce.MyECommerce.repository.production.ProductionOptionRepository;
import com.myecommerce.MyECommerce.repository.production.ProductionRepository;
import com.myecommerce.MyECommerce.type.ProductionCategoryType;
import com.myecommerce.MyECommerce.type.ProductionOrderByStdType;
import com.myecommerce.MyECommerce.type.ProductionSaleStatusType;
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

    private final ServiceProductionMapper serviceProductionMapper;

    private final SearchDetailProductionMapper searchDetailProductionMapper;

    private final ProductionRepository productionRepository;
    private final ProductionOptionRepository productionOptionRepository;

    /** 상품등록 **/
    @Transactional
    public ResponseProductionDto registerProduction(RequestProductionDto requestProductionDto,
                                                    Member member) {
        // requestDto -> serviceDto 변환
        ServiceProductionDto serviceProductionDto =
                serviceProductionMapper.toServiceDto(requestProductionDto);

        // 판매자별 상품코드 중복체크 정책
        enforceProductionCodeUniquenessPolicy(
                member.getId(), serviceProductionDto.getCode());
        // 상품옵션목록 중복체크 정책
        enforceProductionOptionCodeUniquenessPolicy(serviceProductionDto.getOptions(), serviceProductionDto.getCode());

        // serviceDto -> entity 변환
        Production production = serviceProductionMapper.toEntity(serviceProductionDto);

        // 상품, 옵션 등록
        Production savedProduction = saveProduction(production, member);
        saveProductionOption(production.getOptions(), savedProduction);

        // 상품, 상품옵션목록 반환
        return serviceProductionMapper.toDto(savedProduction);
    }

    /** 상품수정 **/
    @Transactional
    public ResponseProductionDto modifyProduction(RequestModifyProductionDto requestProductionDto,
                                                  Member member) {

        // requestDto -> ServiceDto 변환
        ServiceProductionDto serviceProductionDto =
                serviceProductionMapper.toServiceDto(requestProductionDto);
        List<ServiceProductionOptionDto> serviceOptionDtoListForUpdate =
                getUpdateServiceOptionList(serviceProductionDto.getOptions());
        List<ServiceProductionOptionDto> serviceOptionDtoListForInsert =
                getInsertServiceOptionList(serviceProductionDto.getOptions());

        // 상품 조회
        Production production = getProductionEntityByIdAndSeller(
                serviceProductionDto.getId(), member.getId());

        // 사전 validation check
        validateUpdateOptionIds(production, serviceOptionDtoListForUpdate);
        // 판매상태 정책 검증
        enforceProductModifySaleStatusPolicy(production.getSaleStatus());
        // 등록할 상품옵션 중복 검증
        enforceProductionOptionCodeUniquenessPolicy(
                serviceOptionDtoListForInsert, production.getCode());

        // 수정, 신규 등록 옵션 목록 dto -> entity 변환
        List<ProductionOption> requestUpdateOptionList =
                serviceOptionDtoListForUpdate.stream()
                        .map(serviceProductionMapper::toOptionEntity)
                        .toList();
        List<ProductionOption> requestInsertOptionList =
                serviceOptionDtoListForInsert.stream()
                        .map(serviceProductionMapper::toOptionEntity)
                        .toList();

        // 상품 설명, 판매상태 변경
        updateProduction(production, serviceProductionDto);
        // 기존 상품옵션 수량 변경
        updateOptions(requestUpdateOptionList, production);
        // 신규 상품옵션 추가
        insertOptions(requestInsertOptionList, production);

        // 상품, 상품옵션목록 반환
        return serviceProductionMapper.toDto(production);
    }

    /** 상품상세조회 **/
    public ResponseSearchDetailProductionDto searchDetailProduction(Long id) {
        return searchDetailProductionMapper.toDto(
                productionRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductionException(PRODUCT_NOT_EXIST)));
    }
  
    /** 상품목록조회 **/
    public Page<ResponseProductionDto> searchProductionList(
            RequestSearchProductionDto requestDto) {
        // 정렬순서에 따른 상품목록조회
        Page<Production> productionPage =
                getSortedProductions(requestDto, requestDto.getKeyword());

        // entity -> dto로 변환
        return  productionPage.map(serviceProductionMapper::toDto);
    }

    // 상품 insert
    private Production saveProduction(Production production, Member member) {
        Production productionForSave = Production.builder()
                .code(production.getCode())
                .name(production.getName())
                .description(production.getDescription())
                .category(production.getCategory())
                .seller(member.getId())
                .saleStatus(ON_SALE)
                .options(null)
                .build();

        // 상품 등록
        return productionRepository.save(productionForSave);
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

    // 상품코드 유일성 검증 정책 (판매자별 상품코드 중복체크)
    private void enforceProductionCodeUniquenessPolicy(Long sellerId, String code) {
        productionRepository.findBySellerAndCode(sellerId, code)
                .ifPresent(existingProduction -> {
                    throw new ProductionException(PRODUCT_CODE_ALREADY_REGISTERED);
                });
    }

    // 상품옵션코드 유일성 검증 정책 (상품옵션 중복체크)
    private void enforceProductionOptionCodeUniquenessPolicy(
            List<ServiceProductionOptionDto> options, String productionCode) {
        // 중복코드 제거된 옵션코드목록 set
        Set<String> optionCodeSet = options.stream()
                .map(ServiceProductionOptionDto::getOptionCode)
                .collect(Collectors.toSet());

        // 1. 입력받은 옵션코드목록 중 중복데이터 체크
        if (optionCodeSet.size() != options.size()) {
            throw new ProductionException(PRODUCT_OPTION_CODE_DUPLICATED);
        }

        // 2. 입력받은 옵션코드목록과 등록된 옵션코드목록 중복 체크
        if (!productionOptionRepository.findByProductionCodeAndOptionCodeIn(
                        productionCode, optionCodeSet.stream().toList())
                .isEmpty()) {
            throw new ProductionException(PRODUCT_OPTION_CODE_ALREADY_REGISTERED);
        }
    }

    // 수정할 옵션목록 반환
    private List<ServiceProductionOptionDto> getUpdateServiceOptionList(
            List<ServiceProductionOptionDto> optionDto) {
        return optionDto.stream()
                .filter(option ->
                        option.getId() != null && option.getId() > 0)
                .toList();
    }

    // 신규 저장할 옵션목록 반환
    private List<ServiceProductionOptionDto> getInsertServiceOptionList(
            List<ServiceProductionOptionDto> optionDto) {
        return optionDto.stream()
                .filter(option ->
                        option.getId() == null || option.getId() <= 0)
                .toList();
    }

    // 상품수정 사전 validation check
    private void validateUpdateOptionIds(
            Production production, List<ServiceProductionOptionDto> serviceOptionDto) {
        // 1. 기존옵션ID목록 Set으로 변환
        Set<Long> productionOptionIds = production.getOptions().stream()
                .map(ProductionOption::getId)
                .collect(Collectors.toSet());

        // 2. 요청옵션ID목록이 기존옵션ID목록에 모두 포함되는지 확인
        boolean isExistAllOptionIds = serviceOptionDto.stream()
                .allMatch(option -> productionOptionIds.contains(option.getId()));

        // 3. 포함되지않는 경우에 대한 예외처리
        if (!isExistAllOptionIds) {
            throw new ProductionException(PRODUCT_OPTION_NOT_EXIST);
        }
    }

    // 상품 수정 시 판매상태 정책
    private void enforceProductModifySaleStatusPolicy(ProductionSaleStatusType saleStatus) {
        if (saleStatus == DELETION) {
            throw new ProductionException(PRODUCT_ALREADY_DELETED);
        }
    }


    // 상품ID, 셀러ID와 일치하는 상품 단건 조회
    private Production getProductionEntityByIdAndSeller(Long productionId, Long sellerId) {
        return productionRepository.findByIdAndSeller(productionId, sellerId)
                .orElseThrow(() -> new ProductionException(PRODUCT_EDIT_FORBIDDEN));
    }

    // 상품 Entity 데이터 변경
    private void updateProduction(Production production,
                                  ServiceProductionDto serviceProductionDto) {
        production.setDescription(serviceProductionDto.getDescription());
        production.setSaleStatus(serviceProductionDto.getSaleStatus());
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
                throw new ProductionException(PRODUCT_OPTION_NOT_EXIST);
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

    // keyword를 포함하는 상품정보 페이지 조회
    private Page<Production> getSortedProductions(RequestSearchProductionDto requestDto,
                                                  String keyword) {
        Page<Production> productionPage;
        ProductionOrderByStdType orderByStd = requestDto.getOrderByStd();
        ProductionCategoryType category = requestDto.getCategory();
        Pageable pageable = requestDto.getPageable();

        if (orderByStd == ORDER_BY_LOWEST_PRICE) {
            productionPage = productionRepository
                    .findByNameOrderByPrice(keyword, category, pageable);

        } else if (orderByStd == ORDER_BY_HIGHEST_PRICE) {
            productionPage = productionRepository
                    .findByNameOrderByPriceDesc(keyword, category, pageable);

        } else if (orderByStd == ORDER_BY_REGISTRATION) {
            productionPage = productionRepository
                    .findByNameLikeAndSaleStatusAndCategoryOrderByCreateDt(
                            keyword, ON_SALE, category, pageable);

        } else { // 기본 정확도순 정렬
            productionPage = productionRepository
                    .findByNameOrderByCalculatedAccuracyDesc(keyword, category, pageable);
        }

        return productionPage;
    }

}
