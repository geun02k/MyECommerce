package com.myecommerce.MyECommerce.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisSingleDataService {

    private final RedisTemplate<String, Object> redisTemplate;

    /** Redis 단일 데이터 등록 **/
    public void saveSingleData(String key, Object value, Duration duration) {
        redisTemplate.opsForValue().set(key, value, duration);
    }

    /** Redis 단일 데이터 삭제&조회 **/
    public Object getAndDeleteSingleData(String key) {
        return redisTemplate.opsForValue().getAndDelete(key);
    }

    /** Redis 단일 테이터 조회 **/
    public Object getSingleData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

}
