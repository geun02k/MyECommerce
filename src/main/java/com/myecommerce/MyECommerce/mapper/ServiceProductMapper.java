package com.myecommerce.MyECommerce.mapper;

import com.myecommerce.MyECommerce.dto.product.*;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ServiceProductMapper {

    ServiceProductMapper INSTANCE = Mappers.getMapper(ServiceProductMapper.class);

    ServiceProductionDto toServiceDto(RequestProductionDto productDto);
    ServiceProductionDto toServiceDto(RequestModifyProductionDto productDto);

    ResponseSearchDetailProductionDto toSearchDetailDto(Product product);
    ResponseProductionDto toDto(Product product);
    Product toEntity(ServiceProductionDto productDto);

    ResponseProductionOptionDto toOptionDto(ProductOption option);
    ProductOption toOptionEntity(ServiceProductionOptionDto optionDto);


}
