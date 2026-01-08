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
8. 테스트코드에서 필드주입
9. Reflection - private 메서드 테스트코드 작성
10. JPA의 Dirty Checking을 사용 시 테스트코드 작성
11. 프로덕션, 테스트 config 파일
12. Spring Security와 테스트코드
    - permitAll() 경로 테스트에서 member.roles가 null이면 에러 발생 이유
    - 권한 검증 테스트가 누락되는 문제 해결
14. 테스트코드 리팩토링
    - 상품등록성공 Controller 테스트코드 리팩토링 

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
1. Controller 슬라이스 테스트   
   Security, JWT, Service 구현 등에 관심이 없고 요청에 따른 컨트롤러 응답만 검증.
   이 때, JWT 토큰이 없고 Security Filter가 먼저 실행되어 Controller까지 가지 못해 테스트가 깨진다.
   따라서 해당 테스트에서 모든 요청을 통과 시키기 위해 Security 자체를 끄는 방법이 필요하다.  
   <br>

2. Controller 슬라이스 테스트 목적
   - 요청 바인딩
   - Validation
   - HTTP Status
   - Controller 로직   
     JWT 파싱, 토큰 서명, 인증 서버 연동, 실제 권한 판별 로직 등은 검증하지 않는다.


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

