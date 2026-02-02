package com.myecommerce.MyECommerce.entity.order;

import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    /* ------------------
        Test Fixtures
       ------------------ */

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
        ProductOption registeredOption = ProductOption.builder()
                .price(new BigDecimal("10000"))
                .quantity(10)
                .product(Product.builder()
                        .saleStatus(ON_SALE)
                        .build())
                .build();
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
}