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

    /** Redis 단일 데이터 등록 (만료시간포함) **/
    public void saveSingleDataWithDuration(RedisNamespaceType nameSpace,
                               String key,
                               Object value,
                               Duration duration) {
        redisTemplate.opsForValue()
                .set(setKey(nameSpace, key), setObjectValue(value), duration);
    }

    /** Redis 단일 데이터 등록 **/
    public void saveSingleData(RedisNamespaceType nameSpace,
                               String key,
                               Object value) {
        redisTemplate.opsForValue()
                .set(setKey(nameSpace, key), setObjectValue(value));
    }

    /** Redis 단일 데이터 삭제&조회 **/
    public Object deleteSingleData(RedisNamespaceType nameSpace,
                                         String key) {
        return redisTemplate.delete(setKey(nameSpace, key));
    }

    /** Redis 단일 테이터 조회 **/
    public Object getSingleData(RedisNamespaceType nameSpace, String key) {
        return redisTemplate.opsForValue().get(setKey(nameSpace, key));
    }

    /** Redis Key에 대한 만료일자 설정 **/
    public Object setExpire(RedisNamespaceType nameSpace, String key, Duration duration) {
        return redisTemplate.expire(setKey(nameSpace, key), duration);
    }

    /** Redis 단일 해시 테이터 등록 **/
    public void saveSingleHashValueData(RedisNamespaceType nameSpace,
                                        String key,
                                        String hashKey,
                                        Object hashValue) {
        redisTemplate.opsForHash()
                .put(setKey(nameSpace, key), hashKey, setObjectValue(hashValue));
    }

    /** Redis 단일 해시 테이터 삭제 **/
    public void deleteSingleHashValueData(RedisNamespaceType nameSpace,
                                          String key,
                                          String hashKey) {
        redisTemplate.opsForHash().delete(setKey(nameSpace, key), hashKey);
    }

    /** Redis 단일 해시 테이터 조회 **/
    public Object getSingleHashValueData(RedisNamespaceType nameSpace,
                                         String key,
                                         String hashKey) {
        return (Object) redisTemplate.opsForHash()
                .get(setKey(nameSpace, key), hashKey);
    }

    // key값 셋팅 (네임스페이스를 포함한 키 생성)
    String setKey(RedisNamespaceType namespace, String key) {
        return namespace + ":" + key;
    }

    // value값 셋팅
    Object setObjectValue(Object value) {
        return (value == null) ? ""  : value;
    }
}
