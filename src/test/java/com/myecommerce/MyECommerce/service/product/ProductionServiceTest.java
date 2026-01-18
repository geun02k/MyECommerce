package com.myecommerce.MyECommerce.service.product;

import com.myecommerce.MyECommerce.dto.production.*;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.mapper.*;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import com.myecommerce.MyECommerce.type.ProductionCategoryType;
import com.myecommerce.MyECommerce.type.ProductionSaleStatusType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.myecommerce.MyECommerce.type.ProductionCategoryType.WOMEN_CLOTHING;
import static com.myecommerce.MyECommerce.type.ProductionSaleStatusType.DISCONTINUED;
import static com.myecommerce.MyECommerce.type.ProductionSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductionServiceTest {

    @Mock
    private ProductPolicy productionPolicy;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductOptionRepository productOptionRepository;
    @Mock
    private ServiceProductMapper serviceProductMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품등록 성공")
    void successSaveProduction() {
        // given
        String productionCode = "RM-JK-D11S51";
        String productionName = "제 품 명";
        ProductionCategoryType category = WOMEN_CLOTHING;
        String description = "설명";
        ProductionSaleStatusType saleStatus = ON_SALE;

        String optionCode = "S-BL";
        String optionName = "스몰사이즈 블루컬러";
        BigDecimal price = BigDecimal.valueOf(67900);
        int quantity = 30;
        int seq = 1;

        Long memberId = 1L;

        // 요청 상품옵션 DTO 목록
        RequestProductionOptionDto requestOptionDto = RequestProductionOptionDto.builder()
                .optionCode(optionCode)
                .optionName(optionName)
                .price(price)
                .quantity(quantity)
                .build();
        // 요청 상품 DTO
        RequestProductionDto requestProductionDto =
                RequestProductionDto.builder()
                        .code(productionCode)
                        .name(productionName)
                        .category(category)
                        .description(description)
                        .options(Collections.singletonList(requestOptionDto))
                        .build();
        // 요청 회원 DTO
        Member member = Member.builder()
                .id(memberId)
                .build();

        // Service DTO
        ServiceProductionOptionDto serviceOptionDto =
                ServiceProductionOptionDto.builder()
                        .optionCode(optionCode)
                        .optionName(optionName)
                        .price(price)
                        .quantity(quantity)
                        .build();
        ServiceProductionDto serviceProductionDto =
                ServiceProductionDto.builder()
                        .code(productionCode)
                        .name(productionName)
                        .category(category)
                        .options(Collections.singletonList(serviceOptionDto))
                        .build();
        ProductOption expectedEntityServiceOptionDto =
                ProductOption.builder()
                        .optionCode(optionCode)
                        .optionName(optionName)
                        .price(price)
                        .quantity(quantity)
                        .build();
        Product expectedEntityOfServiceProductionDto =
                Product.builder()
                        .code(productionCode)
                        .name(productionName)
                        .category(category)
                        .options(Collections.singletonList(
                                expectedEntityServiceOptionDto))
                        .build();

        // 저장할 상품옵션 Entity
        ProductOption optionEntity = ProductOption.builder()
                .optionCode(optionCode)
                .optionName(optionName)
                .price(price)
                .quantity(quantity)
                .build();
        // 저장할 상품 Entity
        Product productionEntity  = Product.builder()
                .seller(member.getId())
                .code(requestProductionDto.getCode())
                .name(requestProductionDto.getName())
                .category(requestProductionDto.getCategory())
                .description(requestProductionDto.getDescription())
                .saleStatus(saleStatus)
                .build();

        // 저장된 상품 Entity
        Product expectedProductionEntity = Product.builder()
                .id(1L)
                .seller(productionEntity.getSeller())
                .code(productionEntity.getCode())
                .name(productionEntity.getName())
                .category(productionEntity.getCategory())
                .description(productionEntity.getDescription())
                .saleStatus(productionEntity.getSaleStatus())
                .options(null)
                .build();
        // 저장된 상품옵션 Entity
        ProductOption expectedOptionEntity = ProductOption.builder()
                .id(1L)
                .optionCode(optionEntity.getOptionCode())
                .optionName(optionEntity.getOptionName())
                .price(optionEntity.getPrice())
                .quantity(optionEntity.getQuantity())
                .product(expectedProductionEntity)
                .build();
        // response 상품 DTO
        ResponseProductionDto expectedProductionDto = ResponseProductionDto.builder()
                .id(1L)
                .seller(member.getId())
                .code(productionCode)
                .name(productionName)
                .category(category)
                .description(description)
                .saleStatus(ON_SALE)
                .build();

        given(serviceProductMapper.toServiceDto(requestProductionDto))
                .willReturn(serviceProductionDto);
        given(serviceProductMapper.toEntity(serviceProductionDto))
                .willReturn(expectedEntityOfServiceProductionDto);

        ArgumentCaptor<Product> productionCaptor =
                ArgumentCaptor.forClass(Product.class);
        ArgumentCaptor<ProductOption> optionCaptor =
                ArgumentCaptor.forClass(ProductOption.class);
        // stub(가설) : productionRepository.save() 실행 시 expectedProductionEntity 반환 예상.
        given(productRepository.save(productionCaptor.capture()))
                .willReturn(expectedProductionEntity);
        // stub(가설) : productionOptionRepository.save() 실행 시
        // expectedProductionEntity 정보를 포함한 ProductOption Entity 반환 예상.
        given(productOptionRepository.save(optionCaptor.capture()))
                .willReturn(expectedOptionEntity);

        // stub(가설) : productionMapper.toDto() 실행 시 productionEntity에 대한 DTO 반환 예상.
        given(serviceProductMapper.toDto(expectedProductionEntity))
                .willReturn(expectedProductionDto);

        // when
        ResponseProductionDto response =
                productService.registerProduction(requestProductionDto, member);

        // then
        verify(productionPolicy, times(1))
                .validateRegister(serviceProductionDto, member);
        verify(productRepository, times(1))
                .save(productionCaptor.capture());
        verify(productOptionRepository, times(1))
                .save(optionCaptor.capture());
        // 상품 전달인자 검증
        Product capturedProduction = productionCaptor.getValue();
        assertNull(capturedProduction.getId());
        assertEquals(productionCode, capturedProduction.getCode());
        assertEquals(productionName, capturedProduction.getName());
        assertEquals(WOMEN_CLOTHING, capturedProduction.getCategory());
        assertEquals(ON_SALE, capturedProduction.getSaleStatus());
        assertEquals(member.getId(), capturedProduction.getSeller());
        assertNull(capturedProduction.getOptions());
        // 상품옵션 전달인자 검증
        ProductOption capturedOption = optionCaptor.getAllValues().get(0);
        assertNull(capturedOption.getId());
        assertEquals(optionCode, capturedOption.getOptionCode());
        assertEquals(quantity, capturedOption.getQuantity());
        assertEquals(price, capturedOption.getPrice());

        // 상품 검증
        assertEquals(requestProductionDto.getCode(), response.getCode());
        assertEquals(requestProductionDto.getName(), response.getName());
        assertEquals(requestProductionDto.getCategory(), response.getCategory());
        assertEquals(requestProductionDto.getDescription(), response.getDescription());
        assertEquals(member.getId(), response.getSeller());
        assertEquals(productionEntity.getSaleStatus(), response.getSaleStatus());
        // 상품옵션검증
        RequestProductionOptionDto reqOptFromReqProduction = requestProductionDto.getOptions().get(0);
        assertEquals(reqOptFromReqProduction.getOptionCode(), expectedOptionEntity.getOptionCode());
        assertEquals(reqOptFromReqProduction.getOptionName(), expectedOptionEntity.getOptionName());
        assertEquals(reqOptFromReqProduction.getPrice(), expectedOptionEntity.getPrice());
        assertEquals(reqOptFromReqProduction.getQuantity(), expectedOptionEntity.getQuantity());
        assertEquals(expectedProductionEntity, expectedOptionEntity.getProduct());
        assertEquals(expectedOptionEntity.getId(), expectedOptionEntity.getProduct().getId());
    }

    @Test
    @DisplayName("상품수정 성공")
    @Transactional
    void successModifyProduction() {
        // given
        Long productionId = 1L;
        String productionCode = "RM-JK-D11S51";
        String productionName = "제 품 명";
        ProductionCategoryType category = WOMEN_CLOTHING;
        String description = "설명";
        ProductionSaleStatusType saleStatus = ON_SALE;

        String updateDescription = "수정한 설명입니다.";
        ProductionSaleStatusType updateSaleStatus = DISCONTINUED;

        Long existingOptionId = 2L;
        String existingOptionCode = "S-BL";
        String existingOptionName = "스몰사이즈 블루컬러";
        BigDecimal existingPrice = BigDecimal.valueOf(20000);
        int existingQuantity = 20;

        int updateQuantity = 100;

//        Long newOptionId = 3L;
        String optionCode = "M-BL";
        String optionName = "미디움사이즈 블루컬러";
        BigDecimal price = BigDecimal.valueOf(30000);
        int quantity = 30;

        // 요청 상품옵션 DTO 목록
        RequestModifyProductionOptionDto requestUpdateOptionDto =
                RequestModifyProductionOptionDto.builder()
                        .id(existingOptionId)
                        .quantity(updateQuantity)
                        .build();
        RequestModifyProductionOptionDto requestInsertOptionDto =
                RequestModifyProductionOptionDto.builder()
                        .optionCode(optionCode)
                        .optionName(optionName)
                        .price(price)
                        .quantity(quantity)
                        .build();
        List<RequestModifyProductionOptionDto> requestModifyOptionDtoList = new ArrayList<>();
        requestModifyOptionDtoList.add(requestUpdateOptionDto);
        requestModifyOptionDtoList.add(requestInsertOptionDto);
        // 요청 상품 DTO
        RequestModifyProductionDto requestProductionDto =
                RequestModifyProductionDto.builder()
                        .id(productionId)
                        .description(updateDescription)
                        .saleStatus(updateSaleStatus)
                        .options(requestModifyOptionDtoList)
                        .build();
        // 요청 회원 DTO
        Member member = Member.builder()
                .id(4L)
                .build();

        // 수정을 위한 요청 상품옵션 Entity
        ProductOption updateReqOptEntity = ProductOption.builder()
                .id(requestUpdateOptionDto.getId())
                .optionCode(requestUpdateOptionDto.getOptionCode())
                .optionName(requestUpdateOptionDto.getOptionName())
                .price(requestUpdateOptionDto.getPrice())
                .quantity(requestUpdateOptionDto.getQuantity())
                .build();
        // 신규 등록을 위한 요청 상품옵션 Entity
        ProductOption insertReqOptEntity = ProductOption.builder()
                .optionCode(requestInsertOptionDto.getOptionCode())
                .optionName(requestInsertOptionDto.getOptionName())
                .price(requestInsertOptionDto.getPrice())
                .quantity(requestInsertOptionDto.getQuantity())
                .build();

        // 조회된 업데이트를 위한 상품옵션 Entity (dirty checking entity)
        ProductOption originOptionEntity = ProductOption.builder()
                .id(existingOptionId)
                .optionCode(existingOptionCode)
                .optionName(existingOptionName)
                .price(existingPrice)
                .quantity(existingQuantity)
                .build();
        // 조회된 업데이트를 위한 상품 Entity (dirty checking entity)
        Product originProductionEntity = Product.builder()
                .id(productionId)
                .seller(member.getId())
                .code(productionCode)
                .name(productionName)
                .category(category)
                .description(description)
                .saleStatus(saleStatus)
                .options(Stream.of(originOptionEntity).collect(Collectors.toList()))
                .build();
        
        // 업데이트한 상품 결과 DTO
        ResponseProductionDto expectedResultProductionDto = ResponseProductionDto.builder()
                .id(originProductionEntity.getId())
                .seller(originProductionEntity.getSeller())
                .code(originProductionEntity.getCode())
                .name(originProductionEntity.getName())
                .category(originProductionEntity.getCategory())
                .description(updateDescription)
                .saleStatus(updateSaleStatus)
                .build();

        ServiceProductionOptionDto serviceUpdateOptionDto =
                ServiceProductionOptionDto.builder()
                        .id(existingOptionId)
                        .quantity(updateQuantity)
                        .build();
        ServiceProductionOptionDto serviceInsertOptionDto =
                ServiceProductionOptionDto.builder()
                        .optionCode(optionCode)
                        .optionName(optionName)
                        .price(price)
                        .quantity(quantity)
                        .build();
        List<ServiceProductionOptionDto> serviceOptionDtoList =
                List.of(serviceUpdateOptionDto, serviceInsertOptionDto);

        ServiceProductionDto serviceProductionDto =
        ServiceProductionDto.builder()
                .id(productionId)
                .description(updateDescription)
                .saleStatus(updateSaleStatus)
                .options(serviceOptionDtoList)
                .build();

        ProductOption insertUpdateOptionEntity =
                ProductOption.builder()
                        .id(existingOptionId)
                        .quantity(updateQuantity)
                        .build();
        ProductOption insertOptionDtoEntity =
                ProductOption.builder()
                        .optionCode(optionCode)
                        .optionName(optionName)
                        .price(price)
                        .quantity(quantity)
                        .build();

        given(serviceProductMapper.toServiceDto(requestProductionDto))
                .willReturn(serviceProductionDto);
        given(serviceProductMapper.toOptionEntity(serviceUpdateOptionDto))
                .willReturn(insertUpdateOptionEntity);
        given(serviceProductMapper.toOptionEntity(serviceInsertOptionDto))
                .willReturn(insertOptionDtoEntity);

        // stub(가설) : productionRepository.findByIdAndSeller() 실행 시 productionEntity 반환 예상.
        given(productRepository.findByIdAndSeller(
                eq(requestProductionDto.getId()), eq(member.getId())))
                .willReturn(Optional.of(originProductionEntity));

        // ArgumentCaptor 생성
        ArgumentCaptor<Product> productionCaptor = ArgumentCaptor.forClass(Product.class);

        // stub(가설) : productionMapper.toDto() 실행 시 productionCaptor로 인자를 캡처하도록 설정.
        given(serviceProductMapper.toDto(productionCaptor.capture()))
                .willReturn(expectedResultProductionDto);

        // when
        ResponseProductionDto responseProductionDto =
                productService.modifyProduction(requestProductionDto, member);

        // then
        verify(productionPolicy, times(1))
                .validateModify(any(), any());
        // 0. toDto에 전달된 인자 캡처 후 검증
        Product capturedProduction = productionCaptor.getValue();
        assertEquals(requestProductionDto.getId(), capturedProduction.getId());
        assertEquals(requestProductionDto.getDescription(), capturedProduction.getDescription());
        assertEquals(requestProductionDto.getSaleStatus(), capturedProduction.getSaleStatus());

        // 1. 상품 수정 검증
        assertEquals(requestProductionDto.getId(), responseProductionDto.getId());
        assertEquals(requestProductionDto.getDescription(), responseProductionDto.getDescription());
        assertEquals(requestProductionDto.getSaleStatus(), responseProductionDto.getSaleStatus());
        assertEquals(originProductionEntity.getSeller(), responseProductionDto.getSeller());
        assertEquals(originProductionEntity.getCode(), responseProductionDto.getCode());
        assertEquals(originProductionEntity.getName(), responseProductionDto.getName());
        assertEquals(originProductionEntity.getCategory(), responseProductionDto.getCategory());
        // 2. 상품옵션 수정 검증
        RequestModifyProductionOptionDto requestUpdateOption = requestProductionDto.getOptions().get(0);
        ProductOption responseUpdatedOption = capturedProduction.getOptions().get(0);
        assertEquals(requestUpdateOption.getId(), responseUpdatedOption.getId());
        assertEquals(requestUpdateOption.getQuantity(), responseUpdatedOption.getQuantity());
        // 3. 상품옵션 신규등록 검증
        RequestModifyProductionOptionDto requestInsertOption = requestProductionDto.getOptions().get(1);
        ProductOption responseInsertedOption = capturedProduction.getOptions().get(1);
        assertNull(requestInsertOption.getId());
        assertNotNull(responseInsertedOption);
    }

}