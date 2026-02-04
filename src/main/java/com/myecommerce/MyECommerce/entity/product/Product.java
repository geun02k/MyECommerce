package com.myecommerce.MyECommerce.entity.product;

import com.myecommerce.MyECommerce.entity.BaseEntity;
import com.myecommerce.MyECommerce.type.ProductCategoryType;
import com.myecommerce.MyECommerce.type.ProductSaleStatusType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(indexes = {@Index(name = "idx_product_search", columnList = "saleStatus,category")}) // 복합인덱스 생성
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long seller;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ProductCategoryType category;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProductSaleStatusType saleStatus;

    // 상품:옵션 (1:N)
    // 상품의 옵션 데이터를 production_option 테이블 join해서 가져옴.
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "product")
    private List<ProductOption> options; // 한 상품이 여러 옵션을 가질 수 있음.

}
