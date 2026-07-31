package com.myecommerce.MyECommerce.service.order;

import com.myecommerce.MyECommerce.dto.order.RequestOrderDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.exception.OrderException;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
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
    Member customer() {
        return Member.builder()
                .userId("customer")
                .roles(List.of(MemberAuthority.builder()
                        .authority(CUSTOMER)
                        .build()))
                .build();
    }

    /** 요청 주문물품 목록 */
    RequestOrderDto requestOrderItem() {
        return RequestOrderDto.builder()
                .productOptionId(1L)
                .quantity(10)
                .build();
    }

    /** 유효하지 않은 요청 주문물품 목록 - 최대 주문 가능한 물품 수량 초과 */
    List<RequestOrderDto> orderItemsOfMaxCountExceeded() {
        List<RequestOrderDto> result = new ArrayList<>();
        int orderItemsMaxCount = 100;

        for(int i = 0; i < orderItemsMaxCount + 1; i++) {
            result.add(requestOrderItem());
        }
        return result;
    }

    /** 유효하지 않은 요청 주문물품 목록 - 중복된 상품옵션 요청 */
    List<RequestOrderDto> orderItemsOfDuplicatedOptionRequest() {
        List<RequestOrderDto> result = new ArrayList<>();

        for(int i = 0; i < 2; i++) {
            result.add(requestOrderItem());
        }
        return result;
    }

    /** productRepository.findByIdIn() 예상 결과 */
    private List<Product> expectedProductFoundByIdIn() {
        return List.of(Product.builder()
                .id(1L)
                .code("productCode")
                .options(List.of(ProductOption.builder()
                        .optionCode("optionCode")
                        .quantity(20)
                        .build()))
                .build());
    }

    /* ------------------
        Helper Methods
       ------------------ */
    /* ----------------------
        주문 생성 Tests
       ---------------------- */
    @Test
    @DisplayName("주문생성 정책 통과 - 유효한 정책 요청 시 정책 통과")
    void validateCreate_shouldPass_whenAllValid() {
        // given
        // 요청 고객
        Member member = Member.builder()
                .userId("customer")
                .roles(List.of(MemberAuthority.builder()
                        .authority(CUSTOMER)
                        .build()))
                .build();
        // 요청 주문물품
        RequestOrderDto requestItem = RequestOrderDto.builder()
                .productOptionId(1L)
                .quantity(10)
                .build();
        // 요청 주문물풀 중 DB에 등록되어 있던 주문물품(옵션)
        ProductOption option = ProductOption.builder()
                .id(1L)
                .quantity(20)
                .build();
        Map<Long, ProductOption> registeredOption =
                Collections.singletonMap(option.getId(), option);

        // when
        // then
        assertDoesNotThrow(() ->
                orderPolicy.validateCreate(
                        List.of(requestItem), registeredOption, member));
    }

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
        List<RequestOrderDto> invalidRequestItems =
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
        List<RequestOrderDto> invalidRequestItems =
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
        // 요청 주문물품
        RequestOrderDto invalidRequestItem = RequestOrderDto.builder()
                .productOptionId(1L)
                .quantity(51) // 주문물품의 구매가능 최대수량 초과
                .build();

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
        RequestOrderDto invalidRequestItem = RequestOrderDto.builder()
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

    // TODO: 판매종료, 판매중단 상태의 상품 주문 요청 시 주문 불가 테스트코드 작성

}