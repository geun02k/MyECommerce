package com.myecommerce.MyECommerce.service.production;

import com.myecommerce.MyECommerce.dto.production.*;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.exception.ProductionException;
import com.myecommerce.MyECommerce.mapper.*;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
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
import static com.myecommerce.MyECommerce.type.ProductionSaleStatusType.ON_SALE;

@Service
@RequiredArgsConstructor
public class ProductionService {

    private final ProductionPolicy productionPolicy;

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    private final ServiceProductMapper serviceProductMapper;

    /** 상품등록 **/
    @Transactional
    public ResponseProductionDto registerProduction(
            RequestProductionDto requestProductionDto, Member member) {

        // requestDto -> serviceDto 변환
        ServiceProductionDto serviceProductionDto =
                serviceProductMapper.toServiceDto(requestProductionDto);

        // 정책 검증
        productionPolicy.validateRegister(serviceProductionDto, member);

        // serviceDto -> entity 변환
        Product production = serviceProductMapper.toEntity(serviceProductionDto);

        // 상품, 옵션 등록
        Product savedProduction = saveProduction(production, member);
        saveProductionOptions(savedProduction, production.getOptions());

        // 상품, 상품옵션목록 반환
        return serviceProductMapper.toDto(savedProduction);
    }

    /** 상품수정 **/
    @Transactional
    public ResponseProductionDto modifyProduction(
            RequestModifyProductionDto requestProductionDto, Member member) {

        // requestDto -> ServiceDto 변환
        ServiceProductionDto serviceProductionDto =
                serviceProductMapper.toServiceDto(requestProductionDto);
        List<ServiceProductionOptionDto> serviceOptionDtoListForUpdate =
                filterUpdateOptions(serviceProductionDto.getOptions());
        List<ServiceProductionOptionDto> serviceOptionDtoListForInsert =
                filterInsertOptions(serviceProductionDto.getOptions());

        // 상품 조회
        Product targetProduction = getProductionEntityByIdAndSeller(
                serviceProductionDto.getId(), member.getId());

        // 사전 validation check
        validateOptionIdsForUpdate(
                targetProduction, serviceOptionDtoListForUpdate);
        // 정책 검증
        productionPolicy.validateModify(
                targetProduction, serviceOptionDtoListForInsert);

        // 수정, 신규 등록 옵션 목록 dto -> entity 변환
        List<ProductOption> updateTargetOptions =
                serviceOptionDtoListForUpdate.stream()
                        .map(serviceProductMapper::toOptionEntity)
                        .toList();
        List<ProductOption> insertTargetOptions =
                serviceOptionDtoListForInsert.stream()
                        .map(serviceProductMapper::toOptionEntity)
                        .toList();

        // 상품 설명, 판매상태 변경
        updateProduction(targetProduction, serviceProductionDto);
        // 기존 상품옵션 수량 변경
        updateOptions(targetProduction, updateTargetOptions);
        // 신규 상품옵션 추가
        insertOptions(targetProduction, insertTargetOptions);

        // 상품, 상품옵션목록 반환
        return serviceProductMapper.toDto(targetProduction);
    }

    /** 상품상세조회 **/
    public ResponseSearchDetailProductionDto searchDetailProduction(Long id) {
        return serviceProductMapper.toSearchDetailDto(
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductionException(PRODUCT_NOT_EXIST)));
    }
  
    /** 상품목록조회 **/
    public Page<ResponseProductionDto> searchProductionList(
            RequestSearchProductionDto requestDto) {
        // 정렬순서에 따른 상품목록조회 & entity -> dto로 변환
        return getSortedProductions(requestDto)
                        .map(serviceProductMapper::toDto);
    }

    // 상품 insert
    private Product saveProduction(Product production, Member member) {
        Product productionForSave = Product.builder()
                .code(production.getCode())
                .name(production.getName())
                .description(production.getDescription())
                .category(production.getCategory())
                .seller(member.getId())
                .saleStatus(ON_SALE)
                .options(null)
                .build();

        // 상품 등록
        return productRepository.save(productionForSave);
    }

    // 상품옵션 insert
    private void saveProductionOptions(Product production,
                                      List<ProductOption> optionList) {
        optionList.forEach(option -> {
            // 상품옵션목록의 JPA 연관관계를 위해 옵션에 상품객체 셋팅
            option.setProduct(production);
            // 상품옵션목록 등록
            productOptionRepository.save(option);
        });
    }

    // 수정할 옵션목록 반환
    private List<ServiceProductionOptionDto> filterUpdateOptions(
            List<ServiceProductionOptionDto> optionDtoList) {
        return optionDtoList.stream()
                .filter(option ->
                        option.getId() != null && option.getId() > 0)
                .toList();
    }

    // 신규 저장할 옵션목록 반환
    private List<ServiceProductionOptionDto> filterInsertOptions(
            List<ServiceProductionOptionDto> optionDtoList) {
        return optionDtoList.stream()
                .filter(option ->
                        option.getId() == null || option.getId() <= 0)
                .toList();
    }

    // 상품수정 사전 validation check
    private void validateOptionIdsForUpdate(
            Product production, List<ServiceProductionOptionDto> serviceOptionDtoList) {
        // 1. 기존옵션ID목록 Set으로 변환
        Set<Long> optionIds = production.getOptions().stream()
                .map(ProductOption::getId)
                .collect(Collectors.toSet());

        // 2. 요청옵션ID목록이 기존옵션ID목록에 모두 포함되는지 확인
        boolean isAllOriginOptionIds = serviceOptionDtoList.stream()
                .allMatch(option ->
                        optionIds.contains(option.getId()));

        // 3. 포함되지않는 경우에 대한 예외처리
        if (!isAllOriginOptionIds) {
            throw new ProductionException(PRODUCT_OPTION_NOT_EXIST);
        }
    }

    // 상품ID, 셀러ID와 일치하는 상품 단건 조회
    private Product getProductionEntityByIdAndSeller(Long productionId, Long sellerId) {
        return productRepository.findByIdAndSeller(productionId, sellerId)
                .orElseThrow(() -> new ProductionException(PRODUCT_EDIT_FORBIDDEN));
    }

    // 상품 Entity 데이터 변경
    private void updateProduction(Product production,
                                  ServiceProductionDto serviceProductionDto) {
        production.setDescription(serviceProductionDto.getDescription());
        production.setSaleStatus(serviceProductionDto.getSaleStatus());
    }

    // 상품 Entity의 필드인 상품옵션 Entity의 수량 변경
    private void updateOptions(Product production,
                               List<ProductOption> optionList) {
        // 기존상품옵션 MAP으로 매핑
        Map<Long, ProductOption> originOptionMap = production.getOptions().stream()
                .collect(Collectors.toMap(
                        ProductOption::getId, option -> option));

        // 기존 데이터와 id가 일치하는 입력데이터 찾아 값 입력
        for (ProductOption option : optionList) {
            ProductOption originOption = originOptionMap.get(option.getId());

            if (originOption != null) {
                originOption.setQuantity(option.getQuantity());
            } else {
                throw new ProductionException(PRODUCT_OPTION_NOT_EXIST);
            }
        }
    }

    // 상품 Entity의 필드인 상품옵션 Entity에 신규옵션 추가
    private void insertOptions(Product production,
                               List<ProductOption> optionList) {
        optionList.forEach(option -> {
            // 상품옵션목록의 JPA 연관관계를 위해 옵션에 상품객체 셋팅
            option.setProduct(production);
            // 조회한 상품옵션목록에 신규옵션 추가
            production.getOptions().add(option);
        });
    }

    // keyword를 포함하는 상품정보 페이지 조회
    private Page<Product> getSortedProductions(RequestSearchProductionDto requestDto) {
        Page<Product> productionPage;
        String keyword = requestDto.getKeyword();
        ProductionOrderByStdType orderByStd = requestDto.getOrderByStd();
        ProductionCategoryType category = requestDto.getCategory();
        Pageable pageable = requestDto.getPageable();

        if (orderByStd == ORDER_BY_LOWEST_PRICE) {
            productionPage = productRepository
                    .findByNameOrderByPrice(keyword, category, pageable);

        } else if (orderByStd == ORDER_BY_HIGHEST_PRICE) {
            productionPage = productRepository
                    .findByNameOrderByPriceDesc(keyword, category, pageable);

        } else if (orderByStd == ORDER_BY_REGISTRATION) {
            productionPage = productRepository
                    .findByNameLikeAndSaleStatusAndCategoryOrderByCreateDt(
                            keyword, ON_SALE, category, pageable);

        } else { // 기본 정확도순 정렬
            productionPage = productRepository
                    .findByNameOrderByCalculatedAccuracyDesc(keyword, category, pageable);
        }

        return productionPage;
    }

}
