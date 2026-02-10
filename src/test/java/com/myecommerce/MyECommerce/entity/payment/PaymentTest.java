package com.myecommerce.MyECommerce.entity.payment;

import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.order.OrderItem;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.service.payment.PgClient;
import com.myecommerce.MyECommerce.service.payment.TestPgClientImpl;
import com.myecommerce.MyECommerce.type.PaymentMethodType;
import com.myecommerce.MyECommerce.type.PgProviderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.myecommerce.MyECommerce.type.PaymentMethodType.CARD;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.*;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 고객 생성 */
    Member member() {
        return Member.builder()
                .id(5L)
                .build();
    }

    /** 상품옵션 생성 */
    ProductOption productOption() {
        return ProductOption.builder()
                .price(new BigDecimal("10000"))
                .quantity(10)
                .product(Product.builder()
                        .saleStatus(ON_SALE)
                        .build())
                .build();
    }

    /** PG 결제대행사 반환 */
    PgProviderType pgProvider() {
        PgClient pgClient = new TestPgClientImpl();
        return pgClient.getProvider();
    }

    /** 주문 생성 */
    private Order order() {
        // 주문 상품옵션
        ProductOption productOption = productOption();
        // 주문요청 상품옵션 수량
        int orderQuantity = 1;
        // 주문 물품 목록
        OrderItem item = OrderItem.createOrderItem(productOption, orderQuantity);
        // 주문자
        Member member = member();

        return Order.createOrder(List.of(item), member);
    }

    /* ------------------
        Helper Method
       ------------------ */

    /* ----------------------
        결제 생성 Tests
       ---------------------- */

    @Test
    @DisplayName("결제 객체 생성 성공")
    void createPayment_shouldPaymentStatusIsReady_whenOrderStatusIsCreated() {
        // given
        // 등록된 주문 (주문 상태는 CREATED)
        Order order = order();
        // 요청 결제 방식
        PaymentMethodType requestMethod = CARD;
        // 회사와 결제 계약된 PG사
        PgProviderType pgProvider = pgProvider();

        // when
        Payment payment =
                Payment.createPayment(order, requestMethod, pgProvider);

        // then
        assertEquals(order, payment.getOrder());
        assertNotNull(payment.getPaymentCode());
        assertEquals(requestMethod, payment.getPaymentMethod());
        assertEquals(pgProvider, payment.getPgProvider());
        assertEquals(READY, payment.getPaymentStatus()); // 결제 생성 상태
    }

}