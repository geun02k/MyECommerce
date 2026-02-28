package com.myecommerce.MyECommerce.service.payment;

import com.myecommerce.MyECommerce.dto.payment.RequestPaymentDto;
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
import com.myecommerce.MyECommerce.type.PaymentMethodType;
import com.myecommerce.MyECommerce.type.PgProviderType;
import io.jsonwebtoken.lang.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.myecommerce.MyECommerce.exception.errorcode.PaymentErrorCode.*;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;
import static com.myecommerce.MyECommerce.type.PaymentMethodType.CARD;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.READY;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentTxServiceTest {

    @Mock
    PgClient pgClient;

    @Mock
    PaymentPolicy paymentPolicy;

    @Mock
    OrderRepository orderRepository;
    @Mock
    PaymentRepository paymentRepository;

    @InjectMocks
    PaymentTxService paymentTxService;

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

    /** 실행가능상태 준비 - 신규 payment 저장 mocking */
    void givenPaymentSaveSucceeds() {
        // 신규 결제 저장
        given(paymentRepository.save(any()))
                .willAnswer(invocationOnMock ->
                        invocationOnMock.getArgument(0));
    }

    /* ---------------------------
        결제생성 정상 시나리오 Tests
       --------------------------- */

    @Test
    @DisplayName("결제생성 정상 시나리오 - 요청한 주문, 결제방법, PG 결제사에 대한 결제 미존재 시 신규 객체 저장")
    void createPayment_shouldSavePayment_whenNotExistsPaymentOfOrder() {
        // given
        Long requestOrderId = 1L;
        PaymentMethodType requestPaymentMethod = PaymentMethodType.CARD;
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

        // when
        Payment response = paymentTxService.createPayment(request, member);

        // then
        assertEquals(requestOrderId, response.getOrder().getId());
        assertEquals(requestPaymentMethod, response.getPaymentMethod());
        assertEquals(pgProvider, response.getPgProvider());
        assertEquals(READY, response.getPaymentStatus()); // 결제 상태 '준비'로 변경
    }

    @Test
    @DisplayName("결제생성 정상 시나리오 - 기존 결제내역 중 동일 주문, 결제방식, 결제사에 대한 결제 존재 시 재사용")
    void createPayment_shouldReUsePayment_whenAlreadyExistsPayment() {
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

        // when
        paymentTxService.createPayment(request, member);

        // then
        // 기존 결제 조회 여부 검증
        verify(paymentRepository).findLockedAllByOrderId(requestOrderId);
        // 기존 결제를 이용하므로, 신규 결제 저장되지 않음을 검증
        verify(paymentRepository, never()).save(any());
        // 기존 결제내역의 PG 요청 가능 여부 검증
        verify(paymentPolicy).isPaymentAvailablePgRequestAboutRequest(
                existingPayment, requestPaymentMethod, pgProvider);
        // 결제 재사용 로직으로 응답이 달라질 가능성은 없으므로, 응답 검증 제외
    }

    // 결제생성 정상 시나리오 - 기존 결제내역목록 중 동일 결제 존재 시 재사용

    // 결제생성 정상 시나리오 - 재사용 불가능 기존 결제 존재 시 결제 신규생성 및 저장

    /* ----------------------------
        결제생성 책임 행위 검증 Tests
       ---------------------------- */

    @Test
    @DisplayName("결제생성 책임 - 결제 시작 시 정책 호출 검증")
    void createPayment_shouldCallPaymentPolicy_whenStartPayment() {
        // given
        Long requestOrderId = 1L;
        // 요청 결제 정보
        RequestPaymentDto request = RequestPaymentDto.builder()
                .orderId(requestOrderId)
                .paymentMethod(CARD)
                .build();
        // 결제 요청 고객
        Member member = customer();

        // 요청 결제에 대한 주문
        Order order = order(member);

        // 요청 결제에 대한 주문 조회
        given(orderRepository.findLockedByIdAndOrderStatus(
                requestOrderId, CREATED))
                .willReturn(Optional.of(order));
        // 주문에 대한 기존 결제내역 미존재
        given(paymentRepository.findLockedAllByOrderId(requestOrderId))
                .willReturn(Collections.emptyList());
        // 실행조건 mock
        givenPaymentSaveSucceeds();

        // when
        paymentTxService.createPayment(request, member);

        // then
        // 정책 실행여부 검증
        verify(paymentPolicy).preValidateCreate(eq(member));
        verify(paymentPolicy).validateCreate(
                argThat(List::isEmpty), eq(order), eq(member));
    }

    @Test
    @DisplayName("결제생성 책임 - 신규 결제 요청 시 결제 객체 생성 및 저장 검증")
    void createPayment_shouldCreateAndSavePayment_whenRequestNewPayment() {
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

        // when
        Payment response = paymentTxService.createPayment(request, member);

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
        assertEquals(10L, response.getId());
    }

    // 결제생성 책임 - PgProvider 호출 검증

    // 결제생성 책임 - 메서드 호출 순서 검증

    /* ----------------------------
        결제생성 실패 Tests
       ---------------------------- */

    @Test
    @DisplayName("결제생성 실패 - 사전 정책 검증 실패 시 예외발생")
    void createPayment_shouldThrowException_whenPreValidate() {
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
                paymentTxService.createPayment(request, invalidMember));
        assertEquals(PAYMENT_CUSTOMER_ONLY, e.getErrorCode());
    }

    @Test
    @DisplayName("결제생성 실패 - DB 조회 후 정책 검증 실패 시 예외발생")
    void createPayment_shouldThrowException_whenValidateAfterSearch() {
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
                paymentTxService.createPayment(request, requestMember));
        assertEquals(PAYMENT_ACCESS_AVAILABLE_ONLY_BUYER, e.getErrorCode());
    }

    @Test
    @DisplayName("결제생성 실패 - 결제 요청에 대한 주문 미존재 시 예외발생")
    void createPayment_shouldThrowException_whenNotExistsOrder() {
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
                paymentTxService.createPayment(request, member));
        assertEquals(PAYMENT_ORDER_NOT_EXISTS, e.getErrorCode());
    }

    // 결제생성 실패 - 결제객체 저장 실패 시 예외발생
}