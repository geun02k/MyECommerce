package com.myecommerce.MyECommerce.entity.order;

import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    // 주문물품:주문 (1:N)
    // 주문 데이터를 order 테이블 join해서 가져옴.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id", nullable = false) // 테이블 매핑 시 foreign key 지정
    private Order order;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    // 주문물품:상품 (N:1)
    // 상품 데이터를 product 테이블 join해서 가져옴.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id", nullable = false) // 테이블 매핑 시 foreign key 지정
    private Product product;

    // 주문물품:상품옵션 (N:1)
    // 상품옵션 데이터를 product_option 테이블 join해서 가져옴.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_option_id", nullable = false) // 테이블 매핑 시 foreign key 지정
    private ProductOption option;

}
