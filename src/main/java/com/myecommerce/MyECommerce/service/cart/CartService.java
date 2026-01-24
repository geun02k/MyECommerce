package com.myecommerce.MyECommerce.service.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.dto.cart.RedisCartDto;
import com.myecommerce.MyECommerce.dto.cart.RequestCartDto;
import com.myecommerce.MyECommerce.dto.cart.ResponseCartDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.exception.ProductException;
import com.myecommerce.MyECommerce.mapper.RedisCartMapper;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.service.redis.RedisSingleDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static com.myecommerce.MyECommerce.exception.errorcode.ProductErrorCode.PRODUCT_OPTION_NOT_EXIST;
import static com.myecommerce.MyECommerce.type.RedisNamespaceType.CART;

@Service
@RequiredArgsConstructor
public class CartService {

    public static final Long EXPIRATION_PERIOD = 30L;

    private final CartPolicy cartPolicy;

    private final ObjectMapper objectMapper;
    private final RedisCartMapper redisCartMapper;

    private final RedisSingleDataService redisSingleDataService;

    private final ProductOptionRepository productOptionRepository;

    /** 장바구니 상품 등록 **/
    @Transactional
    public ResponseCartDto addCart(RequestCartDto requestCartDto, Member member) {
        // redis 장바구니에 상품 단건 등록
        // key = 사용자아이디(토큰값을 사용하게되면 만료 시 장바구니내역 사용불가로)
        // value = 상품목록(해시,같은 필드명에 대해서는 새로운 값으로 덮어씌움)
        //       - hashKey = 등록할 상품코드:상품옵션코드
        //       - hashValue = 등록할 상품옵션 정보

        // 0. 정책검증
        cartPolicy.validateAdd(requestCartDto.getProductCode(), member);

        String redisKey = member.getUserId();
        String redisHashKey = createCartRedisHashKey(
                requestCartDto.getProductCode(), requestCartDto.getOptionCode());

        // 1. 요청 상품옵션에 대한 장바구니 조회
        RedisCartDto cartItem = getCartItem(redisKey, redisHashKey);

        // 2. 상품수량, 만료일자 셋팅
        RedisCartDto saveCartItem =
                setAddCartData(cartItem, requestCartDto);
        // 3. Redis에 상품 등록
        saveServiceCartInRedis(redisKey, redisHashKey, saveCartItem);

        // 4. 장바구니 상품 단건 반환
        return redisCartMapper.toResponseDto(saveCartItem);
    }

    // 장바구니 조회 Redis Hash Key 생성
    private String createCartRedisHashKey(String productCode, String optionCode) {
        return productCode + ":" + optionCode;
    }

    // 장바구니 상품옵션 단건 조회
    private RedisCartDto getCartItem(String redisKey, String redisHashKey) {
        // 1. redis 조회
        Object redisCartObj =
                redisSingleDataService.getSingleHashValueData(
                        CART, redisKey, redisHashKey);

        RedisCartDto targetRedisCartDto = null;
        // 2. object -> redisCartDto로 변환
        if(redisCartObj != null) {
            targetRedisCartDto =
                    objectMapper.convertValue(redisCartObj, RedisCartDto.class);
        }

        return targetRedisCartDto;
    }

    // 상품옵션수량 셋팅
    private RedisCartDto setAddCartData(RedisCartDto targetRedisCartDto,
                                RequestCartDto requestCartDto) {
        RedisCartDto result;

        // 1. 요청 상품이 장바구니에 미존재 시 상품옵션조회 (DB)
        if (targetRedisCartDto == null) {
            result = findOptionDtoById(
                    requestCartDto.getProductCode(),
                    requestCartDto.getOptionCode());
            // 장바구니 신규 등록이므로 수량 0으로 초기화
            result.setQuantity(0);

        } else {
            result = targetRedisCartDto;
        }

        // 2. 상품수량 셋팅 (기존수량 + 신규수량)
        result.setQuantity(
                result.getQuantity() + requestCartDto.getQuantity());

        return result;
    }

    // 상품옵션에 해당하는 상품옵션 조회
    private RedisCartDto findOptionDtoById(String productCode, String optionCode) {
        // 상품옵션조회
        return productOptionRepository
                .findByProductCodeAndOptionCodeOfOnSale(productCode, optionCode)
                .orElseThrow(() ->
                        new ProductException(PRODUCT_OPTION_NOT_EXIST));
    }

    // 장바구니에 상품옵션 단건등록
    private void saveServiceCartInRedis(String key,
                                        String hashKey,
                                        RedisCartDto hashValue) {
        // 장바구니에 상품옵션 단건등록
        redisSingleDataService.saveSingleHashValueData(
                CART, key, hashKey, hashValue);
        // 만료기간 설정
        redisSingleDataService.setExpire(
                CART, key, Duration.ofDays(EXPIRATION_PERIOD));
    }
}
