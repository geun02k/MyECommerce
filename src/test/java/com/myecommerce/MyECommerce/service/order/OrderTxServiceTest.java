package com.myecommerce.MyECommerce.service.order;

import com.myecommerce.MyECommerce.dto.payment.PgApprovalResult;
import com.myecommerce.MyECommerce.dto.payment.PgResult;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.order.OrderItem;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.exception.PaymentException;
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import com.myecommerce.MyECommerce.service.payment.PaymentTxService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.ORDER_STATUS_NOT_CREATED;
import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.PAYMENT_NOT_FOUND;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;
import static com.myecommerce.MyECommerce.type.OrderStatusType.PAID;
import static com.myecommerce.MyECommerce.type.PaymentMethodType.*;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.APPROVED;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.FAILED;
import static com.myecommerce.MyECommerce.type.PgProviderType.MOCK_PG;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTxServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    PaymentTxService paymentTxService;

    @InjectMocks
    OrderTxService orderTxService;

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 고객권한 회원 */
    Member customer() {
        return Member.builder()
                .userId("customer")
                .roles(List.of(MemberAuthority.builder()
                        .authority(CUSTOMER)
                        .build()))
                .build();
    }

    /** 상품 */
    Product product() {
        return Product.builder()
                .id(3L)
                .code("productCode")
                .seller(1L)
                .saleStatus(ON_SALE)
                .build();
    }

    /** 등록된 상품 옵션 */
    ProductOption productOption() {
        return ProductOption.builder()
                .optionCode("optionCode")
                .quantity(100)
                .price(new BigDecimal("10000"))
                .product(product())
                .build();
    }

    /** 주문 객체 생성 */
    Order createdOrder(Long orderId) {
        Member customer = customer();
        ProductOption productOption = productOption();
        OrderItem orderItem = OrderItem.createOrderItem(productOption, 1);

        Order order = Order.createOrder(List.of(orderItem), customer);
        ReflectionTestUtils.setField(order, "id", orderId);
        return order;
    }

    /** PG 요청에 대한 응답 */
    PgResult pgResult() {
        return PgResult.builder()
                .pgTransactionId("pgTransactionId")
                .build();
    }

    /** PG 요청에 대한 승인 응답 */
    PgApprovalResult pgApprovalResult() {
        return PgApprovalResult.builder()
                .pgTransactionId("pgTransactionId")
                .approvalStatus(APPROVED)
                .paidAmount(new BigDecimal(10000))
                .build();
    }

    /** PG 요청에 대한 실패 응답 */
    PgApprovalResult pgFailResult() {
        return PgApprovalResult.builder()
                .pgTransactionId("pgTransactionId")
                .approvalStatus(FAILED)
                .build();
    }

    /** 승인된 결제 객체 생성 */
    Payment approvedPayment(Long paymentId, Order order) {
        // 결제 생성
        Payment payment = Payment.createPayment(order, CARD, MOCK_PG);
        ReflectionTestUtils.setField(payment, "id", paymentId);

        // PG 결제요청
        PgResult pgRequestResult = pgResult();
        payment.requestPgPayment(pgRequestResult); // 결제상태 = IN_PROGRESS

        // PG 결제승인
        PgApprovalResult pgApprovalResult = pgApprovalResult();
        payment.approve(pgApprovalResult); // 결제상태 = APPROVED

        return payment;
    }

    /** 미승인 결제 객체 생성 */
    Payment failedPayment(Long paymentId, Order order) {
        // 결제 생성
        Payment payment = Payment.createPayment(order, CARD, MOCK_PG);
        ReflectionTestUtils.setField(payment, "id", paymentId);

        // PG 결제요청
        PgResult pgRequestResult = pgResult();
        payment.requestPgPayment(pgRequestResult); // 결제상태 = IN_PROGRESS

        // PG 결제승인
        PgApprovalResult pgFailResult = pgFailResult();
        payment.fail(pgFailResult); // 결제상태 = FAILED

        return payment;
    }

    /* ----------------------------------
        주문 결제처리 정상 시나리오 Tests
       ---------------------------------- */

    // updatePaidOrderStatus() 검증
    // paymentTxService.findPaymentById()가 APPROVED 상태인 엔티티를 반환하게 stub
    @Test
    @DisplayName("주문 결제 성공 - 결제가 승인(APPROVED)된 경우 주문 결제상태(PAID)로 변경")
    void updatePaidOrderStatus_shouldUpdateOrderToPaid_whenPaymentIsApproved() {
        // given
        Long orderId = 5L;
        Long paymentId = 10L;

        Order createdOrder = createdOrder(orderId);
        Payment approvedPayment = approvedPayment(paymentId, createdOrder);

        // 결제 조회
        given(paymentTxService.findPaymentByIdWithOrder(paymentId))
                .willReturn(approvedPayment);
        // 주문 조회
        given(orderRepository.findByIdAndOrderStatus(orderId, CREATED))
                .willReturn(Optional.of(createdOrder));

        // when
        orderTxService.updatePaidOrderStatus(orderId, paymentId);

        // then
        // 의존성 호출 검증
        verify(paymentTxService).findPaymentByIdWithOrder(paymentId);
        verify(orderRepository).findByIdAndOrderStatus(orderId, CREATED);

        // 주문 상태변경 검증
        assertEquals(PAID, createdOrder.getOrderStatus());
    }

    /* ----------------------------------
        주문 결제처리 실패 시나리오 Tests
       ---------------------------------- */

    @Test
    @DisplayName("주문 결제 실패 - 결제 ID에 대한 결제 미존재 시 예외발생")
    void updatePaidOrderStatus_shouldThrowException_whenNotExistsPayment() {
        // given
        Long invalidPaymentId = 100L;

        // paymentId로 결제 조회
        doThrow(new PaymentException(PAYMENT_NOT_FOUND))
                .when(paymentTxService).findPaymentByIdWithOrder(any());

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                orderTxService.updatePaidOrderStatus(null, invalidPaymentId));
        assertEquals(PAYMENT_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("주문 결제 실패 - 결제가 승인(APPROVED)되지 않은 경우 주문상태 미변경")
    void updatePaidOrderStatus_shouldNotUpdateOrder_whenPaymentIsNotApproved() {
        // given
        Long orderId = 5L;
        Long paymentId = 10L;

        Order createdOrder = createdOrder(orderId);
        Payment failedPayment = failedPayment(paymentId, createdOrder); // 승인 실패된 결제

        // 결제 조회
        given(paymentTxService.findPaymentByIdWithOrder(paymentId))
                .willReturn(failedPayment);

        // when
        orderTxService.updatePaidOrderStatus(orderId, paymentId);

        // then
        // 의존성 호출 검증
        verify(paymentTxService).findPaymentByIdWithOrder(paymentId);
        verify(orderRepository, never()).findByIdAndOrderStatus(any(), any());

        // 주문 상태 미변경 검증
        assertEquals(CREATED, createdOrder.getOrderStatus());
    }

    @Test
    @DisplayName("주문 결제 실패 - 주문 ID에 대한 주문 미존재 시 예외발생")
    void updatePaidOrderStatus_shouldThrowException_whenNotExistsOrder() {
        // given
        Long invalidOrderId = 100L;
        Long paymentId = 10L;

        Order createdOrder = createdOrder(5L); // 조회 id와 다른 주문
        Payment approvedPayment = approvedPayment(paymentId, createdOrder);

        // 결제 조회
        given(paymentTxService.findPaymentByIdWithOrder(paymentId))
                .willReturn(approvedPayment);
        // 주문 조회 불가
        given(orderRepository.findByIdAndOrderStatus(invalidOrderId, CREATED))
                .willReturn(Optional.empty());

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                orderTxService.updatePaidOrderStatus(invalidOrderId, paymentId));
        assertEquals(ORDER_STATUS_NOT_CREATED, e.getErrorCode());
    }

}