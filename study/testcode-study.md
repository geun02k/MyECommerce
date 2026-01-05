# MY E-COMMERCE PROJECT TEST CODE STUDY
2025 간단한 이커머스 서비스를 만들면서 접한 테스트코드에 대해 공부한 내용


---
## < 목차 >
1. 테스트코드
   - 테스트코드
   - @Test (단위테스트)
   - @WebMvcTest (웹 계층 슬라이스 테스트)
   - @SpringBootTest (애플리케이션 통합테스트)
2. mocking
   - mocking
   - Mock 라이브러리
   - Mock 객체 생성
   - Stub
   - Spy
   - mock 객체 호출 검증
   - @ExtendWith(MockitoExtension.class)
   - repository의 메서드를 stub하지 않아도 테스트코드 오류가 발생하지 않는 이유
4. 테스트코드 검증
   - 테스트코드 검증
   - 테스트코드 검증과 ArgumentCaptor
   - 테스트코드 전달인자 검증
7. Reflection - private 메서드 테스트코드 작성
8. Controller 테스트케이스 작성 - 도저히 안되서 Chat gpt를 통해 작성
9. JPA의 Dirty Checking을 사용 시 테스트코드 작성


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
## 2. mocking
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


---
## 4. 테스트코드 검증
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
## 7. Reflection - private 메서드 테스트코드 작성
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
## 8. Controller 테스트케이스 작성 - 도저히 안되서 Chat gpt를 통해 작성
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
## 9. JPA의 Dirty Checking을 사용 시 테스트코드 작성
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

          