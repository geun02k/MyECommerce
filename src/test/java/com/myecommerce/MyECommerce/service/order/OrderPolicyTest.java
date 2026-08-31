package com.myecommerce.MyECommerce.service.order;

import com.myecommerce.MyECommerce.dto.order.RequestOrderItemDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.exception.OrderException;
import com.myecommerce.MyECommerce.type.MemberAuthorityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.myecommerce.MyECommerce.exception.errorcode.OrderErrorCode.*;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderPolicyTest {

    @InjectMocks
    private OrderPolicy orderPolicy;

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 고객권한 사용자 */
    Member member(String userId, MemberAuthorityType memberAuthorityType) {
        return Member.builder()
                .userId(userId)
                .roles(List.of(MemberAuthority.builder()
                        .authority(memberAuthorityType)
                        .build()))
                .build();
    }
    Member customer() {
        return member("customer", CUSTOMER);
    }

    /** 요청 주문물품 목록 */
    RequestOrderItemDto requestOrderItem(Long productOptionId, int quantity) {
        return RequestOrderItemDto.builder()
                .productOptionId(productOptionId)
                .quantity(quantity)
                .build();
    }
    RequestOrderItemDto requestOrderItem() {
        return requestOrderItem(1L, 10);
    }

    /** 등록된 상품 옵션 */
    ProductOption registeredOption(Long optionId, int quantity) {
        return ProductOption.builder()
                .id(optionId)
                .optionCode("optionCode")
                .quantity(quantity)
                .price(new BigDecimal("10000"))
                .build();
    }

    /** 유효하지 않은 요청 주문물품 목록 - 최대 주문 가능한 물품 수량 초과 */
    List<RequestOrderItemDto> orderItemsOfMaxCountExceeded() {
        List<RequestOrderItemDto> result = new ArrayList<>();
        int orderItemsMaxCount = 100;

        for(int i = 0; i < orderItemsMaxCount + 1; i++) {
            result.add(requestOrderItem());
        }
        return result;
    }

    /** 유효하지 않은 요청 주문물품 목록 - 중복된 상품옵션 요청 */
    List<RequestOrderItemDto> orderItemsOfDuplicatedOptionRequest() {
        List<RequestOrderItemDto> result = new ArrayList<>();

        for(int i = 0; i < 2; i++) {
            result.add(requestOrderItem());
        }
        return result;
    }

    /* ----------------------
        주문 생성 Tests
       ---------------------- */

    @Test
    @DisplayName("주문생성 정책 통과 - 유효한 정책 요청 시 정책 통과")
    void validateCreate_shouldPass_whenAllValid() {
        // given
        // 요청 고객
        Member member = member("customer", CUSTOMER);
        // 요청 주문물품
        RequestOrderItemDto requestItem = requestOrderItem(1L, 10);
        // 요청 주문물풀 중 DB에 등록되어 있던 옵션
        Map<Long, ProductOption> registeredOption =
                Map.of(1L, registeredOption(1L, 20));

        // when
        // then
        assertDoesNotThrow(() -> orderPolicy.validateCreate(
                        List.of(requestItem), registeredOption, member));
    }

    // TODO: 주문생성 정책 실패 - 비회원 주문 불가

    @Test
    @DisplayName("주문생성 정책 실패 - 고객 외 권한자 주문 불가")
    void validateCreate_shouldThrowException_whenMemberNotCustomerRole() {
        // given
        // 요청 고객
        Member invalidMember = Member.builder()
                .roles(List.of())
                .build(); // 고객 권한 없음

        // when
        // then
        OrderException e = assertThrows(OrderException.class, () ->
                orderPolicy.validateCreate(List.of(), Map.of(), invalidMember));
        assertEquals(ORDER_CUSTOMER_ONLY, e.getErrorCode());
    }

    // TODO: 최소 주문 가능 물품 수량 미달 시 주문 불가 테스트코드 작성

    @Test
    @DisplayName("주문생성 정책 실패 - 최대 주문 가능 물품 수량 초과 시 주문 불가")
    void validateCreate_shouldThrowException_whenOrderCountExceeded() {
        // given
        // 요청 고객
        Member member = customer();
        // 요청 주문물품
        List<RequestOrderItemDto> invalidRequestItems =
                orderItemsOfMaxCountExceeded(); // 최대 주문 가능 물품 수량 초과

        // when
        // then
        OrderException e = assertThrows(OrderException.class, () ->
                orderPolicy.validateCreate(invalidRequestItems, Map.of(), member));
        assertEquals(ORDER_COUNT_EXCEEDED, e.getErrorCode());
    }

    @Test
    @DisplayName("주문생성 정책 실패 - 동일 주문물품 중복 요청 시 주문 불가")
    void validateCreate_shouldThrowException_whenDuplicatedItemsRequest() {
        // given
        // 요청 고객
        Member member = customer();
        // 요청 주문물품
        List<RequestOrderItemDto> invalidRequestItems =
                orderItemsOfDuplicatedOptionRequest(); // 동일 상품옵션의 중복된 물품 요청

        // when
        // then
        OrderException e = assertThrows(OrderException.class, () ->
                orderPolicy.validateCreate(invalidRequestItems, Map.of(), member));
        assertEquals(ORDER_ITEM_REQUEST_DUPLICATED, e.getErrorCode());
    }

    @Test
    @DisplayName("주문생성 정책 실패 - 주문물품의 구매가능 최대수량 초과 시 주문 불가")
    void validateCreate_shouldThrowException_whenMaxQuantityExceededOfOrderItem() {
        // given
        // 요청 고객
        Member member = customer();
        // 요청 주문물품 (최대 구매 가능 수량 초과한 51개 요청)
        int maxQuantity = 50; // 최대 구매 가능 수량
        RequestOrderItemDto invalidRequestItem =
                requestOrderItem(1L,  maxQuantity + 1);

        // when
        // then
        OrderException e = assertThrows(OrderException.class, () ->
                orderPolicy.validateCreate(List.of(invalidRequestItem), Map.of(), member));
        assertEquals(ORDER_ITEM_MAX_QUANTITY_EXCEEDED, e.getErrorCode());

    }

    @Test
    @DisplayName("주문생성 정책 실패 - 등록되지 않은 상품옵션의 주문 요청 시 주문 불가")
    void validateCreate_shouldThrowException_whenProductOptionNotRegistered() {
        // given
        // 요청 고객
        Member member = customer();
        // 요청 주문물품
        RequestOrderItemDto invalidRequestItem = RequestOrderItemDto.builder()
                .productOptionId(1L) // 등록되지 않은 상품옵션
                .quantity(10)
                .build();
        // 요청 주문물품이 DB에 등록되어있지 않음
        Map<Long, ProductOption> emptiedRegisteredOption = Map.of();

        // when
        // then
        OrderException e = assertThrows(OrderException.class, () ->
                orderPolicy.validateCreate(
                        List.of(invalidRequestItem), emptiedRegisteredOption, member));
        assertEquals(PRODUCT_OPTION_NOT_REGISTERED, e.getErrorCode());
    }

    // TODO: 상품 판매상태 검증 로직 추가 후 판매종료, 판매중단 상태의 상품 주문 요청 시 주문 불가 테스트코드 작성

}