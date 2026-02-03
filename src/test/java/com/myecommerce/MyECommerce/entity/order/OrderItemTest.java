package com.myecommerce.MyECommerce.entity.order;

import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.exception.OrderException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.myecommerce.MyECommerce.exception.errorcode.OrderErrorCode.*;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 고객 생성 */
    Member member() {
        return Member.builder()
                .id(5L)
                .build();
    }

    /** 주문물품 생성 */
    ProductOption productOption() {
        return ProductOption.builder()
                .price(new BigDecimal("10000"))
                .quantity(10)
                .product(Product.builder()
                        .saleStatus(ON_SALE)
                        .build())
                .build();
    }

    /* ------------------
        Helper Method
       ------------------ */

    /* ------------------------
        주문 물품 객체 생성 Test
       ------------------------ */

    @Test
    @DisplayName("주문물품 객체생성 성공 - 주문물품 객체 생성 시 주문물품 총액 계산")
    void createOrderItem_shouldCalculateTotalPrice_whenValidRegisteredOptionAndOrderQuantity() {
        // given
        // 주문 상품옵션
        ProductOption registeredOption = ProductOption.builder()
                .price(new BigDecimal("10000"))
                .quantity(10)
                .product(Product.builder()
                        .saleStatus(ON_SALE)
                        .build())
                .build();
        // 주문요청 상품옵션 수량
        int orderQuantity = 3;

        // when
        OrderItem orderItem =
                OrderItem.createOrderItem(registeredOption, orderQuantity);

        // then
        // 값 매핑 검증
        assertEquals(orderQuantity, orderItem.getQuantity());
        assertEquals(new BigDecimal("10000"), orderItem.getUnitPrice());
        assertEquals(registeredOption.getProduct(), orderItem.getProduct());
        assertEquals(registeredOption, orderItem.getOption());
        // 금액 계산 로직 검증 (10000원 * 3개 = 30000)
        assertEquals(new BigDecimal("30000"), orderItem.getTotalPrice());
    }

    @Test
    @DisplayName("주문물품 객체 생성 실패 - 주문 상품옵션의 금액이 0원 미초과 시 주문물품 객체 생성 불가")
    void createOrderItem_shouldNotCreateOrderItem_whenOrderItemPriceInvalid() {
        // given
        // 주문 상품옵션
        ProductOption invalidOption = ProductOption.builder()
                .price(BigDecimal.ZERO) // 주문 물품 가격 없음
                .quantity(10)
                .product(Product.builder()
                        .saleStatus(ON_SALE)
                        .build())
                .build();
        // 주문요청 상품옵션 수량
        int orderQuantity = 3;

        // when
        // then
        OrderException e = assertThrows(OrderException.class, () ->
                OrderItem.createOrderItem(invalidOption, orderQuantity));
        assertEquals(ORDER_ITEM_PRICE_INVALID, e.getErrorCode());
    }

    @Test
    @DisplayName("주문물품 객체 생성 실패 - 주문 수량이 최소 수량인 1개 미만이면 주문물품 객체 생성 불가")
    void createOrderItem_shouldNotCreateItem_whenOrderItemMinQuantityBelow() {
        // 주문 상품옵션
        ProductOption option = productOption();
        // 주문요청 상품옵션 수량
        int invalidOrderQuantity = 0; // 최소 주문 수량 미달

        // when
        // then
        OrderException e = assertThrows(OrderException.class, () ->
                OrderItem.createOrderItem(option, invalidOrderQuantity));
        assertEquals(ORDER_ITEM_MIN_QUANTITY_BELOW, e.getErrorCode());
    }

    @Test
    @DisplayName("주문물품 객체 생성 실패 - 상품옵션 재고가 없으면 주문물품 객체 생성 불가")
    void createOrder_shouldNotCreateOrder_whenProductOptionOutOfStock() {
        // given
        // 주문 상품옵션
        ProductOption invalidOption = ProductOption.builder()
                .price(new BigDecimal("10000"))
                .quantity(0) // 재고 = 0 -> 상품옵션 품절
                .product(Product.builder()
                        .saleStatus(ON_SALE)
                        .build())
                .build();
        // 주문요청 상품옵션 수량
        int orderQuantity = 3;

        // when
        // then
        OrderException e = assertThrows(OrderException.class, () ->
                OrderItem.createOrderItem(invalidOption, orderQuantity));
        assertEquals(PRODUCT_OPTION_OUT_OF_STOCK, e.getErrorCode());
    }

    @Test
    @DisplayName("주문물품 객체 생성 실패 - 주문 수량이 재고를 초과하면 주문물품 생성 불가")
    void createOrder_shouldNotCreateOrder_whenOrderAvailableQuantityExceeded() {
        // given
        // 주문 상품옵션
        ProductOption validOption = productOption();
        // 주문요청 상품옵션 수량
        int invalidOrderQuantity = 20;  // 주문 상품옵션 재고 초과

        // when
        // then
        OrderException e = assertThrows(OrderException.class, () ->
                OrderItem.createOrderItem(validOption, invalidOrderQuantity));
        assertEquals(ORDER_AVAILABLE_QUANTITY_EXCEEDED, e.getErrorCode());
    }

}