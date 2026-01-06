# MY E-COMMERCE PROJECT REDIS STUDY
2025 간단한 이커머스 서비스를 만들면서 NoSQL인 Redis에 대해 공부한 내용


---
## < 목차 >
2. Redis
    - Redis
    - Redis 의존성 추가
    - Redis 실행하기
    - Java에서 redis client
3. redis 설정파일
    - redis 설정파일
    - 설정클래스(설정파일)을 직접 의존성 주입하는 방식에 대한 우려
    - Redis 역직렬화 오류해결
4. Redis 연결과 해제
5. Redis Pub/Sub
6. Redis 인스턴스와 네임스페이스
    - Redis 인스턴스와 네임스페이스
    - Redis에서 네임스페이스 분리 구현하기


---
## 2. Redis
> redis는 빠른성능, 다양한 데이터 구조 지원이 가장 큰 특징.

1. Redis(Remote Dictionary Server)
    - NoSQL 데이터베이스 중 하나이며 오픈소스 소프트웨어.
    - **키-값(Key-Value)** 형태로 데이터를 저장.
      다양한 종류의 value 저장 가능.
      value로 문자열, 리스트, 셋, 정렬된 셋, 해시 등의 타입을 지원.
    - 데이터를 **인-메모리 데이터 저장소**에 저장.
      서버의 메인 메모리에 모든 데이터를 저장하므로, 디스크 I/O를 거치는 다른 데이터베이스 시스템보다 훨씬 빠른 성능을 보여줌.
    - 메모리에 저장되는 데이터 베이스는 디스크에 저장하여 휘발성으로 사용되는 **데이터를 영구적으로 저장**하는 기능을 제공하여 서버가 다운되더라도 데이터를 복구가능.

2. redis 의존성 추가
- spring-boot-starter-data-redis
    - Spring Data Redis를 쉽게 사용할 수 있도록 도와주는 라이브러리.
    - 내장 Lettuce Client를 사용해 Redis의 데이터를 키-값 형태의 저장소로 이용이 가능.
    - Lettuce Client를 이용해 Redis 데이터베이스에 접근하고 데이터를 저장, 조회하는 등의 작업을 수행가능.
    - Redis 서버와 연결하여 데이터를 처리하기 위해 해당 서버의 호스트와 포트 정보를 설정 필요.

3. redis 실행하기
   redis를 설치했음을 가정한다.
    - redis 서버 실행
        - C:\Program Files\Redis\redis-server.exe
    - redis client 실행
        - C:\Program Files\Redis\redis-cli.exe
    - redis insight
        - redis 데이터베이스를 시각화하고 관리하기 위한 GUI 도구. (redis client로 보면 될듯)
- 참고 블로그   
  https://seodaeya.tistory.com/248

3. Java에서 redis client (Lettuce 선택)
    - redis client를 통해 할 수 있는 작업을 Java로 작성가능하도록 한다.
    1. Lettuce
        - 동기, 비동기 통신을 모두 지원하는 non-blocking 자바 레디스 클라이언트.
        - 대량의 요청과 응답 처리에 있어서, Lettuce가 더욱 유리하다. TPS, CPU, Connection 수, 응답속도 등 모든 면에서 Lettuce가 우위이기 때문이다.
    2. Jedis
        - 사용 편의성을 위해 설계된 레디스 자바 클라이언트.
        - 동기식으로만 작동.
        - 타 redis client에 비해 가벼움.
- 참고블로그    
  https://devforme.tistory.com/46

- 참고블로그   
  https://adjh54.tistory.com/459   
  https://adjh54.tistory.com/448

- redis 사용 명령어
  https://jang8584.tistory.com/290


---
## 3. redis 설정파일
### < redis 설정파일 >
1. RedisConnectionFactory 인터페이스
    - Redis와의 연결을 위한 Connection을 생성하고 관리.

2. LettuceConnectionFactory
    - 해당 클래스는 RedisConnectionFactory 인터페이스를 구현한 클래스.

3. RedisTemplate<String, Object>
    - Spring Data Redis에서 제공하는 클래스.
    - Redis 데이터베이스에 접근하고 데이터를 처리하는 데 사용.
    - 타입 안전성을 제공하고, 직렬화 및 역직렬화를 처리하며, Redis의 기본적인 데이터 구조를 지원.

- 발생오류
  > ava.lang.IllegalArgumentException: template not initialized; call afterPropertiesSet() before using it
  at org.springframework.util.Assert.isTrue(Assert.java:116) ~[spring-core-6.2.1.jar:6.2.1]
  at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:394) ~[spring-data-redis-3.4.1.jar:3.4.1]
  at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:378) ~[spring-data-redis-3.4.1.jar:3.4.1]
  at org.springframework.data.redis.core.AbstractOperations.execute(AbstractOperations.java:117) ~[spring-data-redis-3.4.1.jar:3.4.1]
  at org.springframework.data.redis.core.DefaultValueOperations.set(DefaultValueOperations.java:208) ~[spring-data-redis-3.4.1.jar:3.4.1]
  at org.springframework.data.redis.core.ValueOperations.set(ValueOperations.java:75) ~[spring-data-redis-3.4.1.jar:3.4.1]
  at com.myecommerce.MyECommerce.service.redis.RedisSingleDataService.saveSingleData(RedisSingleDataService.java:18) ~[main/:na]

- 발생원인
    - RedisTemplate 또는 유사한 Spring Bean이 초기화되지 않았거나 설정되지 않은 상태에서 사용하려고 시도했을 때 발생

- 해결방법
    - RedisConfig.java에 설정한 redisTemplate() 메서드 호출 시 오류발생.
    - 빈으로 등록하지 않아 발생한 오류.
    - redisTemplate() 위에 @Bean 어노테이션 추가해 해결.


### < 설정클래스(설정파일)을 직접 의존성 주입하는 방식에 대한 우려 >
1. 설정클래스(설정파일)을 직접 의존성 주입
    - 보통 설정 파일(또는 설정 클래스)을 직접 의존성 주입하는 것은 권장되지 않습니다.
    - 그 이유는 설정 파일은 주로 애플리케이션의 설정을 구성하고 빈을 등록하는 역할을 하기 때문입니다.
    - 설정 파일은 일반적으로 스프링 컨테이너에 의해 자동으로 관리되며, 해당 설정이 적용된 빈을 다른 클래스에서 사용할 수 있도록 하는 것이 목표입니다.
    - 설정 파일을 직접적으로 의존성 주입하는 것은 애플리케이션의 설계 원칙에 위배될 수 있고, 유지보수 및 확장성 측면에서 바람직하지 않습니다.
    - **설정 클래스에서 관리하는 빈을 다른 컴포넌트에서 주입받는 것이 권장**되는 방법입니다.

2. 설정파일을 직접 의존성 주입하는 방법이 권장되지 않는 이유
    1. 설정 파일의 책임
        - 설정 클래스는 주로 빈 등록과 환경 설정을 담당합니다.
        - 직접적으로 비즈니스 로직을 담당하는 클래스에 의존성을 주입하는 것은 관심사의 분리 원칙을 위반할 수 있습니다.
        - 설정 클래스는 애플리케이션의 설정을 제공하는 역할에 집중해야 합니다.
    2. 스프링 컨테이너의 관리
        - 스프링 컨테이너는 설정 클래스를 관리하고, 이를 기반으로 필요한 빈을 자동으로 생성하여 주입합니다.
        - 설정 클래스를 직접적으로 주입하는 대신, 스프링 컨테이너가 관리하는 빈(예: RedisTemplate)을 주입받는 것이 더 자연스럽고,
          이를 통해 의존성 주입이 잘 이루어집니다.
    3. 재사용성
        - 설정 클래스를 다른 서비스나 컴포넌트에서 직접 주입받는다면, 해당 설정을 사용할 때마다 불필요하게 설정 파일을 직접 의존하게 됩니다.
        - 반면, 설정 클래스에서 필요한 빈을 @Bean으로 등록해 두면, 스프링 컨테이너가 해당 빈을 관리하고
          필요한 클래스에서 이 빈만 주입받아 사용하면 됩니다.

3. 설정파일의 직접 의존성 주입을 피하는 방식
    - 설정 클래스를 의존성 주입하는 대신, 설정 클래스는 단지 빈을 등록하고 관리하는 역할만 합니다.
    - 예를 들어, RedisConfig 클래스를 통해 RedisTemplate을 생성하고, 다른 클래스에서 이를 주입받아 사용하는 방식이 이상적입니다.
    - 이렇게 설정 클래스에서 RedisTemplate을 @Bean으로 등록한 후, RedisTemplate을 사용하는 다른 서비스 클래스에서는 다음과 같이 주입받습니다.

4. 편리한 테스트 및 유지보수
    - 설정 클래스를 직접 주입받지 않고, 빈을 주입받는 방식은 단위 테스트와 유지보수 측면에서도 더 유리합니다.
    - 설정 클래스는 구현 세부사항을 숨기고, 빈을 주입하는 방식으로 구성 요소들의 독립성을 높이는 데 유리합니다.

5. Redis 설정파일 직접 의존하지 않도록 변경하기    
   RedisTemplate을 @Bean으로 등록했기 때문에 RedisSingleDataService에서 redisConfig.redisTemplate()처럼 직접 주입하는 것이 아니라,
   필요한 곳에서 RedisTemplate을 주입하는 방식이 더 나은 설계입니다.

현재 RedisConfig 클래스에서 @Bean을 사용하여 redisTemplate을 생성 및 등록하고 있습니다.     
스프링의 의존성 주입 방식 중 redisTemplate을 다른 컴포넌트에서 사용하기 위해서 설정 클래스에서 빈을 등록합니다.

설정 클래스인 RedisConfig를 다른 컴포넌트에서 의존성으로 주입받는 대신,
redisTemplate을 직접 필요한 클래스에서 주입받도록 합니다.
RedisConfig를 다른 클래스에서 직접 주입받는 것보다 RedisTemplate을 주입받는 것이 더 자연스럽고, 더 직관적인 방식입니다.

**설정 클래스에서 @Bean으로 RedisTemplate을 정의**했기 때문에, 이를 **필요한 클래스에서 @Autowired나 생성자 주입을 통해 주입받는 것이 좋습니다.**
RedisTemplate을 @Bean으로 등록하면 스프링 컨테이너에서 자동으로 관리되기 때문에 다른 클래스에서 이를 주입받아 사용하면 됩니다.
따라서 RedisTemplate을 사용하면 되는데 굳이 RedisConfig 클래스를 의존성 주입으로 사용할 필요는 없습니다.

- redis 설정
    ~~~
    @Configuration
    public class RedisConfig {
    
        @Value("${spring.data.redis.host}")
        private String host;
        @Value("${spring.data.redis.port}")
        private int port;
    
        /** Lettuce를 사용한 Connection 객체 생성 **/
        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            final RedisStandaloneConfiguration redisStandaloneConfig = new RedisStandaloneConfiguration();
    
            // ...
            
            return new LettuceConnectionFactory(redisStandaloneConfig);
        }
    
        /** RedisTemplate 설정
         *  : Redis에 저장할 데이터형식 제공 **/
        @Bean
        public RedisTemplate<String, Object> redisTemplate() {
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    
            // 사용할 Connection 설정
            redisTemplate.setConnectionFactory(redisConnectionFactory());
             
            // ...
             
            return redisTemplate;
        }
    }
    ~~~
- redis 사용부분 올바르게 수정
    ~~~
    @Service
    @RequiredArgsConstructor
    public class RedisSingleDataService {
        // 설정파일이 아닌 bean에 의존하도록 의존성주입
        // 기존 : private final RedisConfig redisConfig;
        private final RedisTemplate<String, Object> redisTemplate;
    
        /** Redis 단일 데이터 등록 **/
        public void saveSingleData(String key, Object value, Duration duration) {
            // 기존 : redisConfig.redisTemplate().opsForValue().set(key, value, duration);        
            redisTemplate.opsForValue().set(key, value, duration);
        }
    
        // ... 
    }
    ~~~


### < Redis 역직렬화 오류해결 >
1. 발생오류
    - java.lang.RuntimeException: com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot construct instance of `com.myecommerce.MyECommerce.dto.cart.ResponseCartDto` (although at least one Creator exists): no String-argument constructor/factory method to deserialize from String value ('{"productId":1,"productName":"제품1","optionId":1,"optionName":"스몰사이즈 화이트컬러","price":76000.00,"quantity":30}')
      at [Source: REDACTED (`StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION` disabled); line: 1, column: 1]

2. 발생원인
    - Redis에 Json으로 직렬화해 저장한 후 조회해 데이터를 역직렬화 해 DTO로 변경 시 역직렬화 오류발생.
    - Redis에 Object로 저장해도 오류가 발생했다.
    - 기존 RedisTemplate의 설정에서 key와 value 모두 StringRedisSerializer로 설정.      
      이 경우 key와 value 모두 문자열로 직렬화되기 때문에 객체를 문자열로 변환하여 Redis에 저장하고, 문자열로 가져오는 방식으로 처리됨.     
      이 방식은 객체를 JSON으로 직렬화하고 역직렬화하는 방식이 아니기 때문에 객체의 복잡한 구조를 제대로 다루지 못하고,
      단순한 문자열로 저장되어 오류가 발생.
      StringRedisSerializer는 문자열(String)만 처리.
      즉, 객체를 문자열로 직렬화하면 객체의 필드나 구조 정보가 손실되고, 그대로 문자열로 저장됨.
      객체를 JSON 형태로 저장하려면 StringRedisSerializer 대신 Jackson2JsonRedisSerializer와 같은 JSON 직렬화 방식을 사용해야함.
      StringRedisSerializer는 객체를 JSON 형식이 아닌 문자열로 저장하고 역직렬화할 때 형식 불일치가 발생가능.
      이로 인해 ResponseCartDto와 같은 객체로 변환 시 오류 발생.
   ~~~
    @Configuration
    public class RedisConfig {
        // ...
     
        /** RedisTemplate 설정
         *  : Redis에 저장할 데이터형식 제공 **/
        @Bean
        public RedisTemplate<String, Object> redisTemplate() {
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    
            // 사용할 Connection 설정
            redisTemplate.setConnectionFactory(redisConnectionFactory());
            // key에 사용할 직렬화 명시
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            // value에 사용할 직렬화 명시 (오류발생원인)
            redisTemplate.setValueSerializer(new StringRedisSerializer());
            // 그 외 사용할 직렬화 명시
            redisTemplate.setDefaultSerializer(new StringRedisSerializer());
    
            return redisTemplate;
        }
    }   
   ~~~

3. 해결방법
    - Redis에서 객체를 JSON 형식으로 직렬화하고 역직렬화하려면 Jackson2JsonRedisSerializer를 사용해야함.
   ~~~
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
        // ...
    
        /** RedisTemplate 설정
         *  : Redis에 저장할 데이터형식 제공 **/
        @Bean
        public RedisTemplate<String, Object> redisTemplate() {
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    
            // 사용할 Connection 설정
            redisTemplate.setConnectionFactory(redisConnectionFactory());
            
            // key에 사용할 직렬화 명시 (Key는 String으로 직렬화)
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            
            // value에 사용할 직렬화 명시 (Value는 Jackson2Json으로 직렬화)
            Jackson2JsonRedisSerializer<Object> jsonSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
            redisTemplate.setValueSerializer(jsonSerializer);
    
            // 그 외 사용할 직렬화 명시 (디폴트 직렬화 방법 설정)
            redisTemplate.setDefaultSerializer(jsonSerializer);
    
            return redisTemplate;
        }
    }
   ~~~
    - Key 직렬화
        - key는 여전히 문자열로 저장할 수 있으므로 StringRedisSerializer를 사용합니다.
    - Value 직렬화
        - value는 객체이므로, Jackson2JsonRedisSerializer를 사용하여 객체를 JSON 형태로 직렬화합니다.
          이 설정은 객체를 JSON으로 변환하여 Redis에 저장하고, 다시 Redis에서 가져올 때 JSON을 객체로 역직렬화합니다.


---
## 4. Redis 연결과 해제
- Spring Data Redis에서 자동 연결 관리를 수행하기 때문에
  RedisTemplate을 사용하게되면 Redis 연결을 명시적으로 생성하거나 끊는 로직은 일반적으로 불필요.

1. Spring Data Redis의 자동 연결 관리
    - RedisTemplate은 RedisConnectionFactory를 통해 Redis와의 연결을 자동으로 관리.
    - Spring Data Redis는 연결 풀(connection pool)을 사용하여 Redis와의 연결을 효율적으로 처리.
      이로 인해 별도의 연결 및 종료 로직 없이도 Redis와의 연결이 자동으로 관리됨.

2. RedisTemplate과 connection pool
    - RedisTemplate은 기본적으로 Lettuce나 Jedis와 같은 Redis 클라이언트를 사용.
      이들 클라이언트는 연결 풀을 사용하여 연결을 재사용.
      즉, 연결을 반복적으로 생성하고 끊는 대신, 연결 풀에서 연결을 가져오고 반환하는 방식으로 동작.
    - Spring에서는 LettuceConnectionFactory 또는 JedisConnectionFactory를 사용하여 연결 풀을 설정.
      Redis 연결이 필요한 작업을 할 때마다 RedisTemplate은 자동으로 이 연결을 사용하고, 작업이 끝난 후 연결을 풀에 반환.

3. connection pool의 이점
    - Redis 서버와의 연결을 효율적으로 재사용하여 성능을 최적화 가능.
    - 연결 풀을 사용하면 새로운 연결을 생성하는 데 드는 비용을 줄이고, 자원의 낭비를 막을 수 있음.
    - Redis 서버와의 연결이 자주 생성되고 끊어지는 대신, 한 번 연결된 세션을 여러 작업에서 재사용 가능.

4. 연결을 끊는 로직이 필요한 경우
    - 특정 종료 작업이 필요한 경우가 있을 수 있습니다.
    - Redis와의 연결을 명시적으로 종료하고자 할 때는 @PreDestroy 어노테이션 사용 가능.
    - 하지만 대부분의 경우, Spring의 연결 관리 시스템에 맡겨두면 충분.
    - 예외상황
        - Redis 서버와의 연결을 명시적으로 종료하고자 할 때 (예: 애플리케이션 종료 시).
        - 테스트 환경에서 Redis 연결을 명시적으로 종료해야 할 경우.
        - 네트워크 문제로 Redis 연결이 끊어졌을 때, 다시 연결을 시도하는 로직이 필요할 수 있습니다.

   ~~~ 
    @PreDestroy
    public void close() {
        // 직접적으로 연결 종료가 필요한 경우
        if (redisConnectionFactory != null) {
            redisConnectionFactory.destroy();
        }
    }
   ~~~


---
## 5. Redis Pub/Sub
- **발행/구독(Publish/Subscribe) 패턴을 Redis에서 구현한 기능.**
- 메시지 시스템을 구현하여 서로 다른 애플리케이션 간 쉽게 통신가능.
- "발행자(publisher)"가 메시지를 보내면, 이를 구독하는 "구독자(subscriber)"가 메시지를 받아 처리하는 구조.

1. Redis Pub/Sub 기본 개념
    - Publisher (발행자): 특정 채널에 메시지를 "발행"하는 역할을 합니다.
    - Subscriber (구독자): 특정 채널을 "구독"하고, 해당 채널에서 발행된 메시지를 받는 역할을 합니다.

2. Pub/Sub 흐름
    1. 발행자는 Redis에 특정 "채널"에 메시지를 발행.
    2. 구독자는 해당 채널을 구독하고, 메시지가 발행될 때 이를 실시간으로 수신.
    3. 메시지는 구독자가 구독한 채널에 실시간으로 전달되며, 구독자는 메시지 처리.
    4. 발행자는 상품의 판매 가능 수량이 변경되었을 때, 특정 채널에 메시지를 발행.
    5. 구독자는 이 채널을 구독하고, 판매 가능 수량이 변경되었음을 알게 되면 해당 상품을 장바구니에서 품절 처리.

3. Redis Pub/Sub 사용하기
    - **PUBLISH, SUBSCRIBE, PSUBSCRIBE** 명령어를 통해 Pub/Sub 기능을 사용가능.

4. 장바구니와 품절 상태 동기화
    - Redis Pub/Sub을 사용하면 실시간으로 품절 상태를 동기화 가능.
    - 사용자 장바구니에 이미 담겨 있는 상품이 품절되었을 경우, Redis Pub/Sub을 통해 실시간으로 해당 상태를 반영가능.

5. 장점
    - 실시간 업데이트
        - 상품이 품절되면, Redis Pub/Sub을 통해 실시간으로 장바구니에 반영되므로 빠르고 즉각적인 동기화가 가능.
    - 비동기 처리
        - 발행자와 구독자가 서로 독립적으로 작업할 수 있기 때문에 비동기적 처리가 가능. 이는 시스템의 성능과 확장성을 높이는 데 유리.
    - 효율적인 품절 상태 처리
        - 상품 품절 상태를 실시간으로 처리할 수 있어 사용자 경험이 향상.



---
## 6. Redis 인스턴스와 네임스페이스
### < Redis 인스턴스와 네임스페이스 >
> Redis는 데이터베이스 간에 직접적인 상호작용을 지원하지 않으므로 한 네임스페이스에서 다른 네임스페이스의 데이터를 조회하거나 수정 불가능.
> 인스턴스와 네임스페이스는 서로 다른 개념으로, Redis 인스턴스는 서버 수준에서, 네임스페이스는 데이터베이스 수준에서 데이터를 구분하는 역할을 수행.
> Redis의 네임스페이스를 잘 활용하면, 하나의 Redis 인스턴스 내에서 여러 애플리케이션을 위한 별도의 저장 공간을 효율적으로 운영가능.

1. Redis 인스턴스
    - Redis 서버를 실행하는 단일 프로세스.
    - Redis 인스턴스는 하나의 Redis 서버를 의미.
    - Redis 인스턴스는 일반적으로 특정 포트를 사용하여 클라이언트와 연결되고 다양한 데이터 구조(예: 문자열, 리스트, 셋, 해시 등)를 저장가능.
    - 하나의 서버에 여러 인스턴스를 실행할 수 있으며, 각 인스턴스는 고유한 포트를 사용.
    - redis-server를 실행하면 기본적으로 6379번 포트에서 Redis 인스턴스가 실행.
    - 추가적으로 6380, 6381번 포트로 또 다른 Redis 인스턴스를 실행가능.

2. Redis 네임스페이스 (Redis DB)
    - Redis는 기본적으로 여러 개의 "데이터베이스"를 지원하고 이 데이터베이스들을 네임스페이스라고 부를 수 있음.
    - 물리적인 분리 없이 논리적으로 데이터를 분리하는 방식으로 여러 데이터베이스를 의미.
    - 기본적으로 0번부터 15번까지 총 16개의 데이터베이스 제공.
    - 각 네임스페이스는 별도의 인덱스를 갖고 있으며, Redis 클라이언트는 SELECT 명령을 사용하여 특정 데이터베이스를 선택가능.
    - 기본적으로 Redis는 0번 데이터베이스를 사용하며 별도로 설정하지 않으면 모든 데이터는 이 데이터베이스에 저장.
    - Redis에서 여러 데이터베이스를 사용 가능하지만, 네임스페이스 간의 데이터는 물리적으로 분리되지 않고 메모리 내에서 별도로 관리됨.
    - 데이터베이스 번호를 변경하려면 SELECT <DB 번호> 명령을 사용.
   ~~~
    SELECT 1
   ~~~

유효한 로그인 토큰 정보와 장바구니 내용을 분리해 저장하기 위해서는 네임스페이스를 이용해 분리하는 게 좋은 방법이겠지?
유효한 로그인 토큰 정보와 장바구니 내용을 분리해 저장하려는 경우, Redis의 네임스페이스(혹은 데이터베이스)를 활용하는 것이 좋은 방법입니다. 여러 가지 이유로 이를 분리하는 것이 유리합니다

- 논리적 분리  
  로그인 토큰과 장바구니 내용은 서로 다른 도메인과 관련된 데이터이기 때문에, 각기 다른 데이터베이스(네임스페이스)로 분리하는 것이 관리에 용이합니다. 예를 들어, 로그인 토큰은 인증과 관련된 데이터이고, 장바구니 내용은 사용자 행동과 관련된 데이터입니다. 이 둘을 별개의 데이터베이스에 저장하면 데이터의 역할과 목적에 맞는 관리가 가능합니다.
- 성능 최적화  
  Redis는 데이터베이스(네임스페이스) 간에 완전히 독립적이지 않지만, 데이터베이스를 나누어 놓으면 한 쪽에서 불필요한 데이터 조회나 수정이 발생해도 다른 데이터베이스에 영향을 미치지 않습니다.
  예를 들어, 로그인 토큰이 많이 만료되거나 갱신되는 작업이 있을 때, 그와 관련된 네임스페이스에만 영향을 미치고 장바구니 데이터는 영향을 받지 않습니다. 이를 통해 성능적인 장점도 있을 수 있습니다.
- 데이터 삭제 및 만료 정책 관리  
  로그인 토큰은 보통 일정 시간이 지나면 만료되거나 삭제됩니다. 이를 하나의 네임스페이스에서 처리하고, 장바구니 데이터는 사용자 세션이 종료될 때까지 유지될 수 있습니다.
  각 데이터베이스에서 개별적인 TTL(Time To Live) 설정을 통해, 로그인 토큰은 짧은 만료 시간을 설정하고 장바구니는 좀 더 긴 만료 시간을 설정하는 식으로 유연한 만료 정책을 적용할 수 있습니다.  
  예시 )  
  DB 0: 로그인 토큰을 저장 (예: 사용자 인증 정보, 토큰)
  DB 1: 장바구니 데이터를 저장 (예: 상품 목록, 수량, 가격 등)
  Redis 클라이언트에서는 다음과 같이 SELECT 명령으로 네임스페이스를 선택하고 데이터를 저장하거나 조회할 수 있습니다.

- 로그인 토큰을 위한 데이터베이스 선택   
  SELECT 0   
  SET user:token:12345 "valid-token"

- 장바구니 데이터를 위한 데이터베이스 선택   
  SELECT 1   
  SET cart:user123 "product1, product2, product3"

4. 세션 및 데이터 분리 관리  
   로그인 토큰은 보통 유저 인증 정보를 담고 있는 중요한 데이터이므로, 보안 측면에서도 별도로 취급하는 것이 좋습니다. 장바구니 데이터는 상대적으로 덜 민감하므로 서로 다른 네임스페이스로 분리하는 것이 좋습니다.
   예를 들어, 로그인 정보는 더 민감하므로 더 빠르고 안전한 접근이 필요하고, 장바구니는 주로 사용자 인터페이스와 밀접하게 관련되어 있어 다른 방식으로 접근하고 관리할 수 있습니다.

   로그인 토큰 정보와 장바구니 데이터를 분리해서 저장하는 것은 논리적 분리와 성능 최적화를 위해 매우 좋은 접근 방식입니다.
   네임스페이스를 나누어 관리하면, 각 데이터의 특성에 맞는 별도의 TTL 설정이나 관리 정책을 적용할 수 있어 더 효율적이고, 시스템 안정성도 높일 수 있습니다.


### < Redis에서 네임스페이스 분리 구현하기 >
Redis에서 네임스페이스를 분리하여 데이터를 저장하려면, RedisTemplate을 사용할 때 키에 네임스페이스를 추가하는 방식으로 구현가능.
이를 통해 키를 논리적으로 구분가능.

1. 네임스페이스를 분리하는 방법
    - Redis에서는 키를 통해 데이터를 저장하고 조회하므로,
      각 데이터베이스나 카테고리별로 네임스페이스를 구분하기 위해 키에 접두사를 추가하는 방법을 사용.
    - 예를 들어, 로그인 토큰을 저장할 때 login:<user_id> 와 같은 형태로 키를 설정하면,
      각 데이터가 고유한 네임스페이스를 가질 수 있음.
    - 아래 코드에서는 RedisTemplate을 이용해 키에 네임스페이스 접두어를 추가하여,
      로그인 토큰과 같은 데이터를 분리해서 저장하는 방법을 구현가능.
   ~~~
    @Service
    @RequiredArgsConstructor
    public class RedisSingleDataService {
    
        // redis에 로그인 토큰 저장 시 value값
        public static final String REDIS_NAMESPACE_FOR_LOGIN = "login";  // 네임스페이스(예: login)
        public static final String REDIS_NAMESPACE_FOR_CART = "cart";    // 네임스페이스(예: cart)
    
        private final RedisTemplate<String, Object> redisTemplate;
    
        /** Redis 단일 데이터 등록 - 네임스페이스 포함하여 키 저장 **/
        public void saveSingleData(String namespace, String key, Object value, Duration duration) {
            String namespacedKey = namespace + ":" + key;  // 네임스페이스를 포함한 키 생성
            redisTemplate.opsForValue().set(namespacedKey, value, duration);
        }
    
        /** Redis 단일 데이터 삭제&조회 **/
        public Object getAndDeleteSingleData(String namespace, String key) {
            String namespacedKey = namespace + ":" + key;  // 네임스페이스를 포함한 키 생성
            return redisTemplate.opsForValue().getAndDelete(namespacedKey);
        }
    
        /** Redis 단일 테이터 조회 **/
        public Object getSingleData(String namespace, String key) {
            String namespacedKey = namespace + ":" + key;  // 네임스페이스를 포함한 키 생성
            return redisTemplate.opsForValue().get(namespacedKey);
        }
    }
   ~~~
    - 네임스페이스 정의
        - 네임스페이스는 접두사로 사용되어 키를 분리하는 데 사용됨.
    - saveSingleData()
        - namespace를 접두사로 붙여서 고유한 키를 생성해 저장.
        - 로그인 토큰을 저장할 때는 login:user12345:token 형태로 저장됨.
        - 네임스페이스와 키를 결합하여 Redis에 데이터를 저장.

2. 추가 고려사항
    - 네임스페이스 접두사 사용
        - login:<user_id>와 같은 형식으로 접두사를 사용함으로써, Redis 인스턴스 내에서 데이터들이 구분됨.
        - 이는 한 Redis 인스턴스를 여러 애플리케이션이나 여러 서비스가 사용하는 경우에 매우 유용.
    - 확장성
        - 네임스페이스 접두사를 설정함으로써 향후 다른 종류의 데이터를 추가할 때도,
          cart:<user_id>와 같은 형식으로 쉽게 확장가능.
        - 이렇게 하면 Redis 인스턴스 내에서 데이터들이 논리적으로 분리되어 관리되므로
          로그인 토큰, 장바구니 등 각각의 데이터를 독립적으로 저장하고 관리가능.

