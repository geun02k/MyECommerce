# MY E-COMMERCE PROJECT TEST CODE STUDY
2025 간단한 이커머스 서비스를 만들면서 접한 테스트코드에 대해 공부한 내용


---
## < 목차 >
1. 테스트코드
   - 테스트코드
   - @Test (단위테스트)
   - @WebMvcTest (웹 계층 슬라이스 테스트)
   - @SpringBootTest (애플리케이션 통합테스트)
2. 단위테스트와 통합테스트
   - 순수 Mockito 테스트 vs Spring 테스트
3. 단위 테스트
   - Service 테스트코드 작성 범위
   - Controller 테스트케이스 작성 - 도저히 안되서 Chat gpt를 통해 작성
4. 슬라이스 테스트
   - Controller 슬라이스 테스트
   - Controller 단위테스트에서 슬라이스 테스트로 변경
   - Controller 단위테스트를 슬라이스 테스트로 변경 시 스프링 시큐리티 문제
5. @WebMvcTest
   - @WebMvcTest와 MockMvc
   - @WebMvcTest에서 Security 설정
   - @WebMvcTest와 ObjectMapper DI
6. mocking
   - mocking
   - Mock 라이브러리
   - Mock 객체 생성
   - Stub
   - Spy
   - mock 객체 호출 검증
   - @ExtendWith(MockitoExtension.class)
   - repository의 메서드를 stub하지 않아도 테스트코드 오류가 발생하지 않는 이유
   - @MockBean vs @Mock
7. 테스트코드 검증
   - 테스트코드 검증
   - 테스트코드 검증과 ArgumentCaptor
   - 테스트코드 전달인자 검증
   - Captor 사용이 필요한 경우
   - Captor로 검증해야하는 값
8. 테스트코드에서 필드주입
9. Reflection - private 메서드 테스트코드 작성
10. JPA의 Dirty Checking을 사용 시 테스트코드 작성
11. 프로덕션, 테스트 config 파일
12. Spring Security와 테스트코드
    - permitAll() 경로 테스트에서 member.roles가 null이면 에러 발생 이유
    - 권한 검증 테스트가 누락되는 문제 해결
13. 유효성 검증 테스트
    - 애플리케이션의 계약/정책 테스트 전략
    - 웹 계층 테스트의 책임 범위 - API 계약과 Validation 책임 구분
    - 커스텀 Validation 테스트 - Enum 유효성 검증 테스트
    - PathVariable 검증 실패가 500이 되는 이유와 400으로 처리하는 방법
    - ValidationMessages.properties 검증 테스트 설계
14. 테스트코드 리팩토링
    - 상품등록성공 Controller 테스트코드 리팩토링 
15. Exception 테스트코드
    - Validation, 비즈니스 예외 메시지 테스트
16. 읽기 쉬운 테스트와 신뢰 가능한 단위 테스트 작성 원칙
17. verify()를 이용한 메서드 호출 검증
18. 단위 테스트 작성 가치
19. 모든 것을 검증하는 정상 시나리오의 문제점
20. 정상 시나리오 테스트에서 given 중복은 제거 대상이 아니라 설계 정보
21. 분기 정상 시나리오를 추가하지 않아도 되는 경우
22. 조회 단위 테스트와 통합 테스트
    - 장바구니 조회 통합테스트 가치
23. Redis 통합 테스트: Embedded Redis vs Testcontainers Redis
    - 테스트에서 Redis 결정
    - Embedded Redis - 테스트용
    - Testcontainers Redis - 진짜 Redis
24. 테스트에서 발생한 Auditing 에러
25. 프로파일 전략 (@Profile)
26. 동시성 통합 테스트
27. 테스트에서 @Transactional
28. 동시성 통합 테스트에서 @Transactional이 무시된 이유와 해결
29. 도메인 테스트
    - 도메인 테스트
    - Order–OrderItem 관계의 불변식을 설계로 고정해 테스트하지 않음
30. Payment.isTerminal() 테스트코드 작성 - 팩토리 생성 구조에서 특정 상태 테스트가 불가능한 문제 해결


--- 
## 1. 테스트코드
### < 테스트코드 >
1. MyECommerceApplicationTests.java   
   스프링 어플리케이션이 뜨는지를 테스트해주는 기본적으로 내장되어있는 test코드.
   - 포트 충돌 문제  
     서버가 실행중일 떄 테스트코드를 실행하면 같은 Redis 포트를 두 번 띄우려 하기 때문에
     테스트 서버와 실행 서버 포트가 충돌해 테스트 코드 실행이 실패한다.
   - 포트 충돌 문제 해결방법   
     테스트 코드를 전체 테스트하는 경우,
     AccountApplicationTests가 포함되어있고 서버가 실행중이라면
     스프링 어플리케이션 서버를 종료해야 AccountApplicationTests 에러가 발생하지 않는다.
     아니면, 테스트전용 설정파일을 사용해야 한다.

2. 테스트코드가 main함수가 아닌데 실행가능한 이유   
   Junit 프레임워크가 대신 실행해주기 때문.


### < @Test (단위테스트) >
특정 클래스의 로직만 빠르고 명확히 검증 가능.

1. JUnit에서 Spring 제공 기능 활성화 방법   
   JUnit은 Spring이 제공하는 기능들을 활성화하기 위해
   JUnit4에서는 SpringRunner, JUnit5에서는 @ExtendWith(SpringExtension.class)와 함께 사용해
   Spring TestContext Framework와 연결해야 한다.
   이는 JUnit 테스트에서 Spring 관련 기능(@Autowired, @MockBean, ApplicationContext 등)을 쓰고 싶다면
   SpringExtension을 통해 Spring 테스트 컨텍스트와 연결해야 한다는 의미이다.

2. Bean 생성/주입   
   빈 주입이 필요 없다면 @Autowired나 @MockBean 같은 어노테이션을 사용하지 않고,
   대신 @Mock이나 @InjectMocks를 사용하여 필요한 객체들을 수동으로 직접 생성/주입하여 테스트 가능.
   따라서 빈 로딩이 없음.

3. Mock 객체    
   특정 객체만 테스트하고자 할 때, @Mock으로 의존성 객체들을 수동으로 모킹하여 컨텍스트를 로드하지 않고도 테스트 가능.

4. Spring Context 로드하지 않음   
   단위 테스트 시 Spring 애플리케이션의 빈이나 컨텍스트를 로딩이 불필요해
   @WebMvcTest나 @SpringBootTest 어노테이션을 사용하지 않음.
   따라서 컨트롤러, 서비스, 레포지토리 등의 빈은 로드되지 않음.


### < @WebMvcTest (웹 계층 슬라이스 테스트) >
전체 애플리케이션을 띄우지 않고 웹 계층만을 검증.
단위 테스트보다 넓고, 통합테스트보다는 가벼운 테스트.

1. @WebMvcTest 사용목적
   @WebMvcTest는 웹 계층(Controller)을 중심으로    
   HTTP 요청, 응답 흐름을 테스트하기 위한 어노테이션.
   컨트롤러의 요청 매핑, 파라미터 바인딩, 검증, 응답 처리 등을 검증할 때 사용.
   주로 컨트롤러를 중심으로
   필터, 인터셉터 등 웹 요청 처리 과정과 관련된 동작을 검증 시 사용.

2. 로드되는 대상   
   웹 계층과 직접 관련된 빈만 로드됨.
   - Controller
   - ControllerAdvice (@ExceptionHandler)
   - Filter, Interceptor
   - WebMvcConfigurer
   - Jackson, Validation 관련 설정
   - Spring Security 설정

3. 로드되지 않는 대상   
   컨트롤러와 관련된 빈만 로드하므로
   Service, Repository, 비즈니스/인프라 계층은 mocking을 통해 가짜 객체로 대체.
   컨트롤러가 의존하는 Service, Repository는 @MockBean 어노테이션을 사용해 대체.

4. MockMvc   
   @WebMvcTest 사용 시 Spring TestContext Framework에 의해
   MockMvc가 자동으로 구성되어 주입됨.
   이를 통해 실제 서버를 띄우지 않고도 HTTP 요청/응답 테스트 가능.


### < @SpringBootTest (애플리케이션 통합테스트) >
1. @SpringBootTest 포함 라이브러리
   org.springframework.boot:spring-boot-starter-test 에 포함된 기능.

2. @SpringBootTest 사용 목적   
   애플리케이션 전체 구성요소가 실제 환경과 유사하게 통합되어 정상적으로 동작하는지 확인하기 위해 사용.

3. 통합테스트   
   @SpringBootTest 사용 시 SpringBootContextLoader에 의해
   Spring Context가 실제 애플리케이션과 유사하게 로드됨.
   애플리케이션의 모든 빈이 등록되므로 전체 빈을 활용하여 **통합테스트** 가능.
   통합 테스트를 통해 서비스, 리포지토리, 웹 계층 등 시스템의 다양한 레이어가 함께 동작하는 흐름 검증 가능.
   의존성 주입을 이용해 테스트 가능하므로,
   new 키워드를 통해 직접 객체 생성하지 않고 실제 빈을 주입받아 테스트 가능.
   이를 통해 레이어 간 연동, 설정(Configuration), 트랜잭션, 데이터 접근 등이 정상적으로 동작하는지 검증 가능.
   단, 통합테스트 실행 시 전체 애플리케이션 컨텍스트를 로드하기 때문에 테스트 실행 속도가 느릴 수 있음.


---
2. 단위테스트와 통합테스트
- 참고블로그        
  https://binco.tistory.com/entry/Unit%EB%8B%A8%EC%9C%84-%ED%85%8C%EC%8A%A4%ED%8A%B8%EC%99%80-Integration%ED%86%B5%ED%95%A9-%ED%85%8C%EC%8A%A4%ED%8A%B8%EC%A0%95%EB%A6%AC%EB%B0%8F%EC%98%88%EC%8B%9C


### < 순수 Mockito 테스트 vs Spring 테스트 >
> @WebMvcTest를 사용하는 경우, @ExtendWith(MockitoExtension.class)를 쓰지 않는다.   
Mockito가 아니라 Spring TestContext가 테스트를 실행/관리하기 때문이다.

1. @ExtendWith(MockitoExtension.class) (순수 Mockito 테스트)
   JUnit5에 해당 테스트는 Mockito가 테스트 생명주기를 관리함을 알림.
   @Mock, @InjectMocks 어노테이션 등 사용이 가능하고 Mockito 기반 객체 생성/주입이 가능해진다.
   테스트는 Spring 컨테이너(ApplicationContext) 없이 실행된다.
   즉, 해당 테스트는 ***순수 단위 테스트용***으로 사용된다.
   Spring없이 실행되므로 매우 빠르지만, HTTP/Validation/Security 테스트 불가.
   ~~~
   @ExtendWith(MockitoExtension.class)
   class ProductionControllerUnitTest {
       @Mock
       ProductionService productionService;

       @InjectMocks
       ProductionController controller;
   }
   ~~~
2. @WebMvcTest (Spring 테스트)   
   Spring이 테스트를 관리하는 테스트.
   Spring에 Spring TestContext Framework를 띄우고, Web MVC 계층만 로딩해 테스트를 수행하라고 알림.
   즉, 테스트의 실행 주체는 Spring이고, 테스트 컨텍스트로 Spring ApplicationContext를 사용하며 Spring과 Mockito를 통합해 Mock을 관리.
   ***Spring 슬라이스 테스트 시 사용.***   
   @MockitoBean을 이용해 Mock 생성 및 주입 가능.
   Spring Context 안에 Mock Bean을 등록해 Controller에 자동 주입함.
   Spring MVC 동작을 하므로 Validation/Binding/Security 테스트가 가능.
   ~~~
   @WebMvcTest(ProductionController.class)
   class ProductionControllerTest {
       @Autowired
       MockMvc mockMvc;

       @MockitoBean
       ProductionService productionService;
   }
   ~~~


---
## 3. 단위 테스트
### < Service 테스트코드 작성 범위 >
Service 코드에서 메서드로 추출된 내용에 대한 단위 테스트 코드 작성은
테스트의 목적과 메서드의 책임에 따라 달라질 수 있음.

1. Service 메서드의 단위 테스트 작성 원칙   
   단위 테스트는 기본적으로 한 가지 책임만을 테스트하는 것이 권장.
   각 메서드가 독립적으로 테스트 가능하도록 잘 분리되어 있다면,
   각 메서드에 대한 단위 테스트를 별도로 작성해야 할 필요성이 생김.
   따라서, ***서비스 코드에서 각 기능이 메서드로 잘 추출되어 있다면 각 메서드의 단위 테스트를 작성하는 것이 원칙적***입니다.
   1. 독립성   
      메서드가 하나의 작은 책임만 담당한다면, 해당 메서드를 독립적으로 테스트 가능.
   2. 단일 책임 원칙(SRP)   
      각 메서드가 특정 작업을 담당하고 있다면, 그 작업에 대해 독립적으로 테스트 가능.
   3. 유지 보수   
      메서드를 추출한 뒤 각 메서드에 대해 단위 테스트를 작성하면,
      이후에 해당 메서드의 변경이 있을 때 테스트가 실패하는지 확인할 수 있어 리팩토링이나 수정에 유리.   
      <br>

2. Service 코드 단위 테스트 예시   
   예를 들어, modifyProduction 메서드에서 호출되는
   checkIfOptionCodeExists, saveProductionOption 등의 메서드가 있다면,
   이들도 각각 단위 테스트를 작성해야 할 경우가 많음.
   하지만, 모든 메서드에 대해 독립적인 테스트를 작성해야 하는지는 상황 고려가 필요.
   1) 메서드가 외부 의존성이 있는 경우   
      checkIfOptionCodeExists() 메서드는 데이터베이스를 조회하거나 외부 서비스 호출 가능.
      이 경우, Mocking을 사용하여 외부 의존성을 대체하고 해당 메서드의 기능을 테스트.
      예를 들어, checkIfOptionCodeExists()가 데이터베이스를 호출하는 메서드라면,
      실제 데이터베이스를 테스트 환경에서 사용하기보다는 Mockito나 MockMvc를 사용해
      Mocking하여 테스트를 진행.
   2) 메서드가 비즈니스 로직을 수행하는 경우   
      saveProductionOption()이 실제 DB에 데이터를 저장하는 로직이라면,
      이 부분도 Mocking을 사용하여 데이터베이스와의 상호작용 없이 해당 메서드가 올바르게 동작하는지 테스트 가능.   
      <br>

3. 각 메서드의 단위 테스트 여부 판단 기준
   1) 핵심 비즈니스 로직을 처리하는 메서드  
      핵심 비즈니스 로직을 처리하는 메서드의 경우, 단위 테스트 필요.  
      예를 들어, modifyProduction 메서드는 상품 수정 로직의 핵심이므로 테스트 대상이 되어야 함.
   2) 단순한 변환 작업을 하는 메서드   
      예를 들어, modifyProductionOptionMapper::toEntity와 같은 메서드는
      단순히 DTO를 엔티티로 변환하는 작업이다.
      이런 메서드는 통합 테스트로 확인하고, 단위 테스트는 필요없을 수 있음.
      변환 로직이 복잡하다면 테스트가 필요할 수 있음.
   3) 외부 의존성을 호출하는 메서드   
      productionRepository.save() 같은 JPA 리포지토리 메서드는 실제 DB를 사용하는 부분이라,
      Mocking이나 통합 테스트로 처리하는 것이 일반적.   
      <br>

4. 단위 테스트를 위한 모킹(Mock)   
   Mockito와 같은 Mocking 프레임워크를 활용하면, 외부 의존성을 차단하고 단위 테스트를 작성 가능.


### < Controller 테스트케이스 작성 - 도저히 안되서 Chat gpt를 통해 작성 >
1. MockMvc를 이용한 테스트 목적
   - 컨트롤러의 엔드포인트를 호출하여 HTTP 클라이언트의 요청을 모방하고 적절한 응답을 확인하기 위해 테스트 수행.
2. @WebMvcTest(MemberController.class)
   - MockMvc 객체를 생성해 컨트롤러와 상호작용함.
   - 명시한 해당 컨트롤러만 격리시켜 단위테스트 수행가능.
   - value : 테스트 할 controller 클래스 명시.
3. @MockBean
   - 테스트 환경에서 애플리케이션 컨텍스트(ApplicationContext)의 빈을 모킹하도록 설정.
   - Spring Boot 3.4.0부터 deprecated된 @MockBean과 거의 동일.
   - 빈으로 등록해주는 mock
   - 자동으로 빈으로 등록되어 컨트롤러에 주입됨.

4. 일반적으로 **@WebMvcTest**와 **MockMvc**를 사용하여 Controller를 테스트할 수 있습니다.    
   이 테스트는 Controller의 HTTP 요청과 응답을 실제로 처리하는 방식으로 동작합니다.   
   다음은 signUpSeller() 메서드의 성공적인 테스트 케이스입니다.   
   이 예제에서는 @MockBean을 사용하여 의존성 주입되는 MemberService를 mock하고, MockMvc를 사용하여 실제 HTTP 요청을 시뮬레이션합니다.
   - 코드 설명
      1) MockMvc 설정   
         MockMvcBuilders.standaloneSetup(memberController).build()로 MockMvc를 설정하여 Controller를 테스트할 수 있게 만듭니다.
      2) Mock 객체 설정   
         @Mock 어노테이션을 통해 MemberService를 mock하고, @InjectMocks를 사용하여 MemberController에 mock된 MemberService를 주입합니다.
      3) MockMvc로 HTTP 요청 시뮬레이션
         - mockMvc.perform(post("/member/signup/seller")...)로 HTTP POST 요청을 시뮬레이션합니다.
         - andExpect(status().isOk())는 응답 상태 코드가 200 OK임을 검증.
         - andExpect(jsonPath("$.name").value("John Doe"))는 JSON 응답 본문에서 name 값이 "John Doe"인지 확인합니다.
   ~~~
    package com.myecommerce.MyECommerce.controller;
    
    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.myecommerce.MyECommerce.dto.MemberDto;
    import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
    import com.myecommerce.MyECommerce.service.member.MemberService;
    import com.myecommerce.MyECommerce.type.MemberAuthorityType;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.Mockito;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.test.web.servlet.MockMvc;
    import org.springframework.test.web.servlet.setup.MockMvcBuilders;
    import org.springframework.web.context.WebApplicationContext;
    
    import java.util.Collections;
    
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.when;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
    
    class MemberControllerTest {
    
        private MockMvc mockMvc;
    
        @Mock
        private MemberService memberService;
    
        @InjectMocks
        private MemberController memberController;
    
        private ObjectMapper objectMapper;
    
        @BeforeEach
        void setUp() {
            objectMapper = new ObjectMapper();
            mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
        }
    
        @Test
        void signUpSeller_Success() throws Exception {
            // given
            MemberDto memberDto = new MemberDto();
            memberDto.setName("John Doe");
            memberDto.setTel1("010");
            memberDto.setTel2("1234");
            memberDto.setTel3("5678");
            memberDto.setPassword("password123");
    
            // 권한 설정
            MemberAuthority authority = new MemberAuthority();
            authority.setAuthority(MemberAuthorityType.SELLER);
            memberDto.setAuthorities(Collections.singletonList(authority));
    
            // when
            when(memberService.saveMember(any(MemberDto.class))).thenReturn(memberDto);
    
            // then
            mockMvc.perform(post("/member/signup/seller")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(memberDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("John Doe"))
                    .andExpect(jsonPath("$.tel1").value("010"))
                    .andExpect(jsonPath("$.tel2").value("1234"))
                    .andExpect(jsonPath("$.tel3").value("5678"))
                    .andExpect(jsonPath("$.password").value("password123"))
                    .andExpect(jsonPath("$.authorities[0].authority").value("SELLER"));
        }
    }
   ~~~

5. 스프링부트 3.4.0 버전 이상인 경우 @Mock 어노테이션 사용 불가.   
   다른방식으로 테스트코드 작성 필요.
   - @Mock 어노테이션 대신 Mockito.mock()을 사용하여 mock 객체를 생성해야 합니다. (Mockito.mock 사용)
   - @Mock 어노테이션을 사용하지 않고, Mockito.mock()을 사용하여 MemberService의 mock 객체를 생성합니다.
     ~~~
      // mock 객체  생성
      memberService = Mockito.mock(MemberService.class);
     ~~~
   - @InjectMocks
      - @InjectMocks는 여전히 MemberController에 mock 객체를 주입하는 데 사용됩니다.
        ~~~
           // mock 객체를 직접 생성하여 주입
           new MemberController(memberService);
        ~~~
   - Mockito로 Mock 객체 설정
     ~~~
      // memberService.saveMember 메서드 호출 시 mock된 MemberDto를 반환하도록 설정
      when(memberService.saveMember(any(MemberDto.class))).thenReturn(memberDto);
     ~~~

   ~~~
    package com.myecommerce.MyECommerce.controller;
    
    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.myecommerce.MyECommerce.dto.MemberDto;
    import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
    import com.myecommerce.MyECommerce.service.member.MemberService;
    import com.myecommerce.MyECommerce.type.MemberAuthorityType;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.mockito.InjectMocks;
    import org.mockito.Mockito;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.test.web.servlet.MockMvc;
    import org.springframework.test.web.servlet.setup.MockMvcBuilders;
    
    import java.util.Collections;
    
    import static org.mockito.ArgumentMatchers.any;
    import static org.mockito.Mockito.when;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
    
    class MemberControllerTest {
    
        private MockMvc mockMvc;
    
        private MemberService memberService;  // @Mock 대신 필드로 사용
    
        @InjectMocks
        private MemberController memberController;
    
        private ObjectMapper objectMapper;
    
        @BeforeEach
        void setUp() {
            // Mockito.mock을 사용하여 mock 객체 생성
            memberService = Mockito.mock(MemberService.class);
            
            // InjectMocks로 Controller에 mock 객체 주입
            memberController = new MemberController(memberService);
            
            objectMapper = new ObjectMapper();
            mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
        }
    
        @Test
        void signUpSeller_Success() throws Exception {
            // given
            MemberDto memberDto = new MemberDto();
            memberDto.setName("John Doe");
            memberDto.setTel1("010");
            memberDto.setTel2("1234");
            memberDto.setTel3("5678");
            memberDto.setPassword("password123");
    
            // 권한 설정
            MemberAuthority authority = new MemberAuthority();
            authority.setAuthority(MemberAuthorityType.SELLER);
            memberDto.setAuthorities(Collections.singletonList(authority));
    
            // when
            when(memberService.saveMember(any(MemberDto.class))).thenReturn(memberDto);
    
            // then
            mockMvc.perform(post("/member/signup/seller")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(memberDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("John Doe"))
                    .andExpect(jsonPath("$.tel1").value("010"))
                    .andExpect(jsonPath("$.tel2").value("1234"))
                    .andExpect(jsonPath("$.tel3").value("5678"))
                    .andExpect(jsonPath("$.password").value("password123"))
                    .andExpect(jsonPath("$.authorities[0].authority").value("SELLER"));
        }
    }
   ~~~

6. 테스트코드를 작성시 @MockMvc 어노테이션을 적용하지 않는 이유
   - @MockMvc는 테스트 클래스에 적용할 수 없고, 객체를 수동으로 생성해야하기 때문입니다.   
     @MockMvc 어노테이션은 실제로 MockMvc 객체를 설정하는 것이 아니라, MockMvc 객체를 자동으로 초기화하기 위한 테스트 클래스의 기본적인 설정을 도와줍니다.    
     그러나, 이 어노테이션은 Spring 3.4.0 이상에서는 사용되지 않으며, @MockMvc를 클래스에 붙여도 동작하지 않습니다.
   - @MockMvc 어노테이션 역할
      - 실제로 MockMvc 객체를 생성해주는 역할을 하지만, **@MockMvc**를 클래스 레벨에 직접 적용하는 것은 지원되지 않습니다.    
        대신, MockMvc 객체는 테스트 클래스에서 직접 **MockMvcBuilders**를 사용하여 설정하고 초기화합니다.
   - MockMvc를 초기화
      - **MockMvcBuilders**를 사용해 MockMvc 객체를 수동으로 설정해야 합니다.
      - 예를 들어, MockMvcBuilders.standaloneSetup(memberController).build();로 MockMvc 객체를 설정하여 Controller를 테스트할 수 있습니다.
   - @WebMvcTest 사용 시 @MockMvc 자동 설정
      - 만약 @WebMvcTest를 사용하면, @MockMvc가 자동으로 주입되지만, @WebMvcTest는 기본적으로 Spring Boot에서 웹 레이어를 테스트할 때 사용됩니다.
      - @MockMvc는 @WebMvcTest 내에서만 자동으로 동작하는 것입니다.
   - 권장
      - @MockMvc는 클래스를 통해 자동 주입되지 않기 때문에 MockMvcBuilders를 사용하여 MockMvc를 수동으로 설정해야 합니다.   
        **@MockMvc**는 @WebMvcTest에서 사용되지만, 일반적으로 @WebMvcTest 없이 사용할 때는 MockMvc를 수동으로 설정하는 방식이 필요합니다.   
        따라서 MockMvc 객체를 수동으로 초기화하는 방식이 더 유연하고, 명시적으로 테스트가 필요한 컨트롤러만 설정할 수 있습니다.
      - MockMvcBuilders.standaloneSetup(memberController).build() 방식이 더 권장됩니다.


---
## 4. 슬라이스 테스트
### < Controller 슬라이스 테스트 >
1. Controller의 역할
   Controller의 역할은 Http 요청 수신, 바인딩, validation, service 호출, response 반환이다.   
   <br>

2. Controller 슬라이스 테스트   
   Controller 슬라이스 테스트는 Controller 역할을 기반하기 때문에
   ***HTTP 요청이 들어왔을 때 바인딩 / 검증 / 인증·인가 / 예외 변환 / 올바른 HTTP 응답으로 변환을 테스트***한다.
   그리고 중요한 점은, ***테스트에서는 중복 제거보다 가독성이 우선***이라는 것이다.
   1) Controller 슬라이스 테스트와 Security
      Security, JWT, Service 구현 등에 관심이 없고 요청에 따른 컨트롤러 응답만 검증한다.
      이 때, JWT 토큰이 없고 Security Filter가 먼저 실행되어 Controller까지 가지 못해 테스트가 깨진다.
      따라서 해당 테스트에서 모든 요청을 통과 시키기 위해 Security 자체를 끄는 방법이 필요하다.  
   <br>

3. Controller 슬라이스 테스트 목적
   1) 요청 데이터 바인딩
   2) 인증/인가
   3) Validation 검증   
      Validation 검증의 경우는, 모든 제약조건 조합, 모든 필드별 케이스를 작성하는 것이 아니라 검증이 동작한다는 사실만 확인하는 것이다.
      DTO 세부 검증을 수행하고 싶다면 DTO 단위 테스트를 별도 수행해야 한다.
   4) 예외 변환
   5) 올바른 HTTP 응답 (Status 등)   
      응답 검증은 모킹된 서비스 결과를 통해 간접 검증하는 구조가 정상이다.   
   6) Controller 로직   
   <br>
   
4. Controller 슬라이스 테스트 검증 제외 
   1) JWT 파싱, 토큰 서명, 인증 서버 연동, 실제 권한 판별 로직 등은 검증하지 않는다.
   2) ArgumentCaptor, refEq, equals를 이용한 Service 전달인자의 검증 또한 웹 계층 테스트의 책임 범위를 넘어선다.
   3) 비즈니스 로직 검증, 서비스 내부 동작 검증, DTO->Entity 변환 검증은 수행하지 않는다.
   <br>


### < Controller 단위테스트에서 슬라이스 테스트로 변경 >
- 참조 ProductionController.successRegisterProduction()

1. 기존 Controller 테스트의 한계
   기존 테스트는 Controller가 Service를 호출하는지만 확인하는 Mockito 기반의 순수 단위 테스트에 가깝다.   
   이는 Controller 메서드를 직접 호출하는 수준에 머물러 있으며,
   JSON 바인딩로 MockMVC의 최소 기능만 사용했다.
   1) ***Controller는 비즈니스 로직이 없기 때문에 단위 테스트의 효용이 낮음.***
   2) JSON 필드명 변경으로 인한 요청/응답 불일치,
      Validation 어노테이션 누락,
      @RequestBody 누락,
      Security 설정 오류,
      ArgumentResolver 설정 오류 등의 문제 검증 불가.
      Controller의 역할은 Http 요청 수신, 바인딩, validation, service 호출, response 반환이다.
      따라서 controller 핵심 책임 검증을 전혀 하지 못함.

2. 수정한 Controller 테스트의 목적    
   수정된 테스트는 Spring MVC + Security를 포함한 웹 계층 테스트이다.   
   HTTP 요청/응답, JSON 바인딩, API 스펙 보호를 주요 목적으로 하며,
   실제 HTTP 요청 처리 흐름을 거의 그대로 통과하고 있다.   
   테스트는 Spring MVC Context, DispatcherServlet, Jackson 기반 JSON 바인딩,
   Validation 처리, Spring Security 인증 흐름의 구성 요소들을 포함하고 있다.   
   그러면서 비즈니스 로직을 담당하는 Service는 Mock 처리하여 웹 계층 테스트로서의 범위를 명확히 유지한다.

3. 인증 처리 관점에서의 차이
   기존 테스트코드는 인증 개념 자체가 존재하지 않고, Member 객체도 단순한 POJO에 불과하다.
   이로 인해 실제 운영 환경과 괴리가 크다.
   반면, 수정된 테스트코드는 with(user(member))를 통해
   Spring Security Context에 사용자를 주입하고 member 객체에 권한 정보를 포함시켜
   실제 운영 환경과 거의 동일한 인증 흐름을 재현한다.
   이로 인해 해당 API는 인증된 SELLER만 호출 가능하다는 요구사항을 테스트 수준에서 보호할 수 있게 된다.

- 테스트 변경 목적
  Controller는 비즈니스 로직이 없으므로 순수 단위 테스트의 효용은 낮다.
  따라서 비즈니스 검증이 아닌 HTTP 요청/응답, JSON 바인딩 및 API 스펙(계약) 보호를 목적으로 변경한다.


### < Controller 단위테스트를 슬라이스 테스트로 변경 시 스프링 시큐리티 문제 >
0. controller 단위테스트 -> 슬라이스 테스트로 변경
   > 테스트 대상에 따른 Security 처리 활성 여부가 달라진다.  
   Controller 슬라이스 테스트에서 Security 처리는 비활성화 하거나 Mocking 처리한다.  
   Security 설정 검증 테스트에서 Security는 별도로 처리하고,
   인증/인가 통합 테스트는 @SpringBootTest로 테스트한다.
   >
   > Controller 슬라이스 테스트에서는 Spring Security의 토큰 인증 자체를 검증 대상에서 제외하고,
   필요 시 Mock 인증을 사용해 권한 분기만 테스트한다.
   1. Security 필터 비활성화 (일반 요청 검증)  
      가장 흔한 해결방법이다.
      Controller 로직에만 집중 가능하고 Validation 테스트도 수행되므로 가장 보편적인 선택이다.
       ~~~
       @WebMvcTest(ProductionController.class)
       @AutoConfigureMockMvc(addFilters = false)
       class ProductionControllerTest {
           // ...
       }
       ~~~
   2. Mock 인증 사용자 주입 (권한 분기 검증)  
      권한 분기 테스트가 가능하며, 인증 로직 자체는 제외된다.
      따라서 토큰 검증을 수행하지 않고, 필터 체인만 통과 가능하다.
       ~~~
       @WithMockUser(authorities = "SELLER")
       @Test
       void 상품등록_성공() throws Exception {
           // ...
       }
       ~~~
   3. 토큰까지 포함해 테스트   
      실제 JWT 생성, SecurityConfig를 로딩하고 @SpringBootTest를 사용한다.
      느리고 유지보수가 어려우며, Controller 테스트 목적과 어긋난다.
      이는 통합 테스트 영역이므로 따라서 잘 사용하지 않는 방법이다.  
      <br>
1. 문제발생
   ~~~
    Failed to load ApplicationContext for [WebMergedContextConfiguration@456aa471 testClass = com.myecommerce.MyECommerce.controller.ProductionControllerTest, locations = [], classes = [com.myecommerce.MyECommerce.MyECommerceApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [], propertySourceProperties = ["org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.autoconfigure.OnFailureConditionReportContextCustomizerFactory$OnFailureConditionReportContextCustomizer@4ebea12c, org.springframework.boot.test.autoconfigure.OverrideAutoConfigurationContextCustomizerFactory$DisableAutoConfigurationContextCustomizer@235f4c10, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.filter.TypeExcludeFiltersContextCustomizer@7f0b4da3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@13b3bb87, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@303e3593, [ImportsContextCustomizer@51f01535 key = [org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration, org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration, org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration, org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration, org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration, org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration, org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration, org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration, org.springframework.boot.test.autoconfigure.web.reactive.WebTestClientAutoConfiguration, org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration, org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration, org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration, org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration, org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcSecurityConfiguration, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcWebClientAutoConfiguration, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcWebDriverAutoConfiguration, org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration, org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration, org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration, org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration, org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration, org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration, org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration, org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration, org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration]], org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@754777cd, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@4cc76301, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.reactor.netty.DisableReactorResourceFactoryGlobalResourcesContextCustomizerFactory$DisableReactorResourceFactoryGlobalResourcesContextCustomizerCustomizer@22c86919, org.springframework.test.context.bean.override.BeanOverrideContextCustomizer@764a2c56, org.springframework.test.context.support.DynamicPropertiesContextCustomizer@0, org.springframework.boot.test.context.SpringBootTestAnnotation@983247bf], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
    java.lang.IllegalStateException: Failed to load ApplicationContext for [WebMergedContextConfiguration@456aa471 testClass = com.myecommerce.MyECommerce.controller.ProductionControllerTest, locations = [], classes = [com.myecommerce.MyECommerce.MyECommerceApplication], contextInitializerClasses = [], activeProfiles = [], propertySourceDescriptors = [], propertySourceProperties = ["org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTestContextBootstrapper=true"], contextCustomizers = [org.springframework.boot.test.autoconfigure.OnFailureConditionReportContextCustomizerFactory$OnFailureConditionReportContextCustomizer@4ebea12c, org.springframework.boot.test.autoconfigure.OverrideAutoConfigurationContextCustomizerFactory$DisableAutoConfigurationContextCustomizer@235f4c10, org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f, org.springframework.boot.test.autoconfigure.filter.TypeExcludeFiltersContextCustomizer@7f0b4da3, org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@13b3bb87, org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@303e3593, [ImportsContextCustomizer@51f01535 key = [org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration, org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration, org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration, org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration, org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration, org.springframework.boot.autoconfigure.hateoas.HypermediaAutoConfiguration, org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration, org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration, org.springframework.boot.test.autoconfigure.web.reactive.WebTestClientAutoConfiguration, org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcAutoConfiguration, org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration, org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration, org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration, org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcSecurityConfiguration, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcWebClientAutoConfiguration, org.springframework.boot.test.autoconfigure.web.servlet.MockMvcWebDriverAutoConfiguration, org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration, org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration, org.springframework.boot.autoconfigure.jsonb.JsonbAutoConfiguration, org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration, org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration, org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration, org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration, org.springframework.boot.autoconfigure.groovy.template.GroovyTemplateAutoConfiguration, org.springframework.boot.autoconfigure.mustache.MustacheAutoConfiguration]], org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@754777cd, org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@4cc76301, org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0, org.springframework.boot.test.web.reactor.netty.DisableReactorResourceFactoryGlobalResourcesContextCustomizerFactory$DisableReactorResourceFactoryGlobalResourcesContextCustomizerCustomizer@22c86919, org.springframework.test.context.bean.override.BeanOverrideContextCustomizer@764a2c56, org.springframework.test.context.support.DynamicPropertiesContextCustomizer@0, org.springframework.boot.test.context.SpringBootTestAnnotation@983247bf], resourceBasePath = "src/main/webapp", contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]   
    // ... 생략   
    Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'jwtAuthenticationFilter' defined in file [C:\workspace\MyECommerce\build\classes\java\main\com\myecommerce\MyECommerce\security\filter\JwtAuthenticationFilter.class]: Unsatisfied dependency expressed through constructor parameter 0: No qualifying bean of type 'com.myecommerce.MyECommerce.config.JwtAuthenticationProvider' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}   
    // ... 생략   
    Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'com.myecommerce.MyECommerce.config.JwtAuthenticationProvider' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}
    at org.springframework.beans.factory.support.DefaultListableBeanFactory.raiseNoMatchingBeanFound(DefaultListableBeanFactory.java:2144)   
    // ... 생략   
    Error creating bean with name 'jwtAuthenticationFilter' defined in file [C:\workspace\MyECommerce\build\classes\java\main\com\myecommerce\MyECommerce\security\filter\JwtAuthenticationFilter.class]: Unsatisfied dependency expressed through constructor parameter 0: No qualifying bean of type 'com.myecommerce.MyECommerce.config.JwtAuthenticationProvider' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}
    org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'jwtAuthenticationFilter' defined in file [C:\workspace\MyECommerce\build\classes\java\main\com\myecommerce\MyECommerce\security\filter\JwtAuthenticationFilter.class]: Unsatisfied dependency expressed through constructor parameter 0: No qualifying bean of type 'com.myecommerce.MyECommerce.config.JwtAuthenticationProvider' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}   
    // ... 생략   
    Caused by: org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type 'com.myecommerce.MyECommerce.config.JwtAuthenticationProvider' available: expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: {}   
    at app//org.springframework.beans.factory.support.DefaultListableBeanFactory.raiseNoMatchingBeanFound(DefaultListableBeanFactory.java:2144)
    at app//org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1594)
    at app//org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1519)
    at app//org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:913)
    at app//org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:791)
    ... 45 more
   ~~~
   <br>

2. 문제발생원인
   > 테스트 범위를 정확히 설정했는데 왜 프레임워크가 그 경계를 침범하는가?

   controller 단위테스트를 @WebMvcTest로 변경했더니
   스프링 시큐리티를 적용하고 있어서인지 토큰 인증문제로 인한 에러가 발생했다.  
   대부분의 Controller 슬라이스 테스트에서는 토큰 인증 자체를 “검증 제외 대상”이다.  
   @WebMvcTest를 사용하면 Controller만 로딩되고 Spring Security FilterChain은 기본적으로 활성화된다.  
   MockMvc → Security Filter → Controller 순서로 실행(?)되므로,
   토큰이 없거나 잘못되면 Controller까지 요청이 도달하지 않기에 테스트가 실패한다.  
   Controller 슬라이스 테스트에서 JWT 파싱, 토큰 서명, 인증 서버 연동, 실제 권한 판별 로직 등은 검증하지 않는다.  
   Security 자체는 다른 테스트의 책임이기 때문이다.   
   <br>
   @AutoConfigureMockMvc(addFilters = false)를 적용하면 filter를 적용하지 않는 것으로 생각했다.  
   여기서 'addFilters = false'는 FilterChain을 실행하지 않는다는 의미이지,
   Filter Bean을 생성하지 않는다는 뜻이 아니다.  
   따라서 ***Filter Bean 생성 시점의 의존성 주입 오류는 그대로 발생한다.***  
   에러의 핵심 부분은 아래와 같다.
   ~~~
   Error creating bean with name 'jwtAuthenticationFilter'
   Unsatisfied dependency expressed through constructor parameter 0
   No qualifying bean of type 'JwtAuthenticationProvider'
   ~~~
   ApplicationContext 생성 시
   Filter Bean 우선 생성 후, MockMvc 요청 시 FilterChain을 적용한다.
   Spring이 JwtAuthenticationFilter Bean을 생성하려고 시도한다.
   이 때, 생성자에서 JwtAuthenticationProvider를 주입하려고 한다.
   하지만 @WebMvcTest 환경에는 해당 Bean이 없다.
   따라서 ApplicationContext 로딩(생성) 단계에서 실패하게 된다.
   이는 Controller까지 요청이 가기 전에 오류가 발생하는 것을 의미한다.   
   <br>
   @WebMvcTest는 기본적으로 Controller 관련 Bean만 로딩한다.
   따라서 SecurityConfig는 로딩될 수 있다.
   하지만 Security 관련 Bean 의존성이 전부 제공되지는 않는다.  
   <br>

3. 문제해결방법   
   @WebMvcTest 환경에서 Security 관련 Bean이 부분적으로만 로딩되기 때문에,
   Bean 생성 실패를 막으려면 테스트 전용 Security 설정(TestConfig)을 별도로 두는 것이 가장 안정적이다.  
   'addFilters=false' 설정은 Filter 실행만 막을 뿐,
   Filter Bean 생성은 막지 않기 때문에 Security 설정이나 Filter 의존성은 별도로 처리해야하기 때문이다.  
   <br>

   1. Security 설정 자체를 테스트에서 제외   
      가장 깔끔하고 실무에서 최다 사용된다.
      TestSecurityConfig를 분리하는 방법으로 JWT Filter Bean 생성 자체가 되지 않도록 한다.
      Security와 Controller 책임을 분리해 Controller 슬라이스 테스트 목적에 가장 부합하다.
      ~~~
      @WebMvcTest(ProductionController.class) 
      @AutoConfigureMockMvc(addFilters = false)
      @Import(TestSecurityConfig.class)
      class ProductionControllerTest {
          // ...
      }
      ~~~
      - @Import(TestSecurityConfig.class)    
        Security 설정만 대체되고 나머지 설정은 그대로 프로덕션 설정 사용.
      ~~~
      @TestConfiguration
      class TestSecurityConfig {
          @Bean
          SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
              http
                  .csrf().disable()
                  .authorizeHttpRequests().anyRequest().permitAll();
              return http.build();
          }
      }
      ~~~
      - 보통 Security 설정만 테스트용으로 생성한다.
        Security는 모든 요청에 개입하고 테스트 목적과 무관한 실패를 만들기 때문이다.

   2. jwtAuthenticationFilter 빈 생성하지 못하게 수정
      SecurityConfig 프로덕션 코드에 JwtAuthenticationFilter Bean을 생성해
      filterChain에 addFilterBefore() 메서드를 통해 등록했다.
      이는 Spring이 뜰 때 JwtAuthenticationFilter Bean을 무조건 생성하라는 의미이다.
      내가 작성한 TestSecurityConfig를 ControllerTest에 적용하는 것은, 테스트에서는 인증을 하지 않겠다는 의미이다.
      그런데도 여전히 JWT 에러가 발생하는 이유는, @Import에 대한 이해가 부족하기 때문이다.
      @Import는 대체가 아닌 추가의 개념이다. 따라서 프로덕션 SecurityConfig가 TestSecurityConfig로 대체되지 않는다.   
      <br>
      여기서 진짜 해결책은 ***프로덕션 SecurityConfig 자체를 테스트 컨텍스트에서 아예 로딩하지 않는 것***이다.
      1. @WebMvcTest 어노테이션의 excludeFilters 속성을 사용.
         ~~~
         @WebMvcTest(
             controllers = ProductionController.class,
             excludeFilters = @ComponentScan.Filter(
                 type = FilterType.ASSIGNABLE_TYPE,
                 classes = SecurityConfig.class
             )
         )
         @Import(TestSecurityConfig.class)
         class ProductionControllerTest {
             // ...
         }
         ~~~
         - 프로덕션 SecurityConfig는 로딩하지 않기 때문에 JwtAuthenticationFilter Bean은 생성되지 않는다.
         - TestSecurityConfig가 로딩되므로, 설정한대로 모든 인증을 허가한다.   
           <br>
      2. SecurityConfig 자체를 조건부 로딩으로 만들기.   
         제일 깔끔하고 실무에서 많이 사용.
         ~~~
         @Profile("!test")
         @Configuration
         public class SecurityConfig { ... }
         ~~~
         ~~~
         @ActiveProfiles("test")
         ~~~
   3. Controller 슬라이스 테스트에서 member 객체가 null인 이유.
      내 프로젝트의 경우, MemberUserDetails가 불필요.
      Member 자체가 UserDetails이기 때문이다.
      즉, Member는 도메인 사용자이자 Security 사용자이다.

      Controller 슬라이스 테스트에서 인증과정이 없기 때문에 SecurityContext가 비어있다.
      따라서 principal이 없기 때문에 member 객체는 null을 가진다.
      Security 설정에서 permitAll() 하는 것은 접근을 허용하는 것이지 인증 객체를 생성해 주는 것이 아니기 때문이다.
      결과적으로 ***테스트에서 Member 주입이 별도로 필요***하다.


---
## 5. @WebMvcTest
### < @WebMvcTest와 MockMvc >
@WebMvcTest 적용 시 MockMvc를 @Autowired로 주입 가능하다.
@WebMvcTest는 Spring MVC 테스트용 ApplicationContext를 구성하고,
그 과정에서 MockMvc를 Bean으로 등록하기 떄문이다.

1. @WebMvcTest   
   Controller 테스트에 필요한 최소한의 MVC 환경 구성.   
   @WebMvcTest 어노테이션 미적용 시,
   단순 JUnit 테스트에서는 Spring Context가 없기 때문에 mockMvc 자동 주입 불가.

2. @WebMvcTest 어노테이션 적용 시 Spring이 로딩하는 것
   - DispatcherServlet
   - HandlerMapping
   - HandlerAdapter
   - ArgumentResolver
   - Validator
   - HttpMessageConverter
   - MockMvc   
     반면, Service, Repository, DB, 전체 비즈니스 Bean은 로딩하지 않음.

3. @WebMvcTest와 MockMvc    
   @WebMvcTest는 MVC 테스트용 ApplicationContext를 구성하면서 MockMvc를 자동으로 Bean으로 등록한다.   
   MockMvc는 Spring MVC 테스트 환경에서만 생성된다.


### < @WebMvcTest에서 Security 설정 >
1. @WebMvcTest의 excludeFilters 속성   
   프로덕션 SecurityConfig에서 표준 필터만 사용하고 커스텀 필터가 없는 경우에는,
   생성자에 복잡한 의존성이 없어 @WebMvcTest에서도 잘 뜨기 때문에 excludeFilters가 필요없다.
   반면, 커스텀 필터가 존재할 때, 필터가 다른 Bean에 의존한다면 excludeFilters가 필요하다.


### < @WebMvcTest와 ObjectMapper DI >
Spring Context가 없는 순수 단위 테스트에서는 new ObjectMapper()를 이용해 ObjectMapper 객체를 직접 생성했다.
반면, @WebMvcTest 적용 시에는 @Autowired를 이용해 Spring이 만든 ObjectMapper Bean을 주입해 사용해야 한다.
JacksonAutoConfiguration에 의해 생성되기 때문이다.
- ObjectMapper   
  ObjectMapper는 Java 객체 ↔ JSON 을 변환해주는 Jackson 라이브러리의 핵심 클래스.   
  Spring MVC에서 요청 Body(JSON) → DTO, 응답 DTO → JSON
  이 모든 걸 실제로 수행하는 “실무의 중심 엔진”.


---
## 6. mocking
### < mocking >
1. Mock을 사용해 테스트 하는 이유
   - 동일 테스트코드를 함께 실행해도 하나는 성공하고 하나는 실패하는 문제 해결가능.
   - DB에 데이터가 바뀌든 의존하고 있는 repository의 로직이 변경되든 관계없이 내가 맡은 역할만 테스트가능.

2. given-when-then(BDD 스타일) 기반 Mocking 테스트 작성   
   Mockito의 BDD API(willReturn, willThrow, should) 사용.
   1) Mock 객체 행위 정의
      willReturn(),willThrow() 등 메서드를 통해 mock 객체(userRepositoy)의 예상 동작을 미리 정의.
   2) exception 검증
      assertThrows와 함께 사용.
   3) 메서드 호출 검증   
      should() 메서드로 해당 메서드가 제대로 호출되었는지 검증.


### < Mock 라이브러리 >
> - 참고블로그 https://soso-shs.tistory.com/109
> - 참고블로그 https://soonmin.tistory.com/85

1. Mockito   
   JAVA 오픈소스 프레임워크.
   단위테스트를 위해 모의 객체(Mock Object)를 생성하고 관리하는데 사용.
   모의 객체를 생성하고 행위를 정의함으로써 의존 객체로부터 독립적인 테스트 수행가능.
2. BDDMockito (BDD, Behavior Driven Development)   
   테스트 코드의 가독성을 높이기 위해 given-when-then 패턴을 따름.


### < Mock 객체 생성 >
1. Mockito.mock()   
   명시적으로 mock 객체를 생성하기 위해 Mockito.mock()을 이용.  
   테스트 메서드 내에서 직접 mock 객체를 생성하고 사용.
   메서드 파라미터로 전달한 타입의 mock 객체 생성.
   mock 객체 세밀하게 제어가능.
   ~~~
     @Test
     void createMock() {
         // Mockito.mock(UserService.class)
         // : UserService 타입의 mock 객체 생성
         UserService mockUserService = Mockito.mock(UserService.class);
     }
   ~~~
2. @Mock 어노테이션
   @Mock 어노테이션을 이용해 객체를 자동으로 mock 처리.
   클래스 레벨에서 필드로 선언한 후 **해당 객체를 직접 초기화하지 않고** Mockito가 자동으로 mock 객체를 주입.
   - @Mock 사용 시 코드 간결.
   - 여러 테스트 메서드에서 동일한 mock 객체를 공유 가능.
   - @Mock 어노테이션 사용 시 MockitoAnnotations.initMocks(this)를 사용해 어노테이션을 초기화 필수.
     단, 최신 버전의 Mockito에서는 **@ExtendWith(MockitoExtension.class)**를 사용해 초기화 가능.
   ~~~
    @Mock
    UserService mockUserService;
   ~~~


### < Stub >
mock으로 생성된 모의 객체는 stub을 통해 예상동작 정의가능.
아래의 두 구문은 동일한 기능 수행.
- stub(가설)   
  생성된 mock 객체의 예상 동작을 정의(mock 객체 리턴내용 지정)

1. **given().willReturn()**   
   given-when-then 스타일의 테스트 코드 작성 시 선호.
   - given()   
     Mockito 2.x 버전부터 BDDMockito 스타일로 도입된 구문.
   ~~~
   given(memberRepository.findByTel1AndTel2AndTel3(any(), any(), any()))
        .willReturn(Optional.empty());
   ~~~
2. when().thenReturn()
   일반적으로 사용.
   - when()   
     Mockito 1.x 버전부터 존재하는 기본적인 구문.
   ~~~
   User user = new User("mockUser", 30, false);
   when(mockUserService.getUser()).thenReturn(user);
   ~~~


### < Spy >
Spy로 만든 mock 객체는 원본 동작을 유지 또는 새로운 정의 가능.
실제 객체를 감싸서 해당 객체의 메서드를 모니터링, 특정 메서드만 stub하거나 mocking 가능하게 함.
1. Stub 하지 않으면 원본 동작대로 실행.
   ~~~
   @Test
   void mockTest2() {
       UserService mockUserService = Mockito.spy(UserService.class); // spy
       User realUser = mockUserService.getUser();

       assertThat(realUser.getName()).isEqualTo("realUser");
   }   
   ~~~
2. Stub 해주면 정의한 동작대로 실행.
   ~~~
   @Test
   void mockTest1() {
       UserService mockUserService = Mockito.spy(UserService.class); // spy
       User user = new User("mockUser", 30, false);
       when(mockUserService.getUser()).thenReturn(user);

       User mockUser = mockUserService.getUser();

       assertThat(mockUser.getName()).isEqualTo("mockUser");
   }
   ~~~
3. @Spy 어노테이션을 이용해 명시적으로 Spy로 mock 객체 생성


### < mock 객체 호출 검증 >
verity() 메서드를 이용해 mock 객체의 메서드 호출을 확인하는 것을 의미
~~~
   @Test
   void mockTest1() {
       UserService mockUserService = Mockito.mock(UserService.class);
       mockUserService.getUser();

      // verification (1회 호출 검증)
      verify(mockUserService).getUser();  
      
      // Verification (2회 호출 검증)
      // verify(mockUserService, times(2)).getUser();
   }
~~~


### < @ExtendWith(MockitoExtension.class) >
@ExtendWith(MockitoExtension.class) 애너테이션이 적용된 클래스는 **Mockito의 기능을 자동으로 활성화.**
필드에 있는 @Mock, @InjectMocks 등은 Mockito가 자동으로 초기화하고, 관련 객체 주입.
JUnit 5에서 MockitoExtension을 사용 시, 테스트 클래스에 @ExtendWith(MockitoExtension.class) 애너테이션을 추가 필요.

1. MockitoExtension.class
   - JUnit 5에서 Mockito를 사용 시 필요한 확장(Extension).
   - MockitoExtension.class 사용 시 @Mock, @InjectMocks, @Captor 등의 Mockito 애너테이션을 자동으로 초기화해주고, 테스트 클래스에서 mock 객체를 관리해주는 역할.

2. 테스트코드 mock 생성 예시 (MemberServiceTest.java)
   MemberService는 MemberRepository에 의존하고 있다.
   MemberRepository를 가짜로 생성해 MemberService에 의존성을 추가해준다.   
   따라서 DB에서 독립적인 테스트가 가능해진다.
   ~~~      
   @ExtendWith(MockitoExtension.class)  // JUnit 5에서 MockitoExtension 활성화
   public class MemberServiceTest {
      // @Mock: MemberRepository를 가짜로 생성해 memberRepository에 담아준다.
      @Mock
      private MemberRepository memberRepository; // mock 객체
     
      // @InjectMocks : Mock으로 생성해준 memberRepository를 memberService에 inject(의존성주입).      
      @InjectMocks
      private MemberService memberService; // mock 객체를 주입 받을 대상
         
      // ...
   }
   ~~~


### < repository의 메서드를 stub하지 않아도 테스트코드 오류가 발생하지 않는 이유 >
1. 궁금증 발생원인   
   기존 테스트코드 작성시에는 repository의 메서드 호출 시 stub 하지 않으면 오류가 발생했다.   
   하지만 ProductionService.saveProduction() 메서드에서
   productionRepository.findBySellerAndCode() 메서드를 서비스 로직에 추가했는데
   테스트코드에서 해당 메서드 호출 시 오류가 발생하지 않았다.

2. 테스트코드가 정상 수행된 이유   
   productionRepository.findBySellerAndCode() 메서드가 실제로 호출되지 않았기 때문이다.   
   Mockito에서 @Mock 어노테이션을 사용해 ProductionRepository를 모킹했기 때문에,
   해당 메서드는 실제로 실행되지 않고 대신 빈 값을 반환하거나 아무 일도 일어나지 않았다.   
   productionRepository.findBySellerAndCode() 메서드는 Optional<Production>을 반환하고,
   해당 값이 존재할 경우 예외를 던지는 코드이다.
   Mockito는 기본적으로 모킹된 객체가 호출되면 빈 값 (예: null 또는 Optional.empty())을 반환하기 때문에
   ifPresent 블록이 실행되지 않는다.
   즉, productionRepository.findBySellerAndCode(sellerId, productionCode) 메서드를 모킹하지 않더라도
   Mockito는 이 메서드가 호출되는 대신 아무 작업도 하지 않으므로, 예외가 발생하지 않는다.
   > 결론     
   > 모킹되지 않으면 기본값인 빈 값(null 또는 Optional.empty())이 반환됨.    
   > 만약 메서드가 빈값을 반환해도 되는 경우, 테스트의 명확성과 일관성을 보장하기 위해 명시적으로 모킹해주는 것이 더 좋을 방법일 수 있음.    
   > 테스트의 정확성을 위해서 Optional.empty()와 Optional.of(existingProduction) 두 가지 경우를 모두 테스트해 보는 것이 좋음.

3. 올바른 테스트코드 작성방법   
   productionRepository.findBySellerAndCode() 메서드를 명시적으로 모킹 필요.
   예를 들어, 해당 메서드가 Optional.empty()를 반환하도록 설정하거나, 이미 존재하는 상품을 반환하도록 설정 가능.


### < @MockBean vs @Mock >
테스트코드에서 @Mock으로 만든 객체는 Controller에 자동 주입되지 않는다.
@Mock은 Mockito 레벨의 객체로 Spring Context에 등록되지 않으며,
Controller에 주입하려면 @MockBean을 사용해야 한다.

1. @Mock
   > 순수 mock 객체 생성 어노테이션.
   > @Mock 단독으로 Service 단위 테스트에서 사용하거나
   @Mock, @InjectMocks를 함께 사용해 Controller 단위 테스트 목적으로 사용.
   > Controller 단위 테스트에서 사용하는 경우는 실무에서는 거의 사용하지 않음.
   - Mockito가 만든 순수 mock 객체.
   - Spring Context에 등록되지 않음.
   - Controller는 Spring이 생성하면서 ApplicationContext에 등록된 Bean을 찾으므로,
     Spring이 생성하는 Controller의 경우, @Mock 으로 생성된 객체를 알 수 없음.
   - @WebMvcTest 적용 시 @Mock 사용하는 경우, NoSuchBeanDefinitionException 또는 실제 Bean 주입 시도.
   ~~~
   @ExtendWith(MockitoExtension.class)
   class ProductionControllerTest {

       @Mock
       ProductionService productionService;

       @InjectMocks
       ProductionController productionController;
   }
   ~~~

2. @MockBean
   > Controller에 주입을 위한 어노테이션.
   > Controller 슬라이스 테스트에서 @WebMvcTest, @MockBean을 함께 사용.
   - Mockito mock 생성.
   - Spring Context에 Bean으로 등록.
   - 기존 Bean이 있으면 교체됨.
   - Controller 생성 시 자동 주입됨.
   ~~~
   @WebMvcTest(ProductionController.class)
   class ProductionControllerTest {

      @Autowired 
      MockMvc mockMvc;

      @MockBean
      ProductionService productionService;
   }
   ~~~


---
## 7. 테스트코드 검증
### < 테스트코드 검증 >
1. 전달인자, 반환값에 대한 검증   
   검증할 때 변경되지 않는 값들에 대해서는 반드시 검증할 필요없음.
   특히, 테스트의 목표가 변경된 값들이 올바르게 처리되었는지를 확인하는 것이라면,
   변경되지 않은 값들은 검증 대상에서 제외가능.

2. 변경된 값들만 필수 검증
   1) 효율성   
      모든 값을 검증하면 너무 많은 검증이 이루어지고 테스트 코드가 불필요하게 복잡해짐.
      특히, ***변경되지 않은 값들은 예상 가능하므로 검증 생략 가능.***
   2) 테스트 목적   
      테스트는 로직이 예상대로 작동하는지 확인해 수정된 값들이 제대로 처리되는지 확인하는 것이 목적.
      띠리서 변경된 값에 대해서만 집중.

3. 변경되지 않은 값들에 대한 간접적 검증.   
   변경되지 않아야 할 부분도 간접적으로 검증하는 것이 중요.
   만약 변경되지 않은 값이 의도치 않게 변경되면, 버그나 논리적 오류가 발생한 가능성이 있기 때문이다.
   하지만 **모든 값을 검증하는 것은 비효율적**이므로,
   **변경되지 않는 값 중 핵심적인 값들만 검증**하여 테스트의 효율성을 유지.
   1) 핵심 식별자(예: id, seller, code, name 등)가 의도치 않게 변경되지 않았는지 간단히 검증.
   2) 비즈니스 로직에 따라 중요한 값이 변경되지 않아야 하는 경우, 해당 값도 검증.

4. 검증 수행 방법
   1) Mockito.verify()
      ~~~
       // then
       // 토큰이 redis에 1번 등록됨.(1번 수행됨)
       Date now = new Date();
       long validTime = expirationDate.getTime() - now.getTime();
       verify(redisSingleDataService, times(1))
               .saveSingleData(eq(token), eq("blacklist"), argThat(duration ->
                       duration.compareTo(Duration.ofMillis(validTime)) >= 0));
      ~~~
      - times(횟수)   
        특정 메서드가 특정 횟수만큼 호출되었는지 검증.
        ~~~
        Mockito.verity(mockObject, times(n)).method() 
        ~~~
      - eq()   
        Mockito의 Argument Matcher 중 하나.
        특정 인자가 기대하는 값과 정확히 일치하는지 검증.

    2) Mockito의 ArgumentMatcher
       eq()와 같은 기본 메서드 외에도, 임의의 조건을 설정하여 검증할 수 있는 방법을 제공.
       1. argThat()   
          사용자정의 조건을 지정.
          ~~~
           // 전달된 duration값이 Duration.ofMillis(validTime)보다 크거나 같은지 비교하는 사용자정의 조건 설정.
           // compareTo() : 객체간 크기 비교 시 사용.
           //             : duration이 크면 1 / duration이 작으면 -1 / duration과 값이 동일하면 0
           argThat(duration -> duration.compareTo(Duration.ofMillis(validTime)) <= 0);
          ~~~
       2. ArgumentCaptor   
          ArgumentCaptor를 사용해 메서드에 전달된 값을 캡쳐하고 이를 비교해 검증 수행.
          ~~~
           // duration값 캡쳐를 위해 ArgumentCaptor<Duration> 객체생성
           ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);
    
           // duration값 캡쳐를 위해 duration값 자리에 durationCaptor.capture() 전달.
           verify(redisSingleDataService, times(1)).saveSingleData(eq(token), eq("blacklist"), durationCaptor.capture());
   
           // Duration 캡쳐값 가져와 유효시간이 validTime 이상인지 검증
           Duration capturedDuration = durationCaptor.getValue();
           assertTrue(capturedDuration.toMillis() <= validTime, "Duration should be less than or equal to validTime");
          ~~~


### < 테스트코드 검증과 ArgumentCaptor >
- 참고블로그 https://hanrabong.com/entry/Test-ArgumentCaptor%EB%9E%80
1. capture()      
   해당 인자를 낚아채는 메서드.
2. getValue()   
   낚아챈 인자의 값을 불러올 때 사용.    
   만약 해당 메서드가 여러번 호출되는 경우, 가장 마지막으로 호출된 메서드의 인자를 반환.
3. getAllValues()   
   낚아챈 인자 전부를 List 형태로 반환.
4. verify()   
   서비스 내부 로직이 실행됐는지 여부 체크.


### < 테스트코드 전달인자 검증 >
1. 전달인자 검증 예시
   - 참고 ProductionServiceTest.successModifyProduction()   
     주로 toDto() **메소드에 전달된 값들이 의도한 대로 정확히 전달되었는지를 확인**.
     이 테스트 코드에서는 전달인자 검증이 중요한 부분이므로
     ArgumentCaptor를 사용해 toDto() 메소드로 전달된 Production 객체의 속성값들이
     예상대로 처리되었는지 검증이 필요.

2. 전달인자 검증 이유   
   ArgumentCaptor를 사용하여 전달인자를 캡처한 뒤 이를 검증하면
   해당 객체가 수정된 값들을 정확하게 포함하고 있는지 확인가능.

3. ArgumentCaptor 사용해 전달인자를 검증하는 코드
   - 참조 : ProductionServiceTest.successModifyProduction()
   ~~~
    // ArgumentCaptor 생성
    ArgumentCaptor<Production> productionCaptor = ArgumentCaptor.forClass(Production.class);
    
    // stub(가설) : productionMapper.toDto() 실행 시 productionCaptor로 인자를 캡처하도록 설정.
    given(productionMapper.toDto(productionCaptor.capture()))
        .willReturn(expectedResultProductionDto);
    
    // when
    ResponseProductionDto responseProductionDto =
        productionService.modifyProduction(requestProductionDto, member);
    
    // then
    // 1. 상품 수정 검증
    assertEquals(requestProductionDto.getId(), responseProductionDto.getId());
    assertEquals(requestProductionDto.getDescription(), responseProductionDto.getDescription());
    assertEquals(requestProductionDto.getSaleStatus(), responseProductionDto.getSaleStatus());
    assertEquals(originProductionEntity.getSeller(), responseProductionDto.getSeller());
    assertEquals(originProductionEntity.getCode(), responseProductionDto.getCode());
    assertEquals(originProductionEntity.getName(), responseProductionDto.getName());
    assertEquals(originProductionEntity.getCategory(), responseProductionDto.getCategory());
    
    // 2. 상품옵션 수정 검증
    RequestModifyProductionOptionDto requestUpdateOption = requestProductionDto.getOptions().get(0);
    ResponseProductionOptionDto responseUpdatedOption = responseProductionDto.getOptions().get(0);
    assertEquals(requestUpdateOption.getId(), responseUpdatedOption.getId());
    assertEquals(requestUpdateOption.getQuantity(), responseUpdatedOption.getQuantity());
    
    // 3. 상품옵션 신규등록 검증
    RequestModifyProductionOptionDto requestInsertOption = requestProductionDto.getOptions().get(1);
    ResponseProductionOptionDto responseInsertedOption = responseProductionDto.getOptions().get(1);
    assertNull(requestInsertOption.getId());
    assertEquals(3L, responseInsertedOption.getId());
    
    // 4. toDto에 전달된 인자 캡처 후 검증
    Production capturedProduction = productionCaptor.getValue();
    assertEquals(updateDescription, capturedProduction.getDescription());
    assertEquals(updateSaleStatus, capturedProduction.getSaleStatus());
    assertEquals(productionId, capturedProduction.getId());
   ~~~
   1) Production 객체에 대한 검증   
      toDto()로 전달된 Production 객체가 수정된 설명(updateDescription)과 판매 상태(updateSaleStatus)를 정확히 포함하는지 검증.
   2) ProductionOption 객체에 대한 검증   
      ResponseProductionOptionDto가 수정된 quantity와 일치하는지,
      신규 추가된 옵션에 대해 id, quantity 값이 예상대로 처리되는지 검증.
   3) toDto()에 전달된 Production 객체 검증   
      ArgumentCaptor를 사용하여 productionMapper.toDto()로 전달된 Production 객체를 캡처하고, 그 값들을 검증.
   4) 옵션 quantity 및 price 값이 올바르게 전달되는지 확인   
      ProductionOption에 대해 수정하고 신규 삽입된 옵션이 quantity와 price를 올바르게 갖고 있는지 검증.

4. productionMapper.toDto() 메소드의 전달인자 검증   
   메소드가 호출되었을 때, 실제로 Production 객체가 올바르게 전달되었는지 확인가능.   
   expectedProductionEntity가 productionMapper.toDto()에 전달된 객체일 때,
   그 객체가 올바르게 수정된 값들을 포함하고 있는지 확인.

5. modifyProductionOptionMapper.toEntity() 메소드의 전달인자 검증   
   전달되는 DTO 값들이 실제로 Entity로 올바르게 변환되는지 검증.
   quantity나 price 값이 제대로 전달되는지 검증가능.

6. ProductionOption 관련 인자들 검증    
   수정된 옵션(RequestModifyProductionOptionDto)들이
   실제로 서비스 로직에 전달될 때 값들이 올바르게 전달되었는지 확인.
   RequestModifyProductionOptionDto에서 quantity, price, optionCode 등이 올바르게 설정되었는지 확인가능.
   expectedResponseOptionDtoList에 있는 값들이 요청 DTO에서 예상한 값과 일치하는지 검증가능.

7. 서비스 내에서 인자 검증   
   productionService.modifyProduction()에서 인자들이 올바르게 처리되는지도 중요한 검증 대상.
   requestProductionDto의 값들이 제대로 전달되었는지,
   productionService.modifyProduction() 내부에서 originProductionEntity에 대한 수정이 예상대로 이루어졌는지 검증.

### < Captor 사용이 필요한 경우 >
1. 저장 직전 값 검증
2. 외부 시스템으로 나가는 요청 파라미터 검증
3. 반환값으로는 확인할 수 없는 부수효과 검증

### < Captor로 검증해야하는 값 >
Captor는 “이 메서드가 계산해서 만들어낸 값”만 잡는다.
이미 메서드 인자나 key로 보장된 값은 잡지 않는다.


---
## 8. 테스트코드에서 필드주입
> 프로덕션 코드에서는 설계 안정성을 위해 생성자 주입을 사용하지만,
테스트 코드에서는 가독성과 작성 속도를 이유로
@Autowired, @MockBean 기반 필드 주입을 실용적으로 허용한다.

1. 프로덕션 코드에서 필드 주입 지양 이유
   - 불변성 보장 불가
   - 의존성 누락을 컴파일 타임에 못 잡음
   - 테스트 어려움
   - 숨겨진 의존성 증가   
     결과적으로 생성자 주입 권장.

2. 테스트 코드에서 필드 주입 널리 허용되는 이유   
   프로덕션 코드와 테스트 코드의 목적은 다르다.
   프로덕션 코드는 안정성, 구조적 설계, 재사용, 불변성을 목적으로 하는 변면,
   테스트 코드는 가독성, 빠른 작성, 단발성, 편의성을 목적으로 한다.
   테스트 코드에서 생성자 주입을 하는 경우, 테스트 가독성 저하, 설정 코드 증가 등으로 실익이 없다.
   테스트 클래스는 Spring이 관리하는 Bean이 아니고 수명 주기가 짧다.
   또한 상태를 외부에 노출하지 않고 재사용성, 확장성 고려가 거의 필요가 없다.
   따라서 설계의 품질보다 편의성이 우선되기 때문에, 필드 주입이 널리 허용된다.
   ~~~
   // 필드 주입 예시
   @WebMvcTest(ProductionController.class)
   class ProductionControllerTest {

       @Autowired
       MockMvc mockMvc;

       @MockBean
       ProductionService productionService;
   }
   ~~~
   ~~~
   // 생성자 주입 예시
   @WebMvcTest(ProductionController.class)
   class ProductionControllerTest {

       private final MockMvc mockMvc;
       private final ProductionService productionService;

       public ProductionControllerTest(MockMvc mockMvc,
                                        ProductionService productionService) {
           this.mockMvc = mockMvc;
           this.productionService = productionService;
       }
   }
   ~~~


---
## 9. Reflection - private 메서드 테스트코드 작성
- 리플렉션을 사용 시 클래스의 메서드나 필드에 직접 접근할 수 없더라도 접근 제어자에 관계없이 해당 클래스의 메서드나 필드를 검사하거나 수정가능.
- 자바의 java.lang.reflect 패키지에서 제공되는 클래스를 사용.
- 프로그램 실행 중에 클래스, 메서드, 필드 등을 동적으로 다룰 수 있는 기능.
- 리플렉션을 사용할 때는 정말 필요한 경우에만 사용하고, 과도하게 사용하지 않아야함.

1. 리플렉션을 사용할 때의 주의점
   1) 성능 저하   
      리플렉션은 일반적인 메서드 호출보다 성능이 낮음.
   2) 컴파일 시점 검사 불가능   
      리플렉션을 사용하면 컴파일 타임에 오류를 잡기 어렵고, 런타임 시에만 오류가 발생가능.
   3) 불완전한 설계   
      리플렉션을 지나치게 많이 사용하면 코드가 복잡해지고, 객체 지향 설계 원칙에 어긋날 수 있음.

2. 메서드 호출
   1) Class.getDeclaredMethod() 또는 Class.getMethod()   
      해당 클래스의 메서드 **객체를 가져옴.**
   2) setAccessible(true)   
      private 메서드에 **접근가능하도록 변경.**
   3) Method.invoke()   
      해당 메서드를 **실행.**
   ~~~
    class JwtAuthenticationProviderTest {
    
        private JwtAuthenticationProvider jwtAuthenticationProvider;
    
        @BeforeEach
        void setUp() {
            // 테스트용 JwtAuthenticationProvider 객체 생성
            jwtAuthenticationProvider = new JwtAuthenticationProvider("bXlTZWNyZXRLZXk=");  // Base64로 인코딩된 비밀키
        }
    
        @Test
        void getDecodedSecretKey_shouldReturnValidSecretKey_usingReflection() throws Exception {
            // 리플렉션을 사용하여 private 메서드 접근
            Method method = JwtAuthenticationProvider.class.getDeclaredMethod("getDecodedSecretKey");
            method.setAccessible(true);  // private 메서드 접근을 허용
    
            // 메서드 호출
            SecretKey secretKey = (SecretKey) method.invoke(jwtAuthenticationProvider);
    
            // then
            assertNotNull(secretKey, "SecretKey should not be null");
            assertTrue(secretKey instanceof SecretKey, "Returned object should be an instance of SecretKey");
        }
    }   
   ~~~

3. 필드값 변경
   1) Field.setAccessible(true)   
      private 필드에 접근가능하도록 변경.
   2) Field.set()   
      값을 수정.


---
## 10. JPA의 Dirty Checking을 사용 시 테스트코드 작성
- JPA의 Dirty Checking을 사용할 때는 명시적으로 repository.save()를 호출하지 않아도 JPA가 트랜잭션이 끝날 때 자동으로 변경된 엔티티를 저장합니다.    
  하지만 이러한 명시적 호출이 아닌 동작도 테스트에서 모킹해야 할지 여부는 조금 다릅니다.

1. Dirty Checking 동작을 테스트할 때 모킹 필요 여부
   - repository.save()를 호출하지 않고 Dirty Checking을 사용하는 경우,
     **모킹해야 할 대상**은 repository.save()가 아니라 **entityManager 또는 JPA의 트랜잭션 처리**입니다.
   - 하지만 대부분의 경우 우리는 **repository.save()**가 호출되는지 확인하거나 트랜잭션의 결과가 잘 반영되는지 확인하려는 목적이기 때문에
     **실제로 repository.save()를 Mocking할 필요는 없습니다.**

2. Dirty Checking 시 테스트코드 작성
   - repository.save() 호출을 명시적으로 확인하고자 할 때만 Mocking이 필요합니다.     
     예를 들어, save() 호출을 기대하는 테스트에서는 repository.save()를 Mocking하여 호출 여부를 확인할 수 있습니다.
   - Dirty Checking의 동작을 직접 테스트하려면 트랜잭션 관리와 관련된 사항을 테스트하고,
     엔티티가 수정된 후 트랜잭션이 정상적으로 커밋되는지 확인하는 것이 중요합니다.

3. Dirty Checking을 통한 테스트 코드 작성 방법
   - Dirty Checking을 사용하는 경우 단위 테스트 코드에서 save()를 호출하지 않더라도
     JPA가 엔티티의 변화를 자동으로 저장하는지 확인할 수 있습니다.
   - 트랜잭션이 커밋되었을 때 엔티티가 변경되었는지 확인하는 방법.
   > Dirty Checking을 사용할 때는 repository.save()를 호출하지 않지만, **트랜잭션 커밋 시 JPA가 자동으로 변경된 데이터를 저장**합니다.    
   단위 테스트에서는 repository.save()를 Mocking하기보다는, 트랜잭션 커밋 후 엔티티가 올바르게 변경되었는지 확인하는 방식으로 테스트할 수 있습니다.    
   즉, 트랜잭션과 **Dirty Checking 동작 테스트 시 @Transactional 어노테이션을 활용하여 엔티티의 변경 사항이 DB에 반영되었는지 확인**하는 것이 일반적입니다.
   ~~~
    @RunWith(MockitoJUnitRunner.class)
    public class ProductionServiceTest {
    
        @Mock
        private ProductionRepository productionRepository;
        @Mock
        private ProductionOptionRepository productionOptionRepository;
    
        @Mock
        private ProductionMapper productionMapper;
        @Mock
        private ModifyProductionOptionMapper modifyProductionOptionMapper;
    
        @InjectMocks
        private ProductionService productionService;
    
        @Test
        @Transactional  // 트랜잭션을 이용해 Dirty Checking 테스트
        public void testModifyProductionWithDirtyChecking() {
            // given: Entity 준비
            Production existingProduction = new Production();
            existingProduction.setId(1L);
            existingProduction.setSeller(1L);

            // when: save 호출 없이 엔티티 수정 (Dirty Checking을 통한 수정)
            existingProduction.setDescription("새로운 설명");  
            existingProduction.setSaleStatus(ON_SALE);
    
            // 트랜잭션 커밋 후 자동으로 DB에 반영됨
    
            // then: 엔티티가 제대로 수정되었는지 확인
            verify(productionRepository).save(existingProduction);  // save가 호출되었는지 확인 (만약 save를 Mock으로 했다면)
            assertEquals("새로운 설명", existingProduction.getDescription()); // 변경된 내용 검증
            assertEquals(ON_SALE, existingProduction.getSaleStatus()); // 변경된 내용 검증
        }
    }
   ~~~
   1) @Transactional   
      이 어노테이션을 사용하면, 트랜잭션이 자동으로 시작되고 커밋됩니다.
      트랜잭션이 커밋될 때 Dirty Checking이 작동하여, 엔티티의 변경 사항이 자동으로 DB에 반영됩니다.
   2) 엔티티 수정   
      엔티티 수정 시 실제로 repository.save()를 호출하지 않고도 변경된 사항이 트랜잭션 커밋 시 DB에 반영됩니다.
   3) 검증   
      수정된 엔티티의 필드 값을 확인하여 변경된 값이 Dirty Checking을 통해 정상적으로 반영되었는지 검증합니다.

4. dirty checking 검증
   > - 단위 테스트에서는 save()가 호출된 엔티티를 검증할 수 있지만,
       실제로 어떤 쿼리(insert, update)가 실행되었는지는 Mockito로 직접 확인하기 어려운 점이 있습니다.
   > - 실제 INSERT/UPDATE 쿼리를 검증하려면 통합 테스트나 SQL 로그를 활용하거나, EntityManager를 직접 사용하는 방법을 고려해야 합니다.

   1. Mockito를 사용하여 save() 호출을 검증
      - Mockito를 사용해 save() 호출이 INSERT 또는 UPDATE 쿼리를 수행했는지를 직접적으로 확인하는 것은 어렵습니다.
      - 다만, save() 메서드가 호출되는지 그리고 호출된 엔티티가 새로 생성된 것인지 기존 것인지를 확인할 수는 있습니다.
   2. 실제 쿼리를 검증하기 위한 방법
      - INSERT나 UPDATE 쿼리가 실제로 실행되는지 확인하려면,
        실제 데이터베이스와의 통합 테스트 또는 @DataJpaTest와 같은 통합 테스트를 통해 실제 쿼리가 실행되도록 할 수 있습니다.
        이 방법은 실제 DB 연결을 사용하므로 쿼리가 실행된 내용을 확인할 수 있습니다.
      - 실제 데이터베이스에 데이터를 저장하고 INSERT 또는 UPDATE가 발생했는지 확인하는 방식입니다.
   3. @Query나 EntityManager를 사용하여 쿼리 검증
      - 만약 save()가 아닌, 직접 쿼리를 실행하는 방식 (예: @Query 또는 EntityManager)을 사용한다면 실제 SQL 로그를 확인하는 방법도 있습니다.
      - 이 경우, @Query나 EntityManager를 사용하여 INSERT/UPDATE를 명시적으로 수행하고
        SQL 로그를 확인하거나 TestEntityManager를 사용하여 쿼리 실행 여부를 검증합니다.


---
## 11. 프로덕션, 테스트 config 파일
1. Spring의 설정 로딩 방식
   1. 기본 설정 로딩.
   2. @Import된 설정 추가 로딩.
   3. 같은 타입 Bean 존재 시, 후순위가 덮어씀.

2. 프로덕션 Config   
   실제 인증/인가, Redis, JWT, DB 같은 외부 의존성을 생성한다.
   따라서 복잡하고 무겁다.
   ~~~
   @Configuration
   public class SecurityConfig {
       @Bean
       SecurityFilterChain securityFilterChain(HttpSecurity http) { ... }

       @Bean
       JwtAuthenticationFilter jwtAuthenticationFilter(...) { ... }
   }
   ~~~ 

3. TestConfig    
   테스트에서만 쓰는 설정 클래스.   
   TestConfig는 테스트 환경에서만 사용되는 전용 설정 클래스로,
   테스트에 필요 없는 보안 인프라를 “대체하거나 제거”해서
   ApplicationContext가 정상적으로 뜨게 만드는 장치.      
   프로덕션 Config를 그대로 쓰기 어렵거나 쓰면 안 되는 경우 이를 대체·단순화하기 위해 둔다.  
   <br>
   테스트 전용으로 단순화된 설정이다.
   외부 의존성을 제거해 테스트 목적에만 집중 가능하다.
   ~~~
   @TestConfiguration
   public class TestSecurityConfig {
       @Bean
       SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
 
            return http.build();
       }
   }
   ~~~
   - @TestConfiguration : 테스트 전용 설정
   - SecurityFilterChain : Security의 핵심
   - permitAll() : 인증/인가 전부 통과
   - JWT 관련 설정 없으므로, JWT 필터 자체가 생성되지 않음.
   - TestSecurityConfig를 통해 인증/인가를 모두 허용하는 SecurityFilterChain을 정의해
     JWT 보안 절차를 완전히 우회하고, Controller 책임만 검증할 수 있도록 함.   
   <br>


---
## 12. Spring Security와 테스트코드
### < permitAll() 경로 테스트에서 member.roles가 null이면 에러 발생 이유 >
1. 에러 발생   
   Validation 실패는 Security와 무관하다.
   따라서 Member의 role은 필요 없는 정보라고 생각했다.
   전달하면 “인증이 필요한 테스트”처럼 보이기 때문에 테스트 의도를 흐린다고 느꼈기 때문이다.
   그래서 role에 null을 전달했는데 에러가 발생했다.

2. 모든 접근 허가 경로와 권한 검증
   1) 의문점 - permitAll()에 대한 오해  
      member 객체의 roles=null일 때 에러가 발생했다.   
      permitAll() 설정한 접근 경로에 접근 시, 권한 검증을 수행하지 않는다고 생각했다.
      하지만 오류가 발생했다.
   2) 오해 정정   
      permitAll()은 인가(Authorization)만 건너뛰고, 인증(Authentication) 객체 생성 과정은 여전히 수행된다.
      즉, 권한 검증을 생략할 뿐, UserDetails->Authentication 생성 과정은 그대로 수행된다.
      그렇기 때문에 roles = null이면 getAuthorities()에서 NPE가 발생한다.   
      요청이 들어오면 Spring Security는 먼저 인증 객체를 생성한다.
      permitAll()을 적용했다 하더라도 이는 항상 실행된다.

3. 내 테스트코드 실행과정
   1) Spring Security 실제 인증 흐름
      - 인증객체생성 > UserDetails 로딩 > getAuthorities 호출 > 권한 비교   
        permitAll() 적용 시, 위 과정에서 권한 비교 부분만 생략됨.
        Spring Security는 기본적으로 익명 사용자 또는 인증 사용자 둘 중 하나의 Authentication은 항상 SecurityContext에 넣는다.
        하지만 테스트에서 with(user(member))를 쓰는 순간, 익명 사용자가 아닌 인증 사용자를 추가하게 된다.
      ~~~
      .with(user(member))
      ~~~   
      이 요청은 '이미 인증된 사용자'를 의미한다.
      - Spring Security 내부 동작 과정
         1) member를 UserDetails로 취급.
         2) getAuthorities() 호출.
         3) roles.stream() 실행.
            이 때, roles = null이 된다.
        ~~~
        public class Member extends BaseEntity implements UserDetails {
            // ...
      
            // 유저:권한 (1:N)
            @OneToMany(cascade = CascadeType.ALL, mappedBy = "member")
            @JsonManagedReference
            private List<MemberAuthority> roles; // 사용자가 여러 권한을 가질 수 있음.

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                // authorities 정보를 SimpleGrantedAuthority로 매핑
                // -> 스프링 시큐리티에서 지원하는 role 관련기능을 쓰기 위함.
                return this.roles.stream()
                                 .map((MemberAuthority authority) ->
                                          new SimpleGrantedAuthority(authority.getAuthority().toString()))
                                 .collect(Collectors.toList());
        }
        ~~~

4. 해결 방법
   1) Member Entity를 안전하게 만들기.   
      getAuthorities()를 null-safe 처리한다.   
      이는 permitAll() 여부와 무관하게 안정성 확보를 위해 실무에서 필수적으로 추가되는 로직.
      ~~~
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
          if (roles == null) {
              return List.of();
          }

          return roles.stream()
                   .map(authority ->
                           new SimpleGrantedAuthority(authority.getAuthority().toString()))
                  .toList();
      }
      ~~~
   2) user() 미사용   
      인증 자체가 필요없는 permitAll() 경로라면, user()를 굳이 쓰지 않는 것이 더 자연스럽다.


### < 권한 검증 테스트가 누락되는 문제 해결 >
1. 문제점   
   권한에 대한 검증이 잘 수행되고 있는지 궁금해서 허용 권한인 SELLER 대산 CUSTOMER 권한을 전달했는데 테스트가 통과되었다.
   기존 정상 시나리오 테스트에서 권한을 설정하고 전달하고 있지만, 검증이 수행되지 않는 문제 발생한 것이다.

2. 내 코드의 상황   
   1) 권한 설정   
      내 코드는 SecurityConfig에서 permitAll()을 이용해 해당 경로에 대해 모든 권한이 접근 가능하도록 설정하고 있다.   
      단, Controller에서 @PreAuthorize를 이용해 'SELLER' 권한만 해당 경로에 접근 가능하도록 제한하고 있다.
   2) 권한을 가진 사용자 전달
      테스트에서는 with(user(member))를 통해 권한 체크를 수행하도록 하고 있다.

3. 문제발생원인
   Spring Security에는 인가가 동작하는 2단계가 있다.
   1) WebSecurity - Filter 단계의 인가   
      URL/HTTP Method 기준으로 Filter Chain 단계에서 permitAll, authenticated, hasRole 등을 이용해 Controller 진입 전에 인가.
   2) Method Security - AOP 기반 인가   
      Controller/Service 메서드 기준으로 Controller 진입 후 AOP 프록시 기반이므로, 명시적인 활성화 필수.
   - Spring Security 인가 동작 과정      
     요청 -> Security Filter Chain -> permitAll() 통과 -> Controller 진입 -> @PreAuthorize AOP 적용되어 실행 -> 메서드 정상 실행
   
   현재 테스트 환경 상황에는 TestSecuritConfig가 존재하고 SecurityFilterChain이 정상 등록되어있다.
   하지만 현재 호출하는 컨트롤러가 @PreAuthorize를 적용해 AOP 기반 인가를 사용하고 있음에도 @EnableMethodSecurity 어노테이션 적용이 되어있지 않다.
   따라서 기존 테스트에서는 SecurityContext에 Authentication 객체를 전달하고, 
   permitAll 설정에 따라 해당 경로에 대해 모든 접근 권한을 허가하지만,
   Method Security 설정에 따라 접근 권한을 제한해야한다.
   하지만 Method Security가 적용되어도 활성화 되어있지 않아 권한 검증이 수행되지 않았다.    
   Method Security는 Filter 이후에 동작하므로 Filter가 permitAll 해도 Controller 메서드 호출 직전에
   MethodSecurityInterceptor가 다시 권한 검사를 수행해야 한다.
   결과적으로 정상 시나리오에 작성한 것과 달리 
   실제로는 권한이 컨트롤러까지 전달되었으나 AuthenticationPrincipal 주입만 확인하고 아래의 두 검증은 실제 수행하지 않는다.
   - SELLER만 접근 가능.
   - 인증된 사용자만 접근.

4. 해결방법
   테스트 Security 설정에 Method Security 활성화
   Method Security가 테스트 컨텍스트에서 활성화되지 않았다.
   없으면 @PreAuthorize 절대 검증되지 않는다.
   - @PreAuthorize 적용해 인가. 
   ~~~
    /**
     * 상품, 상품옵션목록 등록 post /production
     **/
    @PostMapping
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<ResponseProductionDto> registerProduction(
            @RequestBody @Valid RequestProductionDto production,
            @AuthenticationPrincipal Member member) {

        return ResponseEntity.ok(
                productionService.registerProduction(production, member));
    }
   ~~~
   - @EnableMethodSecurity 적용해 Method Security 활성화.
   ~~~
   @TestConfiguration
   @EnableMethodSecurity
   public class TestSecurityConfig {
       /** 인증관련설정: 자원별 접근권한 설정 */
       @Bean
       public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
           http
                // CSRF 보호를 비활성화
                // : HTTP POST로 직접 엔드포인트 호출 시 기본적으로는 CSRF 보호를 비활성화 필요.
                .csrf(AbstractHttpConfigurer::disable)

                // 접근경로허가
                .authorizeHttpRequests((authorizeRequests) ->
                        authorizeRequests
                                // 회원가입, 로그인 경로에 대해 모든인원 접근허용
                                .requestMatchers("/member/signup/**",
                                        "/member/signin/**",
                                        "/production").permitAll()
                                .requestMatchers(HttpMethod.GET,  "/production/*").permitAll()
                                // 그 외 경로에 대해 인증필요
                                .anyRequest().authenticated());

          return http.build();
       }
   }
   ~~~
   - 테스트 시 권한을 가진 사용자 전달
   ~~~
    @Test
    @DisplayName("상품등록 성공")
    void successRegisterProduction() throws Exception {
        // given 생략
   
        // when / then
        mockMvc.perform(post("/production")
                        .with(user(member))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestProductionDto)))
                .andExpect(status().isOk());
    }
   ~~~


5. 권장사항   
   permitAll의 경우에는 보안 상 위험하고 예측이 어려우므로 permitAll이 아닌 경우는 authenticated 처리하도록 하자.


---
## 13. 유효성 검증 테스트
### < 애플리케이션의 계약/정책 테스트 전략 >
1. 애플리케이션의 계약/정책 테스트
   이는 Spring 기본 동작을 검증하는 테스트가 아니라,
   Spring 위에 정의한 개발자가 선택/정의한 계약과 정책을 검증하는 테스트이므로 테스트의 가치가 높다.
   1) 에러 코드 정책   
      예외를 직접 처리하는 경우   
      잘못된 값이 입력되었을 때 400 상태 코드를 출력하는지가 아니라
      우리 에러 응답 정책이 유지되느냐를 확인한다.
   2) 에러 응답 포맷   
      에러 응답 구조가 API 계약인 경우
      Front가 해당 에러 응답 구조를 의존하는 경우, 테스트로 반드시 보호해야 함.
   3) 권한별 메시지 및 접근 제어 규칙   
      보안/라우팅 정책과 결합된 경우
      - 인증된 사용자만 접근 가능.
      - 권한에 따라 다른 메시지 출력.
   4) Validation 그룹 / 커스텀 Validator
      커스텀 Validator의 경우, 비즈니스 규칙으로 테스트 가치가 있음.

2. 테스트 전략
   1) WebMvcTest에서 Web 계층 테스트를 최소화
      ***정상 시나리오(테스트케이스), 요구사항/정책으로 정의한 비즈니스 규칙만 테스트***한다.
      validation 실패 대표 케이스는 2~4개 정도가 적당하므로, 모든 필드에 대해 검증할 필요 없다.
      DTO의 경우 유효성 검증이 걸려있고, 실제 동작한다는 사실만 증명하면 충분하다.   
      또한, 타입 변환 실패까지 다 테스트하지 않는다.
      - 올바른 요청 (HTTP status 200 반환)
      - 올바른 인증 정보 (접근 가능 여부)
      - 올바른 JSON (정상 바인딩)
      - 요구사항에 맞는 응답 구조
      - DTO Validation (개발자 실수 잡기)
      - 커스텀 Validator (비즈니스 규칙)
   2) 통합 테스트에서 한 번만 검증   
      예를 들면, 프레임워크가 올바르게 설정되어있다는 신뢰 확보를 위해 통합테스트에서 검증 할 수 있다.
      대표적으로 문자열->long->400, 음수->400 을 대표 케이스 1,2개만 확인한다.
      이를 통해 Spring MVC + Validation이 정상 동작하고 있다는 사례만 확인한다.
   3) Exception 정책 테스트는 별도 진행   
      잘못되 입력값에 대해 우리가 정의한 에러 응답이 반환되는지 확인.
      이는 대표적인 케이스를 통해 보호. (Controller마다 테스트 작성할 필요 없음.)

3. 적용 사례
   - 참고 : ProductionControllerTest.class 에서 상품등록 관련 테스트코드
   1) 상품등록 controller 테스트에서 검증한 부분
      - 정상 시나리오    
        “SELLER 권한을 가진 사용자가 상품과 옵션을 JSON으로 요청하면
        ProductionService의 결과를 그대로 200 OK JSON 응답으로 반환한다.”   
        - 요청 JSON → DTO로 잘 매핑되는지   
        - 인증된 사용자가 접근 가능한지 (@PreAuthorize)   
        - 서비스 호출 결과를 HTTP 응답(JSON)으로 잘 변환하는지   
      - DTO Validation
        - 문자열 제약 검증 (상품코드 형식 오류)
        - 커스텀 검증 (유효하지 않은 Enum 값 오류)
        - 중첩 검증 (옵션 유효성 미검증)
   2) 상품등록 controller 테스트에서 아래의 의도적으로 제거한 부분
      이들은 다른 테스트에서 다루는 게 옳음.
      - SELLER가 아닐 때 403
      - 인증되지 않았을 때 401
      - @Valid 실패 시 400
      - 옵션 없을 때 에러


### < 웹 계층 테스트의 책임 범위 - API 계약과 Validation 책임 구분 >
WebMvcTest에서 무엇을 테스트하고 무엇을 테스트하지 않을 것인가?

1. WebMvcTest은 제약사항 테스트 수행.    
   WebMvcTest에서 Spring이 기본으로 보장하는 타입 바인딩 실패는 보통 테스트하지 않음.   
   개발자가 명시적으로 추가한 제약사항은 테스트 대상.

2. Controller의 API 문서로서 역할.   
   WebMvcTest의 목적은 컨트롤러의 요청 처리 계약(API contract) 검증이다.   
   개발자의 선언 실수를 전부 잡아내는 테스트는 아니다.

3. ***DTO Validation 테스트 필수.***   
   DTO Validation은 개발자가 직접 정의한 규칙으로, 비즈니스 요구사항의 일부이다.   
   해당 내용이 빠지면 명백한 버그를 발생시키므로 프론트와의 게약이 깨지게 된다.   
   따라서 API 계약 그 자체이므로 테스트로 보호해야 한다.
   단, 필드 하나하나의 validation 로직을 전부 테스트할 필요는 없다.
   각 제약 조건의 대표적인 케이스만 작성할 수 있도록 한다.
   1) validation 실패 테스트 검증
      - 컨트롤러 진입 차단 : 오류발생 검증
      - 비즈니스 로직 보호 : service 호출되지 않음을 검증

4. PathVariable 타입체크 테스트 제외.   
   이는 Spring MVC가 강제하는 계약이다.
   따라서 누락하거나 타입이 일치하지 않으면 아예 컨트롤러에 진입이 불가하다.
   @PathVariable 어노테이션이 적용된 필드의 타입 체크는 불필요하다.

5. ***유효성검증 어노테이션이 붙은 PathVariable은 선택적 테스트.***
   이는 Spring 기본 책임이 아닌 개발자가 추가한 제약.   
   단순한 방어적 검증이면 테스트 제외하고, 비즈니스 규칙이라면 테스트를 수행한다.
   1) 단순 방어적 검증   
      예를 들어 ID는 항상 양수니까 그냥 적용한 어노테이션이라면 이는 설계상 당연한 규칙이다.   
      따라서 테스트의 가치가 낮아 보통 테스트에서 제외한다.
   2) API 계약으로 중요한 경우   
      '음수 ID 요청시 반드시 400을 반드시 반환해야 한다'는 명시적 요구사항이 주어진다면,
      이는 테스트 대상으로 WebMvcTest에서 검증하는 것이 좋다.


### < 커스텀 Validation 테스트 - Enum 유효성 검증 테스트 >
1. 기존 예외 발생 코드
   JSON 문자열 방식으로 유효하지 않은 Enum값 전달.   
   String으로 JSON 전달 시 유효하지 않은 Enum 값을 전달했다.
   ~~~
   @Test
   @DisplayName("상품등록실패_유효하지않은 Enum 값 오류")
   public void failRegisterProduction_invalidEnum() throws Exception {
       // given
       String invalidRequestJson = """
             {
              "code": "validCode",
              "name": "정상 상품명",
              "category": "INVALID_ENUM_VALUE",
             }
             """;
       Member member = sellerMember();

       // when
       // then
       mockMvc.perform(post("/production")
                 .with(user(member))
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(invalidRequestJson))
             .andExpect(status().isBadRequest());
       verify(productionService, never()).registerProduction(any(), any());
   }
   ~~~      

2. 발생에러
   ~~~
   org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error: Cannot deserialize value of type `com.myecommerce.MyECommerce.type.ProductionCategoryType` from String "INVALID_ENUM_VALUE": not one of the values accepted for Enum class: [BABY_PRODUCTION, ETC, ELECTRONICS, WOMEN_CLOTHING, OUTDOOR, MANS_CLOTHING, FASHION_GOODS, BEAUTY]
   ~~~

3. 문제점  
   HttpMessageNotReadableException 오류가 발생했다.  
   해당 예외는 Jackson 역직렬화 단계에서 요청이 실패했을 때 발생하는 예외이다.
   즉, validation check 전에 JSON -> DTO로 변환하는 단계에서 실패했다는 이야기다.
   HTTP 요청 수신 후 Jackson이 JSON을 DTO로 역직렬화 해 객체 생성 후 DTO 검증을 수행한다.
   즉, DTO 검증 전에 객체 생성 과정에서 enum 값 매핑에서 실패해서 해당 예외가 발생했다.
   이는 커스텀 validation 테스트가 아닌, 컨트롤러 바인딩 테스트 실패 케이스가 된다.

4. 해결방법   
   Enum 타입 값으로 null을 전달하게 되면
   @EnumValid 검증이 아닌 @NotNull 검증처럼 보일까봐 json으로 유효하지 않은 Enum값을 전달했다.
   하지만 목적이 Controller + Validation 테스트이므로, 검증 전에 오류가 발생하는 위의 방법은 적절하지 않다.   
   해결 방법으로 검증 의도에 따라 2가지의 선택 사항이 있다.
   1) enum 값으로 null 사용   
      @EnumValid 검증을 보장.
      테스트의 목적이 Controller + Validation이면 null 값을 전달하는 것이 적합.
      ~~~
      ~~~
   2) enum 문자열 오류로 테스트 (기존 테스트)   
      API 완성도를 중시하는 경우 권장.
      API가 잘못된 enum 문자열에 대해 어떤 응답을 하는지에 목적이라면 의미있음.
      단, 발생하는 오류에 대해 400으로 변환하는 ExceptionHandler 작성이 필요하다.
      - Controller 단위 테스트의 필수 항목 아님.

   
### < PathVariable 검증 실패가 500이 되는 이유와 400으로 처리하는 방법 >
1. DTO 검증 실패
   ~~~
   @RequestBody @Valid DTO
   ~~~
   1) 발생 예외 : MethodArgumentNotValidException
   2) Spring MVC 기본 동작   
      자동으로 400 Bad Request 반환.

2. PathVariable / RequestParam 검증 실패
   ~~~
   @PathVariable @Positive Long id
   ~~~
   1) 발생 예외 : ConstraintViolationException
   2) 발생 시점 : Controller 메서드 호출 중 (Method Validation)
   3) Spring MVC 기본 동작   
      Spring MVC가 예외를 자동으로 400으로 바꿔주지 않음.   
      그래서 결과가 Unhandled Exception -> 500 Internal Server Error 발생.

3. PathVariable / RequestParam 검증 실패 시 400 예외 미발생 해결 방법
   - 해결방법 : 예외를 400으로 매핑해준다.
   1) ExceptionHandler 생성
      - @ExceptionHandler   
        응답 형태와 메시지를 통제하고 싶을 때 사용.

      @RestControllerAdvice 어노테이션을 사용해 ConstraintViolationException 예외에 대한 ExceptionHandler를 작성한다.
      그러면 @Positive, @NotNull 등 유효성 검증 실패 시 ConstraintViolationException 예외가 발생하면 400 Bad Request를 반환한다.
      이로써 DTO 검증과 동일하게 '클라이언트의 입력 오류 = 400' 의미를 유지할 수 있다.
      이는 실제 운영 환경에서도 필요한 처리이다.
      ~~~
      @RestControllerAdvice
      public class GlobalExceptionHandler {
          @ExceptionHandler(ConstraintViolationException.class)
          @ResponseStatus(HttpStatus.BAD_REQUEST)
          public Map<String, String> handleConstraintViolation(
                  ConstraintViolationException e) {

              return Map.of( 
                      "message", e.getMessage()
              );
          }
      }
      ~~~

   2) (테스트 적용 시) @WebMvcTest에 Advice 포함   
      @WebMvcTest는 Controller와 필요한 Bean만 로딩하기 떄문에
      ControllerAdvice를 명시적으로 포함해야 한다.
      ~~~
      @WebMvcTest(controllers = ProductionController.class)
      @Import(GlobalExceptionHandler.class)
      class ProductionControllerTest {
          // ...
      }
      ~~~
      또는
      ~~~
      @WebMvcTest(
          controllers = ProductionController.class,
          includeFilters = @ComponentScan.Filter(
              type = FilterType.ASSIGNABLE_TYPE,
              classes = GlobalExceptionHandler.class
          )
      )
      ~~~

3. 내 테스트의 문제점   
   아래의 테스트는 웹 계층 테스트로 Spring MVC와 Controller만 로딩하고 나머지 애플리케이션 자동 설정은 최대한 배제된다.
   따라서 기본 ExceptionResolver 체인이 “운영 환경과 동일하게” 구성되지 않을 수 있다.
   이 때 Spring MVC가 아닌 테스트 컨텍스트에 해당 예외를 HTTP 상태코드 400으로 처리할 주체가 없어
   예외가 처리되지 않고 그대로 던져지게 된다.

4. 문제 해결방법
   테스트를 의도대로 통과시키는 방법은 아래와 같다.
   1) 명시적으로 해당 Exception에 대해 400 매핑 추가하는 ExceptionHandler 작성.
   2) 작성한 ExceptionHandler.class를 import를 통해 테스트에 포함.
   > 그런데 위와 같은 Exception 정책은 결국 반복된다.   
   > Controller마다 동일한 테스트 케이스를 중복으로 계속 생성하지 않고, 별도의 테스트로 분리해 관리하는 것이 좋다.   
   > 애플리케이션 전역의 정책이기 때문에 Exception 전용 테스트 파일을 생성해
     ExceptionHandler 단위로 테스트를 생성하도록 한다.


---
## 14. 테스트코드 리팩토링
### < 상품등록성공 Controller 테스트코드 리팩토링 >
- 참조 : ProductionControllerTest.successRegisterProduction()

1. 의도   
   API 계약을 보여주어 '이 API는 이런 필드를 받고 이런 필드들을 그대로 응답으로 돌려준다.'는 내용을 표현하기 위해 
   상품등록 성공 테스트코드를 작성 시 모든 객체 생성 내용을 테스트 내에 포함했다.

2. 테스트에 모든 값을 넣는 방식의 문제점   
   결과적으로 기존 테스트의 의도는 명확하지만 중복, 노이즈, 책임 조화가 존재한다.
   1) 테스트의 실패 이유가 흐려짐.   
      json 매핑 문제인지, 서비스 반환 문제인지, DTO 변경 문제일지 즉시 알기 어렵다.
      이렇게 되면 게약 검증이 아닌 service 구현 복사가 될 뿐이다.
   2) 올바른 책임 분리되지 않음.  
      데이터 설명서의 역할은 API 계약 명세가 담당해야지 테스트가 담당할 부분이 아니다.
      API 문서 역할 자체를 테스트가 대신하려하면 본질이 흐려진다.
      테스트는 행위 보장, 문서는 계약 설명의 책임을 가진다.
      좋은 테스트는 설명서가 아닌 안전벨트이다.
      - API 계약 명세(API 문서 역할 수행) : Swagger / Spring REST Docs가 담당.
      - Controller 행위 검증 : Controller Test가 담당.
      - 필드 제약 설명 : Validation 테스트가 담당.
      - 예제 요청/응답 : 문서로 작성.

3. 테스트의 본질적인 책임    
   SELLER 권한을 가진 사용자가 유효한 상품 등록 요청을 보내면
   Controller는 Service 결과를 그대로 200 OK JSON으로 반환한다.

4. 테스트가 굳이 책임질 필요 없는 것   
   - 실제 상품 도메인 값의 의미
   - optionCode/price 같은 세부 데이터 의미
   - 사용되지 않는 값

5. 테스트코드 리팩토링 수행 내용
   1) 테스트에서 의미 없는 변수 제거   
   2) 테스트 행위와 무관한 값 축약   
      테스트는 도에인 샘플 생성기가 아님.   
   3) 최소한으로 충분한 검증 수행.   
      해당 API가 정상 동작한다는 신뢰를 할 수 있을 만큼, 최소한으로 충분한 검증을 수행해야 한다.   
      예를 들면 핵심 식별자, 인증 주체, 핵심 상태, 대표 필드 1~2개만 사용할 수 있도록 한다.
      해당 내용만으로도 DTO의 직렬화 Sservice 결과 반영, 응답 구조 등이 충분히 설명된다.
      모든 필드 값을 검증하는 것은 구현에 종속되는 것이며, 모든 입력 케이스를 나열하는 것은 Validation 테스트의 역할이다.
      API 문서를 대체하겠다는 마음으로 작성하는 것은 유지보수가 불가한 일임을 알아야 한다.
      - id, seller, code는 기본적인 핵심값으로 필수 검증.
      - saleStatus는 입력값이 아닌, 저장하면서 추가된 값으로 필수 검증.
      - 나머지는 핵심 검증이지만, category는 enum -> string 변환으로 JSON 직렬화 보장 측면에서 의미있기에 추가 검증.
   4) 테스트 데이터 생성 책임 분리.   
      가장 중요하게 생각한 리팩토링 포인트이다.
      데이터를 생성하는데 시선이 분산되어 테스트코드가 지저분한 느낌이다.
   5) Service 전달인자 검증 최소화   
      any() 사용해도 괜찮음. 굳이 DTO equals 필요 없음.
      Controller 테스트에서는 전달인자가 그대로 Service로 넘어가는지만 간접적으로 확인하면 충분
      ~~~
       given(productionService.registerProduction(
              any(RequestProductionDto.class), any(Member.class)))
              .willReturn(response);
      ~~~
   6) 의도한 객체에 대한 반환값 보장 (JSON Path 검증)   
      기존에는 any()를 통해 “무엇이 전달돼도 성공”하는 스텁.
      Controller 테스트에서 최소한 “요청 JSON → DTO 변환이
      의도한 객체로 이루어졌는지”를 한 번은 보장해주는 게 좋다.
      이를 위해 JSON Path 검증을 통해 간접 검증을 수행한다.
      응답 DTO 필드가 올바르게 매핑됐는지 확인해 Controller → Service → Response 변환까지 테스트 가능.
      ~~~
              mockMvc.perform(post("/production")
                        .with(user(member))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.seller").value(member.getId()))
                .andExpect(jsonPath("$.code").value(request.getCode()))
                .andExpect(jsonPath("$.category").value(WOMEN_CLOTHING.toString()))
                .andExpect(jsonPath("$.saleStatus").value(ON_SALE.toString()));

      ~~~
   7) ArgumentCaptor 제거   
       DTO 단순 전달만 하는 Controller라면 불필요.   
       유지보수 코드가 간결해지고 테스트가 가벼움.  

6. 테스트 픽스쳐(Test Fixture)   
   테스트가 반복적으로 의존하는 준비된 상태(데이터, 객체, 환경)
   즉, 테스트를 실행하기 위해 항상 필요한 재료들로 
   테스트용 객체, 테스트용 DTO, 테스트용 사용자, 테스트용 설정값 등을 
   테스트 본문이 아닌, 별도 메서드 구조로 분리한 것.
   1) 테스트 픽스쳐가 없는 테스트   
      테스트 본문이 단순 데이터 설명서가 된다.
   2) 테스트 픽스쳐가 있는 테스트
      테스트 본문은 행위와 의도만 표현한다.
      - 가독성 : 테스트 시나리오가 한 눈에 들어옴.
      - 재사용 : 여러 테스트에서 동일한 정상 요청 객체 사용가능.
      - 변경에 강함 : DTO 필드 추가/삭제 시 수정 지점 최소화
      - 의도 분리 : 데이터 준비와 검증할 행위를 분리 가능.
   
7. response 하드코딩
   ***테스트코드에서 response를 하드코딩 시 response가 정확히 어떤 값이어야 하는지 명확히 할 수 있다.***
   서버 로직이 변해도 기대값이 무엇인지 바로 확인하기 쉽고,
   테스트 실패 시 문제 지점을 빠르게 찾기도 쉬움.   
   따라서 ***테스트코드 작성 시에는 response 값을 하드코딩 하는 것이 테스트의 검증 목적에 적합***하다.
   단, Request와 Response 값이 같은 경우, 반복적이거나 불필요하게 보일 수 있고 동일 값 여부를 직관적으로 확인하기는 어렵다.
   따라서 단순히 CRUD 흐름을 테스트하는 경우에는 Request 기반의 값을 재사용 가능하다.
   1) 테스트 유지보수 우선 → request 기반 response 작성
   2) 테스트 의도 명확성 우선 → response 값 하드코딩


### < ValidationMessages.properties 검증 테스트 설계 >
Validation 메시지를 키로 관리하는 설계를 제대로 했는지 검증하는 방법.
테스트의 목적은 Validation 결과로 “최종 메시지 문구가 기대값인지”를 확인하는 것이다.
메시지 키를 직접 관리하는 경우, 필요한 테스트이다.

1. 테스트의 목적   
   Validation 메시지 키가 정상 로딩되는지 검증하는 것이 목적이다.
   - {validation...}로 작성된 키가 실제 메시지로 치환되는가
   - properties 설정이 깨지지 않았는가
   - 인코딩/MessageSource 설정 이상이 없는가

2. 적절한 테스트 환경   
   설정 + 프레임워크 통합 테스트이므로 Spring Boot 환경에서 하는 것이 옳다.
   ~~~
   @SpringBootTest
   public class MessageSourceTest {
       @Autowired
       MessageSource messageSource;

       @Test
       @DisplayName("messages.properties파일에 대해 메시지 key로 value에 접근 성공")
       public void message_key_is_resolved() {
           // given
           // when
           String message = messageSource.getMessage(
                      "키값",
                      new Object[]{1, 100},
                      Locale.KOREA
           );

           // then
           Assertions.assertTrue(message.contains("상품코드는 최소 "));
       }
   } 
   ~~~

3. 테스트 코드 작성 후 발생에러
   ~~~
   No message found under code 'validation.product.code.size' for locale 'ko_KR'.
   org.springframework.context.NoSuchMessageException: No message found under code 'validation.product.code.size' for locale 'ko_KR'.
   ~~~

4. 발생원인   
   MessageSource가 해당 properties 파일을 찾지 못해 발생한 에러이다.
   Spring 기본 탐색 파일은 messages.properties이니 당연하다.   
   나는 Bean Validation 메시지 탐색 시 스펙상 기본 메시지 파일인
   ValidationMessages.properties에서 메시지 탐색이 우선이라는 생각을 했다
   하지만 이는 Validator가 직접 메시지를 해석할 때 해당하는 이야기이다.   
   테스트코드에서는 MessageSource를 통해 메시지를 호출했으므로 해당하지 않는 이야기다.

5. 해결방법
   역할분리가 필요하다.
   Bean Validation 동작 테스트는 ControllerTest에서 수행하도록 한다.   
   따라서 해당 키에 대한 value를 잘 찾는지에 대해서는 validation check를 수행하는 controller 테스트에서 함께 수행한다.
   이는 response에서 message로 전달된 값을 확인해 검증 가능하다.
   ~~~
        mockMvc.perform(post("/production")
                        .with(user(seller()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage") // 에러메시지 검증
                        .value("유효하지않은 값입니다.\n상품코드는 영문자, 숫자만 사용 가능합니다.\n특수문자는 -만 허용하며 첫 글자로 사용 불가합니다."));
   ~~~


## 15. Exception 테스트코드
### < Validation, 비즈니스 예외 메시지 테스트 >
1. Validation 메시지    
   대부분 컨트롤러 테스트에서 MockMvc로 검증.
   Validation 에러는 대표 케이스 몇 개만 작성해 테스트.

2. 비즈니스 예외 메시지    
   프로젝트, 조직 정책에 따라 다름
   1) 단순히 메시지를 Key로 가져오는 경우   
      메시지가 올바르게 내려가는지만 확인하면 되므로 통합(MockMvc) 테스트만으로 충분하다.
   2) 메시지 생성 로직이 조합되거나 포맷팅, Locale 처리, 커스텀 파라미터 포함하는 경우   
      통합(MockMvc) 테스트 외에도 ExceptionHandler 단위 테스트를 별도로 작성해 검증하는 경우가 많음.
   3) 비즈니스 에러는 대표 케이스 1개만 작성해 테스트 수행   
      모든 비즈니스 에러는 같은 ExceptionHandler, 같은 응답 포맷, 같은 MessageSource 로직을 수행하므로,
      한 번만 검증해도 구조 전체가 검증되기 떄문이다. 

3. 테스트 작성 방법
   1) 기존 컨트롤러 테스트에 포함 (선택한 방법)    
      컨트롤러 테스트에서 비즈니스 예외를 발생시키고, 응답 JSON 메시지 검증 추가해 테스트 수행.   
      테스트 케이스가 컨트롤러부터 핸들러까지의 흐름을 그대로 검증.   
      단, ExceptionHandler 단위 로직만 독립적으로 테스트하지 않음.

   2) ExceptionHandler 단위 테스트   
      @WebMvcTest 없이 핸들러 직접 호출.   
      핸들러 단독 검증 가능하고, MockMvc 없이 빠르게 테스트 가능.   
      단, 컨트롤러 흐름까지 검증하지 않음.   
      ~~~
      @Test
      void businessExceptionHandler_returnsMessageFromMessageSource() {
      // given
      BusinessException ex = new BusinessException("error.product.status.invalid");
     
          // when
          ResponseEntity<CommonErrorResponse> response =
                  commonExceptionHandler.commonHandler(ex);
       
          // then
          assertEquals("상품 상태가 올바르지 않습니다.", response.getBody().getErrorMessage());
          assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      }      
      ~~~
   

## 16. 읽기 쉬운 테스트와 신뢰 가능한 단위 테스트 작성 원칙
읽기 쉬운 테스트, 실패 원인이 바로 보이는 테스트가 좋은 테스트이다.
1. 데이터 객체 생성    
   정상 케이스 + 예외 케이스 모두 데이터 가시성을 최우선으로 하고, 반복이 많거나 길어지는 부분만 Fixture 메서드로 분리하면 이상적.
   1) 정상 케이스   
      예외가 발생하지 않는지 확인하므로, Fixture 메서드를 쓰지 않고 간단하게 객체를 생성.
      단, 공통 로직이 길거나 반복이 많을 때, 여러 테스트에서 재사용 필요하므로 Fixture 자체는 유지.
      기본적으로 이 정도면 통과한다”는 의미만 전달하면 됨.
   2) 예외 테스트   
      Fixture 메서드를 쓰더라도 예외 원인을 테스트 안에서 바로 작성하면 가시성이 높아짐.
      보편적으로 실패 원인이 있는 데이터만 테스트 안에서 직접 쓴다.
      실패 원인이 숨겨지는 픽스처는 지양해야 한다. 실패 조건은 테스트에서 직접 조립을 권장한다.
2. 전달인자 검증
   1) any()   
      Mock 설정이 간단해진다.
      하지만, 어떤 코드로 조회했는지 알 수 없기 때문에, 실제 로직에서 잘못된 값(옵션코드)가 넘어가도 테스트는 통과되는 문제가 발생한다.   
      따라서 mock의 정상 동작 여부를 의심하게 되어 실제 의도대로 동작하느지 확실하지 않아 테스트 신뢰성이 저하된다.   
      특히 DB 조회나 외부 연동이 필요한 경우는 잘못된 값으로 호출해도 테스트가 통과할 가능성이 높다.
      즉, 테스트가 통과한다는 것이 실제 로직이 정상임을 보장하지 못하게 되는 것이다.    
      단순 정상 케이스에서 에외가 발생하지 않으면 성공임을 확인할 경우, 값 검증이 필요 없으므로 any()로 충분하다.
      하지만 예외 케이스 테스트나 검증이 핵심인 테스트에서는 정확한 값 확인이 필수이다.
   2) 정확한 값 전달 + ArgumentCaptor   
      실제로 어떤 값으로 호출됐는지 확인 가능하다.
      따라서 예외 발생 원인이나 로직이 정확히 테스트됨을 확인할 수 있다.
      추후 로직이 변경되어도, 전달인자가 정확히 전달되는지 검증 가능해 리팩토링의 안정성 확보가 가능하다.
3. 단위 테스트에서 불필요한 PK값 제거   
   단위 테스트에서는 테스트 대상 로직이 실제로 사용하는 값만 세팅하면 된다.
   id(pk)가 정책 판단에 사용되지 않는다면 굳이 넣지 않는 것이 더 좋은 테스트다.
   불필요한 필드를 넣으면 테스트 의도가 흐려지고 리팩토링 저항만 커진다.
   id가 필요한 경우는 식별·비교·상태 전이 로직을 검증할 때뿐이다.


--- 
## 17. verify()를 이용한 메서드 호출 검증
1. Redis 만료기간 설정 메서드 호출 검증    
   Redis 만료기간 설정 메서드 검증은 Hash 저장 이후, 올바른 Redis Key(namespace:userId)에 TTL을 설정한다는 계약을 고정한다.   
   eq() 같은 전달인자 검증을 통해 정확한 키를, times(), never()를 통해 정확한 시점에 항상 실행을 보장한다.   
   단위 테스트의 책임은 “우리 코드가 Redis에 올바른 명령을 보냈는가”이므로 경계선을 정확히 지키게 해준다.   
   ~~~
        // redis 만료 기간 갱신 검증
        verify(redisSingleDataService, times(1))
                .setExpire(eq(CART), eq(redisKey), eq(Duration.ofDays(EXPIRATION_PERIOD)));
   ~~~


--- 
## 18. 단위 테스트 작성 가치
1. 단위 테스트 작성 가치가 없는 경우   
   비즈니스 판단, 조건 분기, 계산 로직, 정책이 없는 경우, 단위 테스트의 가치가 낮다.
   테스트가 결국 Mock 검증 밖에 안되기 때문이다.
   이는 로직 검증이 아니라 구현 검증에 가깝다.

2. 단위 테스트 작성 가치가 있는 경우   
   - 정책이 포함될 때
   - 판단/계산이 수행될 때
   - Redis Key 생성 규칙이 중요할 때   
     여러 곳에서 쓰이고 포맷이 바뀌면 장애가 나거나 외부 시스템과 약속된 규칙인 경우라면 Key 생성 로직만 빼서 테스트 권장.
   - 실수하면 장애 나는 코드일 때
   - 

1. 재고 등록 메서드
   조회해 그대로 Redis에 데이터를 저장하는 경우이다.
   아래 코드의 경우 productOptionRepository.findByProductId() 가 호출되었는지, 몇 번 호출 되었는지, key/value가 맞는지 구현에 대한 검증밖에 수행할 게 없다.
   그리고 실패해도 얻는 신뢰도가 낮다.
   테스트가 통과해도 Redis에 실제도 잘 들어가는지, 직렬화 문제는 없는지 등 확인이 불가하므로 결국 통합테스트에서만 의미가 있다. 
   ~~~
    public void saveProductStock(Product product) {
        // 옵션목록조회
        List<ProductOption> options =
                productOptionRepository.findByProductId(product.getId());

        // 상품옵션 재고등록
        options.forEach(option -> {
            redisSingleDataService.saveSingleData(
                    STOCK,
                    createStockRedisKey(product.getCode(), option.getOptionCode()),
                    option.getQuantity());
        });
    }
   ~~~


---
## 19. 모든 것을 검증하는 정상 시나리오의 문제점
> < 테스트 작성 시 느낀 문제점 >    
> 테스트가 간단했을 때는 테스트 가시성에 문제가 없었는데, 
  테스트가 복잡해지니 아래의 문제에 직면하게 되었다.
> 작성하면서도 테스트가 안 읽힌다는 느낌을 받았고, 
  그럼에도 정상 시나리오니 모든 내용을 검증해 정상동작하고 있음을 명확히해야 한다고 생각했다.
> 그럼에도 픽스쳐 메서드와 헬퍼 메서드를 생성해 나름 깔끔하게 만들고 있다고 생각했다.
> 하지만 그렇지 않았다.   
> 
> 목표 : 대표 정상 시나리오는 흐름 중심으로 검증하고, 세부 로직은 역할별 단위 테스트로 분리.

1. 모든 것을 검증하는 정상 시나리오     
   ~~~
    @Test
    @DisplayName("상품수정 성공 - 상품 판매중 유지 시 상품 및 옵션 수정,등록 후 재고 등록")
    void modifyProduct_shouldUpdateProductAndCacheStock_whenProductOnSale() {
        // given
        // 요청 상품옵션 DTO 목록
        RequestModifyProductOptionDto requestUpdateOption = requestUpdateOption();
        RequestModifyProductOptionDto requestInsertOption = requestInsertOption();
        List<RequestModifyProductOptionDto> requestOptions = new ArrayList<>();
        requestOptions.add(requestUpdateOption);
        requestOptions.add(requestInsertOption);
        // 요청 상품 DTO
        RequestModifyProductDto requestProduct =
                RequestModifyProductDto.builder()
                        .id(5L)
                        .description("수정한 상품 설명입니다.")
                        .saleStatus(ON_SALE)
                        .options(requestOptions)
                        .build();
        // 요청 회원 DTO
        Member member = seller();

        Product targetProductEntity = onSaleProductEntity();

        ServiceProductDto serviceProductDto =
                serviceProductDto(ON_SALE, requestOptions);
        ServiceProductOptionDto insertOptionDto =
                filterOption(serviceProductDto, null);
        ServiceProductOptionDto updateOptionDto =
                filterOption(serviceProductDto, 1L);

        // 업데이트한 상품 결과 DTO
        ResponseProductDto expectedResponseProduct =
                ResponseProductDto.builder()
                        .id(5L)
                        .description("수정한 상품 설명입니다.")
                        .saleStatus(ON_SALE)
                        .build();

        // requestDto -> ServiceDto 변환
        given(serviceProductMapper.toServiceDto(eq(requestProduct)))
                .willReturn(serviceProductDto);
        // 요청한 셀러 상품 단건 조회 (반환 결과는 dirty checking 대상)
        given(productRepository.findByIdAndSeller(
                eq(requestProduct.getId()), eq(member.getId())))
                .willReturn(Optional.of(targetProductEntity));
        // 수정, 신규 옵션 DTO -> Entity로 변환 (옵션값 변경 직전)
        given(serviceProductMapper.toOptionEntity(eq(updateOptionDto)))
                .willReturn(updateProductOptionEntity());
        given(serviceProductMapper.toOptionEntity(eq(insertOptionDto)))
                .willReturn(insertProductOptionEntity());
        // Entity -> response DTO로 변환 (더티 체킹이므로 변환 전 변경값 검증)
        ArgumentCaptor<Product> productCaptor =
                ArgumentCaptor.forClass(Product.class);
        given(serviceProductMapper.toDto(productCaptor.capture()))
                .willReturn(expectedResponseProduct);

        // when
        ResponseProductDto responseProduct =
                productService.modifyProduct(requestProduct, member);

        // then
        // 정책 검증 여부 검증
        verify(productionPolicy, times(1))
                .validateModify(eq(targetProductEntity), eq(List.of(insertOptionDto)));
        // 상품 재고 등록 여부 검증
        verify(stockCacheService, times(1))
                .saveProductStock(eq(targetProductEntity));
        // 상품 재고 삭제 여부 검증
        verify(stockCacheService, never()).deleteProductStock(any());

        // 0. toDto에 전달된 인자 캡처 후 검증 (변경값만 검증)
        // 상품 판매상태, 설명 / 신규, 수정 옵션 수량 검증
        Product capturedProduct = productCaptor.getValue();
        ProductOption updatedOption = filterOption(capturedProduct, 1L);
        ProductOption insertedOption = filterOption(capturedProduct, null);
        assertEquals(requestProduct.getSaleStatus(), capturedProduct.getSaleStatus());
        assertEquals(requestProduct.getDescription(), capturedProduct.getDescription());
        assertEquals(10, updatedOption.getQuantity());
        assertEquals(20, insertedOption.getQuantity());
        // 1. 상품 수정 검증
        assertEquals(requestProduct.getId(), responseProduct.getId());
        assertEquals(requestProduct.getDescription(), responseProduct.getDescription());
        assertEquals(requestProduct.getSaleStatus(), responseProduct.getSaleStatus());
        // 2. 상품옵션 수정 검증
        ProductOption responseUpdatedOption = filterOption(capturedProduct, 1L);
        ProductOption responseInsertedOption = filterOption(capturedProduct, null);
        assertEquals(requestUpdateOption.getId(), responseUpdatedOption.getId());
        assertEquals(requestUpdateOption.getOptionCode(), responseUpdatedOption.getOptionCode());
        assertEquals(requestUpdateOption.getQuantity(), responseUpdatedOption.getQuantity());
        // 3. 상품옵션 신규등록 검증 (JPA 더티체킹으로, 신규 생성되어야하는 아이디는 미검증)
        assertEquals(requestInsertOption.getOptionCode(), responseInsertedOption.getOptionCode());
        assertEquals(requestInsertOption.getQuantity(), responseInsertedOption.getQuantity());
    }
   ~~~

2. 위 테스트의 문제점과 해결방법      
   비즈니스 이해도는 높고 실무 코드 보호력은 강하지만, 좋은 테스트가 아닌 무거운 테스트이다.
   정확하지만 읽기 힘든 테스트에 가깝고, 몇 가지는 Mockito/JPA 관점에서 위험한 냄새도 있다.
   1) 과도한 책임   
      테스트가 한 번에 너무 많은 것을 검증한다.
      위 테스트는 아래의 총 7가지를 동시에 검증하고 있다.
      이는 사실상 ***modifyProduct 메서드 전체를 재구현한 수준***이다. 
      이는, 실매 시 어디가 깨졌느지 파악하기 어렵게 하고, 
      내부 구현 변경 시 테스트 대량 파손으로 리팩토링에 매우 취약하며, 
      테스트가 행귀 검증이 아닌 구현 검증이 되게 한다.    
      시나리오를 ***단위 테스트로 쪼개는 개선이 필요***하다.
      하나의 테스트는 핵심 행귀 12개, 부수 검증 1~2개 정도가 읽기 좋다.
      - 요청 DTO -> Service DTO 변환
      - 상품 조회 및 dirty checking 동작
      - 옵션 수정 / 옵션 신규 등록 로직
      - 정책 검증 호출 여부
      - 재고 캐시 save / delete 분기
      - Entity 변경값
      - Response DTO 값
   2) filterOption(..., null) 패턴 위험    
      id가 null인 옵션을 신규 옵션으로 간주하는 메서드인데, 이로 인해 테스트 전체가 'null==신규'라는 내부 규칙에 강하게 의존하게 된다.   
      문제점은, 옵션이 1개 이상 신규이거나 id 생성전략이 변경되면 전체 코드 수정이 필요하고, 
      의미가 코드에 드러나지도 않아 가독성이 최악이다.   
      getInsertOption() 같이 의도를 드러내는 헬퍼 메서드로 분리하거나 fixture 단계에서 명시적으로 분리해야 한다.
   3) ArgumentCaptor 사용 위치가 너무 늦음   
      ~~~
      given(serviceProductMapper.toDto(productCaptor.capture()))
      ~~~
      toDto 호출을 전제로 내부 상태를 검증하기 때문에 toDto 호출이 제거되면 테스트 의미도 사라진다.   
      mapper 호출 방식은 비즈니스 핵심이 아니다. 따라서 내부 구현이 변경되면 테스트가 불필요하게 깨질 수 있다.
      예를 들면 아래와 같이 ***행위 기준으로 캡쳐해야한다.***
      '재고에 저장된 상품 상태'라는 비즈니스 의미가 생긴다.
      ~~~
      ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
      verify(stockCacheService).saveProductStock(captor.capture());
      Product savedProduct = captor.getValue();
      ~~~
   4) Given 섹션이 너무 길고 핵심이 안보임   
      현재 Given 블록이 70줄 이상이고, 중간에 흐름이 끊긴다.   
      이는 테스트에서 중료한 입력이 무엇인지 확인하기 어려운 등 
      테스트 시나리오가 한눈에 안 들어오게 하는 문제점을 발생시킨다.   
      Given을 입력, 기존상태, Mock 동작 3단 구조로 정리가 필요하다. 테스트는 이야기여야하기 때문이다.
   5) 정책 검증 대상이 테스트 내부 구현을 너무 잘 알고 있음.    
      아래의 코드를 보면, 정책 메서드에 insert 옵션만 전달된다는 구현 세부사항에 의존한다.
      이는 실제 중요한 건 신규 옵션이 존재한다는 사실인데, 
      정책 파라미터 구조가 바뀌면 테스트 전부를 수정해야하는 문제를 발생시킨다.   
      캡쳐를 사용해 이 문제를 해결할 수 있다.
      ~~~
      // 문제코드
      verify(productionPolicy)
             .validateModify(eq(targetProductEntity), eq(List.of(insertOptionDto)));
      ~~~
      ~~~
      // 해결방법
      ArgumentCaptor<List<ServiceProductOptionDto>> captor = ArgumentCaptor.forClass(List.class);
      verify(productionPolicy).validateModify(eq(targetProductEntity), captor.capture());
      assertTrue(captor.getValue().stream().anyMatch(o -> o.getId() == null));
      ~~~
   6) assert가 너무 많고 중복됨.   
      같은 값이 Entity, Response 양쪽에서 반복 검증되고 있다.   
      이는 실패 시 메시지 노이즈를 증가시키며 테스트 의도를 흐린다는 문제점을 가지고 있다.   
      > 역할별 검증을 분리가 필요하다. (근데 어떻게?)
   7) 테스트 이름이 너무 많은 의미를 담고 있음.   
      '판매중 상품 수정 시 재고가 다시 등록된다'와 같이 디테일은 다른 테스트에서 책임지게 하는 것이 좋다.

3. 정상 시나리오 테스트의 책임 범위    
   정상 시나리오가 길어지는 건 자연스럽지만, 정상 시나리오가 모든 것을 검증하는 것은 아니다.
   현재는 정상 시나리오의 모든 세부 구현을 한 테스트에 몰아 넣은 구조이다.
   즉, 정상 시나리오 테스트, 단위 테스트, 내부 구현 검증이 섞여 있는 것이다.
   실무에서 중요한 메인 플로우를 다루고 있는데, 대표 시나리오 테스트 하나 정도는 무겁지 않아도 괜찮다.
   ***정상 시나리오는 핵심 결과만 책임진다.***
   여기서는 판매중인 상품을 수정하면, 수정/신규 옵션을 반영하고 재고를 다시 등록한다는 것으로,
   핵심 검증은 재고가 저장 되었는가, 옵션이 수정/신규 상태가 되었는가, 결과가 성공 응답인가 정도의 핵심 검증 3개 정도면 충분하다.   
  
4. 가벼운 정상 시나리오 테스트 생성   
   테스트의 정체성은 판매중인 상품을 수정하면, 옵션 변경이 반영되고 재고가 다시 등록된다는 것이다.   
   따라서 내부 구현, mapper 호출 방식, 정책 인자 디테일은 제거하고, 흐름과 핵심 결과만 검증한다.   
   전달인자를 검증하지 않고, Response의 상세 검증을 제거하고, 정책 인자 검증을 수행하지 않는다. 
   결과적으로 상품이 어떻게 변경되어쏙, 재고가 저장되었다는 것만 확인한다.
   여기까지가 대표 정상 시나리오 테스트의 적정 무게이다.

5. 세부 동작 테스트로 분해 
   기존 무거운 테스트에서 가벼운 정상 시나리오로 변경하기 위해 덜어낸 것들을 각자 책임지는 테스트들이다.
   
6. 권장 테스트코드에 대한 의문점
   1) PK 값 유지 확인을 위한 방어적 검증   
      대부분 서비스 테스트에서는 불필요하다.
      단, JPA가 아닌 경우, 직접 재생성 가능성이 있는 로직이라면 필요하다.
      만약, findById()를 호출해 값을 반환받고 이를 response로 사용한다면, 
      PK는 이미 식별자 불변성을 가진 객체로, 서비스 로직에서 setId()를 수행하지 않는 한 바뀔 수 없기 때문이다. 
      위 수정 서비스에서 PK가 유지된다는 건, 비즈니스 규칙이 아니라 영속성 모델의 기본 계약이므로 보통 검증하지 않는다.
      
7. ***최종 변경사항 및 개선점***
   1) 전달인자(ArgumentCaptor) 기반 내부 구현 검증을 제거하고, 서비스 실행 결과와 도메인 상태 변화 중심으로 테스트를 단순화했다.
   2) 정상 시나리오 테스트에서 PK 유지 여부 같은 방어적 검증을 제거하고, 실제 변경 가능한 값(설명, 판매상태, 수량)만 검증하도록 개선했다.
   3) 정책(ProductPolicy)은 내부 로직 검증(전달인자를 명확히해 검증) 대신 호출 여부만 확인해 서비스 테스트와 정책 테스트의 책임을 분리했다.
   4) mapper 호출 결과에 의존하던 테스트 구조를 제거해, 리팩토링 시 깨지기 쉬운 결합도를 낮췄다.
   5) 반환 DTO에 포함되지 않는 옵션은 도메인 엔티티(Product)의 상태를 직접 검증하도록 테스트 관점을 명확히 했다.
   6) 판매 상태에 따른 재고 캐시 처리(save/delete)를 명확히 구분해 상태 전이에 따른 부수 효과 검증을 분리했다.
   7) 테스트 Fixture와 Helper 메서드를 정리해 테스트 의도를 빠르게 파악할 수 있도록 가독성을 개선했다.
   8) 정상 시나리오 테스트를 “결과 검증” 중심으로 재정의하고, 세부 동작 및 전달 인자 검증은 별도 단위 테스트로 분리 가능한 구조로 개선했다.


---
## 20. 정상 시나리오 테스트에서 given 중복은 제거 대상이 아니라 설계 정보
테스트를 처음 읽는 사람에게 아래 코드는 단순한 반복이 아닌, 정보이다.
서비스 설계 전제를 드러내고 있는 것이다.
- 수정 로직은 Service DTO를 기준으로 동작한다.
- 수정 대상은 셀러 소유 상품만 조회한다.
- 더티체킹 대상 엔티티를 직접 수정한다.
~~~
// requestDto -> ServiceDto 변환
given(serviceProductMapper.toServiceDto(requestProduct))
        .willReturn(serviceProductDto);

// 요청한 셀러 상품 단건 조회
given(productRepository.findByIdAndSeller(...))
        .willReturn(Optional.of(targetProduct));

// 옵션 DTO -> Entity 변환
given(serviceProductMapper.toOptionEntity(...))
        .willReturn(...);
~~~
위 given() 절 구조는 수정 시나리오의 공통 흐름을 설명하는 역할을 한다.
이는 정상 시나리오 내에서 반복적으로 작성하게 되더라도 테스트 내 유지하는 게 맞다.
두 정상 시나리오가 거의 동일해도 괜찮다. given이 거의 같은 것은 오히려 좋은 신호다.
같은 흐름에 상태값 하나만 달라진다는 것을 정확히 보여줄 수 있기 때문이다.


---
## 21. 분기 정상 시나리오를 추가하지 않아도 되는 경우
테스트의 목적은 ‘코드가 이렇게 동작한다’를 나열하는 것이 아니라 코드가 무엇을 보장하고 무엇을 금지하는지 명확히 드러내는 것이다.   
이 정상 시나리오는 이미 다른 정상 시나리오에서 수정 성공 흐름을 충분히 설명하고 있어, 유스케이스 관점엥서 새로운 정보를 주지 못하는 ‘정보 중복’이기 때문에 추가하지 않는 것이 좋다.   
상태만 다른데 성공 흐름, 책임, 결과가 동일하면 정상 시나리오 테스트로서 정보가 늘지 않기 때문이다.   
또한 이 정상 시나리오의 경우, 판매 상태만 변경하고 상품,옵션 정보를 수정하지 않고, 재고를 삭제하는 흐름이다.   
이 때 로직을 실행하지 않고 그대로 통과하는 정상 흐름은 관찰 가능한 동작 변화가 없으므로 테스트로 작성해도 새로운 의미를 만들지 못한다.
실행 결과나 부수 효과가 없는 정상 경로는 테스트 가치가 낮다.   
또한 재고를 삭제하는 흐름도 이미 다른 성공 시나리오에서 흐름을 보여주고 있다.   
그래서 분기만 다르고 실행되는 로직, 호출되는 협력자, 결과가 동일하거나 없으면 안 써도 되는 정상 시나리오로 보는 것이 합리적이다.


---
## 22. 조회 단위 테스트와 통합 테스트
### < 장바구니 조회 통합테스트 가치 >
- 참고: CartService.retrieveCart()

1. 단위 테스트가 가치 있는 경우   
   1) 분기 많음 (if, 정책 케이스 다수)
   2) 계산 로직 복잡
   3) 실패 케이스 중요
   4) 외부 의존성 거의 없는 순수 로직

2. 단위 테스트로 신뢰를 만들기 어려운 로직    
   retrieveCart()는 단순 조회가 아니다. 아래의 내용을 모두 수행한다.   
   순수 로직이 거의 없고 Redis, ObjectMapper, Policy 등 외부 의존성 투성이 이므로 테스트 시 전무 mocking이 필요하다.
   만약 단위 테스트로 만든다면, 내가 만든 가짜 세계에서만 통과하는 테스트를 생성하게 된다.   
   따라서 단위 테스트로는 신뢰를 만들기 어려운 로직이므로, 통합 테스트가 필요한 시나리오이다.   
   - Redis Hash 조회
   - key-value 순서 보장 로직
   - Redis MGET 기반 재고 조회
   - ObjectMapper 변환
   - 재고 -> 품절여부/구매가능수량 적용
   1) 그럼에도 단위 테스트가 의미 있는 대상   
      1. 정책    
         순수 로직이거나 실패가 중요하므로 단위 테스트 필수이다.
         현재 조회 로직에는 정책 로직은 없으므로 패스한다.
      2. 재고 매핑 로직   
         재고가 null, 0, 양수이냐에 따라 품절 여부, 구매 가능 수량이 잘 셋팅되는지 검증한다.   
         값 기반의 로직으로 단위 테스트 가치가 있다.

3. retrieveCart()에서 통합 테스트의 목적    
   통합 테스트의 목적은 Redis 성능 벤치마크, TPS 측정, ms 단위 응답 검증이 아니다.
   1) 100건 저장 정상 수행
   2) 조회
      - 누락없이 100건 조회
      - 각 item이 올바른 재고를 가짐.
      - 품절 여부 계산이 정확함.
   3) key-item 순서 의존 로직 깨지지 않음.

4. 통합 테스트 시나리오     
   - 시나리오: 100건 저장 -> 조회 -> 결과 검증
   - 단순 기능 테스트가 아닌, 설계가 실제 사용 패턴을 버틸 수 있는지 확인하는 테스트.
   1) 100건 처리
      - 장바구니 최대 상품 수로 합리적인 숫자.
      - MGET, 순서 의존 로직 검증 가능.
      - 성능 병목이 있으면 바로 체감됨.

5. 테스트가 검증하려는 리스크
   - MGET 결과 순서가 키 순서와 일치하는가
   - Hash -> keyList -> stockList 매핑이 깨지지 않는가
   - Redis 직렬화 / 역직렬화 문제
   - 다건 조회 성능 및 안정성


---
## 23. Redis 통합 테스트: Embedded Redis vs Testcontainers Redis
테스트를 어느 수준까지 현실에 가깝게 가져갈 것인가?

### < 테스트에서 Redis 결정 >
1. 사용할 Redis 결정   
   통합 테스트에서 CartService.retrieveCart() 동작 검증을 목표로 했다. 
   주요 검증 포인트는 MGET을 통한 다건(100건) 조회, 결과 순서 의존, Hash->DTO 변환, 품절 판단 로직 등이다.
   초기에는 운영 환경과 최대한 동일한 환경을 만들고자 초기에 Testcontainers Redis 사용을 검토했다.
   하지만 Embedded Redis로도 충분하다고 판단해 최종적으로 선택했다.

2. 판단 근거 및 최종 선택    
   CartService.retrieveCart() 테스트의 핵심 검증은 대부분 애플리케이션 레벨 로직 (순서 보장, DTO 매핑, 품절 판단)
   Redis 엔진 차이와 무관한 부분이 90% 이상
   Docker 학습 및 설치 비용 대비 실익이 낮음

3. 결론    
   Embedded Redis로 충분히 통합 테스트 목적 달성 가능
   Docker 설치/학습 비용 없이 통합 테스트 구현 및 검증에 집중
   Testcontainers Redis는 Redis 엔진 기능 검증이 필요한 경우에만 사용

### < Embedded Redis - 테스트용 >
테스트 JVM 안에서 Redis 서버를 띄우는 방식이다.
로컬 포트에서 Redis 프로세스를 직접 실행한다.
빠르고 설정이 간단하며 Docker가 필요없다.
1. 장점   
   따라서 빠르고 설정이 간단하다.
2. 단점   
   Windows 환경에서 자주 깨지고, Redis 최신 기능은 지원하지 않는 등 치명적인 단점이 있다.
   따라서 테스트 동작은 하지만 ***운영 환경과 일부 동작 차이가 존재해 믿기 어려운 쪽에 가깝다.***
3. 적합성    
   Hash, MGET, 순서, DTO 변환 등 통합 테스트 목적 달성 가능.
   빠른 피드백이 필요한 단순 통합 테스트나 CI 외 테스트에 권장.

### < Testcontainers Redis - 진짜 Redis >
Docker 컨테이너로 진짜 Redis를 띄운다.
운영 Redis와 거의 동일한 환경을 가진다.
1. 장점    
   느리지만 현실적으로 CI 환경에서도 안정적이다.
2. 단점   
   ***Docker 설치 및 학습 필요***   
   테스트 속도 느림
3. 적합성   
   MGET 결과 순서 보장, TTL 등 Redis 엔진 특성 검증 필요 시 적합.
   단순 애플리케이션 로직 검증에는 과함.

4. 테스트를 위한 의존성 추가
   - junit-jupiter: 테스트 작성
   - junit-platform-launcher: 테스트 실행
   - testcontainers:junit-jupiter: 컨테이너 생명주기 제어
   1) org.testcontainers:junit-jupiter   
      Testcontainers 전용 JUnit 확장 라이브러리로 JUnit5와 Testcontainters를 연결해주는 브리지.
      @Testcontainers, @Container. 테스트 생명주기와 컨테이너 자동 연동을 가능하게 한다.
      만약 의존성을 추가하지 않는다면, 컨테이너가 뜨지 않게 된다.
   2) org.junit.platform:junit-platform-launcher   
      기존 의존성 추가 되어있던 라이브러리이다.   
      JUnit 테스트를 실행해주는 런처로, IDE/Gradle/Maven이 테스트를 돌릴 때 필요하다.
      Testcontainers랑 직접적인 연관은 없고, 보통 Spring Boot가 자동으로 포함한다.
   ~~~
   Testcontainers 의존성 설정 (단, 도커 필수)
   testImplementation "org.testcontainers:testcontainers"
   testImplementation 'org.testcontainers:junit-jupiter'
   ~~~

5. 주요 어노테이션 
   1) @ActiveProfiles("test")   
      - application-test.yml 사용
      - Redis/DB/로그 레벨을 테스트 전용으로 분리
      - 운영 설정과 테스트 설정 충돌 방지
      - 통합 테스트에서는 거의 필수적
   2) @Testcontainers   
      해당 클래스에서 Testcontainers를 사용함을 선언해 
      JUnit이 테스트에서 컨테이너 생명주기를 관리하도록 함.
   3) @Container   
      해당 필드가 컨테이너임을 선언.
      테스트 시작 시 컨테이너를 시작하고, 종료 시 컨테이너를 멈춘다.
      static으로 선언해 테스트 클래스 전체에서 한 번만 실행하게 하고 성능을 최적화한다.
   4) @DynamicPropertySource   
      Redis 컨테이너는 랜덤 포트로 뜬다.
      컨테이너 실행 후 알 수 있는 값을 Spring Environment에 동적으로 주입한다.
      예를 들면 Redis 포트, Redis Host를 설정(주입)한다.
      위 데이터가 없으면 Spring이 Redis를 찾지 못한다.    
   ~~~
        import org.springframework.boot.test.context.SpringBootTest;
      import org.springframework.test.context.DynamicPropertyRegistry;
      import org.springframework.test.context.DynamicPropertySource;
      import org.testcontainers.containers.GenericContainer;
      import org.testcontainers.junit.jupiter.Container;
      import org.testcontainers.junit.jupiter.Testcontainers;
   
      /** 테스트용 Redis 서버 생성 **/
      @Testcontainers
      @SpringBootTest
      public class RedisTestSupport {
   
          // 테스트 시작 시 Redis 자동 실행
          @Container
          static GenericContainer<?> redisContainer =
                  new GenericContainer<>("redis:3.0.5")
                          .withExposedPorts(6379);
   
          // Spring Redis 설정에 컨테이너 정보 주입 (Redis 실제 버전 고정 가능)
          @DynamicPropertySource
          static void redisProperties(DynamicPropertyRegistry registry) {
              registry.add("spring.redis.host", redisContainer::getHost);
              registry.add("spring.redis.port", () ->
                      redisContainer.getMappedPort(6379));
          }
      }
   ~~~

6. RedisTemplate은 운영 Bean을 쓰는 이유   
   Testcontainers는 Redis 서버만 제공한다. 
   따라서 Spring이 생성한 RedisTemplate Bean은 그대로 사용한다.


---
## 24. 테스트에서 발생한 Auditing 에러
> Auditing 에러는 테스트 환경에 Spring Security 인증 정보가 없는데,
  AuditorAware가 SecurityContext에 의존했기 때문에 발생했다. 
  테스트에서는 운영 Auditor를 로딩하지 않고, 테스트용 Auditor를 분리해 해결했다.

1. 발생 에러       
   통합 테스트 실행 중, Repository.save() -> persist() 시점, JPA Auditing 동작 중 발생한다.
   ~~~
   com.myecommerce.MyECommerce.exception.SpringSecurityException
           at SecurityAuditorAware.getCurrentAuditor(...)
   ~~~

2. 에러 발생 원인      
   Entity에 Auditing을 적용하거나 Auditor 제공자에서 에러가 발생한다.   
   JPA Auditing이 엔티티 저장 시 @CreatedBy / @LastModifiedBy 값을 채우려고 했는데,
   Spring Security 컨텍스트에서 인증 정보를 못 찾아서 예외가 터진 것이다.
   테스트 환경에서는 Spring Security 인증 정보가 없다.

   1) 테스트에서 Entity save() 실행.
   2) JPA Auditing이 @CreatedBy 값을 채우려 함.
   3) AuditorAware.getCurrentAuditor() 호출.
   4) SecurityContextHolder에 Authentication 없음.
   5) SpringSecurityException 발생.     

   실제 운영에서는 실제 API 요청이 발생해 SecurityFilterChain을 통과하기 떄문에 
   SecurityContextHolder에 인증 정보가 존재하므로 Auditing이 정상 동작한다.    
   따라서 테스트에서만 문제가 발생하게 된다.

   ~~~
   // Entity
   @CreatedBy
   private Long createId;
   ~~~
   ~~~
   @Component
   public class SecurityAuditorAware implements AuditorAware<Long> {
   
       @Override
       public Optional<Long> getCurrentAuditor() {
           Authentication authentication =
               SecurityContextHolder.getContext().getAuthentication();
   
           if (authentication == null) {
               throw new SpringSecurityException();
           }
           ...
       }
   }
   ~~~ 

3. 해결 방법
   1) 테스트용 Auditor 분리    
      SecurityContext가 필요없고, 인증 세팅이 불필요하다.
      테스트 안정적이므로 실무에서 가장 많이 쓰는 방식이다.     
      - TestAuditingConfig 생성
         ~~~
         @TestConfiguration
         @EnableJpaAuditing
         public class TestAuditingConfig {
   
             @Bean
             public AuditorAware<Long> auditorAware() {
                 return () -> Optional.of(1L);
             }
         }
         ~~~
      - 테스트 클래스에 TestAuditingConfig import
        ~~~
        @SpringBootTest
        @ActiveProfiles("test")
        @Import(TestAuditingConfig.class)
        class OrderCreateIntegrationTest {
        }
        ~~~
      - 운영 Auditing Config에 테스트 시 미실행 되도록 설정
        ~~~
        @Configuration
        @EnableJpaAuditing
        @Profile("!test")
        public class JpaAuditingConfig {
        }
        ~~~


---
## 25. 프로파일 전략 (@Profile)
1. @Profile   
   환경별로 빈 설정을 분리할 때 사용한다.
   Auditing이나 Security처럼 테스트에서 불필요한 인프라는 프로파일이나 테스트 설정으로 분리해서, 
   도메인 테스트가 인프라에 끌려가지 않도록 설정할 수 있다.

2. @Profile 적용    
   아래와 같이 테스트에서는 필요 없고, 운영에서는 필수인 경우 사용한다.
   - 보안 / 인증 / Auditing 관련 
   - AuditorAware
   - SecurityConfig
   - JwtProvider
   - OAuth / SSO 연동


---
## 26. 동시성 통합 테스트
비관적 락 동시성 제어가 들어간 주문 생성 메서드에 대한 통합 테스트 설계 문제이다.
1. 테스트 케이스 분리
   아래의 두 테스트는 역할이 다르므로 분리하는 것이 좋다.    
   하나로 합치면 테스트가 복잡해지고 실패 원인 분석이 어렵다.
   1) 정상 주문 생성 테스트    
      단순히 주문이 정상적으로 저장되고 DTO가 잘 반환되는지 확인.
      단순하고 예측 가능한 단건 테스트이다.
      주문 1, 2건만 있는 단건 테스트로 충분하다. 
      DTO 변환, 재고 차감, DB 저장 모두 확인 가능하기 때문이다.
   2) 동시성 제어 테스트    
      두 개 이상 요청이 동시에 들어왔을 때 재고 차감이 정상적으로 처리되는지 검증.    
      멀티스레드와 멀티트랜잭션 환경에서 재고가 안전하게 처리되는지를 검증한다.
      동시성 시뮬레이션용 다건 테스트가 필요하다.
      핵심은 충돌 상황을 만들어내는 것이므로, 2~5건 정도면 충분하다.
      너무 많으면 테스트 시간이 늘어나고, 오히려 실패 원인 분석이 힘들어진다.
      적은 수치와 반복 횟수로 동시성 버그를 검증한다.

2. 동시성 테스트 설계     
   Thread, ExcutorService 등을 사용해 멀티스레드로 동시 주문 요청을 수행한다.    
   재고가 충분한 상태에서 두 개 이상 요청이 동시에 들어오면, 재고가 0이하로 내려가지 않는지 확인한다.     

3. 트랜잭션 락이 적용된 동적 JPQL 단위 테스트 불필요    
   통합 테스트에서 조회 쿼리까지 포함해 검증 가능하다.
   따라서 별도 단위 테스트로 JPQL 동작 검증을 수행할 필요는 없다.    
   만약 쿼리 메서드에 복잡한 비즈니스 로직을 포함하거나 동적 조건 분기가 많다면,
   Repository 단위 테스트를 작서앻 놓으면 문제를 빠르게 찾을 수 있다.


### < 멀티스레드 환경에서 ArrayList에 데이터 저장 >
1. 문제점    
   멀티 스레드에서 ArrayList는 thread-safe 하지 않다.    
   동시 접근 시 데이터가 꼬이거나 예외 발생이 가능하다. 
   운이 좋으면 동작하고 아니면 값이 누락되거나, IndexOutOfBoundsException이 발생할 수 있다.

2. 해결방법
   1) thread-safe 컬렉션 사용   
      가장 간단히 해결 가능한 방법이다.
      ~~~
      List<Long> orderIds = Collections.synchronizedList(new ArrayList<>());
      ~~~
   2) Concurrent 컬렉션 사용   
      실무에서 선호하는 방법이다.
      락을 신경쓰지 않아도 되지 않지만, 쓰기가 많으면 성능 측면에서 손해 볼 수 있다.
      하지만 테스트라면 무시 가능한 측면이다.
      ~~~
      List<Long> orderIds = new CopyOnWriteArrayList<>();
      ~~~
   3) Future로 결과 수집    
      가장 정석적인 방법이다.
      결과 수집 구조가 가장 깔끔하고 동시성 컬렉션도 불필요하다.
      또한 예외 처리도 명확해진다.
      ~~~
      List<Future<ResponseOrderDto>> futures = new ArrayList<>();

      for (int i = 0; i < threadCount; i++) {
         futures.add(executor.submit(() ->
               orderService.createOrder(requestOrder, member)
         ));
      }
   
      for (Future<ResponseOrderDto> future : futures) {
         orderIds.add(future.get().getOrderId());
      }
      ~~~


---
## 27. 테스트에서 @Transactional
테스트에서 @Transactional 사용은 선택사항이지만, 통합 테스트에서는 보통 적용한다.
- 라이브러리     
  org.springframework.transaction.annotation.Transactional    
  Spring AOP 기반 트랜잭션으로 전파 옵션, 격리 수전, rollback 규칙을 포함한다.
  Spring Data JPA, 테스트, 프록시 전부 연동 가능하다.
   jakarta에서 제공하는 것은 Spring에서 지원은 하지만 핵심 기능이 빠져 있으므로 사용하지 않도록 하자.   

1. Service에서 @Transactional
   - 비즈니스 동작 보장
   - 더티체킹
   - 락 유지
   - 원자성 보장

2. 테스트에서의 @Transactional
   - 테스트 격리 (테스트 clean up 편의성 용으로 사용)
   - 테스트 종료 시 자동 롤백으로 DB 정리하지 않아도 됨.

3. 테스트에서 @Transactional과 비관적 락     
   운영 코드에서 트랜잭션 락을 걸었다면, 테스트에서 @Transactional을 적용하면 안된다.     
   테스트에서 @Transactional 적용하면 메서드 시작 시 하나의 트랜잭션, 하나의 커넥션을 가진다.   
   내부에서 스레드를 아무리 나눠도 실제 DB 입장에서는 동일 트랜잭션이므로 락 경쟁 자체가 발생하지 않는다.
   즉, 동시성 테스트가 구조적으로 불가능하게 된다.
    
4. 테스트에서 @Transactional과 Redis     
   @Transactional은 Redis를 롤백해주지 않는다.
   따라서 테스트에서 사용한 데이터는 직접 정리해야 한다.
   @Transactional은 트랜잭션 매니저가 관리하는 리소스만 롤백하기 때문이다.
   RDB 관련한 JPA, JDBC의 경우는 DataSourceTransactionManager가 관리하는 한다.
   하지만 Redis는 트랜잭션이 관리하지 않는다.


---
## 28. 동시성 통합 테스트에서 @Transactional이 무시된 이유와 해결
1. 발생 에러
   member 테이블의  user_id 컬럼에 유니크 제약 조건이 있다.
   동시성 테스트 실행 후 테스트 데이터가 롤백되지 않은 상태에서
   다음 테스트에서 user_id = "customer"인 회원 insert를 다시 시도했다.
   이 때 이미 존재하는 값이라 DB가 거부한다.
   ~~~
   could not execute statement
   Duplicate entry 'customer' for key 'member.UK_user_id'
   ~~~

2. 에러 발생 원인
   > 이번 에러의 원인은 멀티스레드 자체가 아니라,
   Self-invocation으로 인해 @Transactional이 적용되지 않은 cleanup 로직에서
   테스트 데이터가 정상적으로 삭제되지 않은 것이었다.

   동시성 테스트에서 흔한 함정이다.   

   멀티스레드 환경에서 주문 생성 로직이 실행되었고,
   테스트 종료 후 데이터 정리를 위해 별도의 cleanup 메서드를 호출했지만,
   해당 cleanup 로직이 트랜잭션 없이 실행되었다.

   cleanup 메서드에는 @Transactional이 적용되어 있었으나,
   같은 테스트 클래스 내부에서 직접 호출(Self-invocation)되었기 때문에
   Spring 트랜잭션 프록시를 거치지 않았고, 트랜잭션이 실제로 시작되지 않았다.

   그 결과 Member / Product / Order 데이터 삭제가 정상적으로 수행되지 않았고,
   다음 테스트에서 동일한 userId로 회원 insert 시 UNIQUE 제약 오류가 발생했다.
   
   Spring 트랜잭션은 프록시 기반으로 동작한다.
   따라서 같은 클래스 내부 메서드 호출 시 프록시를 타지 않으며
   @Transactional은 무시된다.
   ~~~
   @Transactional
   void cleanUpSavedDataForConcurrency(...) { 
            orderRepository.deleteByIdIn(orderIds);
            productRepository.deleteById(savedProduct.getId());
            memberRepository.deleteById(savedMember.getId());
   }
   
   @Test
   @DisplayName("주문생성 성공 - 동시에 여러 주문 요청 시 옵션 재고 차감")
   void createOrder_shouldDecreaseOptionStock_whenConcurrentlyOrderRequest() {
       // ...
       cleanUpSavedDataForConcurrency(orderIds, savedProduct, savedMember); 
   }
   ~~~

3. 해결방법
   cleanup 로직에서 @Transactional에 의존하는 방식을 제거하고,
   트랜잭션을 명시적으로 시작할 수 있는 TransactionTemplate을 사용한다.

   TransactionTemplate은 프록시 호출 여부와 무관하게
   해당 지점에서 즉시 트랜잭션을 시작하므로
   Self-invocation 문제를 회피할 수 있다.
   
   이를 통해 트랜잭션 경계가 코드상 명확해지고
   cleanup 로직이 항상 트랜잭션 내에서 실행되며
   테스트 종료 후 DB 상태를 확실하게 정리할 수 있다.
   ~~~
    @Autowired
    private TransactionTemplate transactionTemplate;
   
    /** 테스트 데이터 정리 */
    void cleanUpSavedDataForConcurrency(List<Long> orderIds,
                          Product savedProduct,
                          Member savedMember) {
        transactionTemplate.executeWithoutResult(status -> {
            orderRepository.deleteByIdIn(orderIds);
            productRepository.deleteById(savedProduct.getId());
            memberRepository.deleteById(savedMember.getId());
        });
    }
   
   @Test
   @DisplayName("주문생성 성공 - 동시에 여러 주문 요청 시 옵션 재고 차감")
   void createOrder_shouldDecreaseOptionStock_whenConcurrentlyOrderRequest() {
       // ...
       cleanUpSavedDataForConcurrency(orderIds, savedProduct, savedMember); 
   }
   ~~~
 

---
## 29. 도메인 테스트
도메인은 행위 중심으로 테스트를 수행해야 한다.    
내부 메서드 로직 테스트는 수행하지 않도록 한다.
1. 내부 메서드 로직 테스트 하지 않는 이유    
   1) 진입점 메서드의 파생 로직이기 때문   
      Entity 내부에서 사용되는 메서드들은 전부 createOrder() 한 진입접에서만 호출되는 행위 메서드의 파생 로직이다.   
      외부에서 직접 호출 불가한 도메인 행위의 구현 세부사항이므로, 결과로만 검증한다.
   2) private 메서드 테스트는 리팩터링 저항을 키움    
      테스트가 구현을 묶는 상황으로 개별 테스트는 진행하지 않고 진입점에서 결과만 검증될 수 있도록 한다.
2. 필수 테스트   
   1) validation 실패 테스트   
      도메인 정책이므로, 정책 실패 테스트는 필수이다.


### < Order–OrderItem 관계의 불변식을 설계로 고정해 테스트하지 않음 >
1. 문제점
   orderItem.assignOrder(null)에서 Order 객체 재할당 시도를 위해 null을 전달한다. 
   주문물품 없이 주문 객체 생성 불가하므로, Order는 설계적으로 빈 객체 생성을 막았기 때문이다.    

   orderItem.assignOrder()는 이미 주문이 있으면 실패만 검증하면 된다.   
   성공 케이스를 만들 수 있는 public API가 없기 때문이다.
   그래서 null을 전달했다.
   이는 테스트가 도메인 설계 한계를 드러낸 부분으로 볼 수 있다.
   ~~~
    @Test
    @DisplayName("주문물품에 주문 할당 - 주문된 주문물품에 대해 주문 재할당 불가")
    void assignOrder_shouldNotAssignOrderAtOrderItem_whenItemAlreadyOrdered() {
        // given
        // 주문 상품옵션
        ProductOption option = productOption();
        // 주문요청 상품옵션 수량
        int orderQuantity = 3;
        // 주문물품 생성
        OrderItem orderItem = OrderItem.createOrderItem(option, orderQuantity);
        // 주문 생성
        Order.createOrder(List.of(orderItem), member()); // 주문물품에 주문 할당됨

        // when
        // then
        OrderException e = assertThrows(OrderException.class, () ->
                orderItem.assignOrder(null)); // 주문물품에 주문 재할당 시도
        assertEquals(ITEM_ALREADY_ORDERED, e.getErrorCode());
    }
   ~~~   
   
2. 해결방법    
   assignOrder()의 외부 호출을 막는다.   
   메서드를 호출 가능한 클래스를 /order 패키지로 한정해  Order.createOrder()에서만 호출 가능하도록 변경하는 것이다.   
   그러면 불변식은 설계로 보장 테스트를 굳이 하지 않아도된다.    
   따라서 재할당 실패 테스트 자체를 제거해도 된다. 
   상태 전이 불가는 설계로 증명되기 때문이다.
   ~~~
   void assignOrder(Order order) // package-private
   ~~~

   1) protected vs package-private    
      - protected       
        같은 패키지내에서 접근 가능하도록 제한하지만, 상속받은 외부 클래스에서도 호출 가능하므로 권장하지 않음.    
        확장을 허용한다는 의미가 되어 '이 메서드는 Order만 써야 한다'는 의도가 상속 하나로 깨지게 된다.
      - package-private
        접근 제한자가 없는 경우로, 같은 패키지에서만 호출 가능하다.
        DDD 관점에서 제일 자연스럽다. 
   
   2) 설계로 보장된다는 것의 의미     
      현재 Order.createOrder 내부에서 orderItem.assignOrder(this)를 호출하고 있다.   
      그리고 orderItem.assignOrder() 메서드를 package-private로 선언해 외부에서 호출 불가하게 막는다면, 
      OrderItem은 이미 주문에 속한 상태로만 생성되게 된다.    
      즉, OrderItem이 주문에 할당되는 유일한 경로가 Order.createOrder() 뿐이게 되는 것이다.   
      이것은 설계로 보장된 상태로 본다.
   
   3) 설계로 보장되어 테스트가 필요 없다는 것의 의미     
      테스트의 목적은 '이 코드가 깨질 가능성이 있는가?'를 확인하는 것이다.   
      orderItem.assignOrder() 메서드를 package-private로 선언해 외부에서 호출 불가하게 막고, 
      유일하게 Order.createOrder()를 통해서만 OrderItem에 주문 할당이 가능하다면 
      성공 경로가 외부에서 조작 불가하고, 제공하는 public API로는 잘못된 상태를 만들 수 없다.
      따라서 깨질 수 없는 구조는 테스트 대상이 아니다.     
      불변식을 코드로 고정했기 때문이다.

   4) 정상 동작의 증명    
      정상 동작은 OrderEntity 테스트 하나면 충분하다.
      assignOrder() 정상 동작은 재할당 불가 전체를 충족하게 되고 Order -> OrderItem 관계를 보장하게 된다.


---
## 30. Payment.isTerminal() 테스트코드 작성 - 팩토리 생성 구조에서 특정 상태 테스트가 불가능한 문제 해결   
1. 발생 원인   
   Payment 엔티티는 팩토리 메서드를 통해서만 생성되고, 상태 변경 또한 도메인 메서드로만 가능하도록 캡슐화되어 있다.    
   이러한 구조는 도메인 무결성을 지키는 올바른 설계지만, 
   아직 실제 로직에서 CANCELED 상태로 전이되는 기능이 구현되지 않은 상태이므로 
   테스트 코드에서 해당 상태 객체를 생성할 방법이 없었다.     
   그 결과 isTerminal() 메서드에서 CANCELED 상태 검증 테스트를 작성할 수 없는 문제가 발생했다.     
   
2. 해결 방법     
   도메인 캡슐화 설계와 테스트 필요 조건 사이에서 발생한 자연스러운 충돌이었고,
   테스트 전용 객체 생성 방식을 도입하여 해결했다.    
   테스트 전용 생성 경로와 리플렉션 기반 필드 설정을 통해 설계를 훼손하지 않으면서 테스트 가능성을 확보하는 것이다.      
   테스트 코드 내부에서만 사용하는 헬퍼(테스트 전용 팩토리)를 만들고, 
   ReflectionTestUtils를 사용해 Payment 객체의 private 필드인 paymentStatus 값을 직접 설정하도록 했다. 
   이를 통해 프로덕션 코드의 생성 규칙과 캡슐화를 유지하면서도 테스트에서 필요한 상태 객체를 생성할 수 있게 되었다.    
   ~~~
    /** PG 승인취소된 결제 생성 - 결제상태 CANCELED */
    Payment canceledPayment() {
        // ...

        // 결제 생성 (READY)
        Payment payment = Payment.createPayment(order, requestMethod, pgProvider);

        // PG 결제 승인취소 (현재 로직에서 미지원으로 강제변경)
        ReflectionTestUtils.setField(payment, "paymentStatus", CANCELED);

        return payment;
    }
   ~~~
   

### < 상태변경 로직이 없어도 테스트해야 하는 이유 >
테스트의 목적은 현재 구현된 기능만 검증하는 것이 아니라, 
도메인이 보장해야 할 규칙이 미래에도 유지되는지를 검증하는 것이다.     
PaymentStatus enum에 CANCELED 상태가 정의되어 있고 도메인 규칙상 해당 상태가 terminal 상태라면, 
그 규칙 자체가 테스트 대상이다.    
만약 테스트가 없다면 이후 코드 수정 과정에서 CANCELED 상태가 terminal 조건에서 빠지는 버그가 생겨도 감지할 수 없다.    
즉, “아직 기능이 없다”는 이유는 테스트 생략의 근거가 되지 않으며, 상태 모델에 존재하는 값이라면 언제든 검증되어야 한다.    


### < ReflectionTestUtils >
1. ReflectionTestUtils    
   ReflectionTestUtils는 Spring 테스트 유틸리티.    
   자바 리플렉션을 활용해 접근 제한자(private, protected 등)를 무시하고 
   객체 내부 필드 값을 읽거나 수정할 수 있게 해주는 도구다.     
   엔티티에서 setter를 제공하지 않아 외부에서 상태 변경이 불가능하도록 설계했더라도, 
   테스트 환경에서는 이 도구를 통해 내부 값을 직접 설정할 수 있다.     
   즉, 도메인 설계를 깨지 않고 테스트만을 위해 객체 상태를 구성할 수 있도록 해주는 안전한 테스트 지원 도구다.   

2. setter가 없는데 값이 수정되는 이유    
   자바 리플렉션은 런타임에 클래스 구조를 분석하고 접근 제한을 우회할 수 있는 기능을 제공한다. 
   ReflectionTestUtils는 내부적으로 리플렉션 API를 사용하여 private 필드 접근 제한을 해제한 뒤 값을 주입한다.    
   따라서 setter가 없어도 값 변경이 가능하다.    
   중요한 점은 이 방식은 일반 애플리케이션 로직에서는 사용되지 않고 테스트 코드에서만 사용된다는 점이며, 
   이는 도메인 무결성을 유지하면서 테스트 유연성을 확보하기 위한 의도된 사용 방식이다.      


---
## 31. 코드의 분기
아래의 코드의 분기는 딱 2개 뿐이다.   
true는 예외 발생, false느 통과한다.   
READY / IN_PROGRESS는 동일분기, APPROVED / CANCELED 도 동일분기이다.    
분기별 테스트는 최소 하나씩 작성하는 게 좋다.
하지만 READY / IN_PROGRESS 중 하나의 상태만 테스트한 반면,
APPROVED / CANCELED 상태 두가지를 모두 테스트코드를 작성하는 것은 분기 문제가 아니라 리스크 관리 문제 때문이다.   
누군가 나중에 코드를 변경할 수 있기 때문에 사전에 방지하는 것이다.
READY / IN_PROGRESS의 경우는 아직 결제가 종결되지 않았다는 도메인적 의미를 가지지만,
APPROVED / CANCELED은 둘 다 approve 불가지만 비즈니스 의미가 다르다.
따라서 정책 테스트에서는 둘 다 검증하는 게 안전하다.   
Entity에서의 테스트코드는 로직 정확성 검증용, 정책 테스트에서는 규칙 적용 검증용이기 때문이다.

~~~
// PaymentPolicy 코드
    // 결제 승인된 경우 결제 생성 불가 (승인, 취소 등 PG 승인된 경우)
    private void validatePaymentAlreadyApproved(List<Payment> paymentList) {
        long invalidPaymentCount = paymentList.stream()
                .filter(payment -> !payment.isApproveRequestAvailable()) // 결제 완료되어 결제불가
                .count();

        if (invalidPaymentCount > 0) {
            throw new PaymentException(PAYMENT_ALREADY_COMPLETED);
        }
    }
~~~
~~~
// Payment 코드
    /** PaymentStatus - 자기 상태 판단
     *  PG 승인 요청 가능 여부 반환 **/
    public boolean isApproveRequestAvailable() {
        return !(this.paymentStatus == APPROVED || this.paymentStatus == CANCELED);
    }

~~~


---
## 32. PaymentServiceTest 구조개선
### < given절 >
1. 좋지 않은 given    
   읽는 사람이 Builder 내부 구조 해석해야 함.
   ~~~
   Member member = Member.builder()
       .userId("customer")
       .roles(List.of(MemberAuthority.builder())
       .build(); 
   ~~~

2. 좋은 given    
   읽는 순간 의미 이해되어야 함.
   1) 기존 생각     
      given 데이터를 픽스쳐로 외부로 빼버리면 데이터를 보러 픽스쳐 메서드로 이동해야하니까 더 읽기 어려운 구조가 되는 것이라고 생각했다.
      그래서 요청 데이터들은 픽스쳐로 빼지 않았다.    
   2) 픽스쳐 메서드로 빼야하는 것    
      fixture는 무조건 빼는 게 아니라 "의미없는 데이터만" 빼는 것이다.
      즉, 테스트 의도와 상관없는 값을 빼는 것이다.
      따라서 요청 데이터는 테스트 코드 안에 유지하는 것이 맞다.
   ~~~
   Member member = fixture.member();
   Order order = fixture.order(member);
   PgResult pgResult = fixture.pgResult();
   ~~~

3. PaymentService 정상흐름 테스트에서 요청값     
   둘 다 요청값이지만 request는 핵심 입력값이고 member는 현재 테스트에서는 핵심 입력값이 아니다.
   reqeust는 결과를 바꾸는 값인 반면, member는 정책 검증 통과용 객체로 결과를 변경하지 않기 때문이다.   
   따라서 request는 유지, member는 픽스쳐로 빼는 것이 가독성 측면에서 좋다.

   
### < verify() >
Mockito에서 verify()는 times()를 명시하지 않으면 기본값이 1회 호출 검증하므로 생략가능.
기본값이 1이니까, times(1)을 쓰게 되면 노이즈일 뿐이다.
1. 기존 코드
   ~~~
   verify(paymentPolicy, times(1)).preValidateCreate(member);
   ~~~
2. 수정한 코드   
   ~~~
   verify(paymentPolicy).preValidateCreate(member);
   ~~~

