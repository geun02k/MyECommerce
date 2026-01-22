package com.myecommerce.MyECommerce.service.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.dto.cart.RedisCartDto;
import com.myecommerce.MyECommerce.dto.cart.RequestCartDto;
import com.myecommerce.MyECommerce.dto.cart.ResponseCartDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.exception.CartException;
import com.myecommerce.MyECommerce.exception.ProductException;
import com.myecommerce.MyECommerce.mapper.RedisCartMapper;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import com.myecommerce.MyECommerce.service.redis.RedisMultiDataService;
import com.myecommerce.MyECommerce.service.redis.RedisSingleDataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class CartServiceTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RedisCartMapper redisCartMapper;

    @Mock
    private RedisSingleDataService redisSingleDataService;
    @Mock
    private RedisMultiDataService redisMultiDataService;

    @Mock
    private ProductOptionRepository productOptionRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 고객권한 사용자 */
    Member member() {
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

    /* ----------------------
        장바구니추가 Tests
       ---------------------- */

    @Test
    @DisplayName("장바구니추가 성공 - 장바구니에 동일 상품 존재하는 경우")
    void successAddCartIfTheSameProductExists() {
        // given
        // 요청 장바구니 상품 정보
        RequestCartDto requestCartDto = RequestCartDto.builder()
                .productCode("productCode")
                .optionCode("optionCode")
                .quantity(1)
                .build();
        // 요청 사용자 정보
        Member member = member();

        // Redis key
        String redisKey = member.getUserId();
        String redisHashKey = requestCartDto.getProductCode()
                + ":" + requestCartDto.getOptionCode();


        // 요청 상품옵션에 대한 장바구니 조회 정보
        Object redisCartObj = new Object() {
            String productCode = "productCode";
            String optionCode = "optionCode";
            BigDecimal price = new BigDecimal("10000");
            int quantity = 1;
        };
        RedisCartDto targetRedisCartDto = RedisCartDto.builder()
                .productCode("productCode")
                .optionCode("optionCode")
                .price(new BigDecimal("10000"))
                .quantity(1)
                .build();

        // DB 상품정보
        RedisCartDto foundOptionDto = RedisCartDto.builder()
                .productCode("productCode")
                .optionCode("optionCode")
                .price(new BigDecimal(10000))
                .quantity(500)
                .build();

        // Redis 장바구니 상품정보 단건
        Object cartItemOfUserCart = new Object() {
            String productCode = "productCode";
            String optionCode = "optionCode";
            BigDecimal price = new BigDecimal("10000");
            int quantity = 1;
        };
        // Redis 장바구니 상품목록
        Map<Object, Object> userCart = new HashMap<>();
        userCart.put(redisHashKey, cartItemOfUserCart);
        // Redis 장바구니 상품정보 단건 변환 결과
        RedisCartDto cartItemDtoOfUserCart = RedisCartDto.builder()
                .productCode("productCode")
                .optionCode("optionCode")
                .price(new BigDecimal(10000))
                .quantity(1)
                .build();

        // 반환 상품정보
        ResponseCartDto expectedResponseCartDto = ResponseCartDto.builder()
                .productCode(foundOptionDto.getProductCode())
                .optionCode(foundOptionDto.getOptionCode())
                .price(foundOptionDto.getPrice())
                .quantity(2)
                .build();

        given(productRepository.findByCodeAndSaleStatus(eq(requestCartDto.getProductCode()), eq(ON_SALE)))
                .willReturn(Optional.of(Product.builder()
                                .code("productCode")
                                .saleStatus(ON_SALE).build()));

        given(redisSingleDataService.getSingleHashValueData(eq(CART), eq(redisKey), eq(redisHashKey)))
                .willReturn(redisCartObj);
        given(objectMapper.convertValue(redisCartObj, RedisCartDto.class))
                .willReturn(targetRedisCartDto);

        // stub(가설) : redisMultiDataService.getHashEntries() 실행 시
        //             Redis에서 기존 유저 장바구니의 상품옵션목록 Map<Object, Object> 반환 예상.
        given(redisMultiDataService.getHashEntries(eq(CART), eq(redisKey)))
                .willReturn(userCart);
        // stub(가설) : objectMapper.convertValue() 실행 시 object를 dto로 반환 예상.
        given(objectMapper.convertValue(
                eq(cartItemOfUserCart), eq(RedisCartDto.class)))
                .willReturn(cartItemDtoOfUserCart);
        // stub(가설) : productionOptionRepository.findByProductIdAndIdOfOnSale() 실행 시 DB에서 상품옵션ID에 해당하는 상품옵션 반환 예상.
        given(productOptionRepository.findByProductCodeAndOptionCodeOfOnSale(
                eq(requestCartDto.getProductCode()), eq(requestCartDto.getOptionCode())))
                .willReturn(Optional.of(foundOptionDto));

        // ArgumentCaptor 생성
        ArgumentCaptor<RedisCartDto> foundOptionDtoCaptor =
                ArgumentCaptor.forClass(RedisCartDto.class);

        // stub(가설) : serviceCartMapper.toDto() 실행 시 entity에 해당하는 dto 반환 예상.
        given(redisCartMapper.toResponseDto(foundOptionDtoCaptor.capture()))
                .willReturn(expectedResponseCartDto);

        // when
        ResponseCartDto responseCartDto = cartService.addCart(requestCartDto, member);

        // then
        // toResponseDto에 전달된 인자 캡처 후 검증
        RedisCartDto capturedFoundOptionDto = foundOptionDtoCaptor.getValue();
        assertEquals(requestCartDto.getProductCode(), capturedFoundOptionDto.getProductCode());
        assertEquals(requestCartDto.getOptionCode(), capturedFoundOptionDto.getOptionCode());
        assertEquals(2, capturedFoundOptionDto.getQuantity());
        // 판매상태 조회 실행여부 검증
        verify(productRepository, times(1))
                .findByCodeAndSaleStatus(requestCartDto.getProductCode(), ON_SALE);
        // redis 저장 실행여부 검증
        verify(redisSingleDataService, times(1))
                .saveSingleHashValueData(
                        CART, redisKey, redisHashKey, targetRedisCartDto);
        // 반환 결과 검증
        assertEquals(requestCartDto.getProductCode(), responseCartDto.getProductCode());
        assertEquals(requestCartDto.getOptionCode(), responseCartDto.getOptionCode());
        assertEquals(foundOptionDto.getPrice(), responseCartDto.getPrice());
        assertEquals(2, responseCartDto.getQuantity());
    }

    @Test
    @DisplayName("장바구니추가 실패 - 판매중인 상품이 아니면 예외발생")
    void addCart_shouldReturnProductNotOnSale_when() {
        // given
        RequestCartDto requestCartDto = requestCartDto();
        Member member = member();

        // 판매중단된 상품으로 조회되지 않음
        given(productRepository.findByCodeAndSaleStatus(
                eq(requestCartDto.getProductCode()), eq(ON_SALE)))
                .willReturn(Optional.empty());

        // when
        // then
        ProductException e =assertThrows(ProductException.class, () ->
                cartService.addCart(requestCartDto, member));
        assertEquals(PRODUCT_NOT_ON_SALE, e.getErrorCode());
        // 판매상태 조회 실행여부 검증
        verify(productRepository, times(1))
                .findByCodeAndSaleStatus(requestCartDto.getProductCode(), ON_SALE);
    }

    @Test
    @DisplayName("장바구니추가 실패 - 고객 외 장바구니 접근 시 예외발생")
    void addCart_shouldReturnCartCustomerOnly_whenAccessNotCustomer() {
        // given
        RequestCartDto requestCartDto = requestCartDto();
        Member member = Member.builder()
                .roles(List.of(MemberAuthority.builder()
                        .authority(SELLER).build())).build();; // 회원아님

        // when
        // then
        CartException e = assertThrows(CartException.class, () ->
                cartService.addCart(requestCartDto, member));
        assertEquals(CART_CUSTOMER_ONLY, e.getErrorCode());
    }
}