package com.myecommerce.MyECommerce.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    /** Lettuce를 사용한 Connection 객체 생성 **/
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        final RedisStandaloneConfiguration redisStandaloneConfig =
                new RedisStandaloneConfiguration();

        redisStandaloneConfig.setHostName(host);
        redisStandaloneConfig.setPort(port);

        return new LettuceConnectionFactory(redisStandaloneConfig);
    }

    /** RedisTemplate 설정
     *  : Redis에 저장할 데이터형식 제공 **/
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        // ObjectMapper 생성 후 Java 8 시간 관련 모듈 추가
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // LocalDate 및 기타 Java 8 시간 클래스 직렬화/역직렬화 지원

        Jackson2JsonRedisSerializer<Object> jsonSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        // 사용할 Connection 설정
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        // key에 사용할 직렬화 명시
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // value에 사용할 직렬화 명시
        redisTemplate.setValueSerializer(jsonSerializer);
        // 그 외 사용할 직렬화 명시 (디폴트 직렬화 방법 설정)
        redisTemplate.setDefaultSerializer(jsonSerializer);
        return redisTemplate;
    }
}
