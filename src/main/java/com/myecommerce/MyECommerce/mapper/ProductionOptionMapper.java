package com.myecommerce.MyECommerce.mapper;

import com.myecommerce.MyECommerce.dto.production.RequestProductionDto;
import com.myecommerce.MyECommerce.dto.production.RequestProductionOptionDto;
import com.myecommerce.MyECommerce.dto.production.ResponseProductionDto;
import com.myecommerce.MyECommerce.dto.production.ResponseProductionOptionDto;
import com.myecommerce.MyECommerce.entity.production.Production;
import com.myecommerce.MyECommerce.entity.production.ProductionOption;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductionOptionMapper {

    ProductionOptionMapper INSTANCE = Mappers.getMapper(ProductionOptionMapper.class);

    ResponseProductionOptionDto toDto(ProductionOption productionOption);
    ProductionOption toEntity(RequestProductionOptionDto productionOptionDto);
}
