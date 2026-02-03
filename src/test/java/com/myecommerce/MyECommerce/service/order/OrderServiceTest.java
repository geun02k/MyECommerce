package com.myecommerce.MyECommerce.service.order;

import com.myecommerce.MyECommerce.dto.order.RequestOrderDto;
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
import com.myecommerce.MyECommerce.service.stock.StockCacheService;
import com.myecommerce.MyECommerce.vo.order.ProductOptionKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    /** 등록된 상품 옵션 */
    ProductOption registeredOption() {
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
    Order savedOrder(ProductOption registeredOption,
                     Member member,
                     int requestedQuantity) {
        OrderItem orderItem = OrderItem.createOrderItem(registeredOption,
                requestedQuantity);
        return Order.createOrder(List.of(orderItem), member);
    }

    /* ------------------
        Helper Method
       ------------------ */
    /* ------------------------
        주문 생성 Test
       ------------------------ */

    @Test
    @DisplayName("주문생성 성공 - 유효한 주문 요청 시 주문 생성 및 재고 감소")
    void createOrder_shouldCreateOrderAndDecreaseStock_whenValidOrderRequest() {
        // given
        // 요청 고객
        Member member = customer();
        // 요청 주문물품
        RequestOrderDto requestItem = RequestOrderDto.builder()
                .productId(5L)
                .optionCode("optionCode")
                .quantity(5)
                .build();

        // 주문 요청에 대한 상품옵션 조회
        ProductOptionKey optionKey =
                new ProductOptionKey(5L, "optionCode");
        ProductOption registeredOption = registeredOption();
        given(productOptionRepository.findOptionsWithLock(List.of(optionKey)))
                .willReturn(new ArrayList<>(List.of(registeredOption)));

        // 주문 저장
        Order savedOrder = savedOrder(
                registeredOption, member, requestItem.getQuantity());
        ArgumentCaptor<Order> capturedOrderBeforeSave =
                ArgumentCaptor.forClass(Order.class);
        given(orderRepository.save(capturedOrderBeforeSave.capture()))
                .willReturn(savedOrder);

        // 저장된 주문 Entity -> response DTO로 변환
        given(orderMapper.toResponseDto(any()))
                .willReturn(mock(ResponseOrderDto.class));

        // when
        orderService.createOrder(List.of(requestItem), member);

        // then
        // 정책 실행 여부 검증
        verify(orderPolicy, times(1))
                .validateCreate(any(), any());
        // 재고 캐시 데이터 차감 실행 여부 검증
        verify(stockCacheService, times(1))
                .decrementProductStock(any());
        // 재고 차감 검증은 통합테스트에서 수행

        // 주문 생성 검증
        Order capturedOrder = capturedOrderBeforeSave.getValue();
        assertEquals(CREATED, capturedOrder.getOrderStatus());
        assertEquals(new BigDecimal("50000"), capturedOrder.getTotalPrice());
        assertEquals(member, capturedOrder.getBuyer());
        assertEquals(1, capturedOrder.getItems().size());
        assertNotNull(capturedOrder.getOrderNumber());
        assertNotNull(capturedOrder.getOrderedAt());
        // 주문물품 생성 검증
        OrderItem capturedOrderItem = capturedOrder.getItems().get(0);
        assertEquals(requestItem.getQuantity(), capturedOrderItem.getQuantity());
        assertEquals(new BigDecimal("10000"), capturedOrderItem.getUnitPrice());
        assertEquals(new BigDecimal("50000"), capturedOrderItem.getTotalPrice());
        assertEquals(registeredOption, capturedOrderItem.getOption());
    }

    @Test
    @DisplayName("주문생성 실패 - 주문생성 정책 검증 실패 시 어떤 상태 변경도 발생하지 않음.")
    void createOrder_shouldThrowException_whenInvalidOrderRequest() {
        // given
        // 요청 고객
        Member member = customer();
        // 요청 주문물품
        RequestOrderDto invalidRequestItem = RequestOrderDto.builder()
                .productId(5L)
                .optionCode("optionCode")
                .quantity(51) // 주문 정책 제한: 물품 당 최대 주문 수량 초과
                .build();

        // 정책에서 예외 발생
        doThrow(new OrderException(ORDER_ITEM_MAX_QUANTITY_EXCEEDED))
                .when(orderPolicy)
                .validateCreate(eq(List.of(invalidRequestItem)), eq(member));

        // when
        // then
        // 정책이 왜 실패했는지가 아니라, 정책 실패 시 Service가 어떻게 반응하는지 검증.
        // -> OrderService는 정책 검증 실패 시 어떤 부작용도 일으키지 않고 즉시 중단한다
        OrderException e = assertThrows(OrderException.class, () ->
                orderService.createOrder(List.of(invalidRequestItem), member));
        verify(orderRepository, never()).save(any());
        verify(stockCacheService, never()).decrementProductStock(any());
        verify(orderMapper, never()).toResponseDto(any());
        assertEquals(ORDER_ITEM_MAX_QUANTITY_EXCEEDED, e.getErrorCode());

    }

}