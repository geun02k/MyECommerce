package com.myecommerce.MyECommerce.entity.production;

import com.myecommerce.MyECommerce.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductionOption extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false, length = 10)
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
    @JoinColumn(name="productionId") // 테이블 매핑 시 foreign key 지정
    private Production production;

}
