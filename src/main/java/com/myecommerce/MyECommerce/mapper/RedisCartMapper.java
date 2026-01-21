package com.myecommerce.MyECommerce.mapper;

import com.myecommerce.MyECommerce.dto.cart.RedisCartDto;
import com.myecommerce.MyECommerce.dto.cart.ResponseCartDto;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RedisCartMapper {

    RedisCartMapper INSTANCE = Mappers.getMapper(RedisCartMapper.class);

    @Mapping(source="product.id", target="productId")
    @Mapping(source="product.name", target="productName")
    @Mapping(source="id", target="optionId")
    @Mapping(target ="expiryDate", ignore = true)
    RedisCartDto toRedisDto(ProductOption option);

    ResponseCartDto toResponseDto(RedisCartDto redisCartDto);
}
