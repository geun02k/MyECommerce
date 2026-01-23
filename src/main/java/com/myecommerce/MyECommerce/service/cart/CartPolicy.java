package com.myecommerce.MyECommerce.service.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.dto.cart.RedisCartDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.exception.CartException;
import com.myecommerce.MyECommerce.exception.ProductException;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import com.myecommerce.MyECommerce.service.redis.RedisMultiDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import static com.myecommerce.MyECommerce.exception.errorcode.CartErrorCode.CART_CUSTOMER_ONLY;
import static com.myecommerce.MyECommerce.exception.errorcode.CartErrorCode.CART_SIZE_EXCEEDED;
import static com.myecommerce.MyECommerce.exception.errorcode.ProductErrorCode.PRODUCT_NOT_ON_SALE;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static com.myecommerce.MyECommerce.type.RedisNamespaceType.CART;

@Component
@RequiredArgsConstructor
public class CartPolicy {

    public static final int CART_MAX_SIZE = 100;

    private final RedisMultiDataService redisMultiDataService;

    private final ProductRepository productRepository;

    private final ObjectMapper objectMapper;


    /** 장바구니 추가 정책 **/
    public void validateAdd(String productCode, Member member) {
        // 고객 한정 장바구니 접근 제한
        validateCartAccessPolicy(member);
        // 장바구니 물품 100건 제한
        checkUserCartSizePolicy(member.getUserId());
        // 장바구니에 추가 가능한 상품은 판매중인 경우로 제한
        validateOnSaleProductPolicy(productCode);
    }

    // 장바구니 제품 수량 체크 정책
    private void checkUserCartSizePolicy(String userId) {
        Long cartItemCount = redisMultiDataService.getSizeOfHashData(CART, userId);

        if (cartItemCount >= CART_MAX_SIZE) {
            throw new CartException(CART_SIZE_EXCEEDED, CART_MAX_SIZE);
        }
    }

    // 상품 판매상태(ON_SALE) 검증 정책
    private void validateOnSaleProductPolicy(String productCode) {
        productRepository.findByCodeAndSaleStatus(productCode, ON_SALE)
                .orElseThrow(() -> new ProductException(PRODUCT_NOT_ON_SALE));
    }

    // 고객 한정 장바구니 접근 제한 정책
    private void validateCartAccessPolicy(Member member) {
        boolean hasCustomerRole = false;

        if (member != null) {
            hasCustomerRole = member.getRoles().stream()
                    .map(MemberAuthority::getAuthority)
                    .anyMatch(authority -> authority.equals(CUSTOMER));
        }

        if (!hasCustomerRole) {
            throw new CartException(CART_CUSTOMER_ONLY);
        }
    }

}
