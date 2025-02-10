package com.myecommerce.MyECommerce.mapper;

import com.myecommerce.MyECommerce.dto.production.RequestProductionDto;
import com.myecommerce.MyECommerce.dto.production.ResponseProductionDto;
import com.myecommerce.MyECommerce.entity.production.Production;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductionMapper {

    ProductionMapper INSTANCE = Mappers.getMapper(ProductionMapper.class);

    ResponseProductionDto toDto(Production production);
    Production toEntity(RequestProductionDto productionDto);
}
