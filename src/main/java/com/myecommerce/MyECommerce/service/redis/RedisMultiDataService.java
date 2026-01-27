package com.myecommerce.MyECommerce.service.redis;

import com.myecommerce.MyECommerce.type.RedisNamespaceType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/** Redis를 직접 controller 하기위한 서비스 **/
@Service
@RequiredArgsConstructor
public class RedisMultiDataService {

    public static final int SCAN_COUNT = 100;

    private final RedisTemplate<String, Object> redisTemplate;

    private final RedisSingleDataService redisSingleDataService;

    /** Redis 데이터 목록 조회 **/
    public List<Object> getMultiData(List<String> keyList) {
        return redisTemplate.opsForValue().multiGet(keyList);
    }

    /** Redis 해시 데이터 사이즈 조회 **/
    public Long getSizeOfHashData(RedisNamespaceType nameSpace,
                                  String key) {
        return redisTemplate.opsForHash()
                .size(redisSingleDataService.setKey(nameSpace, key));
    }

    /** Redis 해시 데이터 목록 조회 **/
    public Map<Object, Object> getHashEntries(RedisNamespaceType nameSpace,
                                              String key) {
        return redisTemplate.opsForHash()
                .entries(redisSingleDataService.setKey(nameSpace, key));
    }

    /** Redis namespace에 해당하는 데이터 목록 scan **/
    public Cursor<byte[]> getNameSpaceScan(RedisNamespaceType nameSpace) {
        // Scan 명령어를 이용해 namespace에 해당하는 모든 키목록 조회
        return redisTemplate.execute((RedisCallback<Cursor<byte[]>>) connection ->
                connection.scan(ScanOptions.scanOptions()
                        .match(nameSpace + ":*")
                        .count(SCAN_COUNT)
                        .build()));
    }

}
