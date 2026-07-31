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

        // 6. Entity -> responseDTO로 변환해 반환
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
