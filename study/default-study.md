# MY E-COMMERCE PROJECT STUDY
2025 간단한 이커머스 서비스를 만들면서 공부한 내용


---
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


---
### < 스프링부트 의존성추가 >
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

   - Lombok         
        
         // Lombok 애너테이션을 처리하기 위해 컴파일 시 애너테이션 프로세서를 추가   
         compileOnly 'org.projectlombok:lombok'   
         annotationProcessor 'org.projectlombok:lombok'   
     - Lombok은 @Getter, @Setter 등의 어노테이션에 대해 **컴파일 시에 자동으로 메서드 생성.**

3. org.springframework.boot:spring-boot-starter-web 의존성추가
   - 참고블로그 https://priming.tistory.com/144


---
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
  

---
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
   - @Validated, @Valid 어노테이션 
     - 사용 시 대부분 스프링이 대신 검증해주므로 Validator 직접 생성할 필요 없음.
     - @Validated
       - Spring에 해당 어노테이션이 달린 클래스의 메서드로 전달되는 매개변수를 검증하도록 지시하는 데 사용할 수 있는 **클래스 수준의 어노테이션.**
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
     - https://reflectoring.io/bean-validation-with-spring-boot/


---
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


---
### < slack과 github 연동 >
- 참고블로그 : https://sepiros.tistory.com/37

---
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
### < Mysql 데이터타입 결정 >
- 참고블로그 https://dev-coco.tistory.com/54

---
### < DTO, VO, MODEL 차이 >
https://010562.tistory.com/11
https://velog.io/@chae_ag/Model-DTO-VO-DAO-%EA%B0%80-%EB%AC%B4%EC%97%87%EC%9D%B8%EA%B0%80%EC%9A%94

---
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
- 

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

- main함수가 아니지만 실행가능한 이유 : Junit 프레임워크가 대신 실행해주기 때문.

1. @SpringBootTest나 @WebMvcTest 어노테이션 미사용 시 (단위테스트)
   - Spring 애플리케이션의 빈이나 컨텍스트를 로드하지 않고, 단위 테스트 시 @WebMvcTest나 @SpringBootTest 불필요.
   - JUnit 5는 기본적으로 SpringRunner(또는 @ExtendWith(SpringExtension.class))와 함께 사용할 때 Spring이 제공하는 기능들을 활성화. 
   - @SpringBootTest나 @WebMvcTest를 사용하지 않으면, **단위테스트**로만 실행가능.
   - 단위테스트 실행
     - @Test 어노테이션을 사용한 메서드는 Spring 컨텍스트를 로드하지 않고도 실행.
     - 빈 주입이 필요 없다면 @Autowired나 @MockBean 같은 어노테이션을 사용하지 않고, 
       @Mock을 사용해서 수동으로 객체를 만들어 테스트 가능.
   - @WebMvcTest, @SpringBootTest를 사용하지 않으면 기본적으로 컨트롤러, 서비스, 레포지토리 등이 로드되지 않음. 
     그 대신 @Mock이나 @InjectMocks를 사용하여 필요한 객체들을 직접 주입하여 테스트. 따라서 빈 로딩 없음.
   - 특정 객체만 테스트하고자 할 때, @Mock으로 의존성 객체들을 수동으로 모킹하여 컨텍스트를 로드하지 않고도 테스트 가능.

2. @SpringBootTest (애플리케이션 통합테스트)
    - 애플리케이션 전체가 제대로 통합되어 동작하는지 확인하고 싶은 경우 사용.
    - 테스트 실행 시 전체 애플리케이션이 로드되기 때문에 속도가 느릴 수 있음.
    - @SpringBootTest를 사용 시 Spring 컨텍스트가 로드되고 애플리케이션의 전체 빈을 활용하여 **통합테스트** 가능.
    - 애플리케이션의 전체 빈을 로드하고, 실제 서비스, 리포지토리, 데이터베이스, 웹 계층 등이 잘 연결되고 동작하는지 확인.
    - 통합 테스트를 통해 시스템의 다양한 레이어가 서로 잘 동작하는지 검증.
    - springboot context loader를 실제 어플리케이션처럼 테스트용으로 만들어줌.
    - context(설정 등)를 실제 환경과 동일하게 모든 기능을 생성해 빈들을 등록한 것을 이용해 테스트 가능하므로 의존성 주입을 이용해 테스트가능.
      ~~~
      private AccountService accountService = new AccountService(new AccountRepository());
      ~~~
      위와같이 작성하지 않고 아래와 같이 작성.
      ~~~
      @Autowired
      private AccountService accountService;
      ~~~
    - org.springframework.boot:spring-boot-starter-test 에 포함된 기능.

3. @WebMvcTest (웹 계층 통합테스트)
   - 웹 계층(컨트롤러)만 테스트할 때 사용.   
     주로 컨트롤러, 필터, 인터셉터 등과 관련된 동작을 확인할 때 사용.
   - HTTP 요청과 컨트롤러 간의 상호작용을 테스트.
   - 컨트롤러와 관련된 빈만 로드하며, 서비스나 리포지토리 같은 비즈니스 계층은 mocking을 통해 가짜 객체로 대체.
   - 기존 @WebMvcTest 어노테이션을 사용하면 MockMvc를 자동 생성해 주입해주었지만
     스프링부트 3.4 버전 이후로는 직접 MockMvc를 생성해주어야 하므로 해당 어노테이션은 사실상 아무런 역할도 하지 않음.


### < 테스트코드작성 mocking >
- Mock을 사용해 테스트 하는 이유
  - 동일 테스트코드를 함께 실행해도 하나는 성공하고 하나는 실패하는 문제 해결가능.   
  - DB에 데이터가 바뀌든 의존하고 있는 repository의 로직이 변경되든 관계없이 내가 맡은 역할만 테스트가능.

- given-when-then(BDD 스타일) 패턴
    - 테스트는 given-when-then(BDD 스타일) 패턴으로 작성한다.
    1. 예상 동작 정의
        - willReturn(),willThrow() 등 메서드를 통해 mock 객체(userRepositoy)의 예상 동작을 미리 정의해준다.
    2. exception을 검사
        - assertThrows와 함께 사용하면 된다.
    3. 메서드 호출 여부 검증
        - should() 메서드로 해당 메서드가 제대로 호출되었는지 검증한다.

- Mockito 와 BDDMockito 라이브러리
  1. Mockito
     - JAVA 오픈소스 프레임워크로 단위테스트를 위해 모의 객체(Mock Object)를 생성하고 관리하는데 사용.
     - 모의 객체를 생성하여 모의 객체의 행위를 정의함으로써 의존 객체로부터 독립적으로 테스트를 수행가능.
  2. BDDMockito (BDD, Behavior Driven Development)
     - BDD 스타일을 따름.
     - 테스트 코드의 가독성을 높이기 위해 given-when-then 패턴을 따름.
  - 참고블로그 https://soso-shs.tistory.com/109

- 참고블로그 https://soonmin.tistory.com/85

1. Mock
   - mock 객체를 생성.
   - mock으로 생성된 모의 객체는 stub을 통해 예상동작 정의가능.
   
   1. 명시적으로 mock 객체 생성
      - 테스트 메서드 내에서 직접 mock 객체를 생성하고 사용.
      - Mockito.mock() 방식은 더 세밀하게 제어가능.
      ~~~
       @Test
       void createMock() {
           // Mockito.mock(UserService.class) : UserService 타입의 mock 객체 생성
           // 생성된 mock 객체는 createMock() 테스트 메서드 내에서만 사용됨.
       
           UserService mockUserService = Mockito.mock(UserService.class);
       }
      ~~~
      
   2. @Mock 어노테이션을 이용해 객체를 자동으로 mock 처리
      - 클래스 레벨에서 필드로 선언한 후 **해당 객체를 직접 초기화하지 않고** Mockito가 자동으로 mock 객체를 주입.
      - @Mock은 코드가 깔끔.
      - 여러 테스트 메서드에서 동일한 mock 객체를 공유할 수 있어 편리.
      - @Mock 어노테이션 사용 시 MockitoAnnotations.initMocks(this)를 사용해 어노테이션을 초기화 필수.
      - 최신 버전의 Mockito에서는 **@ExtendWith(MockitoExtension.class)**를 사용해 초기화 가능.
      ~~~
       @Mock
       UserService mockUserService;
      ~~~

2. Stub
   - 생성된 mock 객체의 예상 동작 정의
   - 아래의 두 구문은 동일한 기능 수행.
   1. **given().willReturn()**
      - given()은 Mockito 2.x 버전부터 BDDMockito 스타일로 도입된 구문.
      - given-when-then 스타일의 테스트 코드 작성 시 선호.
      - stub(가설) : 생성된 mock 객체의 예상 동작을 정의 (mock 객체 리턴내용 지정)
      ~~~
      given(memberRepository.findByTel1AndTel2AndTel3(any(), any(), any()))
           .willReturn(Optional.empty());
      ~~~
   2. when().thenReturn()
      - when()은 Mockito 1.x 버전부터 존재하는 기본적인 구문.
      - 일반적으로 사용.
      ~~~
      User user = new User("mockUser", 30, false);
      when(mockUserService.getUser()).thenReturn(user);
      ~~~

3. Spy
   - Spy로 만든 mock 객체는 원본 동작을 유지 또는 새로운 정의 가능.
   - 실제 객체를 감싸서 해당 객체의 메서드를 모니터링하거나, 특정 메서드만 스텁(stub)하거나 모킹(mocking) 가능하게 함.
   - Stub 하지 않으면 원본 동작대로 실행.
     ~~~
     @Test
     void mockTest2() {
         UserService mockUserService = Mockito.spy(UserService.class); // spy
         User realUser = mockUserService.getUser();

         assertThat(realUser.getName()).isEqualTo("realUser");
     }   
     ~~~
   - Stub 을 해주면 정의한 동작대로 실행.
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
   1. 명시적으로 Spy로 mock 객체 생성
   2. @Spy
   
4. Verification
   - mock 객체의 메서드 호출을 확인하는 것을 의미
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

- @ExtendWith(MockitoExtension.class)
   - 애너테이션이 적용된 클래스는 **Mockito의 기능을 자동으로 활성화.**
   - 필드에 있는 @Mock, @InjectMocks 등은 Mockito가 자동으로 초기화하고, 관련 객체 주입.
   - JUnit 5에서 MockitoExtension을 사용 시, 테스트 클래스에 @ExtendWith(MockitoExtension.class) 애너테이션을 추가 필요.
   - MockitoExtension.class
     - JUnit 5에서 Mockito를 사용 시 필요한 확장(Extension).
     - MockitoExtension.class 사용 시 @Mock, @InjectMocks, @Captor 등의 Mockito 애너테이션을 자동으로 초기화해주고, 테스트 클래스에서 mock 객체를 관리해주는 역할.
   - 테스트코드 mock 생성 예시 (MemberServiceTest.java)
     MemberService는 MemberRepository에 의존하고있다.
     MemberRepository에 가짜로 생성해 MemberService에 의존성을 추가해준다.   
     DB에서 독립적인 테스트가 가능해진다.
     ~~~      
     @ExtendWith(MockitoExtension.class)  // JUnit 5에서 MockitoExtension 활성화
     public class MemberServiceTest {
         // @Mock: MemberRepository를 가짜로 생성해 memberRepository에 담아준다.
         @Mock
         private MemberRepository memberRepository; // mock 객체
     
         // @InjectMocks : Mock으로 생성해준 memberRepository를 memberService에 inject(의존성주입).      
         @InjectMocks
         private MemberService memberService; // mock 객체를 주입 받을 대상
         
         ...
     }
     ~~~
   

### < repository의 메서드를 stub하지 않아도 테스트코드 오류가 발생하지 않는 이유 >
1. 궁금증 발생원인
   - ProductionService.saveProduction() 메서드에서 productionRepository.findBySellerAndCode() 메서드를 서비스 로직에 추가했는데 
     테스트코드에서 해당 메서드 호출 시 오류가 발생하지 않았다. (기존 다른 테스트코드 작성시에는 repository의 메서드 호출 시 stub 하지 않으면 오류가 발생했다.)

2. 테스트코드 정상 수행된 이유
   - productionRepository.findBySellerAndCode() 메서드가 실제로 호출되지 않아서입니다. 
   - Mockito에서 @Mock 어노테이션을 사용해 ProductionRepository를 모킹했기 때문에, 해당 메서드는 실제로 실행되지 않고, 대신 빈 값을 반환하거나 아무 일도 일어나지 않게 됩니다.
   - productionRepository.findBySellerAndCode() 메서드는 Optional<Production>을 반환하고, 해당 값이 존재할 경우 예외를 던지는 코드입니다.
   - Mockito는 기본적으로 모킹된 객체가 호출되면 빈 값 (예: null 또는 Optional.empty())을 반환하기 때문에 ifPresent 블록이 실행되지 않습니다.
   - 즉, productionRepository.findBySellerAndCode(sellerId, productionCode) 메서드를 모킹하지 않더라도, Mockito는 이 메서드가 호출되는 대신 아무 작업도 하지 않으므로, 예외가 발생하지 않는 것입니다.
   > 결론     
   > 모킹되지 않으면 기본값인 빈 값(null 또는 Optional.empty())이 반환됨.    
   > 만약 메서드가 빈값을 반환해도 되는 경우, 테스트의 명확성과 일관성을 보장하기 위해 명시적으로 모킹해주는 것이 더 좋을 방법일 수 있음.    
   > 테스트의 정확성을 위해서 Optional.empty()와 Optional.of(existingProduction) 두 가지 경우를 모두 테스트해 보는 것이 좋음.    

3. 올바른 테스트코드 작성방법
   - productionRepository.findBySellerAndCode() 메서드를 명시적으로 모킹해야 합니다.
   - 예를 들어, 해당 메서드가 Optional.empty()를 반환하도록 설정하거나, 이미 존재하는 상품을 반환하도록 설정할 수 있습니다.

 
### < 테스트코드 검증 >
- 전달인자, 반환값에 대한 검증
  - 검증할 때 변경되지 않는 값들에 대해서는 반드시 검증할 필요없음.
  - 특히, 테스트의 목표가 변경된 값들이 올바르게 처리되었는지를 확인하는 것이라면, 변경되지 않은 값들은 검증 대상에서 제외가능.

- 변경된 값들 필수 검증
  - 효율성
    - 모든 값을 검증하면 너무 많은 검증이 이루어지게 되며, 테스트 코드가 불필요하게 복잡해짐. 
    - 특히 변경되지 않은 값들은 이미 예상할 수 있기 때문에 검증을 생략해도 문제없음.
  - 테스트 목적
    - 테스트는 로직이 예상대로 작동하는지 확인해 수정된 값들이 제대로 처리되는지 것이므로 변경된 값에 대해서만 집중.

- 변경되지 않은 값들에 대한 간접적 검증.
  > 변경되지 않아야 할 부분도 간접적으로 검증하는 것이 중요.
  > 만약 변경되지 않은 값이 의도치 않게 변경되면, 버그나 논리적 오류가 발생한 가능성 있음.
  > 하지만 **모든 값을 검증하는 것은 비효율적**이므로,
    **변경되지 않는 값 중 핵심적인 값들만 검증**하여 테스트의 효율성을 유지.
  - 핵심 식별자(예: id, seller, code, name 등)가 의도치 않게 변경되지 않았는지 간단히 검증.
  - 비즈니스 로직에 따라 중요한 값이 변경되지 않아야 하는 경우, 해당 값도 검증.

1. Mockito의 verify()
   ~~~
    // then
    // 토큰이 redis에 1번 등록됨.(1번 수행됨)
    Date now = new Date();
    long validTime = expirationDate.getTime() - now.getTime();
    verify(redisSingleDataService, times(1))
            .saveSingleData(eq(token), eq("blacklist"), argThat(duration ->
                    duration.compareTo(Duration.ofMillis(validTime)) >= 0));
   ~~~
   - 특정 메서드가 특정 횟수만큼 호출되었는지 검증.
     ~~~
     // Mockito.verity(mockObject, times(n)).method()
     
     // redisSingleDataService.saveSingleData() 메서드가 호출되었는지 검증.
     verify(redisSingleDataService, times(1)).saveSingleData();
     ~~~
   - eq()
     - Mockito의 Argument Matcher 중 하나.
     - 특정 인자가 기대하는 값과 정확히 일치하는지 검증.              .
     ~~~
     // 호출이 정확한 값들로 이뤄졌는지 확인. 
     verify(redisSingleDataService, times(1))
            .saveSingleData(eq(token), eq("blacklist"), any());
     ~~~
   - Mockito의 ArgumentMatcher는 eq()와 같은 기본 메서드 외에도, 임의의 조건을 설정하여 검증할 수 있는 방법을 제공.
     1. argThat()
        - 사용자정의 조건을 지정.
        ~~~
        // 전달된 duration값이 Duration.ofMillis(validTime)보다 크거나 같은지 비교하는 사용자정의 조건 설정.
        // compareTo() : 객체간 크기 비교 시 사용.
        //             : duration이 크면 1 / duration이 작으면 -1 / duration과 값이 동일하면 0
        argThat(duration -> duration.compareTo(Duration.ofMillis(validTime)) <= 0);
        ~~~
     2. ArgumentCaptor를 사용해 값을 캡쳐하고 검증.
        - 메서드에 전달된 값을 캡쳐한 후 이를 비교.
        - 실제로 메서드 호출 시 전달된 값을 캡쳐한 후 후속 검증을 통해 조건 검사.
        ~~~
        // duration값 캡쳐를 위해 ArgumentCaptor<Duration> 객체생성
        ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);

        // duration값 캡쳐를 위해 duration값 자리에 durationCaptor.capture() 전달.
        verify(redisSingleDataService, times(1)).saveSingleData(eq(token), eq("blacklist"), durationCaptor.capture());

        // Duration 캡쳐값 가져와 유효시간이 validTime 이상인지 검증
        Duration capturedDuration = durationCaptor.getValue();
        assertTrue(capturedDuration.toMillis() <= validTime, "Duration should be less than or equal to validTime");
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


### < 테스트코드 전달인자 검증 >
- 참고 ProductionServiceTest.successModifyProduction()
- 주로 toDto **메소드에 전달된 값들이 의도한 대로 정확히 전달되었는지를 확인**.

- 전달인자 검증방법
  - ArgumentCaptor를 사용하여 전달인자를 캡처한 뒤 이를 검증하면 해당 객체가 수정된 값들을 정확하게 포함하고 있는지 확인가능.
  - 이 테스트 코드에서는 전달인자 검증이 중요한 부분이므로
    ArgumentCaptor를 사용해 toDto 메소드로 전달된 Production 객체의 속성값들이 예상대로 처리되었는지 검증하는 핖요.

- ArgumentCaptor 사용해 전달인자 검증 예시
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
    1. Production 객체에 대한 검증
       - toDto 메소드로 전달된 Production 객체가 수정된 설명(updateDescription)과 판매 상태(updateSaleStatus)를 정확히 포함하는지 검증.
    
    2. ProductionOption 객체에 대한 검증
       - ResponseProductionOptionDto가 수정된 quantity와 일치하는지, 그리고 신규 추가된 옵션에 대해서도 id 값이나 quantity 값이 예상대로 처리되는지 검증.
    
    3. toDto에 전달된 Production 객체 검증
       - ArgumentCaptor를 사용하여 productionMapper.toDto()로 전달된 Production 객체를 캡처하고, 그 값들을 검증합니다.
    
    4. 옵션 quantity 및 price 값이 올바르게 전달되는지 확인
       - ProductionOption에 대해 업데이트와 삽입된 옵션이 quantity와 price를 올바르게 갖고 있는지 검증합니다.

1. productionMapper.toDto() 메소드의 전달인자 검증
    - 메소드가 호출되었을 때, 실제로 Production 객체가 올바르게 전달되었는지 확인가능.
    - expectedProductionEntity가 productionMapper.toDto()에 전달된 객체일 때,
      그 객체가 올바르게 수정된 값들을 포함하고 있는지 확인하는 것입니다.

2. modifyProductionOptionMapper.toEntity() 메소드의 전달인자 검증
    - RequestModifyProductionOptionDto 객체를 ProductionOption으로 변환하는 로직.
    - 이 과정에서 전달되는 DTO 값들이 실제로 Entity로 올바르게 변환되는지 검증.
    - quantity나 price 값이 제대로 전달되는지 검증가능.

3. ProductionOption 관련 인자들 검증
    - 수정된 옵션(RequestModifyProductionOptionDto)들이
      실제로 서비스 로직에 전달될 때 값들이 올바르게 전달되었는지 확인 필요.
    - RequestModifyProductionOptionDto에서 quantity, price, optionCode 등이 올바르게 설정되었는지 확인가능.
    - expectedResponseOptionDtoList에 있는 값들이 요청 DTO에서 예상한 값과 일치하는지 검증가능.

4. 서비스 내에서 인자 검증
    - productionService.modifyProduction()에서 인자들이 올바르게 처리되는지도 중요한 검증 대상.
    - requestProductionDto의 값들이 제대로 전달되었는지.
    - productionService.modifyProduction() 내부에서 originProductionEntity에 대한 수정이 예상대로 이루어졌는지.

      
### < Reflection - private 메서드 테스트코드 작성 >
- 리플렉션을 사용 시 클래스의 메서드나 필드에 직접 접근할 수 없더라도 접근 제어자에 관계없이 해당 클래스의 메서드나 필드를 검사하거나 수정가능.
- 자바의 java.lang.reflect 패키지에서 제공되는 클래스를 사용.
- 프로그램 실행 중에 클래스, 메서드, 필드 등을 동적으로 다룰 수 있는 기능.
- 리플렉션을 사용할 때는 정말 필요한 경우에만 사용하고, 과도하게 사용하지 않아야함.

- 리플렉션을 사용할 때의 주의점
  - 성능 저하
    - 리플렉션은 일반적인 메서드 호출보다 성능이 낮음.
  - 컴파일 시점 검사 불가능
    - 리플렉션을 사용하면 컴파일 타임에 오류를 잡기 어렵고, 런타임 시에만 오류가 발생가능.
  - 불완전한 설계
    - 리플렉션을 지나치게 많이 사용하면 코드가 복잡해지고, 객체 지향 설계 원칙에 어긋날 수 있음.

1. 메서드 호출
   - Class.getDeclaredMethod() 또는 Class.getMethod() 
     - 해당 클래스의 메서드 **객체를 가져옴.**
   - setAccessible(true)
     - private 메서드에 **접근가능하도록 변경.**
   - Method.invoke()
     - 해당 메서드를 **실행.**
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

2. 필드값 변경
   - Field.setAccessible(true)
     - private 필드에 접근가능하도록 변경.
   - Field.set()
     - 값을 수정.


### < Controller 테스트케이스 작성 - 도저히 안되서 Chat gpt를 통해 작성 >
- MockMvc를 이용한 테스트 목적
  -  컨트롤러의 엔드포인트를 호출하여 HTTP 클라이언트의 요청을 모방하고 적절한 응답을 확인하기 위해 테스트 수행.
- @WebMvcTest(MemberController.class)
   - MockMvc 객체를 생성해 컨트롤러와 상호작용함.
   - 명시한 해당 컨트롤러만 격리시켜 단위테스트 수행가능.
   - value : 테스트 할 controller 클래스 명시.
- @MockBean
   - 테스트 환경에서 애플리케이션 컨텍스트(ApplicationContext)의 빈을 모킹하도록 설정.
   - Spring Boot 3.4.0부터 deprecated된 @MockBean과 거의 동일.
   - 빈으로 등록해주는 mock
   - 자동으로 빈으로 등록되어 컨트롤러에 주입됨.


1. 일반적으로 **@WebMvcTest**와 **MockMvc**를 사용하여 Controller를 테스트할 수 있습니다.    
   이 테스트는 Controller의 HTTP 요청과 응답을 실제로 처리하는 방식으로 동작합니다.   
   다음은 signUpSeller() 메서드의 성공적인 테스트 케이스입니다.   
   이 예제에서는 @MockBean을 사용하여 의존성 주입되는 MemberService를 mock하고, MockMvc를 사용하여 실제 HTTP 요청을 시뮬레이션합니다.
   - 코드 설명
       - MockMvc 설정
           - MockMvcBuilders.standaloneSetup(memberController).build()로 MockMvc를 설정하여 Controller를 테스트할 수 있게 만듭니다.
       - Mock 객체 설정
           - @Mock 어노테이션을 통해 MemberService를 mock하고, @InjectMocks를 사용하여 MemberController에 mock된 MemberService를 주입합니다.
       - MockMvc로 HTTP 요청 시뮬레이션
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


2. 스프링부트 3.4.0 버전 이상인 경우 @Mock 어노테이션 사용 불가.
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


3. 테스트코드를 작성시 @MockMvc 어노테이션을 적용하지 않는 이유
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


---
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
### < DTO >
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

  
---
### < request, response DTO 분리 >
- client->controller에 들어오는 request 정보와 controller->client로 내보내는 response는
  사용하는 목적이 다르고 전달하는 데이터도 다르기에 분리하는 것이 좋다.
- DTO는 주고받는 데이터를 정의한 api 명세서와 다름없다.
- 단건, 복수 건 조회 정도만 동일 DTO 사용으로 운명을 함께할 수 있다.

1. request DTO
    - validation check 어노테이션을 사용 -> 데이터의 유효성 검사.
2. response DTO
    - 단순히 데이터를 반환.


---
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
### < Enum값 비교 연산자 >
- Java에서 Enum 값을 비교할 때는 **== 연산자를 사용**하는 것이 더 안전하고 효율적.
- 연산자 비교하기
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


---
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
### < 컨트롤러에서 @RequestParam을 DTO(Data Transfer Object)로 받는 이유 >
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


---
### < 컨트롤러에서 파라미터값 받는 방법 >
- DTO (Data Transfer Object)를 사용할 때 @RequestParam을 사용하지 않는 이유는
  DTO 객체를 자동으로 매핑하기 위해 Spring이 사용하는 방식 때문입니다.

1. GET 쿼리파라미터
    - @RequestParam은 개별 쿼리 파라미터를 메서드 파라미터로 매핑할 때 사용.
    - 쿼리 파라미터가 하나하나 대응되어야 하는데, DTO 객체처럼 여러 필드를 묶어서 전달하는 경우에는
      @RequestParam으로 직접 받는 것이 번거롭고 불편할 수 있습니다.
   ~~~
    // 각 파라미터마다 @RequestParam을 명시해주어야 하기 때문에 필드가 많을 경우 코드가 길어지고 불편.
   
    @RequestMapping("/search")
    public String search(@RequestParam("name") String name, @RequestParam("status") String status) {
        // name과 status를 각각 받아서 처리
    }
   ~~~

2. GET 쿼리파라미터와 DTO
    - Spring MVC에서는 DTO 객체를 쿼리 파라미터와 자동으로 매핑가능.
    - @RequestParam 없이 DTO의 필드 이름과 동일한 쿼리 파라미터가 있을 경우
      자동으로 해당 값을 DTO에 주입해주기 때문에, 하나의 객체로 여러 파라미터를 묶어 처리가능.
   ~~~
    // 이 방식에서는 name, status 같은 쿼리 파라미터가 있으면, 
    //  Spring이 자동으로 RequestSearchProductionDto 객체의 필드에 매핑. 
    // 이 방식은 쿼리 파라미터가 많을 때 유용하고, 코드를 간결하게 유지가능.
    
    @RequestMapping("/search")
    public String search(RequestSearchProductionDto requestSearchProductionDto) {
        // method logic
    }
   ~~~

3. GET,POST requestBody
    - @RequestBody는 HTTP 요청의 본문에 포함된 데이터를 객체로 변환해서 받는 방식.
    - 보통 POST 요청에서 JSON 데이터를 받을 때 사용.
    - 쿼리파라미터와 달리 DTO로 데이터를 받을 때 @requestBody 어노테이션을 사용해야함.

