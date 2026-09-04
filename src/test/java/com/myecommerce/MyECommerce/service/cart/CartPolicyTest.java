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
import static com.myecommerce.MyECommerce.service.cart.CartPolicy.CART_MAX_SIZE;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.SELLER;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static com.myecommerce.MyECommerce.type.RedisNamespaceType.CART;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
    Member customer(String userId) {
        return Member.builder()
                .userId(userId)
                .roles(List.of(MemberAuthority.builder()
                        .authority(CUSTOMER)
                        .build()))
                .build();
    }

    /** 판매자권한 사용자 */
    Member seller() {
        return Member.builder()
                .roles(List.of(MemberAuthority.builder()
                        .authority(SELLER)
                        .build()))
                .build();
    }

    /** 상품 */
    Product product(Long optionId) {
        return Product.builder()
                .id(optionId)
                .saleStatus(ON_SALE)
                .build();
    }

    /* ----------------------
        Helper Method
       ---------------------- */

    /* ---------------------------
        장바구니추가 정책 통과 Tests
       --------------------------- */

    @Test
    @DisplayName("장바구니추가 정책 통과")
    void validateAdd_shouldPass_whenAllValid() {
        // given
        String userId = "userId";
        Long optionId = 5L;
        Member customer = customer(userId);

        // 사용자 장바구니 현재 사이즈 반환
        given(redisMultiDataService.getSizeOfHashData(CART, userId))
                .willReturn(1L);
        // 상품옵션에 대해 판매중인 상품 반환
        Product product = product(optionId);
        given(productRepository.findByOptionIdAndSaleStatusOnSale(optionId))
                .willReturn(Optional.of(product));

        // when
        // then
        assertDoesNotThrow(() -> cartPolicy.validateAdd(optionId, customer));
    }

    /* ---------------------------
        장바구니추가 정책 경계값 Tests
       --------------------------- */

    // TODO: 장바구니추가 정책 경계값 - 장바구니 최대 사이즈 - 1 일 때 정책 통과
    // TODO: 장바구니추가 정책 경계값 - 장바구니 최대 사이즈 + 1 때 정책 실패

    /* ---------------------------
        장바구니추가 정책 실패 Tests
       --------------------------- */

    @Test
    @DisplayName("장바구니추가 정책 실패 - 이미 장바구니에 최대 사이즈 도달 시 예외발생")
    void validateAdd_shouldReturnCartSizeExceeded_whenCartItemCountExceed() {
        // given
        String userId = "userId";
        Long optionId = 5L;
        Member customer = customer(userId);

        // 사용자 장바구니 현재 사이즈 반환
        given(redisMultiDataService.getSizeOfHashData(CART, userId))
                .willReturn((long) CART_MAX_SIZE);

        // when
        // then
        CartException e = assertThrows(CartException.class, () ->
                cartPolicy.validateAdd(optionId, customer));
        assertEquals(CART_SIZE_EXCEEDED, e.getErrorCode());
        // 이후 정책 미검증
        verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("장바구니추가 정책 실패 - 판매중인 상품이 아니면 예외발생")
    void validateAdd_shouldReturnProductNotOnSale_whenProductNotOnSale() {
        // given
        String userId = "userId";
        Long optionId = 5L;
        Member customer = customer(userId);

        // 사용자 장바구니 현재 사이즈 반환
        given(redisMultiDataService.getSizeOfHashData(CART, userId))
                .willReturn(1L);
        // 상품옵션에 대해 미판매중으로 상품 미반환
        given(productRepository.findByOptionIdAndSaleStatusOnSale(optionId))
                .willReturn(Optional.empty());

        // when
        // then
        ProductException e = assertThrows(ProductException.class, () ->
                cartPolicy.validateAdd(optionId, customer));
        assertEquals(PRODUCT_NOT_ON_SALE, e.getErrorCode());
    }

    // TODO: 장바구니추가 정책 실패 - 비회원 주문 불가 (null 체크)

    @Test
    @DisplayName("장바구니추가 정책 실패 - 고객 외 장바구니 접근 시 예외발생")
    void validateAdd_shouldReturnCartCustomerOnly_whenAccessNotCustomer() {
        // given
        Long optionId = 5L;
        Member invalidMember = seller(); // 고객아님

        // when
        // then
        CartException e = assertThrows(CartException.class, () ->
                cartPolicy.validateAdd(optionId, invalidMember));
        assertEquals(CART_CUSTOMER_ONLY, e.getErrorCode());
        // 이후 정책 미검증
        verifyNoInteractions(redisMultiDataService, productRepository);
    }
}