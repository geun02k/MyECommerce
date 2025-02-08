package com.myecommerce.MyECommerce.service.production;

import com.myecommerce.MyECommerce.dto.production.RequestProductionDto;
import com.myecommerce.MyECommerce.dto.production.RequestProductionOptionDto;
import com.myecommerce.MyECommerce.dto.production.ResponseProductionDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.production.Production;
import com.myecommerce.MyECommerce.entity.production.ProductionOption;
import com.myecommerce.MyECommerce.mapper.ProductionMapper;
import com.myecommerce.MyECommerce.mapper.ProductionOptionMapper;
import com.myecommerce.MyECommerce.repository.production.ProductionOptionRepository;
import com.myecommerce.MyECommerce.repository.production.ProductionRepository;
import com.myecommerce.MyECommerce.type.ProductionCategoryType;
import com.myecommerce.MyECommerce.type.ProductionSaleStatusType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static com.myecommerce.MyECommerce.type.ProductionCategoryType.WOMEN_CLOTHING;
import static com.myecommerce.MyECommerce.type.ProductionSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;
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
                .seq(seq)
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
                .seq(seq)
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
                .seq(optionEntity.getSeq())
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
                .options(null)
                .build();

        // stub(가설) : productionMapper.toEntity() 실행 시 requestProductionDto에 대한 Entity 반환 예상.
        given(productionMapper.toEntity(requestProductionDto))
                .willReturn(productionEntity);
        // stub(가설) : productionMapper.toEntity() 실행 시 requestOptionDto에 대한 Entity 반환 예상.
        given(productionOptionMapper.toEntity(requestOptionDto))
                .willReturn(optionEntity);

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
                productionService.saveProduction(requestProductionDto, member);

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
        assertNull(response.getOptions());
        // 상품옵션검증
        RequestProductionOptionDto reqOptFromReqProduction = requestProductionDto.getOptions().get(0);
        assertEquals(reqOptFromReqProduction.getOptionCode(), expectedOptionEntity.getOptionCode());
        assertEquals(reqOptFromReqProduction.getOptionName(), expectedOptionEntity.getOptionName());
        assertEquals(reqOptFromReqProduction.getPrice(), expectedOptionEntity.getPrice());
        assertEquals(reqOptFromReqProduction.getQuantity(), expectedOptionEntity.getQuantity());
        assertEquals(reqOptFromReqProduction.getSeq(), expectedOptionEntity.getSeq());
        assertEquals(expectedProductionEntity, expectedOptionEntity.getProduction());
        assertEquals(expectedOptionEntity.getId(), expectedOptionEntity.getProduction().getId());
    }
}