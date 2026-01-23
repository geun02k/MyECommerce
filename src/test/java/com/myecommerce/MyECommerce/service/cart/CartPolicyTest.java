package com.myecommerce.MyECommerce.service.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.dto.cart.RedisCartDto;
import com.myecommerce.MyECommerce.dto.cart.RequestCartDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.exception.CartException;
import com.myecommerce.MyECommerce.exception.ProductException;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import com.myecommerce.MyECommerce.service.redis.RedisMultiDataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.myecommerce.MyECommerce.exception.errorcode.CartErrorCode.CART_CUSTOMER_ONLY;
import static com.myecommerce.MyECommerce.exception.errorcode.ProductErrorCode.PRODUCT_NOT_ON_SALE;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.SELLER;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static com.myecommerce.MyECommerce.type.RedisNamespaceType.CART;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CartPolicyTest {

    @Mock
    private RedisMultiDataService redisMultiDataService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CartPolicy cartPolicy;

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

    /** 유효한 장바구니 요청 */
    RequestCartDto requestCartDto() {
        return RequestCartDto.builder()
                .productCode("productCode")
                .optionCode("optionCode")
                .quantity(1)
                .build();
    }

    /** 사용자 장바구니 전체 조회 **/
    Map<Object, Object> userCartOfOneItem(Object cartItem) {
        // Redis 장바구니 상품목록
        Map<Object, Object> userCart = new HashMap<>();
        userCart.put("tester", cartItem);

        return userCart;
    }

    /** 장바구니 상품 단건 */
    Object cartItem() {
        // Redis 장바구니 상품정보 단건
        return new Object() {
            String productCode = "productCode";
            String optionCode = "optionCode";
            BigDecimal price = new BigDecimal("10000");
            int quantity = 1;
        };
    }

    /* ----------------------
        Helper Method
       ---------------------- */

    /** 사용자 장바구니 전체 조회 given절 */
    void givenUserCart(Member member) {
        Object cartItem = cartItem();

        // Redis 장바구니의 상품옵션목록 Map<Object, Object> 반환
        given(redisMultiDataService.getHashEntries(eq(CART), eq(member.getUserId())))
                .willReturn(userCartOfOneItem(cartItem));
        // Redis 장바구니 상품정보 단건 변환
        given(objectMapper.convertValue(
                eq(cartItem), eq(RedisCartDto.class)))
                .willReturn(RedisCartDto.builder()
                        .productCode("productCode")
                        .optionCode("optionCode")
                        .price(new BigDecimal(10000))
                        .quantity(1)
                        .build());
    }

    /* ----------------------
        장바구니추가정책 Tests
       ---------------------- */

    @Test
    @DisplayName("장바구니추가 실패 - 판매중인 상품이 아니면 예외발생")
    void addCart_shouldReturnProductNotOnSale_whenProductNotOnSale() {
        // given
        Member customer = customer();
        // 장바구니에 이미 담긴 상품 (타정책 통과용)
        givenUserCart(customer);
        // 판매중단된 상품으로 조회되지 않음
        given(productRepository.findByCodeAndSaleStatus(eq("productCode"), eq(ON_SALE)))
                .willReturn(Optional.empty());

        // when
        // then
        ProductException e =assertThrows(ProductException.class, () ->
                cartPolicy.validateAdd("productCode", customer));
        assertEquals(PRODUCT_NOT_ON_SALE, e.getErrorCode());
        // 판매상태 조회 실행여부 검증
        verify(productRepository, times(1))
                .findByCodeAndSaleStatus("productCode", ON_SALE);
    }

    @Test
    @DisplayName("장바구니추가 실패 - 고객 외 장바구니 접근 시 예외발생")
    void addCart_shouldReturnCartCustomerOnly_whenAccessNotCustomer() {
        // given
        Member invalidMember = Member.builder()
                .roles(List.of(MemberAuthority.builder()
                        .authority(SELLER).build())).build();; // 고객아님

        // when
        // then
        CartException e = assertThrows(CartException.class, () ->
                cartPolicy.validateAdd("productCode", invalidMember));
        assertEquals(CART_CUSTOMER_ONLY, e.getErrorCode());
    }
}