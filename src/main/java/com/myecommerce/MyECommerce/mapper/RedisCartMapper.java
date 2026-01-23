package com.myecommerce.MyECommerce.mapper;

import com.myecommerce.MyECommerce.dto.cart.RedisCartDto;
import com.myecommerce.MyECommerce.dto.cart.ResponseCartDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RedisCartMapper {

    RedisCartMapper INSTANCE = Mappers.getMapper(RedisCartMapper.class);


    ResponseCartDto toResponseDto(RedisCartDto redisCartDto);
}
