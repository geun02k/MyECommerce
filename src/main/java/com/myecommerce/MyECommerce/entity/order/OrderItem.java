package com.myecommerce.MyECommerce.entity.order;

import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.exception.OrderException;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.myecommerce.MyECommerce.exception.errorcode.OrderErrorCode.*;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE) // Builder 접근 제한
@AllArgsConstructor(access = AccessLevel.PRIVATE)  // 생성자 접근 제한
@NoArgsConstructor(access = AccessLevel.PROTECTED) // application 접근제한, JPA(Hibernate)는 리플렉션으로 접근 가능
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

    /* ----------------------
        Method
       ---------------------- */

    private final static int ITEM_MIN_QUANTITY = 1; // 물품 당 최소 주문 수량 (불변, JPA가 Entity 생성 시 무시)

    /** 주문 물품 생성 **/
    public static OrderItem createOrderItem(ProductOption registeredOption,
                                            int orderQuantity) {
        // 유효성 검증 (불변 도메인 규칙 검증)
        validateOrderItem(registeredOption, orderQuantity);

        // totalPrice, order 외 값 추가
        OrderItem orderItem = OrderItem.builder()
                .quantity(orderQuantity)
                .unitPrice(registeredOption.getPrice())
                .product(registeredOption.getProduct())
                .option(registeredOption)
                .build();

        // 주문 물품 총 금액 셋팅
        orderItem.setCalculatedTotalPrice();

        return orderItem;
    }

    /** 주문 물품에 주문 객체 할당 **/
   void assignOrder(Order order) {
        if (this.order != null) {
            throw new OrderException(ITEM_ALREADY_ORDERED);
        }
        this.order = order;
    }

    // 불변 조건 강제 (null, 입력 금지 값을 허용하지 않는 상태만 생성되게 만드는 것이 핵심)
    private static void validateOrderItem(ProductOption registeredOption, int orderQuantity) {
        // 주문 물품 가격 방어 검증
        validateItemPrice(registeredOption.getPrice());
        // 주문 최소 수량 검증
        validateMinOrderQuantity(orderQuantity);
        // 상품 옵션 품절 여부 확인
        validateOutOfStock(registeredOption, orderQuantity);
    }

    // 주문 물품 가격 방어 검증
    private static void validateItemPrice(BigDecimal optionPrice) {
        // 주문 물품 가격 필수
        if (optionPrice == null) {
            throw new OrderException(ORDER_ITEM_PRICE_INVALID);
        }
        // 주문 물품 가격은 양수
        if (optionPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new OrderException(ORDER_ITEM_PRICE_INVALID);
        }
    }

    // 주문 최소 수량 검증
    private static void validateMinOrderQuantity(int orderQuantity) {
        // 주문 수량 최소 1개 이상
        if (orderQuantity < ITEM_MIN_QUANTITY) {
            throw new OrderException(ORDER_ITEM_MIN_QUANTITY_BELOW);
        }
    }

    // 상품 옵션 품절 정책
    private static void validateOutOfStock(ProductOption registeredOption,
                                           int orderQuantity) {
        int sellAvailableQuantity = registeredOption.getQuantity();

        // 1. 상품 판매 가능 수량이 0이면 품절로 주문불가
        // 등록된 옵션의 판매가능수량 체크
        if(sellAvailableQuantity <= 0) {
            throw new OrderException(PRODUCT_OPTION_OUT_OF_STOCK);
        }

        // 2. 요청 수량이 판매 수량보다 많으면 주문불가
        if(sellAvailableQuantity < orderQuantity) {
            throw new OrderException(ORDER_AVAILABLE_QUANTITY_EXCEEDED);
        }
    }

    // 주문 물품 총 금액 셋팅
    private void setCalculatedTotalPrice() {
        this.totalPrice = this.unitPrice
                .multiply(BigDecimal.valueOf(this.quantity))
                .setScale(0, RoundingMode.HALF_UP); // 소수점없음, 소수점 1자리에서 반올림
    }

}
