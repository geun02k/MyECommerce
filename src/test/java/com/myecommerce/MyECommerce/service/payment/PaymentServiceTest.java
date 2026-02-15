package com.myecommerce.MyECommerce.service.payment;

import com.myecommerce.MyECommerce.dto.payment.PgApiResponse;
import com.myecommerce.MyECommerce.dto.payment.PgResult;
import com.myecommerce.MyECommerce.dto.payment.RequestPaymentDto;
import com.myecommerce.MyECommerce.dto.payment.ResponsePaymentDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.order.OrderItem;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.exception.PaymentException;
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import com.myecommerce.MyECommerce.repository.payment.PaymentRepository;
import com.myecommerce.MyECommerce.type.OrderStatusType;
import com.myecommerce.MyECommerce.type.PaymentMethodType;
import com.myecommerce.MyECommerce.type.PgProviderType;
import io.jsonwebtoken.lang.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.*;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.PaymentMethodType.CARD;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.IN_PROGRESS;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.READY;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    PgClient pgClient;

    @Mock
    PaymentPolicy paymentPolicy;

    @Mock
    OrderRepository orderRepository;
    @Mock
    PaymentRepository paymentRepository;

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

    /** 저장된 결제 (id 추가) */
    Payment savedPayment(Order order,
                         PaymentMethodType requestPaymentMethod,
                         PgProviderType pgProvider) {
        Payment savedPayment = Payment.createPayment(
                order, requestPaymentMethod, pgProvider);
        ReflectionTestUtils.setField(savedPayment, "id", 10L);

        return savedPayment;
    }

    /** PG 요청 성공응답 */
    PgApiResponse<PgResult> pgApiResponseOfRequest() {
        // PG 결제대행사에 결제 요청
        PgResult pgResult = PgResult.builder()
                .pgTransactionId("pgTransactionId")
                .redirectUrl("redirectUrl")
                .build();
        return PgApiResponse.success(pgResult);
    }

    /** 고객 권한 */
    MemberAuthority customerRole() {
        return MemberAuthority.builder()
                .authority(CUSTOMER)
                .build();
    }

    /* ------------------
        Helper Method
       ------------------ */

    void givenPgProvider(PgProviderType pgProvider) {
        given(pgClient.getProvider()).willReturn(pgProvider);
    }

    void givenOrderOfPayment(Order order) {
        given(orderRepository.findLockedByIdAndOrderStatus(any(), any()))
                .willReturn(Optional.of(order));
    }

    void givenEmptyPayments() {
        given(paymentRepository.findLockedAllByOrderId(any()))
                .willReturn(Collections.emptyList());
    }

    void givenPaymentSaved(Payment savedPayment) {
        given(paymentRepository.save(any())).willReturn(savedPayment);
    }

    void givenRequestPayment (Payment savedPayment,
                              PgApiResponse<PgResult> pgApiResponse) {
        given(pgClient.requestPayment(savedPayment)).willReturn(pgApiResponse);
    }

    /** 실행가능상태 준비 - 신규 payment 저장 및 PG 결제 요청 mocking */
    void givenPaymentPersistSucceeds() {
        // 신규 결제 생성 및 저장
        given(paymentRepository.save(any()))
                .willAnswer(invocationOnMock ->
                        invocationOnMock.getArgument(0));

        // PG 결제대행사에 결제 요청
        PgResult pgResult = PgResult.builder()
                .pgTransactionId("pgTransactionId")
                .redirectUrl("redirectUrl")
                .build();
        PgApiResponse<PgResult> pgApiResponse = PgApiResponse.success(pgResult);
        // PG 결제대행사에 결제 요청
        given(pgClient.requestPayment(any())).willReturn(pgApiResponse);
    }

    /** 실행가능상태 준비 - PG 결제 요청 mocking */
    void givenPgRequestSucceeds() {
        // PG 결제대행사에 결제 요청
        PgResult pgResult = PgResult.builder()
                .pgTransactionId("pgTransactionId")
                .redirectUrl("redirectUrl")
                .build();
        PgApiResponse<PgResult> pgApiResponse = PgApiResponse.success(pgResult);
        // PG 결제대행사에 결제 요청
        given(pgClient.requestPayment(any())).willReturn(pgApiResponse);
    }

    /* -------------------------
        결제생성 정상 시나리오 Tests
       ------------------------- */

    @Test
    @DisplayName("결제생성 정상 시나리오 - 정상 결제 요청 시 결제상태 IN_PROGRESS 응답")
    void startPayment_shouldInProgressResponse_whenValidRequest() {
        // given
        Long requestOrderId = 1L;
        String pgTransactionId = "pgTransactionId";
        String redirectUrl = "redirectUrl";
        // 요청 결제 정보
        RequestPaymentDto request = RequestPaymentDto.builder()
                .orderId(requestOrderId)
                .paymentMethod(CARD)
                .build();
        // 결제 요청 고객
        Member member = customer();

        // PG 결제대행사
        PgProviderType pgProvider = PgProviderType.MOCK_PG;
        // 요청 결제에 대한 주문
        Order order = order(member);
        // PG 결제대행사에 결제 요청
        PgResult pgResult = PgResult.builder()
                .pgTransactionId(pgTransactionId)
                .redirectUrl(redirectUrl)
                .build();
        PgApiResponse<PgResult> pgApiResponse = PgApiResponse.success(pgResult);

        // PG 결제대행사 반환
        given(pgClient.getProvider()).willReturn(pgProvider);
        // 요청 결제에 대한 주문 조회
        given(orderRepository.findLockedByIdAndOrderStatus(any(), any()))
                .willReturn(Optional.of(order));
        // 주문에 대한 기존 결제내역 미존재
        given(paymentRepository.findLockedAllByOrderId(any()))
                .willReturn(Collections.emptyList());
        // 신규 결제 생성 및 저장
        given(paymentRepository.save(any()))
                .willAnswer(invocationOnMock ->
                        invocationOnMock.getArgument(0));
        // PG 결제대행사에 결제 요청
        given(pgClient.requestPayment(any())).willReturn(pgApiResponse);

        // when
        ResponsePaymentDto response =
                paymentService.startPayment(request, member);

        // then
        // PG 요청 후 응답 검증 (API 사용자 입장에서 반드시 필요한 결과만 검증)
        assertEquals(requestOrderId, response.getOrderId());
        assertEquals(IN_PROGRESS, response.getPaymentStatus()); // 결제 상태 PG 요청으로 변경
        assertEquals(pgResult.getRedirectUrl(), response.getRedirectUrl());
    }

    @Test
    @DisplayName("결제생성 정상 시나리오 - 기존 결제내역 중 동일 결제방식, 결제사에 대한 결제 존재 시 재사용")
    void startPayment_shouldReUsePayment_whenAlreadyExistsPayment() {
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

        // 요청 결제에 대한 주문
        Order order = order(member);
        // 기존 결제 내역
        Payment existingPayment = Payment.createPayment(
                order, requestPaymentMethod, pgProvider);
        ReflectionTestUtils.setField(existingPayment, "id", 10L);

        givenPgProvider(pgProvider); // PG 결제대행사 반환
        givenOrderOfPayment(order); // 요청 결제에 대한 주문 조회
        // 주문에 대한 기존 결제내역 미존재
        given(paymentRepository.findLockedAllByOrderId(any()))
                .willReturn(List.of(existingPayment));
        // 기존 PG 결제 요청 가능 여부 반환
        given(paymentPolicy.isPaymentAvailablePgRequestAboutRequest(any(), any(), any()))
                .willReturn(true);
        // PG 결제대행사에 결제 요청
        given(pgClient.requestPayment(any())).willReturn(pgApiResponseOfRequest());

        // when
        paymentService.startPayment(request, member);

        // then
        // 기존 결제 조회 여부 검증
        verify(paymentRepository).findLockedAllByOrderId(requestOrderId);
        // 기존 결제를 이용하므로, 신규 결제 저장되지 않음을 검증
        verify(paymentRepository, never()).save(any());
        // 기존 결제내역의 PG 요청 가능 여부 검증
        verify(paymentPolicy).isPaymentAvailablePgRequestAboutRequest(
                existingPayment, requestPaymentMethod, pgProvider);
        // 기존 결제내역의 PG 요청 여부 검증
        verify(pgClient).requestPayment(existingPayment);
        // 결제 재사용 로직으로 응답이 달라질 가능성은 없으므로, 응답 검증 제외
    }

    /* ----------------------------
        결제생성 책임 행위 검증 Tests
       ---------------------------- */

    @Test
    @DisplayName("결제생성 책임 - 결제 시작 시 정책 호출 검증")
    void startPayment_shouldCallPaymentPolicy_whenStartPayment() {
        // given
        Long requestOrderId = 1L;
        // 요청 결제 정보
        RequestPaymentDto request = RequestPaymentDto.builder()
                .orderId(requestOrderId)
                .paymentMethod(CARD)
                .build();
        // 결제 요청 고객
        Member member = customer();

        // PG 결제대행사
        PgProviderType pgProvider = PgProviderType.MOCK_PG;
        // 요청 결제에 대한 주문
        Order order = order(member);

        // 요청 결제에 대한 주문 조회
        given(orderRepository.findLockedByIdAndOrderStatus(
                requestOrderId, OrderStatusType.CREATED))
                .willReturn(Optional.of(order));
        // 주문에 대한 기존 결제내역 미존재
        given(paymentRepository.findLockedAllByOrderId(requestOrderId))
                .willReturn(Collections.emptyList());
        // 실행조건 mock
        givenPaymentPersistSucceeds();

        // when
        paymentService.startPayment(request, member);

        // then
        // 정책 실행여부 검증
        verify(paymentPolicy).preValidateCreate(eq(member));
        verify(paymentPolicy).validateCreate(
                argThat(List::isEmpty), eq(order), eq(member));
    }

    @Test
    @DisplayName("결제생성 책임 - 신규 결제 요청 시 결제 객체 생성 및 저장 검증")
    void startPayment_shouldCreateAndSavePayment_whenRequestNewPayment() {
        // given
        Long requestOrderId = 1L;
        PaymentMethodType requestPaymentMethod = CARD;
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

        // PG 결제대행사 반환
        given(pgClient.getProvider()).willReturn(pgProvider);
        // 요청 결제에 대한 주문 조회
        given(orderRepository.findLockedByIdAndOrderStatus(any(), any()))
                .willReturn(Optional.of(order));
        // 주문에 대한 기존 결제내역 미존재
        given(paymentRepository.findLockedAllByOrderId(any()))
                .willReturn(Collections.emptyList());
        // 신규 결제 생성 및 저장
        given(paymentRepository.save(any())) // stub은 any()로, 검증은 captor로 진행
                .willReturn(savedPayment);
        // 실행조건 mock
        givenPgRequestSucceeds();

        // when
        ResponsePaymentDto response = paymentService.startPayment(request, member);

        // then
        // 결제 저장여부 검증
        ArgumentCaptor<Payment> paymentArgCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentArgCaptor.capture());
        // Payment 저장 전, 신규 생성된 결제객체 검증
        Payment capturedPayment = paymentArgCaptor.getValue();
        assertEquals(order, capturedPayment.getOrder());
        assertEquals(request.getPaymentMethod(), capturedPayment.getPaymentMethod());
        assertEquals(pgProvider, capturedPayment.getPgProvider());
        assertNotNull(capturedPayment.getPaymentCode()); // paymentCode 규칙 검증은 Payment Entity에서 검증 (paymentCode 생성 규칙 바뀌면 Service 테스트가 깨지기 때문)
        assertEquals(READY, capturedPayment.getPaymentStatus()); // 결제 생성 상태
        // 응답 검증
        assertEquals(10L, response.getPaymentId());
    }

    @Test
    @DisplayName("결제생성 책임 - 결제 객체 존재 시 PG 결제 요청 검증") // 정상 시나리오에서 응답 검증을 통해 확인가능하지만 행위 검증을 위해 테스트 코드 작성
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

        // PG 결제대행사 반환
        given(pgClient.getProvider()).willReturn(pgProvider);
        // 요청 결제에 대한 주문 조회
        given(orderRepository.findLockedByIdAndOrderStatus(any(), any()))
                .willReturn(Optional.of(order));
        // 주문에 대한 기존 결제내역 미존재
        given(paymentRepository.findLockedAllByOrderId(any()))
                .willReturn(Collections.emptyList());
        // 신규 결제 생성 및 저장
        given(paymentRepository.save(any())).willReturn(savedPayment);
        // PG 결제대행사에 결제 요청
        given(pgClient.requestPayment(argThat(p ->
                p.getOrder().equals(order)
                        && p.getPaymentMethod() == requestPaymentMethod
                        && p.getPgProvider() == pgProvider
                        && p.getPaymentStatus() == READY
        ))).willReturn(pgApiResponse);

        // when
        ResponsePaymentDto response =
                paymentService.startPayment(request, member);

        // then
        // 호출 순서 검증
        InOrder inOrder = inOrder(paymentPolicy, paymentRepository, pgClient);
        inOrder.verify(paymentPolicy).preValidateCreate(member);
        inOrder.verify(paymentPolicy).validateCreate(any(), eq(order), eq(member));
        inOrder.verify(paymentRepository).save(any());
        inOrder.verify(pgClient).requestPayment(any());
        // PG 요청 후 응답 검증
        assertEquals(requestOrderId, response.getOrderId());
        assertEquals(pgResult.getRedirectUrl(), response.getRedirectUrl());
        assertEquals(IN_PROGRESS, response.getPaymentStatus()); // 결제 상태 PG 요청으로 변경
    }

    // 결제생성 책임 - 응답값 검증
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
        Payment savedPayment = savedPayment(invalidOrder, requestPaymentMethod, pgProvider);
        // PG 결제 요청 응답 (결제 실패 응답)
        PgApiResponse<PgResult> pgApiResponse = PgApiResponse.fail(
                "INVALID_AMOUNT", "결제 금액이 올바르지 않습니다.");

        givenPgProvider(pgProvider); // PG 결제대행사 반환
        givenOrderOfPayment(invalidOrder); // 요청 결제에 대한 주문 조회
        givenEmptyPayments(); // 주문에 대한 기존 결제내역 미존재
        givenPaymentSaved(savedPayment); // 신규 결제 생성 및 저장
        givenRequestPayment(savedPayment, pgApiResponse); // PG 결제대행사에 결제 요청

        // when
        ResponsePaymentDto response = paymentService.startPayment(request, member);

        // then
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

    /* ----------------------------
        결제생성 실패 Tests
       ---------------------------- */

    @Test
    @DisplayName("결제생성 실패 - 사전 정책 검증 실패 시 예외발생")
    void startPayment_shouldThrowException_whenPreValidate() {
        // given
        RequestPaymentDto request = RequestPaymentDto.builder().build();
        Member invalidMember = Member.builder()
                .roles(Collections.emptyList()) // 고객 권한 없음
                .build();

        // 사전 정책 실행 시 예외발생
        doThrow(new PaymentException(PAYMENT_CUSTOMER_ONLY))
                .when(paymentPolicy)
                .preValidateCreate(eq(invalidMember));

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                paymentService.startPayment(request, invalidMember));
        assertEquals(PAYMENT_CUSTOMER_ONLY, e.getErrorCode());
    }

    @Test
    @DisplayName("결제생성 실패 - DB 조회 후 정책 검증 실패 시 예외발생")
    void startPayment_shouldThrowException_whenValidateAfterSearch() {
        // given
        final Long requestOrderId = 1L;
        final Long requestMemberId = 5L;
        final Long orderMemberId = 7L;
        // 요청 결제 정보
        RequestPaymentDto request = RequestPaymentDto.builder()
                .orderId(requestOrderId)
                .paymentMethod(CARD)
                .build();
        // 결제 요청 고객
        Member requestMember = Member.builder()
                .id(requestMemberId)
                .roles(List.of(customerRole()))
                .build();
        // 주문한 고객
        Member orderMember = Member.builder()
                .id(orderMemberId)
                .roles(List.of(customerRole()))
                .build();
        // 요청 결제에 대한 주문
        Order order = order(orderMember);

        // 요청 결제에 대한 주문 조회
        given(orderRepository.findLockedByIdAndOrderStatus(any(), any()))
                .willReturn(Optional.of(order));
        // 주문에 대한 기존 결제내역 미존재
        given(paymentRepository.findLockedAllByOrderId(any()))
                .willReturn(Collections.emptyList());

        // 정책 실행 시 예외발생
        doThrow(new PaymentException(PAYMENT_ACCESS_AVAILABLE_ONLY_BUYER))
                .when(paymentPolicy)
                .validateCreate(argThat(List::isEmpty),
                                argThat(o -> o.getId().equals(requestOrderId)
                                        && o.getBuyer().getId().equals(orderMemberId)),
                                argThat(m -> m.getId().equals(requestMemberId)));
        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                paymentService.startPayment(request, requestMember));
        assertEquals(PAYMENT_ACCESS_AVAILABLE_ONLY_BUYER, e.getErrorCode());
    }

    @Test
    @DisplayName("결제생성 실패 - 결제 요청에 대한 주문 미존재 시 예외발생")
    void startPayment_shouldThrowException_whenNotExistsOrder() {
        // given
        // 요청 결제 정보
        RequestPaymentDto request = RequestPaymentDto.builder()
                .orderId(1L)
                .paymentMethod(CARD)
                .build();
        // 결제 요청 고객
        Member member = customer();

        // 요청 결제에 대한 주문 미존재
        given(orderRepository.findLockedByIdAndOrderStatus(eq(1L), eq(CREATED)))
                .willReturn(Optional.empty());

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                paymentService.startPayment(request, member));
        assertEquals(PAYMENT_ORDER_NOT_EXISTS, e.getErrorCode());
    }

    /* ----------------------
        결제 승인 웹훅 Tests
       ---------------------- */

    // PG 결제 승인 웹훅 성공 테스트
    // PG 결제 실패 웹훅 성공 테스트
    // 종결된 결제 웹훅 재요청 무시 성공 테스트

}