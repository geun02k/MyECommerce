package com.myecommerce.MyECommerce.entity.member;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.myecommerce.MyECommerce.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {

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

}
