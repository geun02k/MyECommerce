package com.myecommerce.MyECommerce.entity.member;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.myecommerce.MyECommerce.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 11)
    private String telephone;

    @Column(nullable = false, length = 500)
    private String address;

    @ColumnDefault("'N'")
    private Character delYn;

    // 유저:권한 (1:N)
    // 사용자 권한 데이터 member_authority 테이블 join해서 가져옴.
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "member")
    @JsonManagedReference
    private List<MemberAuthority> authorities; // 사용자가 여러 권한을 가질 수 있음.

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // authorities 정보를 SimpleGrantedAuthority로 매핑
        // -> 스프링 시큐리티에서 지원하는 role 관련기능을 쓰기 위함.
        return this.authorities.stream()
                .map((MemberAuthority authority) ->
                        new SimpleGrantedAuthority(authority.getAuthority().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.userId;
    }
}
