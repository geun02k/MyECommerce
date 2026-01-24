package com.myecommerce.MyECommerce.service.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.dto.cart.RedisCartDto;
import com.myecommerce.MyECommerce.dto.cart.RequestCartDto;
import com.myecommerce.MyECommerce.dto.cart.ResponseCartDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.mapper.RedisCartMapper;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.service.redis.RedisSingleDataService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static com.myecommerce.MyECommerce.service.cart.CartService.EXPIRATION_PERIOD;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.RedisNamespaceType.CART;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private RedisCartMapper redisCartMapper;

    @Mock
    private RedisSingleDataService redisSingleDataService;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private CartPolicy cartPolicy;

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

    /** 장바구니에 존재하는 상품옵션 단건  */
    RedisCartDto existingCartItem() {
        return RedisCartDto.builder()
                .productCode("productCode")
                .optionCode("optionCode")
                .price(new BigDecimal("10000"))
                .quantity(1)
                .build();
    }

    /** 판매중인 상품옵션 단건  */
    RedisCartDto requestedItemNotInCart() {
        return RedisCartDto.builder()
                .productCode("productCode")
                .optionCode("optionCode")
                .price(new BigDecimal("10000"))
                .build();
    }


    /* ----------------------
        장바구니추가 Tests
       ---------------------- */

    @Test
    @DisplayName("장바구니추가 성공 - 장바구니에 동일 상품 존재하는 경우 장바구니 수량 증가")
    void addCart_shouldIncreaseQuantity_whenProductAlreadyExistsInCart() {
        // given
        // 요청 장바구니 상품 정보
        RequestCartDto requestCartDto = RequestCartDto.builder()
                .productCode("productCode")
                .optionCode("optionCode")
                .quantity(1)
                .build();
        // 요청 사용자 정보
        Member member = member();

        // 저장 대상 장바구니 옵션
        RedisCartDto targetRedisCartDto = existingCartItem();

        // Redis key
        String redisKey = member.getUserId();
        String redisHashKey = requestCartDto.getProductCode()
                + ":" + requestCartDto.getOptionCode();

        // 반환 상품정보
        ResponseCartDto expectedResponseCartDto = ResponseCartDto.builder()
                .productCode(targetRedisCartDto.getProductCode())
                .optionCode(targetRedisCartDto.getOptionCode())
                .price(targetRedisCartDto.getPrice())
                .quantity(2)
                .build();

        // 요청 상품 Redis 장바구니에서 조회
        given(redisSingleDataService.getSingleHashValueData(eq(CART), eq(redisKey), eq(redisHashKey)))
                .willReturn(targetRedisCartDto);
        given(objectMapper.convertValue(targetRedisCartDto, RedisCartDto.class))
                .willReturn(targetRedisCartDto);

        // RedisCartDto -> 응답DTO 변환.
        given(redisCartMapper.toResponseDto(targetRedisCartDto))
                .willReturn(expectedResponseCartDto);

        // when
        ResponseCartDto responseCartDto = cartService.addCart(requestCartDto, member);

        ArgumentCaptor<RedisCartDto> redisCartDtoCaptor =
                ArgumentCaptor.forClass(RedisCartDto.class);
        // then
        // 정책 실행여부 검증
        verify(cartPolicy, times(1))
                .validateAdd(requestCartDto.getProductCode(), member);
        verify(productOptionRepository, never())
                .findByProductCodeAndOptionCodeOfOnSale(any(), any());
        // redis 저장 실행여부 검증
        verify(redisSingleDataService, times(1))
                .saveSingleHashValueData(
                        eq(CART), eq(redisKey), eq(redisHashKey), redisCartDtoCaptor.capture());
        // redis 만료 기간 갱신 검증
        verify(redisSingleDataService, times(1))
                .setExpire(eq(CART), eq(redisKey), eq(Duration.ofDays(EXPIRATION_PERIOD)));
        // 캡쳐 결과 검증
        RedisCartDto capturedRedisCartDto = redisCartDtoCaptor.getValue();
        assertEquals(2, capturedRedisCartDto.getQuantity());
        // 반환 결과 검증
        assertEquals(requestCartDto.getProductCode(), responseCartDto.getProductCode());
        assertEquals(requestCartDto.getOptionCode(), responseCartDto.getOptionCode());
        assertEquals(targetRedisCartDto.getPrice(), responseCartDto.getPrice());
        assertEquals(2, responseCartDto.getQuantity());
    }

    @Test
    @DisplayName("장바구니추가 성공 - 장바구니에 요청 상품 미존재 시 상품 옵션 신규 추가")
    void addCart_shouldAddNewItemWithRequestedQuantity_whenProductNotInCart() {
        // given
        // 요청 장바구니 상품 정보
        RequestCartDto requestCartDto = RequestCartDto.builder()
                .productCode("productCode")
                .optionCode("optionCode")
                .quantity(5)
                .build();
        // 요청 사용자 정보
        Member member = member();

        // DB 요청 상품옵션 정보 조회
        RedisCartDto foundOptionDto = requestedItemNotInCart();

        // Redis key
        String redisKey = member.getUserId();
        String redisHashKey = requestCartDto.getProductCode()
                + ":" + requestCartDto.getOptionCode();

        // 반환 상품정보
        ResponseCartDto expectedResponseCartDto = ResponseCartDto.builder()
                .productCode(foundOptionDto.getProductCode())
                .optionCode(foundOptionDto.getOptionCode())
                .price(foundOptionDto.getPrice())
                .quantity(5)
                .build();

        // 요청 상품 Redis 장바구니에서 조회
        given(redisSingleDataService.getSingleHashValueData(
                eq(CART), eq(redisKey), eq(redisHashKey)))
                .willReturn(null);
        // 판매중인 상품옵션 DB에서 조회.
        given(productOptionRepository.findByProductCodeAndOptionCodeOfOnSale(
                eq(requestCartDto.getProductCode()),
                eq(requestCartDto.getOptionCode())))
                .willReturn(Optional.of(foundOptionDto));
        // RedisCartDto -> 응답DTO 변환.
        given(redisCartMapper.toResponseDto(any()))
                .willReturn(expectedResponseCartDto);

        // when
        ResponseCartDto responseCartDto =
                cartService.addCart(requestCartDto, member);

        // then
        // 정책 실행여부 검증
        verify(cartPolicy, times(1))
                .validateAdd(eq(requestCartDto.getProductCode()), eq(member));
        // 요청 상품 장바구니 미존재해 상품 옵션 조회
        verify(productOptionRepository, times(1))
                .findByProductCodeAndOptionCodeOfOnSale(
                        eq("productCode"), eq("optionCode"));
        // redis 저장 실행여부 검증
        ArgumentCaptor<RedisCartDto> redisCartDtoCaptor =
                ArgumentCaptor.forClass(RedisCartDto.class);
        verify(redisSingleDataService, times(1))
                .saveSingleHashValueData(
                        eq(CART), eq(redisKey), eq(redisHashKey),
                        redisCartDtoCaptor.capture());
        // redis 만료 기간 셋팅 검증
        verify(redisSingleDataService, times(1))
                .setExpire(eq(CART), eq(redisKey), eq(Duration.ofDays(EXPIRATION_PERIOD)));
        // 캡쳐 결과 검증
        assertEquals(5, redisCartDtoCaptor.getValue().getQuantity());
        // 반환 결과 검증
        assertEquals(requestCartDto.getProductCode(), responseCartDto.getProductCode());
        assertEquals(requestCartDto.getOptionCode(), responseCartDto.getOptionCode());
        assertEquals(foundOptionDto.getPrice(), responseCartDto.getPrice());
        assertEquals(5, responseCartDto.getQuantity());
    }
}