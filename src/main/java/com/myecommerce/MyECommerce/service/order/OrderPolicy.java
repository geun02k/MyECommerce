package com.myecommerce.MyECommerce.service.order;

import com.myecommerce.MyECommerce.dto.order.RequestOrderDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.exception.OrderException;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.myecommerce.MyECommerce.exception.errorcode.OrderErrorCode.*;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;

@Component
@RequiredArgsConstructor
public class OrderPolicy {

    private final static int ORDER_ITEM_MAX_CNT = 100; // 물품 주문가능 최대 수량
    private final static int ITEM_MAX_QUANTITY = 50; // 물품 당 최대 주문 수량

    private final ProductRepository productRepository;

    /** 주문 생성 정책 **/
    public void validateCreate(List<RequestOrderDto> orderItemList,
                               Member member) {
        // 1, 서비스 정책 검증 (받아도 되는 요청인가?)
        // 주문 접근 권한 제한
        validateOrderAccessPolicy(member);
        // 최대 주문 가능 물품 수량 제한
        validateMaxOrderItemsPolicy(orderItemList);
        // 물품 중복 요청 거부
        validateDuplicatedItemRequestPolicy(orderItemList);

        // 2. 도메인 규칙 검증 (서비스 로직 수행 가능한가?)
        // 요청 물품별 최대 수량 제한
        validateQuantityOfItemPolicy(orderItemList);

        // 도메인 규칙 검증 전 조회
        // 상품 ID 목록 생성
        List<Long> productIds = getProductIds(orderItemList);
        // 등록된 상품 ID 목록 조회
        List<Product> registeredProducts = productRepository.findByIdIn(productIds);

        // 3. DB 조회 필요 도메인 규칙 검증
        // 등록된 상품 옵션 한정 주문 제한
        validateNotRegisteredProductOptionPolicy(
                orderItemList, productIds, registeredProducts);
    }

    // 주문 접근 권한 제한 정책
    private void validateOrderAccessPolicy(Member member) {
        // 1. 비회원 제한
        if(member == null) {
            throw new OrderException(MEMBER_NOT_LOGGED_IN);
        }

        // 2. 고객 외 권한 접근 제한
        boolean hasCustomerRole = false;

        hasCustomerRole = member.getRoles().stream()
                .map(MemberAuthority::getAuthority)
                .anyMatch(authority ->
                        authority.equals(CUSTOMER));

        if (!hasCustomerRole) {
            throw new OrderException(ORDER_CUSTOMER_ONLY);
        }
    }

    // 최대 주문 가능 물품 수량 제한 정책
    private void validateMaxOrderItemsPolicy(List<RequestOrderDto> orderItemList) {
        if (ORDER_ITEM_MAX_CNT < orderItemList.size()) {
            throw new OrderException(ORDER_COUNT_EXCEEDED , ORDER_ITEM_MAX_CNT);
        }
    }

    // 물품 중복 요청에 대한 정책
    private void validateDuplicatedItemRequestPolicy(
            List<RequestOrderDto> orderItemList) {

        // 중복 제거된 물품 목록 set
        Set<String> deduplicatedSet = orderItemList.stream()
                .map(this::createUniqueItemKey)
                .collect(Collectors.toSet());

        // 중복 요청 거부
        if (deduplicatedSet.size() != orderItemList.size()) {
            throw new OrderException(ORDER_ITEM_REQUEST_DUPLICATED);
        }
    }

    // 요청 물품 수량 제한 정책
    private void validateQuantityOfItemPolicy(List<RequestOrderDto> orderItemList) {
        // 1. 요청 최대 수량 검증
        long itemsAboveMinQuantity = orderItemList.stream()
                .filter(item ->
                        item.getQuantity() > ITEM_MAX_QUANTITY)
                .count();
        if(itemsAboveMinQuantity > 0) {
            throw new OrderException(ORDER_ITEM_MAX_QUANTITY_EXCEEDED, ITEM_MAX_QUANTITY);
        }
    }

    // 상품 옵션 정책
    private void validateNotRegisteredProductOptionPolicy(
            List<RequestOrderDto> orderItemList,
            List<Long> productIds,
            List<Product> registeredProducts) {
        // 1. 등록되지 않은 상품 요청 거부
        if(productIds.size() != registeredProducts.size()) {
            throw new OrderException(PRODUCT_NOT_REGISTERED);
        }

        // 2. 등록되지 않은 상품 옵션 요청 거부
        // TODO: 요청 아이템(R) × 상품(P) × 옵션(O) 순회 구조 = O(R^2*P) → 조회용 자료구조로 개선 필요
        for(RequestOrderDto orderItem : orderItemList) {
            Long productId = orderItem.getProductId();
            String optionCode = orderItem.getOptionCode();

            // 등록된 옵션에서 요청 옵션 찾기
            boolean isOptionFound = false;
            for(Product product : registeredProducts) {
                // 요청 옵션과 일치되는 등록된 옵션
                Optional<ProductOption> registeredOption = product.getOptions().stream()
                        .filter(option ->
                                productId.equals(product.getId()) && optionCode.equals(option.getOptionCode()))
                        .findFirst();

                // 등록된 옵션과 요청 옵션 일치 시 true 반환 및 반복문 탈출
                if(registeredOption.isPresent()) {
                   isOptionFound = true;
                   break;
                }
            }

            // 등록되지 않은 물품 주문 요청 거부
            if(!isOptionFound) {
                throw new OrderException(PRODUCT_OPTION_NOT_REGISTERED);
            }
        }
    }

    // 상품 ID 목록 반환
    private List<Long> getProductIds(List<RequestOrderDto> orderItems) {
        return orderItems.stream()
                .map(RequestOrderDto::getProductId)
                .distinct()
                .toList();
    }

    // 요청 물품 구분 키 생성
    private String createUniqueItemKey(Long productId, String optionCode) {
        return productId + "-" + optionCode;
    }
    private String createUniqueItemKey(RequestOrderDto item) {
        return createUniqueItemKey(item.getProductId(), item.getOptionCode());
    }

}
