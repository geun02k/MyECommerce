# MY E-COMMERCE PROJECT STUDY
2025 간단한 이커머스 서비스를 만들면서 공부한 내용

---
### < 스프링부트 의존성추가 >
1. org.springframework.boot:spring-boot-starter-web 의존성추가
    - 참고블로그 https://priming.tistory.com/144

---
### < slack과 github 연동 >
- 참고블로그 : https://sepiros.tistory.com/37

---
### < Mysql 데이터타입 결정 >
- 참고블로그 https://dev-coco.tistory.com/54

---
### < DTO, VO, MODEL 차이 >
https://010562.tistory.com/11
https://velog.io/@chae_ag/Model-DTO-VO-DAO-%EA%B0%80-%EB%AC%B4%EC%97%87%EC%9D%B8%EA%B0%80%EC%9A%94

---
### < 비밀번호 유효성검사 정규식 >
- (?= ...)
    - 긍정형 전방 탐색(lookahead)를 의미하며 특정 패턴이 뒤따라야 한다는 조건을 확인하지만, 문자열에서 그 부분을 소비하지(매칭으로 소모하지) 않습니다.
- (?=.[!@#$%^+=-])
    - 문자열에 하나 이상의 지정된 특수 문자가 포함되어 있는지 확인합니다.
- 참고블로그 https://ddyes.tistory.com/6
- 참고블로그 https://kin.naver.com/qna/detail.naver?d1id=1&dirId=1040202&docId=468186271&enc=utf8&kinsrch_src=pc_nx_kin&qb=7KCV6rec7ZGc7ZiE7Iud&rank=1&search_sort=0&section=kin.qna_ency_cafe&spq=0
- 비밀번호 추가 유효성검사
    ~~~
    // ~!@#$%^&*-=_+ 특수문자 중 1개 필수포함
    String pwPattern = "(?=.[~!@#$%^&*-=_+])";
    if(!(Pattern.matches(pwPattern, member.getPassword()))) {
        System.out.println("비밀번호에 특수문자 ~ ! @ # $ % ^ & * - = _ + 중 하나를 필수 포함해야합니다.");
        return false;
    }
    // 연속으로 동일문자 3번 반복 불가
    ~~~
  
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


---
### < 인터페이스 사용 이유 >
- 기능(메소드)의 구현을 강제함으로써, 클래스의 설계 또는 표준화를 유도하기위해 사용함.
- 참고블로그 https://0soo.tistory.com/62

### < 인터페이스와 추상화 클래스의 차이 >
    기능(메소드)의 구현을 강제함으로써, 클래스의 설계 또는 표준화를 유도를 위해 
    인터페이스와 추상화 클래스로 Error Code와 Custom Exception을 생성했다.      
    RuntimeException을 확장해 Custom Exception을 생성한다는 의미로 추상클래스로 구현했고
    Error Code 생성 시 필수 선언해야하는 필드들의 getter()를 추상메서드로 선언해 필수 구현하도록 했다.

- 인터페이스
    - 인터페이스에 정의된 메서드를 각 클래스의 목적에 맞게 기능을 구현하는 느낌.
    - 클래스와 별도로 구현 객체가 같은 동작을 한다는 것을 보장하기 위해 사용하는 것에 초점을 맞춘다.
- 추상클래스
    - 자신의 기능들을 하위 클래스로 확장 시키는 느낌.
    - 추상화(추상 메서드)를 하면서 중복되는 클래스 멤버들을 통합 및 확장을 할 수 있다.   
    - 같은 추상화인 인터페이스와 다른점은, 추상클래스는 클래스간의 연관 관계를 구축하는 것에 초점을 맞춘다는 것에 있다.
- 참고블로그 https://inpa.tistory.com/entry/JAVA-%E2%98%95-%EC%9D%B8%ED%84%B0%ED%8E%98%EC%9D%B4%EC%8A%A4-vs-%EC%B6%94%EC%83%81%ED%81%B4%EB%9E%98%EC%8A%A4-%EC%B0%A8%EC%9D%B4%EC%A0%90-%EC%99%84%EB%B2%BD-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0


---
### < 예외처리 >
- Custom Exception을 생성해 예외처리하는 이유
  - throw new NullPointerException("error message") 와 같이 에러를 처리하면   
    통일성이 전혀 없고 또 정확한 에러 원인과 이유도 알 수 없다.
  - 참고블로그 https://velog.io/@mindfulness_22/%EC%98%88%EC%99%B8%EC%B2%98%EB%A6%AC%EB%8A%94-%EC%99%9C-Custom%ED%95%B4%EC%84%9C-%EC%8D%A8%EC%95%BC%ED%95%A0%EA%B9%8C   

- Custom Exception 생성 시 RuntimeException 상속받는 이유
  - 호출자가 예외를 처리하도록 강제받지 않기위해 Exception이 아닌 RuntimeException 상속받음.
  1. Checked Exception을 피할 수 있음
     커스텀 예외를 정의할 때 RuntimeException을 상속받으면 언체크 예외가 됩니다. 이는 호출자가 예외를 처리하도록 강제받지 않기 때문에 코드가 더 간결해질 수 있습니다.
  2. 사용자 편의성
     개발자가 특정한 상황에서 발생하는 예외를 명확하게 정의할 수 있어, 코드의 가독성을 높이고 예외 처리 로직을 간단하게 유지할 수 있습니다. 불필요한 예외 처리를 피할 수 있는 장점이 있습니다.
  3. 비즈니스 로직과의 일관성
     대부분의 비즈니스 로직에서 발생하는 예외는 런타임 예외로 간주되므로, 커스텀 예외도 이를 따르는 것이 자연스럽습니다.   
  - 참고블로그 https://pixx.tistory.com/409
  
- CommonErrorResponse.java
  - 에러 발생 시 응답을 위한 모델.
  - 모든 에러에 대해 일괄적인 응답을 위해 사용.

- CommonExceptionHandler.java
  - 발생한 예외처리를 수행.
    ~~~
    // 모든 BaseAbstractException을 상속하는 에외객체에 대한 예외처리 수행.
    @ExceptionHandler
    protected ResponseEntity<CommonErrorResponse> commonHandler(BaseAbstractException e) {
        // 응답 객체 생성
        // 응답 객체 반환
      }
    ~~~
  - HttpStatus.resolve(e.getStatusCode())  : HttpStatus에 상태코드를 담아서 errorResponse와 함께 Http 응답으로 내려보낸다.
    ~~~ 
      return new ResponseEntity<>(errorResponse,
                Objects.requireNonNull(HttpStatus.resolve(e.getStatusCode())));
    ~~~



