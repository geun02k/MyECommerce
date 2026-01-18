package com.myecommerce.MyECommerce.mapper;

import com.myecommerce.MyECommerce.dto.product.*;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ServiceProductMapper {

    ServiceProductMapper INSTANCE = Mappers.getMapper(ServiceProductMapper.class);

    ServiceProductDto toServiceDto(RequestProductionDto productDto);
    ServiceProductDto toServiceDto(RequestModifyProductionDto productDto);

    ResponseSearchDetailProductionDto toSearchDetailDto(Product product);
    ResponseProductionDto toDto(Product product);
    Product toEntity(ServiceProductDto productDto);

    ResponseProductionOptionDto toOptionDto(ProductOption option);
    ProductOption toOptionEntity(ServiceProductOptionDto optionDto);


}
