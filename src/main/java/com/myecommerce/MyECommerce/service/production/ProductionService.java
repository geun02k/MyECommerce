package com.myecommerce.MyECommerce.service.production;

import com.myecommerce.MyECommerce.dto.production.RequestProductionDto;
import com.myecommerce.MyECommerce.dto.production.ResponseProductionDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.production.Production;
import com.myecommerce.MyECommerce.entity.production.ProductionOption;
import com.myecommerce.MyECommerce.mapper.ProductionMapper;
import com.myecommerce.MyECommerce.mapper.ProductionOptionMapper;
import com.myecommerce.MyECommerce.repository.production.ProductionOptionRepository;
import com.myecommerce.MyECommerce.repository.production.ProductionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public ResponseProductionDto saveProduction(RequestProductionDto requestProductionDto,
                                                Member member) {
        // 상품 dto -> entity 변환 및 상품등록 기본데이터 셋팅
        Production production = this.setProductionEntityDataFromDto(
                requestProductionDto, member);
        // 상품옵션 dto -> entity 변환
        List<ProductionOption> optionEntityList =
                requestProductionDto.getOptions().stream()
                        .map(productionOptionMapper::toEntity)
                        .toList();

        // 상품 등록
        Production savedProduction = productionRepository.save(production);
        // 상품옵션목록 등록
        optionEntityList.forEach(option -> {
            option.setProduction(savedProduction);
            productionOptionRepository.save(option);
        });

        // 상품, 상품옵션목록 반환
        return productionMapper.toDto(savedProduction);

//        // 추후 삭제 예정
//        // 상품, 상품옵션목록 등록 및 반환
//        // -> Production entity가 요청시 받은 options 정보까지도 가지고 함께 isnert 처리되기를 바람.
//        // -> options가 production 정보를 가지고있지 않아서 productionId가 null로 입력됨.
//        // -> 그래서 아래의 코드를 사용하지 못하고 상품과 상품옵션등록을 분할해서 진행.
//        return productionMapper.toDto( // 상품, 상품옵션목록 반환
//                productionRepository.save( // 상품, 상품옵션목록 등록
//                        setProductionEntityDataFromDto(requestProductionDto, member))); // dto->entity변환 및 상품등록 기본데이터 셋팅
    }

    // 상품등록을 위한 dto -> entity 변환 및 상품등록 기본데이터 셋팅
    private Production setProductionEntityDataFromDto(RequestProductionDto productionDto,
                                       Member member) {
        Production productionEntity = productionMapper.toEntity(productionDto);

        productionEntity.setSeller(member.getId());
        productionEntity.setSaleStatus(ON_SALE);
        productionEntity.setOptions(null);
        // production Entity 저장 시 option Entity를 자동저장하지 않도록 하기 위함인데
        // 이렇게 null처리를 수동으로 해줘도 되는지는 모르겠음...

        return productionEntity;
    }
}
