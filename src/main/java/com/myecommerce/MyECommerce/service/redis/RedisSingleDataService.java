package com.myecommerce.MyECommerce.service.redis;

import com.myecommerce.MyECommerce.type.RedisNamespaceType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/** Redis를 직접 controller 하기위한 서비스 **/
@Service
@RequiredArgsConstructor
public class RedisSingleDataService {

    private final RedisTemplate<String, Object> redisTemplate;

    /** Redis 단일 데이터 등록 **/
    public void saveSingleData(RedisNamespaceType nameSpace,
                               String key,
                               Object value,
                               Duration duration) {
        redisTemplate.opsForValue()
                .set(setKey(nameSpace, key), setObjectValue(value), duration);
    }

    /** Redis 단일 데이터 삭제&조회 **/
    public Object getAndDeleteSingleData(RedisNamespaceType nameSpace,
                                         String key) {
        return redisTemplate.opsForValue().getAndDelete(setKey(nameSpace, key));
    }

    /** Redis 단일 테이터 조회 **/
    public Object getSingleData(RedisNamespaceType nameSpace, String key) {
        return redisTemplate.opsForValue().get(setKey(nameSpace, key));
    }

    // key값 셋팅 (네임스페이스를 포함한 키 생성)
    private String setKey(RedisNamespaceType namespace, String key) {
        return namespace + ":" + key;
    }

    // value값 셋팅
    private Object setObjectValue(Object value) {
        return (value == null) ? ""  : value;
    }
}
