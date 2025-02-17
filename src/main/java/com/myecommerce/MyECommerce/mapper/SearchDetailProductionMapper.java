package com.myecommerce.MyECommerce.mapper;

import com.myecommerce.MyECommerce.dto.production.ResponseSearchDetailProductionDto;
import com.myecommerce.MyECommerce.entity.production.Production;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SearchDetailProductionMapper {

    SearchDetailProductionMapper INSTANCE = Mappers.getMapper(SearchDetailProductionMapper.class);

    ResponseSearchDetailProductionDto toDto(Production production);
}
