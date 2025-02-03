package com.myecommerce.MyECommerce.service.redis;

import com.myecommerce.MyECommerce.config.RedisConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisSingleDataService {

    private final RedisConfig redisConfig;

    /** Redis 단일 데이터 등록 **/
    public void saveSingleData(String key, Object value, Duration duration) {
        redisConfig.redisTemplate().opsForValue().set(key, value, duration);
    }

    /** Redis 단일 데이터 삭제 **/
    public void deleteSingleData(String key) {
        redisConfig.redisTemplate().delete(key);
    }

    /** Redis 단일 테이터 조회 **/
    public Object getSingleData(String key) {
        return redisConfig.redisTemplate().opsForValue().get(key);
    }

}
