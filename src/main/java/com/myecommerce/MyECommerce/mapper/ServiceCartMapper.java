package com.myecommerce.MyECommerce.mapper;

import com.myecommerce.MyECommerce.dto.cart.ResponseCartDto;
import com.myecommerce.MyECommerce.dto.cart.ServiceCartDto;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ServiceCartMapper {

    ServiceCartMapper INSTANCE = Mappers.getMapper(ServiceCartMapper.class);

    @Mapping(source="product.id", target="productId")
    @Mapping(source="product.name", target="productName")
    @Mapping(source="id", target="optionId")
    @Mapping(target ="expiryDate", ignore = true)
    ServiceCartDto toDto(ProductOption productionOption);

    ResponseCartDto toResponseDto(ServiceCartDto serviceCartDto);
}
