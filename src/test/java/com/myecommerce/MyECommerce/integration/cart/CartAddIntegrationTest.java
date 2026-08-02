package com.myecommerce.MyECommerce.integration.cart;

import com.myecommerce.MyECommerce.dto.cart.RequestCartDto;
import com.myecommerce.MyECommerce.dto.cart.ResponseCartDto;
import com.myecommerce.MyECommerce.dto.product.RequestProductDto;
import com.myecommerce.MyECommerce.dto.product.RequestProductOptionDto;
import com.myecommerce.MyECommerce.dto.product.ResponseProductDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.integration.config.TestAuditingConfig;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.service.cart.CartService;
import com.myecommerce.MyECommerce.service.product.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.ProductCategoryType.WOMEN_CLOTHING;
import static com.myecommerce.MyECommerce.type.RedisNamespaceType.CART;
import static com.myecommerce.MyECommerce.type.RedisNamespaceType.STOCK;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(TestAuditingConfig.class)
public class CartAddIntegrationTest {

    @Autowired
    ProductService productService;

    @Autowired
    CartService cartService;

    @Autowired
    ProductOptionRepository productOptionRepository;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    /* ------------------
        Test Fixtures
       ------------------ */

    private final String USER_ID = "customer";
    // 등록한 재고 캐시 키목록
    List<String> stockCacheKeys = new ArrayList<>();
    private final String cartKey = CART + ":" + USER_ID;

    @AfterEach
    void cleanUp() {
        // 상품등록으로 인한 redis 재고 캐시 삭제
        redisTemplate.delete(stockCacheKeys);
        redisTemplate.delete(cartKey);
    }

    /** 셀러권한 사용자 */
    Member seller(Long sellerId) {
        return Member.builder()
                .id(sellerId)
                .userId("seller" + sellerId)
                .build();
    }

    /** 고객권한 사용자 */
    Member customer() {
        return Member.builder()
                .userId(USER_ID)
                .roles(List.of(MemberAuthority.builder()
                        .authority(CUSTOMER)
                        .build()))
                .build();
    }

    /** 상품등록 */
    ProductOption registerProduct(Long sellerId, String productCode, String optionCode) {
        // 상품등록
        RequestProductDto requestProduct = requestProductDto(productCode, optionCode);
        Member seller = seller(sellerId);
        ResponseProductDto savedProduct =
                productService.registerProduct(requestProduct, seller);
        // 등록한 상품옵션조회
        ProductOption option =
                productOptionRepository.findByProductId(savedProduct.getId())
                        .get(0);

        // TODO: Key 포맷 변경 시 정상 동작하지 않을 수 있으므로, 운영 코드의 RedisKeyGenerator 또는 Enum/Constant 메서드를 공유해서 사용하는 것을 권장
        // redis 캐시 재고 삭제를 위한 키 생성
        String stockKey = STOCK + ":" + option.getId();
        stockCacheKeys.add(stockKey);

        return option;
    }

    /** 상품등록 요청 상품 옵션 DTO 생성 */
    RequestProductOptionDto requestOptionDto(String optionCode) {
        return RequestProductOptionDto.builder()
                .optionCode(optionCode)
                .optionName("옵션명")
                .price(new BigDecimal("10000"))
                .quantity(5)
                .build();
    }

    /** 상품등록 요청 상품 DTO 생성 */
    RequestProductDto requestProductDto(String productCode, String optionCode) {
        return RequestProductDto.builder()
                .code(productCode)
                .name("상품")
                .category(WOMEN_CLOTHING)
                .options(List.of(requestOptionDto(optionCode)))
                .build();
    }

    /** 장바구니 상품추가 요청 DTO 생성 */
    RequestCartDto requestCartDto(Long productOptionId) {
        return RequestCartDto.builder()
                .productOptionId(productOptionId)
                .quantity(1)
                .build();
    }

    /* ------------------
        장바구니 추가 Test
       ------------------ */

    // 기존 설계 문제 해결
    @Test
    @DisplayName("장바구니 상품추가 성공 - 동일한 상품코드가 존재 시에도 특정 셀러의 상품 장바구니에 추가")
    void addCart_shouldAddItem_whenDuplicateProductCodeExists() {
        // given
        // 동일한 상품코드로 두 셀러가 상품 등록
        registerProduct(1L, "productCode", "option1");
        ProductOption productOption = registerProduct(2L, "productCode", "option2");
        // 장바구니 추가를 위한 객체 생성
        Member member = customer();
        RequestCartDto requestCartDto = requestCartDto(productOption.getId());

        // when
        ResponseCartDto response = cartService.addCart(requestCartDto, member);
        // then
        assertNotNull(response.getOptionId());
        assertEquals(2L, response.getSellerId());
        assertEquals("productCode", response.getProductCode());
        assertEquals("option2", response.getOptionCode());
    }

//    // 기존 설계 문제 재현
//    @Test
//    @Disabled("기존 장바구니 상품 추가 실패 문제 재현 후 버그 수정 완료로 테스트 제외")
//    @DisplayName("장바구니 추가 실패 - 동일한 상품코드가 존재하면 상품 단건 조회 실패")
//    void addCart_shouldFailedSearch_whenDuplicateProductCodeExists() {
//        // given
//        // 동일한 상품코드로 두 셀러가 상품 등록
//        registerProduct(1L, "productCode", "option1");
//        registerProduct(2L, "productCode", "option2");
//        // 장바구니 추가를 위한 객체 생성
//        Member member = customer();
//        RequestCartDto requestCartDto =
//                requestCartDto("productCode", "option2");
//
//        // when
//        // then
//        // productCode 단일 조회 결과가 2건이 되어 예외 발생
//        assertThrows(IncorrectResultSizeDataAccessException.class, () ->
//                cartService.addCart(requestCartDto, member));
//    }

}
