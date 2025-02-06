package com.myecommerce.MyECommerce.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/** Redis를 직접 controller 하기위한 서비스 **/
@Service
@RequiredArgsConstructor
public class RedisSingleDataService {

    // redis에 로그인 토큰 저장 시 value값
    // (value값이 많아지면 enum으로 관리하겠지만 하나뿐이라 변수로 선언해 사용)
    public static final String REDIS_VALUE_FOR_LOGIN = "LOGIN";

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
