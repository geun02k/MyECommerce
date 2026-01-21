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
                .optionId(10L)
                .quantity(1)
                .build();
        // 요청 사용자 정보
        Member member = Member.builder()
                .userId("tester")
                .build();

        // DB 상품정보
        ProductOption foundOption = ProductOption.builder()
                .id(10L)
                .optionCode("optionCode")
                .optionName("상품옵션명")
                .price(new BigDecimal(10000))
                .quantity(500)
                .product(Product.builder()
                        .id(1L)
                        .name("상품명")
                        .build())
                .build();
        RedisCartDto foundOptionDto = RedisCartDto.builder()
                .optionId(foundOption.getId())
                .optionName(foundOption.getOptionName())
                .price(foundOption.getPrice())
                .quantity(foundOption.getQuantity())
                .productId(foundOption.getProduct().getId())
                .productName(foundOption.getProduct().getName())
                .build();
        // Redis 장바구니 상품정보 단건
        Map<Object, Object> orgOption = new HashMap<>();
        orgOption.put("optionId", foundOptionDto.getOptionId());
        orgOption.put("optionName", foundOptionDto.getOptionName());
        orgOption.put("price", foundOptionDto.getPrice());
        orgOption.put("quantity", 1);
        orgOption.put("productId", foundOptionDto.getProductId());
        orgOption.put("productName", foundOptionDto.getProductName());
        // Redis 장바구니 상품목록
        Map<Object, Object> userCart = new HashMap<>();
        userCart.put(orgOption.get("optionId"), orgOption);

        // 반환 상품정보
        ResponseCartDto expectedResponseCartDto = ResponseCartDto.builder()
                .optionId(foundOptionDto.getOptionId())
                .optionName(foundOptionDto.getOptionName())
                .price(foundOptionDto.getPrice())
                .quantity(requestCartDto.getQuantity() +
                          (Integer) orgOption.get("quantity"))
                .productId(foundOptionDto.getProductId())
                .productName(foundOptionDto.getProductName())
                .build();

        // stub(가설) : redisMultiDataService.getHashEntries() 실행 시
        //             Redis에서 기존 유저 장바구니의 상품옵션목록 Map<Object, Object> 반환 예상.
        given(redisMultiDataService.getHashEntries(eq(CART), eq(member.getUserId())))
                .willReturn(userCart);

        // stub(가설) : objectMapper.convertValue() 실행 시 object를 dto로 반환 예상.
        given(objectMapper.convertValue(eq(userCart.get(orgOption.get("optionId"))), eq(RedisCartDto.class)))
                .willReturn(RedisCartDto.builder()
                        .optionId((Long) orgOption.get("optionId"))
                        .optionName((String) orgOption.get("optionName"))
                        .price((BigDecimal) orgOption.get("price"))
                        .quantity((Integer) orgOption.get("quantity"))
                        .productId((Long) orgOption.get("productId"))
                        .optionName((String) orgOption.get("productName"))
                        .build());

        // stub(가설) : productionOptionRepository.findById() 실행 시 DB에서 상품옵션ID에 해당하는 상품옵션 반환 예상.
        given(productOptionRepository.findById(eq(requestCartDto.getOptionId())))
                .willReturn(Optional.of(foundOption));

        // stub(가설) : redisSingleDataService.getSingleHashValueData() 실행 시
        //             Redis에서 기존 장바구니에 저장한 key에 해당하는 상품옵션 Object 반환 예상.
        given(redisSingleDataService.getSingleHashValueData(
                eq(CART), eq(member.getUserId()), eq(requestCartDto.getOptionId().toString())))
                .willReturn(orgOption);

        // stub(가설) : serviceCartMapper.toDto() 실행 시 entity에 해당하는 dto 반환 예상.
        given(redisCartMapper.toRedisDto(foundOption))
                .willReturn(foundOptionDto);

        // stub(가설) : objectMapper.convertValue() 실행 시 object를 dto로 반환 예상.
        given(objectMapper.convertValue(orgOption, RedisCartDto.class))
                .willReturn(RedisCartDto.builder()
                        .optionId((Long) orgOption.get("optionId"))
                        .optionName((String) orgOption.get("optionName"))
                        .price((BigDecimal) orgOption.get("price"))
                        .quantity((Integer) orgOption.get("quantity"))
                        .productId((Long) orgOption.get("productId"))
                        .optionName((String) orgOption.get("productName"))
                        .build());

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
        assertEquals(requestCartDto.getOptionId(), capturedFoundOptionDto.getOptionId());
        assertEquals(2, capturedFoundOptionDto.getQuantity());
        // redis 저장 실행여부 검증
        verify(redisSingleDataService, times(1))
                .saveSingleHashValueData(
                        CART, member.getUserId(), requestCartDto.getOptionId().toString(), foundOptionDto);
        // 반환 결과 검증
        assertEquals(requestCartDto.getOptionId(), responseCartDto.getOptionId());
        assertEquals(foundOption.getOptionName(), responseCartDto.getOptionName());
        assertEquals(foundOption.getPrice(), responseCartDto.getPrice());
        assertEquals(2, responseCartDto.getQuantity());
        assertEquals(foundOption.getProduct().getId(), responseCartDto.getProductId());
        assertEquals(foundOption.getProduct().getName(), responseCartDto.getProductName());
    }
}