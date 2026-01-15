package com.myecommerce.MyECommerce.dto.member;

import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestMemberDto {

    private Long id;

    @NotBlank(message = "{validation.member.user.id.not.blank}")
    @Size(min=1, max=30, message = "{validation.member.user.id.size}")
    private String userId;

    @NotBlank(message = "{validation.member.password.not.blank}")
    private String password;

    @NotBlank(message = "{validation.member.name.not.blank}")
    @Size(min=1, max=50, message = "{validation.member.name.size}")
    @Pattern(regexp = "^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ]+$",
            message = "{validation.member.name.pattern}")
    private String name;

    @NotBlank(message = "{validation.member.telephone.not.blank}")
    @Pattern(regexp = "^[0-9\\-\\s]+$", // 입력필터역할(숫자, 하이픈, 공백만 허용, 전화번호가 될 수있는 번호만 입력)
            message = "{validation.member.telephone.pattern}")
    private String telephone;

    @Size(min=1, max=500, message = "{validation.member.address.size}")
    private String address;

    private Character delYn;

    private List<MemberAuthority> roles;

}
