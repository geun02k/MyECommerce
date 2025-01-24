package com.myecommerce.MyECommerce.dto;

import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import lombok.*;

import java.util.List;

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

    private List<MemberAuthority> authorities;

}
