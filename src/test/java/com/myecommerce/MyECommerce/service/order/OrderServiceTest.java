package com.myecommerce.MyECommerce.service.order;

import com.myecommerce.MyECommerce.dto.order.RequestOrderDto;
import com.myecommerce.MyECommerce.dto.order.RequestOrderItemDto;
import com.myecommerce.MyECommerce.dto.order.ResponseOrderDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.order.OrderItem;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.exception.OrderException;
import com.myecommerce.MyECommerce.mapper.OrderMapper;
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.service.cart.CartService;
import com.myecommerce.MyECommerce.service.stock.StockCacheService;
import com.myecommerce.MyECommerce.type.OrderPathType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static com.myecommerce.MyECommerce.exception.errorcode.OrderErrorCode.ORDER_ITEM_MAX_QUANTITY_EXCEEDED;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderPolicy orderPolicy;

    @Mock
    private StockCacheService stockCacheService;
    @Mock
    private CartService cartService;

    @Mock
    private ProductOptionRepository productOptionRepository;
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 고객권한 사용자 */
    Member customer() {
        return Member.builder()
                .userId("tester")
                .roles(List.of(MemberAuthority.builder()
                        .authority(CUSTOMER)
                        .build()))
                .build();
    }

    /** 등록된 상품 */
    Product registeredProduct() {
        return Product.builder()
                .id(5L)
                .code("productCode")
                .seller(1L)
                .saleStatus(ON_SALE)
                .build();
    }

    /** 등록된 상품 옵션 */
    ProductOption registeredOption(Long optionId, int quantity) {
        return ProductOption.builder()
                .id(optionId)
                .optionCode("optionCode")
                .quantity(quantity)
                .price(new BigDecimal("10000"))
                .product(registeredProduct())
                .build();
    }
    ProductOption registeredOption() {
        return registeredOption(10L, 100);
    }
    ProductOption registeredOption(Long optionId) {
        return registeredOption(optionId, 100);
    }

    /** 생성된 주문 */
    Order savedOrder(ProductOption registeredOption,
                     Member member,
                     int requestedQuantity) {
        OrderItem orderItem =
                OrderItem.createOrderItem(registeredOption, requestedQuantity);
        return Order.createOrder(List.of(orderItem), member);
    }

    /** 요청 주문 물품 */
    RequestOrderItemDto requestOrderItemDto(Long productOptionId, int quantity) {
        return RequestOrderItemDto.builder()
                .productOptionId(productOptionId)
                .quantity(quantity)
                .build();
    }
    RequestOrderItemDto requestOrderItemDto() {
        return requestOrderItemDto(10L, 5);
    }

    /** 요청 주문 */
    RequestOrderDto requestOrderDto(OrderPathType orderPathType, RequestOrderItemDto requestItem) {
        return RequestOrderDto.builder()
                .orderPathType(orderPathType)
                .orderItems(List.of(requestItem))
                .build();
    }
    RequestOrderDto requestOrderDto(RequestOrderItemDto requestItem) {
        return requestOrderDto(null, requestItem);
    }
    RequestOrderDto requestOrderDto() {
        return requestOrderDto(null, requestOrderItemDto());
    }

    /* ------------------
        Helper Method
       ------------------ */

    // BigDecimal 금액 반환
    BigDecimal price(String price) {
        return new BigDecimal(price);
    }

    /* ------------------------
        주문 생성 Test
       ------------------------ */

    @Test
    @DisplayName("주문생성 성공 - 유효한 주문 요청 시 주문생성 정책 검증")
    void createOrder_shouldValidatePolicy_whenValidOrderRequest() {
        // given
        // 요청 고객
        Member member = customer();
        // 요청 주문
        RequestOrderDto requestOrder = requestOrderDto();

        // 요청한 주문 상품옵션 조회
        given(productOptionRepository.findByIdIn(any()))
                .willReturn(List.of(registeredOption()));
        // 주문 저장
        given(orderRepository.save(any())).willReturn(mock(Order.class));
        // 저장된 주문 Entity -> response DTO로 변환
        given(orderMapper.toResponseDto(any())).willReturn(mock(ResponseOrderDto.class));

        // when
        orderService.createOrder(requestOrder, member);

        // then
        // 정책 실행 여부 검증
        verify(orderPolicy).validateCreate(any(), any(), any());
    }

    @Test
    @DisplayName("주문생성 성공 - 유효한 주문 요청 시 주문 Entity 저장")
    void createOrder_shouldSaveOrder_whenValidOrderRequest() {
        // given
        // 요청 고객
        Member member = customer();
        // 요청 주문
        Long optionId = 10L;
        int quantity = 5;
        RequestOrderItemDto requestItem = requestOrderItemDto(optionId, quantity);
        RequestOrderDto requestOrder = requestOrderDto(OrderPathType.CART, requestItem);

        // 요청한 주문 상품옵션 조회
        ProductOption registeredOption = registeredOption(optionId);
        given(productOptionRepository.findByIdIn(List.of(optionId))) // 저장할 객체인 주문물품의 정보를 조회하므로 비즈니스 로직의 핵심이기 때문에 구체적인 값 명시
                .willReturn(List.of(registeredOption));

        // 주문 저장
        Order savedOrder = savedOrder(registeredOption, member, quantity);
        ArgumentCaptor<Order> capturedOrderBeforeSave =
                ArgumentCaptor.forClass(Order.class);
        given(orderRepository.save(capturedOrderBeforeSave.capture()))
                .willReturn(savedOrder);

        // 저장된 주문 Entity -> response DTO로 변환
        given(orderMapper.toResponseDto(any())).willReturn(mock(ResponseOrderDto.class));

        // when
        orderService.createOrder(requestOrder, member);

        // then
        // 주문 생성 검증
        Order capturedOrder = capturedOrderBeforeSave.getValue();
        assertEquals(CREATED, capturedOrder.getOrderStatus());
        assertEquals(price("50000"), capturedOrder.getTotalPrice());
        assertEquals(member, capturedOrder.getBuyer());
        assertEquals(1, capturedOrder.getItems().size());
        assertNotNull(capturedOrder.getOrderNumber());
        assertNotNull(capturedOrder.getOrderedAt());

        // 주문물품 생성 검증
        OrderItem capturedOrderItem = capturedOrder.getItems().get(0);
        assertEquals(requestItem.getQuantity(), capturedOrderItem.getQuantity());
        assertEquals(price("10000"), capturedOrderItem.getUnitPrice());
        assertEquals(price("50000"), capturedOrderItem.getTotalPrice());
        assertEquals(registeredOption, capturedOrderItem.getOption());
    }

    @Test
    @DisplayName("주문생성 성공 - 유효한 주문 요청 시 상품옵션 재고 감소")
    void createOrder_shouldDecreaseOptionStock_whenValidOrderRequest() {
        // given
        // 요청 고객
        Member member = customer();
        // 요청 주문
        Long optionId = 10L;
        int quantity = 5; // 요청한 옵션의 주문 수량
        RequestOrderItemDto requestItem = requestOrderItemDto(optionId, quantity);
        RequestOrderDto requestOrder = requestOrderDto(requestItem);

        // 요청한 주문 상품옵션 조회
        ProductOption registeredOption = registeredOption(optionId, 100); // 옵션의 재고 100개
        given(productOptionRepository.findByIdIn(List.of(optionId)))
                .willReturn(List.of(registeredOption));

        // 주문 저장
        Order savedOrder = savedOrder(registeredOption, member, quantity);
        given(orderRepository.save(any())).willReturn(savedOrder);

        // 저장된 주문 Entity -> response DTO로 변환
        given(orderMapper.toResponseDto(any())).willReturn(mock(ResponseOrderDto.class));

        // when
        orderService.createOrder(requestOrder, member);

        // then
        // 재고 차감 수량 검증 (Service 내부 연산에 의한 Java Entity 객체 메모리 상태 변경 검증)
        assertEquals(95, registeredOption.getQuantity());
        // 더티체킹으로 인해 실제 재고 감소 검증은 통합테스트로 수행 - createOrder_shouldDecreaseOptionStock_whenOrderCreated()
    }

    @Test
    @DisplayName("주문생성 성공 - 유효한 주문 요청 시 캐시 재고 감소")
    void createOrder_shouldDecreaseOptionStockCache_whenValidOrderRequest() {
        // given
        // 요청 고객
        Member member = customer();
        // 요청 주문
        Long optionId = 10L;
        int quantity = 5;
        RequestOrderItemDto requestItem = requestOrderItemDto(optionId, quantity);
        RequestOrderDto requestOrder = requestOrderDto(requestItem);

        // 요청한 주문 상품옵션 조회
        ProductOption registeredOption = registeredOption(optionId);
        given(productOptionRepository.findByIdIn(List.of(optionId)))
                .willReturn(List.of(registeredOption));

        // 주문 저장
        Order savedOrder = savedOrder(registeredOption, member, quantity);
        given(orderRepository.save(any())).willReturn(savedOrder);

        // 저장된 주문 Entity -> response DTO로 변환
        given(orderMapper.toResponseDto(any())).willReturn(mock(ResponseOrderDto.class));

        // when
        orderService.createOrder(requestOrder, member);

        // then
        // 재고 캐시 데이터 차감 실행 여부 검증
        verify(stockCacheService).decrementProductStock(savedOrder.getItems());
    }

    // TODO: CartService에서는 장바구니에서 상품옵션을 제거하는 로직만 가지고, OrderService에서 주문경로에 따라 removeOrderItems() 호출 여부를 결정하는 메서드를 두는 것 고려하기
    @Test
    @DisplayName("주문생성 성공 - 유효한 주문 요청 시 장바구니에서 상품옵션 제거")
    void createOrder_shouldRemoveOrderItemsFromCart_whenValidOrderRequest() {
        // given
        // 요청 고객
        Member member = customer();
        // 요청 주문
        Long optionId = 10L;
        int quantity = 5;
        RequestOrderItemDto requestItem = requestOrderItemDto(optionId, quantity);
        RequestOrderDto requestOrder = requestOrderDto(OrderPathType.CART, requestItem);

        // 요청한 주문 상품옵션 조회
        ProductOption registeredOption = registeredOption(optionId);
        given(productOptionRepository.findByIdIn(List.of(optionId)))
                .willReturn(List.of(registeredOption));

        // 주문 저장
        Order savedOrder = savedOrder(registeredOption, member, quantity);
        given(orderRepository.save(any())).willReturn(savedOrder);

        // 저장된 주문 Entity -> response DTO로 변환
        given(orderMapper.toResponseDto(any())).willReturn(mock(ResponseOrderDto.class));

        // when
        orderService.createOrder(requestOrder, member);

        // then
        // 장바구니에서 주문한 상품옵션 제거 실행 여부 검증
        // 주문 경로에 따라 장바구니에서 상품옵션 제거여부가 상이하나, 해당 메서드 호출은 주문 경로에 관계없이 호출
        verify(cartService).removeOrderItems(eq(OrderPathType.CART),
                                             eq(member.getUserId()),
                                             eq(savedOrder.getItems()));
    }

    @Test
    @DisplayName("주문생성 실패 - 주문생성 정책 검증 실패 시 어떤 상태 변경도 발생하지 않음")
    void createOrder_shouldThrowException_whenInvalidOrderRequest() {
        // given
        // 요청 고객
        Member member = customer();
        // 요청 주문물품
        RequestOrderItemDto invalidRequestItem = RequestOrderItemDto.builder()
                .productOptionId(5L)
                .quantity(51) // 주문 정책 제한: 물품 당 최대 주문 수량 초과
                .build();
        RequestOrderDto invalidRequestOrder = requestOrderDto(invalidRequestItem);

        // 정책에서 예외 발생
        doThrow(new OrderException(ORDER_ITEM_MAX_QUANTITY_EXCEEDED))
                .when(orderPolicy)
                .validateCreate(eq(List.of(invalidRequestItem)), any(), eq(member));

        // when
        // then
        // 정책이 왜 실패했는지가 아니라, 정책 실패 시 Service가 어떻게 반응하는지 검증.
        // -> OrderService는 정책 검증 실패 시 어떤 부작용도 일으키지 않고 즉시 중단한다
        OrderException e = assertThrows(OrderException.class, () ->
                orderService.createOrder(invalidRequestOrder, member));
        verify(orderRepository, never()).save(any());
        verify(stockCacheService, never()).decrementProductStock(any());
        verify(orderMapper, never()).toResponseDto(any());
        assertEquals(ORDER_ITEM_MAX_QUANTITY_EXCEEDED, e.getErrorCode());
    }

}