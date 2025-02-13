package com.myecommerce.MyECommerce.service.production;

import com.myecommerce.MyECommerce.dto.production.*;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.production.Production;
import com.myecommerce.MyECommerce.entity.production.ProductionOption;
import com.myecommerce.MyECommerce.mapper.ModifyProductionOptionMapper;
import com.myecommerce.MyECommerce.mapper.ProductionMapper;
import com.myecommerce.MyECommerce.mapper.ProductionOptionMapper;
import com.myecommerce.MyECommerce.repository.production.ProductionOptionRepository;
import com.myecommerce.MyECommerce.repository.production.ProductionRepository;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductionServiceTest {

    @Mock
    private ProductionMapper productionMapper;
    @Mock
    private ProductionOptionMapper productionOptionMapper;
    @Mock
    private ModifyProductionOptionMapper modifyProductionOptionMapper;

    @Mock
    private ProductionRepository productionRepository;
    @Mock
    private ProductionOptionRepository productionOptionRepository;

    @InjectMocks
    private ProductionService productionService;

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

        // 저장할 상품 Entity
        Production productionEntity  = Production.builder()
                .seller(member.getId())
                .code(requestProductionDto.getCode())
                .name(requestProductionDto.getName())
                .category(requestProductionDto.getCategory())
                .description(requestProductionDto.getDescription())
                .saleStatus(saleStatus)
                .build();
        // 저장할 상품옵션 Entity
        ProductionOption optionEntity = ProductionOption.builder()
                .optionCode(optionCode)
                .optionName(optionName)
                .price(price)
                .quantity(quantity)
                .build();

        // 저장된 상품 Entity
        Production expectedProductionEntity = Production.builder()
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
        ProductionOption expectedOptionEntity = ProductionOption.builder()
                .id(1L)
                .optionCode(optionEntity.getOptionCode())
                .optionName(optionEntity.getOptionName())
                .price(optionEntity.getPrice())
                .quantity(optionEntity.getQuantity())
                .production(expectedProductionEntity)
                .build();
        // 저장된 상품 DTO
        ResponseProductionDto expectedProductionDto = ResponseProductionDto.builder()
                .id(1L)
                .seller(member.getId())
                .code(productionCode)
                .name(productionName)
                .category(category)
                .description(description)
                .saleStatus(ON_SALE)
                .build();

        // stub(가설) : productionMapper.toEntity() 실행 시 requestProductionDto에 대한 Entity 반환 예상.
        given(productionMapper.toEntity(requestProductionDto))
                .willReturn(productionEntity);
        // stub(가설) : productionMapper.toEntity() 실행 시 requestOptionDto에 대한 Entity 반환 예상.
        given(productionOptionMapper.toEntity(requestOptionDto))
                .willReturn(optionEntity);

        // stub(가설) : productionRepository.findBySellerAndCode() 실행 시 빈 optional 객체 반환 예상.
        given(productionRepository.findBySellerAndCode(any(Long.class), any(String.class)))
                .willReturn(Optional.empty());
        // stub(가설) : productionOptionRepository.findByProductionIdAndOptionCodeIn() 실행 시 null 반환 예상.
        given(productionOptionRepository.findByProductionCodeAndOptionCodeIn(
                any(String.class), any(List.class)))
                .willReturn(new ArrayList());

        // stub(가설) : productionRepository.save() 실행 시 expectedProductionEntity 반환 예상.
        given(productionRepository.save(productionEntity))
                .willReturn(expectedProductionEntity);
        // stub(가설) : productionOptionRepository.save() 실행 시
        // expectedProductionEntity 정보를 포함한 ProductionOption Entity 반환 예상.
        given(productionOptionRepository.save(optionEntity))
                .willReturn(expectedOptionEntity);

        // stub(가설) : productionMapper.toDto() 실행 시 productionEntity에 대한 DTO 반환 예상.
        given(productionMapper.toDto(expectedProductionEntity))
                .willReturn(expectedProductionDto);

        // when
        ResponseProductionDto response =
                productionService.registerProduction(requestProductionDto, member);

        // then
        verify(productionRepository, times(1))
                .save(productionEntity);
        verify(productionOptionRepository, times(1))
                .save(optionEntity);
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
        assertEquals(expectedProductionEntity, expectedOptionEntity.getProduction());
        assertEquals(expectedOptionEntity.getId(), expectedOptionEntity.getProduction().getId());
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
        ProductionOption updateReqOptEntity = ProductionOption.builder()
                .id(requestUpdateOptionDto.getId())
                .optionCode(requestUpdateOptionDto.getOptionCode())
                .optionName(requestUpdateOptionDto.getOptionName())
                .price(requestUpdateOptionDto.getPrice())
                .quantity(requestUpdateOptionDto.getQuantity())
                .build();
        // 신규 등록을 위한 요청 상품옵션 Entity
        ProductionOption insertReqOptEntity = ProductionOption.builder()
                .optionCode(requestInsertOptionDto.getOptionCode())
                .optionName(requestInsertOptionDto.getOptionName())
                .price(requestInsertOptionDto.getPrice())
                .quantity(requestInsertOptionDto.getQuantity())
                .build();

        // 조회된 업데이트를 위한 상품옵션 Entity (dirty checking entity)
        ProductionOption originOptionEntity = ProductionOption.builder()
                .id(existingOptionId)
                .optionCode(existingOptionCode)
                .optionName(existingOptionName)
                .price(existingPrice)
                .quantity(existingQuantity)
                .build();
        // 조회된 업데이트를 위한 상품 Entity (dirty checking entity)
        Production originProductionEntity = Production.builder()
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

        // stub(가설) : productionRepository.findByIdAndSeller() 실행 시 productionEntity 반환 예상.
        given(productionRepository.findByIdAndSeller(
                eq(requestProductionDto.getId()), eq(member.getId())))
                .willReturn(Optional.of(originProductionEntity));

        // stub(가설) : modifyProductionOptionMapper.toEntity() 실행 시 updateReqOptDto에 대한 Entity 반환 예상.
        given(modifyProductionOptionMapper.toEntity(requestUpdateOptionDto))
                .willReturn(updateReqOptEntity);

        // stub(가설) : modifyProductionOptionMapper.toEntity() 실행 시 insertReqOptDto에 대한 Entity 반환 예상.
        given(modifyProductionOptionMapper.toEntity(requestInsertOptionDto))
                .willReturn(insertReqOptEntity);

        // stub(가설) : productionOptionRepository.findByProductionCodeAndOptionCodeIn() 실행 시
        // 상품코드, 요청옵션코드목록에 대해 null 반환 예상.
        given(productionOptionRepository.findByProductionCodeAndOptionCodeIn(
                productionCode, Collections.singletonList(insertReqOptEntity.getOptionCode())))
                .willReturn(new ArrayList());

        // ArgumentCaptor 생성
        ArgumentCaptor<Production> productionCaptor = ArgumentCaptor.forClass(Production.class);

        // stub(가설) : productionMapper.toDto() 실행 시 productionCaptor로 인자를 캡처하도록 설정.
        given(productionMapper.toDto(productionCaptor.capture()))
                .willReturn(expectedResultProductionDto);

        // when
        ResponseProductionDto responseProductionDto =
                productionService.modifyProduction(requestProductionDto, member);

        // then
        // 0. toDto에 전달된 인자 캡처 후 검증
        Production capturedProduction = productionCaptor.getValue();
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
        ProductionOption responseUpdatedOption = capturedProduction.getOptions().get(0);
        assertEquals(requestUpdateOption.getId(), responseUpdatedOption.getId());
        assertEquals(requestUpdateOption.getQuantity(), responseUpdatedOption.getQuantity());
        // 3. 상품옵션 신규등록 검증
        RequestModifyProductionOptionDto requestInsertOption = requestProductionDto.getOptions().get(1);
        ProductionOption responseInsertedOption = capturedProduction.getOptions().get(1);
        assertNull(requestInsertOption.getId());
        assertNotNull(responseInsertedOption);
    }

}