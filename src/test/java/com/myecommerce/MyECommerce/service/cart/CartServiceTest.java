package com.myecommerce.MyECommerce.service.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.dto.cart.RedisCartDto;
import com.myecommerce.MyECommerce.dto.cart.RequestCartDto;
import com.myecommerce.MyECommerce.dto.cart.ResponseCartDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.order.OrderItem;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.mapper.RedisCartMapper;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.service.redis.RedisMultiDataService;
import com.myecommerce.MyECommerce.service.redis.RedisSingleDataService;
import com.myecommerce.MyECommerce.type.OrderPathType;
import com.myecommerce.MyECommerce.type.RedisNamespaceType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.myecommerce.MyECommerce.service.cart.CartService.EXPIRATION_PERIOD;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
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
    private RedisMultiDataService redisMultiDataService;

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
    RedisCartDto existingCartItem(Long productOptionId, int quantity) {
        return RedisCartDto.builder()
                .optionId(productOptionId)
                .quantity(quantity)
                .build();
    }

    /** 판매중인 상품옵션 단건  */
    RedisCartDto requestedItemNotInCart(Long productOptionId) {
        return RedisCartDto.builder()
                .optionId(productOptionId)
                .build();
    }

    /** 상품옵션 생성 */
    ProductOption productOption(Long optionId) {
        return ProductOption.builder()
                .id(optionId)
                .price(new BigDecimal("10000"))
                .quantity(10)
                .product(Product.builder()
                        .saleStatus(ON_SALE)
                        .build())
                .build();
    }

    /** 주문물품 생성 */
    List<OrderItem> orderItems(List<Long> orderOptionIds) {
        return orderOptionIds.stream()
                .map(optionId -> OrderItem.createOrderItem(
                        productOption(optionId), 1))
                .toList();
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
                .productOptionId(5L)
                .quantity(1) // 수량 1개 추가
                .build();
        // 요청 사용자 정보
        Member member = member();

        // 요청자 장바구니에 존재하는 동일 상품옵션
        RedisCartDto targetRedisCartDto = existingCartItem(5L, 1); // 기존 수량 1개

        // Redis key
        String redisKey = member.getUserId();
        String redisHashKey = String.valueOf(requestCartDto.getProductOptionId());

        // 반환 상품정보
        ResponseCartDto expectedResponseCartDto = ResponseCartDto.builder()
                .optionId(targetRedisCartDto.getOptionId())
                .quantity(2)
                .build();

        // 요청 상품 Redis 장바구니에서 조회
        given(redisSingleDataService.getSingleHashValueData(eq(RedisNamespaceType.CART), eq(redisKey), eq(redisHashKey)))
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
                .validateAdd(requestCartDto.getProductOptionId(), member);
        verify(productOptionRepository, never()).findByIdOfOnSale(any());
        // redis 저장 실행여부 검증
        verify(redisSingleDataService, times(1))
                .saveSingleHashValueData(
                        eq(RedisNamespaceType.CART), eq(redisKey), eq(redisHashKey), redisCartDtoCaptor.capture());
        // redis 만료 기간 갱신 검증
        verify(redisSingleDataService, times(1))
                .setExpire(eq(RedisNamespaceType.CART), eq(redisKey), eq(Duration.ofDays(EXPIRATION_PERIOD)));
        // 캡쳐 결과 검증
        RedisCartDto capturedRedisCartDto = redisCartDtoCaptor.getValue();
        assertEquals(2, capturedRedisCartDto.getQuantity());
        // 반환 결과 검증
        assertEquals(5L, responseCartDto.getOptionId());
        assertEquals(2, responseCartDto.getQuantity());
    }

    @Test
    @DisplayName("장바구니추가 성공 - 장바구니에 요청 상품 미존재 시 상품 옵션 신규 추가")
    void addCart_shouldAddNewItemWithRequestedQuantity_whenProductNotInCart() {
        // given
        // 요청 장바구니 상품 정보
        RequestCartDto requestCartDto = RequestCartDto.builder()
                .productOptionId(10L)
                .quantity(5)
                .build();
        // 요청 사용자 정보
        Member member = member();

        // DB 요청 상품옵션 정보 조회
        RedisCartDto foundOptionDto =
                requestedItemNotInCart(requestCartDto.getProductOptionId());

        // Redis key
        String redisKey = member.getUserId();
        String redisHashKey = String.valueOf(requestCartDto.getProductOptionId());

        // 반환 상품정보
        ResponseCartDto expectedResponseCartDto = ResponseCartDto.builder()
                .optionId(10L)
                .quantity(5)
                .build();

        // 요청 상품 Redis 장바구니에서 조회
        given(redisSingleDataService.getSingleHashValueData(
                eq(RedisNamespaceType.CART), eq(redisKey), eq(redisHashKey)))
                .willReturn(null);
        // 판매중인 상품옵션 DB에서 조회.
        given(productOptionRepository.findByIdOfOnSale(
                eq(requestCartDto.getProductOptionId())))
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
                .validateAdd(eq(requestCartDto.getProductOptionId()),
                             eq(member));
        // 요청 상품 장바구니 미존재해 상품 옵션 조회
        verify(productOptionRepository, times(1))
                .findByIdOfOnSale(eq(10L));
        // redis 저장 실행여부 검증
        ArgumentCaptor<RedisCartDto> redisCartDtoCaptor =
                ArgumentCaptor.forClass(RedisCartDto.class);
        verify(redisSingleDataService, times(1))
                .saveSingleHashValueData(
                        eq(RedisNamespaceType.CART), eq(redisKey), eq(redisHashKey),
                        redisCartDtoCaptor.capture());
        // redis 만료 기간 셋팅 검증
        verify(redisSingleDataService, times(1))
                .setExpire(eq(RedisNamespaceType.CART), eq(redisKey), eq(Duration.ofDays(EXPIRATION_PERIOD)));
        // 캡쳐 결과 검증
        assertEquals(5, redisCartDtoCaptor.getValue().getQuantity());
        // 반환 결과 검증
        assertEquals(requestCartDto.getProductOptionId(), responseCartDto.getOptionId());
        assertEquals(5, responseCartDto.getQuantity());
    }

    /* ----------------------
        장바구니 조회 Tests
       ---------------------- */

    /* -------------------------------
        장바구니에서 주문물품 제거 Tests
       ------------------------------- */

    @Test
    @DisplayName("장바구니에서 주문물품제거 성공 - 단건 주문물품 존재 시 주문물품삭제 메서드 호출")
    void removeOrderItems_shouldCallDeleteMethod_whenExistAOrderItem() {
        // given
        Long orderOptionId = 13L;

        OrderPathType orderPath = OrderPathType.CART;
        String userId = member().getUserId();
        List<OrderItem> orderItems = List.of(
                OrderItem.createOrderItem(productOption(orderOptionId), 1));

        // when
        cartService.removeOrderItems(orderPath, userId, orderItems);

        // then
        verify(redisMultiDataService).deleteMultiHashData(
                RedisNamespaceType.CART, userId, List.of(String.valueOf(orderOptionId)));
    }

    @Test
    @DisplayName("장바구니에서 주문물품제거 성공 - 다건 주문물품 존재 시 주문물품삭제 메서드 호출")
    void removeOrderItems_shouldCallDeleteMethod_whenExistOrderItemList() {
        // given
        List<Long> orderOptionIds = Arrays.asList(1L, 2L);

        OrderPathType orderPath = OrderPathType.CART;
        String userId = member().getUserId();
        List<OrderItem> orderItems = orderItems(orderOptionIds);

        // when
        cartService.removeOrderItems(orderPath, userId, orderItems);

        // then
        List<String> strOrderOptionIds = orderOptionIds.stream().map(String::valueOf).toList();
        verify(redisMultiDataService).deleteMultiHashData(
                RedisNamespaceType.CART, userId, strOrderOptionIds);
    }

    @Test
    @DisplayName("장바구니에서 주문물품제거 실패 - 주문경로가 장바구니가 아니면 주문물품제거 미수행")
    void removeOrderItems_shouldNotCallDeleteMethod_whenNotCartOfOrderPath() {
        // given
        OrderPathType orderPathOfNotCartDelete = OrderPathType.DIRECT;

        // when
        cartService.removeOrderItems(orderPathOfNotCartDelete, null, null);

        // then
        verify(redisMultiDataService, never()).deleteMultiHashData(any(), any(), any());
    }

}