# MY E-COMMERCE PROJECT STUDY
2025 간단한 이커머스 서비스를 만들면서 공부한 내용


---
## <목차>
1. Spring
   - Spring core
   - 스프링부트 의존성추가 키워드
   - Spring 어노테이션
2. 라이브러리
   - 매핑 라이브러리 MapStruct
   - 유효성 검사 라이브러리 validation
3. 유효성 검증
   - 정책과 보호 
   - DTO Validation과 Service 정책 검증의 책임 분리 기준
   - 잘못된 Service Validation Check
   - 전화번호 검증 중복으로 인한 구조적 문제와 개선 방향
   - 검증관련처리
   - PathVariable에 대한 @NotNull 검증 제외 판단
   - PathVariable 검증
   - DTO 검증 실패 vs PathVariable/RequestParam 검증 실패
   - 유효성검사 정규식
   - 비밀번호 유효성검사 정규식
4. git 사용하기
   - slack과 github 연동
   - git push 내역 되돌리기
5. 데이터베이스
   - Mysql 데이터타입 결정 
   - 데이터베이스의 커서(Cursor)
   - 커서의 hasNext(), isClosed()
   - 커서가 닫힌 것과 읽을 데이터가 없는 것
6. DTO
   - DTO, VO, MODEL 차이
   - 도메인과 DTO
   - 요청과 응답으로 DTO 사용해야하는 이유
   - request, response DTO 분리
   - Value Object (VO)
7. 인터페이스
   - 인터페이스 사용 이유
   - 인터페이스와 추상화 클래스의 차이
   - service, service impl 분리하지 않는 이유
8. 예외처리
   - 예외 처리 표준화: Custom RuntimeException 기반 설계
   - BaseAbstractException 설계 선택: 계약 중심에서 데이터 중심으로
   - BaseAbstractException 설계 개선: Lombok 생성자 제거와 불변 구조 확립
   - 예외 처리 구조 개선: ErrorCode 식별자화 및 메시지 책임 분리
9. JWT 이용한 로그아웃
10. 로그 레벨 결정
11. 상수 선언
12. Enum값 비교 연산자
13. Pageable
    - Pageable - 페이지 기능을 지원 
    - Pageable 페이지, 사이즈 디폴트값 지정
14. Controller 메서드 파라미터
    - Controller에서 @RequestParam을 DTO로 받는 이유 
    - 스프링 MVC 파라미터 바인딩 (컨트롤러에서 파라미터값 받는 방법) 
15. 메시지 관리
    - properties 파일 한글 깨짐 문제 해결
    - 메시지 관리 방식 개선
    - 서버 에러 메시지 하드코딩 제거: placeholder 기반 메시지 분리 전략
    - 메시지 호출 테스트 문제해결 : Spring Validation과 ExceptionHandler에서 메시지 처리
    - 메시지 조회 Locale 문제 해결방법
    - MessageSource placeholder 규칙으로 인한 메시지 파싱 오류와 해결
16. HTTP 상태코드
17. null과 ObjectUtils.isEmpty()
    - ObjectUtils.isEmpty() 남용 문제
18. 자바 참조 전달
19. 비즈니스 정책의 구분
    - 비즈니스 정책의 책임 범위: 판단 로직과 구현 로직의 분리
    - 장바구니 만료 정책과 Redis TTL 구현의 책임 분리
20. Redis와 반환된 HashMap 순서 미보장
21. 조회
    - Context 객체 (미사용, 스터디는 필요)
    - MapStruct (미사용, 스터디는 필요)
22. DB 키 사용 원칙
23. Java에서 ""와 """ 차이
24. IllegalStateException vs OrderException
    - order.assignOrder()에서 던질 Exception 결정
    - IllegalStateException vs OrderException 사용 가이드라인
25. 주문 번호 생성 방법
26. BigDecimal
27. 도메인 모델 중심 설계 (DDD, Domain-Driven-Design)
    - 도메인 주도 설계
    - DDD에서 가장 중요한 것
    - DDD 애플리케이션 레이어 분리 필수 규칙
    - DDD의 도메인 레이어
    - DDD 설계 방식이 적합한 경우
    - DDD 스타일의 Entity 설계
    - DDD 관점에서 권장되는 구조
    - 도메인 규칙과 정책(비즈니스 규칙)
    - Entity 도메인 규칙
    - 주문 생성시에만 특정 메서드 호출 강제하는 방법



---
## 1. Spring
### < Spring core >
1. Spring Core
    - Spring Framework의 핵심 모듈로, 애플리케이션 개발에 필요한 기본적인 기능들을 제공.
    - 프레임워크의 기본적인 구조와 핵심적인 기능을 담당.
    - Spring Framework의 기반 모듈로, 다른 모듈들이 제공하는 기능들(예: Spring MVC, Spring Data, Spring Security 등)이 모두 이 Core 기능 위에 구축됨.
    - 복잡한 애플리케이션을 보다 간결하고 확장성 있게 만들 수 있도록 함.

    1. 의존성 주입 (Dependency Injection, DI)
        - Spring Core는 의존성 주입을 지원하여 객체 간의 결합도를 낮추고, 테스트 및 유지보수를 쉽게 만듭니다.
        - DI는 객체가 자신이 의존하는 다른 객체들을 외부에서 주입받는 방식입니다. 이를 통해 객체 생성과 의존 관계를 관리할 수 있습니다.

    2. 컨테이너 (Bean Factory, ApplicationContext)
        - Spring Core는 Bean Factory와 ApplicationContext를 제공하여 객체를 관리합니다.
        - Spring의 컨테이너는 애플리케이션에서 필요한 객체들을 생성하고 관리하며, 이 객체들은 '빈(Bean)'이라고 불립니다.
        - ApplicationContext는 BeanFactory를 확장한 형태로, 추가적인 기능(예: AOP, 메시지 리소스)을 제공합니다.

    3. AOP (Aspect-Oriented Programming)
        - Spring Core는 AOP를 지원하여 코드의 비즈니스 로직과는 별개로 공통적인 관심사를 처리할 수 있도록 합니다.
        - 예를 들어, 로깅, 보안, 트랜잭션 관리 등은 AOP를 통해 비즈니스 로직과 분리하여 처리할 수 있습니다.

    4. 트랜잭션 관리
        - Spring은 트랜잭션 관리 기능을 제공하여, 데이터베이스 작업의 일관성을 유지하고 오류 발생 시 롤백할 수 있게 도와줍니다.

    5. 자원 관리
        - Spring Core는 객체의 생성, 초기화, 소멸 등의 생명 주기를 관리하며, 객체가 필요한 자원을 효율적으로 관리할 수 있도록 도와줍니다.


### < 스프링부트 의존성추가 키워드 >
1. Gradle에서 종속성 추가 시 사용가능한 키워드
   - dependencies 
     - Gradle에서 종속 항목을 추가할 때 사용하는 키워드.
     - 종속 항목을 설정하는 블록을 정의.
   - 종속성을 추가하는 유형
     - implementation: 애플리케이션 실행에 필요한 라이브러리.
     - api: 라이브러리의 공개 API를 제공하는 라이브러리.
     - testImplementation: 테스트 코드에서 필요한 라이브러리.
     - compileOnly: 컴파일 시에만 필요한 라이브러리 (실행 시에는 포함되지 않음).
     - runtimeOnly: 실행 시에만 필요한 라이브러리.

2. annotationprocessor vs implementation
   - implementation **(애플리케이션 실행에 필요한 라이브러리 지정.)**
     - 애플리케이션 코드에서 실제로 사용되는 라이브러리나 의존성을 지정할 때 사용.
     - **의존성은 컴파일, 실행 시에 모두 포함.**
     - 다른 모듈이 이 모듈을 사용할 때 필요한 라이브러리로 포함됨.
   - annotationprocessor **(컴파일 타임에만 필요한 의존성 처리.)**
     - **컴파일 과정에서 애너테이션 프로세서를 사용하도록 지정할 때 사용.**
     - 주로 **컴파일 시 자동으로 코드를 생성**하거나 수정하는 역할.   
       ex) Lombok 같은 라이브러리들이 컴파일 시 애너테이션을 처리하는 데 사용됨.
     - 런타임 시에는 필요하지 않으며, 컴파일 타임에만 필요한 의존성을 지정할 때 사용됨.
     - 코드 생성이나 바이트코드 수정을 담당하는 툴들에 필요.
   - 사용 예시   
     Lombok   
     Lombok은 @Getter, @Setter 등의 어노테이션에 대해 **컴파일 시에 자동으로 메서드 생성.**
     ~~~
     // Lombok 애너테이션을 처리하기 위해 컴파일 시 애너테이션 프로세서를 추가   
     compileOnly 'org.projectlombok:lombok'   
     annotationProcessor 'org.projectlombok:lombok'   
     ~~~

3. org.springframework.boot:spring-boot-starter-web 의존성추가
   - 참고블로그 https://priming.tistory.com/144


### < Spring 어노테이션 >
1. @RequestHeader
    - public @interface RequestHeader.java 파일 오픈해서 확인.
    - 메서드 매개변수가 웹 요청 헤더에 바인딩되어야 함을 나타내는 애너테이션.
    - 매서드 매개변수가 Map<String, String>, MultiValueMap<String, Strng>, HttpHeaders 이면 Map은 모든 헤더 이름과 값으로 채워짐.
   ~~~
    @PostMapping("/signout")
    public ResponseEntity<String> signOut(@RequestHeader Map<String, String> headers) {
        String authority = headers.get("authorization");
        // ...
    }
   ~~~
    1. @Name
        - The name of the request header to bind to.   
          요청 헤더에 바인딩할 이름 선택가능.
       ~~~
         @PostMapping("/signout")
         public ResponseEntity<String> signOut(@RequestHeader @Name("Authorization") String authorization) {
             // ...
         }
       ~~~
    2. Spring core에 있는 상수를 사용
     ~~~
       public ResponseEntity<String> signOut(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
           // ...
       } 
     ~~~


---
## 2. 라이브러리
### < 매핑 라이브러리 MapStruct >
- MapStruct
    - 자바 Bean 매핑 라이브러리로, 컴파일 타임에 매핑 코드를 자동으로 생성.    
      이 과정은 annotationProcessor를 통해 이루어짐.   
      MapStruct는 @Mapper 애너테이션을 처리하여 매핑 인터페이스에 대한 구현체 생성.

- mapstruct-processor 
  - mapstruct-processor를 annotationProcessor로 의존성 추가하는 이유    
    MapStruct가 annotationProcessor를 사용하여 컴파일 타임에 코드를 생성하기 때문.
  - MapStruct가 실행 시에는 필요하지 않지만,
    컴파일 시에 코드 생성을 위해 사용되므로 런타임에 포함될 필요가 없기에 annotationProcessor로 지정해야함.
    즉, **성능 최적화**를 위해서 implementation이 아닌 annotationProcessor로 설정하면 
    mapstruct-processor가 컴파일 중에만 실행되고 실행 파일에 포함되지 않는다.    
    따라서 애플리케이션의 실행 시 성능에 영향을 미치지 않는다.

- mapstruct 와 mapstruct-processor
  - **mapstruct 라이브러리 자체는 애플리케이션에서 실행 시 사용되고
    mapstruct-processor는 컴파일 시에만 작동하여 매핑 코드가 생성.**
   ~~~
   dependencies {
       implementation 'org.mapstruct:mapstruct:1.5.2.Final'  
       annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.2.Final'  
   }
   ~~~
  

### < 유효성 검사 라이브러리 validation >
1. 작동방식
   - 지원하는 어노테이션으로 클래스의 필드에 대한 제약조건을 정의.
   - Validator에 객체를 전달해 제약 조건이 충족되는지 확인하여 객체의 유효성 검증.
     ~~~
     Set<ConstraintViolation<Input>> violations = validator.validate(customer);
     if (!violations.isEmpty()) {
         throw new ConstraintViolationException(violations);
     } 
     ~~~
   - @Validated, @Valid 어노테이션 - 검증트리거
     - 사용 시 대부분 스프링이 대신 검증해주므로 Validator 직접 생성할 필요 없음.
     - @Validated
       - Spring에 해당 어노테이션이 달린 클래스의 메서드로 전달되는 ***매개변수를 검증하도록 지시***하는 데 사용할 수 있는 **클래스 수준의 어노테이션.**
     - @Valid
       - **메서드 매개변수와 필드**에 주석을 달아서 Spring에 메서드 매개변수나 필드의 유효성을 검사.
     - Spring MVC 컨트롤러에 대한 입력 검증가능.
       - Spring REST Controller 구현 시 클라이언트가 전달한 입력 검증 가능.

2. 가장 일반적인 검증 어노테이션
    - @NotNull
        - 필드가 null이어서는 안 됨을 나타냅니다. (null허용x)
    - @NotEmpty
        - 목록 필드는 비워둘 수 없음을 나타냅니다. (null, 빈문자 허용x)
    - @NotBlank
        - 문자열 필드는 빈 문자열이어서는 안 됩니다. (null, 빈문자, 공백 허용x / 최소한 하나의 문자가 있어야 함)
        - 문자열에 대한 유효성 검사를 수행하는 어노테이션이므로 BigDecimal과 같은 숫자 타입에는 사용이 불가능.
    - @Min, @Max
        - 숫자 필드는 값이 특정 값보다 크거나 작을 때만 유효함을 나타냅니다.
    - @Pattern
        - 문자열 필드는 특정 정규 표현식과 일치할 때만 유효함을 나타냅니다.
    - @Email
        - 문자열 필드는 유효한 이메일 주소여야 함을 나타냅니다.
    - @Size 
        - 해당 제약 조건은 문자열, 배열, 컬렉션 타입 등에 대해 길이 검사(validation)을 수행.
        - Enum 타입인 경우, @Size는 이 타입에 대해 미작동.
3. 검증 가능한 HTTP 요청
   1. Request Body
      - 검증 실패 시 MethodArgumentNotValidException 예외를 발생시키며, HTTP 상태코드 400으로 응답 반환.
      ~~~
      class Input {  
          @Min(1)
          @Max(10)
          private int numberBetweenOneAndTen;
        
          @Pattern(regexp = "^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$")
          private String ipAddress;
        
          // ...
      }
      ~~~
      ~~~
      @RestController
      class ValidateRequestBodyController { 
      
          // Spring은 들어오는 JSON을 Java 객체인 Input에 자동
          @PostMapping("/validateBody")
          ResponseEntity<String> validateBody(@Valid @RequestBody Input input) {
              return ResponseEntity.ok("valid");
          }
      } 
      ~~~
      
   2. 경로 내 전달되는 변수    
      ex) /shop/{id} 에서 id를 지칭.   
   3. 쿼리 매개변수      
      - 경로 변수와 요청 매개변수의 유효성 검사 request body와는 다르게 작동.   
        이 때는 복잡한 Java 객체의 유효성을 검사하지 않음.
      - 검증 실패 시 ConstraintViolationException 예외를 발생시키며, HTTP 상태코드 500으로 응답 반환.
      - HTTP 상태 400을 반환하려는 경우 컨트롤러에 사용자 정의 Exception Handler 추가해 처리가능.
      ~~~
      @RestController
      @Validated
      class ValidateParametersController {

          @GetMapping("/validatePathVariable/{id}")
          ResponseEntity<String> validatePathVariable(
              @PathVariable("id") @Min(5) int id) {
                  return ResponseEntity.ok("valid");
          }

          @GetMapping("/validateRequestParameter")
          ResponseEntity<String> validateRequestParameter(
              @RequestParam("param") @Min(5) int param) {
                  return ResponseEntity.ok("valid");
          }
      }
      ~~~
      ~~~
      // 통합 테스트코드
      @ExtendWith(SpringExtension.class)
      @WebMvcTest(controllers = ValidateRequestBodyController.class)
      class ValidateRequestBodyControllerTest {
   
          @Autowired
          private MockMvc mvc;
    
          @Autowired
          private ObjectMapper objectMapper;
           
          @Test
          void whenInputIsInvalid_thenReturnsStatus400() throws Exception {
              Input input = invalidInput();
              String body = objectMapper.writeValueAsString(input);
         
              mvc.perform(post("/validateBody")
                             .contentType("application/json")
                             .content(body))
                             .andExpect(status().isBadRequest());
         }
      }     
      ~~~ 
      ~~~
      // Exception Handler
      @RestController
      @Validated
      class ValidateParametersController {
    
          // request mapping method omitted 
          @ExceptionHandler(ConstraintViolationException.class)
          @ResponseStatus(HttpStatus.BAD_REQUEST)
          ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
              return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
          }
      }       
      ~~~

4. 컨트롤러 수준에서 입력을 검증함과 동시에 모든 Spring 구성 요소에 대한 입력을 검증도 함께하려는 경우.
   - @Validated, @Valid 조합으로 사용.
   ~~~
   @Service
   @Validated
   class ValidatingService{
       void validateInput(@Valid Input input){
         // do something
       } 
   }
   ~~~
   - @Valid 적용 객체의 필드 객체에 대해서도 유효성 검증을 수행하는 방법 
     - @Valid 어노테이션은 객체 내부에 포함된 객체에 대해서도 유효성 검증을 수행하려면, 필드에 대해서도 @Valid 어노테이션을 명시해야 합니다.     
       간단히 말해서, @Valid는 재귀적으로 유효성 검증을 하지 않기 때문에
       중첩된 DTO(예: 상품 옵션 DTO)가 있는 경우, 그 DTO에도 @Valid를 적용해야만 유효성 검증이 수행됩니다.
    - ex) RequestProductionDto.java
   
5. JPA Entity 검증 (Bean Validation 안티 패턴)
   - 검증을 위한 마지막 방어선은 지속성 계층임.
   - 일반적으로 지속성 계층에서 가장 늦게 검증을 하지 않는다.    
     비즈니스 코드가 잠재적으로 유효하지 않은 객체와 함께 작동하여 예상치 못한 오류를 초래할 수 있기 때문임.

6. SpringBoot 사용한 사용자정의 검증 (어노테이션 생성)
   - 어노테이션 생성
     ~~~
      @Target({ FIELD })
      @Retention(RUNTIME)
      @Constraint(validatedBy = IpAddressValidator.class)
      @Documented
      public @interface IpAddress {
    
          String message() default "{IpAddress.invalid}";
    
          Class<?>[] groups() default { };
    
          Class<? extends Payload>[] payload() default { };
    
      }
     ~~~
     - @Constraint
       - validatedBy 옵션을 이용해 ConstraintValidator를 구현한 클래스 참조를 명시. 런타임에 ConstraintValidatorFactory를 구현한
   
   - 검증 구현
     ~~~
      class IpAddressValidator implements ConstraintValidator<IpAddress, String> {
    
          @Override
          public boolean isValid(String value, ConstraintValidatorContext context) {
              Pattern pattern =
                  Pattern.compile("^([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})$");
              Matcher matcher = pattern.matcher(value);
              try {
                  if (!matcher.matches()) {
                      return false;
                  } else {
                      for (int i = 1; i <= 4; i++) {
                          int octet = Integer.valueOf(matcher.group(i));
                          if (octet > 255) {
                              return false;
                          }
                      }
                      return true;
                  }
              } catch (Exception e) {
                  return false;
              }
          }
      }   
     ~~~
     - ConstraintValidator
       - 구현체는 ConstraintValidator 인터페이스를 상속받아 구현.
     - ConstraintValidator<A, T>
       - 주어진 객체 유형 T에 대해 제약 조건 A를 검증하는 논리를 정의.
       - T는 파라미터화되지 않은 유형으로 해결되어야 함. (A : 구현이 처리하는 애너테이션 유형)
       - T의 제네릭 파라미터는 한정되지 않은 와일드카드 유형이어야 함. (T : 구현이 지원하는 대상 유형)
       - 정의한 어노테이션 인터페이스, Validation에 사용할 Type의 class type.
     - isValid()
       - ConstraintValidator 인터페이스에 정의된 필수 구현 메서드.
       - 실제 Validation에 사용할 코드는 isValid 를 통해 구현.
     - initialize()
       - ConstraintValidator 인터페이스에 정의된 메서드.
       - isValid(Object, ConstraintValidatorContext) 호출을 준비하기 위해 검증기를 초기화.
       - 검증 인스턴스가 사용되기 전에 반드시 우선호출됨.
       - 주어진 제약 선언에 대한 제약 애너테이션이 전달
       - default(인터페이스 메서드에 기본 구현을 제공하는 키워드)로 기본 구현은 아무 작업도 수행하지 않는(no-op) 구현 
     - @SupportedValidationTarget 
       - 제약 검증기가 교차 매개변수 제약을 지원하는지 표시하는 데 사용가능.
     - 참고블로그 https://jsy1110.github.io/2022/enum-class-validation/
   
   - 어노테이션 사용
   ~~~
    class InputWithCustomValidator {
        @IpAddress
        private String ipAddress;
    
        // ...
    }
   ~~~

7. 프로그래밍 방식으로 검증   
   - 하단의 링크 참조         
     https://reflectoring.io/bean-validation-with-spring-boot/


---
## 3. 유효성 검증
### < 정책과 보호 >
1. 정책과 보호의 판단 기준
   1) 도메인이 규칙을 보장해야 한다.    
      해당 규칙이 깨지면 비즈니스가 성립하지 않는 경우, 정책으로 취급.   
      예를 들어 비밀번호 형식이 틀리면 인증, 알림, 본인 확인이 불가해 비즈니스가 붕괴된다.
      반면 아이디를 30자로 제한했지만 31자리가 되더라도 대부분의 비즈니스에 영향이 없다.
      이 때 비밀번호는 정책으로 관리해야 한다.
   2) 입력 채널이 바뀌어도 동일하게 적용돼야 하는가?   
      변경되지 안항야 한다면 정책이다.   
      전화번호는 API, Batch, Admin, Event 등에서 호출하더라도 항상 동일하게 적용되어야 한다.
      아이디는 DB, UI, 기술 스택에 따라 바뀔 수 있다.
   3) 왜 해당 규칙이 존재하하는지 설명 가능한가?
      비즈니스 언어로 설명 가능하면 정책이다.
      전화번호 패턴의 경우, 대한민국 휴대폰 정책 때문에 정책으로 관리해야 한다.   
      비밀번호 길이의 경우, 보안 정책 때문에 관리가 필요하다.   
      반면, 아이디의 경우, DB 컬럼이 30자라서 제한한다는 건 모호하다.    
      필수로 지켜야하는 일이라 보기 힘들기 때문이다. 길이 제한의 이유가 기술적일 가능성이 크다.

2. 정책과 보호의 모호함 제거 장치
   1) 규칙을 정책으로 메서드명으로 명명   
      가장 흔하고 효과적인 방법이다.
      정책은 반드시 이름(policy)으로 고정한다.
      ~~~
      validatePasswordPolicy(password);
      validatePhoneNumberPolicy(phoneNumber);
      ~~~
      반대로 보호는 아래와 같이 작성한다.
      ~~~
      validateUserIdLengthForStorage(userId);            
      ~~~
   2) 정책과 보호의 코드 위치 고정   
      구조를 통해 의사결정을 고정한다.
      입력 보호는 validation으로 dto에, 비즈니스 규칙은 도메인(service)에 위치시킨다.
   3) 주석이 아닌 커밋 메시지로 결정 기록을 남긴다.
      ~~~
      ADR-004: Password Validation Policy Location

      - 비밀번호 정책은 Service/Domain 책임으로 한다.
      - DTO에는 NotBlank만 둔다.
      - 이유:
          - 입력 채널 확장
          - 정책 변경 빈도
      ~~~
   4) 테스트를 정책 문서로 만든다.
      테스트 이름 자체가 정책 선언이다.
      ~~~
      @Test
      void password_policy_requires_8_to_100_characters() {
      assertThrows(MemberException.class,
          () -> Password.from("short"));
      }
      ~~~
   5) Value Object로 정책을 구조에 묶는다. (최종형)
      모호함이 없다. 
      해당 Value Obejct를 생성하지 못한다는 것은 정책을 위반하는 것으로 판단한다.
      정책과 보호에 대한 위치 논쟁이 생길 수 없다.


### < DTO Validation과 Service 정책 검증의 책임 분리 기준 >
- 문제상황   
  비밀번호 길이 및 일치여부 체크, 전화번호 중복 체크의 경우  
  회원가입 서비스 로직에서 일회성으로만 사용할 로직이라 커스텀 어노테이션을 생성해 dto에 적용하지 않았다.
- 권장방법
  - 비밀번호 길이 및 일치여부 체크의 경우, 커스텀 Validator 생성 권장 가능.
    (단, 추후 2곳 이상에서 재사용하면 고려)
  - 전화번호 중복 체크의 경우, 서비스 로직에 유지.

1. DTO Validation으로 생성해야 하는 것  
   ***요청이 형식적으로 유효한가를 검증.***
    - 형태/포맷/길이/필수값
2. Service 로직에 남아야 하는 것   
   ***현재, 해당 비즈니스를 수행해도 되는가를 검증.***
    - DB 조회 필요   
      경쟁 조건 발생 가능, 트랜잭션 락 이슈 등 존재.
    - 비즈니스 규칙
    - 조건이 복잡하고 맥락 의존적인 경우
3. 커스텀 Validaiton 생성 가치가 있는 경우
    - 2곳 이상에서 재사용하는 경우
    - 선언적으로 표현하면 의도가 더 명확한 경우
    - DTO 내부 정보만 사용하는 경우
    - 테스트 가치가 있는 경우


### < 잘못된 Service Validation Check >
1. 문제상황   
   ***Service에서 프로그래밍 오류(null 전달) 를 비즈니스 오류처럼 처리하고 있다.***
   기본적으로 Controller에서 null 객체가 들어오는 것은 비즈니스 오류가 아닌 서버 내부 계약 위반일 뿐이다.
   따라서 ***서비스에서 더블 체크하는 경우***는 개발자의 실수거나 API 사용 오류로 정상 시나리오가 아니라 프론트가 이해할 필요가 없다.
   이 떄 서버 내부 사용 오류(프로그래밍 오류)를 ***Validation Exception을 던지는 것은 옳지 않다.***
   이는 심지어, validation Exception을 비즈니스 에러로 관리하는 것으로 잘못된 방어이다.
   ~~~
    // 회원가입 validation check
    private void saveMemberValidationCheck(RequestMemberDto member) {
        // 회원 객체 존재여부 validation check
        if(ObjectUtils.isEmpty(member)) {
            throw new MemberException(EMPTY_MEMBER_INFO);
        }
        // ...
    }
   ~~~
2. 해결방법   
   비즈니스 예외를 쓰지 말자.
   ***메서드 사용 오류인 IllegalArgumentException을 이용***하자.   
   하지만, 실무 코드에서 Service에서도 HTTP Status 담긴 예외 던지는 경우 많음.
   그래서 “이게 왜 문제지?”가 자연스럽지만, 구조적으로 깔끔한 기준은 분명히 존재한다.   
   시스템 에러는 “관리 대상”이 아니라 “조기 발견·즉시 실패 대상”이기 때문에
   보통 Enum으로 관리하지 않고 하드코딩한다.
   ~~~
   public void saveMemberValidationCheck(RequestMemberDto member) {
       if (member == null) {
           throw new IllegalArgumentException("RequestMemberDto must not be null");
       }
      // ...
   }
   ~~~

3. Service 방어는 중요.  
   서비스는 거의 항상 재사용되므로 방어가 중요하다.   
   예를 들어 배치, 스케줄러, 이벤트 리스너, 다른 서비스 호출, 테스트 코드 등 Controller를 거치지 않는 경우가 훨씬 많기 때문이다.   
   방어가 없다면, NPE 발생 위치가 애매해지고, 로그로 원인 추적이 어려워 장애 시 원인 분석 시간이 증가된다.  
   따라서 초기 방어가 디버깅 비용을 줄인다.


### < 전화번호 검증 중복으로 인한 구조적 문제와 개선 방향 >
DTO와 Service에서 동일한 패턴을 중복 관리하는 구조는 장기적으로 반드시 깨진다.
책임을 분리해 중복을 제거해야한다.

1. 현재 구조의 문제점    
   현재 request DTO와 Service에서 동일한 전화번호 패턴에 해대 검증을 수행하고 있다.
   이 상태에서 정책 변경 시, 한쪽만 수정되고 테스트는 통과되면서 운영에서 버그가 발생할 가능성이 높아진다.

2. 해결방법   
   DTO는 입력 필터이고 Service는 도메인 규칙이므로 레벨이 다르다.
   따라서 같은 정규식이라도 의미가 다를 수 있으므로 패턴을 공유하는 방식으로 해결하면 안된다.
    1) ***Controller DTO는 느슨하게, Service는 단일 진실*** (Best)   
       Controller validation은 1차 필터, Service는 진짜 책임자 역할을 수행하도록 한다.   
       DTO는 형식만 검증해 전화번호처럼 생긴 입력만 통과 가능하도록 한다.
       Service나 도메인은 정책의 단일 진실이다.
       이 방법은 정책 변경 시 Service만 수정하면되고, DTO는 거의 바뀌지 않는다.
       테스트도 도메인 기준으로만 작성하면 된다.
       정책 변경이 잦은 도메인, 외부 API / 앱 / 백오피스 등 입력 채널이 여러 개인 경우,
       테스트/도메인 주도 설계를 중시하는 팀에서 거의 표준처럼 쓰인다.
       정책 변경 대응력이 높고, 입력 채널 증가에 유리하며 테스트 용이성과 중복 제거의 장점을 가지기 때문이다.

    2) 전화번호 Value Obejct로 캡슐화   
       가장 이상적인 방법으로 정책, 정규화, 검증을 한 곳에서 수행한다.
       ~~~
       public class PhoneNumber {
           private final String value;
 
           private PhoneNumber(String value) {
               this.value = value;
           }
 
           public static PhoneNumber from(String raw) {
               String normalized = raw.replaceAll("[^0-9]", "");
 
               if (!normalized.matches(PHONE_REGEX)) {
                   throw new MemberException(INVALID_PHONE_NUMBER);
               }
 
               return new PhoneNumber(normalized);
           }
       }
       ~~~

3. 구조를 개선하지 않을 때 최소 조치    
   리팩터링 비용 부담과 다른 기능의 우선순위로 인해 구조를 개선하지 않고 현 상태를 유지하고자 한다.   
   이 떄, 임시 구조임을 명확히 표시하고 깨질 가능성을 최소화하는 장치를 해야한다.
    1) Service를 단일 진실로 만듦    
       DTO 패턴을 완화하거나 최소한 주석으로 의도를 남긴다.
       주석으로는 Service와 중복, 임시 구조임을 명시하고 의사결정을 기록한다.
       ~~~
       // RequestMemberDto.java 
       @Size(min=10, max=11, message = "{validation.member.telephone.size}")
       @Pattern(regexp = "^01[016789]\\d{7,8}$",
       message = "{validation.member.telephone.pattern}")
       // NOTE: 데이터 정규화를 위해 실제 전화번호 정책 검증은 Service에서 수행한다.
       // TODO: 전화번호 정책이 확장되면 telephone을 Value Object로 분리 예정
       private String telephone;
       ~~~
    2) Service의 정규식 상수화    
       상수화를 통해 수정 포인트를 단일화하고 테스트에서 재사용 가능해진다.
       ~~~
       private static final String PHONE_MEMBER_REGEX = "^01[016789]\\d{7,8}$";
       ~~~
    3) 테스트로 정책 고정   
       테스트가 문서 역할을 함.
       ~~~
       @ParameterizedTest
       @ValueSource(strings = {
           "010-1234-5678",
           "01012345678"
       })
       void valid_phone_numbers(String input) {
           assertDoesNotThrow(() -> service.normalizeAndValidate(input));
       }
       ~~~


### < 검증관련처리 > 
1. DTO에서 처리
   > 단순한 검증 로직은 DTO에서 @Valid 어노테이션을 사용.   
     기본적인 @NotNull, @Size, @Pattern 등의 검증 어노테이션으로 처리하는 것이 더 효율적.
   - 클라이언트와 서버 간의 빠른 피드백 제공
     - DTO에서 검증을 수행 시 클라이언트가 서버에 요청을 보내기 전에 빠르게 유효성 검사 수행가능.    
     - @NotNull, @Size 등의 어노테이션을 사용하여 필드의 값이 유효한지 확인할 수 있기 때문에, 잘못된 데이터를 보내는 것을 방지 가능.
     - 클라이언트는 서버로부터 응답을 받기 전에 자신이 보낸 데이터가 잘못된지 빠르게 알 수 있음. 
     - 사용자 경험을 개선하는 데 중요한 역할 수행.
   - 비즈니스 로직과 검증 로직의 분리
     - DTO에서 검증을 처리하면 비즈니스 로직과 검증 로직을 명확히 분리가능 -> 코드가 깔끔하고 관리에 용이. 
     - 서비스 계층에서 비즈니스 로직만 처리하고, DTO에서 유효성 검증을 전담.
     - 비즈니스 로직에서 DTO 객체를 바로 사용할 수 있기 때문에, 유효성 검증이 빠르게 이루어지고, 불필요한 추가적인 검증 코드가 서비스 계층에 포함되지 않게 함.
   - 표준화된 검증 처리 (Spring Validation과 같은 프레임워크를 사용 시)
     - 검증 로직을 표준화된 방식으로 처리가능. 
     - 각 필드에 대한 검증 로직이 명확하고 일관되게 처리.
     - 다양한 검증 어노테이션(@NotNull, @Size, @Pattern 등)을 사용하여 검증 로직을 간단하고 명확하게 작성가능.
   - 중복 코드 최소화
     - 필드별로 여러 검증 조건을 추가 시, DTO에서 @Size, @NotBlank, @Pattern 등 여러 어노테이션을 조합하여 사용가능. 
     - 각 검증 로직을 서비스나 컨트롤러에서 중복해서 작성할 필요 x.
     - 특히 여러 엔드포인트에서 동일한 DTO를 사용하는 경우, DTO에 검증을 추가하면 각 엔드포인트마다 검증 로직을 반복하지 않아도 됨.
   - 쉽고 빠른 디버깅
     - DTO에서 검증을 시 요청이 서비스로 전달되기 전에 유효성 검사 실패로 인해 문제가 발생하는 지점을 빠르게 파악가능. 
     - 예외가 발생하면 검증에 실패한 필드가 명시적으로 반환되어, 디버깅이 용이.
   - 복잡한 비즈니스 로직 검증 불가
     - 비즈니스 로직에 기반한 복잡한 검증처리 어려움. 
     - 여러 필드의 값들이 특정 조건에 따라 서로 관계를 가져야 하는 경우(예: startDate가 endDate보다 이전이어야 한다면) DTO에서 처리하는 것은 한계가 있을 수 있음.
   - 검증 로직의 변경 시 코드 수정 필요
     - DTO에서 검증을 하게 되면, 검증 로직이 DTO에 결합되므로 검증 로직을 변경하려면 DTO를 수정 필요. 
     - 비즈니스 로직에서만 사용하는 복잡한 검증 조건이 있을 경우, 이 검증을 DTO에 포함시키는 것이 코드의 복잡도를 증가시킬 수 있음.
     - 사용자 정의 검증 어노테이션을 추가하려면 별도의 클래스 생성 필요.
     - 이를 관리하고 변경하는 데 추가적인 유지보수 작업이 필요할 수 있음.
   - 테스트 어려움
     - DTO에서 유효성 검증을 수행하는 경우, 검증 로직을 테스트하는 데 추가적인 설정이 필요할 수 있음. 
     - 테스트 코드에서 검증 실패를 감지하고, 적절한 예외 처리를 할 수 있도록 해야 하기 때문에, 검증 로직을 독립적으로 테스트하는 과정이 번거로울 수 있음.
     - @Valid나 @Validated를 사용하는 경우, 이를 테스트하려면 Spring의 @WebMvcTest나 @SpringBootTest와 같은 테스트 어노테이션을 사용해야 하므로, 단위 테스트가 어려울 수 있음.
   - 한정된 유효성 검증
     - DTO의 유효성 검증은 단순히 필드 값을 체크. 
     - 복잡한 데이터 검증이나 외부 서비스와의 연동 검증 등은 DTO에서 처리 어려움. 
     - 특정 필드 값이 외부 API나 데이터베이스의 값을 기반으로 해야 할 때, 이를 DTO에서 처리하기 어려움.
     - 조건부 검증(예: if 조건에 따라 다른 검증을 해야 하는 경우)은 DTO에서 구현 어려움.
   - 어노테이션에 의존적 
     - DTO에서 검증을 어노테이션 기반으로 하게 되면, 어노테이션의 제한된 기능에 의존하게 됨. 
     - 복잡한 규칙이나 특정 조건을 반영하는 검증 로직은 어노테이션을 사용하여 구현이 어려움. 

2. 서비스 로직에서 처리
   > 복잡한 비즈니스 로직 검증이나 특정 조건에 맞는 검증 로직은 서비스 계층에서 처리하는 것을 권장.    
     이는 유연성과 확장성을 높일 수 있음.
   - 유연성
     - 비즈니스 로직에서 필요한 추가 검증을 세밀하게 처리할 수 있습니다. 
   - 분리된 책임
     - DTO는 데이터를 표현하고 검증하는 데만 집중하고, 비즈니스 로직은 서비스 계층에서 별도로 처리할 수 있습니다. 각 계층의 책임이 명확하게 분리됩니다.
   - 복잡한 검증 로직 처리
     - DTO에 정의하기 힘든 복잡한 비즈니스 검증 로직을 서비스에서 처리할 수 있습니다.
   - 검증 코드 중복
     - 여러 서비스에서 동일한 검증을 반복적으로 작성할 경우 코드가 중복될 수 있습니다.
   - 가독성 저하
     - 서비스 로직이 커질 수 있으며, 검증 로직이 서비스 코드에 섞여 들어가면 가독성이 떨어질 수 있습니다.

3. 사용자 정의 검증 어노테이션 생성
   > 사용자 정의 검증 어노테이션을 사용하는 것은 복잡한 검증 로직을 DTO에서 처리하고 싶을 때 유용.   
     이 방법은 여러 곳에서 재사용 가능.
     DTO에 검증 로직을 명시적으로 포함시킬 수 있어 코드의 일관성을 높일 수 있음.
   - 한 곳에서 검증 처리
     - 검증 로직을 별도의 클래스로 분리할 수 있기 때문에 코드 중복을 줄일 수 있습니다. 또한, 여러 필드에 동일한 검증 로직을 재사용할 수 있습니다.
   - DTO에서 검증 로직을 처리
     - 검증을 DTO 내에서 처리하기 때문에, 클라이언트가 요청할 때 한 번에 데이터 검증을 완료할 수 있습니다. 이를 통해 서비스 로직이 더 깔끔해질 수 있습니다.
   - 편리한 재사용성
     - 복잡한 검증을 @Valid와 결합하여, 다른 DTO에서도 동일한 검증 로직을 재사용할 수 있습니다.
   - 복잡성 증가
     - 사용자 정의 어노테이션을 만들고, 검증 로직을 작성하는 데 시간이 소요됩니다. 또한, 너무 많은 사용자 정의 어노테이션이 존재하면 프로젝트가 복잡해질 수 있습니다.
   - 제한된 검증
     - DTO의 필드에 대해 간단한 유효성 검증은 @NotBlank, @Size 등의 내장 어노테이션을 사용하면 되지만, 비즈니스 로직에서 필요로 하는 복잡한 검증은 사용자 정의 어노테이션으로 처리하기 어려운 경우가 있을 수 있습니다.


### < PathVariable에 대한 @NotNull 검증 제외 판단 >
- 참조 : ProductionController.searchDetailProduction()
1. 검증 제외 판단 사유 
   - PathVariable은 요청 레벨에서 차단되어 Null 도달 불가
   상품 상세 조회는 GET /production/{id} 형태로 요청된다.
   이 떄, id 값은 PathVariable로 전달된다.
   Spring MVC는 PathVariable을 반드시 바인딩해야 하므로, 
   값이 없을 경우 컨트롤러 메서드에 진입하기 전에 요청이 실패한다.
   즉, PathVariable 특성상 id 값이 누락된 요청은 컨트롤러 진입 이전에 404(Not Found) 또는 400(Bad Request) 처리된다.
   이로 인해 id == null 상태는 사실상 발생할 수 없기에 @NotNull 검증은 불필요.
2. 대안 검증 제시
   - @Positive
   추가적으로 상품 ID는 양수라는 도메인 제약을 가지므로,
   @NotNull을 이용한 단순 존재 여부 검증 보다는 값의 범위 및 형식 검증이 더 의미있고 적절한 선택이다.
   ex) @Positive : 값이 0보다 큰지 검증.


### < PathVariable 검증 >
1. 문제점   
   PathVariable 필드 유효성 검증이 수행되는지 확인하고 싶어서 pathVariable 유효성 검증 테스트코드를 작성해보았다.
   이 떄, PathVariable 필드에 유효성 검증 어노테이션을 적용해 
   컨트롤러 진입 전에 검증 실패를 기대했으나 유효성 검증이되지 않음.

2. 문제의 원인   
   Method Parameter Validation은 기본으로 활성화되지 않는다.
   @PathVariable Validation이 동작을 위해서는 Controller에 @Validated 어노테이션을 필수 적용해야 함.
   1) @Validated   
      빈에 대해 메서드 파라미터 검증을 활성화하는 어노테이션.
      컨트롤러 클래스 레벨에 적용해야 정상 동작.
      빈 자체가 Validation 대상이 되어야하므로, 메서드나 필드에 적용하면 안된다.

3. 문제해결방법   
   ~~~
   @RestController
   @RequestMapping("/production")
   @RequiredArgsConstructor
   @Validated
   public class ProductionController {
       /**
       * 상품상세 조회 get /production/{id}
       **/
       @GetMapping("/{id}")
       public ResponseEntity<ResponseSearchDetailProductionDto> searchDetailProduction(
                            @PathVariable @Positive Long id) {
           return ResponseEntity.ok(
                   productionService.searchDetailProduction(id));
       }
   }
   ~~~


### < DTO 검증 실패 vs PathVariable/RequestParam 검증 실패 >
두 검증은 검증 시점, 계층, 대상, 에러 구조가 다르기 때문에 Spring이 의도적으로 다른 예외를 사용한다.   
따라서 필드에 동일한 @Positive 어노테이션을 사용해도 발생하는 예외타입도 다르다.

| 구분                  | DTO 검증                                                        | PathVariable / RequestParam 검증                                     |
|:--------------------|:--------------------------------------------------------------|:-------------------------------------------------------------------|
| 검증 주체               | Spring MVC + Bean Validation                                  | Bena Validation                                                    |
| 검증 시점               | 컨트롤러 메서드 진입 직전                                                | ArgumentResolver 단계                                                |
| 실패 예외               | MethodArgumentNotValidException <br> (Spring 메서드 인자 검증 전용 예외) | ConstraintViolationException <br> (Bean Validation 메서드 파라미터 제약 위반) |
| 바인딩 대상              | 객체 전체                                                         | 단일 값                                                               |
| BindingResult 존재 여부 | o                                                             | x                                                                  |
| 에러 개수               | 여러 개 가능                                                       | 보통 1개                                                              |

1. Spring MVC DefaultHandlerExceptionResolver가 400 처리하는 예외    
   Spring MVC에는 기본적으로 DefaultHandlerExceptionResolver가 존재한다.   
   이 클래스에서 아래의 예외들은 아무 설정이 없어도 내부적으로 400으로 처리한다.
   - MethodArgumentTypeMismatchException : PathVariable / RequestParam 타입 불일치
   - MissingServletRequestParameterException : 필수 RequestParam 누락
   - HttpMessageNotReadableException : JSON 파싱 실패
   - BindException : Form/Query 바인딩 실패
   - MethodArgumentNotValidXecption : @Valid @RequestBody 검증 실패
   - ConstraintViolationException : @Valid + PathVariable/Param 검증 실패


### < 유효성검사 정규식 >
- ^[a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ0-9\s]+$
    - ^ : 문자열의 시작을 의미합니다.
    - [ ] : 대괄호 안의 모든 문자는 허용된 문자 범위입니다.
    - a-zA-Z : 영어 대소문자 (a부터 z까지, A부터 Z까지).
    - 가-힣 : 한글 완성형 글자 (한글 음절).
    - ㄱ-ㅎ : 한글 자음 (초성).
    - ㅏ-ㅣ : 한글 모음.
    - 0-9 : 숫자 (0부터 9까지).
    - \s : 공백 문자 (스페이스, 탭, 줄 바꿈 등).
    - + : 앞의 패턴이 하나 이상 반복되는 것을 허용합니다.
    - $ : 문자열의 끝을 의미합니다.
    - * : 앞의 패턴이 0개 이상 반복되는 것을 허용합니다.

### < 비밀번호 유효성검사 정규식 >
- (?= ...)
    - 긍정형 전방 탐색(lookahead)를 의미하며 특정 패턴이 뒤따라야 한다는 조건을 확인하지만, 문자열에서 그 부분을 소비하지(매칭으로 소모하지) 않습니다.
- (?=.[!@#$%^+=-])
    - 문자열에 하나 이상의 지정된 특수 문자가 포함되어 있는지 확인합니다.
- 정규식 참고블로그 https://gocoding.tistory.com/93
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
## 4. git 사용하기
### < slack과 github 연동 >
- 참고블로그 : https://sepiros.tistory.com/37

### < git push 내역 되돌리기 >
1. 로그내역 조회 명령어
   - git log --oneline
   
2. 조회한 로그내역 빠져나가기
   - Q키를 눌러 로그 화면을 빠져 나올수 있다

3. push 내역 되돌리기
   - Git reset을 사용하면 특정 커밋 지점으로 되돌아갈 수 있다.
     ~~~
     git reset --hard "해당commit의해시값"
     ~~~
   - reset hard
     - 돌아갈 시점 이후의 모든 이력을 삭제하는 옵션. 이전에 커밋한 파일의 이력이 모두 사라진다. (commit내역유지)
   - reset soft 
     - 이력을 남겨두는 옵션. 즉 이전에 커밋했던 파일들은 스테이징 상태로 남겨진다. (commit내역 제거, 변경된 파일은 유지)

4. 원격 저장소에 변경사항 반영
   - 원격저장소를 내가 되돌린 상태(현재)와 같아지도록 반영.
   - -f 옵션
     - 해당 옵션 사용 시 강제로 푸시(push)하면 원격 저장소에 현재 로컬 저장소의 상태를 강제로 덮어씌울 수 있다. 
       따라서 이 명령어를 사용할 때에는 주의 필요.
   ~~~
   git push -f origin 브랜치명
   ~~~


---
## 5. 데이터베이스
### < Mysql 데이터타입 결정 >
- 참고블로그 https://dev-coco.tistory.com/54


### < 데이터베이스의 커서(Cursor) >
- 데이터베이스에서 데이터를 읽거나 쿼리 결과를 순차적으로 한 번에 하나씩 처리하기 위한 객체로 포인터 역할을 수행.
- 커서는 결과셋을 탐색하고 각 항목에 접근하는 방법을 제공.
- 특히 데이터베이스 결과가 크거나, 여러 페이지로 나누어 결과를 받아올 때 유용.
- 커서는 데이터베이스에서 쿼리 결과를 효율적으로 탐색하고 처리하는 데 중요한 역할 수행. 
- 커서는 데이터를 순차적으로 읽고, 대량의 데이터를 처리하는 데 필요한 효율성을 제공.

1. 주요기능
   1. 데이터 순차적 접근
      - 커서는 쿼리로 반환된 여러 결과 중 하나씩 접근할 수 있는 방법을 제공.
      - 데이터베이스에서 수천 개의 결과를 반환했을 때, 커서는 결과를 하나씩 읽을 수 있도록 도움.
   2. 커서 위치 지정
      - 커서는 항상 쿼리 결과에서 현재 위치를 추적. 
      - 예를 들어, 커서가 처음에는 첫 번째 항목을 가리키고 있으며, 
        next() 메서드를 호출할 때마다 커서는 하나씩 다음 항목으로 이동.
   3. 메모리 절약
      - 커서를 사용하면 쿼리의 결과를 한 번에 모두 메모리에 로드하지 않고, 
        한 번에 하나씩 또는 일정 크기씩 데이터를 가져오는 방식으로 
        메모리 자원을 효율적으로 사용가능.
   4. 순회 및 탐색
      - 커서는 쿼리 결과를 반복(iterate)하면서 데이터를 처리할 수 있는 기능을 제공. 
      - 예를 들어, MongoDB나 SQL 데이터베이스에서 커서를 사용해 while 문으로 반복문을 작성가능.

2. 커서의 상태
   1. 열려 있는 상태
      - 커서가 데이터베이스에서 데이터를 읽고 있을 때
   2. 닫힌 상태
      - 데이터 읽기가 완료된 후, 또는 명시적으로 커서가 닫혔을 때

3. 커서의 종류
   1. Implicit Cursor (암시적 커서)
      - 데이터베이스가 자동으로 생성하고 관리하는 커서. 
      - 예를 들어, SELECT 쿼리에서 바로 결과를 반환할 때 사용.
   2. Explicit Cursor (명시적 커서)
      - 개발자가 명시적으로 선언하고 관리하는 커서. 
      - 일반적으로 복잡한 데이터 처리나 반복문 사용 시 사용.

4. 커서의 필요성
   1. 대량 데이터 처리
      - 결과셋이 매우 크고 데이터를 한 번에 모두 메모리로 가져오는 것이 비효율적일 때, 
        커서를 사용하여 데이터를 한 번에 하나씩 처리하는 방식이 유용.
   2. 효율적인 메모리 관리
      - 커서를 사용하면 데이터베이스에서 데이터를 한 번에 모두 가져오는 것이 아니라, 
        필요한 만큼씩만 가져와서 메모리 자원을 절약가능.

5. 커서 사용 시 주의사항
   - 커서는 결과를 순차적으로 처리할 때 사용되므로, 커서가 다 끝날 때까지 데이터를 반복해서 처리가능.
   - 커서를 닫지 않으면 자원을 낭비할 수 있으므로, 사용 후 커서를 명시적으로 닫는 것이 중요.

6. SQL에서의 커서 사용 예 (PL/SQL 또는 T-SQL)
   - SQL에서 커서를 사용하면 쿼리 결과를 반복적으로 처리가능. 
   - 예를 들어, PL/SQL에서는 커서를 선언하고 OPEN, FETCH, CLOSE 명령어로 커서를 다룸.
   ~~~
   DECLARE
   CURSOR c_employee IS SELECT * FROM employees;
   emp_record c_employee%ROWTYPE;
   BEGIN
   OPEN c_employee;
   LOOP
   FETCH c_employee INTO emp_record;
   EXIT WHEN c_employee%NOTFOUND;
   -- 데이터 처리
   DBMS_OUTPUT.PUT_LINE(emp_record.name);
   END LOOP;
   CLOSE c_employee;
   END
   ;
   ~~~


### < 커서의 hasNext(), isClosed() >
1. hasNext()
   - 커서가 현재 더 이상 읽을 데이터가 있는지 여부를 확인하는 메서드.
   - 데이터를 처리할 때 사용.
   - cursor.hasNext()는 데이터를 다 읽은 상태에서도 false를 반환가능.
2. isClosed()
   - 커서가 닫혔는지를 확인하는 메서드.
   - 커서 상태를 체크할 때 사용.
   - 커서를 닫았다면 cursor.isClosed()는 true를 반환하고 이 경우 더 이상 데이터를 읽을 수 없음.


### < 커서가 닫힌 것과 읽을 데이터가 없는 것 >
1. 커서가 닫힌 것 (Cursor Closed)
   - 커서는 데이터베이스와의 연결을 통해 데이터를 순차적으로 읽어오는 객체. 
   - 커서가 닫혔다는 것은 커서가 더 이상 데이터를 읽지 않거나, 
     해당 커서 객체가 더 이상 유효하지 않음을 의미.
   - 커서가 닫히면 더 이상 데이터를 가져올 수 없으므로, 데이터를 가져오는 작업이 중단됨. 
   - 그러나 커서가 닫힌 이유는 데이터가 더 이상 없어서가 아닐 수 있음. 
   - 예를 들어 명시적으로 커서를 닫았거나, 연결이 끊어졌을 때도 커서가 닫힐 수 있음.
   
2. 더 이상 읽을 데이터가 없다는 것
   - 이는 단순히 커서가 데이터를 모두 읽었고, 더 이상 읽을 데이터가 없음을 의미.
   - 이 상태에서도 커서는 여전히 열려있을 수 있으며, hasNext() 메서드는 false를 반환. 
   - 즉, 데이터가 더 이상 없다는 것은 커서가 "닫힌 상태"가 아니라 단지 읽을 데이터가 끝났다는 것.
   - 데이터베이스에서 모든 데이터를 읽은 후, 커서는 여전히 열려 있을 수 있음. 
     이 경우 cursor.hasNext()는 false를 반환하며 이때 커서를 닫지 않은 상태일 수 있음.


---
## 6. DTO
### < DTO, VO, MODEL 차이 >
https://010562.tistory.com/11
https://velog.io/@chae_ag/Model-DTO-VO-DAO-%EA%B0%80-%EB%AC%B4%EC%97%87%EC%9D%B8%EA%B0%80%EC%9A%94

### < 도메인과 DTO >
1. 도메인
    - 하나의 비즈니스 업무 영역과 관련된 개념.
    - 실제 비즈니스 로직이나 규칙, 도메인 정보 등을 포함.
2. DTO
    - 계층간의 데이터 전달을 위해 사용하는 객체.
- 참고블로그
  https://shyun00.tistory.com/214


### < 요청과 응답으로 DTO 사용해야하는 이유 >
1. Entity 내부 구현 캡슐화해 은닉
    - Entity
        - 도메인의 핵심 로직, 속성을 가짐.
        - 실제 DB 테이블과 매칭되는 클래스.
    - Entity가 getter, setter를 갖게 되면 controller 같은 비즈니스 로직과 관계없는 곳에서 자원의 속성이 실수로라도 변경될 수 있음.
    - Entity UI 계층을 노출하는 것은 테이블 설계 화면을 공개하는 것과 같으므로 보안상 바람직하지 않음.

2. 화면에 필요한 데이터 선별
    - 요청과 응답으로 Entity 사용 시, 요청하는 화면에서 불필요한 속성까지 전달됨. -> 속도 느려짐.
    - 요청과 응답으로 Entity 사용 시 다양한 요청과 응답에 따른 속성들을 동적 선택 불가.
    - 특정 api에 필요한 데이터를 포함한 DTO 별도 생성 시 화면이 요구하는 필요 데이터들만 선별해 용청과 응답가능.

3. 순환참조 예방
    - JPA로 개발 시 양방향 참조인 경우 순환참조를 조심해야함.
    - 양방향 참조된 Entity를 응답으로 return 하게되면  
      entity가 참조하고 있는 객체 지연로딩 -> 로딩된 객체는 또 다시 본인이 참조하고 있는 객체를 호출
      을 반복하면 무한루프에 빠진다.
    - 순환참조가 일어나지 않도록 응답의 return으로 DTO를 사용하는 것이 순환참조에서 안전.

4. validation코드와 모델링코드 분리가능.
    - Entity 클래스는 DB의 테이블과 매칭되는 필드가 속성으로 선언되어 있고, 복잡한 비즈니스 로직이 작성되어있음.
      띠리서 속성에 @Column, @JoinColumn , @ManyToOne, @OneToOne 등의 모델링을 위한 코드가 추가됨.
      여기에 추가로 @NotNull, @NotEmpty @NotBlank 같은 요청에 대한 값에 대한 validation 코드가 들어가면 Entity 클래스는 더 복잡해지고 가독성이 저하됨.
    - DTO에는 요청에 필요한 validation 정의.
    - Entity 클래스는 모델링과 비즈니스 로직에만 집중가능.

- DTO를 모든 API마다 구별해서 만들다보면 너무 많은 DTO가 생겨서 관리하기 어렵다고 하기도 한다.
  그럼에도 불구하고 요청과 응답으로 엔티티를 사용하면, 개발의 편리함을 얻는 대신 애플리케이션의 결함을 얻게 될 수 있다.
  API 스펙과 엔티티 사이에 의존성이 생기는 문제도 간과할 수 없다
  UI와 도메인이 서로 의존성을 갖zx지 않고 독립적으로 개발하는 것을 지향하기 때문에 이를 중간에서 연결시켜주는 DTO의 역할은 꽤나 중요
  요청과 응답으로 DTO를 사용하면 각각의 DTO 클래스가 데이터를 전송하는 클래스로서의 역할을 명확히 가질 수 있게 되고, 이는 하나의 클래스가 하나의 역할을 해야 한다는 객체지향의 정신과도 부합

- 참고블로그   
  https://shyun00.tistory.com/214

  
### < request, response DTO 분리 >
- client->controller에 들어오는 request 정보와 controller->client로 내보내는 response는
  사용하는 목적이 다르고 전달하는 데이터도 다르기에 분리하는 것이 좋다.
- DTO는 주고받는 데이터를 정의한 api 명세서와 다름없다.
- 단건, 복수 건 조회 정도만 동일 DTO 사용으로 운명을 함께할 수 있다.

1. request DTO   
   validation check 어노테이션을 사용 -> 데이터의 유효성 검사.
2. response DTO   
   단순히 데이터를 반환.


### < Value Object (VO) >
1. Value Object (VO)
   식별자가 없고 값 그 자체로 의미를 가지는 객체.
   여러 값이 따로는 의미가 없고 함께 있을 때만 의미가 있는 경우 VO로 생성한다.
   1) VO 특징   
     id가 없고 불변의 특성이 있으며 equals/hashCode가 값의 기준이 된다.
     - 모든 필드가 private final
     - setter 없음
     - 값 기반 equals/hashCode 자동
     - 생성자도 있음
   2) VO 사용 의의   
      여러 값이 따로는 의미가 없고 함께 있을 때만 의미가 있는 경우 VO로 생성하므로,
      productId-optionCode 조합을 임의로 생성해 데이터를 구분할 필요가 없어진다.   
   ~~~
    public record ProductOptionKey(
            Long productId,
            String optionCode
    ) {}
   ~~~

2. record
   VO 생성을 위한 자바 문법.
   Java 16 버전부터 지원한다.
   위 코드로 생성자, getter, equals, hashCode, toString을 자동 생성한다.
   class로 객체 생성하는 것과 기능은 동일하지만, record를 사용하는 경우가 압도적으로 간결해진다.
   1) record 사용 전
      Java 16 이전에는 아래의 코드로 VO를 생성했다.
      ~~~
      @Value
      public class ProductOptionKey {
         Long productId;
         String optionCode;
      }
      ~~~

3. 데이터 전달 클래스의 추가 생성    
   DTO를 남발하는 것과 VO는 다르다.
   VO를 만드는 이유는 의미있는 하나의 개념으로 묶기 위함이다.
   이건 클래스를 남발하는 게 아닌, 개념을 모델링하는 것이다.
   데이터 전달용이라서 DTO를 만드는 것은 권장되지 않지만, 개념적 의미가 있다면 만드는 것이다.
   값 묶음이 한 번만 쓰인다면 굳이 VO로 생성할 필요가 없지만,
   코드 여러 곳에서 반복 사용된다면 VO로 만들 가치가 충분하다.


---
## 7. 인터페이스
### < 인터페이스 사용 이유 >
- 기능(메소드)의 구현을 강제함으로써, 클래스의 설계 또는 표준화를 유도하기위해 사용함.
- 참고블로그 https://0soo.tistory.com/62

### < 인터페이스와 추상화 클래스의 차이 >
    기능(메소드)의 구현을 강제함으로써, 클래스의 설계 또는 표준화를 유도를 위해 
    인터페이스와 추상화 클래스로 Error Code와 Custom Exception을 생성했다.      
    RuntimeException을 확장해 Custom Exception을 생성한다는 의미로 추상클래스로 구현했고
    Error Code 생성 시 필수 선언해야하는 필드들의 getter()를 추상메서드로 선언해 필수 구현하도록 했다.

1. 인터페이스
    - 인터페이스에 정의된 메서드를 각 클래스의 목적에 맞게 기능을 구현하는 느낌.
    - 클래스와 별도로 구현 객체가 같은 동작을 한다는 것을 보장하기 위해 사용하는 것에 초점을 맞춘다.
2. 추상클래스
    - 자신의 기능들을 하위 클래스로 확장 시키는 느낌.
    - 추상화(추상 메서드)를 하면서 중복되는 클래스 멤버들을 통합 및 확장을 할 수 있다.
    - 같은 추상화인 인터페이스와 다른점은, 추상클래스는 클래스간의 연관 관계를 구축하는 것에 초점을 맞춘다는 것에 있다.
- 참고블로그 https://inpa.tistory.com/entry/JAVA-%E2%98%95-%EC%9D%B8%ED%84%B0%ED%8E%98%EC%9D%B4%EC%8A%A4-vs-%EC%B6%94%EC%83%81%ED%81%B4%EB%9E%98%EC%8A%A4-%EC%B0%A8%EC%9D%B4%EC%A0%90-%EC%99%84%EB%B2%BD-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0


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
## 8. 예외처리
### < 예외 처리 표준화: Custom RuntimeException 기반 설계 >
Custom RuntimeException 기반 예외 처리 구조를 설명한다.

1. Custom Exception을 생성해 예외처리하는 이유
   - throw new NullPointerException("error message") 와 같이 에러를 처리하면   
     통일성이 전혀 없고 또 정확한 에러 원인과 이유도 알 수 없다.
   - 참고블로그 https://velog.io/@mindfulness_22/%EC%98%88%EC%99%B8%EC%B2%98%EB%A6%AC%EB%8A%94-%EC%99%9C-Custom%ED%95%B4%EC%84%9C-%EC%8D%A8%EC%95%BC%ED%95%A0%EA%B9%8C   

2. Custom Exception 생성 시 RuntimeException 상속받는 이유
   - 호출자가 예외를 처리하도록 강제받지 않기위해 Exception이 아닌 RuntimeException 상속받음.
   1) Checked Exception을 피할 수 있음   
      커스텀 예외를 정의할 때 RuntimeException을 상속받으면 언체크 예외가 됩니다. 이는 호출자가 예외를 처리하도록 강제받지 않기 때문에 코드가 더 간결해질 수 있습니다.
   2) 사용자 편의성   
      개발자가 특정한 상황에서 발생하는 예외를 명확하게 정의할 수 있어, 코드의 가독성을 높이고 예외 처리 로직을 간단하게 유지할 수 있습니다. 불필요한 예외 처리를 피할 수 있는 장점이 있습니다.
   3) 비즈니스 로직과의 일관성   
      대부분의 비즈니스 로직에서 발생하는 예외는 런타임 예외로 간주되므로, 커스텀 예외도 이를 따르는 것이 자연스럽습니다.   
   - 참고블로그 https://pixx.tistory.com/409
  
3. CommonErrorResponse.java
   - 에러 발생 시 응답을 위한 모델.
   - 모든 에러에 대해 일괄적인 응답을 위해 사용.

4. CommonExceptionHandler.java
   - 발생한 예외처리를 수행.
     ~~~
     // 모든 BaseAbstractException을 상속하는 에외객체에 대한 예외처리 수행.
     @ExceptionHandler
     protected ResponseEntity<CommonErrorResponse> commonHandler(BaseAbstractException e) {
        // 응답 객체 생성
        // 응답 객체 반환
     }
     ~~~
  - HttpStatus.resolve(e.getStatusCode())     
    HttpStatus에 상태코드를 담아서 errorResponse와 함께 Http 응답으로 내려보낸다.
    ~~~ 
      return new ResponseEntity<>(errorResponse,
                Objects.requireNonNull(HttpStatus.resolve(e.getStatusCode())));
    ~~~


### < BaseAbstractException 설계 선택: 계약 중심에서 데이터 중심으로 >
1. 추상메서드만 두는 경우 -> 계약 중심 설계   
   ***계약만 강제하는 방식.***   
   Exception 종류가 아주 적고
   공통 필드가 거의 늘지 않을 것이 확실하고
   팀이 작거나 개인 프로젝트이고
   “중복”보다 “명시적 계약”이 더 중요한 경우에는
   계약만 강제하는 구조가 더 깔끔하고 좋을 방법일 수 있다.
   ~~~
   public abstract class BaseAbstractException extends RuntimeException {
       public abstract CommonErrorCode getErrorCode();
   }
   ~~~
    1) 계약만 제공   
       순수하게 계약만 제공하므로 모든 커스텀 Exception에서 getErrorCode() 구현을 강제.  
       BaseAbstractException은 필드나 생성자 없이 순수 계약 역할만 수행.
    2) 중복코드발생   
       대부분 커스텀 Exception에서 동일한 errorCode를 만들고, getter를 구현 필요.
    3) 공통필드관리 시 매번 구현 필요   
       Handler에서 일괄 처리하려면 개발자 규칙에 의존해야 함.
       결국 모든 커스텀 Exception이 동일한 필드·구조를 암묵적으로 유지해야 한다.
       그러나 BaseAbstractException은 계약(getErrorCode)만 강제할 뿐,
       공통 필드의 존재나 구조 일관성은 컴파일 타임에 보장하지 못한다.   
       강제는 계약 하나뿐이고, 실제 구조 일관성은 개발자 규칙에 의존하는 구조가 되는 것이다.

2. 필드+생성자+getter 모두 포함하는 경우 -> 데이터 중심 베이스 클래스   
   커스텀 Exception이 많고, 공통 필드를 일관되게 관리하고 싶을 때 선택하는
   실용적인 패턴으로 ***데이터 중심 베이스 클래스***이다.   
   아주 소규모 프로젝트에서는 과할 수 있음.
   ~~~
   public abstract class BaseAbstractException extends RuntimeException {
      private final CommonErrorCode errorCode;
      private final Object[] messageArgs;

      protected BaseAbstractException(CommonErrorCode errorCode, Object... messageArgs) {
          this.errorCode = errorCode;
          this.messageArgs = messageArgs;
      }

      public CommonErrorCode getErrorCode() { return errorCode; }
      public Object[] getMessageArgs() { return messageArgs; }
   }
   ~~~
    1) 중복 제거   
       모든 커스텀 Exception에서 동일한 getter 구현 불필요.
       super()만 호출하면 됨.
    2) 공통 필드 제공
       공통 필드를 베이스 클래스에서 직접 관리.   
       하위 Exception은 super() 호출만으로 구현 가능.  
       따라서 Handler가 메시지 조립을 위해 공통 필드에 일관되게 접근 가능.
    3) 추상 클래스가 단순 계약 제공을 넘어,
       공통 상태(errorCode, messageArgs)를 직접 관리하는 역할 수행.

3. 데이터 중심 베이스 클래스가 더 보편적으로 사용되는 이유
    1) 예외는 행위보다 상태 전달이 목적   
       예외 객체는 로직을 수행하는 주체가 아니므로, 발생한 에러와 필요한 값을 담으면 된다.
    2) 변경 사항이 한 곳   
       베이스 클래스의 대부분의 경우 하위 Exception 수정 없이 변경을 흡수 가능.
        - 필드 추가 시 베이스 클래스만 수정
        - 메시지 전략 변경 시 ExceptionHandler만 수정
    3) Handler가 중심 구조
       표현 로직인 메시지, 상태코드 정책이 Handler에 모이게 된다.

4. BaseAbstractException 구조 변경 결정
   현재 프로젝트는 BaseAbstractException이 이미 존재하고 CommonExceptionHandler가 중심에 있다.   
   ErrorCode 추상화가 되어있고 도메인별 Exception이 계속 늘어날 가능성이 존재한다.
   messageArgs, 메시지 전략 변경 같은 경우가 실제로 발생했다.
   이 상황에서 계약만 강제하는 방식의 문제는 변경이 생길 때, 항상 “모든 Exception”이 영향을 받는다는 것이다.
   또한 기본 구조는 계약은 명확하나, 실질적으로 모든 커스텀 Exception이 동일한 필드와 구현을 반복하고 있었다.
   이에 따라 BaseAbstractException을 계약 중심 추상 클래스에서 데이터 중심 베이스 클래스로 변경한다.
   이는 설계 단순화가 아닌 중복제거와 변경 대응성 강화를 위한 구조적 개선이다.


### < BaseAbstractException 설계 개선: Lombok 생성자 제거와 불변 구조 확립 >
1. 문제점
   1) 의도 불분명   
      Lombok의 @AllArgsConstructor + @RequiredArgsConstructor 조합은 의도대로 
      final 필드인 CommonErrorCode만 받는 생성자와 모든 필드를 받는 생성자를 자동 생성한다.   
      컴파일 오류는 없지만 어떤 생성자가 의도된 진입점인지, messageArgs가 필수인지 선택인지 코드에서 명확히 드러나지 않는다.
      베이스 클래스는 구조 변경에 영향을 자주 받는 지점으로, Lombok 조합보다는 명시적인 생성자 정의 방식이 안전하다.
   2) 예외 객체의 가변성
      messageArgs를 final로 선언하지 않으므로서 예외 객체를 가변 상태로 만들게 된다.   
      하지만 예외는 생성 시점에 상태가 결정되고, 던져진 이후에는 변하지 않는 객체여야 한다.   
      불변 객체는 디버깅, 로깅, 멀티스레드 안정성이 높다.
   3) RuntimeException 메시지 사용 의도 불명확   
      RuntimeExcception을 상속하지만 명시적으로 super()를 통해 호출하지 않는다. (자동 호출)
      따라서 RuntimeException 메시지를 사용하지 않는 구조임이 명확하게 드러나지 않는다. 
      이렇게 메시지를 Exception에 담지 않는 구조라면 의도를 더 명확히 드러내는 생성자 정의가 필요하다.
   ~~~
    @Getter
    @AllArgsConstructor
    @RequiredArgsConstructor
    public abstract class BaseAbstractException extends RuntimeException {
        // 에러코드 반환(HTTP 상태코드, 에러메시지)
        private final CommonErrorCode errorCode;
        // 에러 메시지 전달인자
        private Object[] messageArgs;
    }
   ~~~

2. 해결방법
   1) 생성자로 의도 분명히   
      messageArgs는 선택적, errorCode는 필수임을 생성자 시그니처만 봐도 확인 가능하도록 직접 정의.
   2) 예외 객체 불변화
      모든 필드를 final로 선언해 예외 객체 불변 처리.
   3) Null-safe    
      messageArgs는 “없음”을 null이 아니라 빈 배열로 표현 권장.
      Handler는 null 체크 방어 로직 없이 MessageSource는 빈 배열을 자연스럽게 처리 가능.   
      null 전달 시, 사용하는 Handler에서 null 체크 방어로직 필요함.
   4) 생성자 접근제어자 제한
      BaseAbstractException은 직접 throw하는 대상이 아니다.
      따라서 도메인 예외를 통해서만 생성되어야 하므로, protected 생성자를 사용한다.
      이로 인해 베이스 클래스는 계약과 공통 상태를 관리한다는 의도를 명확히할 수 있다.
   ~~~
    @Getter
    public abstract class BaseAbstractException extends RuntimeException {
        private static final Object[] EMPTY_ARGS = new Object[0];
    
        // 에러코드 반환(HTTP 상태코드, 에러메시지)
        private final CommonErrorCode errorCode;
        // 에러 메시지 전달인자
        private final Object[] messageArgs;
    
        protected BaseAbstractException(CommonErrorCode errorCode) {
            this(errorCode, EMPTY_ARGS);
        }
    
        protected BaseAbstractException(CommonErrorCode errorCode, Object... messageArgs) {
            this.errorCode = errorCode;
            this.messageArgs = messageArgs == null ? EMPTY_ARGS : messageArgs;
        }
    }
   ~~~


### < 예외 처리 구조 개선: ErrorCode 식별자화 및 메시지 책임 분리 >
messages.properties 도입을 고려하게 되면서, 기존 설계의 문제점을 개선하게 되었다.

1. 기존 예외처리 결정 배경   
   잘하려다 생긴 전형적인 과도 설계이다.   
   각 계층(?)에 대한 책임을 분명하게 부여하지 못했다.
   결론적으로 예외가 똑똑해지려다 책임이 섞인 상태이다.
    1) Exception 표준화를 목적으로 함
       모든 예외가 HTTP 상태코드, 에러코드(Enum), 사용자에게 내려줄 메시지를 가지므로
       이를 같은 방식으로 갖도록 강제하려 했다.
       그래서 BaseAbstractException에 추상 메서드를 생성해 해당 내용을 반드시 구현하도록 강제했다.
    2) ExceptionHandler는 예외 내부 구현을 몰라도 됨.   
       Handler는 타입 확인, 예외별 분기, 메시지 조합 로직을 전혀 몰라도 되도록 구현하고 싶었다.
       예외가 스스로 응답 정보를 알고 있어야 한다고 생각했다.
    3) ErrorCode는 Enum 중심으로 시스템 정리를 목적으로 함.   
       CommonErrorCode 인터페이스를 구현해 에러의 단위가 enum이라는 명확한 기준을 세웠다.

2. 상황 - 상황 변화로 인한 한계
   (messages.properties 도입 시 기존 예외 구조의 한계)    
   현재 시스템은 하드코딩된 에러 메시지를 제거하고,
   messages.properties 기반으로 메시지를 관리하도록 구조를 변경하고 있다.
   이 과정에서 메시지를 동적으로 구성해야 하는 요구가 발생했고,
   기존 예외 구조가 이를 수용하지 못하는 한계에 직면했다.
    1) 기존 구조   
       기존 CartErrorCode는 에러 메시지를 직접 문자열로 보유하고 있으며,
       이 과정에서 CartService.CART_MAX_SIZE와 같은 자바 코드 기반의 동적 값을 메시지 생성 시점에 참조하고 있다.
    2) 발생한 구조적 문제 - messages.properties 도입으로 인한 제약    
       messages.properties는 순수 리소스 파일로, 자바 코드를 직접 사용할 수 없다.   
       Spring은 MessageSource를 통해 메시지 키 + 전달 인자(Object[]) 방식으로만 메시지를 조립한다.   
       띠라서 에러 메시지를 런타임 시점에 동적으로 생성하려면
       메시지의 구성 요소(placeholder에 들어갈 값)를 예외 발생 시점에 함께 전달해야 하는 구조가 필요해졌다.
    3) 구조적 요구사항의 변화   
       변경되어야 하는 구조로 인해 아래와 같은 요구사항이 생겼다.
       즉, CartErrorCode는 더 이상 메시지를 직접 생성허지 않고 메시지 식별자(message key)만 제공해야 한다.
       메시지 조립에 필요한 전달 인자는 에러가 발생한 도메인 계층에서 예외와 함께 전달되어야 한다.   
       공통 예외 처리 계층에서는 전달받은 정보들을 기반으로 최종 메시지를 최종 조립해야 한다.

3. ***원인 - 기존 설게의 본질적 문제***
    1) ErrorCode는 메시지의 소유자가 아닌 식별자(identifier)   
       메시지는 표현 계층(UI/응답)의 관심사이며,
       ErrorCode는 시스템 내부에서 에러를 식별하고 분류하기 위한 값 객체이다.   
       즉, ***ErrorCode는 식별자***이지 메시지 생성기가 아니다.
       그러나 기존 구조에서는 역할 경계가 무너지면서 ErrorCode가 본래 의도보다 과도한 책임을 가지게 되었다.
        - 기존 구조에서 ErrorCode가 가지는 책임
            1. 서비스 상수 참조   
               현재 CartErrorCode에서, ErrorCode가 Service 상수를 참조하면서 인프라를 침범했다.
            2. 메시지 하드코딩    
               다국어, 메시지 교체 불가
            3. 테스트 취약   
               메시지 변경이 코드 변경으로 이어져 테스트 범위가 불필요하게 확장됨.
               (단순 문구 수정에도 enum 재컴파일 및 배포 필요)
            4. 책임 혼합
               ErrorCode가 식별의 책임을 지니는데 메시지 생성 책임까지 지님.
       ~~~
       LIMIT_CART_MAX_SIZE(
            400,
            "장바구니에는 최대 " + CartService.CART_MAX_SIZE + "건만 추가 가능합니다."
       )
       ~~~
    2) BaseAbstractException을 중심으로 한 메시지 책임 혼란   
       BaseAbstractException이 너무 많은 걸 강제하고 있다.
       ErrorCode에도, Exception에도 메시지가 있으며 Handler에서 이미 있는 메시지를 다시 조합한다.
       이로 인해 메시지의 최종 책임 주체가 모호해졌다.   
       각 계층이 메시지에 부분적으로 관여하면서 명확한 소유권이 사라진 구조를 가지고 있다.
    3) messages.properties 기반 메시지 조립 구조와 근본적 충돌    
       현재 구조에서는 enum 생성 시점에 메시지가 이미 완성되어있다.   
       이 구조에서는 plcaeholder 개념이 들어갈 여지가 없고,
       Locale/MessageSource 사용 위치 또한 명확하지 않다.   
       즉, 기존 예외 구조는 정적 메시지 생성에는 적합했지만,
       messages.properties 기반의 동적 메시지 조립 구조와는 근본적으로 맞지 않는 설게였다.

4. 수정 방향 검토 및 대안 비교   
   messages.properties 기반의 동적 메시지 조립 구조를 도입하기 위해
   다음과 같은 수정 방향을 검토했다.
    1) CartException에만 messageArgs 필드를 추가하고 전용 Handler 분리   
       CartException만을 위한 메시지 처리 로직을 별도로 구성하는 방법이다.
       예외 처리 구조가 도메인별로 분기되어 예외가 늘어날수록 Handler 폭증 우려가 있다.
       따라서 공통 예외 처리 전략이 붕괴된다.
       확장성은 낮고 유지보수 비용은 증가하는 방법이다.   
       추가적으로, 메시지 파라미터를 특정 도메인안의 예외에 한정된 특성으로 취급한다는 점에서 구조적으로 적절하지 않다.
       messageArgs는 특정 도메인 예외의 구현 세부사항이 아니라, 에러 메시지가 표현되기 위해 필요한 공통 속성이다.
       에러 메시지는 종종 수량, 한계값, 상태, 정책값 등 동적 정보를 포함하기 때문에 모든 도메인에서 발생할 수 있다.
       따라서 특정 도메인의 특성이 아닌, 에러 표현의 일반 속성으로 취급되어야 하므로
       BaseAbstractException 수준에서 다루는 것이 구조적으로 타당하다.
    2) BaseAbstractException에 선택적 messageArgs 제공 메서드 추가   
       전달인자가 필요한 경우에만 오버라이딩하도록 설계.
       예외마다 메시지 인자 사용 여부가 다르므로 Handler는 분기 로직을 알아야한다.
       또한 메시지 조립 책임이 불명확해진다.
       표면적으로는 유연해보이지만, 내부 일관성이 깨지게 된다.
    3) BaseAbstractException에 추상 메서드 추가   
       모든 커스텀 Exception이 메시지 관련 메서드를 구현하도록 강제.
       메시지가 필요 없는 예외까지 불필요한 구현을 강제하게 된다.
       따라서 메시지 전략 변경 시 모든 예외 수정이 필요하다.
       과도한 강제로 인해 변경 비용이 높아질 수 있다.
    4) 기존 Exception + ErrorCode 구조 전면 개편 (최종 선택)
       ErroCode는 식별자 역할에 집중하고,
       Exception은 상황과 메시지 인자를 전달하고,
       Handler는 메시지 조립과 응답을 책임지도록 변경한다.
       역할이 명확하고 확징 시 구조가 흔들리지 않을 것이다.

5. 역할 재정의 - 개선된 예외 구조의 책임 분리
   기존 구조의 한계를 해결하기 위해 예외 처리 구조 전반에 대한 역할 재정의가 필요하다.
   이 변경은 단순히 메시지 관리 방식을 바꾸는 것이 아니라,
   ErrorCode, Exception, Handler 간 책임을 명확히 분리하기 위한 구조적 개선이다.
   이 과정에서 CommonExceptionHandler가 예외에 포함된 전달 인자를 사용해 메시지를 조립할 수 있도록,
   BaseAbstractException은 ErrorCode와 messageArgs를 공통으로 보유하는 구조로 수정되었다.
   ***이 변경은 단순한 메시지 관리 방식 변경이 아니라,
   ErrorCode · Exception · Handler 간 책임을 명확히 분리하기 위한 구조적 개선이다.***
    1) CartErrorCode
        - 에러의 의미를 식별하는 식별자
        - HTTP 상태 코드 + 메시지 키만을 보유
    2) CartException
        - 어떤 에러가 발생했는지(ErrorCode)
        - 메시지 조립에 필요한 전달 인자(messageArgs)를 함께 보유
    3) CommonExceptionHandler
        - BaseAbstractException을 기준으로 예외를 일괄 처리
        - MessageSource를 사용해 메시지를 최종 조립
        - HTTP 상태와 에러 정보를 포함한 응답을 생성

6. 개선 방향 - messages.properties 도입의 설계적 의미
   현재 구조는 이미 초기 설계 시 책임이 명확하지 않고 동일한 정보가 여러 군데 분산되어있기 때문에
   메시지 관리 문제를 넘어선 구조의 문제가 있는 경우다.
   이 때 messages.properties 도입의 목적은 국제화 등이 아닌
   ***ErrorCode · Exception · Handler 간 책임을 명확히 분리하기 위한 구조적 개선***이다.
   <br>
   설계를 위한 설계가 되지 않도록 하기 위해 고민했다.
   Validation 에러 메시지를 하드코딩에서 파일로 별도 분리하면서 서버 에러의 메시지도 관리 방식도 함께 검토하게 되었다.   
   Validation 메시지는 수정 빈도와 일관성 측면에서 파일 분리는 명확한 이점이 있었다.
   반면, 서버 에러 메시지의 경우는 Enum으로 관리하는 방식도 베스트는 아니지만 최악은 아니라고 생각해 현상 유지 계획이었다.
   그럼에도 불구하고 이번 설계 변경은 ***책임 분리를 위한 설계 안정성 측면에서 의미가 있다.***
    1) 메시지를 도메인 로직에서 분리   
       책임이 명확하지 않고 동일한 정보가 여러 군데 분산되어 있는 구조를 정리할 수 있다.
       기본 구조의 본질적인 문제는 ErrorCode가 메시지 자체를 보유하고,
       Exception이 메시지 또는 메시지 조합 정보를 보유하고,
       Handler가 이미 있는 메시지를 다시 조합해 응답한다는 것이다.
       이는 메시지 책임이 여러 계층에 분산되어 메시지가 시스템 내부 로직을 오염시키게 된다.
       messages.properties 구조를 이용해
       ErrorCode는 에러 식별, Exception은 어떤 에러가 발생했고 어떤 값을 전달할지 상황전달 역할을 하고,
       Handler는 표현을 위해 메시지를 생성하고, properties는 UI 문구 자체를 관리하므로서
       각 계층의 관심사를 분리할 수 있도록 한다.
    2) ErrorCode를 불변 식별자로 단순화      
       messages.properties를 쓰지 않으면 ErrorCode는 필연적으로 문자열 포맷터 역할을 하게 된다.
       그러면 ErrorCdoe는 정책, 표현 방식을 알고, 서비스 상수를 참조하면서 변경에 민감하게 된다.
       하지만 메시지 외부로 분리하면 ErrorCode는 불변 식별자 역할에 집중할 수 있다.
    3) 변경에 대한 최소한의 여지 확보   
       현실에서는 여러 변경 사항이 발생하게 된다.
       messages.properties 구조는 코드 변경 없이 수용할 여지를 남긴다.
       이는 설계 관점에서 중요하다.

   구조가 단순하고 책임이 명확하다면 messages.properties를 사용할 필요는 없다.
   하지만 현재 구조는 이미 BaseAbstractException, CommonExceptionHandler, ErrorCode 추상화, 도메인별 Exception 분리 등
   구조가 설계되어 있기 떄문에 messages.properties는 메시지 관리 목적을 넘어 책임 분리를 위한 선택이다.

7. 개선된 예외 구조
    1) 핵심 원칙   
       ***예외는 상태를 전달하고, 표현은 Handler에서 결정한다.***   
       ErrorCode는 식별자, Exception은 상황 전달, Handler는 메시지로 MessageSource를 결정하는 구조를 가지도록 한다.
        - ErrorCode는 식별자 + HTTP 상태까지만 책임진다.
        - 메시지는 Exception 또는 Handler 레벨에서 결정한다.
            - Exception은 어떤 에러인지, 메시지 파라미터만 가진다.
            - Handler는 메시지 해석과 응답 생성을 담당한다.
        - messages는 UI 표현 문구만 담당한다.
    2) CommonErrorCode 단순화   
       메시지 호출을 위한 key를 갖는다.(메시지 문자열 자체를 갖지 않음)
       ~~~
       public interface CommonErrorCode {
           HttpStatus getStatus();
           String getCode(); // 메시지 키
       }
       ~~~
       ~~~
       public enum CartErrorCode implements CommonErrorCode {
       LIMIT_CART_MAX_SIZE(HttpStatus.BAD_REQUEST, "cart.limit.exceeded");
       
           private final HttpStatus status;
           private final String code;
       }
       ~~~
    3) BaseAbstractException은 ErrorCode, messages 전달인자만 갖는다.   
       getter를 강제하지 않고, 필드기반으로 단순, 명확하게 가져간다.
        - Object...   
          법적으로는 Object[] 배열이고, 사용성을 좋게 만든 자바의 가변 인자(varargs) 문법.   
          컴파일 타임에 그냥 배열로 변환.
        - 추상 클래스를 유지한다는 말은 경계를 유지한다는 뜻이다.
          경계를 유지한다는 말은, BaseAbstractException이 직접 사용되는 예외가 아니라
          도메인 예외들이 반드시 거쳐야 하는 기준선으로 유지한다는 뜻이다.
          따라서 책임의 분리가 선명해진다.
        - 추상 클래스가 아니지만 abstract를 유지하는 이유
            1. 예외를 직접 던지지 말라는 설계 의도 표현 수단으로 사용   
               BaseAbstractException은 직접 사용되는 예외가 아니라
               도메인 예외들이 반드시 상속해야 하는 기반 계약(contract) 이다.
               띠라서 abstract로 선언해 직접 예외를 던지는 경우를 막는다.
            2. 도메인 경계 강제
               로그를 통하면 어느 도메인에서 예외가 발생했는지 즉시 알 수 있음.
            3. 추후 "공통 동작"을 추가할 확장의 여지를 남김
        - abstract 없애도 되는 경우   
          추상 클래스가 과한 경우에는 제거해 단순화하는 것이 좋다.
          예를 들어 소규모 프로젝트인데 에러코드 10개 미만이고 도메인 구분 필요없는 경우가 있다.
       ~~~
       public abstract class BaseAbstractException extends RuntimeException {
           private final CommonErrorCode errorCode;
           private final Object[] messageArgs;
 
           protected BaseAbstractException(CommonErrorCode errorCode, Object... messageArgs) {
               this.errorCode = errorCode;
               this.messageArgs = messageArgs;
           }
 
           public CommonErrorCode getErrorCode() {
               return errorCode;
           }
 
           public Object[] getMessageArgs() {
               return messageArgs;
           }
       }
       ~~~ 
    4) 커스텀 Exception
       ~~~
       public class CartException extends BaseAbstractException {
           public CartException(CartErrorCode errorCode, Object... args) {
               super(errorCode, args)
 
           }
     
           public CartException(CartErrorCode errorCode) {
               super(errorCode, null);
           }
       }
       ~~~
    5) ExceptionHandler
       ~~~
       @ExceptionHandler(BaseAbstractException.class)
       public ResponseEntity<CommonErrorResponse> handle(BaseAbstractException e) {
     
           String message = messageSource.getMessage(
               e.getErrorCode().getCode(),
               e.getMessageArgs(),
               LocaleContextHolder.getLocale()
           );
     
           return ResponseEntity
               .status(e.getErrorCode().getStatus())
               .body(new CommonErrorResponse(e.getErrorCode(), message));
       }       
       ~~~


---
## 9. JWT 이용한 로그아웃
### < JWT 이용한 로그아웃 >
1. 로그인, 로그아웃 구현방식
    - 세션 방식
        - 서버는 세션 DB에 사용자의 정보를 저장하고, 생성된 세션 ID를 (일반적으로) 쿠키에 넣어서 클라이언트에 반환.
        - 클라이언트는 세션 ID를 포함한 쿠키를 서버에 전달.
        - 서버는 쿠키에서 세션 ID를 꺼내, 세션 DB에 해당 아이디에 해당하는 레코드가 존재하는지 조회하고, 존재하지 않으면 로그인이 되어있지 않다고 판단.
    - JWT 방식
        - JWT는 세션을 사용한 방식과 달리, **Stateless(상태를 유지,관리하지 않음)**한 특징을 최대한 살린 형태로 동작.
        - JWT 방식은 로그인, 로그아웃 과정에서 데이터베이스에 저장한다거나, 조회한다는 과정을 포함하지 않음.
        - 서버에서만 처리하고자 하는 경우 토큰 정보를 클라이언트 쿠키에서 삭제할 수 없음.
        - **JWT의 방식에 Stateful(상태를 유지하고 관리)한 방식을 결합한 형태로 문제를 해결.**

2. JWT를 이용한 stateless한 로그인, 로그아웃 구현.
    - 서버는 토큰을 발급하면, 그 뒤로는 토큰에 대한 제어를 완전히 잃어버린다.    
      어딘가에 내가 발급한 토큰이 무엇인지, 어떤 클라이언트에 토큰을 발급했다는 정보도 저장해두지 않기 때문이다.   
      JWT가 토큰 자체에 사용자 정보와 만료 시간을 포함하고 있으며, 서버 측에서 이를 확인하여 인증한다.
    - 데이터베이스를 전혀 사용하지 않는 stateless한 JWT 방식을 사용할 때,
      **로그아웃은 클라이언트 측에서 쿠키나 로컬 스토리지에서 JWT 토큰을 삭제하거나 무효화**하는 방식으로 구현 가능하다.

3. JWT를 이용해 stateless하게 구현하지 않는 이유
    - 악성 해커가 토큰을 탈취해서 사용한다면, 서버는 토큰 만료 시간까지는 더이상 손쓸 방법이 없다.    
      혹시 Refresh Token도 함께 탈취당한 경우에는 정말 영원히 악성 해커를 막을 수 없게 될 수도 있다.
    - **데이터베이스를 전혀 사용하지 않는 JWT 방식으로는 안전한 로그아웃을 구현할 수 없다.**

4. Refresh Token과 Access Token
    - Access Token
        - 인증된 사용자가 특정 리소스에 접근할 때 사용되는 토큰.
        - 클라이언트는 Access Token을 사용하여 인증된 사용자의 신원을 확인하고, 서비스 또는 리소스에 접근.
        - 유효 기간이 지나면 만료 (expired).
        - 만료된 경우, 새로운 Access Token을 얻기 위해 Refresh Token 사용.
        - Access Token이 만료되면 사용자는 로그아웃 상태가 되고, 새로운 로그인을 수행해야 함.
        - Access Token의 만료 기간이 짧은 경우, 매우 안좋은 사용자 경험을 줄 수 있음.
        - Access Token의 만료 기간이 긴 경우, 보안이 매우 취약.

    - Refresh Token
        - Access Token의 갱신을 위해 사용되는 토큰
        - 일반적으로 Access Token과 함께 발급
        - **Access Token이 만료**되면 Refresh Token을 사용하여 **새로운 Access Token 발급하기 위해 사용.**
        - 사용자가 지속적으로 인증 상태를 유지할 수 있도록 도와줌 (매번 로그인 다시 하지 않아도 됨)
        - 이 과정에서 Refresh Token은 서버에 저장 필요.
        - 보안 상의 이유로 Access Token보다 긴 유효 기간 가짐
        - 유효성을 검증하고 새로운 Access Token을 발급.

    - 서버 측에서 Refresh Token을 저장해야 하기 때문에 데이터베이스를 전혀 사용하지 않고서는 Refresh Token을 통한 Access Token 재발급을 구현 불가.

4. JWT를 이용한 stateful한 로그인, 로그아웃 구현.
    - 완전한 stateless를 포기하고 일부분 데이터베이스의 힘을 빌리기로 타협한다면, 보안, 사용자 경험에 대한 개선 가능.
    - 사용자가 로그아웃을 요청할 때 리프레시 토큰을 무효화하여 해당 사용자가 더 이상 새로운 엑세스 토큰을 발급받지 못하도록 하는 방식으로 구현한다.

    1. Access Token의 만료 기간을 30분 이하로 아주 짧게 설정.
        - 만료 시간이 굉장히 짧기 때문에, Access Token이 탈취되더라도 피해를 최소화 가능해 비교적 안전함.

    2. 로그아웃한 사용자의 Refresh Token은 블랙리스트의 역할을 하는 데이터베이스에 저장.
        - 블랙리스트
            - 만료되거나 무효화(로그아웃)된 토큰을 저장하는 테이블.
        - 클라이언트 로그아웃 시 프론트엔드에서 직접 Access Token을 삭제.
        - Access Token이 만료되어 Refresh Token으로 새로운 Access Token을 요청하는 경우
            - 서버는 블랙리스트에 해당 Refresh Token이 저장되어 있는 것을 확인.
            - 해당 Refresh Token은 로그아웃 여부를 알 수있기 때문에 Access Token 재발급을 거절 가능.
            - 서버 측에서는 블랙리스트를 관리하여 무효화된 토큰을 거부할 수 있다.
            - JWT가 아직 만료되지 않은 상태에서 로그아웃 요청이 오는 경우 서버 측에서 해당 토큰을 블랙 리스트로 처리.
            - 인증 필터에서 매 요청마다 토큰이 블랙리스트에 해당하는 토큰인지 확인필요.

- 조금 stateful 하지만 악성 해커가 계속해서 판을 치는 것을 방지하면서도 Access Token이 만료되었을 때 Refresh Token을 통해 재발급을 받아 너무 자주 로그인이 풀리는 것을 방지해서 사용자 경험도 향상될 수 있다.   
  물론 여전히 Access Token이 탈취당한 경우에는 토큰이 만료되기까지 기다릴 수밖에 없다는 점은 이전과 동일하다.    
  하지만, Refresh Token 방식을 사용할 수 있게 되어, 부담없이 Access Token의 만료 기간을 매우 짧게 설정할 수 있어서 보안 측면에서는 확실히 개선된다.

- 위의 방식에서는 Access Token이 탈취당했을 때, 서버 측에서 해당 토큰을 제어할 수 없기 때문에 토큰이 만료될 때까지 기다리는 방법 뿐이었다.
  하지만, 블랙리스트에 Access Token까지 저장한다면 탈취된 Access Token을 곧바로 서버 측에서 블랙 리스트에 저장해 버리면, 해당 Access Token으로 오는 요청을 거절할 수 있게 된다.
  매우 좋은 방식인 것 같지만 이 방식을 사용하게 되면 세션을 사용한 방식과 사실상 차이가 없어진다.
  모든 요청이 들어왔을 때, Access Token이 블랙리스트(테이블)에 존재하는지 매 번 데이터베이스를 찔러 조회해 확인해봐야 하기 때문에 JWT 방식을 사용하지만 매우 stateful한 방식으로 전혀 장점을 살리지 못하고 있다고 볼 수 있다.
  이 경우에는 그냥 세션 방식을 사용하는 것과 다를 것이 없으므로, 세션 방식을 사용하는 편이 낫다.

5. 로그인, 로그아웃 구현방식의 차이
    - 로그인
        - 세션 방식: 여전히 세션 DB에 접근해 유저 정보를 저장하고 세션 ID를 반환 받아야 한다.
        - JWT 방식: DB에 접근할 필요 없이 토큰만 발급하면 된다.

    - 로그인이 되어 있는 경우
        - 세션 방식: 늘 세션 ID가 유효한지 DB를 조회해야 한다.
        - JWT 방식: DB에 별도로 조회할 필요 없이, 서명만 확인해서 토큰이 조작되지 않았는지만 확인하면 된다.

    - 로그아웃 시
        - 세션 방식: 세션 DB에서 해당 세션 ID에 해당하는 레코드를 삭제해야 한다
        - JWT 방식: DB의 블랙리스트 테이블에 로그아웃한 계정의 Refresh Token을 저장하면 된다.

    - 여전히 JWT 방식은 세 가지 중 오직 로그아웃 시에만 데이터베이스에 접근하면 된다.
    - 일부분 stateful 해지긴 했으나, 여전히 stateless의 장점을 잘 살리고 있다고 볼 수 있다.
    - 추가적인 보안 요소를 도입할 수 있다.
      예를 들어, IP 주소나 기기 정보를 토큰에 포함시켜 토큰을 사용하는 클라이언트의 신원을 확인할 수 있다.
      이를 통해 탈취된 토큰이 다른 기기나 IP 주소에서 사용되는 것을 방지할 수 있다.

> - 고민한 내용
    > 토큰을 생성하는 것으로 로그인 기능을 구현했으니 로그아웃 시에는 토큰을 제거하는 것으로 구현할 수 있을 것이라고 단순히 생각했다.
    > 하지만 JWT를 이용하는 현재 방식은 stateless하므로 서버에서 관리할 수 있는 방법이 없고 따라서 클라이언트측이 가지고 있는 토큰정보를 서버에서 제어할 수 없다.
    > 그렇기에 서버에서 처리는 불가하고 클라이언트 측에서 처리가 필요하다고 생각했다.
    > 하지만 로그아웃 자체를 온전히 클라이언트에게 맡겨도 되나? 라는 의문이 들었다.
    >
    > 기존 한 번 발행한 토큰의 시간이 24시간인 것도 보안상 위험할 수 있다는 것을 알았다.
    > DB에 Access Token, Refresh Token을 저장하는 것보다 속도 측면에서 redis에 저장하는 것이 속도 측면에서 유리함을 알았다.

- 참고블로그   
  https://engineerinsight.tistory.com/232   
  https://f-lab.kr/insight/jwt-logout-implementation-20240608   
  https://evga7.tistory.com/141


---
## 10. 로그 레벨 결정
### < 로그 레벨 결정 >
- 로그 레벨
  - 해당 로그 메시지가 얼마나 중요한지 알려주는 정보.
  - 정보 노이즈 및 경고 피로를 줄이는 데 도움이 될 수 있음.
- 정상적이지 않은 모든 상황에서 전부 ERROR 레벨로 처리하게 되면 불필요하게 많은 알람들로 인해 정작 봐야할 심각한 에러 로그들을 놓칠 수 있다.

1. DEBUG
   - 개발 혹은 테스트 단계에서 해당 기능들이 올바르게 작동하는지 확인하기 위한 로그 레벨.
   - 개발 및 테스트 과정에서 문제를 추적하고 해결하는 데 도움을 준다.
   - **운영 환경에서는 남기고 싶지 않은 로그 메세지**를 위한 레벨.
2. INFO
   - 애플리케이션에서 정상 작동에 대한 정보로 어떤 일이 발생했음을 나타내는 표준 로그 레벨.
   - **애플리케이션 상태, 설정 또는 외부 리소스와의 상호 작용과 같은 상태 확인을 위한 이벤트**를 나타냄.
     - ex) 인증 API의 백엔드 시스템에서 인증이 성공했는지 여부에 따라 사용자가 인증을 요청한 정보.
     - ex) 애플리케이션이 시작되거나 종료되는 시점, 중요한 작업이 완료되거나 실행되는 시점 (예: 배치 작업, 정기 작업) 등의 경우.
   - **시스템을 파악하는데 유익한 정보**여야만 한다.
3. WARN
   - 애플리케이션에서 잠재적으로 문제가 될 수 있는 상황일때 남기는 로그 레벨.
     - ex) 사용자가 로그인에 실패하는 것은 ID, PW 등을 오입력하여 언제든 발생할 수 있는 일반적인 문제 상황.
           이럴때는 WARN 레벨로 책정하고, 로그인이 5번 연속 실패하는 등 특정 기준치를 넘길 경우 ERROR 레벨로 남기는 것이 좋다.
     - ex) 예상치 못한 입력 (사용자가 유효하지 않은 입력을 제공한 경우 - 예: 이메일 형식이 아닌 입력), 리소스 제한 (파일 업로드 사이즈 초과 등)과 같은 경고.
           이런 경우 사용자에게 노출되는 메세지에 상세한 가이드가 필요한 것이지, 로그 레벨이 ERROR일 필요가 없다.
   - **개발자가 조치를 취할 수 있도록 주의를 기울일 필요가 있는 상황.**
4. ERROR
   - 애플리케이션에서 발생한 심각한 오류나 예외 상황을 나타내는 로그 레벨.
   - 기능 자체가 제대로 작동하지 못하는 문제일 때 남겨야 하며 **즉시 조치가 필요할 때**를 의미.
     - ex) 데이터베이스 연결이 실패한 경우, 내부 시스템의 문제로 결제가 실패하는 경우 등 즉시 조치를 취해야 한다.

- **운영에서 필요한 것은 WARN 레벨과 ERROR 레벨을 구분**하는 것.
  - 예외 상황, 오류 상황이 모두 ERROR 일 수는 없다.
    일반적이지 않은 예외 상황이 개발자가 제어할 수 없는 상황이라면 이는 WARN으로 두는 것이 좋다.
  1. 외부 API 연동
     - WARN
       - 외부 API 의 에러 발생율은 개발자가 컨트롤 불가.
         한 두번의 실패가 큰 영향을 끼치는 상황이 아니라면 외부 API는 WARN으로 레벨 사용.
     - ERROR
       - 결제 등 비즈니스상 치명적인 API의 경우     
         그 외 외부 결제사의 문제라면 이는 ERROR로 로그를 남겨 빠르게 인지후, 해당 결제사가 정상이 될 때까지는 미노출시키는 등의 작업 필요. 
         (단, 잔고 부족, 인증 실패 등 고객의 실수인 경우는 WARN)
       - 재시도 전략이 없는 상태에서 하루 1번, 한달에 1번 정도로 요청 자체가 주기적으로 적은 횟수만 수행하는 경우      
         Retry 호출, 메세지큐 등 재시도 전략이 있는게 아니면서 딱 1번씩만 호출하는 경우 재시도를 해야하기 때문에 ERROR로 로그

- 모니터링 기준 설정    
  로그 레벨 중 WARN과 ERROR을 구분하면 진짜 봐야할 중요한 문제들만 볼 수 있다.
  둘의 레벨을 구분하면 아래와 같이 모니터링 기준을 가져갈 수 있다.
  - INFO: 기존대비 +-50%이상 차이날 경우 알람
  - WARN: 분당 20개이상일 경우 알람
  - ERROR: 분당 5개이상일 경우 알람

- Exception Handler에서 에러경중에 맞는 로그 출력하기.
  - CustomException 클래스를 만들고, 예외의 심각도에 따라 로그 레벨을 설정가능.
  - warn과 error를 구분할 수 있는 필드를 추가.
      ~~~
      public class CustomException extends RuntimeException {
      private boolean isCritical; // WARN, ERROR
    
          public CustomException(String message, boolean isCritical) {
              super(message);
              this.isCritical = isCritical;
          }
    
          public boolean isCritical() {
              return isCritical;
          }
      }
      ~~~
    
- 참고블로그   
  https://jojoldu.tistory.com/712    
  https://velog.io/@midas/Exception-%EC%B2%98%EB%A6%AC-%EB%B0%A9%EB%B2%95

- 추가공부권장 (AOP와 log)   
  https://curiousjinan.tistory.com/entry/spring-controlleradvice-logging


---
## 11. 상수 선언
### < 상수 선언 >
1. final
   - 변수에 final 선언 시 해당 변수는 한 번만 초기화 가능.
2. static
   - 컴파일 시점에 데이터의 메모리 할당.
   - 동적 데이터와 달리 프로그램 실행 직후부터 종료시까지 메모리 수명 유지.
3. 상수 선언 시 static final로 선언하는 이유
   - 상수는 모든 클래스 인스턴스에서 똑같이 써야할 값이고, 처음부터 끝까지 바뀌지 않아야 할 값.
   - 상수를 static으로 선언하지 않은 경우, 클래스의 모든 인스턴스에 해당 상수에 대한 메모리를 할당.
   - 변하지 않고 계속 일관된 값을 제공 -> 데이터의 의미와 용도 고정.
   - 인스턴스 생성마다 매번 같은 메모리를 잡지 않아 효율적.

- 참고블로그
  https://velog.io/@tjddus0302/Java-%EC%83%81%EC%88%98%EB%8A%94-%EC%99%9C-static-final%EB%A1%9C-%EC%84%A0%EC%96%B8%ED%95%A0%EA%B9%8C


---
## 12. Enum값 비교 연산자
### < Enum값 비교 연산자 >
- Java에서 Enum 값을 비교할 때는 **== 연산자를 사용**하는 것이 더 안전하고 효율적.
1. == 연산자
    - == 연산자는 참조 비교를 수행합니다.
    - Enum은 싱글턴 패턴을 따르므로, 각 Enum 인스턴스는 고유하고 애플리케이션 내에서 유일합니다.
    - 따라서 == 연산자는 Enum 값들이 동일한지 비교할 때 항상 참조가 동일한지 확인합니다.
    - Enum 값들은 JVM 내에서 고정된 값으로 관리되기 때문에, 두 Enum 값이 동일한 값을 갖고 있다면 동일한 객체를 참조하게 됩니다.
      이런 이유로 ==를 사용하면 성능과 안정성 면에서 최적화됩니다.

2. equals() 메서드
    - equals() 메서드는 내용 비교를 수행합니다.
    - Enum은 기본적으로 Object 클래스를 상속받고, Object 클래스의 equals() 메서드는 참조를 비교하는 방식으로 구현되어 있습니다.
    - Enum 값에서 equals() 메서드를 호출하면, 사실상 == 연산자와 동일한 결과를 반환합니다.
    - equals()를 사용하는 것은 불필요한 오버헤드를 발생시킬 수 있습니다.
    - equals()는 일반적으로 객체의 상태를 비교하는 데 사용되므로, Enum과 같은 상수 객체에서는 == 연산자가 더 명확하고 효율적입니다.

3. instanceof 연산자
    - 객체가 특정 클래스의 인스턴스인지를 확인하는 연산자.
    - instanceof를 사용하여 특정 Enum 클래스의 값인지 확인하는 것은 불가능합니다.
    - 이유는 Enum 값이 클래스 타입으로 존재하는 객체가 아니기 때문입니다.
    - 대신, Enum의 값 자체가 Enum 클래스의 인스턴스가 아니라,
      Enum 클래스에 정의된 고유한 상수 값으로 존재하기 때문에 instanceof를 사용하여 Enum 값을 직접 확인하는 방식은 적합하지 않습니다.
    - Enum 값은 클래스의 인스턴스가 아니라, 해당 Enum 클래스의 고정된 상수입니다.
      즉, Enum 값은 Enum 클래스의 정적 필드일 뿐, 클래스의 객체가 아닙니다.
      그래서 instanceof를 사용하여 Enum 값이 특정 Enum 타입의 인스턴스인지 확인하는 것은 불가능.
    - Enum 값이 특정 Enum 타입인지 확인하려면 Enum 클래스의 getClass() 메서드를 활용하는 방법을 사용가능.
      ~~~
      // status가 Status enum의 인스턴스인지 확인. 
      // Enum 상수는 getClass()를 호출할 수 있으며, 해당 Enum의 타입을 반환.
      if(status.getClass() == Status.class) { 
          // ...
      }
      ~~~


---
## 13. Pageable
### < Pageable - 페이지 기능을 지원 >
- 개발기간 단축
  - 페이지 기능은 흔하지만 서비스의 핵심적인 기능은 아니다.
    때문에 직접 구현하려면 많은 공수가 필요하지만 스프링부트에서 지원하는 Pageable을 사용해 구현하게 되면
    단순한 기능보다 핵심적인 기능에 집중해 더 완성도있는 서비스를 제공할 수 있다.

- 조회 데이터 제한    
  - api 호출에 대한 응답 결과로 이 모든 정보를 조회하는 것은 옳지 않다.
    요청을 주고 받는 데이터의 수가 클수록 당연히 네트워크의 대역폭도 더 많이 사용해야 하므로
    서비스 전체에 악영향을 줄 가능성도 높아진다.
  - 많은 회사 목록을 받는다 하더라도 어짜피 클라이언트에서 한번에 보여줄 수 있는 아이템의 개수는 한정되어있다.
    따라서 적당한 수의 회사 정보만 조회하도록 한다.

1. Pageable 
   - 페이지 기능을 지원한다.
   - import org.springframework.data.domain.Pageable;
   - controller에서 파라미터로 Pageable을 받을 수 있도록 한다.
   - 클라이언트에서 페이징 관련 옵션을 추가해 api를 호출할 수 있게 된다.
   - Pageable이 임의로 변경되는 것을 막기위해 final로 선언한다.
   - service에서 JPA repository의 findAll() 메서드 호출 시 인자로 Pageable 객체를 전달한다.
     단, 반환값은 List<Entity> 타입이 아닌 Page<Entity>로 변경되어야 한다.
   
   - 페이지 관련 옵션   
     - 응답시 회사목록에 추가로 하단에 페이지에 대한 정보도 함께 제공한다.
     - 페이지의 사이즈(한 페이지에 출력할 데이터 수)는 default로 20.
     - 따라서 사이즈 변수를 파라미터로 넘겨 사이즈 조절도 가능하다.
     - query param으로 사이즈=3, 페이지는 0번째 페이지 출력하도록 전달 url
     
   - 참고블로그    
     http://localhost:8080/company?size=3&page=0

2. Page JSON 리턴값
  {
    "content": [
      {
        "id": 1,
        "ticker": "MMM",
        "name": "3M Company"
      },
      ...
    ],
    "pageable": {
        "sort": {
            "empty": true,
            "sorted": false,
            "unsorted": true
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 20,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalElements": 4,
    "totalPages": 1,
    "size": 20,
    "number": 0,
    "sort": {
        "empty": true,
        "sorted": false,
        "unsorted": true
    },
    "first": true,
    "numberOfElements": 4,
    "empty": false
  }

3. page 객체의 Entity -> Dto로 변환1
   - Page.map() 메서드 사용. 
     - map() 메서드는 Page 객체 내의 각 요소를 변환하는 데 사용됩니다.
   ~~~ 
   import org.springframework.data.domain.Page;
   import org.springframework.data.domain.PageImpl;
   import java.util.List;
   import java.util.stream.Collectors;

   public Page<ResponseProductionDto> convertToDto(Page<Entity> entityPage) {
       List<ResponseProductionDto> dtoList = entityPage.getContent().stream()
                   .map(entity -> new ResponseProductionDto(entity)) 
                   .collect(Collectors.toList());

       return new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements());
   }
   ~~~ 
   - entityPage.getContent()
     - Page 객체의 실제 데이터를 가져옵니다.
   - map()
     - Page 객체의 각 Entity를 ResponseProductionDto로 변환합니다.
   - new PageImpl<>(dtoList, entityPage.getPageable(), entityPage.getTotalElements())
     - Page 객체는 PageImpl로 변환할 수 있습니다. 
     - PageImpl은 DTO 리스트와 페이지 정보를 받아 새로운 Page 객체를 만듭니다.

4. page 객체의 Entity -> Dto로 변환2
   - Spring Data Page는 map() 메서드를 제공합니다. 
   - Page의 각 항목을 변환한 후 새로운 Page를 반환할 수 있습니다.
   ~~~
    import org.springframework.data.domain.Page;
    
    public Page<ResponseProductionDto> convertToDto(Page<Entity> entityPage) {
        return entityPage.map(entity -> new ResponseProductionDto(entity));
    }
   ~~~


### < Pageable 페이지, 사이즈 디폴트값 지정 >
1. @PageableDefault 어노테이션
   - Pageable 객체의 기본 설정을 Controller 메서드에서 직접 지정할 때 사용.
   - PageRequest를 Controller에서 동적으로 생성.
   - @PageableDefault 어노테이션은 기본적으로 페이지 크기(size)와 페이지 번호(page)를 설정하는 데 사용.
   - Pageable 객체가 HTTP 요청의 쿼리 파라미터를 기반으로 자동으로 매핑.
     - GET /products?page=1&size=10
   - Pageable의 주요 파라미터:
     - page: 요청한 페이지 번호. (기본값 0)
     - size: 한 페이지당 데이터 수. (기본값 20)
     - sort: 정렬 기준. 예를 들어, sort=name,asc는 이름을 기준으로 오름차순 정렬

   ~~~
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;
    
    @RestController
    public class ProductController {
    
        // ...
       
        @GetMapping("/products")
        public Page<Product> getProducts(@PageableDefault(size = 5, page = 0)
                                         Pageable pageable) {
            return productService.getProducts(pageable);
        }
    } 
   ~~~

2. PageRequest 생성 
   - Pageable을 DTO에서 페이지와 페이지 크기의 기본 사이즈를 설정하려면 PageRequest를 사용해 Pageable을 집접 수동으로 생성해 처리가능.
   
    1. PageRequest 직접 생성
      - PageRequest는 페이지 번호와 페이지 크기를 지정할 수 있는 방법을 제공. 
      - 컨트롤러에서 DTO를 받아서 Pageable을 생성하고, 서비스에 전달.
      - 유연성
        - Pageable 객체를 DTO에서 직접 생성함으로써 페이지와 크기 설정을 한 곳에서 관리가능.
      - 재사용성
        - 페이지네이션 설정을 여러 곳에서 재사용 가능. (여러 엔티티에서 동일한 방식으로 페이지와 크기 처리가능)
      ~~~
      import org.springframework.data.domain.PageRequest;
      import org.springframework.data.domain.Pageable;
    
      public class ProductSearchDto {
      
          private int page = 0;  // 기본값은 0으로 설정
          private int size = 5; // 기본값은 5으로 설정
    
          // Getters and Setters
    
          public Pageable toPageable() {
              return PageRequest.of(this.page, this.size); // PageRequest 생성
          } 
      }
      ~~~ 
      - DTO에서 page와 size를 필드로 가지고 PageRequest.of(page, size)로 변환하는 toPageable() 메서드 생성. 
      ~~~
      @RestController
      public class ProductController {
    
          // ...
    
          @GetMapping("/products")
          public Page<Product> getProducts(ProductSearchDto searchDto) {
              Pageable pageable = searchDto.toPageable(); // requestDto에서 Pageable 생성
              return productService.getProducts(pageable);
          }
      }
      ~~~
      - DTO는 Pageable을 변환하여 Controller로 전달하고, 컨트롤러는 이 값을 사용하여 서비스에서 데이터를 조회.


---
14. Controller 메서드 파라미터
### < Controller에서 @RequestParam을 DTO로 받는 이유 >
> 컨트롤러에서 @RequestParam을 DTO로 받는 것은 코드의 가독성, 유지보수성, 확장성을 높이는 좋은 설계 방식.
> 파라미터를 하나의 객체로 묶어 관리하고, 유효성 검사를 적용하며, 비즈니스 로직을 더욱 명확하고 일관되게 처리가능.

1. 데이터 캡슐화 및 관리 용이.
  - DTO를 사용하면 파라미터들을 하나의 객체로 묶어 관리 가능.   
  - 파라미터가 많거나 복잡할 때 유용.    
  - URL 쿼리 파라미터가 많다면, 각 파라미터를 DTO로 묶어 하나의 객체로 전달하면 코드가 더 깔끔하고 읽기 쉬워짐.

2. 유효성 검사 (Validation)
   - DTO 객체에 @Valid 또는 @Validated 어노테이션을 사용하여 유효성 검사를 간편하게 적용가능. 
   - 각 필드에 @NotNull, @Size, @Pattern 등의 제약 조건을 추가하여 서버 측에서 파라미터 검증을 처리가능.
   ~~~
    public class SearchCriteria {
    @NotNull
    private String name;
        @Min(18)
        private int age;
    }
   ~~~
   ~~~
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@Valid SearchCriteria criteria) {
        // 유효성 검증 후 비즈니스 로직 수행
    }
   ~~~

3. 파라미터 처리의 일관성
   - @RequestParam을 DTO로 받으면, 컨트롤러 메소드의 파라미터를 일관되게 처리가능. 
   - 여러 개의 파라미터를 개별적으로 처리하는 것보다 DTO로 묶으면 코드의 가독성과 유지보수성이 높아짐.

4. 확장성 (Scalability)
   - 비즈니스 로직이 커지거나, 파라미터가 추가되는 경우 DTO를 수정하는 것만으로도 손쉽게 확장가능. 
   - 새로운 파라미터가 추가되면 DTO 클래스에 새로운 필드만 추가하고, 컨트롤러는 동일한 형태로 처리가능.

5. 객체 지향적인 설계
   - 파라미터를 DTO 객체로 묶으면 객체 지향적인 설계를 구현가능.
   - 각 파라미터가 가진 의미를 명확히 하고, 코드에서 해당 객체를 통해 데이터를 전달함으로써 객체 지향의 이점을 살릴 수 있음.

6. 장점
   - 객체 매핑 (Binding)
     - Spring은 @RequestParam과 DTO 객체 간의 자동 매핑을 처리.
     - 복잡한 변환 로직을 따로 작성할 필요없음.
   - 재사용성
     - DTO는 다른 컨트롤러나 서비스에서 재사용할 수 있어 중복 코드 줄어듦.


### < 스프링 MVC 파라미터 바인딩 (컨트롤러에서 파라미터값 받는 방법) >
- @RequestParam을 DTO(Data Transfer Object)에서 생략 가능한 이유
  DTO를 파라미터로 받을 경우,
  Spring MVC의 데이터 바인딩(Data Binding) 기능(@ModelAttribute)에 의해
  쿼리 파라미터가 DTO 필드에 자동 매핑되므로
  개별 필드마다 @RequestParam을 사용할 필요 없음.

1. GET 쿼리파라미터 - @RequestParam 사용
    - @RequestParam은 ***개별 쿼리 파라미터를 개별 메서드 파라미터로 매핑*** 시 사용.
    - 각 파라미터마다 @RequestParam을 명시해주어야 하기 때문에 필드가 많을 경우 코드가 길어지고 불편.
    - 쿼리 파라미터가 하나하나 대응되어야 하므로 DTO 객체처럼 여러 필드를 묶어서 전달하는 경우에는
      @RequestParam으로 직접 받는 것이 번거롭고 불편할 수 있음.
   ~~~
    @RequestMapping("/search")
    public String search(@RequestParam("name") String name, 
                         @RequestParam("status") String status) {
        // name과 status를 각각 받아서 처리
    }
   ~~~

2. GET 쿼리파라미터 - DTO 사용
    - Spring MVC에서는 DTO 객체를 쿼리 파라미터와 자동으로 매핑가능.
    - @RequestParam 없이 DTO의 필드 이름과 동일한 쿼리 파라미터가 있을 경우
      자동으로 해당 값을 DTO 필드에 바인딩하기 때문에, 하나의 객체로 여러 파라미터를 묶어 처리가능.
    - Spring MVC에서는
      @RequestBody가 없고, 단순 객체 타입 파라미터인 경우
      기본적으로 @ModelAttribute 방식으로 처리되며,
      이때 쿼리 파라미터를 DTO의 필드 이름 기준으로 자동 바인딩한다.
   ~~~
    // 이 방식에서는 name, status 같은 쿼리 파라미터가 있으면, 
    //  Spring이 자동으로 RequestSearchProductionDto 객체의 필드에 매핑. 
    // 이 방식은 쿼리 파라미터가 많을 때 유용하고, 코드를 간결하게 유지가능.
    
    @RequestMapping("/search")
    public String search(RequestSearchProductionDto requestSearchProductionDto) {
        // method logic
    }
   ~~~

3. POST JSON requestBody - @RequestBody 사용 / POST Form 데이터 - @ModelAttribute  
   HTTP 요청의 본문에 포함된 데이터를 객체로 변환해서 받는 방식.
   1) POST 요청에서 JSON 데이터   
      ***POST 요청에서 JSON 데이터***를 Body로 받을 경우 ***@RequestBody***를 사용.   
      Jackson(ObjectMapper)가 HTTP Body의 JSON 데이터를 지정한 DTO 객체로 변환.   
      requestBody가 JSON인데 @RequestBody가 없는 경우,
      Spring은 Body를 파싱하지 않으므로 DTO 필드가 null이 되거나 바인딩에 실패.
   2) POST 요청에서 form 데이터
      POST 요청이라도 form 데이터의 경우는 ***@ModelAttribute*** 방식으로 바인딩됨.
      ex) content type이 application/x-www-form-urlencoded, multipart/form-data
   3) GET은 Body 사용 비권장
      HTTP 스펙 상 GET은 Body 사용을 권장하지 않기 때문에 
      일부 서버, 프록시, 캐시가 Body를 무시하므로 GET에 @RequestBody를 쓰지 않음.


---
## 15. 메시지 관리
### < properties 파일 한글 깨짐 문제 해결 >
1. IDE File Encoding   
   파일을 어떻게 저장할지 결정하는 설정.   
   개발자가 파일을 디스크에 저장할 떄 사용할 문자 인코딩 규칙을 지정.   
   해당 설정이 잘못되면 파일 자체가 꺠진 상태로 저장됨.
    - 설정방법   
      FIle > Settings > Editor > Code Style > File Encodings에서 Default encoding for properties Files를 UTF-8로 설정.
2. spring.messages.encoding   
   Spring이 파일을 어떻게 읽을지 결정하는 설정.
   애플리케이션 런타임에서 MessageSource가 .properties 파일을 해석할 때 사용할 문자 인코딩 지정.   
   IDE에서 정상적으로 저장된 파일이다로, 해당 설정이 없으면 실행 시 한글이 깨질 수 있음.   
   ~~~
   // applicatioin.properties 파일에 설정 추가
   
   spring.messages.encoding=UTF-8
   ~~~


### < 메시지 관리 방식 개선 >
> 비즈니스 에러는 의미와 계약이므로 Enum,
> Validation 에러는 표현과 UX이므로 properties가 최적.

1. 메시지 하드코딩의 문제점과 메시지 관리 방식 개선   
   하드코딩된 메시지는 표현(UI)과 로직을 강하게 결합시켜
   변경 비용이 크고, 일관성 유지와 확장이 어렵다.   
   메시지를 Enum(비즈니스 에러) 과 Validation 메시지(properties) 로 분리하면
   에러를 문자열이 아닌 정책(Policy) 단위로 관리할 수 있고,
   메시지 변경 및 국제화(i18n)에도 유연하게 대응할 수 있다.   

2. 메시지 하드코딩 비권장 이유
   1) 메시지 변경 시 코드 수정 필요   
      - 문구 수정에도 코드 변경 및 배포 필요
      - 비즈니스 로직과 표현 책임이 섞임
   2) 메시지 일관성 관리 어려움
      - 같은 에러에 대해 서로 다른 문구 사용 가능성 증가
      - UX 및 CS 대응 시 혼란 발생
   3) 에러 추적 및 정책 파악 어려움
      - 전체 에러 목록을 한눈에 파악하기 어려움
      - 에러가 “정책”이 아니라 “산재된 문자열”로 존재
   4) 다국어(i18n) 대응 불가
      - 언어 추가 시 코드 수정 필수
      - 확장 비용이 매우 큼

3. Enum / Validation 메시지 분리의 의미   
   Enum / Validation 메시지 분리는 단순 리팩토링이 아니라
   규칙 · 표준 · 계약을 코드로 표현하는 설계 결정이다.    
   메시지를 문자열이 아닌 정책(Policy) 으로 다루게 된다.   

4. 서버 에러 코드 · 메시지 관리 방식
   - 서버 에러 코드, 메시지를 properties로 분리할지 판단 기준
     - Enum 메시지가 사용자에게 그대로 노출되는 문구라면 → 분리 고려.
     - 에러 코드의 의미 설명(계약) 이라면 → Enum 유지.
   1) messages.properties로 메시지 관리 특징
      - 메시지 중앙 집중 관리
      - 다국어 지원
      - 코드에서 메시지 키가 흩어질 수 있음
      ~~~
      // messages.properties
      error.product.already-exists=이미 등록된 상품입니다.
      ~~~
      ~~~
      messageSource.getMessage("error.product.already-exists", null, locale);
      ~~~
   2) Enum 기반 에러 코드 관리 특징
      - Enum 기반 에러 코드 관리 방식   
        의미와 계약(API 스펙)을 중심으로 에러를 관리하는 방식
      - 에러 정책 중앙 집중 관리 가능
      - 상태 코드와 에러 의미의 강한 결합   
        잘못된 상태 코드 반환 가능성 감소 및 일관된 API 응답 보장.
      - 타입 안정성   
        문자열이 아닌 Enum 타입을 사용해 오타 및 잘못된 에러 코드 사용을 컴파일 타임에 방지.   
      - API 스펙 관리에 유리
        클라이언트와의 게약인 에러 코드를 코드 값 변경 없이 의미 유지 가능.
      - 비즈니스 의미 명확   
        단순 메시지가 아닌 도메인 에러로 표현해, 로그, 모니터링, 장애 분석 시 의미 파악에 용이. 
      - 메시지 변경 시 코드 수정 및 배포 필요
      - 다국어 대응이 어려움
      ~~~
      public enum ErrorCode {
          PRODUCT_ALREADY_EXISTS("P001", "이미 등록된 상품입니다."); 
      }
      ~~~
   3) Enum + MessageSource 혼합 특징   
      - 에러 정책과 표현 분리   
        에러 코드는 Enum으로, 메시지는 properties로 관리.
      - 메시지 변경 및 다국어 모두 대응 가능.
      ~~~
      public enum ErrorCode {
          PRODUCT_ALREADY_EXISTS("P001", "error.product.already-exists");
      }
      ~~~
      ~~~
      messageSource.getMessage(errorCode.getMessageKey(), null, locale);
      ~~~   

5. Validation 예외 메시지 관리
   1) Validation 메시지   
      아래의 특성으로 인해 properties 관리가 최적.
      - 사용자 노출 UI 메시지
      - 필드 단위
      - 케이스 수 많음
      - 문구 변경 빈번
      - 다국어 대응 가능성 높음
   2) ValidationMessages.properties 사용 시 이점
      - 표현 책임 분리 (SRP)
      - DTO / Validator: 규칙
      - properties: 표현
      - 메시지 변경 시 코드 수정 없이 대응 가능
      - 다국어 확장 용이
      - 메시지 일관성 확보
   3) Validation 예외를 Enum으로 관리해야 하는 예외적 경우
      - B2B API에서 Validation 실패도 에러 코드로 내려야 하는 경우
      - 클라이언트가 에러 코드 기반 로직을 수행하는 경우
   4) Validation 메시지 키 작성 규칙
      1. 도메인 키 방식 권장
         - 검색성과 예측 가능성 높음
         - 팀 협업 시 규칙 명확
         - 필드별 메시지 커스터마이징 가능
         - 다국어 대응 용이
      2. FQCN 방식 비권장
         - 어떤 도메인/필드 메시지인지 알 수 없음
         - UX 메시지로 부적합
         - 하나의 메시지 변경 시 모든 필드에 영향 끼침

6. 메시지 파일 역할
   1) messages.properties
      - 비즈니스 에러
      - 시스템 메시지
      - 공통 메시지
   2) ValidationMessages.properties
      - Bean Validation 기본 메시지 파일
      - messages.properties와 충돌 방지를 위해 Validation 전용 prefix 사용 권장.

7. 기타 주의사항
   - {min}, {max} placeholder 활용해 하드코딩 제거
   - = 뒤 공백은 실제 메시지에 포함되므로 주의

8. 최종 결정 사항
   1) 서버 비즈니스 에러
      - 의미+계약(API스펙)의 일부이므로 Enum 유지.
   2) Validation 에러
      - 문구 변경시 코드를 수정하지 않도록 하기 위해 ValidationMessages.properties로 분리.


### < 서버 에러 메시지 하드코딩 제거: placeholder 기반 메시지 분리 전략 >
1. 해결해야 할 문제
    1) 하드코딩 된 메시지 분리   
       자바코드를 포함하는 하드코딩 된 에러 메시지를 properties 파일로 이동시키려고 한다.   
       이 때, properties 파일 안에서 자바코드를 직접 사용할 수 없다.
    2) 현재 코드 방식의 문제점
        - 메시지와 로직의 강결합.   
          메시지가 CartService에 의존.   
          ErrorCode가 도메인 정책 값의 소유자처럼 동작하고 있다.
          CartService 수정 시 ErrorCode까지 저장.
        - 국제화 불가.
        - 정책 변경 시 코드 수정 필요.   
          메시지 문구 수정 시 배포 필요.
   ~~~
   @Getter
   @AllArgsConstructor
   public enum CartErrorCode implements CommonErrorCode {
       // 장바구니 validation check
       LIMIT_CART_MAX_SIZE(HttpStatus.BAD_REQUEST.value(), "장바구니에는 최대 " + CartService.CART_MAX_SIZE + "건만 추가 가능합니다.")
       ;

       private final int statusCode;
       private final String errorMessage;
   }
   ~~~

2. properties 파일의 역할과 한계    
   properties 파일은 순수 리소스 파일로, 문자열만 저장 가능하다.
   따라서 properties 파일이 자바 코드를 포함할 수 없다.
   이 때 스프링은 MessageSource를 통해 '문자열+인자(Object[])'만 치환한다.

3. 해결방법
   위와 같은 방식 대신, 'placeholder + 런타임 인자 전달' 방식으로 동일한 효과를 낼 수 있다.
    1) properties + placeholder 사용   
       properties에 메시지를 하드코딩해 사용하면 자바코드와 properties 파일 모두 값을 수정해야한다.
       따라서 정책 값이 메시지와 코드에 중복 정의되어 둘 다 관리가 필요하므로 정합성 붕괴와 실수 확률이 증가한다.   
       따라서 properties 파일에서 메시지를 관리하되, placeholder를 이용해 인자를 전달해 사용한다.
       전달인자는 런타임에 placeholder에 전달된다.
       ~~~
       error.cart.limit.max=장바구니에는 최대 {0}건만 추가 가능합니다.
       ~~~
    2) Enum에 하드코딩 메시지를 키로 변경
       ~~~
       @Getter
       @AllArgsConstructor
       public enum CartErrorCode implements CommonErrorCode {
 
           LIMIT_CART_MAX_SIZE(
               HttpStatus.BAD_REQUEST.value(),
               "error.cart.limit.max"
           );
 
           private final int statusCode;
           private final String messageKey;
       }
       ~~~
    3) 예외 발생 시 값 주입   
       예외 발생 시 값 주입해 해당 예외가 발생하면 에러 메시지를 출력한다.
       ~~~
       throw new CartException(
           CartErrorCode.LIMIT_CART_MAX_SIZE,
           new Object[]{ CartService.CART_MAX_SIZE }
       );
       ~~~

4. 더 나은 확장 (선택)
   메시지와 정책 값 모두 외부 설정으로 분리하면 환경별 정책 변경이 코드 수정 없이 가능하다.   
   - 운영 환경별 설정 가능
   - 메시지 구조는 그대로 유지
   ~~~
   //application.properties
   cart:
      max-size: 30 
   ~~~
   ~~~
   // CartService
   @Value("${cart.max-size}")
   private int cartMaxSize;
   ~~~


### < 메시지 호출 테스트 문제해결 : Spring Validation과 ExceptionHandler에서 메시지 처리 >
1. 발생에러
   ~~~
   2026-01-14T09:42:33.047+09:00  WARN 21560 --- [MyECommerce] [    Test worker] c.m.M.e.handler.CommonExceptionHandler   : error.default.invalid.value
   
   org.springframework.web.bind.MethodArgumentNotValidException: Validation failed for argument [0] in public org.springframework.http.ResponseEntity<com.myecommerce.MyECommerce.dto.production.ResponseProductionDto> com.myecommerce.MyECommerce.controller.ProductionController.registerProduction(com.myecommerce.MyECommerce.dto.production.RequestProductionDto,com.myecommerce.MyECommerce.entity.member.Member): [Field error in object 'requestProductionDto' on field 'code': rejected value [!!INVALID!!]; codes [Pattern.requestProductionDto.code,Pattern.code,Pattern.java.lang.String,Pattern]; arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [requestProductionDto.code,code]; arguments []; default message [code],[Ljakarta.validation.constraints.Pattern$Flag;@687f62a5,^[a-zA-Z0-9][a-zA-Z0-9\-]*$]; default message [상품코드는 영문자, 숫자만 사용 가능합니다.
   특수문자는 -만 허용하며 첫 글자로 사용 불가합니다.]]
   // ... 생략
   
   MockHttpServletRequest:
   HTTP Method = POST
   Request URI = /production
   Parameters = {}
   Headers = [Content-Type:"application/json;charset=UTF-8", Content-Length:"110"]
   Body = {"code":"!!INVALID!!","name":"정상 상품명","category":"WOMEN_CLOTHING","description":null,"options":null}
   Session Attrs = {SPRING_SECURITY_CONTEXT=SecurityContextImpl [Authentication=UsernamePasswordAuthenticationToken [Principal=com.myecommerce.MyECommerce.entity.member.Member@1b26fac2, Credentials=[PROTECTED], Authenticated=true, Details=null, Granted Authorities=[SELLER]]]}
   
   Handler:
   Type = com.myecommerce.MyECommerce.controller.ProductionController
   Method = com.myecommerce.MyECommerce.controller.ProductionController#registerProduction(RequestProductionDto, Member)
   
   Async:
   Async started = false
   Async result = null
   
   Resolved Exception:
   Type = org.springframework.web.bind.MethodArgumentNotValidException
   
   // ... 생략
   
   MockHttpServletResponse:
   Status = 400
   Error message = null
   Headers = [Content-Type:"application/json", X-Content-Type-Options:"nosniff", X-XSS-Protection:"0", Cache-Control:"no-cache, no-store, max-age=0, must-revalidate", Pragma:"no-cache", Expires:"0", X-Frame-Options:"DENY"]
   Content type = application/json
   Body = {"errorCode":"INVALID_VALUE","errorMessage":"error.default.invalid.value\n상품코드는 영문자, 숫자만 사용 가능합니다.\n특수문자는 -만 허용하며 첫 글자로 사용 불가합니다."}
   Forwarded URL = null
   Redirected URL = null
   Cookies = []
   
   JSON path "$.errorMessage"
   <Click to see difference>
   
   java.lang.AssertionError: JSON path "$.errorMessage" expected:<유효하지 않은 값입니다.
   상품코드는 영문자, 숫자만 사용 가능합니다.
   특수문자는 -만 허용하며 첫 글자로 사용 불가합니다.> but was:<error.default.invalid.value
   상품코드는 영문자, 숫자만 사용 가능합니다.
   특수문자는 -만 허용하며 첫 글자로 사용 불가합니다.>
   at org.springframework.test.util.AssertionErrors.fail(AssertionErrors.java:61)
   at org.springframework.test.util.AssertionErrors.assertEquals(AssertionErrors.java:128)
   // ... 생략
   at com.myecommerce.MyECommerce.controller.ProductionControllerTest.failRegisterProduction_invalidCode(ProductionControllerTest.java:157)
   // ... 생략
   ~~~
2. 에러발생원인   
   Validation관련 ExceptionHandler는 DefaultErrorCode.java에 존재하는 에러코드의 메시지를, Validation 메시지에 앞에 붙여 사용하고 있었다.
   이 때 서버 비즈니스 메시지에 대해 하드코딩에서 messages.properties 파일로 관리하게 되면서 기존에 잘 수행되던 테스트가 깨지게 되었다.   
   MockMvc 기반 컨트롤러 테스트에서는 MessageSource가 자동으로 메시지 key르 문자열로 변환해주지 않는다.
   그래서 기존에 메시지가 출력되던 위치에 메시지 key 자체가 출력되고 테스트 예상값과 동일하지 않게 되어 테스트가 실패한 것이다.
   에러 메시지를 파일로 관리하기로 결정한 순간, 메시지 생성 책임은 전부 Handler로 이동한다.
   따라서 messages.properties를 사용하는 경우, 명시적인 MessageSource 설정 또는 메시지 해석 로직을 직접 호출해야 한다.   
   결론적으로, ***메시지 관리 구조를 개선하면서 Exceptionhandelr에 메시지 생성 로직을 누락***해 에러가 발생했다.
3. 해결방법
   Exceptionhandelr에 messageSource를 이용해 메시지 생성 로직을 추가해, 생성된 메시지를 응답으로 전달한다.   
   테스트코드 수정은 필요없다.
   ~~~
   public class CommonExceptionHandler{
      /** DTO 유효성검사 예외처리 **/
      @ExceptionHandler(MethodArgumentNotValidException.class)
      @ResponseStatus(HttpStatus.BAD_REQUEST)
      protected ResponseEntity<CommonErrorResponse> methodArgumentNotValidExceptionHandler(
              MethodArgumentNotValidException e) {
          // 서버에러객체생성
          DefaultErrorCode errorCode = INVALID_VALUE;

          // 서버 에러 메시지 생성
          String errorCodeMessage = messageSource.getMessage(
                  errorCode.getErrorMessage(),
                  null,
                  LocaleContextHolder.getLocale()
          );

         // Validation Bean 에러 메시지 (에러메시지 여러개일 수 있음, BindingResult에서 가져옴)
         String defaultErrorMessage = e.getBindingResult().getFieldError().getDefaultMessage() == null ?
                 "" : "\n" + e.getBindingResult().getFieldError().getDefaultMessage() ;

          // 응답 객체 생성
          CommonErrorResponse errorResponse = CommonErrorResponse.builder()
                  .errorCode(errorCode) // DefaultErrorCode.INVALID_VALUE
                  .errorMessage(errorCodeMessage + defaultErrorMessage)
                  .build();

          // 응답 객체 반환
          // HttpStatus에 상태코드를 담아서 errorResponse와 함께 Http 응답으로 내려보낸다.
          return new ResponseEntity<>(errorResponse,
                  Objects.requireNonNull(HttpStatus.resolve(errorCode.getStatusCode())));
   }
   ~~~ 
4. Validation 메시지는 자동으로 key에 대한 메시지 생성되는 이유   
   Spring Validation은 내부적으로 MessageSource를 이미 사용하고 있다.   
   Hibernate Validator가 동작하면 Spring MessageSource가 실행되어
   ValidationMessages.properties에서 key에 대한 value 값을 가져온다.
5. 메시지 key와 자동 변환   
   Validation 메시지는 Spring이 대신 MessageSource를 호출해주지만
   비즈니스 에러 메시지는 절대 자동 변환되지 않는다

  
### < 메시지 조회 Locale 문제 해결방법 >
1. 발생에러   
   Bean Validation에서 발생한 메시지를 MessageSource로 가져올 때 Locale이 엉뚱하게 잡히는 문제.
   ~~~
   No message found under code 'validation.product.id.positive' for locale 'en'
   ~~~
2. 해결방법    
   글로벌 서비스가 아니라면, 메시지 조회 시 Locale을 고정해 해결 가능하다.   
   LocaleContextHolder.setLocale(Locale.KOREAN)를 전역 상수를 만들어 MessageSource 조회 시 매번 Locale을 지정한다.
   ExceptionHandler 전역상수로 고정해 메시지 생성 시마다 호출하는 경우, 테스트 환경에서도 Locale이 바뀌는 문제를 신경쓰지 않아도 된다. 
   별도로 테스트에서 Locale 설정이 필요 없게된다.
   ~~~
    public class CommonExceptionHandler {
    
        private static final Locale DEFAULT_LOCALE = Locale.KOREAN;  // 전역 상수
    
        @Autowired
        private MessageSource messageSource;
    
        private String createMessage(String key, Object... args) {
            return messageSource.getMessage(key, args, DEFAULT_LOCALE);
        }
        // ...
    }
   ~~~


### < MessageSource placeholder 규칙으로 인한 메시지 파싱 오류와 해결 >
~~~
// messages.properties
error.cart.limit.max.size=장바구니에는 최대 {value}건만 추가 가능합니다.
~~~
1. 발생에러   
   ~~~
    can't parse argument number: value
    java.lang.IllegalArgumentException: can't parse argument number: value
    // ... 생략
    Caused by: java.lang.NumberFormatException: For input string: "value"
   ~~~
2. 에러발생원인   
   MessageSource는 이름 기반 바인딩을 지원하지 않는다.
   순서 기반 placeholder만 사용 가능하다.
3. 해결방법
   ~~~
    // messages.properties
    error.cart.limit.max.size=장바구니에는 최대 {0}건만 추가 가능합니다.
   ~~~


## 16. HTTP 상태코드
1. Bad Request(400)의 정확한 의미   
   클라이언트가 ***봰 요청이 형식, 내용적으로 잘못되었을 때 발생***하는 HTTP 상태코드.
    - 잘못된 요청 파라미터
    - 요청 상태가 현재 서버 자원 상태와 논리적으로 맞지 않음.
    - ***클라이언트가 요청을 수정하면 재시도 가능.***
2. 400 또는 Conflict(409)    
   현재 리소스 상태상 수정 불가하거나 요청 자체는 형식적으로 문제 없지만 ***비즈니스 규칙을 위반한 경우.***
3. Forbidden(403)   
   인증은 되었지만 권한없음.
4. Not Found(404)   
   요청한 리소스가 존재하지 않음.   
   예를 들면 상품이 존재하지 않는 경우.
5. 비즈니스 로직에 적절한 HTTP 상태코드
    - 비즈니스 규칙 위반도 '요청이 잘못되었다'라고 해석 가능.
    - 비즈니스 로직에서 발생했다고 400 상태코드가 틀린 것은 아니다.   
      하지만 클라이언트가 무엇을 잘못했는지를 기준으로 상태 코드를 나누면 API 품질이 올라간다.
    1) 비즈니스 에러는 전부 400 전략
        - 에러 미시지 & 에러 코드로 충분히 표현 가능
        - 상태 코드 분기 단순
        - 내부 서비스나 B2C 프론트에서는 큰 문제 없음.
        - API 소비자가 에러 의미를 상태코드로 판단 불가.
        - REST 의미 약화
        - 프론트/외부 API 연동 시 분기 처리 어려움


## 17. null과 ObjectUtils.isEmpty()
### < ObjectUtils.isEmpty() 남용 문제 >
1. ObjectUtils.isEmpty() 사용 시 문제점   
   내부적으로 null, Optional.empty, 빈 문자열, 빈 컬렉션, 배열 길이=0 을 검증한다.
   ObjectUtils.isEmpty는 “범용 유틸”이고 도메인 코드에서는 의도가 흐려진다.
   이 코드를 보면 'member가 null인가?', 'member 내부 필드가 비었나?', '빈 컬렉션인가?', 'Optional인가?'
   질문하게 되므로 의도를 전혀 알 수 없음
   즉, DTO 객체에 쓰면 의미가 모호해진다.
   따라서 null 체크는 명시적으로 하는 게 더 좋다.
   내가 누린 것은 범용성의 장점이지, 도메인 코드의 장점이 되지 않는다.
   ~~~
   if (ObjectUtils.isEmpty(member)) {
      //... 
   }
   ~~~

2. ObjectUtils.isEmpty() 사용을 권장하지 않는 경우
    1) 도메인 객체
    2) 비즈니스 규칙 검증
    3) Bean Validation 로직

3. ObjectUtils.isEmpty() 사용하는 경우   
   타입과 의미가 불명확한 경계 계층에서 사용.
    1) Controller / API 입구   
       의미보다 방어가 중요한 지점.   
       외부 입력에 대해 타입이 불명확하지만 어쨌든 비어있으면 안 되는 경우.
    2) 유틸 / 공통 라이브러리 코드   
       의미 표현보다 범용성이 더 중요한 경우.
    3) Map / Collection 처리
    4) Optional 처리


---
## 18. 자바 참조 전달 
1. 문제점   
   재할당은 호출자에게 반영되지 않으므로, targetRedisCartDto는 재할당이 외부에 반영되지 않는다.
   즉, 호출부에서 targetRedisCartDto = null 상태였다면 호출한 메서드 실행 후에도 여전히 null일 수 있다.  
   이는 논리 버그로 꼭 수정해야하는 부분이다. (신규 상품 장바구니에 추가하는 정상 시나리오 테스트케이스 작성 시 발견)
   ~~~
        // 상품옵션수량 셋팅
        private void setAddCartData(RedisCartDto targetRedisCartDto,
                                    RedisCartDto foundOptionDto,
                                    RequestCartDto requestCartDto) {
                // ...
                // 상품수량 (신규수량으로 초기화)
                targetRedisCartDto = foundOptionDto;
                targetRedisCartDto.setQuantity(requestCartDto.getQuantity());
        }
   ~~~
   - 위 코드에서 targetRedisCartDto는 지역 변수로 재할당되며, 이 변경은 호출자에게 전혀 전달되지 않는다.

2. 자바에서 객체 전달   
   > 객체 필드 변경은 호출자에 반영되지만, 객체 참조 재할당은 호출자에게 반영되지 않는다.
   > 따라서 값을 만들 수도 있는 메서드는 반드시 반환해야 한다

   ***자바는 참조 자체를 전달하지 않고 참조값(주소값)을 복사해 전달한다.***
   따라서 targetRedisCartDto을 setAddCartData()에 전달하면, 
   이때는 참조값의 복사본을 전달하는 것이다.
   즉, target은 객체를 가리키는 주소값이고 메서드로 전달될 때는 주소값이 복사되어 전달되는 것이다.     
   이 떄, ***값의 변경과 참조 재할당은 완전히 다르다.***
   따라서 객체 내부 값 변경은 문제가 없다. 같은 객체를 가리키고 있고, 내부 필드만 바뀌기 때문이다.
   그렇기 때문에 targetRedisCartDto을 setAddCartData()를 사용하면 값은 정상적으로 저장된다.   
   반면, ***참조 객체를 새 객체로 바꾸는 경우엔 문제가 발생한다.***
   같은 targetRedisCartDto이지만 호출한 곳에서는 null, setAddCartData()에서는 새 객체를 가리킨다.   
   null에 새 객체를 대입한 경우엔 저장이 안된다.
   따라서 targetRedisCartDto.setProductCode()가 저장되는 경우는, 애초에 targetRedisCartDto는 null이 아니었기 때문이다.

3. 해결방법
   setAddCartData() 구조상 두 가지 책임을 동시에 갖고 있어 반환값을 가지는 메서드여야 한다.
   기존 객체를 수정할 수도 있고, 새 객체를 만들어야할 수도 있기 때문이다. 
   (Redis에 이미 있으면 수정, Redis에 없으면 새로 생성)    
   만약 void 타입으로 반환, 참조 재할당으로 처리하면 자바 특성상 반드시 버그가 생긴다.   
   따라서 setAddCartData()가 수정된 값을 반환하도록 수정한다.
   가장 안전하고 의도가 명확한 구조이다.


---
## 19. 비즈니스 정책의 구분
### < 비즈니스 정책의 책임 범위: 판단 로직과 구현 로직의 분리 >
비즈니스 정책이란 말이 붙어 있더라도,
모든 정책이 CartPolicy로 가는 게 아니라,
의사결정이 필요한 정책만 CartPolicy에 모이고,
항상 실행되는 구현 규칙은 Service 코드에 유지된다.   

모든 규칙을 전부 CartPolicy 같은 정책 클래스로 몰아넣는 건 오히려 구조를 망가뜨릴 수 있다.
CartPolicy에 모이는 것은 모든 정책이 아니라, ‘판단이 필요한 정책’이다.
어떤 상황에서 예 / 아니오, 허용 / 거부, 연장 / 미연장처럼 선택이 갈리는 규칙들만 모인다.    


### < 장바구니 만료 정책과 Redis TTL 구현의 책임 분리 >
장바구니 TTL 자체는 비즈니스 정책이지만,
Redis TTL을 ‘어떻게’ 적용할 것인지는 인프라/애플리케이션 계층의 책임이다. 
그래서 CartPolicy가 아닌 Service에서 처리하는 게 맞다.   

“장바구니는 30일 후 만료된다”, “상품 추가 시 만료가 연장된다”는 명백한 비즈니스 규칙입니다.   
하지만 이 규칙을 CartPolicy에 그대로 구현하면 문제가 생긴다.
CartPolicy가 Redis, TTL, Key같은 기술 개념을 알게 되는 순간,
정책 계층에 인프라 지식이 침투하게 되기 때문이다.       

“장바구니는 30일 후 만료된다”는 표현은 비즈니스가 이해하는 언어이고,
“Redis에서 namespace:userId 키에 EXPIRE를 건다”는 표현은 기술이 이해하는 언어다.   
CartPolicy는 기술 언어가 아닌 비즈니스의 언어만을 다루는 곳이어야 한다. 
따라서 CartPolicy의 역할은 “이 상황에서 장바구니 만료를 연장해야 하는가, 아니면 연장하지 말아야 하는가”를 판단하는 데 한정된다. 

예를 들어, 상품을 추가했을 때는 만료를 연장해야 하고, 
단순 조회만 했을 때는 연장하지 말아야 한다는 판단이 여기에 해당한다. 
CartPolicy는 이 판단만 알면 충분하고, 
그 판단이 Redis인지, DB인지, 파일인지와는 전혀 상관이 없어야 한다.   

만약 CartPolicy 안에서 Redis TTL을 직접 다루기 시작하면, 정책 코드가 
“Redis는 Hash에 TTL을 못 건다” 같은 지식을 갖게 된다.
그러면 CartPolicy는 더 이상 순수한 정책이 아니라, 특정 기술에 강하게 묶인 코드가 된다. 

그 결과, 나중에 Redis를 걷어내고 DB로 전환하거나  
장바구니 저장 방식을 변경하려 할 때, 정책 코드까지 함께 수정해야 하는 구조가 된다.
이런 결합을 피하기 위해 TTL 정책과 구현은 분리되어야 한다.


---
## 20. Redis와 반환된 HashMap 순서 미보장
1. 순서 미보장 케이스    
   Map은 순서가 보장되지 않는다.
   따라서 keySet()과 values()의 순서는 같은 Map 인스턴스 기준으로 보통 일치하지만, 명세상 보장되지는 않는다.
   cart.keySet(), cart.values()로 서로 다른 iteration을 수행을 수행하면 순서가 보장되지 않는다는 것이다.
   아래의 코드는 itemStock.get(i) <-> targetCartItemList.get(i) 매핑을 전제로 해야 한다.
   ~~~
   // key, DTO 생성 (별도 반복)
   List<String> itemKeys = cart.keySet().stream().toList();
   List<ResponseCartDetailDto> targetCartItemList = cart.values().stream()
      .map(...)
      .toList();
   
   // 재고 조회
   List<Object> itemStock = redisMultiDataService.getMultiData(itemKeys);
   ~~~
2. 순서 보장 케이스
   순서를 보장하려면 iteration은 반드시 한 번이어야 한다.
   Map을 두 번 돌리는 순간, 설게가 아닌 운에 순서를 맡기는 것이다.
   아래와 같은 경우는 같은 루프 안에서 key, dto, index를 묶는다. 
   index 계약을 개발자가 직접 만든 것이다. 
   따라서 Redis 결과 순서만 믿으면 되고 Map 순서에 더 이상 의존하지 않아도 된다.
   ~~~
   // 함께 반복문 수행
   for (Map.Entry<Object, Object> entry : cart.entrySet()) {
       itemKeys.add(key);
       targetCartItemList.add(dto);
   }
   ~~~


---
## 21. 조회
### < Context 객체 (미사용, 스터디는 필요) >
작업용 객체

### < MapStruct (미사용, 스터디는 필요) >


---
## 22. DB 키 사용 원칙
1. PK(id)   
   DB 내부 식별자로, 관계(FK) 기준이다.
   변경되지 않는 값으로, 내부 참조(FK, JOIN) 시 사용한다.

2. 유니크키(code, userId...)    
   비즈니스 식별자로, 외부 입력(API 요청)과 외부 노출(응답, 화면)용으로 사용한다. 

3. 외부 요청과 내부 처리 분리    
   ***외부에서는 비즈니스 의미가 있는 도메인(유니크) 키로 요청을 받고, 
   내부에서는 변경 불가한 ID로 변환해 처리한다.***    
   만약 옵션이 상품에 종속이라면, product_id, option_code 조합으로 유니크를 보장한다.
   이는 DB 관계와 JOIN의 기준이 PK이기 때문이다.

4. 시스템 기준점과 도메인 의미의 구분
   시스템 전반의 기준점이 되는 엔티티는 PK로 식별하고, 
   특정 도메인 내에서 선택 의미가 중요한 값은 유니크 키로 식별한다. 
   예를 들면, 주문 요청에서는 productId + optionCode 조합을 사용한다.

   
---
## 23. Java에서 ""와 """ 차이
기능의 차이는 없고, 표현과 가독성에 차이가 있다.
1. " "    
   - 일반 문자열 (String Literal)   
   - 한 줄 기준
   - 줄바꿈 시 \n 필요
   - 내용이 길어지면 가독성 떨어짐
2. """ """
   - Text Block
   - Java 15 버전부터 사용 가능
   - 여러 줄의 문자열 표현 가능
   - 개행, 들여쓰기를 그대로 유지


---
## 24. IllegalStateException vs OrderException
### < order.assignOrder()에서 던질 Exception 결정 >
OrderItem Entity의 assignOrder() 메서드는 
이미 주문이 할당된 객체에 다시 주문을 할당하려고 하면 안된다는 객체 상태 규칙을 강제하고 있다.   
이 때 어떤 예외를 발생시켜야할까?

1. IllegalStateException 
   IllegalStateException은 java 관점에서 객체 상태가 유효하지 않음을 나타내기에 적합하다.   
   단순 내부 상태 검증용으로 적합.    

2. 커스텀 Exception (OrderException)   
   사용자 정의 예외를 생성했다면, 목적은 주문 관련 비즈니스 예외를 일관되게 처리하는 것이다.
   비즈니스 규칙과 관련된 상태 위반 시 사용 추천.   
   1) 장점
      - 서비스 계층에서 catch 하거나 글로벌 핸들러에서 일괄 처리 가능.
      - 예외 메시지를 비즈니스 로직에 맞춰 구체화 가능.
      - IllegalStateException 대신 도메인 의미 명확히 표현.
   2) 단점
      - 순수 java 상태 체크라면 굳이 도메인 에외를 쓰는 것이 오버헤드처럼 느껴질 수 있음.
      - 다른 개발자가 표준 Java 예외가 아님을 인지해야함.

3. 예외를 발생시킬 코드
   ~~~
    public void assignOrder(Order order) {
        if (this.order != null) {
            throw new IllegalStateException("Order has already been assigned");
        }
        this.order = order;
    }
   ~~~

4. 결정   
   assignOrder는 주문 도메인 규칙과 직결된 로직으로, 커스텀 Exception을 던지는 것이 명확하고 통일성도 있다.


### < IllegalStateException vs OrderException 사용 가이드라인 >
1. IllegalStateException    
   객체 상태가 잘못되어 버그를 방지할 때 사용.
   사용자의 문제가 아닌 논리적 버그/프로그래밍 오류일 때 사용.
   - Java 표준 런타임 예외. (java 런타임 시 발생하는 RuntimeException의 하위 클래스 중 하나)
   - ***객체가 현재 상태에서 수행할 수 없는 작업을 시도했을 때 던지는 예외.***
   - 객체나 환경의 상태가 메서드 호출을 허용하지 않는 경우 사용.    
     따라서 프로그래밍 관점에서 상태오류, 논리적 버그, 객체 상태 불일치 시 사용.
   - 발생 가능한 경우 코드 예시
     ~~~
     Optional<String> opt = Optional.empty();
     opt.get(); // IllegalStateException: No value present
     ~~~
   - 대부분 코드 버그로, catch보다 코드의 수정이 우선되어야 한다.
   - 내부 상태 검증용, 로직 오류 방지용으로 사용한다.

2. 커스텀 도메인 Exception    
   비즈니스 규칙 위반을 나타내고 사용자/서비스 레이어에서 의미 있는 처리가 필요할 때 사용.    
   - 도메인 관련 비즈니스 규칙 위반.
   - 주문 중복, 재고 부족 등 도메인 관점에서 비즈니스 규칙을 위반한 경우 사용.    
     이미 주문이 할당된 객체에 다시 주문을 시도하거나, 주문 금액이 0보다 작은 경우 예외를 발생시킨다.
   - 비즈니스 처리 관점에서 service, controller에서 catch 후 사용자 메시지 반환이 필요하면 사용한다.
   - 도메인 규칙 위반, service/비즈니스 레벨에서 의미있는 예외 발생 시 사용한다.

- 런타임 예외    
  check 예외가 아니므로 throws 선언 없이 발생 가능.


---
## 25. 주문 번호 생성 방법
주문 번호는 식별자가 아닌 표시용이다.
기계가 계산하는 값이 아닌 사람이 참고하는 값이다.
검색, 조회, API 식별로는 PK를 사용하고, 
고객센터, CS 응대 등 UI용으로는 주문번호를 사용한다.

1. 현재시간 + DB에서 마지막 번호 조회 후 + 1 (비추천)    
   절대 추천하지 않는다. 특히 주문 도메인에서는 지뢰다.   
   동시성 문제에서 치명적이기 때문이다.
   주문 테이블은 계속 커질 테고, 마지막 번호 조회는 항상 핫스팟이기 때문에 주문이 많아질수록 병목현상이 발생한다.
   또한 만약 날짜가 바뀌는 시간 대의 주문인 경우, 동시 주문이 들어오게 되면 실패 케이스 발생하게 된다.
   마지막 번호 조회 시 락을 걸어도 범위락, 테이블 락으로 인해 성능 저하, 트랜잭션 충돌이 발생할 수 있다.

2. DB 시퀀스 기반 (정석)    
   동시성 부분에서 완벽하고 최고의 성능이며 단순하다.
   MySQL 이라면 AUTO_INCREMENT, 전용 sequence 테이블을 사용해 번호를 생성한다.
   1) 시퀀스
      ~~~
      order_no = yyyyMMdd + LPAD(sequence.nextval, 6)
      ~~~
   2) 주문 테이블 PK 사용 (AUTO_INCREMENT)
      별도의 시퀀스를 새로 만들 필요 없이 
      주문을 저장 후 생성되는 PK 자동 증가값인 주문ID를 조회해 주문 번호를 수정하는 방법이다.
      중복도 없고 락이 필요없기 때문에 성능 문제가 없다.
      단, 주문 번호가 연속적이지 않겠지만, 연속성은 비즈니스 가치가 아니다. 
      유니크함과 생산 안정성이 가치이다.
      JPA 기준으로 IDENTITY 전략이라면 insert 즉시 발생해 save 이후 바로 id 사용이 가능하다.
      그리고 주문 Entity 저장 직후 다시 save한다.
      ~~~
      @GeneratedValue(strategy = IDENTITY)
      ~~~
      
3. UUID 기반   
   중복 걱정이 없다. 순서의 의미가 없고, 사람이 보기엔 약간 불편할 수 있다.
   ~~~
   20260129-A8F3K9
   ~~~

4. Snowflake / ID Generator   
   분산 환경에서 사용.
   주문량이 매우 많을 때 사용.

5. 가능한 전략
   PK를 이용한 주문번호 생성 방법을 검토했다.
   하지만 PK는 자동증가이므로 ID는 반드시 계속 커진다.
   따라서 처음 결정한 자릿수 이상에 분명히 도달한다. 그래서 일정 자리수 요구는 현실과 충돌한다.
   그래서 의미를 바꿔 최소 자릿수만 지정하고 커지면 자연히 자릿수가 확장 가능하도록 제약이 없도록 할 수 있다. 
   하지만 현재 Entity에서 컬럼 사이즈는 16자릿수로 제한하고 있어, 
   PK를 이용한 주문번호를 생성하려면 길이 제한을 32로 변경하거나 제거하는게 올바르다.
   또한 길이를 제거한다고 해도 주문번호를 not null로 설정되어 있다.
   그런데 save() 시 주문 번호는 null이고, pk가 생성된 후 주문번호를 update 해야한다.
   따라서 여기서 또 DB 제약 위반이다.
   1) PK를 미리 알 수 있는 전략을 사용할 수 있다.    
      persist 전에 id 확보가 가능해 주문번호 생성이 가능하다.  
      따라서 insert 1번이면 되고 not null 유지가 가능해진다.
      ~~~
      @GeneratedValue(strategy = SEQUENCE)
      ~~~
   2) nullable 허용 후 즉시 채운다.   
      주문 insert 후 주문번호를 update 하므로 트랜잭션 안에서는 매우 짧은 시간만 null이다.
      외부에 노출되지 않기 때문에 실무에서도 많이 쓰는 방법이다.
      null 순간이 찜찜할 순 있지만 논리적으로는 항상 존재한다.
      기술적으로 생성 직후 잠깐 비어 있을 뿐이다.
   3) 날짜 + UUID 를 이용한다.
   
6. UUID   
   UUID(Universally Unique Identifier)는 전 세계에서 유일한 ID를 만드는 표준 방식이다.    
   길이는 128비트로 Java 기준 36자리 문자열이다.
   같은 UUID가 다시 나올 확률은 거의 0에 가깝기 때문에 사실상 유일하다.
   1) UUID 사용하는 경우
      - DB 시퀀스 없이 유니크한 식별자가 필요한 경우
      - 분산 시스템 / MSA에서 ID 충돌 방지 시 사용
      - 고객에게 보여줄 주문번호나 토큰 생성 시 안전
   2) 주문번호용으로 변형하기
      UUID는 길고 사람이 보기 어렵다.
      따라서 일부만 잘라서 쓰거나 날짜와 조합한다.
      중복 확률은 거의 0이며 길이도 적당해지고 사람이 읽기도 가능하다.
   3) 길이를 줄이면서 유니크함을 유지하는 방법
      - 날짜 + UUID 일부를 활용
        ~~~
        String orderNumber = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                + "-" + UUID.randomUUID().toString().substring(0,8).toUpperCase();
        ~~~
      - Base62 인코딩으로 압축


---
## 26. BigDecimal 
1. BigDecimal    
   BigDecimal은 실수 연산에서 정확한 소수 계산이 필요할 때 사용된다.
   따라서 곱셈, 나눗셈 시 주의할 점이 있다.
   특히 스케일과 라운딩 모드를 지정해야 예외가 발생하지 않는다.
   1) multiply    
      두 BigDecimal을 곱한다.
      스케일이 자동 적용되어 정수 곱셈에는 문제가 없다.
      단, 소수를 포함해 계산하는 경우, setScale로 소수 자리 수를 지정해야 한다.
      RoundingMode가 필수이므로, 설정하지 않으면 ArithmeticException발생이 가능하다.

2. BigDecimal과 객체 연산 스트림
   BigDecimal도 컬렉션(Stream)을 통해 여러 BigDecimal 값을 합치거나 곱하는 것도 가능하다.
   다만 주의할 점은 mapToInt(), mpaToDouble 같은 기본형 스트림의 결과는 부정확할 수 있다는 것이다.
   BigDecimal은 객체 연산 스트림을 쓰는 게 안전하다.
   1) reduce()    
      스트림의 모든 요소를  하나의 값으로 합치는 연산.
      합계, 곱, 최대값, 최소값 등 스트림 안의 여러 요소를 하나의 결과로 축소할 때 사용한다.   
      reduce(0, (a,b) -> a+b) 의 경우, 초기값은 0이고, 누적값 a와 현재 요소 b를 합친다.
      ~~~
      // 초기값은 BigDecimal.ZERO, BigDecimal::add 메서드 참조로 합산.
      prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
      ~~~


---
## 27. 도메인 모델 중심 설계 (DDD, Domain-Driven-Design)
### < 도메인 주도 설계 >
복잡한 비즈니스 문제를 해결하기 위해 
소프트웨어 구조를 비즈니스 도메인의 개념과 규칙에 맞춰 설계하는 방법.    
비즈니스 로직이 복잡해질수록 계층형 아키텍처(Controller, Service, DAO)만으로는 
규칙의 응집도가 낮아지고 변경 관리가 어려울 때 도입하는 방법.   

- 도메인
  시스템이 해결하려는 문제영역과 그 안의 규칙. (비즈니스)    
  Entity, Service 모두 도메인이다. 다만 역할이 다르다.  
  1) Entity
     - 도메인의 상태, 불변 규칙
  2) Domain Service
     - Entity 하나에 넣기 애매한 규칙.
     - 행위가 도메인 개념이긴 한데 주어가 애매할 때 위치.
     - Domain Service는 도메인이 맞다. 하지만 Application Service는 도메인이 아니다.
       
- 핵심 도메인    
  상태, 생명주기, 규칙, 비즈니스 의미가 있다.    
  주문의 경우가 여기에 해당한다.

1. 보편언어   
   개발자, 기획자, 도메인 전문가 모두가 공유하는 공통 언어를 생성해 단어를 그대로 코드에 반영한다.
2. 도메인 모델   
   도메인(비즈니스)의 상태, 규칙, 행동을 객체로 표현한 것.   
   비즈니스 로직을 Entity, Value객체, Service 같은 형태로 모델링 한 것.
   1) Entity 
      - 유일한 ID로 구분되는 객체.
      - 시간에 따라 상태 변화.
   2) Value 객체
      - ID가 없는 의미있는 개념의 캡슐화.
      - 불변.
   3) Service
      - Entity, Value객체가 맡기 어려운 도메인 관련 연산 수행.
3. 집합체(Aggregates)   
   비즈니스 규칙을 지키기 위한 도메인 객체들의 묶음.  
   - 집합체 - 도메인들    
     햄버거 세트 - 햄버거, 사이드메뉴, 음료 


### < DDD에서 가장 중요한 것 >
***DDD는 도메인 모델이 시스템의 중심***이어야 한다.
***DDD의 핵심은 규칙의 책임을 어디에 두느냐***이다.

1. DDD가 아닐 때 특징
   - 규칙은 Service에 위치
   - Entity는 데이터의 역할
   - Service는 조힙과 흐름을 담당
   - 변경의 기준은 유스케이스
2. DDD일 때 특징     
   도메인 모델 중심적 사고를 하고 도메인 규칙을 모델로 삼는다.
   유비쿼터스 언어를 코드에 반영한다. 
   그 중 가장 눈에 보이는 결과가 Entity가 데이터만 가지지 않는다는 것이다.   
   Entity가 불변 규칙을 가진다는 것은 DDD로 보기 시작하는 최소 조건이다.
   Entity가 상태 변경을 스스로 통제하고, 무분별한 setter가 없으며, 
   규칙이 if문으로 서비스에 흩어지지 않게 하기 때문이다.   
   하지만 이로는 Entity가 여전히 수동적이므로 완전한 DDD로 보기도 어렵다.
   - 규칙은 Entity, Domain에 위치
   - Entity는 상태와 규칙의 역할
   - Service 최소화
   - 변경의 기준은 도메인의 개념
   

### < DDD 애플리케이션 레이어 분리 필수 규칙 >
비즈니스 규칙은 기술 변화에 흔들리면 안된다.   
예를 들어, 주문 도메인인 Order Entity가 인프라인 PG에 의존하면 안된다.
PG의 변경이 주문 도메인의 수정을 불러오기 때문이다.
도메인은 무엇을 해야 하는지만 알고, 어떻게 하는지는 몰라야 한다.
1) 애플리케이션 레이어, 인프라스트럭처 레이어 모두 도메인 레이어에 의존한다.   
   반면, ***도메인 레이어는 다른 레이어에 의존해서는 안된다.***    
2) 도메인 레이어의 변경에 의해 애플리케이션 레이어의 변경은 허용된다.   
   반면, 애플리케이션 레이어의 변경에 의해 도메인 레이어의 변경이 발생해서는 안된다.


### < DDD의 도메인 레이어 >
- Entity:	상태 + 불변 규칙
- Value Object:	값 그 자체 + 불변
- Aggregate:	규칙의 경계
- Domain Service:	엔티티에 넣기 어려운 규칙
- Repository:	Aggregate 저장소의 추상화
  

### < DDD 설계 방식이 적합한 경우 >
- 도메인 규칙이 복잡한 경우
- 도메인 모델 변화가 잦은 경우
- 트랜잭션/유스케이스 중심인 경우
- 외부 시스템 연동이 많은 경우

1. 주문 도메인의 경우 DDD 권장 이유    
   DDD 설계는 변화에 대한 취약성 때문에 도입하는 것이다.
    - 주문 도메인의 경우, 규칙의 복잡성이 매우 높다.
    - 주문, 결제, 취소, 환불, 배송 등 상태 변화가 많다.
    - 트랜잭션에 강하다.
    - PG, 재고, 배송, 쿠본 등 외부와의 연동이 많다.

2. 주문 도메인에 DDD 설계를 도입하지 않았을 때 문제점    
   변경 비용이 기하급수적으로 증가한다.   
   정책을 서비스로부터 분리하면 괜찮다고 생각하는 것은 절반만 맞는 내용이다.
   서비스가 규칙을 호출만 하고 Entity는 아무 책임도 없다면 상태 정합성을 보장할 최종 책임자가 사라지게 된다.
   ***DDD에서는 상태를 가진 객체가 규칙을 책임진다.***
    1) Entity가 무의미한 DTO가 된다.   
       값을 변경함에 있어 아무런 제약이 없기 때문에 어디서든 수정 가능하다.
    2) 같은 규칙이 여러 서비스에 중복   
       주문 취소, 관리자 주문 취소, 자동 주문 취소 배치 등 전부 if문을 복붙해야 하는 일이 발생할 수 있다.
    3) 정책 변경 시 영향 범위 예측 불가    
       배송 중에는 취소 불가 규칙 변경 시 모든 서비스 코드를 뒤져야 하는 문제가 발생한다.
    4) 테스트가 서비스 단위로만 가능.   
       도메인 규칙 자체를 테스트하기 어려움.

3. 상품 Entity vs 주문 Entity    
   상품의 경우, 상대적으로 정적이며 규칙이 단순하고 상태 변화가 거의 없다.
   이는 빈약한 Entity로도 큰 문제가 되지 않는다.    
   반면, 주문의 경우는 상태 중심의 도메인이다. 
   규칙 대부분이 상태 전이이며, 잘못되면 돈, 클레임, 법적 문제로 이어지게 된다.
   따라서 Entity가 규칙을 몰라도 되는 구조가 아니다.    
   물론 주문의 경우에도 정책으로 다 처리할 수 있다.
   그러면 Entity는 데이터, Policy는 규칙, Service는 조립의 책임을 가지게 된다.
   하지만 이 구조는 규칙이 분산되고 상태 변경이 무제한으로 이뤄질 수 있으며 도메인 보호막이 없는 상태가 되는것이다.
   DDD는 Entity가 아무것도 모르면 안된다.

   
### < DDD 스타일의 Entity 설계 >
1. 빈약한 도메인 모델   
   Entity는 데이터 덩어리로, 모든 규칙은 Service에 있다.

2. Rich Domain Model   
   Entity가 규칙을 알고 있다.
   상태 변경 경로가 제한되고, 잘못된 사용 자체가 불가하도록 한다.
   ~~~
   public class Order {
    public void addItem(...) {
        validateState();
        this.items.add(...);
        recalcTotalPrice();
    }
   }
   ~~~


### < DDD 관점에서 권장되는 구조 >
1. 생성 팩토리 (static)   
   Entity를 만들 때는 필수 필드 초기화 책임이 있다.   
   외부에서 Entity 생성 시 불변성을 보장해야 한다.   
2. 인스턴스 메서드   
   상태 변경, 계산, 검증 같은 도메인 로직을 처리한다.   
   calculateTotalPrice(), addItem(), validatePolicy() 등.
3. 생성 과정에서 인스턴스 메서드 활용   
   static 팩토리에서 builder()로 인스턴스를 만들고, 
   필요한 계산이나 검증을 인스턴스 메서드로 수행한다.   
   인스턴스를 먼저 만든 뒤, this를 사용하는 private 메서드를 호출하면 
   DDD 관점에서 생성 책임(factory)과 계산 책임(business logic)을 명확히 분리할 수 있다.
4. 연관관계의 책임은 Entity 내부에 두는 것이 좋다.   
   예를 들면 Order가 OrderItem에게 order 셋팅을 책임지게 하는 패턴이다.
   연관관계 세팅 로직이 분산되지 않고 Order 내부에 집중되어 유지보수 측면에서 좋다.


### < 도메인 규칙과 정책(비즈니스 규칙) >
규칙의 불변과 가변은 도메인 서비스와 정책 객체로 분리하는 이유이다.   

1. 도메인 규칙   
   ***도메인 자체가 항상 지켜야 하는 불변 조건.*** (불변 규칙)    
   만약, 시스템이 바뀌어도 거의 바뀌지 않음.   
   Entity의 정합성을 깨면 안 되는 규칙.   
   1) 도메인 규칙 예시
      아래의 규칙들은 Entity 내부에 있어야 자연스럽다.   
      - 주문은 결제 완료 -> 배송중 -> 배송완료 순서만 가능.
      - 주문 완료 후에는 주문 수량 변경 불가.
      - 재고가 0이면 주문 생성 불가.
      
2. 비즈니스 규칙 (정책)    
   바뀔 수 있는 비즈니스 판단 기준이다. (가변 규칙)    
   상황/이벤트/조직에 따라 상이하다.    
   어떤 규칙을 언제 적용할 것인가?   
   1) 정책 예시   
      - VIP 고객은 배송비 무료.
      - 이벤트 기간에는 주문 취소 가능 시간 24 -> 48시간.
      - 첫 주문 고객은 쿠본 자동 발급.


### < Entity 도메인 규칙 >
Entity의 책임
- 정체성(identity) 유지
- 상태 전이 규칙을 스스로 통제
- 잘못된 상태로 변경되지 않도록 보호 

1. 엔티티로 옮길 수 있는 규칙의 조건
   1) 외부 의존 없이 자기 상태만으로 검증할 것    
      Entity(도메인 규칙)는 외부 의존 없이 자기 상태만으로 검증 가능한 규칙만 포함한다.
      즉, Entity 내부 규칙은, 자체 상태와 의미만 검사하는 것이다.
      예를 들어 수량이 1보다 커야한다, 재고가 충분한가, 상품이 판매중인가 등...    
      외부 의존 필요 규칙은 Entity에 넣지 않고 Service/Policy에서 처리해야한다.
      예를 들어 DB 조회 여부, 다른 Aggregate 상태 확인, 외부 API 호출 등...   
      Entity가 외부에 직접 의존하는 것은 테스트를 어렵게 하고 순수 도메인의 의미를 깨는 일이다.    

   2) 생성 시 불변 조건 강제로 인한 도메인 안전성     
      도메인 내부 메서드는 방어적 코딩보다, 불변 조건을 믿을 수있어야 한다는 쪽이 DDD 스럽다. 
      가격의 BigDecimal.ZERO 초기화만 믿고 가격 계산 메서드에서 null 방어를 생략하는 것은 우연히 안전한 상태일 뿐이다. 
      올바른 접근은 ***생성 시점에 unitPrice/quantity의 유효성을 강제하는 것***이다. 

   3) 엔티티 불변 조건의 조건
   
   4) 외부 요청(Request DTO)에 의존하지 않음


### < 주문 생성시에만 특정 메서드 호출 강제하는 방법 >
실무에서는 1,2번 같이 사용.
팩토리 메서드로만 주문을 생성하고, 상태 기반 검증으로 추가 방어한다.

1. 상태 기반 제어
   가장 보편적인 방법으로 추천.   
   객체의 상태 필드를 이용해 도메인 규칙을 Entity 내부에서 강제.   
   규칙이 Entity 내에 존재해 Service가 실수로 호출해도 Entity가 막아준다.
   ~~~
   public void addItem(OrderItem item) {
       if (this.orderStatus != OrderStatusType.CREATED) {
           throw new IllegalStateException("주문 생성 단계에서만 상품을 추가할 수 있습니다.");
       }
       items.add(item);
       item.setOrder(this);
   }
   ~~~

2. 생성 전용 팩토리 메서드   
   도메인스럽고 깔끔한 방법.    
   외부에서 new를 통해 객체를 생성하지 못하게 막고, 주문 생성 책임을 한 곳으로 몰아버리는 방식.
   ~~~
   public static Order create(Member buyer, List<OrderItem> items) {
       Order order = Order.builder()
           .buyer(buyer)
           .orderStatus(OrderStatusType.CREATED)
           .orderedAt(LocalDateTime.now())
           .build();

       items.forEach(order::addItem);
       order.calculateTotalPrice();

       return order;
   }
   ~~~

3. 컬렉션 Setter 차단
   외부에서 items를 통째로 교체 불가하도록 제거.


---
## 28. DDD Entity
- 참조: Order.java / OrderItem.java    
상태, 행위, 규칙, 불변식(invariant) 을 Entity 스스로 가지고 있는 모델.    
Aggregate Root로서 책임을 가진 Entity.      
하위 Entity의 생명주기 관리, 연관관계의 진입점, 상태 기반 규칙의 중심이면 Aggregate Root 역할을 수행한다.   
<br>
Order는 JPA Entity이면서 동시에 도메인 규칙을 캡슐화한 Aggregate Root이다.
Service는 흐름만 조율하고, 실제 규칙은 Entity가 스스로 지키도록 설계 가능하게 한다.

1. 생성 경로가 통제됨.    
   외부에서 마음대로 new/builder 불가.    
   Entity의 주문 생성 규칙을 거치지 않으면 객체 생성 불가.     
   생성자를 제거하고 팩토리 메서드롤만 생성 가능하게 함.     
2. 생태 변경 지점이 제한.    
   객체가 생성된 후 무분별한 변경을 차단해 불변성에 가까운 구조로 만듦.   
   setter를 제거하고, 필요한 경우는 별도의 메서드 제공.   
3. 비즈니스 의미 있는 로직(도메인 규칙)을 Entity가 가짐.    
   예를 들면 총 금액 계산, 연관관계 설정 같은 도메인 규칙을 가진다.   
4. 상태 전이 가능여부 판단 및 상태 기반 행위    
   상태를 가진 객체가 자기 상태 변경 가능 여부를 판단해 변경 해야한다.
   자기 자신의 상태만 보고 판단 가능한 도메인 규칙에 한정해 책임을 가진다.   
   1) Policy의 책임
      - 생성 이전 검증
      - 외부 리소스(DB, 다른 Aggregate) 기반 검증
      - “이 요청을 받아도 되는가?”
   2) Entity의 책임
      - 생성 이후 상태 검증
      - 상태 전이 가능 여부 판단
      - 자기 불변식 유지


---
## 29. 생성자 접근 제한 (JPA, Lombok)
JPA Entity에서는 기본 생성자가 반드시 필요하다.
생성자가 없다면 JPA가 Entity를 리플렉션으로 생성할 수 없다.
프록시 생성 실패하고 런타임 오류로 Entity 로딩이 불가하게 된다.
JPA는 protected 기본 생성자를 호출하고 프록시 생성에도 문제가 없다.

1. 기본생성자 protected로 제한    
   - 애플리케이션 코드에서 기본 생성자 호출 불가, 동일 패키지 내에서도 직접 생성 불가.
   - JPA(Hibernate)는 리플렉션으로 접근 가능해 프록시 생성 문제 없음.
   - JPA 스펙 상 private 기본 생성자는 허용하지 않기 때문에 공식적으로 안전한 선택은 protected이다.
   ~~~
    @Entity
    @Getter
    @Builder(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)  // 프레임워크(JPA)만을 위한 생성자
    public class Order {
    
        // fields...
    
        public static Order createOrder(...) {
            ...
        }
    }   
   ~~~

2. AllArgsConstructor private으로 제한    
   AllArgsConstructor 제거 시 Builder에 오류발생.
   Lombok Builder가 기대하던 생성자가 사려졌기 때문이다.
   @Builder는 내부적으로 생성자를 호출해 객체를 생성한다. 
   따라서 호출할 생성자가 없으면 컴파일 에러가 발생한다.
   팩토리 메서드를 제공해 그 외 생성을 제한 시 만약 빌더를 사용하지 않는다면, 
   빌더와 함께 제거 가능하다.   



---
## 30. DDD Entity
1. DDD Entity와 상수    
   Entitypd 정책, 설정 상수는 두면 안된다.
   하지만 불변식은 Entity가 가져야 한다.
   '최소 수량=1' 같은 불변식은 Entity에 상수로 존재해도 괜찮다.    
   private static final 필드는 JPA Entity 생성.영속화에 아무런 영향도 주지 않는다.
   JPA는 기본적으로 static 필드는 아예 무시하고 인스턴스 필드만 매핑 대상으로 보기 때문이다.
   따라서 Entity 생성과 상수 필드 선언은 무관하다.    
   

2. DDD Entity 메서드 전달인자
   Order가 자연스럽게 다룰 수 있는 대상은 OrderItem, 그 안에 연결된 Product, ProductOption 같은 것들이다.
   productid 목록 같은 primitive 값들은 도메인을 벗어난 표현이다.
   Entity가 primitive 집합으로 사고하기 시작하면 절차적 코드가 된다.    
   Order Entity의 검증은 내가 가진 OrderItem 상태를 기준으로 해야한다.
   따라서 items 자체에서 정보를 직접 꺼내 검증하는 게 맞다.    
   ID 같은 특정 정보를 추출해 메서드 파라미터로 전달하는 것은 정보 손실이다.    
   수량, 옵션, 아이템 단위의 의미는 전부 사라지고 식별자 목록만 남는다.
   이는 'Order 입장에서 무엇을 주문했는지' 라는 의미가 사라지게 된다.     
   알고리즘 최적화 관점에서 특정 값을 추출해 전달하는 것은 옳은 판단일 수 있다.
   하지만, Entity에서 이렇게 집계용 데이터 쿠조를 만들기 시작하면, 검증 로직이 상태 기반이 아닌 구조 기반이 된다.
   즉, 도메인 언어가 아니라 알고리즘 언어가 되는 것이다.

3. 상태 변경과 데이터 변경     
   상태 변경 + 데이터 변경을 한 메서드로 묶는 게 도메인적으로 더 안전.   


---
## 30. 중첩 반복문으로 인한 시간복잡도 O(R^2*O) 문제해결
- 메모리에서 조회 최적화와 Map

---
## @Transactional과 private
@Transactional 적용 시 private 선언이 안됨.

---
## 인터페이스 메서드의 접근 제한자
public 쓸 필요 없나?

---
## 결제 설계
### < 결제 설계 >
1. 주문 중심 모델
    - 결제 상태 서진
    - 주문은 결과를 책임진다.
2. 정책 명확
    - 결제 단독 취소는 없다.
      주문 취소는 곧 결제 취소로 이어진다.    
      해당 전제로 API 설계가 단순해지고 예외 케이스가 감소한다.
3. PG 결제 흐름    
   1) 결제 요청 (PG 결제 세션 생성)    
      사용자가 결제하기 클릭 시 PG 결제창이 호출되며 아직 승인되지 않고 PG 결제 요청만 된 상태이다.
      결제 프로세스를 시작했다는 정도까지만 책임진다.   
      PG API 호출 시 요청의 결과로 트랜잭션 ID, redirect URL만 응답해준다.
      PG 결제 세션 생성 후 사용자가 실제로 결제할지, 카드 인증이 성공할지, 중간에 결제 창을 닫을지 알 수 없기 때문이다.
      따라서 결제 금액 등을 반환하지 않는다.    
      반면 트랜잭션 ID는 결제 세션 식별자로 PG 내부의 결제 흐름을 추적하기 위한 ID이다.
      우리 서버와 PG, 웹훅의 연결 키로 응답으로 반환한다.    
      리다이렉션 URL은 PG 결제의 UX 구조 때문에 응답으로 반환해준다.
      PG가 제공하는 결제창으로 이동시켜야 하고, 결제가 끝나면 우리 서비스로 보내야 하므로 전달한다.
      하지만, URL 응답을 신뢰할 수는 없다. 사용자가 직접 URL 조작 가능, 성공 페이지로 강제 이동 가능, 중간에 끊어도 이동 가능하기 때문이다.
      따라서 리다이렉션 결과로 주문과 결제 상태를 변경하면 안되고 단순히 UI 처리용으로만 사용해야 한다.    
      결제 row를 먼저 저장하는 이유는, PG가 비동기 웹훅을 빠르게 쏘는 경우를 대비하기 위해서이다.   
   2) 프론트는 리다이렉트 / 응답 (사용자 결제 진행)    
      PG에서 성공 페이지로 리다이렉트한다.
      단 위조가 가능해 신뢰 불가하다.
      이게 PG 결제 요청 시 받는 결과이다.    
   3) 외부 서버 -> 우리 서버로 PG 웹 훅 (결제 승인 확정) 
      PG가 우리 서버로 직접 승인 결과를 통지해준다.
      유일하게 신뢰 가능한 승인 시점으로, 해당 단게에서 주문 결제 상태가 확정되어야 한다.
      - 결제 요청 시 IN_PROGRESS로 결제상태 변경
      - PG 응답 시 상태변경 X
      - PG 웹 훅 요청 시 APPROVED / FAILED로 결제상태 변경

4. 현재의 결제 요청 구조 개선 방법    
   1) createPayment() 전체가 하나의 트랜잭션으로 설정해, 
      requestPayment()는 실제로는 TX 밖에서 실행되도록 분리한다. (프록시/구조로 해결)
      ~~~
       @Transactional
       public void createPayment(...) {
           Payment payment = insertPayment(...);
           PgResult result = pgClient.requestPayment(payment);
           payment.assignPgTransactionId(result.getPgTransactionId());
       }
      ~~~ 
   2) 웹훅 처리 테이블 별도 생성    
      트랜잭션 아이디로 Payment를 조회해 없으면 PendingWebhook 테이블에 저장하고 나중에 매칭해 보정한다.
   3) 트랜잭션 ID와 우리 서버 결제 식별자 분리     
      우리가 만드는 결제 식별자를 먼저 만든다.
      그리고 그걸 PG에 넘겨서 연결고리로 쓴다. 


### < 외부 PG API로 결제 요청 >
1. 외부 API 요청 로직이 포함되어있는 서비스에서 @Transactional    
   - PG 요청이 외부 IO라서 트랜잭션에 묶기 싫은 경우
     1) 결제 시작 시 상태 먼저 저장    
        payment를 먼저 생성해 save + flush 한다.
     2) PG 요청을 한다.     
        트랜잭션 밖에서 PG 호출.
     3) PG 결과 반영을 별도 트랜잭션으로 명확히 분리한다.
   ~~~
    public void startPayment(...) {
        Payment payment = createPayment(...); // @Transactional 적용
    
        PgResult pgResult = pgClient.requestPayment(payment); // 외부 API 호출로 @Transactional 미적용
    
        payment.startPaymentRequest(pgResult); // @Transactional 적용
    }
   ~~~

2. READY와 IN_PROGRESS 상태 사이에 외부 PG API 호출 전 상태가 추가적으로 필요한 이유
   > 도메인 전제가 즉시호출 + 즉시 응답이라면 READY 하나로 충분히 논리적이다.
   > 다만, 이는 운영 리스크를 버린 선택이라는 것을 알아야 한다.

   READY 상태만으로 PG 호출 전 상태임이 충분히 표현되지 않는다.
   개념적인 도메인 정의상으로 READY는 결제 생성되어 아직 승인되지 않은 상태이다.
   따라서 PG 호출 전 상태로 해석될 여지가 있다.
   이는 ***READY 상태의 의미가 두가지***가 되는 것을 의미한다.
   PG를 아직 호출하지 않았다는 의미와, PG를 호출했지만 결과 반영 실패를 의미한다.
   이 두 상태를 DB 상으로 구분 불가하게 된다.
   ***이 둘을 구분하지 못하면 재시도 로직 작성 불가, 관리자 수동 처리 불가, 중복 결제의 위험이 있다.***    
   따라서 외부 시스템을 건드렸는지 여부는 다른 상태로 남기는 것이 좋다.     
   단, PG 로직이 DB 트랜잭션과 동일한 원자성으로 rollback 가능한 로직이라면 그때는 READY 하나의 상태로 충분하다.
   하지만 PG는 그게 불가능한 영역이다.
   그럼에도 불구하고 도메인 정책이 즉시 PG 호출 및 즉시 응답을 전제로 한다면
   중간 상태값은 필수가 아니다.
   위에서 이야기하는 것은 일반적인 결제 시스템의 안전 장치를 말하는 것이다.
   현재 내 구조에서는 Payment 생성하는 같은 유스케이스 안에서 반드시 PG 요청이 이뤄지고 응답을 받아야 다음 상태로 갈 수 있다.
   이런 흐름이라면, PG 요청했지만 응답을 못 받은 상태를 다시 다룰 필요가 없다.
   하지만 서버 크래시, GC 스톱, 배포 중 종료, 네트워크 타임아웃, PG 응답 지연 중 하나라도 고려하면 REQUESTING 상태가 추가적으로 필요하다.
   
   1) 문제의 코드     
      1. PG API 호출 성공 시 PG는 결제 시작.
      2. PG API 호출 후 서버가 다운되면, PG API 호출되었음에도 
         DB 상태는 IN_PROGRESS로 변경되지 못하고 여전히 READY.
      3. 반면 ***실제 상태는 PG 결제 진행 중으로 PG 진행 상와 DB의 상태가 어긋나게 된다.***
      ~~~
       public void startPayment() {
           Payment payment = createPayment(); // save (READY)
           PgResult pgResult = pgClient.requestPayment(payment); // 외부 API
           payment.startPaymentRequest(pgResult); // IN_PROGRESS
       }
      ~~~
     2) 안전한 구조로 수정    
        외부 API 호출 전, 후 상태를 DB에 남긴다.
        1. PG API 호출 전 READY 후, IN_PROGRESS 전인 다른 상태를 저장.
        2. PG API는 트랜잭션 밖에서 호출.
        3. PG API에 결제 요청 후 결제 진행중인 최종 상태 IN_PROGRESS 반영. 
    
3. 트랜잭션 경계의 문제 (사실 아직 잘 모르겠다.)    
   아래는 메서드별로 트랜잭션이 쪼개져 있고, 중간에 PG 호출이 끼어있다.
   만약, PG 호출은 성공했으나, 추후 DB 업데이트가 실패한다면 결제는 됐는데 DB는 반영이 안 된 상태가 발생 가능하다.    
   따라서 PG 호출을 트랜잭션 안에 두는 건 논쟁의 여지가 있지만, 최소한 결제 결과 반영은 하나의 트랜잭션으로 묶어야 한다.
   startPayment()에 @Transactional을 거는 것이다.
   실무에서는 ***비동기와 웹훅으로 더 분리한다.***
   ~~~
   public void startPayment(...) {
      Payment payment = insertPayment(); // @Transactional
      PgResult pgResult = requestPaymentAtPg(payment); // 외부 호출
      setPayment(payment, pgResult); // @Transactional
   }
   ~~~

4. PG 요청 실패 시 IN_PROGRESS로 가지 않고 재시도 가능하도록 처리    
   1) 수정을 최소화하는 방법
      복잡한 재시도 정책이 없는 경우 결제 Entity 생성과 PG 결제 요청을 분리하지 않고 하나의 APi에서 호출가능.
      ~~~
       public void startPayment(RequestPaymentDto requestPaymentDto,
                             Member member) {
           // 1. 정책검증 / 결제 Entity 반환
           Payment payment = createPayment(requestPaymentDto, member); // 결제상태 READY

           try {
               // 2-1. PG 결제대행사에 결제 요청
               PgResult pgResult = pgClient.requestPayment(payment);
               // 2-2. 결제 도메인에 PG 요청 결과 반영 (결제번호, 결제상태 셋팅)
               updatePaymentToInProgress(payment, pgResult);

           } catch (Exception e) {
               // READY 유지 (의도적으로 아무 것도 안 함)
               throw e;
           }
       }
      ~~~
   2) Payment 객체 생성, PG 요청 API 분리   
      > 결제 Entity(READY)를 먼저 생성하고,
        그 ID를 기준으로 PG 결제 생성 API를 호출한 뒤,
        컨트롤러 응답으로 “PG 결제창 리다이렉트 정보”를 내려준다.
   
      결제 버튼 클릭 시 POST /payment -> POST /payment/{id}/request -> PG 결제창 다이렉트 수행.    
      프론트에서 한 번의 클릭했지만 서버에서는 내부적으로 두 단계를 수행한다.   
      1. API 분리 이점
         PG 요청 실패 시 복구 가능.
         READY 상태가 남아있으므로, 다시 호출해 결제 재시도 가능 등.
         

### < Service, Payment, Order 책임분리 >
1. Service     
   흐름 담당.    
   Service가 금액 검증, PG 결과 해석, 상태 결정, Entity 수정 등 결제 승인 로직을 알지 못하게 해야한다.
   Service가 결제 로직을 아는 순간 Payment Entity는 데이터 덩어리가 되고,
   Service는 도메인 로직 집합소가 된다.   
2. Payment Entity    
   결제 상태, 금액 검증
3. Order Entity    
   주문 상태 전이 규칙 검증    

~~~
// 기존에 Service 내에서 호출하는 결제 관련 로직
// Service 로직에서 3개의 메서드 생성 및 호출 필요.
public void approve(PgResult pgResult) {
    validateAmount(pgResult);
    setPgResult(pgResult);
    this.paymentStatus = PaymentStatusType.APPROVED;
}

public void fail(PgResult pgResult) {
    setPgResult(pgResult);
    this.paymentStatus = PaymentStatusType.FAILED;
}

private void validateAmount(PgResult pgResult) {
    if (this.order.getTotalPrice().compareTo(pgResult.getPaidAmount()) != 0) {
        throw new PaymentException(PAYMENT_AMOUNT_INCONSISTENCY);
    }
}
~~~
~~~
// 위 메서드를 Payment Entity로 이전하면 아래와 같이 서비스가 단순해짐
// 결제는 어떻게 승인되는지를 결정하는 건 Payment의 책임이다.
if (pgResult.isSuccess()) {
    payment.approve(pgResult);
} else {
    payment.fail(pgResult);
} 
~~~


### < Order 상태변경 트리거 Payment >
Payment가 Order 상태 변경을 트리거하는 게 자연스럽다.
결제가 원인으로 도메인 이벤트를 발생시킨다는 것을 보여준다. 
결제로 인해 주문이 바뀐다는 표현을 한다.

1. Order 상태 변경    
   Payment에서 Order 상태 변경을 직접 호출하는 것은 강결합을 발생시킨다.   
   Aggregate 간 협력은 SErvice에서 조율하는 것이 DDD적으로 깔끔한 방법이다.    
   Payment 도메인에서 Order 상태 변경을 하지말고 
   상태변경 메서드는 Order에 두어 DDD 측면의 책임을 명확히하고 Service에서 메서드를 호출해 사용하도록 한다.
   ~~~
   order.paidBy(payment);
   ~~~


### < Order, Payment 연관관계 변경 >
기존 Order와 Payment를 1:1로 두고 Payment 히스토리를 관리할 생각이었다. 
그런데 결제 실패 후 재시도하는 경우, order에 대한 결제 row가 추가 생성된다는 것을 간과하고 있었다.
그래서 payment에 마지막 결제 생성 건만 두고 이전 생성 건은 delete 하고,
결제 생성, 변경 시마다 history를 쌓으려고 했다.

1. Order와 Payment 1:N    
   Order는 여러 Payment를 가질 수 있지만, 동시에 유효한 결제는 하나로 한다.
   
2. Order와 Payment를 1:1로 두지 않고 1:N으로 두는 이유    

3. 권장 방법
   Order가 Payment를 소유, Payment는 orderId만 들고있는 경우가 보편적이다.   


### < Entity와 Policy >
Entity에는 상태 검증을, Policy에는 규칙 검증을 둬야한다.    

1. Entity 상태 검증
   - 상태 전이 메서드의 네이밍은 행위 기준으로 통일 할 수 있도록 한다.    
     누가 호출했는지보다 해당 상태 검증 메서드에서 무슨 일이 일어났는지를 네이밍에 표현하는게 좋다.  
   ~~~
   // 잘못됨!! 누가 호출했는지를 메서드 네이밍에 표헌 
   approveWebHook()
   failWebHook()
   
   // 상태를 어떻게 변경했는지를 네이밍에 표현
   approve()
   fail()
   ~~~

2. 도메인 크기에 비해 과한 PaymentPolicy 분리    
   1) 문제점     
      Servie와 Policy 분리는 이론적으로 맞지만 현 재 도메인 크기에 비해 과한 면이 있다.
      단순한 권한, 소유자 검증만 존재하고, Entity에 대부분의 검증 로직이 존재하기 때문이다.
      이는 흐름을 오히려 따라가기 어렵게 한다.
   2) 해결방법   
      권한, 접근 제어는 Service에서, 도메인 규칙은 Entity에서 수행하도록 한다.
      판단 로직은 최대한 Entity로 이동하는 것이다.

### < 에러코드 관리 >
1. 에러코드 네이밍 방법    
   에러코드는 누가 잘못했는, 그로 인해 무엇이 안 되는지를 표현해야 한다.
2. 치명적인 에러 관리     
   주문 금액과 승인된 결제 금액 불일치 같은 치명적인 에러의 경우는 별도의 관리가 필요하다.
   이는 단순한 비즈니스 에러가 아닌, 시스템 무결성 에러이기 때문이다.     
   이런 경우는 로그를 출력하고 알람을 발생시켜 즉시 운영자 개입 가능하도록 해야한다.


### < 로그 작성법 >
로그를 문자열 더하기로 작성하지 않고, 파라미터 바인딩으로 작성하도록 한다.
성능이 더 좋아지고 로그 파싱이 쉬우며 표준이다.
~~~
// 파라미터 바인딩
log.info("PAYMENT_START orderId={}", orderId);
~~~

1. 어노테이션 별 로그 구현체(?)
   1) @Slf4j    
      log 필드는 컴파일 시 Lombok이 자동생성한다.
      - log.info / warn / error
   2) @Log4j2
      - log4j2
   3) @CommonsLog
      - commons-logging

2. 나쁜 로그
   ~~~
   log.warn("금액이 다릅니다");
   ~~~

3. 좋은 로그의 기준
   어떤 일이 발생했는지, 식별자는 무엇인지, 비교값은 무엇인지 등 정보를 제공해 원인 추적이 가능해야한다.
   1) 치명적 에러
      ~~~
      log.error(
          "PAYMENT_AMOUNT_MISMATCH orderId={}, paymentId={}, expectedAmount={}, paidAmount={}, pgTransactionId={}",
          order.getId(),
          payment.getId(),
          order.getTotalPrice(),
          pgResult.getPaidAmount(),
          pgResult.getPgTransactionId()
      );
      ~~~
   2) 정책 위반
      ~~~
      log.warn(
         "PAYMENT_NOT_REQUESTABLE orderId={}, status={}",
         order.getId(),
         order.getOrderStatus()
      );
      ~~~
   3) 정상 흐름 추적
      ~~~
      log.info(
         "PAYMENT_APPROVED orderId={}, paymentId={}, amount={}",
         order.getId(),
         payment.getId(),
         payment.getApprovedAmount()
      );
      ~~~

### < 웹훅 >
웹훅(Webhook)은 “다른 서버가 우리 서버에게 특정 이벤트 발생을 알려주기 위해 보내는 HTTP 요청.
우리가 요청한 결과를 비동기로 받는 통로.


### < PG 웹 훅 응답 안정성 체크 >
1. 웹훅 중복 수신 처리 
2. 이미 승인된 결제에 재 승인 요청 오는 경우
3. 승인 후 Order가 Paid인 경우
4. 웹훅 트랜잭션 분리    
   되돌릴 수 없는 외부 세계의 상태와 내부 실패를 함께 운영하면 안된다.     
   Order는 처리 실패 하더라도 Payment 승인 데이터 응답 받았으니 안전하게 수정한다.   
   외부에서 정상 응답을 받았는데 Order 처리를 실패했다고 함께 rollback하지 않는다.
   Order는 처리 실패 하더라도 서버 내부에서 수정을 재시도 하거나 보정 가능하기 때문에 분리한다.
5. pg 서명 검증    
   해당 웹 훅이 진짜 PG가 보낸 게 맞는지 검증하는 것.
   서명 검증이 없다면 웹 훅 URL만 알면 누구나 결제 승인 위조가 가능하기 때문이다.


### < 웹훅과 트랜잭션 락 남용 >
조회 시 트랜잭션 락을 걸게 되면 결제 시작, 웹 훅, 재시도 시 트래픽이 증가하게 되면 DB 락 경합이 심각해진다.
PG 웹훅은 락이 아니라 멱등성으로 막아야 한다. 

1. 멱등성 (Idempotency)    
   같은 요청을 1번 보내든 10번 보내든 결과가 동일한 것.
   1) pgTransactionId는 유니크
   2) 상태 체크
   3) 중복 웹훅 무시

2. 웹훅의 특징
   - 중복이 오는 것
   - 순서 보장이 안됨
   - 승인 결과가 재전송 됨


### < 웹 훅 응답 >
PG는 “HTTP 200 OK를 받을 때까지” 계속 재전송한다.   
400 / 500 / 예외 → “서버가 못 받았나 보다” → 재시도 폭탄된다.
따라서 이미 처리된 결제라면 결제 상태와 무관하게 200 OK를 반환해야 한다.

1. 결제 상태에 따른 웹 훅 동작
   - READY / IN_PROGRESS: 정상 처리
   - APPROVED / FAILED / CANCELED: 아무 동작 안함 + 200 OK 응답

2. 잘못된 기존 코드
   ~~~
        // 1. 중복 웹훅 무시 (멱등성 검증: 동일 요청을 여러번 보내도 결과는 동일해야 함)
        PaymentStatusType paymentStatus = payment.getPaymentStatus();
        if (!(paymentStatus == READY || paymentStatus == IN_PROGRESS)) {
            throw new PaymentException(PaymentErrorCode.PAYMENT_ALREADY_COMPLETED);
        }
   ~~~

3. 수정된 코드
   ~~~
   // payment.isTerminal() == !(paymentStatus == READY || paymentStatus == IN_PROGRESS)
   if (payment.isTerminal()) { 
       log.info("Duplicate webhook ignored. paymentId={}", payment.getId());
       return; // 200 OK
   }
   ~~~


### < 웹훅 응답에 대한 결제정보수정 및 주문상태수정 트랜잭션의 분리 >
PG가 이미 확정한 사실(Payment 승인)은 절대 롤백되지 않게 하자.    
웹훅 핸들러에 @Transactional 적용 시 
PG 승인 성공해 payment 수정이 완료되었는데 order.paid()에서 예외가 발생하게 되면 트랜잭션 롤백이 발생하게 된다.
이를 트랜잭션을 분리해 막는다.    
웹 훅 입장에서는 이미 처리된 이벤트, 중복 이벤트, 모르는 이벤트는 성공으로 처리해야한다.   
실패로 응답해야 하는 건 서명 검증 실패, 포맷 자체가 깨진 요청이다.

1. 기존 문제 코드
   ~~~
    /** 결제 생성 웹훅 처리 - 결제 완료 **/
    @Transactional
    public void handlePgWebHook(PgApprovalResult pgApprovalResult) {
        // transactionId 조회
        Payment payment = paymentRepository.findByPgTransactionId(
                pgApprovalResult.getPgTransactionId())
                .orElseThrow(() ->
                        new PaymentException(PG_TRANSACTION_ID_NOT_EXISTS));

        // 중복 웹훅 무시 (멱등성 검증: 동일 요청을 여러번 보내도 결과는 동일해야 함)
        // payment.isTerminal() == !(paymentStatus == READY || paymentStatus == IN_PROGRESS)
        if (payment.isTerminal()) { 
            log.info("Duplicate webhook ignored. paymentId={}", payment.getId());
            return; // 200 OK
        }

        // 1. 결제 승인, 실패 반영 (결제에 웹 훅 결과 반영) -> 절대 롤백 되지 않게 수정 필요
        if (pgApprovalResult.getApprovalStatus() == APPROVED) {
            payment.approve(pgApprovalResult);
        } else {
            payment.fail();
        }

        // 2. 주문상태 변경 (주문에 웹 훅 결과 반영) -> 실패 가능하도록 수정
        Long orderId = payment.getOrder().getId();
        Order order = orderRepository.findByIdAndOrderStatus(orderId, CREATED)
                .orElseThrow(() -> new PaymentException(ORDER_STATUS_NOT_CREATED));
        order.paid(payment);
    }
   ~~~

2. Order 보정 방법
   1) 실시간 재시도   
      Order 변경 실패 시 이벤트를 발행해 재시도 큐.
   2) 배치 보정   
      가장 흔한 방법으로 결제는 승인 되었는데 주문은 CREATED인 건은 매일 새벽 정리.
   3) 운영 알람 및 수동 처리    


---
## @Transactional 속성
1. propagation    
   이미 트랜잭션이 있을 때 새 메서드를 어떻게 실행할지 결정.    
   1) REQUIRED    
      위 속성을 설정하지 않으면 기본값인 REQUIRED 적용되어 이미 트랜잭션이 있으면 같이 타고, 없으면 새로 만든다.
   2) REQUIRES_NEW     
      기존 트랜잭션은 잠시 멈추고, 무조건 새 트랜잭션을 생성한다.
      ~~~
      @Transactional(propagation = Propagation.REQUIRES_NEW)
      ~~~
      
2. 같은 클래스 안에서 호출
   아래의 경우는 프록시를 거치지 않고 자기 자신을 호출한다.
   그래서 REQUIRES_NEW, rollbackFor 전부 무시된다.
   ~~~
   @Service
   public class PaymentService {
    
        @Transactional
        public void handleWebhook() {
            approvePayment(); // 내부 호출
        }
    
        @Transactional(propagation = REQUIRES_NEW)
        public void approvePayment() {
            ...
        }
   }
   ~~~

3. 다른 Bean 호출 시    
   다른 Bean을 호출하면 그 Bean의 프록시를 거치기 때문에 @Transactional이 적용된다.   
   ~~~
    @Service
    class AService {
    
        private final BService bService;
    
        @Transactional
        public void a() {
            bService.b(); // 프록시 경유
        }
    }
    
    @Service
    class BService {
    
        @Transactional(propagation = REQUIRES_NEW)
        public void b() { }
    }
   ~~~


### < 도메인 책임 분리 - 상태 판단 >
isDuplicatedPgWebHookRequest()는 Payment Entity의 책임이 아니다.
하지만 Payment가 “종결 상태인지”를 판단하는 로직 자체는 Payment Entity로 이동하는 게 맞다.
즉, ***판단 기준은 엔티티, 웹훅 중복 처리라는 행위는 서비스 책임***이다.
1. 문제 코드   
   Payment 엔티티: “내 상태가 종결인지”만 알게 한다.    
   PaymentStatus의 종결 개념이 서비스에 흩어짐.
   다른 곳에서도 같은 조건이 필요해질 가능성 큼.
   ~~~
   private boolean isDuplicatedPgWebHookRequest(PaymentStatusType paymentStatus) {
      return paymentStatus == APPROVED
          || paymentStatus == CANCELED
          || paymentStatus == FAILED;
   }
   ~~~
2. Service: “웹훅을 무시할지”를 결정한다
   나중에 EXPIRED, TIMEOUT 같은 상태 추가돼도 Payment만 수정하면 되므로 정책 변경에 강해진다.
   ~~~
    // Payment Entity 코드
    public boolean isTerminal() {
       return this.paymentStatus == APPROVED
            || this.paymentStatus == CANCELED
            || this.paymentStatus == FAILED;
    } 
   ~~~
   ~~~
   // 서비스 코드
   if (payment.isTerminal()) {
      return;
   }
   ~~~


### < 도메인 책임 분리 - 서비스에서 Entity, Policy로 정책 책임 분리 >
1. Entity가 아닌 Policy에 정책을 두는 이유    
   아래와 같이 주문에 대한 결제 목록의 '승인 요청 가능한 상태 판단'은 Entity가 아닌 Policy에 둔다.
   이유는 아래와 같다.
   - 단건(Entity) 상태 판단이 아님 
   - 컬렉션 + 외부 컨텍스트(PG Provider) + 요청값을 함께 판단
   - “행위”가 아니라 “선별 규칙(정책)”에 가깝다.     
   Entity는 나 자신만 알아야하는데, 여러 결제 중 어떤 것이 적합한지 판단하는 것은 Entity의 책임이 아니다.
   반면, Policy는 여러 도메인 객체를 놓고 현재 비즈니스 규칙상 가능한 선택지를 결정하므로, Policy에 두면 의미가 또렷해진다.   
   단건 판단은 Entity, 조합/선별은 Policy에 둔다.

2. 문제 코드    
   기존 코드는 Service가 너무 많은 판단 책임을 가지고 있었다.
   단순 조회, 결제 상태 해석, 요청 행위 기준 판단 같은 비즈니스 규칙 판단이 섞여 있음에도 Service 내부에 직접 작성되어 있었다.
   ~~~
    // Service 코드
    // 승인 요청 가능한 결제 반환
    private Payment filterApproveRequestAvailablePayment(List<Payment> paymentList,
                                                  PaymentMethodType requestPaymentMethod) {
        for (Payment payment : paymentList) {
            PaymentStatusType paymentStatus = payment.getPaymentStatus();
            PaymentMethodType paymentMethod = payment.getPaymentMethod();
            PgProviderType pgProvider = payment.getPgProvider();

            // PG 승인 요청 가능한 결제 상태인가? (이거 정책에서 검증하는데 제거 여부 판단하기)
            boolean isStatusOfApproveAvailable =
                    !(paymentStatus == APPROVED || paymentStatus == CANCELED);
            // 요청 결제방식과 동일한가?
            boolean isRequestMethod = paymentMethod == requestPaymentMethod;
            // 회사가 계약한 결제대행사와 동일한가?
            boolean isPgClient = pgProvider == pgClient.getProvider();

            if(isStatusOfApproveAvailable && isRequestMethod && isPgClient) {
                return payment;
            }
        }

        return null;
    }
   ~~~
   
3. 문제 해결     
   서비스에서 정책을 분리했다.
   그리고 Payment의 상태 판단은 Entity로 분리했다.    
   요청의 차단과 상태에 따른 Entity 선택 역할이 함께하지 않도록 한다.     
   
   isTerminal()은 해당 결제가 더 이상 상태 전이를 하지 않는 종결 상태를 의미한다. 
   따라서 승인(APPROVED), 취소(CANCELED), 실패(FAILED)처럼 이후 어떤 처리도 허용되지 않는 상태들을 묶어 표현할 때 적합하다. 이 기준에 따라 isTerminal()은 “이 결제가 라이프사이클상 이미 끝났는가”를 판단하는 책임을 엔티티가 갖는 것이 맞다.
   반면 PG 승인 요청 가능 여부는 단순 상태 판단이 아니라 요청 행위 기준의 규칙이므로, 
   엔티티에는 isApproveRequestAvailable()처럼 자기 상태만을 기준으로 한 판단 메서드만 두고, 
   여러 결제 간 비교나 외부 조건(PG, 요청 방식 등)이 포함되는 로직은 정책으로 분리하는 것이 좋다. 
   즉, 엔티티는 “이 상태가 허용되는가”까지만 알고, “어떤 결제를 선택할 것인가”는 정책이 결정하도록 역할을 명확히 나누는 방향이 적절하다.

   ~~~
    // Service 코드
    // 승인 요청 가능한 결제 반환
    private Payment filterApproveRequestAvailablePayment(
            List<Payment> paymentList, PaymentMethodType requestPaymentMethod) {

        for (Payment payment : paymentList) {
            boolean isApproveRequestAvailable =
                    paymentPolicy.isPaymentAvailablePgRequestAboutRequest(
                            payment, requestPaymentMethod, pgClient.getProvider());

            if(isApproveRequestAvailable) {
                return payment;
            }
        }

        return null;
    }
   ~~~
   ~~~
    // Policy 코드
    /** 결제생성 정책 **/
    public void validateCreate(List<Payment> paymentList,
                               Order order, Member member) {
        // 3. DB 조회 필요 도메인 규칙 검증
        // 본인 외 결제 불가
        validatePaymentOnlyOwnAccess(order, member);
        // 결제상태 종결여부 검증
        validateNoTerminalPayment(paymentList);
    }
   
    // 결제 승인된 경우 결제 생성 불가 (승인, 취소 등 PG 승인된 경우)
    private void validateNoTerminalPayment(List<Payment> paymentList) {
        long invalidPaymentCount = paymentList.stream()
                .filter(payment -> !payment.isTerminal())
                .count();

        if (invalidPaymentCount > 0) {
            throw new PaymentException(PAYMENT_ALREADY_COMPLETED);
        }
    }

    /** 요청에 대한 승인 가능한 유효한 결제 판단 **/
    public boolean isPaymentAvailablePgRequestAboutRequest(Payment payment,
                                                           PaymentMethodType requestPaymentMethod,
                                                           PgProviderType requestPgProvider) {

        PaymentMethodType paymentMethod = payment.getPaymentMethod();
        PgProviderType pgProvider = payment.getPgProvider();

        // PG 승인 요청 가능한 결제 상태인가?
        boolean isStatusOfApproveAvailable = payment.isApproveRequestAvailable();
        // 요청 결제방식과 동일한가?
        boolean isRequestMethod = paymentMethod == requestPaymentMethod;
        // 회사가 계약한 결제대행사와 동일한가?
        boolean isPgClient = pgProvider == requestPgProvider;

        return isStatusOfApproveAvailable && isRequestMethod && isPgClient;
    }
   ~~~
   ~~~
    // Payment Entity 코드
    /** PaymentStatus - 자기 상태 판단
     *  PG 승인 요청 가능 여부 반환 **/
    public boolean isApproveRequestAvailable() {
        return !(this.paymentStatus == APPROVED || this.paymentStatus == CANCELED);
    }
   ~~~
   

### < 도메인 책임 분리 - 외부 응답 검증과 도메인 검증 책임 분리 원칙 >
외부 PG 응답 데이터가 정상적인 값인지 판단하는 검증은 
외부 시스템 신뢰성 문제이므로 서비스 계층의 책임이다.     
반면, 해당 응답 데이터가 현재 결제 객체의 상태와 일치하는지, 
즉 트랜잭션 ID가 동일한지, 결제 상태가 변경 가능한 상태인지, 결제 금액이 주문 금액과 일치하는지와 같은 검증은 
도메인 무결성 규칙에 해당하므로 엔티티 내부에서 수행해야 한다.     
따라서 외부 응답 자체의 유효성 검증은 서비스가 담당하고, 
응답 데이터를 적용할 수 있는지에 대한 상태 검증은 엔티티가 담당하는 구조가 올바른 책임 분리다.

