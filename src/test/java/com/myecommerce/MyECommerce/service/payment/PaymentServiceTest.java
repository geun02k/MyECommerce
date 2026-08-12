package com.myecommerce.MyECommerce.service.payment;

import com.myecommerce.MyECommerce.dto.payment.*;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.order.OrderItem;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.exception.PaymentException;
import com.myecommerce.MyECommerce.repository.payment.PaymentRepository;
import com.myecommerce.MyECommerce.service.order.OrderTxService;
import com.myecommerce.MyECommerce.type.PaymentMethodType;
import com.myecommerce.MyECommerce.type.PgProviderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.ORDER_STATUS_NOT_CREATED;
import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.PG_TRANSACTION_ID_NOT_EXISTS;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;
import static com.myecommerce.MyECommerce.type.OrderStatusType.PAID;
import static com.myecommerce.MyECommerce.type.PaymentMethodType.CARD;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.*;
import static com.myecommerce.MyECommerce.type.PgProviderType.MOCK_PG;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    // TODO: paymentTxService에만 의존하도록 고려하기
    @Mock
    PaymentRepository paymentRepository;

    @Mock
    PgClient pgClient;

    @Mock
    PaymentTxService paymentTxService;
    @Mock
    OrderTxService orderTxService;

    @InjectMocks
    PaymentService paymentService;

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

    /** 등록된 상품 옵션 */
    ProductOption productOption() {
        Product registeredProduct = Product.builder()
                .id(5L)
                .code("productCode")
                .seller(1L)
                .saleStatus(ON_SALE)
                .build();

        return ProductOption.builder()
                .optionCode("optionCode")
                .quantity(100)
                .price(new BigDecimal("10000"))
                .product(registeredProduct)
                .build();
    }

    /** 등록된 주문 */
    Order order(Member member) {
        ProductOption productOption = productOption();
        OrderItem orderItem = OrderItem.createOrderItem(productOption, 1);

        Order order = Order.createOrder(List.of(orderItem), member);
        ReflectionTestUtils.setField(order, "id", 1L);
        return order;
    }
    Order order() {
        ProductOption productOption = productOption();
        OrderItem orderItem = OrderItem.createOrderItem(productOption, 1);

        Order order = Order.createOrder(List.of(orderItem), customer());
        ReflectionTestUtils.setField(order, "id", 1L);
        return order;
    }

    /** 저장된 결제 (id 추가) */
    Payment savedPayment(Order order,
                         PaymentMethodType requestPaymentMethod,
                         PgProviderType pgProvider) {
        Payment savedPayment = Payment.createPayment(
                order, requestPaymentMethod, pgProvider);
        ReflectionTestUtils.setField(savedPayment, "id", 10L);

        return savedPayment;
    }

    /** PG 요청한 결제 */
    Payment inProgressPayment(Order order) {
        PgResult pgRequestResult = pgResult();

        Payment payment = savedPayment(order, CARD, MOCK_PG);
        payment.requestPgPayment(pgRequestResult); // 결제상태 = IN_PROGRESS

        return payment;
    }

    /** PG 승인된 결제 */
    Payment approvedPayment(Order order) {
        PgResult pgRequestResult = pgResult();
        PgApprovalResult pgApprovalResult = pgApprovalResult();

        Payment payment = savedPayment(order, CARD, MOCK_PG);
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

    /** PG 요청에 대한 응답 */
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

    void givenRequestPayment (Payment savedPayment,
                              PgApiResponse<PgResult> pgApiResponse) {
        given(pgClient.requestPayment(savedPayment)).willReturn(pgApiResponse);
    }

    /** 결제 도메인에 PG 요청 결과 반영 (트랜잭션ID, 결제상태 셋팅) */
    void givenUpdatePaymentToInProgress(Payment savedPayment, PgResult pgResult) {
        Payment updatedPayment = Payment.createPayment(
                savedPayment.getOrder(),
                savedPayment.getPaymentMethod(),
                savedPayment.getPgProvider());
        ReflectionTestUtils.setField(updatedPayment, "id", savedPayment.getId());
        updatedPayment.requestPgPayment(pgResult);

        given(paymentTxService.updatePaymentToInProgress(any(), any()))
                .willReturn(updatedPayment);
    }

    /* ---------------------------
        결제시작 정상 시나리오 Tests
       --------------------------- */

    @Test
    @DisplayName("결제시작 정상 시나리오 - 정상 결제 요청 시 결제상태 IN_PROGRESS 응답")
    void startPayment_shouldInProgressResponse_whenValidRequest() {
        // given
        Long requestOrderId = 1L;
        PaymentMethodType requestPaymentMethod = PaymentMethodType.CARD;
        PgProviderType pgProvider = PgProviderType.MOCK_PG;
        // 요청 결제 정보
        RequestPaymentDto request = RequestPaymentDto.builder()
                .orderId(requestOrderId)
                .paymentMethod(requestPaymentMethod)
                .build();
        // 결제 요청 고객
        Member member = customer();

        // 신규 저장된 결제
        Payment savedPayment = Payment.createPayment(
                order(member), requestPaymentMethod, pgProvider);
        ReflectionTestUtils.setField(savedPayment, "id", 10L);
        // PG 결제대행사에 결제 요청
        PgResult pgResult = PgResult.builder()
                .pgTransactionId("pgTransactionId")
                .redirectUrl("redirectUrl")
                .build();
        PgApiResponse<PgResult> pgApiResponse = PgApiResponse.success(pgResult);
        // 다른 트랜잭션에서 조회되어 수정된 결제
        Payment updatedPayment = Payment.createPayment(
                savedPayment.getOrder(),
                savedPayment.getPaymentMethod(),
                savedPayment.getPgProvider());
        ReflectionTestUtils.setField(updatedPayment, "id", savedPayment.getId());
        updatedPayment.requestPgPayment(pgResult); // READY -> IN_PROGRESS로 변경

        // 정책 검증 및 결제 Entity 반환
        given(paymentTxService.createPayment(any(), any()))
                .willReturn(savedPayment);
        // PG 결제대행사에 결제 요청
        given(pgClient.requestPayment(any())).willReturn(pgApiResponse);
        // 결제 도메인에 PG 요청 결과 반영 (결제번호, 결제상태 셋팅)
        given(paymentTxService.updatePaymentToInProgress(any(), any()))
                .willReturn(updatedPayment);

        // when
        ResponsePaymentDto response =
                paymentService.startPayment(request, member);

        // then
        // PG 요청 후 응답 검증 (API 사용자 입장에서 반드시 필요한 결과만 검증)
        assertEquals(requestOrderId, response.getOrderId());
        assertEquals(IN_PROGRESS, response.getPaymentStatus()); // 결제 상태 PG 요청으로 변경
        assertEquals(pgResult.getRedirectUrl(), response.getRedirectUrl());
    }

    /* ----------------------------
        결제시작 책임 행위 검증 Tests
       ---------------------------- */

    // TODO: 호출순서검증 별도 분리할 것
    @Test
    @DisplayName("결제시작 책임 - 결제 객체 존재 시 PG 결제 요청 검증") // 정상 시나리오에서 응답 검증을 통해 확인가능하지만 행위 검증을 위해 테스트 코드 작성
    void startPayment_shouldRequestPgPayment_whenExistsPayment() {
        // given
        Long requestOrderId = 1L;
        PaymentMethodType requestPaymentMethod = CARD;
        String pgTransactionId = "pgTransactionId";
        String redirectUrl = "redirectUrl";
        // 요청 결제 정보
        RequestPaymentDto request = RequestPaymentDto.builder()
                .orderId(requestOrderId)
                .paymentMethod(requestPaymentMethod)
                .build();
        // 결제 요청 고객
        Member member = customer();

        // PG 결제대행사
        PgProviderType pgProvider = PgProviderType.MOCK_PG;
        // 요청 결제에 대한 주문
        Order order = order(member);
        // 저장된 신규 결제
        Payment savedPayment =Payment.createPayment(
                order, requestPaymentMethod, pgProvider);
        ReflectionTestUtils.setField(savedPayment, "id", 10L);
        // PG 결제대행사에 결제 요청
        PgResult pgResult = PgResult.builder()
                .pgTransactionId(pgTransactionId)
                .redirectUrl(redirectUrl)
                .build();
        PgApiResponse<PgResult> pgApiResponse = PgApiResponse.success(pgResult);

        // 정책 검증 및 결제 Entity 반환
        given(paymentTxService.createPayment(any(), any()))
                .willReturn(savedPayment);
        // PG 결제대행사에 결제 요청
        given(pgClient.requestPayment(argThat(p ->
                p.getOrder().equals(order)
                        && p.getPaymentMethod() == requestPaymentMethod
                        && p.getPgProvider() == pgProvider
                        && p.getPaymentStatus() == READY
        ))).willReturn(pgApiResponse);
        // 결제 도메인에 PG 요청 결과 반영 (트랜잭션ID, 결제상태 셋팅)
        givenUpdatePaymentToInProgress(savedPayment, pgResult);

        // when
        ResponsePaymentDto response =
                paymentService.startPayment(request, member);

        // then
        // 호출 순서 검증
        InOrder inOrder = inOrder(paymentTxService, pgClient);
        inOrder.verify(paymentTxService).createPayment(any(), any());
        inOrder.verify(pgClient).requestPayment(any());
        inOrder.verify(paymentTxService).updatePaymentToInProgress(any(), any());
    }

    // 결제시작 책임 - 응답값 검증
    // 응답 로직이 단순하고 정상 흐름에서 핵심 응답 검증했기에 불필요
    // 같은 책임 두 번 테스트하면 유지보수 비용만 증가

    /* ---------------------------
        결제생성 PG 응답 분기 Tests
       --------------------------- */

    // PG 요청 응답 분기 - PG 요청 성공 응답 시 결제상태 IN_PROGRESS로 변경
    // -> 정상 시나리오에서 간접 검증으로 제외

    @Test
    @DisplayName("PG 요청 응답 분기 - PG 요청 실패 응답 시 결제상태 READY 유지")
    void startPayment_shouldKeepReadyStatus_whenPgResponseFailed() {
        Long requestOrderId = 1L;
        PaymentMethodType requestPaymentMethod = CARD;
        PgProviderType pgProvider = PgProviderType.MOCK_PG;

        // 요청 결제 정보
        RequestPaymentDto request = RequestPaymentDto.builder()
                .orderId(requestOrderId)
                .paymentMethod(requestPaymentMethod)
                .build();
        // 결제 요청 고객
        Member member = customer();

        // 요청 결제에 대한 주문 (PG 결제 실패를 위해 주문금액 0원)
        Order invalidOrder = order(member);
        ReflectionTestUtils.setField(invalidOrder, "totalPrice", BigDecimal.ZERO);
        // 저장된 신규 결제
        Payment savedPayment =
                savedPayment(invalidOrder, requestPaymentMethod, pgProvider);
        // PG 결제 요청 응답 (결제 실패 응답)
        PgApiResponse<PgResult> pgApiResponse = PgApiResponse.fail(
                "INVALID_AMOUNT", "결제 금액이 올바르지 않습니다.");

        // 정책 검증 및 결제 반환
        given(paymentTxService.createPayment(any(), any())).willReturn(savedPayment);
        // PG 결제대행사에 결제 요청
        givenRequestPayment(savedPayment, pgApiResponse);

        // when
        ResponsePaymentDto response = paymentService.startPayment(request, member);

        // then
        // 결제 도메인에 PG 요청 결과 반영 여부 검증
        verify(paymentTxService, never()).updatePaymentToInProgress(any(), any());
        // PG 결제 실패 응답 검증
        assertEquals(requestOrderId, response.getOrderId());
        assertEquals(savedPayment.getId(), response.getPaymentId());
        assertEquals(savedPayment.getPaymentStatus(), response.getPaymentStatus());
        assertEquals(pgApiResponse.getError().getCode(), response.getFailCode());
        assertNotNull(response.getFailMessage());
        assertEquals(READY, response.getPaymentStatus());
    }

    // PG 요청 응답 분기 - PG 요청 실패 시 예외발생
    // -> Service 책임 아니므로 제외

    /* ----------------------------------
        PG 결제승인 웹훅 정상 시나리오 Tests
       ---------------------------------- */

    @Test
    @DisplayName("PG 결제승인 웹훅 성공 - 승인가능한 결제 존재 시 Payment 승인 처리 및 Order 완료 처리 요청")
    void handlePgWebHook_shouldUpdatePaymentAndOrder_whenValidPgApprovalResult() {
        // given
        String pgTransactionId = "pgTransactionId";
        // PG 결제승인 요청에 대한 웹훅 응답
        PgApprovalResult pgApprovalResult = PgApprovalResult.builder()
                .pgTransactionId(pgTransactionId)
                .approvalStatus(APPROVED) // PG 결제 승인
                .paidAmount(new BigDecimal(10000))
                .build();

        // 결제 대한 주문
        Order order = order();
        // 주문에 대한 PG 요청된 결제
        Payment payment = inProgressPayment(order);

        // PG 트랜잭션 ID와 일치하는 결제 조회
        given(paymentRepository.findByPgTransactionIdWithOrder(any()))
                .willReturn(Optional.of(payment));
        // 결제 승인 결과 반영
        given(paymentTxService.updatePgApprovalResult(any(),any()))
                .willReturn(1);

        // when
        paymentService.handlePgWebHook(pgApprovalResult);

        // then
        // payment 조회, 결제 및 주문 상태 변경을 위한 메서드 호출 검증
        verify(paymentRepository).findByPgTransactionIdWithOrder(eq(pgTransactionId));
        verify(paymentTxService).updatePgApprovalResult(eq(payment.getId()), eq(pgApprovalResult));
        verify(orderTxService).updatePaidOrderStatus(eq(order.getId()), eq(payment.getId()));
    }

    @Test
    @DisplayName("PG 결제승인 웹훅 성공 - 트랜잭션ID에 맞는 결제 존재 시 결제 실패")
    void handlePgWebHook_shouldFailPayment_whenExistsTransactionId() {
        // given
        String pgTransactionId = "pgTransactionId";
        // PG 결제승인 요청에 대한 웹훅 응답
        PgApprovalResult pgApprovalResult = PgApprovalResult.builder()
                .pgTransactionId(pgTransactionId)
                .approvalStatus(FAILED) // PG 결제 실패
                .build();

        // 결제 대한 주문
        Order order = order();
        // 주문에 대한 PG 요청된 결제
        Payment payment = inProgressPayment(order);

        // PG 트랜잭션 ID와 일치하는 결제 조회
        given(paymentRepository.findByPgTransactionIdWithOrder(any()))
                .willReturn(Optional.of(payment));

        // when
        paymentService.handlePgWebHook(pgApprovalResult);

        // then
        // 결제 조회 필수 검증
        verify(paymentRepository).findByPgTransactionIdWithOrder(eq(pgTransactionId));
        verify(paymentTxService).updatePgApprovalResult(eq(payment.getId()), eq(pgApprovalResult));
        verify(orderTxService, never()).updatePaidOrderStatus(any(),any());
    }

    /* ----------------------------------
        PG 결제승인 웹훅 멱등성 보장 Tests
       ---------------------------------- */

    @Test
    @DisplayName("PG 결제승인 웹훅 멱등성 보장 - 종결된 결제에 대해 승인완료 재요청 시 웹훅 무시")
    void handlePgWebHook_shouldNotChange_whenReRequestSamePaymentStatus() {
        // given
        String pgTransactionId = "pgTransactionId";
        // PG 결제승인 요청에 대한 웹훅 응답
        PgApprovalResult pgApprovalResult = PgApprovalResult.builder()
                .pgTransactionId(pgTransactionId)
                .approvalStatus(APPROVED) // PG 승인완료
                .build();

        // 결제 대한 주문
        Order order = order();
        // 주문에 대한 PG 요청된 결제
        Payment payment = approvedPayment(order);

        // PG 트랜잭션 ID와 일치하는 결제 조회
        given(paymentRepository.findByPgTransactionIdWithOrder(any()))
                .willReturn(Optional.of(payment));

        // when
        paymentService.handlePgWebHook(pgApprovalResult);

        // then
        verify(paymentTxService, never()).updatePgApprovalResult(any(),any());
        verify(orderTxService, never()).updatePaidOrderStatus(any(),any());
    }

    @Test
    @DisplayName("PG 결제승인 웹훅 멱등성 보장 - 종결된 결제에 대한 다른 승인상태로 재요청 시 웹훅 무시로 상태역전 방지")
    void handlePgWebHook_shouldNotChange_whenReRequestDifferentPaymentStatus() {
        // given
        String pgTransactionId = "pgTransactionId";
        // PG 결제승인 요청에 대한 웹훅 응답
        PgApprovalResult pgApprovalResult = PgApprovalResult.builder()
                .pgTransactionId(pgTransactionId)
                .approvalStatus(FAILED) // PG 승인실패
                .build();

        // 결제 대한 주문
        Order order = order();
        // 주문에 대한 PG 요청된 결제
        Payment payment = approvedPayment(order);

        // PG 트랜잭션 ID와 일치하는 결제 조회
        given(paymentRepository.findByPgTransactionIdWithOrder(any()))
                .willReturn(Optional.of(payment));

        // when
        paymentService.handlePgWebHook(pgApprovalResult);

        // then
        // 상태값 미변경 검증 (멱등성 검증)
        verify(paymentTxService, never()).updatePgApprovalResult(any(),any());
        verify(orderTxService, never()).updatePaidOrderStatus(any(),any());
    }

    /* ----------------------------------
        PG 결제승인 웹훅 책임 행위 검증 Tests
       ---------------------------------- */

    // PG 결제승인 웹훅 책임 - transactionId에 대한 결제 정상 조회 검증
    // 정상 시나리오 등에서 검증 완료되어 불필요

    // PG 결제승인 웹훅 책임 - 주문 상태변경 책임 검증
    // 정상 시나리오에서의 일부 값을 다시 검증하므로 불필요

    /* ----------------------------------
        PG 결제승인 웹훅 실패 Tests
       ---------------------------------- */

    @Test
    @DisplayName("PG 결제승인 웹훅 실패 - 트랜잭션 ID에 대한 결제 미존재 시 예외발생")
    void handlePgWebHook_shouldThrowException_whenNotExistsTransactionId() {
        // given
        String pgTransactionId = "pgTransactionId";
        // PG 결제승인 요청에 대한 웹훅 응답
        PgApprovalResult pgApprovalResult = PgApprovalResult.builder()
                .pgTransactionId(pgTransactionId)
                .build();

        // PG 트랜잭션 ID와 일치하는 결제 조회
        given(paymentRepository.findByPgTransactionIdWithOrder(any()))
                .willReturn(Optional.empty());

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                paymentService.handlePgWebHook(pgApprovalResult));
        assertEquals(PG_TRANSACTION_ID_NOT_EXISTS, e.getErrorCode());
    }

    @Test
    @DisplayName("PG 결제승인 웹훅 성공 - Order 처리 실패 시에도 Payment 승인은 유지되고 예외를 전파하지 않음")
    void handlePgWebHook_shouldNotThrowException_whenAlreadyPaidOrder() {
        String pgTransactionId = "pgTransactionId";
        BigDecimal paidAmount = new BigDecimal(10000);
        // PG 결제승인 요청에 대한 웹훅 응답
        PgApprovalResult pgApprovalResult = PgApprovalResult.builder()
                .pgTransactionId(pgTransactionId)
                .approvalStatus(APPROVED) // PG 결제 승인
                .paidAmount(paidAmount)
                .build();

        // 결제 대한 주문
        Order order = order();
        // 주문에 대한 PG 요청된 결제
        Payment payment = inProgressPayment(order);

        // PG 트랜잭션 ID와 일치하는 결제 조회
        given(paymentRepository.findByPgTransactionIdWithOrder(any()))
                .willReturn(Optional.of(payment));
        // 결제 승인 결과 반영
        given(paymentTxService.updatePgApprovalResult(any(),any()))
                .willReturn(1);
        // 주문 결제완료 처리 실패
        willThrow(new PaymentException(ORDER_STATUS_NOT_CREATED))
                .given(orderTxService)
                .updatePaidOrderStatus(any(), any());

        // when
        // then
        // 예외를 밖으로 전파하지 않음을 검증
        assertDoesNotThrow(() -> paymentService.handlePgWebHook(pgApprovalResult));
    }

    // 금액 검증, TransactionId 검증, 상태 전이 검증 등의 경우
    // PaymentTest에 이미 존재하므로, 이미 검증된 책임에 대해서는 검증 제외

}