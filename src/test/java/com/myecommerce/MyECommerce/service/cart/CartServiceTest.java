package com.myecommerce.MyECommerce.service.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.dto.cart.RedisCartDto;
import com.myecommerce.MyECommerce.dto.cart.RequestCartDto;
import com.myecommerce.MyECommerce.dto.cart.ResponseCartDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.mapper.RedisCartMapper;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
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
import java.util.Map;
import java.util.Optional;

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

    @InjectMocks
    private CartService cartService;

    @Test
    @DisplayName("장바구니에 동일 상품 존재하는 경우 장바구니 상품추가 성공")
    void successAddCartIfTheSameProductExists() {
        // given
        // 요청 장바구니 상품 정보
        RequestCartDto requestCartDto = RequestCartDto.builder()
                .productCode("productCode")
                .optionCode("optionCode")
                .quantity(1)
                .build();
        // 요청 사용자 정보
        Member member = Member.builder()
                .userId("tester")
                .build();

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
}