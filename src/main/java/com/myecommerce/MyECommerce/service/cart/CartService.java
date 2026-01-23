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

import static com.myecommerce.MyECommerce.exception.errorcode.ProductErrorCode.PRODUCT_NOT_EXIST;
import static com.myecommerce.MyECommerce.type.RedisNamespaceType.CART;

@Service
@RequiredArgsConstructor
public class CartService {
    // 장바구니 최대 데이터 수
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
        //       - hashKey = 등록할 상품옵션ID
        //       - hashValue = 등록할 상품옵션 정보

        // 0. 정책검증
        cartPolicy.validateAdd(requestCartDto.getProductCode(), member);

        // 요청 상품옵션에 대한 장바구니 조회
        //getOptionFromRedisCart();
        String redisKey = member.getUserId();
        String redisHashKey = requestCartDto.getProductCode().toString()
                + ":" + requestCartDto.getOptionCode().toString();
        Object redisCartObj =
                redisSingleDataService.getSingleHashValueData(
                        CART, redisKey, redisHashKey);

        // object -> redisCartDto로 변환
        RedisCartDto targetRedisCartDto =
                objectMapper.convertValue(redisCartObj, RedisCartDto.class);

        // 1. 상품옵션조회 (DB)
        RedisCartDto foundOptionDto = findOptionDtoById(
                requestCartDto.getProductCode(),requestCartDto.getOptionCode());
        // 2. 상품수량, 만료일자 셋팅
        setAddCartData(targetRedisCartDto, foundOptionDto, requestCartDto, redisKey);
        // 3. Redis에 상품 등록
        saveServiceCartInRedis(redisKey, redisHashKey, targetRedisCartDto);

        // 4. 장바구니 상품 단건 반환
        return redisCartMapper.toResponseDto(targetRedisCartDto);
    }

    // 상품옵션수량 셋팅
    private void setAddCartData(RedisCartDto targetRedisCartDto,
                                RedisCartDto foundOptionDto,
                                RequestCartDto requestCartDto,
                                String redisKey) {
        // 1. 상품수량 셋팅
        if (targetRedisCartDto != null) {
            // 상품수량 (기존수량 + 신규수량)
            targetRedisCartDto.setQuantity(
                    targetRedisCartDto.getQuantity() + requestCartDto.getQuantity());
        } else {
            // 상품수량 (신규수량으로 초기화)
            targetRedisCartDto = foundOptionDto;
            targetRedisCartDto.setQuantity(requestCartDto.getQuantity());
        }

        // 2. 만료일자 셋팅
        redisSingleDataService.setExpire(
                redisKey, Duration.ofDays(EXPIRATION_PERIOD));
    }

    // 상품옵션ID에 해당하는 상품옵션 Entity 조회해 ServiceCartDto로 변환해 반환
    private RedisCartDto findOptionDtoById(String productCode, String optionCode) {
        // 상품옵션조회
        return productOptionRepository
                .findByProductCodeAndOptionCodeOfOnSale(productCode, optionCode)
                .orElseThrow(() ->
                        new ProductException(PRODUCT_NOT_EXIST));
    }

    // 장바구니에 상품옵션 단건등록
    private void saveServiceCartInRedis(String key,
                                        String hashKey,
                                        RedisCartDto hashValue) {
        redisSingleDataService.saveSingleHashValueData(
                CART, key, hashKey, hashValue);
    }
}
