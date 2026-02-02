package com.myecommerce.MyECommerce.entity.order;

import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.exception.ProductException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.myecommerce.MyECommerce.exception.errorcode.ProductErrorCode.PRODUCT_NOT_ON_SALE;
import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 판매중단된 주문 물품 생성 (유효하지 않은 주문 물품) */
    OrderItem discontinuedItem() {
        ProductOption registeredOption = ProductOption.builder()
                .price(new BigDecimal("10000"))
                .quantity(10)
                .product(Product.builder()
                        .saleStatus(DISCONTINUED)
                        .build())
                .build();
        int orderQuantity = 3;

        return OrderItem.createOrderItem(registeredOption, orderQuantity);
    }

    /** 고객 생성 */
    Member member() {
        return Member.builder()
                .id(5L)
                .build();
    }

    /* ------------------
        Helper Method
       ------------------ */

    /* ------------------
        주문 객체 생성 Test
       ------------------ */
    @Test
    @DisplayName("주문 객체 생성 성공 - 주문 생성 시 주문 물품 양방향 연관관계 설정 및 총액 계산")
    void createOrder_shouldAssignOrderAndCalculateTotalPrice_whenValidItemsAndMember() {
        // given
        // 주문 물품 목록
        ProductOption registeredOption = ProductOption.builder()
                .price(new BigDecimal("10000"))
                .quantity(10)
                .product(Product.builder()
                        .saleStatus(ON_SALE)
                        .build())
                .build();
        int orderQuantity = 3;
        OrderItem item = OrderItem.createOrderItem(registeredOption, orderQuantity);
        // 주문자
        Member member = member();

        // when
        Order order = Order.createOrder(List.of(item), member);

        // then
        assertEquals(member, order.getBuyer());
        assertEquals(CREATED, order.getOrderStatus()); // 주문 생성 시 기본 상태 검증1
        assertNotNull(order.getOrderedAt()); // 주문 생성 시 기본 상태 검증2
        assertNotNull(order.getOrderNumber());
        assertEquals(1, order.getItems().size());
        OrderItem resultOrderItem = order.getItems().get(0);
        assertEquals(item, resultOrderItem);

        assertEquals(order, resultOrderItem.getOrder()); // 연관관계 검증
        assertEquals(new BigDecimal("30000"), order.getTotalPrice()); // 금액 계산 로직 검증
    }

    @Test
    @DisplayName("주문 객체 생성 실패 - 판매 중이 아니면 주문 생성 불가")
    void createOrder_shouldNotCreateOrder_whenProductNotOnSale() {
        // given
        // 주문 물품 목록
        OrderItem discontinuedItem = discontinuedItem(); // 판매중단된 상품 옵션 주문
        // 주문자
        Member member = member();

        // when
        // then
        ProductException e = assertThrows(ProductException.class, () ->
                Order.createOrder(List.of(discontinuedItem), member));
        assertEquals(PRODUCT_NOT_ON_SALE, e.getErrorCode());
    }

}