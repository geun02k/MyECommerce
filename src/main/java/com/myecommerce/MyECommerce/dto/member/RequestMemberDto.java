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

    @Size(min=1, max=30, message = "{validation.member.user.id.size}")
    private String userId;

    @NotBlank(message = "{validation.member.password.not.blank}")
    private String password;

    @Size(min=1, max=50, message = "{validation.member.name.size}")
    @Pattern(regexp = "^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ]+$",
            message = "{validation.member.name.pattern}")
    private String name;

    @Size(min=10, max=11, message = "{validation.member.telephone.size}")
    @Pattern(regexp = "^01[016789]\\d{7,8}$",
            message = "{validation.member.telephone.pattern}")
    private String telephone;

    @Size(min=1, max=500, message = "{validation.member.address.size}")
    private String address;

    private Character delYn;

    private List<MemberAuthority> roles;

}
