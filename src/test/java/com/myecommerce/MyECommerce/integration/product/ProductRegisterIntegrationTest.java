package com.myecommerce.MyECommerce.integration.product;

import com.myecommerce.MyECommerce.dto.product.RequestProductDto;
import com.myecommerce.MyECommerce.dto.product.RequestProductOptionDto;
import com.myecommerce.MyECommerce.dto.product.ResponseProductDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.exception.ProductException;
import com.myecommerce.MyECommerce.integration.config.TestAuditingConfig;
import com.myecommerce.MyECommerce.service.product.ProductService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.myecommerce.MyECommerce.exception.errorcode.ProductErrorCode.PRODUCT_OPTION_CODE_ALREADY_REGISTERED;
import static com.myecommerce.MyECommerce.type.ProductCategoryType.WOMEN_CLOTHING;
import static com.myecommerce.MyECommerce.type.RedisNamespaceType.STOCK;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Import(TestAuditingConfig.class)
public class ProductRegisterIntegrationTest {

    @Autowired
    ProductService productService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    /* ------------------
        Test Fixtures
       ------------------ */

    @AfterEach
    void cleanUp() {
        // 상품등록으로 인한 redis 재고 캐시 삭제
        redisTemplate.delete(String.valueOf(STOCK));
    }

    /** 셀러권한 사용자 */
    Member seller(Long sellerId) {
        return Member.builder()
                .id(sellerId)
                .userId("seller" + sellerId)
                .build();
    }

    /** 상품등록 */
    void registerProduct(Long sellerId, String productCode) {
        RequestProductDto requestProduct = requestProductDto(productCode);
        Member seller = seller(sellerId);

        productService.registerProduct(requestProduct, seller);
    }

    /** 상품등록 요청 상품 옵션 DTO 생성 */
    RequestProductOptionDto requestOptionDto() {
        return RequestProductOptionDto.builder()
                .optionCode("optionCode")
                .optionName("옵션명")
                .price(new BigDecimal("10000"))
                .quantity(5)
                .build();
    }

    /** 상품등록 요청 상품 DTO 생성 */
    RequestProductDto requestProductDto(String productCode) {
        return RequestProductDto.builder()
                .code(productCode)
                .name("상품")
                .category(WOMEN_CLOTHING)
                .options(List.of(requestOptionDto()))
                .build();
    }

    /* ------------------
        상품등록 Test
       ------------------ */

    // 기존 설계 문제 해결
    @Test
    @DisplayName("상품등록 성공 - 다른 판매자의 동일 상품코드 존재시에도 상품등록 가능")
    void registerProduct_shouldRegister_whenExistsDuplicateProductCodeOfOtherSellerRegistered() {
        // given
        // 셀러가 상품 등록
        registerProduct(1L, "productCode");

        Member seller = seller(2L);
        RequestProductDto requestProductDto = requestProductDto("productCode");

        // when
        // 다른 셀러가 동일 상품코드로 상품을 등록했더라도 셀러별 상품코드는 유니크하므로 등록 가능
        ResponseProductDto response = productService.registerProduct(requestProductDto, seller);

        // then
        assertNotNull(response.getId());
        assertEquals(2L, response.getSeller());
        assertEquals("productCode", response.getCode());
    }

    // TODO: 상품등록 통합테스트 - Redis 재고 캐시 생성 검증 테스트 필요성 검토.
    // 캐시가 도메인 결과인지 단순 조회 최적화 용도인지에 따라 테스트 범위 결정.
    // 기존 설계 문제 재현
    // 서로 다른 판매자가 동일한 productCode를 사용할 수 있음에도
    // 상품옵션 중복 조회 시 seller 조건이 없어 등록에 실패하는 문제를 검증한다.
    @Test
    @Disabled("기존 상품등록 실패 문제 재현 후 버그 수정 완료로 테스트 제외")
    @DisplayName("상품등록 실패 - 다른 판매자의 동일 상품코드를 중복 상품으로 판단하여 예외 발생")
    void registerProduct_shouldFail_whenExistsDuplicateProductCodeOfOtherSellerRegistered() {
        // given
        // 셀러가 상품 등록
        registerProduct(1L, "productCode");

        Member seller = seller(2L);
        RequestProductDto requestProductDto = requestProductDto("productCode");

        // when
        // then
        // 다른 셀러가 동일 상품코드로 상품 등록 시 에러발생
        ProductException e = assertThrows(ProductException.class, () ->
                productService.registerProduct(requestProductDto, seller));
        assertEquals(PRODUCT_OPTION_CODE_ALREADY_REGISTERED, e.getErrorCode());
    }

}
