package com.myecommerce.MyECommerce.service.redis;

import com.myecommerce.MyECommerce.type.RedisNamespaceType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

/** Redis를 직접 controller 하기위한 서비스 **/
@Service
@RequiredArgsConstructor
public class RedisMultiDataService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedisSingleDataService redisSingleDataService;

    /** Redis 해시 데이터 사이즈 조회 **/
    public Long getSizeOfHashData(RedisNamespaceType nameSpace,
                                  String key) {
        return redisTemplate.opsForHash()
                .size(redisSingleDataService.setKey(nameSpace, key));
    }

}
