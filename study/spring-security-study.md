# MY E-COMMERCE PROJECT SPRING SECURITY STUDY
2025 간단한 이커머스 서비스를 만들면서 공부한 스프링 시큐리티 내용


---
### < Spring Security >
- 인증, 인가 및 보호 기능을 제공하는 프레임워크.
  - 인증(Authentication) : 서비스의 사용자임을 확인.
  - 인가(Authorization) : 인증된 사용자가 서비스에 접근 시 요청된 자원에 대한 접근 허가.
- 유저 정보를 인증, 인증된 유저의 정보 권한을 추출해 요청한 자원에 접근 권한이 있는지 판단해 보안 수행.   
- 보안 작업은 까다롭지만 스프링 시큐리티를 이용하면 제공 기능들을 이용해 보다 쉽게 보안작업 진행가능.
- 스프링 시큐리티는 필터 방식으로 동작.
  - 인증, 인가에 대한 처리를 스프링 시큐리티 내부적 필터를 순차적으로 통과하면서 수행.
  - Spring Security 의존성을 추가하게 되면 15개의 **기본 필터들이 자동으로 적용**됨.
  - 설정에 따라 필요한 필터가있고 필요없는 필터가 있을 수 있음.
  - **SecurityConfig에서 필요하지 않은 필터들을 끌 수 있음**.
- HttpSecurity 
  - 실제 필터를 생성.
  - 해당 필터들은 WebSecurity 클래스를 통해 FilterChainProxy의 인자들로 전달됨.   
  - 사용자가 요청 > DelegatingFilterProxy가 가장 먼저 그 요청 받음 > FilterChainProxy에게 요청 위임 > 위임받은 요청에 해당하는 SecurityFilterChain이 필터 수행 > 반복...
- 커스터마이징 시 아래의 코드를 실행하지 않으면 다음 필터로 넘어가지 않음. 
  ~~~
  chain.doFilter(request, response);
  ~~~
- 참고 블로그   
  https://motti.tistory.com/entry/%EC%8A%A4%ED%94%84%EB%A7%81-%EC%8B%9C%ED%81%90%EB%A6%AC%ED%8B%B0Spring-security%EC%9D%98-%EC%9D%B4%ED%95%B4


---
### < Spring Security 아키텍처 >
- 시큐리티는 자신만의 세션공간인 SecurityContext를 가진다.   
  해당 세션에 들어갈 수 있는 객체는 Authentication 객체 뿐이다.   
  Authentication 객체는 UserDetails, OAuth2User 객체를 가질 수 있다.   
- 세션에 Authentication 객체가 들어가는 것   
  = 로그인 되었다는 의미
- SecurityContext에 Authentication 객체가 들어갈 수 있도록 Authentication 객체를 만드는 것이 스프링 시큐리티의 핵심.

- Spring Security 아키텍처 (Spring 요청 처리과정)
  1. 사용자의 요청이 서버로 들어옵니다.
  2. Authotication Filter가 요청을 가로채고 Authotication Manger로 요청을 위임합니다.
  3. Authotication Manager는 등록된 Authotication Provider를 조회하며 인증을 요구합니다.
  4. Authotication Provider가 실제 데이터를 조회하여 UserDetails 결과를 돌려줍니다.
  5. 결과는 SecurityContextHolder에 저장이 되어 저장된 유저정보를 Spring Controller에서 사용할 수 있게 됩니다.

- 참고블로그    
  https://www.elancer.co.kr/blog/detail/235


---
### < Spring Security 내부구조 >
1. 사용자가 자격 증명 정보를 제출하면, AbstractAuthenticationProcessingFilter가 Authentication 객체를 생성합니다.
   - AbstractAuthenticationProcessingFilter는 SecurityFilterChain 객체 반환.
2. Authentication 객체가 AuthenticationManager에게 전달됩니다.
3. 인증에 실패하면, 로그인 된 유저정보가 저장된 SecurityContextHolder의 값이 지워지고 RememberMeService.joinFail()이 실행됩니다. 그리고 AuthenticationFailureHandler가 실행됩니다.
4. 인증에 성공하면, SessionAuthenticationStrategy가 새로운 로그인이 되었음을 알리고, Authentication 이 SecurityContextHolder에 저장됩니다. 이후에 SecurityContextPersistenceFilter가 SecurityContext를 HttpSession에 저장하면서 로그인 세션 정보가 저장됩니다.
그 뒤로 RememberMeServices.loginSuccess()가 실행됩니다. ApplicationEventPublisher가 InteractiveAuthenticationSuccessEvent를 발생시키고 AuthenticationSuccessHandler 가 실행됩니다.


---
### < Spring Security 설정정보 >
- SecurityConfiguration.java
    - 스프링 시큐리티 설정정보파일.
    - WebSecurityConfigurerAdapter 상속받아 구현.   
      현재 3.4.1 버전 사용중이라고 생각했지만 실제로 라이브러리를 열어보면
      spring-security-config:6.4.2로 6.4.2버전을 사용중이다.   
      Spring Security 5.2.0 이상에서는 완전히 제거되어 사용불가.   
      스프링 공식 문서에서는 WebSecurityConfigAdapter 상속 대체 방법으로 @Bean을 생성하여 구성하는 기능을 권장.
    - 참고 블로그
        - https://sohyeonnn.tistory.com/46
        - https://velog.io/@pjh612/Deprecated%EB%90%9C-WebSecurityConfigurerAdapter-%EC%96%B4%EB%96%BB%EA%B2%8C-%EB%8C%80%EC%B2%98%ED%95%98%EC%A7%80
        - https://this-circle-jeong.tistory.com/162

- @EnableWebSecurity 어노테이션 적용필수.
  - @EnableWebSecurity   
    스프링 시큐리티를 활성화하고 웹 보안 설정 구성에 사용됨.   
    자동으로 스프링 시큐리티 필터 체인을 생성하고 웹 보안 활성화.   
    웹 보안 활성화 : 웹 보안 설정 활성화. 스프링 시큐리티의 필터 체인이 동작하여 요청을 인가하고 인증.   
    스프링 시큐리티 구성 : 스프링 시큐리티 구성을 위한 WegbSecurityConfigurer 빈 생성 / 구성 클래스에서 configure() 메서드를 오버라이딩하여 웹 보안 설정 구성가능.   
    다양한 보안 기능 추가 ex) 폼 기반 인증, 로그인 페이지 구성, 권한 설정 등 가능.   

- permitAll()
    - 해당경로에 대해 인증없이 무조건적으로 권한허용.
    - 회원가입, 로그인의 경우는 토큰없이 접근가능해야함. (JWT 토큰은 로그인 후 생성되므로.)
  ~~~
  http
      .authorizeHttpRequests((authorizeRequests) ->
            // 모든 경로에 대해 접근허용
            authorizeRequests.anyRequest().permitAll());
  ~~~


---
### < Spring Security 의존성 추가 시 api호출불가 에러해결 >
- 발생에러
    - 스프링 시큐리티 의존성 추가 후 api 호출 시 401 에러 발생 -> api 호출 불가.
    - 참고블로그
        - https://velog.io/@readnthink/Spring-Security-%EC%97%90%EB%9F%AC
        - https://ttasjwi.tistory.com/148
- 발생원인
    - Spring Security 의존성을 추가하면 Client에서 API를 통해 요청 시
      인증계층(Filter Chain)을 거친 후 EventHandler를 거쳐 Controller로 가게 된다.   
      여기서 인증계층(FilterChain)의 기본값은 모든 요청 막는다.   
      우선 모든 경로에 대해 접근 허용하려했다.   
      그래서 SecurityConfig 파일을 생성해 아래와 같이 securityFilterChain() 메서드를 생성했고
      모든 경로에 대해 접근 허가했지만 동일 오류가 발생했다.
    - 검색해 찾은 답은 스프링 시큐리티가 적용된 애플리케이션에서
      HTTP POST 로 직접 엔드포인트를 호출할 때
      기본적으로는 CSRF 보호를 비활성화해주어야 호출이 가능하다는 것이었다.

      ~~~
      @EnableWebSecurity
      @RequiredArgsConstructor
      public class SecurityConfig {
          /** 인증관련설정: 자원별 접근권한 설정 */
          @Bean
          protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
              // 인증경로 접근허가 설정
              http
                  .authorizeHttpRequests((authorizeRequests) ->
                      authorizeRequests.anyRequest().permitAll());
              return http.build();
          }
      }
      ~~~

- 해결방법
    - 우선 인증계층(FilterChain)에서 원하는 Endpoint(호출경로)를 허용해 주어야 한다.   
      따라서 우선적으로 모든 경로에 대해 허가하기 위해 위와같이 SecurityConfig 설정파일을 생성해
      인증허가경로를 추가해주었다.   
      또한 호출하려했던 api는 POST 메서드를 사용하고 있었다.   
      CSRF 보호를 비활성화 하기 위해 아래와 같이 http의 csrf 비활성화 설정코드를 추가해주었다.
      ~~~
      http
          // HTTP POST로 직접 엔드포인트 호출 시 기본적으로는 CSRF 보호를 비활성화 필요.
          .csrf(AbstractHttpConfigurer::disable)
          .authorizeHttpRequests((authorizeRequests) ->
              authorizeRequests.anyRequest().permitAll());
      ~~~
    - 참고 블로그
        - CSRF 비활성화 필요   
          https://assu10.github.io/dev/2023/12/17/springsecurity-csrf/
        - 추가설정 ex) httpBasic diable 등   
          https://hipopatamus.tistory.com/72
        - https://velog.io/@woosim34/Spring-Spring-Security-%EC%84%A4%EC%A0%95-%EB%B0%8F-%EA%B5%AC%ED%98%84SessionSpring-boot3.0-%EC%9D%B4%EC%83%81


---
### < 중요 필터 >
1. SecurityContextPersistenceFilter
   - SecurityContextRepository에서 SecurityContext를 가져오거나 생성. (세션공간)
2. LogoutFilter
   - 로그아웃 요청 처리
3. UsernamePasswordAuthenticationFilter
   - ID, Password를 사용하는 실제 Form 기반 유저 인증 처리.
4. ConcurrentSessionFilter
   - 동시 세션 관련 필터
5. RememberMeAuthenticationFilter
   - 세션이 사라지거나 만료 되더라도 쿠키 또는 DB를 사용하여 저장된 토큰 기반으로 인증 처리.
6. AnonymousAuthenticationFilter
   - 사용자 정보가 인증되지 않았다면 익명 사용자 토큰 반환
7. SessionManagementFilter
   - 로그인 후 Session과 관련된 작업 처리.
8. ExceptionTranslationFilter
   - 필터 체인 내에서 발생되는 인증, 인가 예외 처리.
9. FilterSecurityInterceptor
   - 권한 부여, 권한 관련 결정을 AccessDscisionManager에게 위임해 권한부여 결정 및 접근 제어 처리.


---
### < 비밀번호 암호화 >
- 비밀번호 암호화 인터페이스 PasswordEncoder
  - 인코딩된 패스워드 정보를 DB에 저장하기 위해 사용.
     PasswordEncoder 인터페이스를 통해 인코딩을 수행하기 위해서는
     Configuration 파일에 어떤 구현체를 사용할 것인지 직접 정의해 빈 등록이 필요. 

- PasswordEncoder 인터페이스 정의된 내용.
    ~~~
    public interface PasswordEncoder {
          // 비밀번호 암호화 (회원가입 시 사용)
          String encode(CharSequence rawPassword);

          // 평문 비밀번호, 암호화된 비밀번호 일치여부 확인 (로그인 시 사용)
          boolean matches(CharSequence rawPassword, String encodedPassword);

          default boolean upgradeEncoding(String encodedPassword) {
              return false;
          }
      }
    ~~~
    
- 참고 블로그     
  https://hou27.tistory.com/entry/Spring-Boot-Spring-Security-%EC%A0%81%EC%9A%A9%ED%95%98%EA%B8%B0-%EC%95%94%ED%98%B8%ED%99%94


---
### < JWT : Json Web Token >
- 필수 참고 블로그   
  https://hjpkotlin2024.tistory.com/68

1. JWT
   - 많이 사용하는 보안방식 중 하나.
   - 보통은 송수신측에서 인증가능한 키들을 가지고 신뢰여부를 판단.
   - 보통은 인증서 등을 통해 진행.
   - JWT를 통해 로그인을 하고 아이디를 추출하도록 한다.

2. JWT 토큰 구성
   - 각 부분은 Base64 URL 인코딩된 문자열로 연결됨.
   - . 으로 구분 ex) <header>.<payload>.<signature>
   1. 헤더 (Header)
      - 토큰의 유형 및 서명 알고리즘 정보.
      - ex) HS256, RS256
   2. 페이로드 (Payload)
      - **실제 데이터나 클레임(Claims)**이 들어 있는 부분.
      - 일반적으로 키가 없어도 다른 사람들과 디코드 가능함.   
        So, 중요 데이터는 담지 않는다.
      - ex) 사용자ID, 권한정보, 만료시간 등
   3. 시그니쳐 (Signature, 서명)
      - 데이터의 무결성을 확인하는 서명.
      - 헤더와 페이로드를 비밀키로 서명해 무결성 보장.

3. 비밀키 인코딩
   - JWT 토큰 생성 시 사용할 디코딩된 비밀키 생성. (암호화x)
     - 토큰 생성시 HS512알고리즘 사용을 위해 512비트(64바이트 이상)의 문자열을 사용해 JWT 토큰의 비밀키 생성   
     - 평문보다는 base64로 인코딩한 값을 사용. (더 좋은 방법은 암호화해 인코딩한 값을 사용하는 것)
     - 터미널에서 인코딩된 문자열 생성가능.   
       - 평문키 파일을 생성해 base64로 인코딩된 문자열을 포함한 새파일 생성.
       ~~~
         C:\workspace\reservation\src\main\resources\token>certutil -encode jwt-secret-key.txt jwt_secret_key_encoding_b64   
         입력 길이 = 49   
         출력 길이 = 128   
         CertUtil: -encode 명령이 성공적으로 완료되었습니다.   
       ~~~
     - 도움받은 블로그   
       https://interconnection.tistory.com/120   

4. application.yml에 환경변수설정 추가
   - 설정한 변수 호출해 JWT 토큰 생성 시 사용가능.
   - spring.jwt.secret-key: '암호화된 비밀키 등록'

5. JWT 서명 검증을 위한 비밀키 생성 (JwtAuthenticationProvider.java)
   - JWT 생성 시 사용할 비밀키 생성
     - JWT 서명 검증을 위해 비밀키 생성 필요.
     - 만약 비밀키로 사용할 문자열이 인코딩된 상태라면 해당 문자열을 디코딩해 평문으로 변경해 준 뒤 사용해야 한다.
       그리고 해당 평문키를 HMAC-SHA 알고리즘을 이용해 키를 생성해 사용한다.
     ~~~
     private SecretKey getDecodedSecretKey() {
         byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); //1
         return Keys.hmacShaKeyFor(keyBytes); //2
     }     
     ~~~
     1. secret key 문자열을 UTF-8 바이트 배열로 변환.
     2. Keys.hmacShakeyFor() 메서드를 사용해서 HMAC-SHA 알고리즘에 맞는 키 객체 생성.
     - 비밀키를 생성하는데 encoding이 아닌 decoding하는 이유
       - 비밀키인 문자열이 base64로 인코딩된 상태였다.   
         이를 평문으로 변경하기 위해 base64로 디코딩하는 과정이 필요하기 때문이다.

6. JWT 토큰생성 (JwtAuthenticationProvider.java)
   - JWT
     - 사용자 인증 및 데이터 전송을 위한 경량의 안전한 토큰 형식으로 사용됨.
   
   - Jwts
     - JWT 생성 및 검증을 도와주는 Java 라이브러리.
     - JJWT(Java JWT Library) 라이브러리에서 제공하는 주요 클래스 중 하나.
     - JWT의 생성, 서명, 파싱, 검증 등 쉽게 처리 가능.

  - Claims
    - JWT의 Payload 부분에 포함되며, 토큰이 담고 있는 실제 정보.
    - 사용자의 아이디, 역할, 권한 등과 같은 중요한 데이터를 포함가능.
    - 변경 불가능한 JSON Map.
    - 한번 입력 후 getter()만 사용가능.
    
  - JWT 생성 및 서명 (Creating JWT, Signing JWT)
    - JWT 생성을 위해 **Jwts.builder()** 사용.
    - JWT의 헤더, 페이로드, 서명 등 설정가능.
    ~~~
     // JWT 생성 예시
     String jwt = Jwts.builder()
                     .subject(String.valueOf(id)) // 주제 (예: 사용자 ID)
                     .claim("userId", userPk)
                     .claim("name", name)
                     .claim("authorities", authorities)
                     .issuedAt(now) // 발급시간
                     .expiration(new Date(now.getTime() + TOKEN_VALID_TIME)) // 만료시간
                     .signWith(getDecodedSecretKey()) // 서명 (HMAC SHA-256 알고리즘 이용해 서명)
                     .compact(); // 최종적으로 JWT 문자열 생성
    ~~~
     1. signWith() 
        - 지정된 키와 저장된 알고리즘을 사용해 토큰에 서명함.
         
  - 참고 블로그      
    https://velog.io/@qkre/Spring-Security-%EB%B2%84%EC%A0%84-%EC%97%85-%EB%90%9C-%EC%8A%A4%ED%94%84%EB%A7%81-%EC%8B%9C%ED%81%90%EB%A6%AC%ED%8B%B0%EC%97%90%EC%84%9C-JWT-%EB%B0%9C%ED%96%89%ED%95%98%EA%B8%B0

7. JWT 토큰생성 테스트코드 작성
   - JWT를 파싱하여 그 안에 포함된 클레임(Claims) 을 추출하는 과정
    3. JWT 파싱 및 검증 (Parsing and validating JWT)
    - JWT 검증을 위해 **Jwts.parser()** 사용.
    - JWT 토큰을 파싱하고 서명 및 유효성 검증.
     ~~~
     // JWT 파싱 및 서명 검증 예시
     Claims claims = Jwts.parser()
                         .setSigningKey(secretKey) // 서명 검증용 비밀 키
                         .parseClaimsJws(jwt) // JWT 파싱
                         .getBody(); // Claims 추출          
     ~~~
    1. Jwts.parser()
        - Jwts는 JWT를 처리하는 라이브러리에서 제공하는 클래스로 JWT를 파싱할 때 사용.
        - parser() 메서드는 JWT를 파싱할 준비를 하는 객체를 반환.
    2. setSigningKey(decodedSecretKey)
        - JWT의 서명을 검증하기 위해 사용되는 비밀 키(decodedSecretKey)를 설정.
        - decodedSecretKey는 JWT가 유효한지 확인하는 데 필요.
    3. build()
        - build() 메서드는 Jwts.parser()로 생성한 JwtParser 객체를 최종적으로 생성.
    4. parseClaimsJws(token)
        - parseClaimsJws(token)는 주어진 token (JWT)을 파싱.
        - JWT의 서명을 검증하고, 유효한 JWT라면 JWT의 본문(클레임)을 추출.
    5. getBody()
        - 파싱된 JWT에서 실제 데이터(클레임)를 추출.
        - 클레임은 JWT에 담긴 정보들(예: 사용자 ID, 권한 등)을 나타냄.

8. 생성된 JWT 토큰 확인
- https://jwt.io/ 사이트를 통해서 내가 생성한 토큰에 정보가 잘 담겼는지 확인가능.
  ~~~
  // 생성된 토큰이 담고있는 정보
  {
     "sub": "1",
     "userId": "glyceria123",
     "name": "김근영",
     "authorities": [
                      {
                        "createId": null,
                        "createDt": "2025-02-01 05-24:40",
                        "updateId": null,
                        "updateDt": "2025-02-01 05-24:40",
                        "id": 1,
                        "authority": "SELLER"
                      } 
     ],
     "iat": 1738355086,
     "exp": 1738441486
  }
  ~~~

9. Spring Security에서 JWT 토큰을 사용한 인증.
  - 모든 인증을 Spring Security를 통하여 처리하고 싶다면 Filter를 쓰면 되고, 그렇지 않다면 Controller를 사용하여 구현하면 된다.
  - JWT 토큰을 이용해 접근제어권한을 확인한다.
  - 로그인을 하지 않은 사람이 허용되지 않은 엔드포인드(api호출경로)에 접근하는 문제를 막는다.

10. JWT 토큰과 세션
    - JWT 토큰을 사용해 사용자 인증을 진행하는 경우 Session stateless 설정하는 이유.
      - 참고 블로그   
        https://mini-frontend.tistory.com/6
        https://giron.tistory.com/17

---
### < 로그인 (최소한의) 인증 구현 >
1. 사용자 Entity 생성 (Member.java)
   - 사용자 인증 시 사용할 Entity.
   - UserDetails 인터페이스
     - Spring Security에서 사용자 인증과 관련된 정보를 제공하는 인터페이스.
     - 스프링 시큐리티 기능 사용을 위해 UserDetails 인터페이스 구현필요.
     - 사용자에 대한 정보를 Spring Security에서 사용할 수 있는 형대로 제공.
     - 사용자 이름, 비밀번호, 권한(roles) 등을 포함.
     - Spring Security에서 인증을 처리할 때, 
       사용자 정보를 제공하는 UserDetails 객체는 
       UserDetailsService의 loadUserByUsername() 메서드를 통해 로드됩니다. 
     - UserDetails 인터페이스를 구현한 객체는 Authentication 객체의 일부분으로 사용되어 인증 정보를 담고있음.
     - UserDetails 인터페이스는 인증된 사용자에 대한 정보를 반환하는 여러 메서드를 제공
   - UserDetails 인터페이스 주요메서드
     - getAuthorities()
       - 사용자에게 부여된 권한(roles)을 반환.
       - GrantedAuthority 객체의 컬렉션을 반환.
     - 
2. 사용자 인증 service (MemberService.java)
   - DB에 사용자 존재여부, 비밀번호 일치여부 확인 완료 시 로그인 토큰 반환.
   - UserDetailsService 인터페이스
     - 스프링 시큐리티 기능 사용을 위해 UserDetailsService 인터페이스 구현.
     - UserDetailsService는 UserDetails 인터페이스를 사용하여 인증된 사용자를 로드하고, AuthenticationManager는 이 정보를 바탕으로 인증을 수행
   - loadUserByUsername()
     - 스프링 시큐리티 기능을 사용하기위한 필수구현 메서드
   - 반환타입 UserDetails 
     - memberRepository.findByUserId() 의 반환타입은 Optional<MemberEntity>이다.
     - 반환값이 null이 아니면 MemberEntity 타입으로 값을 반환한다.
     - Member Entity는 UserDetails 인터페이스의 구현체이므로 
       loadUserByUsername() 메서드는 다형성에 의해 MemberEntity 타입으로도 반환가능.
     - username은 회원아이디(회원구분식별데이터)로 간주한다.
       - 참고 블로그    
         https://velog.io/@jyyoun1022/SpringSpring-Security%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EB%A1%9C%EA%B7%B8%EC%9D%B8%EC%B2%98%EB%A6%AC-5
   

3. 인증 Filter (JwtAuthenticationFilter.java)
   - 인증 필터를 통해 토큰의 유효성을 확인.
   - OncePerRequestFilter 필터를 상속해 생성.
     - 해당 필터를 상속해 정의해주면 모든 요청이 올 때 마다 (한 요청당 한 번) 필터 실행.
   - doFilterInternal() 메서드 필수구현.
   - 사용자가 signup, signin api를 호출했을 때 바로 해당 컨트롤러로 요청이 들어오는 것은 아니다.   
     컨트롤러로 요청이 들어오기 전에 제일 먼저 필터를 거치게 된다.   
     ( 컨트롤러 요청 -> 필터 -> 서블릿 -> 인터셉터 -> aop layer를 거친 후 컨트롤러 호출.
     응답을 내보낼 때는 위의 과정을 반대로 거쳐 내보낸다. )
     OncePerRequestFilter를 상속했기에 요청이 들어올 때 마다 매번 해당 필터를 실행하게 된다.
     따라서 컨트롤러 실행 전의 request, 컨트롤러 실행 후 응답 시 response를 가공 가능.   
     So, 컨트롤러에 요청이 들어올 때 마다 사용자토큰의 포함여부, 유효성 여부 등 확인가능.
  - UsernamePasswordAuthenticationToken 클래스
    - 사용자이름, 비밀번호를 기반으로 사용자 인증을 처리하는 역할을 수행.
    - 주로 사용자 인증 정보를 캡슐화하고 인증 프로세스 중에 사용됨.
    - 주요 목적   
      - 사용자로부터 입력받은 사용자이름, 비밀번호롤 AuthenticationManager에 전달해 인증을 수행.
    - 주요 필드
      - principal : 사용자이름
      - credentials : 비밀번호
      - authorities : 사용자권한
      - 참고 블로그   
        https://loginshin.tistory.com/98


---
### < 권한과 역할 >
1. 역할 (Role)
   - 일반적으로 사용자가 수행할 수 있는 "대분류" 작업으로 권한을 포함하는 상위개념.   
     ex) ADMIN, USER, MANAGER  // ADMIN은 USER 관련 권한을 포함할 수 있음.
   - Spring Security에서 ROLE_ 접두사를 붙여 사용하는 것이 일반적.    
     ex) ROLE_ADMIN, ROLE_USER

2. 권한 (Authority)
   - 사용자가 시스템 내에서 **"구체적으로 할 수 있는 작업"**으로 세부 권한을 나타냄.    
     ex) READ_PRIVILEGES, WRITE_PRIVILEGES, DELETE_PRIVILEGES

- 따라서 접근 권한을 허용하기 위해 hasAnyRole(), hasAnyAuthority() 처럼 두 가지의 메서드가 존재하는 것.


---
### < 인증관 권한에 대한 HTTP 상태코드 >
1. 401 Unauthorized(인증되지않음)
   - 요청을 처리하기 위해 사용자가 인증을 받아야함을 의미.
   - 클라이언트가 인증을 제공하지 않았거나 잘못된 인증 정보를 제공했을 때 반환.

2. 403 Forbidden(접근금지됨)
   - 사용자가 인증되었어도 해당 요청을 수행할 권한이 없음을 의미.
   - 서버는 요청을 받았지만 특정 리소스에 대한 엑세스를 거부.


---
### < 인증, 권한 관련 예외처리 >
스프링 시큐리티에서 인증 및 인가 과정 중 예외가 발생했을 때, 일반적으로 IDEA의 콘솔 창에 예외가 출력되지 않는다.
스프링 시큐리티가 자체적으로 예외를 처리하고, 이를 클라이언트에게 적절한 HTTP 응답 코드와 메시지로 반환하기 때문이다.

이에 불편함을 느껴 스프링 시큐리티에서 발생하는 예외를 CommonExceptionHandler.class에서 처리할 수 있도록 변경하고자 한다.

- 참고 블로그
  https://backend-jaamong.tistory.com/169

1. AuthenticationEntryPoint 인터페이스
   - 인증되지 않은 사용자가 인증이 필요한 endpoint(api url)로 접근 시 발생하는
   - 401 Unauthorized 예외처리할 수 있도록 도움. 
   - commence()
     - 인증되지 않은 요청 발생 시 호출되는 메서드.
     - 예외처리를 직접 수행하지 않고 HandlerExceptionResolver로 넘긴다.

2. AccessDeniedHandler 인터페이스
   - 권한이 없는 사용자가 권한이 필요한 endpoint로 접근 시 발생하는
   - 403 Forbidden 예외를 처리할 수 있도록 도움.
   - handle()
     - 권한이 없는 요청이 발생했을 때 호출되는 메서드.
     - 예외를 직접 처리하지 않고 HandlerExceptionResolver로 넘긴다.

3. HandlerExceptionResolver 인터페이스
   - Spring Security 영역이 아닌 Spring MVC 영역에 속해있는 컴포넌트.
   - 스프링 MVC에서 HandlerExceptionResolver는 
     DispatcherServlet의 HandlerExceptionResolver 체인(예외처리체인)에 등록되어있다.
   - 이 체인은 컨트롤러 영역에서 발생한 예외를 처리하는 역할을 한다.
   - 따라서 위의 **두 스프링 시큐리티 핸들러는 결국 스프링 MVC의 HandlerExceptionResolver를 호출해** 
     **컨트롤러에서 예외를 처리할 수 있도록 한다.**
   ~~~
   handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
   ~~~
   - 위와 같이 handler = null 로 전달하면 ExceptionHandler를 찾아 실행한다.   
     만약 처리할 수 있는 ExceptionResolver가 없으면 예외처리를 수행하지 않고 기존 발생한 예외를 서블릿 밖으로 던진다.   
     즉, null을 인자로 전달하면 @RestControllerAdvice, @ExceptionHandler를 사용하는 클래스에서 예외처리가 가능해진다.


---
### < JWT 인증 필터에서 Custom Exception 발생 시 AuthenticationException 발생하는 이유 >
- Spring Security에서 JWT 인증 필터에서 custom exception이 발생했을 때, AuthenticationException이 발생하는 이유    
  Spring Security의 기본 동작 방식 때문입니다. 
  인증 및 권한 부여 처리를 할 때, AuthenticationException을 사용하여 인증 실패를 처리합니다.

- 원인 후보
  1. Exception 예외 처리 mismatch
     - Spring Security에서 인증 관련 예외는 AuthenticationException을 상속한 예외들로 처리됩니다. 
     - 예를 들어, JWT 인증 필터에서 custom exception을 발생 tl AuthenticationException을 상속받지 않으면 Spring Security가 이를 인증 실패 예외로 인식하지 못하고 기본 예외를 던질 수 있습니다.
  2. 필터 설정에서 예외 처리 누락
     - custom exception을 발생시키고 싶다면, 이를 처리할 수 있는 예외 처리 로직을 필터에서 명시적으로 설정해야 할 수 있습니다. 
     - 예외가 발생했을 때 Spring Security가 기본적으로 처리하는 AuthenticationException이 아니라 custom exception을 처리하도록 해야 합니다.

예외 처리 핸들러 미설정: custom exception을 처리하기 위해서는 ExceptionTranslationFilter와 같은 예외 처리 필터가 예외를 제대로 처리할 수 있도록 설정되어야 합니다. 만약 custom exception이 발생하더라도 ExceptionTranslationFilter가 이를 AuthenticationException으로 변환하여 처리할 수 있도록 설정이 되어 있지 않으면, 기본적으로 AuthenticationException이 발생할 수 있습니다.

- My mistake
  > redis에 정보가 없을 때 발생시키는 custom exception은 AuthenticationException을 상속하지 않는다.
  > 따라서 Spring Security 인증에 대한 기본 Exception인 AuthenticationException을 발생시킨다.    
  > 어짜피 spring security 인증정보를 filterChain을 통해 넘겨주지 않으면
  > AuthenticationException 예외를 발생시켜 custom exception handler에서 정의한대로 예외처리를 수행할테니
  > custom exception 발생 로직을 제거하는 것으로 해결한다.     
  > 만약 custom exception을 발생시키고 싶으면 MemberException이 아니라 
  > AuthenticationException을 상속받는 새로운 custom exception을 생성하고 해당 예외를 던져 에외처리를 수행하도록 한다.     

