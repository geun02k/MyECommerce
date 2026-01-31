package com.myecommerce.MyECommerce.entity.order;

import com.myecommerce.MyECommerce.entity.BaseEntity;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.exception.ProductException;
import com.myecommerce.MyECommerce.type.OrderStatusType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.myecommerce.MyECommerce.exception.errorcode.ProductErrorCode.PRODUCT_NOT_ON_SALE;
import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE) // Builder 접근 제한
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 생성자 접근 제한, Lombok Builder의 내부 구현으로 없으면 @Builder 오류발생
@NoArgsConstructor(access = AccessLevel.PROTECTED) // application 접근제한, JPA(Hibernate)는 리플렉션으로 접근 가능
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    // yyyyMMdd 8자리 + 하이픈 + 주문 ID 9자리
    // 추후 주문ID 값 증가 예상으로 길이 제한하지 않음.
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
    // 한 주문이 여러 주문 상품울 가질 수 있음.
    // CascadeType: 주문이 주문물품 생명주기 관리.
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order")
    private List<OrderItem> items = new ArrayList<>();

    /* ----------------------
        Method
       ---------------------- */

    /** 주문 생성 **/
    public static Order createOrder(List<OrderItem> items, Member member) {
        // 유효성 검증 (불변 도메인 규칙 검증)
        validateOrder(items);

        LocalDateTime now = LocalDateTime.now();

        // totalPrice, items 외 값 추가
        Order order = Order.builder()
                .orderNumber(createOrderNumber(now))
                .buyer(member)
                .orderStatus(CREATED)
                .orderedAt(now)
                .build();

        // 주문 물품 목록 셋팅 및 연관관계 설정
        order.setItems(items);
        // 총 주문 금액 계산 및 셋팅
        order.setCalculatedTotalPrice();

        return order;
    }

    // 유효성 검증
    private static void validateOrder(List<OrderItem> items) {
        validateProductSaleStatus(items);
    }

    // 상품 목록 판매상태 검증 정책
    private static void validateProductSaleStatus(List<OrderItem> items) {
        // 판매중이 아닌 상품 수 조회
        long notOnSaleProductCnt = items.stream()
                .map(item -> item.getProduct().getSaleStatus())
                .filter(saleStatus -> saleStatus != ON_SALE)
                .count();

        // 판매중이 아닌 상품 주문 불가
        if (0 < notOnSaleProductCnt) {
            throw new ProductException(PRODUCT_NOT_ON_SALE);
        }
    }

    // 주문 물품 목록 셋팅 및 연관관계 설정
    private void setItems(List<OrderItem> items) {
        this.items = items;
        this.items.forEach(orderItem ->
                orderItem.assignOrder(this));
    }

    // 총 금액 계산 및 셋팅
    private void setCalculatedTotalPrice() {
        this.totalPrice = this.items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // reduce(초기값, 연산) : 전체합산
    }

    // 주문번호 생성
    private static String createOrderNumber(LocalDateTime now) {
        // yyyyMMdd 8자리 + 하이픈 + 주문 ID 9자리
        return now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + "-" +  UUID.randomUUID().toString().substring(0,9).toUpperCase();
    }

}
