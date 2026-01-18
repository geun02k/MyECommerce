package com.myecommerce.MyECommerce.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestSignInMemberDto {
    @NotBlank(message = "{validation.member.user.id.not.blank}")
    @Size(min=1, max=30, message = "{validation.member.user.id.size}")
    private String userId;

    @NotBlank(message = "{validation.member.password.not.blank}")
    private String password;
}
