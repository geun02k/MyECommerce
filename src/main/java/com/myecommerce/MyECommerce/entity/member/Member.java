package com.myecommerce.MyECommerce.entity.member;

import com.myecommerce.MyECommerce.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String userId;

    @Column(nullable = false, length = 50)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 3)
    private String tel1;

    @Column(nullable = false, length = 3)
    private String tel2;

    @Column(nullable = false, length = 3)
    private String tel3;

    @Column(nullable = false, length = 500)
    private String address;

    @Column(nullable = false)
    private Character delYn;

}
