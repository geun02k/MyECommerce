package com.myecommerce.MyECommerce.integration.cart;

import com.myecommerce.MyECommerce.dto.cart.RedisCartDto;
import com.myecommerce.MyECommerce.dto.cart.ResponseCartDetailDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.service.cart.CartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.myecommerce.MyECommerce.type.RedisNamespaceType.CART;
import static com.myecommerce.MyECommerce.type.RedisNamespaceType.STOCK;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CartRetrieveIntegrationTest {

    @Autowired
    CartService cartService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 고객권한 사용자 */
    Member customer() {
        return Member.builder()
                .userId("customer")
                .build();
    }

    /* ------------------
        Helper Method
       ------------------ */

    /** Redis에 사용자 장바구니 저장 */
    void givenCartItemList(String userId) {
        String key = CART + ":" + userId;
        Map<String, RedisCartDto> value = new HashMap<>();

        // 100건 장바구니 상품 옵션 데이터 생성
        for (int i = 1; i <= 100; i++) {
            String productCode = "product" + i;
            String optionCode = "option" + i;
            String hashKey = productCode + ":" + optionCode;

            RedisCartDto redisCartDto = RedisCartDto.builder()
                    .productId((long) i)
                    .productCode(productCode)
                    .optionId((long) i)
                    .optionCode(optionCode)
                    .quantity(1)
                    .build();

            value.put(hashKey, redisCartDto);
        }

        // 장바구니 상품 목록 저장
        redisTemplate.opsForHash().putAll(key, value);
    }

    /** Redis에 재고 캐시 데이터 저장 */
    void givenItemStockCache() {
        Map<String, Integer> stock = new HashMap<>();

        for (int i = 1; i <= 70; i++) {
            String productCode = "product" + i;
            String optionCode = "option" + i;
            String key = STOCK + ":" + productCode + ":" + optionCode;
            int stockValue;

            // 짝수는 재고 있음, 홀수는 재고 없음
            // 100건 중 30건은 판매중단, 종료되어 미등록으로 간주
            if (i % 2 == 0) {
                stockValue = i;
            } else {
                stockValue = 0;
            }

            stock.put(key, stockValue);
        }

        redisTemplate.opsForValue().multiSet(stock);
    }

    /** 장바구니에서 특정 상품 옵션 찾기 */
    ResponseCartDetailDto findItem(List<ResponseCartDetailDto> cart,
                                   String productCode,
                                   String optionCode) {
        for (ResponseCartDetailDto item : cart) {
            if (productCode.equals(item.getProductCode()) &&
                    optionCode.equals(item.getOptionCode())) {
                return item;
            }
        }
        return null;
    }

    /* ------------------
        장바구니 조회 Test
       ------------------ */

    @Test
    @DisplayName("장바구니조회 성공 - 재고 키/장바구니 상품옵션 동일 순서, 품절여부/구매가능수량 검증")
    void retrieveCart_shouldDetermineOutOfStock_whenCartHasItemsOfMixedStock() {
        // given
        // 요청 사용자 DTO
        Member member = customer();

        // 장바구니 상품 옵션 100건 저장
        givenCartItemList(member.getUserId());
        // 장바구니 상품 옵션 재고 저장
        givenItemStockCache();

        // when
        List<ResponseCartDetailDto> response =
                cartService.retrieveCart(member);

        // then
        // 총 장바구니 수 검증
        assertEquals(100, response.size());
        // 품절된 상품 옵션 수 검증
        Long itemsOutOfStock = response.stream()
                .filter(ResponseCartDetailDto::isOutOfStock)
                .count();
        assertEquals(65, itemsOutOfStock);

        // 특정 상품 재고/품절여부 검증
        ResponseCartDetailDto itemAvailable =  // 판매중이고 재고 있음 (70이하 짝수)
                findItem(response, "product38", "option38");
        assertEquals(38, itemAvailable.getAvailableQuantity());
        assertFalse(itemAvailable.isOutOfStock());

        ResponseCartDetailDto itemOutOfStock =  // 판매중이지만 재고 0 (70이하 홀수)
                findItem(response, "product57", "option57");
        assertEquals(0, itemOutOfStock.getAvailableQuantity());
        assertTrue(itemOutOfStock.isOutOfStock());

        ResponseCartDetailDto itemOfDiscontinue = // 판매중단으로 재고 미등록 (71이상)
                findItem(response, "product88", "option88");
        assertEquals(0, itemOfDiscontinue.getAvailableQuantity());
        assertTrue(itemOfDiscontinue.isOutOfStock());

        // 장바구니 수량 보존 검증
        assertEquals(1, itemAvailable.getQuantity());
    }
}
