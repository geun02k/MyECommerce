package com.myecommerce.MyECommerce.entity.order;

import com.myecommerce.MyECommerce.entity.BaseEntity;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.type.OrderStatusType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    // yyyyMMddHHmmss + 4자리 시퀀스
    // -> 동시성 고려, 초당 최대 9,999건 처리가능
    @Column(nullable = false, unique = true, length = 18)
    private String orderNumber;

    // 주문:회원 (N:1)
    // 회원 데이터를 member 테이블 join해서 가져옴.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="buyer_id", nullable = false) // 테이블 매핑 시 foreign key 지정
    private Member buyer;

    @Builder.Default
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatusType orderStatus;

    @Column(nullable = false)
    private LocalDateTime orderedAt;

    // 주문:주문물품 (1:N)
    // 주문 물품 데이터를 order_item 테이블 join해서 가져옴.
    // 연관관계의 주인은 OrderItem.
    // 한 주문이 여러 주문 상품울 거잘 수 있음.
    // CascadeType: 주문이 주문물품 생명주기 관리.
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order")
    private List<OrderItem> items = new ArrayList<>();

}
