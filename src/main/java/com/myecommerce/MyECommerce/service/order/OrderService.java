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
import com.myecommerce.MyECommerce.vo.order.ProductOptionKey;
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
        Map<ProductOptionKey, ProductOption> registeredOptions =
                findOrderRequestOptionsWithLock(requestOrder);

        // 2. 정책검증
        // TODO: 정책 검증 시 조회 데이터는 Service에서 만들어서 전달하도록 수정해 성능 개선 필요
        orderPolicy.validateCreate(requestOrder, member);

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
                                              Map<ProductOptionKey, ProductOption> registeredOptions) {
        for(OrderItem item : orderItems) {
            // 옵션 키 생성
            ProductOptionKey optionKey = new ProductOptionKey(
                    item.getProduct().getId(), item.getOption().getOptionCode());

            // 재고 차감 대상 옵션
            ProductOption option = registeredOptions.get(optionKey);

            // 재고 차감
            int currentStock = option.getQuantity();
            int orderQuantity = item.getQuantity();
            option.setQuantity(currentStock - orderQuantity);
        }
    }

    // 주문 요청 옵션 조회
    private Map<ProductOptionKey, ProductOption> findOrderRequestOptionsWithLock(
            List<RequestOrderDto> requestOrder) {
        // 상품 옵션 조회 Key 목록 생성
        List<ProductOptionKey> optionKeys = createOptionKeys(requestOrder);
        // 상품 옵션 목록 조회 (트랜잭션 비관적 락)
        List<ProductOption> options =
                productOptionRepository.findOptionsWithLock(optionKeys);
        // List -> Map 변환
        return options.stream().collect(Collectors.toMap(
                option -> new ProductOptionKey(
                        option.getProduct().getId(), option.getOptionCode()),
                option -> option));
    }

    // 주문 물품 목록 생성
    private List<OrderItem> createOrderItems(List<RequestOrderDto> requestOrder,
                                             Map<ProductOptionKey, ProductOption> registeredOptions) {
        List<OrderItem> orderItems = new ArrayList<>();

        for(RequestOrderDto requestItem : requestOrder) {
            // 요청한 주문물품 대상 옵션
            ProductOptionKey optionKey = new ProductOptionKey(
                    requestItem.getProductId(), requestItem.getOptionCode());
            ProductOption registeredOption = registeredOptions.get(optionKey);

            // 주문 물품 단건 생성
            OrderItem item = OrderItem.createOrderItem(
                    registeredOption, requestItem.getQuantity());

            // 주문 물품 목록에 추가
            orderItems.add(item);
        }

        return orderItems;
    }

    // 옵션 키 생성
    private List<ProductOptionKey> createOptionKeys(List<RequestOrderDto> requestOrder) {
        return requestOrder.stream()
                .map(request -> new ProductOptionKey(
                        request.getProductId(), request.getOptionCode()))
                .toList();
    }

}
