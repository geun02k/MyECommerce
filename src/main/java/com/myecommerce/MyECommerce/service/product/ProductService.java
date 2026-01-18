package com.myecommerce.MyECommerce.service.product;

import com.myecommerce.MyECommerce.dto.product.*;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.exception.ProductException;
import com.myecommerce.MyECommerce.mapper.*;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.type.ProductCategoryType;
import com.myecommerce.MyECommerce.type.ProductOrderByStdType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.myecommerce.MyECommerce.exception.errorcode.ProductErrorCode.*;
import static com.myecommerce.MyECommerce.type.ProductOrderByStdType.*;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductPolicy productPolicy;

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;

    private final ServiceProductMapper serviceProductMapper;

    /** 상품등록 **/
    @Transactional
    public ResponseProductionDto registerProduct(
            RequestProductionDto requestProductDto, Member member) {

        // requestDto -> serviceDto 변환
        ServiceProductDto serviceProductDto =
                serviceProductMapper.toServiceDto(requestProductDto);

        // 정책 검증
        productPolicy.validateRegister(serviceProductDto, member);

        // serviceDto -> entity 변환
        Product product = serviceProductMapper.toEntity(serviceProductDto);

        // 상품, 옵션 등록
        Product savedProduct = saveProduct(product, member);
        saveProductOptions(savedProduct, product.getOptions());

        // 상품, 상품옵션목록 반환
        return serviceProductMapper.toDto(savedProduct);
    }

    /** 상품수정 **/
    @Transactional
    public ResponseProductionDto modifyProduct(
            RequestModifyProductionDto requestProductDto, Member member) {

        // requestDto -> ServiceDto 변환
        ServiceProductDto serviceProductDto =
                serviceProductMapper.toServiceDto(requestProductDto);
        List<ServiceProductionOptionDto> serviceOptionDtoListForUpdate =
                filterUpdateOptions(serviceProductDto.getOptions());
        List<ServiceProductionOptionDto> serviceOptionDtoListForInsert =
                filterInsertOptions(serviceProductDto.getOptions());

        // 상품 조회
        Product targetProduct = getProductEntityByIdAndSeller(
                serviceProductDto.getId(), member.getId());

        // 사전 validation check
        validateOptionIdsForUpdate(
                targetProduct, serviceOptionDtoListForUpdate);
        // 정책 검증
        productPolicy.validateModify(
                targetProduct, serviceOptionDtoListForInsert);

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
        updateProduct(targetProduct, serviceProductDto);
        // 기존 상품옵션 수량 변경
        updateOptions(targetProduct, updateTargetOptions);
        // 신규 상품옵션 추가
        insertOptions(targetProduct, insertTargetOptions);

        // 상품, 상품옵션목록 반환
        return serviceProductMapper.toDto(targetProduct);
    }

    /** 상품상세조회 **/
    public ResponseSearchDetailProductionDto searchDetailProduct(Long id) {
        return serviceProductMapper.toSearchDetailDto(
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ProductException(PRODUCT_NOT_EXIST)));
    }
  
    /** 상품목록조회 **/
    public Page<ResponseProductionDto> searchProductList(
            RequestSearchProductionDto requestDto) {
        // 정렬순서에 따른 상품목록조회 & entity -> dto로 변환
        return getSortedProducts(requestDto)
                        .map(serviceProductMapper::toDto);
    }

    // 상품 insert
    private Product saveProduct(Product product, Member member) {
        Product productForSave = Product.builder()
                .code(product.getCode())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .seller(member.getId())
                .saleStatus(ON_SALE)
                .options(null)
                .build();

        // 상품 등록
        return productRepository.save(productForSave);
    }

    // 상품옵션 insert
    private void saveProductOptions(Product product,
                                    List<ProductOption> optionList) {
        optionList.forEach(option -> {
            // 상품옵션목록의 JPA 연관관계를 위해 옵션에 상품객체 셋팅
            option.setProduct(product);
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
            Product product, List<ServiceProductionOptionDto> serviceOptionDtoList) {
        // 1. 기존옵션ID목록 Set으로 변환
        Set<Long> optionIds = product.getOptions().stream()
                .map(ProductOption::getId)
                .collect(Collectors.toSet());

        // 2. 요청옵션ID목록이 기존옵션ID목록에 모두 포함되는지 확인
        boolean isAllOriginOptionIds = serviceOptionDtoList.stream()
                .allMatch(option ->
                        optionIds.contains(option.getId()));

        // 3. 포함되지않는 경우에 대한 예외처리
        if (!isAllOriginOptionIds) {
            throw new ProductException(PRODUCT_OPTION_NOT_EXIST);
        }
    }

    // 상품ID, 셀러ID와 일치하는 상품 단건 조회
    private Product getProductEntityByIdAndSeller(Long productId, Long sellerId) {
        return productRepository.findByIdAndSeller(productId, sellerId)
                .orElseThrow(() -> new ProductException(PRODUCT_EDIT_FORBIDDEN));
    }

    // 상품 Entity 데이터 변경
    private void updateProduct(Product product,
                               ServiceProductDto serviceProductDto) {
        product.setDescription(serviceProductDto.getDescription());
        product.setSaleStatus(serviceProductDto.getSaleStatus());
    }

    // 상품 Entity의 필드인 상품옵션 Entity의 수량 변경
    private void updateOptions(Product product,
                               List<ProductOption> optionList) {
        // 기존상품옵션 MAP으로 매핑
        Map<Long, ProductOption> originOptionMap = product.getOptions().stream()
                .collect(Collectors.toMap(
                        ProductOption::getId, option -> option));

        // 기존 데이터와 id가 일치하는 입력데이터 찾아 값 입력
        for (ProductOption option : optionList) {
            ProductOption originOption = originOptionMap.get(option.getId());

            if (originOption != null) {
                originOption.setQuantity(option.getQuantity());
            } else {
                throw new ProductException(PRODUCT_OPTION_NOT_EXIST);
            }
        }
    }

    // 상품 Entity의 필드인 상품옵션 Entity에 신규옵션 추가
    private void insertOptions(Product product,
                               List<ProductOption> optionList) {
        optionList.forEach(option -> {
            // 상품옵션목록의 JPA 연관관계를 위해 옵션에 상품객체 셋팅
            option.setProduct(product);
            // 조회한 상품옵션목록에 신규옵션 추가
            product.getOptions().add(option);
        });
    }

    // keyword를 포함하는 상품정보 페이지 조회
    private Page<Product> getSortedProducts(RequestSearchProductionDto requestDto) {
        Page<Product> productPage;
        String keyword = requestDto.getKeyword();
        ProductOrderByStdType orderByStd = requestDto.getOrderByStd();
        ProductCategoryType category = requestDto.getCategory();
        Pageable pageable = requestDto.getPageable();

        if (orderByStd == ORDER_BY_LOWEST_PRICE) {
            productPage = productRepository
                    .findByNameOrderByPrice(keyword, category, pageable);

        } else if (orderByStd == ORDER_BY_HIGHEST_PRICE) {
            productPage = productRepository
                    .findByNameOrderByPriceDesc(keyword, category, pageable);

        } else if (orderByStd == ORDER_BY_REGISTRATION) {
            productPage = productRepository
                    .findByNameLikeAndSaleStatusAndCategoryOrderByCreateDt(
                            keyword, ON_SALE, category, pageable);

        } else { // 기본 정확도순 정렬
            productPage = productRepository
                    .findByNameOrderByCalculatedAccuracyDesc(keyword, category, pageable);
        }

        return productPage;
    }

}
