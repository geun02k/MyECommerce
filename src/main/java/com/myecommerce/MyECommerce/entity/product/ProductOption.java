package com.myecommerce.MyECommerce.entity.product;

import com.myecommerce.MyECommerce.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(  // 비즈니스 키
                name = "uk_product_option_product_id_option_code",
                columnNames = {"product_id", "option_code"} // @UniqueConstraint의 columnNames에는 엔티티 필드명이 아니라 실제 DB 컬럼명 작성
)})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 50)
    private String optionCode;

    @Column(nullable = false, length = 100)
    private String optionName;

    @Column(nullable = false)
    @ColumnDefault("0")
    private BigDecimal price;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int quantity;

    @ManyToOne
    @JoinColumn(name="product_id") // 테이블 매핑 시 foreign key 지정
    private Product product;

}
