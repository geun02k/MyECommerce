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

    @Size(min=1, max=30, message = "사용자 아이디는 최소 1자, 최대 30자로 제한됩니다.")
    private String userId;

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    private String password;

    @Size(min=1, max=50, message = "사용자명은 최소 1자, 최대 50자로 제한됩니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ]+$", message = "사용자명은 영문자, 한글만 사용 가능합니다.")
    private String name;

    @Size(min=10, max=11, message = "전화번호는 최소 10자, 최대 11자로 제한됩니다.")
    @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "유효하지 않은 전화번호입니다.")
    private String telephone;

    @Size(min=1, max=500, message = "주소는 최소 1자, 최대 500자로 제한됩니다.")
    private String address;

    private Character delYn;

    private List<MemberAuthority> roles;

}
