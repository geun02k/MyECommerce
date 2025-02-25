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
    public void saveSingleData(RedisNamespaceType nameSpace, String key, Object value, Duration duration) {
        String nameSpaceKey = nameSpace + ":" + key; // 네임스페이스를 포함한 키 생성
        value = (value == null) ? ""  : value;
        redisTemplate.opsForValue().set(nameSpaceKey, value, duration);
    }

    /** Redis 단일 데이터 삭제&조회 **/
    public Object getAndDeleteSingleData(RedisNamespaceType nameSpace, String key) {
        String nameSpaceKey = nameSpace + ":" + key; // 네임스페이스를 포함한 키 생성
        return redisTemplate.opsForValue().getAndDelete(nameSpaceKey);
    }

    /** Redis 단일 테이터 조회 **/
    public Object getSingleData(RedisNamespaceType nameSpace, String key) {
        String nameSpaceKey = nameSpace + ":" + key; // 네임스페이스를 포함한 키 생성
        return redisTemplate.opsForValue().get(nameSpaceKey);
    }

}
