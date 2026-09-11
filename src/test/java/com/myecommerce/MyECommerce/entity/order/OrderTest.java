package com.myecommerce.MyECommerce.entity.order;

import com.myecommerce.MyECommerce.dto.payment.PgApprovalResult;
import com.myecommerce.MyECommerce.dto.payment.PgResult;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.exception.OrderException;
import com.myecommerce.MyECommerce.exception.PaymentException;
import com.myecommerce.MyECommerce.exception.ProductException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static com.myecommerce.MyECommerce.exception.errorcode.OrderErrorCode.ORDER_STATUS_NOT_CREATED;
import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.PAYMENT_NOT_APPROVED;
import static com.myecommerce.MyECommerce.exception.errorcode.ProductErrorCode.PRODUCT_NOT_ON_SALE;
import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;
import static com.myecommerce.MyECommerce.type.OrderStatusType.PAID;
import static com.myecommerce.MyECommerce.type.PaymentMethodType.*;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.APPROVED;
import static com.myecommerce.MyECommerce.type.PgProviderType.*;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 상품 옵션 */
    ProductOption productOption(String price) {
        return ProductOption.builder()
                .price(new BigDecimal(price))
                .quantity(10)
                .product(Product.builder()
                        .saleStatus(ON_SALE)
                        .build())
                .build();
    }

    /** 판매중단된 주문 물품 생성 (유효하지 않은 주문 물품) */
    OrderItem discontinuedOrderItem() {
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

    /** 주문물품 생성 */
    OrderItem orderItem(String price, int orderQuantity) {
        // 상품옵션 생성
        ProductOption productOption = productOption(price);
        // 주문물품 생성
        return OrderItem.createOrderItem(productOption, orderQuantity);
    }
    OrderItem orderItem() {
        return orderItem("10000", 10);
    }

    /** 고객 생성 */
    Member member() {
        return Member.builder()
                .id(5L)
                .build();
    }

    /** 주문 생성 */
    Order order() {
        return Order.createOrder(List.of(orderItem()), member());
    }

    /** 결제 생성 */
    Payment payment(Order order) {
        return Payment.createPayment(order, CARD, MOCK_PG);
    }

    /** PG 승인된 결제 */
    Payment approvedPayment(Order order) {
        PgResult pgRequestResult = pgResult();
        PgApprovalResult pgApprovalResult = pgApprovalResult();

        Payment payment = payment(order);
        payment.requestPgPayment(pgRequestResult); // 결제상태 = IN_PROGRESS
        payment.approve(pgApprovalResult); // 결제상태 = APPROVED

        return payment;
    }

    /** PG 요청에 대한 응답 */
    PgResult pgResult() {
        return PgResult.builder()
                .pgTransactionId("pgTransactionId")
                .build();
    }

    /** PG 승인 응답 */
    PgApprovalResult pgApprovalResult() {
        return PgApprovalResult.builder()
                .pgTransactionId("pgTransactionId")
                .approvalStatus(APPROVED)
                .paidAmount(new BigDecimal(10000))
                .build();
    }

    /* ------------------
        Helper Method
       ------------------ */

    /* -------------------
        주문 객체 생성 Test
       ------------------- */

    @Test
    @DisplayName("주문 객체 생성 성공 - 주문 객체 생성 시 주문 물품 양방향 연관관계 설정 및 총액 계산")
    void createOrder_shouldAssignOrderAndCalculateTotalPrice_whenValidItemsAndMember() {
        // given
        int orderQuantity = 3; // 구매 수량
        String optionUnitPrice = "10000"; // 옵션 금액
        BigDecimal totalPrice = new BigDecimal(optionUnitPrice)
                .multiply(BigDecimal.valueOf(orderQuantity)); // 총 주문 금액
        // 주문 물품 목록
        OrderItem item = OrderItem.createOrderItem(
                productOption(optionUnitPrice), orderQuantity);
        // 주문자
        Member member = member();

        // when
        Order order = Order.createOrder(List.of(item), member);

        // then
        // 주문 생성 검증
        assertEquals(CREATED, order.getOrderStatus());
        assertNotNull(order.getOrderedAt());
        assertNotNull(order.getOrderNumber());
        assertEquals(member, order.getBuyer());
        assertEquals(totalPrice, order.getTotalPrice());

        // 주문 물품 생성 검증
        assertEquals(1, order.getItems().size());
        OrderItem resultOrderItem = order.getItems().get(0);
        assertEquals(item, resultOrderItem);
        assertEquals(order, resultOrderItem.getOrder()); // 연관관계 검증
    }

    @Test
    @DisplayName("주문 객체 생성 성공 - 주문 객체 생성 시 여러 주문물품의 금액은 합산")
    void createOrder_shouldCalculateTotalPrice_whenOrderItemsRequest() {
        // given
        // 주문 물품 목록
        OrderItem item1 = orderItem("10000", 3); // 10,000 * 3 = 30,000
        OrderItem item2 = orderItem("1000", 5); // 1,000 * 5 = 5,000
        // 주문자
        Member member = member();

        // when
        Order order = Order.createOrder(List.of(item1, item2), member);

        // then
        // 금액 계산 로직 검증
        assertEquals(new BigDecimal("35000"), order.getTotalPrice());
    }

    @Test
    @DisplayName("주문 객체 생성 실패 - 판매 중이 아니면 예외 발생으로 주문 객체 생성 불가")
    void createOrder_shouldThrowException_whenProductNotOnSale() {
        // given
        // 주문 물품 목록
        OrderItem discontinuedOrderItem = discontinuedOrderItem(); // 판매중단된 상품 옵션 주문
        // 주문자
        Member member = member();

        // when
        // then
        ProductException e = assertThrows(ProductException.class, () ->
                Order.createOrder(List.of(discontinuedOrderItem), member));
        assertEquals(PRODUCT_NOT_ON_SALE, e.getErrorCode());
    }

    /* ---------------------------
        주문상태 전이 성공 Test
       --------------------------- */

    @Test
    @DisplayName("CREATED -> PAID 성공 - 승인완료된 결제 전달 시 주문상태는 PAID로 변경")
    void paid_shouldChangeToPaid_whenApprovedPaymentProvided() {
        // given
        Order order = order();
        Payment payment = approvedPayment(order);

        // when
        order.paid(payment);

        // then
        assertEquals(PAID, order.getOrderStatus());
    }

    /* ---------------------------
        주문상태 전이 실패 Test
       --------------------------- */

    @Test
    @DisplayName("CREATED -> PAID 실패 - 결제 승인완료 상태가 아니면 주문 결제완료 불가")
    void paid_shouldThrowException_whenPaymentIsNotApproved() {
        // given
        Order order = order();
        Payment notApprovedPayment = payment(order); // 결제상태는 READY

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                order.paid(notApprovedPayment));
        assertEquals(PAYMENT_NOT_APPROVED, e.getErrorCode());
    }

    @Test
    @DisplayName("CREATED -> PAID 실패 - 이미 주문 결제완료 시 주문 결제완료 실패")
    void paid_shouldThrowException_whenOrderIsAlreadyPaid() {
        // given
        Order order = order();
        Payment payment = approvedPayment(order);

        // 이미 다른 승인된 결제로 주문을 결제 완료된 상태로 변경
        Payment alreadyApprovedPayment = approvedPayment(order);
        order.paid(alreadyApprovedPayment);

        // when
        // then
        OrderException e = assertThrows(OrderException.class, () ->
                order.paid(payment));
        assertEquals(ORDER_STATUS_NOT_CREATED, e.getErrorCode());
    }
}