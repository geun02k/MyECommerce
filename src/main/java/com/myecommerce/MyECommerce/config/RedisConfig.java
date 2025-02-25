package com.myecommerce.MyECommerce.config;

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

        // 사용할 Connection 설정
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        // key에 사용할 직렬화 명시
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // value에 사용할 직렬화 명시
        Jackson2JsonRedisSerializer<Object> jsonSerializer =
                new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setValueSerializer(jsonSerializer);
        // 그 외 사용할 직렬화 명시 (디폴트 직렬화 방법 설정)
        redisTemplate.setDefaultSerializer(jsonSerializer);
        return redisTemplate;
    }
}
