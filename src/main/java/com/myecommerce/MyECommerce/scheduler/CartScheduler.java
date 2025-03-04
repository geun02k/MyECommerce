package com.myecommerce.MyECommerce.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.dto.cart.ServiceCartDto;
import com.myecommerce.MyECommerce.service.redis.RedisMultiDataService;
import com.myecommerce.MyECommerce.service.redis.RedisSingleDataService;
import com.myecommerce.MyECommerce.type.RedisNamespaceType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.myecommerce.MyECommerce.type.RedisNamespaceType.CART;

@Component
@RequiredArgsConstructor
public class CartScheduler {

    private final ObjectMapper objectMapper;

    private final RedisSingleDataService redisSingleDataService;
    private final RedisMultiDataService redisMultiDataService;

    /** 사용자 장바구니에서 만료된 상품 삭제 **/
    @Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시 실행
    public void removeExpiredProducts() {
        // 만료일자 생성
        LocalDate expireDate = LocalDate.now();

        Cursor<byte[]> cursor = null;
        try{
            do {
                // redis 장바구니 Scan
                cursor = redisMultiDataService.getNameSpaceScan(CART);

                // scan한 key 데이터 byte -> String 변환
                Set<String> keys = getKeysToString(cursor);

                keys.forEach(key -> {
                    // userId 파싱
                    String userId = getUserIdFromKey(key, CART);
                    // 유저의 상품목록 조회
                    Map<Object, Object> hashEntries =
                            redisMultiDataService.getHashEntries(CART, userId);
                    // 만료상품삭제
                    deleteExpiredProductsInRedis(userId, expireDate, hashEntries);
                });
            } while (cursor != null && cursor.hasNext());

        } finally {
            // 커서닫기
            closeCursor(cursor);
        }
    }

    // scan 데이터 byte -> String 변환
    private Set<String> getKeysToString(Cursor<byte[]> cursor) {
        Set<String> keys = new HashSet<>();
        cursor.forEachRemaining(byteKey -> {
            String key = new String(byteKey);
            keys.add(key);
        });
        return keys;
    }

    // key에서 userId 조회
    private String getUserIdFromKey(String key, RedisNamespaceType nameSpace) {
        return key.substring(nameSpace.toString().length()+1);
    }

    // Redis에서 만료된 상품목록을 삭제
    private void deleteExpiredProductsInRedis(String userId,
                                              LocalDate expireDate,
                                              Map<Object, Object> hashEntries) {
        hashEntries.forEach((productId, product) -> {
            // object -> dto로 변환
            ServiceCartDto optionDto =
                    objectMapper.convertValue(product, ServiceCartDto.class);
            // 만료일자 지나면 삭제
            if (optionDto.getExpiryDate().isBefore(expireDate)) {
                redisSingleDataService.deleteSingleHashValueData(
                        CART, userId, productId.toString());
            }
        });
    }

    // 커서 멍시적 닫기
    private void closeCursor(Cursor<byte[]> cursor) {
        // 커서닫기
        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }
}
