package com.myecommerce.MyECommerce.service.payment;

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
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import com.myecommerce.MyECommerce.repository.payment.PaymentRepository;
import com.myecommerce.MyECommerce.type.OrderStatusType;
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

import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.PaymentMethodType.CARD;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.IN_PROGRESS;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.READY;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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

    /** 생성된 주문 */
    Order order(Member member) {
        ProductOption productOption = productOption();
        OrderItem orderItem = OrderItem.createOrderItem(productOption, 1);

        Order order = Order.createOrder(List.of(orderItem), member);
        ReflectionTestUtils.setField(order, "id", 1L);
        return order;
    }

    /* ------------------
        Helper Method
       ------------------ */

    /* ----------------------
        결제 생성 Tests
       ---------------------- */

    @Test
    @DisplayName("결제생성 성공 - 결제 객체 생성 및 PG 결제 요청") // 결제 생성 흐름 정상 시나리오
    void startPayment_shouldCreatePaymentAndPgRequest_whenValid() {
        // given
        // 요청 결제 정보
        Long requestOrderId = 1L;
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
        // 저장된 신규 결제
        Payment savedPayment = Payment.createPayment(order, CARD, pgProvider);
        // PG 결제 요청 응답
        PgResult pgResult = PgResult.builder()
                .pgTransactionId("pgTransactionId")
                .build();

        // PG 결제대행사 반환
        given(pgClient.getProvider()).willReturn(pgProvider);
        // 요청 결제에 대한 주문 조회
        given(orderRepository.findLockedByIdAndOrderStatus(
                requestOrderId, OrderStatusType.CREATED))
                .willReturn(Optional.of(order));
        // 주문에 대한 기존 결제내역 미존재
        given(paymentRepository.findLockedAllByOrderId(requestOrderId))
                .willReturn(Collections.emptyList());
        // 신규 결제 생성 및 저장
        given(paymentRepository.save(any())).willReturn(savedPayment);
        // PG 결제대행사에 결제 요청
        given(pgClient.requestPayment(savedPayment))
                .willReturn(pgResult);

        // when
        ResponsePaymentDto response =
                paymentService.startPayment(request, member);

        // then
        // 정책 실행여부 검증
        verify(paymentPolicy).preValidateCreate(member);
        verify(paymentPolicy).validateCreate(Collections.emptyList(), order, member);
        // PG 요청여부 검증 (외부 api 호출)
        verify(pgClient).requestPayment(savedPayment);
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

        // PG 요청 후 응답 검증
        assertEquals(requestOrderId, response.getOrderId());
        assertEquals(IN_PROGRESS, response.getPaymentStatus()); // 결제 상태 PG 요청으로 변경
        assertEquals(pgResult.getPgTransactionId(), response.getPgResult().getPgTransactionId());
        assertEquals(pgResult.getRedirectUrl(), response.getPgResult().getRedirectUrl());
    }

    // + 기존 결제 중 승인 가능한 결제가 있을 경우 결제 재사용 테스트

    // 주문된 조회가 없는데 결제 요청한 경우 실패 테스트
    // 사전 정책 검증 실패 테스트
    // 조회 후 정책 검증 실패 테스트
    // PG 결제대행사 요청 문제 발생 시 IN_PROGRESS로 변경되지 않음을 테스트


    // 2. 메서드를 정책으로 이동해 테스트 진행여부 판단할 것
    // PG 결제 종료된 경우 실패 테스트
    // 요청 결제 방식과 다른 경우 실패 테스트
    // 결제 대행사 정보가 동일하지 않은 경우 실패 테스트

    /* ----------------------
        결제 승인 웹훅 Tests
       ---------------------- */

    // PG 결제 승인 웹훅 성공 테스트
    // PG 결제 실패 웹훅 성공 테스트
    // 종결된 결제 웹훅 재요청 무시 성공 테스트

}