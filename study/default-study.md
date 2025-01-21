# MY E-COMMERCE PROJECT STUDY
2025 간단한 이커머스 서비스를 만들면서 공부한 내용

---
### < service, service impl 분리하지 않는 이유 >
- 참고블로그 https://zhfvkq.tistory.com/76

spring은 객체간의 의존성을 관리해주는데 이 의존성은 테스트 코드 단위테스트를 작성할때 문제가 생긴다.    
의존성을 가지는 다른 객체에 의해 테스트 결과가 영향을 받는다.    
그래서 내가 원하는 동작만 하도록 하는것이 Mock 객체다.    
이를 편하게 사용하도록 지원하는것이 Mockito 이다.    

Service와 ServiceImpl 구조를 사용하면 단위 테스트가 어려울 수 있다.    
이 구조를 사용하면 Service 인터페이스와 ServiceImpl 구현체를 모두 생성해야 하므로 테스트 코드를 작성하기 어렵다.    
또한, 구현체와 인터페이스 간의 의존성이 높아져 테스트 케이스 작성이 복잡해질 수 있다.     
결론적으로 Service ServiceImpl 구조를 사용해야 할까?    
OOP 관점에서 봤을 때 인터페이스는 다형성 혹은 개방 폐쇄 원칙 때문에 사용한다. 보통 흔히 얘기하는 느슨한 결합 혹은 유연해지도록 설계하기 위해 사용된다.    
그렇다면 Spring에서 Service는 다형성이 필요한가?    
우리가 사용하는 대부분의 Service는 하나의 Service가 여러 개의 SerivceImpl을 가지고 있는 경우가 아닌 1:1 구조를 띄고 있다.    
그러므로 Service를 사용하고, 추후 다형성이 필요해지면 그때 해당 Service에 대해서 인터페이스를 적용해도 늦지 않는 것 같다.    

--- 
### < 테스트코드 >
- MyECommerceApplicationTests.java   
  스프링 어플리케이션이 뜨는지를 테스트해주는 기본적으로 내장되어있는 test코드.   
  서버가 실행중일 떄 테스트코드를 실행하면 redis 포트를 사용하고있다고 에러가 발생한다.   
  그래서 전체 테스트 시 AccountApplicationTests이 포함되어있고 서버가 실행중이라면
  스프링 어플리케이션 서버를 종료해야 AccountApplicationTests 에러가 발생하지 않는다.   

- @SpringBootTest
    - org.springframework.boot:spring-boot-starter-test 에 포함된 기능.
    - 의존성을 주입하지 않고 테스트가능.
    - springboot context loader를 실제 어플리케이션처럼 테스트용으로 만들어줌.
    - context(설정 등)를 실제 환경과 동일하게 모든 기능을 생성해 빈들을 등록한 것을 이용해 테스트 가능.   
      따라서 의존성 주입을 이용해 테스트가능.
      ~~~
      private AccountService accountService = new AccountService(new AccountRepository());
      ~~~
      위와같이 작성하지 않고 아래와 같이 작성.
      ~~~
      @Autowired
      private AccountService accountService;
      ~~~
    - Junit을 그대로 이용해서는 Mockito 기능 사용불가.
      Mockito 확장팩을 테스트 클래스에 달아줌.
      @SpringBootTest -> @ExtendWith(MockitoExtension.class) 로 변경

- main함수가 아니지만 실행가능한 이유 : Junit 프레임워크가 대신 실행해주기 때문.


### < 테스트코드작성 mocking >
- Mock을 사용해 테스트하면
  동일 테스트코드를 함께 실행해도 하나는 성공하고 하나는 실패하는 문제 해결가능.   
  DB에 데이터가 바뀌든 의존하고 있는 repository의 로직이 변경되든 관계없이
  내가 맡은 역할만 테스트가능.
- 참고블로그 https://soonmin.tistory.com/85   
- 테스트는 given-when-then(BDD 스타일) 패턴으로 작성한다. 
  willReturn(),willThrow() 등 메서드를 통해 mock 객체(userRepositoy)의 예상 동작을 미리 정의해준다.
  exception을 검사하고 싶을 때는 assertThrows와 함께 사용하면 된다.
  should() 메서드로 해당 메서드가 제대로 호출되었는지 검증한다.   
- Mockito 와 BDDMockito
    - 참고블로그 https://soso-shs.tistory.com/109

    
    - MemberServiceTest.java
        MemberService는 MemberRepository에 의존하고있다.
        MemberRepository에 가짜로 생성해 MemberService에 의존성을 추가해준다.
        
        @Mock
        private MemberRepository memberRepository;
        -> MemberRepository를 가짜로 생성해 memberRepository에 담아준다.
          (injection, 의존성주입과 비슷하게 생성해준다.)
        
        @InjectMocks
        private MemberService memberService;
        -> Mock으로 생성해준 memberRepository를 memberService에 inject.

- given() 
  - stub(가설)
  - 생성된 mock 객체의 예상 동작을 정의 (mock 객체 리턴내용 지정)
  ~~~
    given(memberRepository.findByTel1AndTel2AndTel3(any(), any(), any()))
        .willReturn(Optional.empty());
  ~~~


### < 테스트코드작성 ArgumentCaptor >
- 참고블로그 https://hanrabong.com/entry/Test-ArgumentCaptor%EB%9E%80
- capture()
  - 말 그대로 해당 인자를 낚아채는 메서드입니다.   
- getValue()
  - 낚아챈 인자의 값을 불러올 때 사용합니다.    
    만약 해당 메서드가 여러번 호출이 되었을 경우 가장 마지막으로 호출된 메서드의 인자를 갖고옵니다.   
- getAllValues()
  - 낚아챈 인자 전부를 List 형태로 받아옵니다.   
- verify() 
  - 서비스 내부 로직이 실행됐는지 여부 체크
  - memberRepository.save() 실행 여부 체크



