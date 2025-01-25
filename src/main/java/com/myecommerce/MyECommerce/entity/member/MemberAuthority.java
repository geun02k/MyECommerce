package com.myecommerce.MyECommerce.entity.member;


import com.myecommerce.MyECommerce.entity.BaseEntity;
import com.myecommerce.MyECommerce.type.MemberAuthorityType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Setter
@Builder
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor
public class MemberAuthority extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    private Long memberId;

    @Enumerated(EnumType.STRING) // enum값 string으로 저장
    private MemberAuthorityType authority;

    @ManyToOne
    @JoinColumn(name="memberId", insertable = false, updatable = false) // 테이블 매핑 시 foreign key 지정
    private Member member;

}
