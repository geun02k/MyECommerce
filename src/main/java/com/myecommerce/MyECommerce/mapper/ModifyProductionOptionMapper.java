package com.myecommerce.MyECommerce.mapper;

import com.myecommerce.MyECommerce.dto.production.RequestModifyProductionOptionDto;
import com.myecommerce.MyECommerce.dto.production.ResponseProductionOptionDto;
import com.myecommerce.MyECommerce.entity.production.ProductionOption;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ModifyProductionOptionMapper {

    ModifyProductionOptionMapper INSTANCE = Mappers.getMapper(ModifyProductionOptionMapper.class);

    ResponseProductionOptionDto toDto(ProductionOption productionOption);

    ProductionOption toEntity(RequestModifyProductionOptionDto productionOptionDto);

}
