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

    /** Redis 데이터 원자적 감소 (기존 값에서 특정 수 감소) **/
    public Long decrementData(RedisNamespaceType nameSpace,
                              String key,
                              Long decrementValue) {
        return redisTemplate.opsForValue().decrement(
                redisSingleDataService.setKey(nameSpace, key), decrementValue);
    }

    /** Redis 데이터 목록 삭제 **/
    public Long deleteMultiData(List<String> keyList) {
        return redisTemplate.delete(keyList);
    }

    /** Redis 데이터 목록 저장 **/
    public void saveMultiData(Map<String, Object> dataMap) {
         redisTemplate.opsForValue().multiSet(dataMap);
    }

    /** Redis 데이터 목록 조회 **/
    public List<Object> getMultiData(List<String> keyList) {
        return redisTemplate.opsForValue().multiGet(keyList);
    }

    // TODO: Redis 자료구조 기반으로 나누기 (레디스 데이터, 해시 데이터 다루는 서비스 분리 고려)
    /* ------------------
        Redis 해시 데이터
       ------------------ */

    /** Redis 해시 데이터 다건 삭제 **/
    public Long deleteMultiHashData(RedisNamespaceType nameSpace,
                                    String key,
                                    List<String> hashKeys) {
        if(hashKeys == null || hashKeys.isEmpty()) {
            return (long) 0;
        }

        return redisTemplate.opsForHash()
                .delete(redisSingleDataService.setKey(nameSpace, key), hashKeys);
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
