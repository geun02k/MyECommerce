package com.myecommerce.MyECommerce.service.cart;

import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.product.Product;
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

import java.util.List;
import java.util.Optional;

import static com.myecommerce.MyECommerce.exception.errorcode.CartErrorCode.CART_CUSTOMER_ONLY;
import static com.myecommerce.MyECommerce.exception.errorcode.CartErrorCode.CART_SIZE_EXCEEDED;
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


    /* ----------------------
        Helper Method
       ---------------------- */

    /** 사용자 장바구니 사이즈 조회 given절 */
    void givenUserCartSize(Member member) {
        given(redisMultiDataService.getSizeOfHashData(CART, member.getUserId()))
                .willReturn(1L);
    }

    /* ----------------------
        장바구니추가정책 Tests
       ---------------------- */

    @Test
    @DisplayName("장바구니추가정책 통과")
    void validateAdd_shouldPass_whenAllValid() {
        // given
        Member customer = customer();
        // 사용자 장바구니 현재 사이즈 반환
        given(redisMultiDataService.getSizeOfHashData(CART, customer.getUserId()))
                .willReturn(1L);
        // 요청한 상품은 판매중인 상품으로 반환
        given(productRepository.findByCodeAndSaleStatus(eq("productCode"), eq(ON_SALE)))
                .willReturn(Optional.of(Product.builder()
                        .code("productCode")
                        .saleStatus(ON_SALE).build()));

        // when
        // then
        assertDoesNotThrow(() ->
                cartPolicy.validateAdd("productCode", customer));

    }

    @Test
    @DisplayName("장바구니추가정책 실패 - 이미 장바구니에 허용된 최대 사이즈 도달 시 예외발생")
    void validateAdd_shouldReturnCartSizeExceeded_whenCartItemCountExceed() {
        // given
        Member customer = customer();

        given(redisMultiDataService.getSizeOfHashData(CART, customer.getUserId()))
                .willReturn((long)CartPolicy.CART_MAX_SIZE);
        // when
        // then
        CartException e = assertThrows(CartException.class, () ->
                cartPolicy.validateAdd("productCode", customer()));
        assertEquals(CART_SIZE_EXCEEDED, e.getErrorCode());

    }

    @Test
    @DisplayName("장바구니추가정책 실패 - 판매중인 상품이 아니면 예외발생")
    void validateAdd_shouldReturnProductNotOnSale_whenProductNotOnSale() {
        // given
        Member customer = customer();
        // 사용자 장바구니 사이즈 (타정책 통과용)
        givenUserCartSize(customer);
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
    @DisplayName("장바구니추가정책 실패 - 고객 외 장바구니 접근 시 예외발생")
    void validateAdd_shouldReturnCartCustomerOnly_whenAccessNotCustomer() {
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