package com.myecommerce.MyECommerce.mapper;

import com.myecommerce.MyECommerce.dto.production.*;
import com.myecommerce.MyECommerce.entity.production.Production;
import com.myecommerce.MyECommerce.entity.production.ProductionOption;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ServiceProductionMapper {

    ServiceProductionMapper INSTANCE = Mappers.getMapper(ServiceProductionMapper.class);

    ServiceProductionDto toServiceDto(RequestProductionDto productionDto);
    ServiceProductionDto toServiceDto(RequestModifyProductionDto productionDto);

    ResponseSearchDetailProductionDto toSearchDetailDto(Production production);
    ResponseProductionDto toDto(Production production);
    Production toEntity(ServiceProductionDto productionDto);

    ResponseProductionOptionDto toOptionDto(ProductionOption option);
    ProductionOption toOptionEntity(ServiceProductionOptionDto optionDto);


}
