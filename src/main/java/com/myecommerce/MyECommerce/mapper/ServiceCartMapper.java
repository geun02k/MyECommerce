package com.myecommerce.MyECommerce.mapper;

import com.myecommerce.MyECommerce.dto.cart.ServiceCartDto;
import com.myecommerce.MyECommerce.entity.production.ProductionOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ServiceCartMapper {

    ServiceCartMapper INSTANCE = Mappers.getMapper(ServiceCartMapper.class);

    @Mapping(source="production.id", target="productId")
    @Mapping(source="production.name", target="productName")
    @Mapping(source="id", target="optionId")
    @Mapping(target ="expiryDate", ignore = true)
    ServiceCartDto toDto(ProductionOption productionOption);
}
