package com.myecommerce.MyECommerce.mapper;

import com.myecommerce.MyECommerce.dto.production.*;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.entity.product.Product;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ServiceProductionMapper {

    ServiceProductionMapper INSTANCE = Mappers.getMapper(ServiceProductionMapper.class);

    ServiceProductionDto toServiceDto(RequestProductionDto productionDto);
    ServiceProductionDto toServiceDto(RequestModifyProductionDto productionDto);

    ResponseSearchDetailProductionDto toSearchDetailDto(Product product);
    ResponseProductionDto toDto(Product product);
    Product toEntity(ServiceProductionDto productDto);

    ResponseProductionOptionDto toOptionDto(ProductOption option);
    ProductOption toOptionEntity(ServiceProductionOptionDto optionDto);


}
