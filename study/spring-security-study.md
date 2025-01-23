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
- 설정에 따라 필요한 필터가있고 필요없는 필터가 있을 수 있음.
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


