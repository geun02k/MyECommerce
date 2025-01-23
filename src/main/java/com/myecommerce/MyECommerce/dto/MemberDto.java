package com.myecommerce.MyECommerce.dto;

import com.myecommerce.MyECommerce.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    private Long id;

    private String userId;

    private String password;

    private String name;

    private String tel1;

    private String tel2;

    private String tel3;

    private String address;

    private Character delYn;

    // dto -> entity로 변환
    public Member toEntity(MemberDto memberDto) {
        return Member.builder()
                .id(memberDto.getId())
                .userId(memberDto.getUserId())
                .password(memberDto.getPassword())
                .name(memberDto.getName())
                .tel1(memberDto.getTel1())
                .tel2(memberDto.getTel2())
                .tel3(memberDto.getTel3())
                .address(memberDto.getAddress())
                .delYn(memberDto.getDelYn())
                .build();
    }

    // entity -> dto로 변환
    public MemberDto toDto(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .userId(member.getUserId())
                .password(member.getPassword())
                .name(member.getName())
                .tel1(member.getTel1())
                .tel2(member.getTel2())
                .tel3(member.getTel3())
                .address(member.getAddress())
                .delYn(member.getDelYn())
                .build();
    }
}
