package com.myecommerce.MyECommerce.service.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.dto.cart.RequestCartDto;
import com.myecommerce.MyECommerce.dto.cart.ResponseCartDto;
import com.myecommerce.MyECommerce.dto.cart.ServiceCartDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.production.Production;
import com.myecommerce.MyECommerce.entity.production.ProductionOption;
import com.myecommerce.MyECommerce.mapper.ServiceCartMapper;
import com.myecommerce.MyECommerce.repository.production.ProductionOptionRepository;
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
    private ServiceCartMapper serviceCartMapper;

    @Mock
    private RedisSingleDataService redisSingleDataService;
    @Mock
    private RedisMultiDataService redisMultiDataService;

    @Mock
    private ProductionOptionRepository productionOptionRepository;

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
        ProductionOption foundOption = ProductionOption.builder()
                .id(10L)
                .optionCode("optionCode")
                .optionName("상품옵션명")
                .price(new BigDecimal(10000))
                .quantity(500)
                .production(Production.builder()
                        .id(1L)
                        .name("상품명")
                        .build())
                .build();
        ServiceCartDto foundOptionDto = ServiceCartDto.builder()
                .optionId(foundOption.getId())
                .optionName(foundOption.getOptionName())
                .price(foundOption.getPrice())
                .quantity(foundOption.getQuantity())
                .productId(foundOption.getProduction().getId())
                .productName(foundOption.getProduction().getName())
                .build();
        // Redis 장바구니 상품정보
        HashMap<String, Object> orgOption = new HashMap<>();
        orgOption.put("optionId", foundOptionDto.getOptionId());
        orgOption.put("optionName", foundOptionDto.getOptionName());
        orgOption.put("price", foundOptionDto.getPrice());
        orgOption.put("quantity", 1);
        orgOption.put("productId", foundOptionDto.getProductId());
        orgOption.put("productName", foundOptionDto.getProductName());
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

        // stub(가설) : redisMultiDataService.getSizeOfHashData() 실행 시 key에 해당하는 hash 데이터 수 2 반환 예상.
        given(redisMultiDataService.getSizeOfHashData(eq(CART), eq(member.getUserId())))
                .willReturn(2L);

        // stub(가설) : productionOptionRepository.findById() 실행 시 DB에서 상품옵션ID에 해당하는 상품옵션 반환 예상.
        given(productionOptionRepository.findById(eq(requestCartDto.getOptionId())))
                .willReturn(Optional.of(foundOption));

        // stub(가설) : redisSingleDataService.getSingleHashValueData() 실행 시
        //             Redis에서 기존 장바구니에 저장한 key에 해당하는 상품옵션 Object 반환 예상.
        given(redisSingleDataService.getSingleHashValueData(
                eq(CART), eq(member.getUserId()), eq(requestCartDto.getOptionId().toString())))
                .willReturn(orgOption);

        // stub(가설) : serviceCartMapper.toDto() 실행 시 entity에 해당하는 dto 반환 예상.
        given(serviceCartMapper.toDto(foundOption))
                .willReturn(foundOptionDto);

        // stub(가설) : objectMapper.convertValue() 실행 시 object를 dto로 반환 예상.
        given(objectMapper.convertValue(orgOption, ServiceCartDto.class))
                .willReturn(ServiceCartDto.builder()
                        .optionId((Long) orgOption.get("optionId"))
                        .optionName((String) orgOption.get("optionName"))
                        .price((BigDecimal) orgOption.get("price"))
                        .quantity((Integer) orgOption.get("quantity"))
                        .productId((Long) orgOption.get("productId"))
                        .optionName((String) orgOption.get("productName"))
                        .build());

        // ArgumentCaptor 생성
        ArgumentCaptor<ServiceCartDto> foundOptionDtoCaptor =
                ArgumentCaptor.forClass(ServiceCartDto.class);

        // stub(가설) : serviceCartMapper.toDto() 실행 시 entity에 해당하는 dto 반환 예상.
        given(serviceCartMapper.toResponseDto(foundOptionDtoCaptor.capture()))
                .willReturn(expectedResponseCartDto);

        // when
        ResponseCartDto responseCartDto = cartService.addCart(requestCartDto, member);

        // then
        // toResponseDto에 전달된 인자 캡처 후 검증
        ServiceCartDto capturedFoundOptionDto = foundOptionDtoCaptor.getValue();
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
        assertEquals(foundOption.getProduction().getId(), responseCartDto.getProductId());
        assertEquals(foundOption.getProduction().getName(), responseCartDto.getProductName());
    }
}