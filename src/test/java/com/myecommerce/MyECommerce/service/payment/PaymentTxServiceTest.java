package com.myecommerce.MyECommerce.service.payment;

import com.myecommerce.MyECommerce.dto.payment.PgApprovalResult;
import com.myecommerce.MyECommerce.dto.payment.PgResult;
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
import com.myecommerce.MyECommerce.type.PaymentStatusType;
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
import static com.myecommerce.MyECommerce.type.PaymentStatusType.*;
import static com.myecommerce.MyECommerce.type.PgProviderType.MOCK_PG;
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
    Member customer(Long memberId) {
        return Member.builder()
                .id(memberId)
                .roles(List.of(MemberAuthority.builder()
                        .authority(CUSTOMER)
                        .build()))
                .build();
    }
    Member customer() {
        return customer(3L);
    }

    /** 권한없는 회원 */
    Member memberOfEmptyRole() {
        return Member.builder()
                .roles(List.of())
                .build();
    }

    /** 등록된 상품 옵션 */
    ProductOption productOption() {
        Product registeredProduct = Product.builder()
                .saleStatus(ON_SALE)
                .build();

        return ProductOption.builder()
                .quantity(100)
                .price(new BigDecimal("10000"))
                .product(registeredProduct)
                .build();
    }

    /** 등록된 주문 */
    Order order(Long orderId, Member member) {
        OrderItem orderItem = OrderItem.createOrderItem(productOption(), 1);
        Order order = Order.createOrder(List.of(orderItem), member);
        ReflectionTestUtils.setField(order, "id", orderId);
        return order;
    }

    Order order(Member member) {
        ProductOption productOption = productOption();
        OrderItem orderItem = OrderItem.createOrderItem(productOption, 1);

        Order order = Order.createOrder(List.of(orderItem), member);
        ReflectionTestUtils.setField(order, "id", 1L);
        return order;
    }

    /** 저장된 결제 (id 추가) */
    Payment payment(Long paymentId,
                    Order order,
                    PaymentMethodType requestPaymentMethod,
                    PgProviderType pgProvider) {
        Payment payment =
                Payment.createPayment(order, requestPaymentMethod, pgProvider);
        ReflectionTestUtils.setField(payment, "id", paymentId);
        return payment;
    }

    /** PG 요청에 대한 응답 */
    PgResult pgRequestResult(String pgTransactionId) {
        return PgResult.builder()
                .pgTransactionId(pgTransactionId)
                .build();
    }

    /** PG 요청에 대한 응답 */
    PgApprovalResult pgApprovalResult(String pgTransactionId,
                                      PaymentStatusType paymentStatus) {
        return PgApprovalResult.builder()
                .pgTransactionId(pgTransactionId)
                .approvalStatus(paymentStatus)
                .paidAmount(new BigDecimal("10000"))
                .build();
    }

    /** PG 요청한 결제 */
    Payment inProgressPayment(Long paymentId, String pgTransactionId) {
        Order order = order(customer());
        PgResult pgRequestResult = pgRequestResult(pgTransactionId);

        Payment payment = payment(paymentId, order, CARD, MOCK_PG);
        payment.requestPgPayment(pgRequestResult); // 결제상태 = IN_PROGRESS
        return payment;
    }

    /** PG 승인된 결제 */
    Payment approvedPayment(Long paymentId, String pgTransactionId) {
        PgApprovalResult pgApprovalResult = pgApprovalResult(pgTransactionId, APPROVED);

        Payment payment = inProgressPayment(paymentId, pgTransactionId);// 결제상태 = IN_PROGRESS
        payment.approve(pgApprovalResult); // 결제상태 = APPROVED
        return payment;
    }

    /** 요청 결제 */
    RequestPaymentDto requestPaymentDto(Long orderId, PaymentMethodType paymentMethod) {
        return RequestPaymentDto.builder()
                .orderId(orderId)
                .paymentMethod(paymentMethod)
                .build();
    }

    /* ------------------
        Helper Method
       ------------------ */

    /* ---------------------------
        결제생성 정상 시나리오 Tests
       --------------------------- */

    @Test
    @DisplayName("결제생성 정상 시나리오 - 요청한 주문, 결제방법, PG 결제사에 대한 결제 미존재 시 신규 객체 저장")
    void createPayment_shouldSavePayment_whenNotExistsPaymentAboutOrder() {
        // given
        Long orderId = 1L;
        PaymentMethodType paymentMethod = CARD;
        PgProviderType pgProvider = MOCK_PG;
        // 요청 결제 정보
        RequestPaymentDto request = requestPaymentDto(orderId, paymentMethod);
        // 결제 요청 고객
        Member member = customer();

        // PG 결제대행사 반환
        given(pgClient.getProvider()).willReturn(pgProvider);
        // 요청 결제에 대한 주문 조회
        Order order = order(orderId, member);
        given(orderRepository.findLockedByIdAndOrderStatus(orderId, CREATED))
                .willReturn(Optional.of(order));
        // 주문에 대한 기존 결제내역 미존재
        given(paymentRepository.findLockedAllByOrderId(orderId))
                .willReturn(Collections.emptyList());
        // 신규 결제 생성 및 저장
        Payment savedPayment = payment(10L, order, paymentMethod, pgProvider);
        given(paymentRepository.save(any())).willReturn(savedPayment);

        // when
        Payment responsePayment = paymentTxService.createPayment(request, member);

        // then
        // 신규 결제 저장 여부 검증
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());
        Payment paymentBeforeSave = captor.getValue();
        assertNull(paymentBeforeSave.getId()); // 신규 객체로 아이디 없어야 함
        assertEquals(1L, paymentBeforeSave.getOrder().getId());
        assertEquals(CARD, paymentBeforeSave.getPaymentMethod());
        assertEquals(MOCK_PG, paymentBeforeSave.getPgProvider());
        assertEquals(READY, paymentBeforeSave.getPaymentStatus()); // 결제 상태 '준비'로 변경
        assertNotNull(paymentBeforeSave.getPaymentCode()); // paymentCode 규칙 검증은 Payment Entity에서 검증 (paymentCode 생성 규칙 바뀌면 Service 테스트가 깨지기 때문)

        // 응답 검증
        assertEquals(10L, responsePayment.getId());
        assertSame(savedPayment, responsePayment);
    }

    @Test
    @DisplayName("결제생성 정상 시나리오 - 동일 주문, 결제방식, 결제사에 대한 기존 결제내역 단건 존재 시 재사용")
    void createPayment_shouldReUsePayment_whenAlreadyExistsPayment() {
        // given
        Long orderId = 1L;
        Long originPaymentId = 10L;
        PaymentMethodType paymentMethod = CARD;
        PgProviderType pgProvider = MOCK_PG;
        // 요청 결제 정보
        RequestPaymentDto request = requestPaymentDto(orderId, paymentMethod);
        // 결제 요청 고객
        Member member = customer();

        // PG 결제대행사 반환
        given(pgClient.getProvider()).willReturn(pgProvider);
        // 요청 결제에 대한 주문 조회
        Order order = order(orderId, member);
        given(orderRepository.findLockedByIdAndOrderStatus(orderId, CREATED))
                .willReturn(Optional.of(order));
        // 주문에 대한 기존 결제내역 존재
        Payment originPayment =
                payment(originPaymentId, order, paymentMethod, pgProvider);
        given(paymentRepository.findLockedAllByOrderId(orderId))
                .willReturn(List.of(originPayment));
        // 기존 PG 결제 요청 가능 여부 반환
        given(paymentPolicy.isPaymentAvailablePgRequestAboutRequest(
                originPayment, paymentMethod, pgProvider))
                .willReturn(true);

        // when
        Payment responsePayment = paymentTxService.createPayment(request, member);

        // then
        // 기존 결제 조회 여부 검증
        verify(paymentRepository).findLockedAllByOrderId(orderId);
        // 기존 결제내역의 PG 요청 가능 여부 검증
        verify(paymentPolicy).isPaymentAvailablePgRequestAboutRequest(
                originPayment, paymentMethod, pgProvider);
        // 기존 결제를 이용하므로, 신규 결제 저장되지 않음을 검증
        verify(paymentRepository, never()).save(any());

        // 응답 검증
        assertSame(originPayment, responsePayment);
    }

    // TODO: 결제생성 정상 시나리오 - 다건의 기존 결제내역목록 중 동일 결제 존재 시 재사용

    // TODO: 결제생성 정상 시나리오 - 재사용 불가능 기존 결제 존재 시 결제 신규생성 및 저장

    /* ----------------------------
        결제생성 책임 행위 검증 Tests
       ---------------------------- */

    @Test
    @DisplayName("결제생성 책임 - 결제 시작 시 정책 호출 검증")
    void createPayment_shouldCallPaymentPolicy_whenStartPayment() {
        // given
        Long orderId = 1L;
        // 요청 결제 정보
        RequestPaymentDto request = requestPaymentDto(orderId, CARD);
        // 결제 요청 고객
        Member member = customer();

        // 요청 결제에 대한 주문 조회
        Order order = order(orderId, member);
        given(orderRepository.findLockedByIdAndOrderStatus(orderId, CREATED))
                .willReturn(Optional.of(order));
        // 주문에 대한 기존 결제내역 미존재
        given(paymentRepository.findLockedAllByOrderId(orderId))
                .willReturn(Collections.emptyList());
        // 신규 결제 저장
        given(paymentRepository.save(any())).willAnswer(
                invocation -> invocation.getArgument(0));

        // when
        paymentTxService.createPayment(request, member);

        // then
        // 정책 실행여부 검증
        verify(paymentPolicy).preValidateCreate(member);
        verify(paymentPolicy).validateCreate(Collections.emptyList(), order, member);
    }

    // 결제생성 책임 - 신규 결제 요청 시 결제 객체 생성 및 저장 검증
    // 정상 시나리오와 테스트 중복되므로 통합하고 제거함

    // TODO: 결제생성 책임 - PgProvider 호출 검증

    // TODO: 결제생성 책임 - 메서드 호출 순서 검증

    /* ----------------------------
        결제생성 실패 Tests
       ---------------------------- */

    @Test
    @DisplayName("결제생성 실패 - 사전 정책 검증 실패 시 예외발생")
    void createPayment_shouldThrowException_whenPreValidate() {
        // given
        RequestPaymentDto request = RequestPaymentDto.builder().build();
        Member invalidMember = memberOfEmptyRole(); // 고객 권한 없음

        // 사전 정책 실행 시 예외발생
        doThrow(new PaymentException(PAYMENT_CUSTOMER_ONLY))
                .when(paymentPolicy)
                .preValidateCreate(invalidMember);

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                paymentTxService.createPayment(request, invalidMember));
        assertEquals(PAYMENT_CUSTOMER_ONLY, e.getErrorCode());
    }

    @Test
    @DisplayName("결제생성 실패 - 결제에 대한 주문 조회 후 정책 검증 실패 시 예외발생")
    void createPayment_shouldThrowException_whenValidateAfterSearch() {
        // given
        final Long orderId = 1L;
        final Long requestMemberId = 5L;
        final Long orderMemberId = 7L;
        // 요청 결제 정보
        RequestPaymentDto request = requestPaymentDto(orderId, CARD);
        // 결제 요청 고객
        Member requestMember = customer(requestMemberId);
        // 주문한 고객
        Member orderMember = customer(orderMemberId);

        // 요청 결제에 대한 주문 조회
        Order order = order(orderId, orderMember);
        given(orderRepository.findLockedByIdAndOrderStatus(orderId, CREATED))
                .willReturn(Optional.of(order));
        // 주문에 대한 기존 결제내역 미존재
        given(paymentRepository.findLockedAllByOrderId(orderId))
                .willReturn(Collections.emptyList());

        // 정책 실행 시 예외발생
        doThrow(new PaymentException(PAYMENT_ACCESS_AVAILABLE_ONLY_BUYER))
                .when(paymentPolicy)
                .validateCreate(Collections.emptyList(), order, requestMember);

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                paymentTxService.createPayment(request, requestMember));
        assertEquals(PAYMENT_ACCESS_AVAILABLE_ONLY_BUYER, e.getErrorCode());
    }

    @Test
    @DisplayName("결제생성 실패 - 요청 결제에 대한 주문 미존재 시 예외발생")
    void createPayment_shouldThrowException_whenNotFoundOrder() {
        // given
        // 요청 결제 정보
        Long orderId = 1L;
        RequestPaymentDto request = requestPaymentDto(orderId, CARD);
        // 결제 요청 고객
        Member member = customer();

        // 요청 결제에 대한 주문 미존재
        given(orderRepository.findLockedByIdAndOrderStatus(orderId, CREATED))
                .willReturn(Optional.empty());

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                paymentTxService.createPayment(request, member));
        assertEquals(PAYMENT_ORDER_NOT_EXISTS, e.getErrorCode());
    }

    // TODO: 결제생성 실패 - 결제객체 저장 실패 시 예외발생

    /* ----------------------------
        결제에 PG요청 결과반영 Tests
       ---------------------------- */
    // updatePaymentToInProgress() 테스트 작성
    // TODO: 결제에 PG요청 결과반영 성공 - 정상 시나리오
    // TODO: 결제에 PG요청 결과반영 실패 - 결제 조회 불가 시 예외발생 (테스트 수정 후 작성 필요)
    // PG요청 결과반영 예외처리는 PaymentTest에서 진행하므로 제외

    /* ----------------------------
        PG 결제승인 정상 시나리오 Tests
       ---------------------------- */

    @Test
    @DisplayName("PG 결제 승인 반영 - PG 결과가 승인(APPROVED)일 때 결제상태 변경하여 1반환")
    void updatePgApprovalResult_shouldReturn1_whenPgResultIsApproved() {
        // given
        Long paymentId = 10L;
        String pgTransactionId = "pgTransactionId";
        // PG 결제승인 결과 (PG 요청 승인)
        PgApprovalResult pgApprovalResult = pgApprovalResult(pgTransactionId, APPROVED);

        // 결제 조회
        Payment payment = inProgressPayment(paymentId, pgTransactionId);
        given(paymentRepository.findByIdWithOrder(paymentId))
                .willReturn(Optional.of(payment));
        // 조건부로 결제상태 우선변경 여부 반환
        given(paymentRepository.approveIfInProgress(paymentId, APPROVED))
                .willReturn(1); // 조건부로 결제상태 우선변경

        // when
        int response = paymentTxService.updatePgApprovalResult(paymentId, pgApprovalResult);

        // then
        // 상태값 조건부 업데이트 메서드 호출 검증
        verify(paymentRepository).approveIfInProgress(paymentId, APPROVED);
        assertEquals(APPROVED, payment.getPaymentStatus()); // 결제승인
        assertEquals(1, response); // 최종 반환 결과
    }

    @Test
    @DisplayName("PG 결제 승인실패 반영 - PG 결과가 실패(FAILED)일 때 결제상태 변경하여 1반환")
    void updatePgApprovalResult_shouldReturn1_whenPgResultIsFailed() {
        // given
        Long paymentId = 10L;
        String pgTransactionId = "pgTransactionId";
        // PG 결제승인 결과 (PG 요청 실패)
        PgApprovalResult pgFailResult = pgApprovalResult(pgTransactionId, FAILED);

        // 결제 조회
        Payment payment = inProgressPayment(paymentId, pgTransactionId);
        given(paymentRepository.findByIdWithOrder(paymentId))
                .willReturn(Optional.of(payment));
        // 조건부로 결제상태 우선변경 여부 반환
        given(paymentRepository.approveIfInProgress(paymentId, FAILED))
                .willReturn(1);

        // when
        int response = paymentTxService.updatePgApprovalResult(paymentId, pgFailResult);

        // then
        // 상태값 조건부 업데이트 메서드 호출 검증
        verify(paymentRepository).approveIfInProgress(paymentId, FAILED);
        assertEquals(FAILED, payment.getPaymentStatus()); // 결제승인실패
        assertEquals(1, response); // 최종 반환 결과
    }

    /* ----------------------------
        PG 결제승인 정합성 Tests
       ---------------------------- */

    @Test
    @DisplayName("PG 결제승인 실패 - PG 결과가 PG 요청중(IN_PROGRESS)일 때 결제상태 미변경으로 0반환")
    void updatePgApprovalResult_shouldReturn0_whenPgResultIsInProgress() {
        // given
        Long paymentId = 10L;
        String pgTransactionId = "pgTransactionId";
        // PG 결제승인 결과 (PG 요청 진행중)
        PgApprovalResult pgApprovalResult = pgApprovalResult(pgTransactionId, IN_PROGRESS);

        // 결제 조회
        Payment payment = inProgressPayment(paymentId, pgTransactionId);
        given(paymentRepository.findByIdWithOrder(paymentId))
                .willReturn(Optional.of(payment));

        // when
        int response = paymentTxService.updatePgApprovalResult(paymentId, pgApprovalResult);

        // then
        // 상태값 조건부 업데이트 메서드 미호출 검증
        verify(paymentRepository, never()).approveIfInProgress(any(), any());
        assertEquals(IN_PROGRESS, payment.getPaymentStatus()); // 결제상태 미변경
        assertEquals(0, response); // 최종 반환 결과
    }

    /* ----------------------------
        PG 결제승인 멱등성 Tests
       ---------------------------- */

    @Test
    @DisplayName("PG 결제승인 멱등성 - 이미 승인된 결제 건은 결제상태 미변경으로 0반환")
    void updatePgApprovalResult_shouldReturn0_whenPaymentAlreadyApproved() {
        // given
        String pgTransactionId = "pgTransactionId";
        Long paymentId = 10L;
        // 승인된 결제건에 대해 FAILED 재응답 시도
        PgApprovalResult pgFailResult = pgApprovalResult(pgTransactionId, FAILED);

        // 결제 조회 - 이미 승인된 결제 조회
        Payment approvedPayment = approvedPayment(paymentId, pgTransactionId);
        given(paymentRepository.findByIdWithOrder(paymentId))
                .willReturn(Optional.of(approvedPayment));
        // 조건부로 결제상태 우선변경
        given(paymentRepository.approveIfInProgress(paymentId, FAILED))
                .willReturn(0);

        // when
        int response = paymentTxService.updatePgApprovalResult(paymentId, pgFailResult);

        // then
        // 상태값 조건부 업데이트 메서드 호출 검증
        verify(paymentRepository).approveIfInProgress(paymentId, FAILED);
        assertEquals(APPROVED, approvedPayment.getPaymentStatus()); // 결제상태 미변경
        assertEquals(0, response); // 최종 반환 결과
    }

    /* ----------------------------
        PG 결제승인 실패 Tests
       ---------------------------- */

    @Test
    @DisplayName("PG 결제승인 상태 분기 - 결제 ID에 대한 결제 미존재 시 예외발생")
    void updatePgApprovalResult_shouldThrowException_whenNotExistsPayment() {
        // given
        Long invalidPaymentId = 10L;
        PgApprovalResult pgApprovalResult = PgApprovalResult.builder().build();

        // 결제 조회 - 해당 결제 미존재
        given(paymentRepository.findByIdWithOrder(invalidPaymentId))
                .willReturn(Optional.empty());

        // when
        // then
        PaymentException e = assertThrows(PaymentException.class, () ->
                paymentTxService.updatePgApprovalResult(invalidPaymentId, pgApprovalResult));
        assertEquals(PAYMENT_NOT_FOUND, e.getErrorCode());
        // 상태값 조건부 업데이트 메서드 미호출 검증
        verify(paymentRepository, never()).approveIfInProgress(any(), any());
    }

}