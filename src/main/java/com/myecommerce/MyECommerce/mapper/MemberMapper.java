package com.myecommerce.MyECommerce.mapper;

import com.myecommerce.MyECommerce.dto.member.RequestMemberDto;
import com.myecommerce.MyECommerce.dto.member.ResponseMemberDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    // mapstruct-processor 라이브러리 추가 등록하지 않을 경우
    // MemberDto, Member, Mappers 클래스 import 불가.

    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    ResponseMemberDto toDto(Member member);
    Member toEntity(RequestMemberDto memberDto);
}
