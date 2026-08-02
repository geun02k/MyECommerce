package com.myecommerce.MyECommerce.service.order;

import com.myecommerce.MyECommerce.dto.order.RequestOrderDto;
import com.myecommerce.MyECommerce.dto.order.ResponseOrderDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.order.OrderItem;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.mapper.OrderMapper;
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.service.stock.StockCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
* TODO: Redis 호출에서 발생한 예외가 트랜잭션 밖으로 전파되어 DB rollback을 유발하므로 Redis 캐시 갱신 실패 정책 결정 필요.
  현재는 Redis 실패 시 주문 전체를 실패 처리한다.
  하지만 재고 캐시는 주문의 진실이 아니기 떄문에 Redis 실패 떄문에 주문을 실패시키는 것은 과한 정책일 수 있다.
  주문 성공률(가용성)을 높이기 위해 캐시 갱신을 트랜잭션 외부로 분리하거나,
  주문 완료 이벤트 기반 비동기 처리 및 실패 재처리를 고려한다.
  1. 비동기 처리 (Events): 주문 성공 이벤트만 발행하고, Redis 업데이트는 별도의 이벤트 핸들러가 처리하게 한다. (실패 시 재시도 가능)
  2. Soft 업데이트: 캐시 업데이트에 실패하더라도 로그만 남기고 주문은 완료시킨다. 어차피 재고 조회 시 캐시에 없으면 DB에서 다시 읽어오도록(Look-aside) 설계하면 해결된다.*/
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderPolicy orderPolicy;

    private final StockCacheService stockCacheService;

    private final ProductOptionRepository productOptionRepository;
    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    /** 주문 생성 **/
    @Transactional
    public ResponseOrderDto createOrder(List<RequestOrderDto> requestOrder, Member member) {
        // 1. 주문 요청 옵션 조회 (재고 차감을 위해 비관적 락)
        Map<Long, ProductOption> registeredOptions =
                findOrderRequestOptionsWithLock(requestOrder);

        // 2. 정책검증
        orderPolicy.validateCreate(requestOrder, registeredOptions, member);

        // 2. 주문 물품 목록 생성
        List<OrderItem> orderItems =
                createOrderItems(requestOrder, registeredOptions);

        // 3. 주문 생성 및 저장
        Order order = Order.createOrder(orderItems, member);
        Order savedOrder = orderRepository.save(order);

        // 4. 상품옵션 목록 재고 차감 (dirty checking)
        decreaseStockOfProductOptions(savedOrder.getItems(), registeredOptions);

        // 5. 재고 캐시 데이터 차감 (원자적 감소)
        stockCacheService.decrementProductStock(savedOrder.getItems());

        // FIXME: 로직추가 -> 6. 장바구니 캐시 데이터 제거
        // 주문 생성 경로에 따른 장바구니 처리 필요
        // 바구니 주문(CART)인 경우 주문 완료 후 장바구니 데이터를 제거해야 한다.
        // 단, 상품 상세에서 바로 주문(DIRECT)하는 경우 장바구니 데이터를 유지해야 하므로 문 생성 출처를 구분하는 구조 검토 필요.
        // 7. Entity -> responseDTO로 변환해 반환
        return orderMapper.toResponseDto(savedOrder);
    }

    // 상품옵션 목록 재고 차감
    private void decreaseStockOfProductOptions(List<OrderItem> orderItems,
                                               Map<Long, ProductOption> registeredOptions) {
        for(OrderItem item : orderItems) {
            // 재고 차감 대상 옵션
            Long optionId = item.getOption().getId();
            ProductOption option = registeredOptions.get(optionId);

            // 재고 차감
            int currentStock = option.getQuantity();
            int orderQuantity = item.getQuantity();
            option.setQuantity(currentStock - orderQuantity);
        }
    }

    // 주문 요청 옵션 조회
    private Map<Long, ProductOption> findOrderRequestOptionsWithLock(
            List<RequestOrderDto> requestOrder) {
        // 상품 옵션 아이디 목록 생성
        List<Long> optionIds = requestOrder.stream()
                .map(RequestOrderDto::getProductOptionId)
                .toList();
        // 상품 옵션 목록 조회 (트랜잭션 비관적 락)
        List<ProductOption> requestOptionList =
                productOptionRepository.findByIdIn(optionIds);
        // List -> Map 변환
        return requestOptionList.stream()
                .collect(Collectors.toMap(
                        ProductOption::getId, option -> option));
    }

    // 주문 물품 목록 생성
    private List<OrderItem> createOrderItems(List<RequestOrderDto> requestOrder,
                                             Map<Long, ProductOption> registeredOptions) {
        List<OrderItem> orderItems = new ArrayList<>();

        for(RequestOrderDto requestItem : requestOrder) {
            // 요청한 주문물품 대상 옵션
            Long optionId = requestItem.getProductOptionId();
            ProductOption registeredOption = registeredOptions.get(optionId);

            // 주문 물품 단건 생성
            OrderItem item = OrderItem.createOrderItem(
                    registeredOption, requestItem.getQuantity());

            // 주문 물품 목록에 추가
            orderItems.add(item);
        }

        return orderItems;
    }

}
