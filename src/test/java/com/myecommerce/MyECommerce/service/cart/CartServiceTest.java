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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.myecommerce.MyECommerce.service.cart.CartService.EXPIRATION_PERIOD;
import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
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
    Member member(String userId) {
        return Member.builder()
                .userId(userId)
                .roles(List.of(MemberAuthority.builder()
                                .authority(CUSTOMER)
                                .build()))
                .build();
    }

    /** 장바구니에 존재하는 상품옵션 단건  */
    RedisCartDto existingCartItem(Long optionId, int quantity) {
        return RedisCartDto.builder()
                .optionId(optionId)
                .quantity(quantity)
                .build();
    }

    /** 판매중인 상품옵션 단건  */
    RedisCartDto requestedItemNotInCart(Long optionId) {
        return RedisCartDto.builder()
                .optionId(optionId)
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

    /** 장바구니 추가 요청 */
    RequestCartDto requestCartDto(Long optionId, int quantity) {
        return RequestCartDto.builder()
                .productOptionId(optionId)
                .quantity(quantity)
                .build();
    }

    /** 장바구니 추가 응답 */
    ResponseCartDto responseCartDto(Long optionId, int quantity) {
        return ResponseCartDto.builder()
                .optionId(optionId)
                .quantity(quantity)
                .build();
    }

    /* ----------------------
        장바구니 추가 Tests
       ---------------------- */

    @Test
    @DisplayName("장바구니추가 성공 - 장바구니에 동일 상품 존재하는 경우 장바구니 수량 증가")
    void addCart_shouldIncreaseQuantity_whenProductAlreadyExistsInCart() {
        // given
        String userId = "userId";
        Long optionId = 5L;
        int requestQuantity = 1;  // 요청수량
        int existingQuantity = 1; // 기존수량
        int expectedQuantity = requestQuantity + existingQuantity; // 최종기대수량
        // 요청 장바구니 상품 정보
        RequestCartDto requestCartDto = requestCartDto(optionId, requestQuantity);
        // 요청 사용자 정보
        Member member = member(userId);

        // Redis key
        String redisKey = userId;
        String redisHashKey = String.valueOf(optionId);

        // 요청자 장바구니에 존재하는 동일 상품옵션 조회
        RedisCartDto userCartDto = existingCartItem(optionId, existingQuantity);
        given(redisSingleDataService.getSingleHashValueData(CART, redisKey, redisHashKey))
                .willReturn(userCartDto);
        given(objectMapper.convertValue(userCartDto, RedisCartDto.class))
                .willReturn(userCartDto);

        // RedisCartDto -> 응답DTO 변환
        given(redisCartMapper.toResponseDto(userCartDto))
                .willReturn(responseCartDto(optionId, expectedQuantity));

        // when
        ResponseCartDto response = cartService.addCart(requestCartDto, member);

        // then
        verify(cartPolicy).validateAdd(optionId, member); // 정책실행검증
        // 판매중인 상품옵션 미조회 검증
        verify(productOptionRepository, never()).findByIdOfOnSale(any());
        // 장바구니에 상품옵션 저장 실행 검증
        ArgumentCaptor<RedisCartDto> cartDtoCaptor = ArgumentCaptor.forClass(RedisCartDto.class);
        verify(redisSingleDataService).saveSingleHashValueData(
                eq(CART), eq(redisKey), eq(redisHashKey), cartDtoCaptor.capture());
        // 장바구니 만료 기간 갱신 검증
        verify(redisSingleDataService).setExpire(
                CART, redisKey, Duration.ofDays(EXPIRATION_PERIOD));

        // 저장 전 수량 검증
        RedisCartDto capturedCartDto = cartDtoCaptor.getValue();
        assertEquals(5L, capturedCartDto.getOptionId());
        assertEquals(expectedQuantity, capturedCartDto.getQuantity());
        // 응답 검증
        assertEquals(5L, response.getOptionId());
        assertEquals(expectedQuantity, response.getQuantity());
    }

    @Test
    @DisplayName("장바구니추가 성공 - 장바구니에 요청 상품 미존재 시 상품 옵션 신규 추가")
    void addCart_shouldAddNewItemWithRequestedQuantity_whenProductNotInCart() {
        // given
        String userId = "userId";
        Long optionId = 10L;
        int requestQuantity = 5;  // 요청수량
        // 요청 장바구니 상품 정보
        RequestCartDto requestCartDto = requestCartDto(optionId, requestQuantity);
        // 요청 사용자 정보
        Member member = member(userId);

        // Redis key
        String redisKey = userId;
        String redisHashKey = String.valueOf(optionId);

        // 반환 상품정보
        ResponseCartDto responseCartDto = responseCartDto(optionId, requestQuantity);

        // 요청자 장바구니에 존재하는 동일 상품옵션 조회
        given(redisSingleDataService.getSingleHashValueData(CART, redisKey, redisHashKey))
                .willReturn(null);
        // 판매중인 상품옵션 조회
        RedisCartDto foundOptionDto = requestedItemNotInCart(optionId);
        given(productOptionRepository.findByIdOfOnSale(optionId))
                .willReturn(Optional.of(foundOptionDto));
        // RedisCartDto -> 응답DTO 변환
        given(redisCartMapper.toResponseDto(foundOptionDto)).willReturn(responseCartDto);

        // when
        ResponseCartDto response = cartService.addCart(requestCartDto, member);

        // then
        verify(cartPolicy).validateAdd(optionId, member); // 정책 실행 검증
        // 요청 상품이 장바구니에 미존재해 상품 옵션 조회
        verify(productOptionRepository).findByIdOfOnSale(optionId);
        // 장바구니에 상품옵션 저장 실행 검증
        ArgumentCaptor<RedisCartDto> cartDtoCaptor = ArgumentCaptor.forClass(RedisCartDto.class);
        verify(redisSingleDataService).saveSingleHashValueData(
                eq(CART), eq(redisKey), eq(redisHashKey), cartDtoCaptor.capture());
        // 장바구니 만료 기간 셋팅 검증
        verify(redisSingleDataService).setExpire(
                CART, redisKey, Duration.ofDays(EXPIRATION_PERIOD));

        // 저장 전 수량 검증
        RedisCartDto capturedCartDto = cartDtoCaptor.getValue();
        assertEquals(10L, capturedCartDto.getOptionId());
        assertEquals(requestQuantity, capturedCartDto.getQuantity());
        // 응답 검증
        assertEquals(10L, response.getOptionId());
        assertEquals(requestQuantity, response.getQuantity());
    }

    // TODO: 정책검증실패
    // TODO: 장바구니에 없는 상품 새로 추가 시, DB에 옵션이 존재하지 않거나 판매중이 아닌 경우 예외발생 (PRODUCT_OPTION_NOT_EXIST 예외발생검증)

    /* ----------------------
        장바구니 조회 Tests
       ---------------------- */

    // TODO: 장바구니 조회 성공 시나리오
    // TODO: 장바구니에서 상품 미조회 시 빈 리스트 반환 (그 후 진행하지 않음 검증)
    // TODO: 장바구니목록 및 재고 키 목록 생성 검증 (둘의 순서 동일해야함)
    // 재고 캐시 데이터 조회 검증 (STOCK:optionId 형태의 Key 생성이 올바른지, stockCacheService로 넘겨져 결과를 정상 수신하는지 검증)
    // - 정상 시나리오와 중복으로 제외
    // TODO: 구매가능여부(품절여부, 구매가능수량) 검증 - 경계값 검증으로 재고 0, -1, 1, null 검증

    /* -------------------------------
        장바구니에서 주문물품 제거 Tests
       ------------------------------- */

    @Test
    @DisplayName("장바구니에서 주문물품제거 성공 - 단건 주문물품 존재 시 주문물품삭제 메서드 호출")
    void removeOrderItems_shouldCallDeleteMethod_whenExistAOrderItem() {
        // given
        String userId = "userId";
        Long orderOptionId = 13L;

        OrderPathType orderPath = OrderPathType.CART;
        List<OrderItem> orderItems = List.of(
                OrderItem.createOrderItem(productOption(orderOptionId), 1));

        // when
        cartService.removeOrderItems(orderPath, userId, orderItems);

        // then
        verify(redisMultiDataService).deleteMultiHashData(
                CART, userId, List.of(String.valueOf(orderOptionId)));
    }

    @Test
    @DisplayName("장바구니에서 주문물품제거 성공 - 다건 주문물품 존재 시 주문물품삭제 메서드 호출")
    void removeOrderItems_shouldCallDeleteMethod_whenExistOrderItemList() {
        // given
        String userId = "userId";
        List<Long> orderOptionIds = Arrays.asList(1L, 2L);

        OrderPathType orderPath = OrderPathType.CART;
        List<OrderItem> orderItems = orderItems(orderOptionIds);

        // when
        cartService.removeOrderItems(orderPath, userId, orderItems);

        // then
        List<String> strOrderOptionIds = orderOptionIds.stream().map(String::valueOf).toList();
        verify(redisMultiDataService).deleteMultiHashData(
                CART, userId, strOrderOptionIds);
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

    @Test
    @DisplayName("장바구니에서 주문물품제거 실패 - 주문물품이 없으면 주문물품제거 미수행")
    void removeOrderItems_shouldNotCallDeleteMethod_whenNotExistOrderItem() {
        // given
        OrderPathType orderPath = OrderPathType.CART;
        List<OrderItem> emptyOrderItems = Collections.emptyList();

        // when
        cartService.removeOrderItems(orderPath, null, emptyOrderItems);

        // then
        verify(redisMultiDataService, never()).deleteMultiHashData(any(), any(), any());
    }

    // TODO: 장바구니에서 주문물품제거 실패 - 주문물품이 null이면 주문물품제거 미수행
}