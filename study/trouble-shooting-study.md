# MY E-COMMERCE PROJECT TROUBLE SHOOTING
2025 간단한 이커머스 서비스를 만들기위한 고민


---
### < 요구사항 명세서 조건을 디테일하게 정리하는 이유 >
구현할 기능들에 대한 조건들이 나열되어야 기능 구현을 올바르게 했는지 체크할 수 있기 때문입니다.

---
### < 정확도순 검색 구현방법 - 좀 더 검색해보고 고민해볼 것 >
1. Java에서 Comparator, Comparable을 이용해 정확도에 따라 내림차순으로 정렬합니다.
2. AWS OpenSearch에서 preference node를 지정해 정확도 점수로 정렬합니다.
3. MySQL에서 문자열 검색 결과를 적합도 순으로 정렬합니다.
    - 참고블로그 : https://m.blog.naver.com/jellyqueen/220426967592

> - 세 가지 방법 중 mysql 문자열 검색 결과를 적합도 순으로 정렬을 선택한 이유.   
    > pageable 객체를 전달해 쿼리를 조회하므로, 쿼리에서 이미 데이터가 정렬된 상태에서 5건의 결과를 가져와야한다.
    > 따라서 java에서 like 검색해 일치하는 전체 데이터를 가져와 내림차순 정렬해 5건의 데이터를 가져오는 것보다 쿼리를 통해 정렬하는 것이 더 효율적이라 생각했다.

---
### < 상품목록 조회 정렬기준 변경에 따른 설정 >
> - 멘토의 코멘트   
>   검색시마다 order, review 를 join 하여 count 쿼리하는 것은 전체적으로 성능이 많이 안좋고, 개선이 필요할 것 같습니다.    
    만일 상품 하나당 주문이 1만건이고, 리뷰도 1천개면 어떻게 될까요?   
    상품 목록 조회시 응답에는 리뷰 수, 평균 별점의 정보를 내려줄때도 마찬가지입니다.    
    조회 API 를 호출할때마다 매번 table join 해서 계산하는 방법보다 다른 방법을 찾아볼 수 있을까요?

> - 고민에 대한 결과
>

---
### < 장바구니 기능구현 >
redis 캐시를 사용할지 DB에 사용자별 장바구니 테이블을 사용할지 결정필요.
생각해보면 로그인, 로그아웃 여부에 관계없이 장바구니 내역을 유지해야하므로 DB에 저장하는 것이 바람직해보인다.
하지만 redis 캐싱 기능을 사용해보기 위해 redis를 이용해 장바구니 기능을 개발해본다.
추후 로그인하지 않은 사용자는 redis 캐시를 이용해 장바구니를 지원하고, 로그인한 사용자는 장바구니 DB 정보를 이용해 장바구니를 지원하도록 변경해본다.
- 추가기능 : 구매자가 장바구니에 상품을 담는 경우 한 달 동안 유지되어야 한다.

---
### < 주문 시 추가 프로세스 >
- 추가가능 : 결제 완료 시 결제확인 이메일 발송
- 추가기능 : PG 결제 외 추가 결제수단 사용가능할 것


---
### < Dto에 Entity->Dto 변환 메서드를 제거한 이유 >
- 기존 Dto->Entity로 변환하는 메서드를 작성했다.   
  그러다 보니 Entity->Dto로 변환하는 메서드도 있으면 유용할 듯 해 메서드를 생성했다.   
  Entity->Dto로 변환할 때 아래와 같이 Dto 객체를 생성해 호출해야하는 불편함이 있었다.    
  따라서 제거하기로 했다.   
  ~~~
  public class MemberService {
      //...
      public MemberDto saveMember(MemberDto member) {
          //...
          // Entity -> Dto 변환
          MemberDto returnMemberDto = new MemberDto();
          returnMemberDto.toDto(savedMember);
          return returnMemberDto;
     }
  }
  ~~~

- **DTO에서 ENTITY->DTO로 변환하는 메서드를 작성하면 안되는 이유.**
  - 사용에 불편하니 올바른 코드는 아닌 것 같은데 코드를 어떻게 잘못 작성한건지 이유를 모르겠어서 찾아봤다.   
  - Entity를 DTO로 변환하는 메서드를 DTO 클래스에 작성하지 않는 이유
  
        책임 분리, 유지보수성, 그리고 설계의 일관성을 고려한 원칙에 기반한다.    
        DTO 클래스는 일반적으로 단순한 데이터 전송 객체로 설계되어야한다.   
        변환 로직을 DTO에 포함시키는 것은 **단일 책임 원칙(SRP)과 같은 객체지향 설계 원칙을 위반**할 수 있다.   
        DTO에 작성한 **Entity->Dto 변환 로직은 서비스 계층이나 별도의 변환(Mapper) 클래스에서 처리하는 것이 더 적절**하다는 결과를 얻었다.
        또한 변환 로직을 분리하면 코드의 응집력이 높아지고, 확장성과 유지보수성도 향상됩니다.
 
        < DTO와 변환로직 분리 시 이점(?) >
        1. 단일책임원칙
        2. 책임분리
        3. 유지보수성 및 테스트 용이성
        4. DTO는 외부와의 인터페이스
        5. 객체지향 설계 원칙에 따른 최적화

- 위반내용
  1. 단일 책임 원칙 (Single Responsibility Principle) 위반    
     DTO (Data Transfer Object) 는 데이터를 전송하는 역할을 한다.    
     일반적으로 DTO는 데이터를 표현하기 위한 단순한 POJO(Plain Old Java Object)로, 
     getter, setter, 생성자 및 필드만 포함되어야 한다.   
     DTO 클래스에 변환 로직(Entity에서 DTO로 변환)을 포함시키면, 그 클래스는 두 가지 책임을 가지게 된다.  
     이러한 **책임의 중복은 DTO 클래스를 복잡하게 만들며, 변경에 취약하게 만든다.**    
     설계가 복잡해지므로, 단일 책임 원칙(SRP)을 지키기 위해 변환 로직은 별도로 분리하는 것이 좋다.
     - 데이터를 전송하는 책임
     - 데이터를 변환하는 책임      
  2. 책임 분리 (Separation of Concerns)   
     DTO는 단순한 데이터 객체일 뿐, 데이터의 변환은 비즈니스 로직에 속한다.   
     DTO 클래스가 변환 로직을 포함하게 되면, 영속성 계층(Entity)과 비즈니스 계층(DTO 변환)이 결합되어 책임이 모호해질 수 있다.   
     변환 로직을 서비스 계층이나 매퍼 클래스(예: ModelMapper, MapStruct)에서 처리하면, **각 
     계층의 책임이 명확히 분리되고, 코드를 변경하거나 확장할 때 문제가 덜 발생**한다.
  3. 유지보수성 및 확장성   
     **변환 로직을 DTO에 넣으면, 데이터 변환 방식이 변경될 때마다 DTO 클래스의 수정이 필요**해진다.    
     예를 들어, 엔티티 클래스가 변경되면, 그에 맞는 DTO 변환 로직을 DTO 클래스에서 수정해야 할 수 있다.   
     이를 해결하기 위해 **변환 로직을 서비스 계층이나 매퍼 클래스에서 분리하면, 
     엔티티와 DTO 간 변환 로직을 한 곳에서 관리할 수 있어, 유지보수성이 높아지고, 향후 확장에도 유리**하다.
  4. DTO는 외부와의 인터페이스   
     **DTO**는 데이터 전송을 위한 객체이다.   
     **외부 시스템이나 클라이언트와의 데이터 교환을 목적으로 사용**된다. 
     변환 로직을 DTO에 넣으면, DTO가 내부 비즈니스 로직과 결합되는 문제가 발생할 수 있다.   
     예를 들어, **DTO에 변환 메서드를 추가하면, DTO를 사용하는 서비스에서 DTO가 엔티티와의 관계를 알고 있어야 한다.**   
     이는 DTO의 본래 역할인 단순한 데이터 전송과 충돌하며, **코드의 결합도가 높아**진다.
  5. 객체지향 설계 원칙에 따른 최적화   
     변환 로직을 서비스 클래스나 매퍼 클래스에 두면, 각 클래스가 자기 역할에 충실하게 된다.   
     **DTO는 데이터를 전송하는 역할에만 집중하고, Service나 Mapper는 변환 로직을 맡게된다.**
     또한, ModelMapper, MapStruct 등의 라이브러리를 사용하면, 변환 로직을 더 효율적으로 관리하고, 자동화할 수 있어 코드가 더 간결하고 유지보수하기 좋다.

- **대안방법**
  - Entity를 DTO로 변환하는 메서드를 DTO 클래스에 작성하지 않는 이유는 책임 분리, 유지보수성 향상, 설계의 일관성 등을 고려한 객체지향 설계 원칙에 기인한다.  
    DTO는 단순한 데이터 전송 객체로, 변환 로직은 서비스 계층이나 매퍼 클래스에서 처리하는 것이 더 나은 설계이다.    
    변환 로직을 분리하면 코드의 응집력이 높아지고, 확장성과 유지보수성도 향상된다.
  - DTO 변환을 별도의 서비스계층 or 유틸리티 클래스에 작성하여 Entity와 DTO 변환을 처리.
  1. 서비스계층에서 변환
     - service에서 Entity를 DTO로 변환가능.
       ~~~
       public class MemberService {
          //...
           public MemberDto saveMember(MemberDto member) {
               //...
               // Entity -> Dto 변환
               return MemberDto.builder()
                       .id(savedMember.getId())
                       .userId(savedMember.getUserId())
                       .password(savedMember.getPassword())
                       .name(savedMember.getName())
                       .tel1(savedMember.getTel1())
                       .tel2(savedMember.getTel2())
                       .tel3(savedMember.getTel3())
                       .address(savedMember.getAddress())
                       .delYn(savedMember.getDelYn())
                       .authorities(savedMember.getAuthorities())
                       .build();
           }
       }
       ~~~
  2. Mapper 클래스 사용
     1. 수동으로 변환 메서드 작성하기: 매핑을 직접 구현하는 방법. 
        - 변환 로직을 직접 작성하여 Entity와 DTO 간 변환을 처리합니다. 유연성과 커스터마이징이 가능하지만, 코드가 다소 길어질 수 있다.
        ~~~
        import org.springframework.stereotype.Component;
 
        @Component
        public class MemberMapper {

           // Entity -> DTO 변환
           public MemberDto toDto(Member member) {
               if (member == null) {
                   return null;
               }

               MemberDto memberDto = new MemberDto();
               memberDto.setId(member.getId());
               memberDto.setUserId(member.getUserId());
               memberDto.setName(member.getName());
               memberDto.setPassword(member.getPassword());
               memberDto.setTel1(member.getTel1());
               memberDto.setTel2(member.getTel2());
               memberDto.setTel3(member.getTel3());
               memberDto.setAddress(member.getAddress());
               memberDto.setDelYn(member.getDelYn());
  
              return memberDto;
           }

           // DTO -> Entity 변환
           public Member toEntity(MemberDto memberDto) {
               if (memberDto == null) {
                   return null;
               }

               Member member = new Member();
               member.setId(memberDto.getId());
               member.setUserId(memberDto.getUserId());
               member.setName(memberDto.getName());
               member.setPassword(memberDto.getPassword());
               member.setTel1(memberDto.getTel1());
               member.setTel2(memberDto.getTel2());
               member.setTel3(memberDto.getTel3());
               member.setAddress(memberDto.getAddress());
               member.setDelYn(memberDto.getDelYn());

               return member;
           }
        }
        ~~~
     2. 라이브러리 사용하기: ModelMapper, MapStruct 등과 같은 라이브러리를 사용해 변환 로직을 외부 클래스로 분리하는 방법.
        - MapStruct 의존성 추가해 해당라이브러리 이용.
        - MapStruct를 사용하면, 변환 로직을 수동으로 작성할 필요 없이 
          **인터페이스에 메서드를 정의만 하면 컴파일 시에 변환 코드를 자동으로 생성**해준다.
        - 컴파일 타임에 자동으로 변환 코드를 생성하여 수동 작성보다 간결하게 변환을 처리 가능하다. 
          성능이 우수하고, 유지보수하기 용이하다.
        - MapStruct는 Java bean 유형 간의 매핑 구현을 단순화하는 코드 생성기. 
        - ModelMapper 대신 MapStruct 라이브러리 선택이유.
          - https://yj9011.tistory.com/51
        ~~~
        import org.mapstruct.Mapper;
        import org.mapstruct.factory.Mappers;

        @Mapper(componentModel = "spring")
        public interface MemberMapper {

            MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);
  
            MemberDto toDto(Member member);
            Member toEntity(MemberDto memberDto);
        }
        ~~~

---
### < 도메인 관리방법 >
두 방법 모두 장단점이 있기 때문에, 프로젝트의 요구 사항과 운영 환경에 맞게 선택해야한다.
DB에서 공통코드, Enum 사이에서 가장 중요한 부분은 **얼마나 자주 추가 및 변경이 일어나는가?** 이다.

동적인 값 변경이 필요하고, 운영 환경에서 관리가 편리해야 한다면 DB에서 관리하는 것이 유리하다.
고정된 값이고, 타입 안정성과 성능이 중요한 경우 Enum을 사용하는 것이 더 좋다.
따라서, 값이 자주 변경될 가능성이 있거나 외부 시스템과 연동하는 경우라면 DB 관리가 더 유리하고, 
그렇지 않다면 Enum으로 관리하는 것이 좋다.

또한 상태 값이 많이 필요하다면 많은 Enum 파일을 관리하는 것은 비효율적일 수 있기 때문에
도메인별로 너무 많은 Enum을 생성해 관리하는 대신 설정 파일이나 DB 테이블로 관리하는 방법도 고려할 수 있다.    
이렇게 하면 코드베이스의 Enum 파일 수를 줄일 수 있으며, 
상태 값이 변경될 때마다 애플리케이션을 재배포하지 않고도 시스템의 동작을 수정할 수 있다.   

> 초기 설계 시 공통코드로 관리할지 enum으로 관리할지 고민했다.      
  그런데 추후 도메인이 계속 추가된다면 enum 클래스가 계속 추가되는 것 보다 DB에서 공통코드로 관리하는 것이 더 좋을 것이라고 생각했기 때문이다.
>
> 현재 프로젝트의 경우 도메인으로 관리해야하는 부분은 회원권한분류, 상품분류, 주문상태 정도이다.   
  해당 도메인들은 추가, 변경이 발생할 수는 있어도 자주 발생할 일은 없다.
> 
> 생각해보니 소규모 프로젝트로 추가적 확장의 여지가 거의 없고 해당 도메인들은 고정된 값이기에 타입 안정성의 정점을 가지는 ENUM 으로 관리하는 게 합리적일 수 있을 것으로 판단. 
  따라서 Enum으로 관리하도록 변경.

1. Java Enum으로 관리
   - 타입 안정성   
     Enum을 사용하면 컴파일 시점에서 타입 체크가 이루어지기 때문에 오류를 미리 방지할 수 있습니다.
   - 성능   
     Enum은 메모리에 상주하는 값이므로 DB 접근 없이 빠르게 값을 사용할 수 있습니다.
   - 단순성   
     코드 내에서 관리되므로 DB와의 동기화 문제나 관리가 비교적 단순합니다.
   - 유연성 부족   
     새로운 주문 상태가 필요할 때마다 애플리케이션을 수정하고 배포해야 하므로 유연성이 떨어집니다.
   - 다국어 지원    
     Enum 자체에는 다국어 처리가 어렵기 때문에, 다국어를 지원하려면 별도로 추가적인 관리가 필요할 수 있습니다.

2. DB에서 공통코드로 관리
   - 유연성   
     새로운 주문 상태나 코드 값을 추가하거나 변경할 때, 애플리케이션을 재배포하지 않고 DB만 수정하면 되기 때문에 유연합니다.
   - 관리 용이성   
     관리자가 DB에서 직접 상태 코드나 값을 수정할 수 있으므로 비즈니스 측면에서 더 편리할 수 있습니다.    
     예를 들어, 주문 상태가 바뀔 때마다 코드 추가나 수정이 필요할 때 유용합니다.
   - 다국어 지원   
     주문 상태에 대한 이름을 DB에서 관리하면서 다국어로 지원할 수 있습니다.   
     상태 코드만 DB에 두고, 각 언어에 맞는 상태명을 별도로 관리할 수 있습니다.   
   - 추가적인 DB 접근 필요   
     상태 값을 조회할 때마다 DB에 접근해야 하므로 성능에 영향을 미칠 수 있습니다.
   - 복잡성   
     DB에서 상태값을 관리하고, 이를 자바 코드와 동기화하는 작업이 추가적으로 필요합니다.    
     코드와 DB 상태가 일치하는지 관리해야 할 책임이 개발자에게 있습니다.


---
### < 노출되면 안되는 정보 관리 방법 결정 >
1. 환경 변수 사용 
   - 코드베이스에서 민감한 정보를 직접 다루지 않기 때문에 코드가 노출되어도 비밀 정보가 유출되지 않습니다. 
   - 설정 파일을 통한 노출을 방지하고, **환경에 따라 다르게 설정 가능**합니다.
   - 서버의 접근 권한을 통해 환경 변수를 볼 수 있는 위험이 있으며, 관리하는 서버(운영 체제)에 대한 보안이 중요합니다.
   - Java에서는 System.getenv("VARIABLE_NAME")를 사용하여 환경 변수 값을 읽을 수 있습니다.
   
2. 보안 파일 또는 키 관리 서비스 사용 
   - 클라우드 제공자들(AWS, GCP, Azure 등)은 비밀 키를 안전하게 관리할 수 있는 서비스들을 제공합니다.   
     (민감한 정보를 안전하게 저장하고 관리할 수 있으며, 필요한 경우에만 접근할 수 있도록 설정가능.)
   - 이 방법은 여러 가지 보안 장치가 적용되어 있어 매우 안전합니다.
     - 자동화된 관리: 비밀번호, API 키, 인증서와 같은 민감한 데이터를 안전하게 저장하고, 애플리케이션에서 이를 동적으로 로드할 수 있습니다.
     - 암호화: 비밀 정보를 자동으로 암호화하여 저장합니다. 또한, 복호화 키를 안전하게 관리하고, 비밀 데이터에 대한 액세스를 제어할 수 있습니다.
     - 접근 제어: 각 애플리케이션이나 사용자에게 특정 비밀 정보에 대한 액세스를 제어할 수 있으며, 최소 권한 원칙(least privilege)을 따를 수 있습니다.
     - 자동화된 로테이션: 비밀번호나 API 키와 같은 민감한 정보의 주기를 설정하여 자동으로 교체할 수 있습니다.
     - 감사 로그: 언제, 누가, 어떤 정보에 접근했는지에 대한 감사 로그를 자동으로 기록합니다.
   - ex) AWS의 Secrets Manager, Azure Key Vault 등

3. 암호화
   - application.properties 파일은 코드베이스에 포함되기 때문에 민감한 정보를 저장하는 것은 보안상 좋지 않습니다.
   - 소스 코드가 외부에 노출되거나 관리되지 않은 상태에서 해당 정보가 유출될 수 있기에 보안상 좋지 않습니다.
   - 만약 application.properties에 저장할 수밖에 없다면, 해당 정보를 암호화하여 저장하고, 프로그램 실행 중에 복호화하는 방법을 사용할 수 있습니다.
   - 암호화된 설정 파일을 사용하는 방법도 가능하지만, 이 방법은 관리가 복잡하고 키 관리가 제대로 이루어지지 않으면 여전히 안전하지 않을 수 있습니다.    
     또한 암호화된 파일을 읽을 수 있는 애플리케이션이 필요하고, 그 암호화 키가 유출되지 않도록 관리해야 하기 때문입니다.

4. **Spring Boot의 @Value와 application.yml 활용**
   - Spring Boot에서는 application.yml이나 application.properties 대신, 보안 정보를 환경 변수나 외부 시스템에서 읽어오는 방식을 사용할 수 있습니다.
   - 결론적으로, application.properties 파일에 민감한 정보를 저장하는 것은 피하고, 외부 환경 변수나 보안 시스템을 활용하는 것이 더 안전합니다.
   - **application.properties 파일은 일반적으로 개발 환경에서 사용됩니다.**
   - 운영 환경에서는 민감한 정보가 다른 방법으로 관리되어야 합니다.   
     예를 들어, 운영 환경에서는 클라우드 비밀 관리 서비스나 환경 변수 등을 사용하여 민감한 정보를 관리하는 것이 더 안전합니다.
   - 토이 프로젝트라도 민감한 정보를 application.properties 파일에 저장하는 것은 피하는 것이 좋습니다.
     대신 환경 변수나 비밀 관리 서비스, .env 파일 등을 사용하는 것이 보안상 더 안전하고, 실제 배포 환경에서도 유용한 방법입니다.   
     코드에 민감한 정보를 하드코딩하는 것은 최악의 선택이므로, 가능한 안전한 방법을 사용해야 합니다.   
     만약 application.properties 파일에 민감한 정보를 작성했다면, 이를 .gitignore 파일에 추가하여 Git에 커밋되지 않도록 설정해야 합니다.   

     1. 환경 변수 사용   
       민감한 정보를 코드에서 하드코딩하지 않고 환경 변수로 관리하는 것이 훨씬 안전합니다. 이를 통해 각 환경별로 민감한 정보를 쉽게 다룰 수 있습니다.   
       예를 들어, application.properties에서 환경 변수로 값을 참조할 수 있습니다.   
       운영 시스템에서는 DB_USERNAME과 DB_PASSWORD 환경 변수를 설정하여 해당 값을 사용할 수 있습니다.
        > application.properties에서 변수 호출 예시   
        > spring.datasource.username=${DB_USERNAME}   
        > spring.datasource.password=${DB_PASSWORD}

     2. .env 파일 사용   
       개발 환경에서는 .env 파일을 사용하여 민감한 정보를 관리할 수도 있습니다.    
       .env 파일은 Git에 커밋하지 않도록 .gitignore에 추가해야 합니다.   
       그 후 Java에서 이를 읽어오는 방식으로 사용할 수 있습니다. Spring Boot에서는 dotenv를 로드하는 라이브러리를 사용할 수 있습니다.
        > .env 파일 설정 예시
        > DB_USERNAME=your_db_username   
        > DB_PASSWORD=your_db_password

> - secret key 노출되지 않도록 하기위해 선택한 방법.   
> 보안상으로는 키관리 서비스를 이용하는 것이 가장 안전하다.    
> 하지만 유료 서비스이며 개인 프로젝트에서는 오버 스펙으로 판단된다.   
> 그래서 남은 방법 중 최선인 환경변수사용 방법을 선택하려고 했지만, 스터디를 위한 프로젝트인 점을 감안해 application.properties 파일에 작성해도 무방하겠다고 생각했다.   
> 따라서 Mysql DB password 정보, JWT secret key는 민감정보임에도 불구하고 유료 api secret key 같은 정보가 아니므로 문제없다고 판단해 application.properties 파일에 작성하도록 한다.


---
### < Base64로 인코딩만 한 정보는 보안에 취약 >
Base64 인코딩은 암호화가 아니라 단순히 데이터를 텍스트 형식으로 변환하는 방법입니다.
암호화 알고리즘을 포함하지 않기 때문에 보안에 취약합니다.
Base64는 데이터를 인간이 읽을 수 있는 형태로 변환하는 방식일 뿐, 데이터를 보호하거나 보안을 강화하는 기능이 없습니다. 그래서 암호화와는 다릅니다.
데이터 전송 과정에서 바이너리 데이터를 텍스트로 변환하여 텍스트 기반 프로토콜을 통해 안전하게 전송할 수 있게 도와주지만, 보안을 위한 방법은 아닙니다.
보안을 위해서는 실제 암호화 알고리즘 (예: AES, RSA 등)을 사용해야 합니다.

1. Base64가 보안에 취약한 이유
    - 암호화가 아님
        - Base64는 데이터를 변환할 뿐, 보안을 제공하지 않습니다. 데이터를 Base64로 인코딩하면 원본 데이터가 쉽게 복원될 수 있습니다. 누구나 Base64로 인코딩된 데이터를 쉽게 디코딩할 수 있기 때문에 실제 보안이 되지 않습니다.
    - 복호화가 쉬움
        - Base64로 인코딩된 데이터를 복호화하는 것은 매우 간단합니다. 이를 수행하는 도구나 라이브러리가 대부분의 프로그래밍 언어나 시스템에 기본적으로 제공됩니다. 암호화된 데이터와 달리 복호화 과정이 특별한 키나 알고리즘을 필요로 하지 않습니다.
    - 구성된 데이터는 원본 그대로임.
        - Base64로 인코딩된 데이터는 원본 데이터의 단순한 변환에 불과합니다.
        - 예를 들어, "Hello"라는 텍스트를 Base64로 인코딩하면 "SGVsbG8="가 되지만, 이를 다시 디코딩하면 원본 "Hello"를 그대로 얻을 수 있습니다.
        - 이 때문에 데이터의 내용이 외부에 노출되었을 때 정보 보호가 되지 않습니다.

2. 암호화와 인코딩의 목적
    - 암호화(Encryption)
        - 암호화는 **데이터를 보호**하기 위해 사용되며, 데이터를 읽을 수 없게 변환하여 **허가되지 않은 사용자가 이를 해석할 수 없도록** 합니다.
        - 암호화된 데이터를 복호화하려면 키나 알고리즘을 사용해야 하며, 그 과정이 안전하게 설계되어야 합니다.
        - 암호화는 보안이 중요한 환경에서 사용됩니다.
    - 인코딩(Encoding)
        - 인코딩은 **데이터를 특정 형식으로 변환**하는 방식입니다.
        - 예를 들어, Base64는 바이너리 데이터를 텍스트로 변환하는 데 사용됩니다.
        - 이 과정은 **데이터를 읽기 좋게 하거나 시스템에서 처리할 수 있는 형식으로 바꾸는 것**이지, 데이터를 보호하는 목적이 아닙니다.
        - Base64는 복호화가 아주 간단하며, 암호화처럼 보안을 강화하지 않습니다.   
          따라서, Base64는 보안 기능을 제공하지 않기 때문에 암호화된 데이터나 중요한 정보를 보호하는 데는 적합하지 않습니다.
          만약 중요한 정보를 보호하려면, 암호화 알고리즘을 사용해야 합니다.


### < 암호화 알고리즘 >
아래의 링크를 포함해 공부필요   
https://velog.io/@choidongkuen/Spring-AES-256-%EC%95%94%ED%98%B8%ED%99%94-%EC%95%8C%EA%B3%A0%EB%A6%AC%EC%A6%98%EC%97%90-%EB%8C%80%ED%95%B4


### < Jasypt 암호화 라이브러리 >
Base64로 인코딩한 값이 보안에 취약해 이를 보완하기위해 암호화를 별도 진행해야한다.
springBoot 환경 설정 파일의 민감정보를 암호화 하여 관리하며, 환경변수를 통해 복호화키를 설정하여 사용할 수 있도록 한다.

1. Jasypt(Java Simplified Encryption)
    - 자신의 프로젝트(Java 어플리케이션)에서 설정파일의 속성값들을 암호화, 복호화할 수 있는 라이브러리.
    - api 문서   
      http://www.jasypt.org/ > http://www.jasypt.org/encrypting-texts.html

2. ASYPT, Jasypt Spring Boot Starter 라이브러리 차이
    - maven repository에 비슷한 이름의 여러 라이브러리들이 존재해 비교해본다.
    - readme.md    
      https://github.com/ulisesbocchio/jasypt-spring-boot
   
    1. ASYPT (Java Simplified Encryption) 라이브러리
        - JASYPT는 Java 애플리케이션에서 암호화 및 복호화를 간단히 구현할 수 있도록 돕는 라이브러리.
        - 기본적으로 JASYPT는 다양한 암호화 알고리즘을 제공하며, 사용자가 별도의 복잡한 설정 없이 간단히 암호화를 구현할 수 있게 도와줍니다.

        - 단독 라이브러리
            - **Spring Framework와 관계없이** Java 애플리케이션에서 **독립적으로 사용 가능**합니다.
        - 간편한 사용
            - 복잡한 암호화/복호화 과정을 간소화하여, 코드에서 간단히 암호화 기능을 구현할 수 있습니다.
        - 다양한 암호화 방식 지원
            - AES, DES 등 다양한 대칭키 및 비대칭키 암호화 방식 지원.
        - 기본 설정 및 기능 제공
            - 기본적인 암호화 및 복호화 기능을 간단히 구현할 수 있도록 도와줍니다.

    2. Jasypt Spring Boot Starter 라이브러리
        - Jasypt Spring Boot Starter는 Spring Boot 애플리케이션에서 JASYPT 라이브러리를 더 쉽게 사용할 수 있도록 도와주는 Spring Boot 특화 라이브러리입니다. Spring Boot와의 통합을 통해, 프로퍼티 파일에 저장된 암호화된 값을 쉽게 처리하고, Spring Boot 애플리케이션 내에서 암호화된 데이터의 관리가 편리해집니다.

        - Spring Boot 통합
            - **Spring Boot 환경에 최적화**된 라이브러리로, **Spring Boot의 설정 파일 (application.properties, application.yml 등)에서 암호화된 값을 처리**할 수 있도록 지원합니다.
        - 자동화된 설정
            - Spring Boot의 자동 설정 기능을 활용하여 암호화 및 복호화 설정을 쉽게 적용할 수 있습니다.
        - 암호화된 프로퍼티 처리
            - 데이터베이스 비밀번호나 API 키 등 민감한 정보를 암호화하여 프로퍼티 파일에 저장할 때 유용합니다. Spring Boot 환경에서 암호화된 값을 자동으로 복호화해 사용할 수 있습니다.
        - 스프링 보안 연동
            - Jasypt Spring Boot Starter는 Spring Security와도 잘 통합되어, 보안이 중요한 애플리케이션에서 유용하게 사용됩니다.

- Encryptor Configuration
    - 암호화 알고리즘 (Algorithm)
        - 3.0 버전 부터 기본 알고리즘이 PBEWithMD5AndDES → PBEWithHMACSHA512AndAES_256로 변경 되었다.
    - 비공개 키, 인코딩(encoding) 타입, 솔트(salt) 값 생성기 등을 설정

- 암호화 관련 설정
  ~~~
    @Configuration
    @EnableEncryptableProperties //  Spring 환경 전체에서 암호화 가능한 속성을 활성화
    public class JasyptConfigAES {
    
        // jasyptEncryptorAES로 Bean을 등록 -> application.properties의 jasypt bean으로 등록 시 사용.
        @Bean("jasyptEncryptorAES")
        public StringEncryptor stringEncryptor() {
            // PBE (Password Based Encryption)
            PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
            SimpleStringPBEConfig config = new SimpleStringPBEConfig();
    
            config.setPassword("my-e-commerce-project-jasypt-secret-key"); // 암호화키 (필수)
            config.setAlgorithm("PBEWithHMACSHA512AndAES_256"); // 알고리즘
            config.setKeyObtentionIterations("1000"); // 반복할 해싱 횟수
            config.setPoolSize("1"); // 인스턴스 pool
            config.setProviderName("SunJCE");
            config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); // salt 생성 클래스
            config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
            config.setStringOutputType("base64"); // 인코딩 방식
    
            encryptor.setConfig(config);
    
            return encryptor;
        }
    }
  ~~~

- 암호화
  - 아래의 메서드를 호출해 암호화 한 값을 application.properties에 등록. 
  ~~~
    private String encryptString(String plainText) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("암호화시 사용할 secret key");
        encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
        encryptor.setIvGenerator(new RandomIvGenerator());
 
        return encryptor.encrypt(plainText);
    }
  ~~~

- 복호화
  - application.properties에서 ENC()를 통해 작성된 값은 변수호출 시 복호화된 평문으로 반환.
  - application.properties에서 복호화를 위해서는 application.properties 에 jasypt.encryptor.bean=jasyptEncryptorAES 변수를 설정해 빈등록 필요.
  ~~~
    spring.datasource.url=ENC(암호화된값)
  ~~~

- 참고 블로그   
  https://goddaehee.tistory.com/321


---
### < 날짜 데이터 역직렬화 에러해결 >
- 로그인 시 아래의 오류발생.    
  로그인 시 JWT(Json Web Token) 토큰을 생성하는 과정에서 LocalDateTime 타입을 직렬화할 때 문제 발생.    
  **JWT 토큰을 생성하려면 일반적으로 사용자 정보를 Claims 객체에 넣어 JSON 형태로 직렬화**하는데, 
  이 과정에서 LocalDateTime 같은 Java 8의 날짜/시간 타입을 처리할 수 없어서 오류가 발생한 것.

- Serialization(직렬화)
  - 객체를 바이트 스트림이나 다른 저장 가능한 형식(JSON, XML)으로 변환하는 과정
  - 직렬화의 목적은 객체를 메모리 내에서 외부로 내보내거나(저장) 외부에서 불러오는 것(복원).
  
- 발생에러   
  (근데 데이터 반환을 회원가입때 하고 로그인 때는 토큰만 반환하는데 왜 로그인 때 에러가 났지..?)
  - com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.LocalDateTime` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (through reference chain: io.jsonwebtoken.impl.DefaultClaims["roles"]->org.hibernate.collection.spi.PersistentBag[0]->com.shop.reservation.entity.MemberRole["createDate"])

- 발생원인
  - LocalDateTime 데이터타입은 바이트화(직렬화) 할 때 어떤 규칙으로 진행할 것인지 
    Serialization에 대한 정의가 되어있지않아 발생한 에러. 
    LocalDateTime에 대해 어떤 데이터 타입으로 직렬화, 역직렬화 할 것인지 따로 지정이 필요.

- 해결방법
  - 날짜타입의 직렬화 역직렬화를 위해 Entity에 아래의 어노테이션 추가   
    @JsonSerialize(using = LocalDateTimeSerializer.class) // 직렬화   
    @JsonDeserialize(using = LocalDateTimeDeserializer.class) // 역직렬화   
    @JsonFormat(pattern = "yyyy-MM-dd kk-mm:ss") // 원하는 형태의 LocalDateTime 설정
  - Dto가 아닌 Entity에서 해당 문제를 해결하는 이유   
    - Entity에 설정을 추가하는 이유는 LocalDateTime이 엔티티 필드일 경우, 
      해당 엔티티를 직접 JSON으로 변환하거나 직렬화/역직렬화할 때 발생할 수 있는 문제를 방지하기 위함이고 
      이로인해 일관성 유지 가능.   
      DTO는 데이터를 전달하는 역할이므로 LocalDateTime과 같은 날짜/시간 타입을 Entity에서 직렬화/역직렬화 하도록 설정하는 것이 더 일관적이고 효율적.

- 다시 생각해보기
  >  다시 생각해보니 DTO에서 직렬화, 역직렬화 변환 타입을 지정해주는 것은 DTO 역할의 범위를 넘었다고 생각하지만
     그렇다고 데이터베이스와 매핑되는 Entity가 가져야 할 역할도 아니라는 생각이 드네요.    
     이렇게 해당 역할을 벗어나게 하는 것보다 새로운 객체를 만들어 역할을 분리하는 것이 좋아보입니다.    
     (토큰 생성에 대한 객체를 따로 두고 해당 객체에서 직렬화, 역직렬화를 수행)

- 참고 블로그   
  https://justdo1tme.tistory.com/entry/Spring-Jackson-%EC%9D%B4%ED%95%B4-%EB%B0%8F-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0


---
### < JWT 토큰인증필터 추가 시 순환참조 오류발생 >
1. 발생오류
   - Error starting ApplicationContext. To display the condition evaluation report re-run your application with 'debug' enabled.
     2025-02-01T21:44:22.568+09:00 ERROR 22252 --- [MyECommerce] [           main] o.s.b.d.LoggingFailureAnalysisReporter   :
    ***************************
    APPLICATION FAILED TO START
    *************************** 
    Description:
    The dependencies of some of the beans in the application context form a cycle:
    ┌─────┐
    |  jwtAuthenticationFilter defined in file [C:\workspace\MyECommerce\build\classes\java\main\com\myecommerce\MyECommerce\security\filter\JwtAuthenticationFilter.class]
    ↑     ↓
    |  jwtAuthenticationProvider defined in file [C:\workspace\MyECommerce\build\classes\java\main\com\myecommerce\MyECommerce\config\JwtAuthenticationProvider.class]
    ↑     ↓
    |  memberService defined in file [C:\workspace\MyECommerce\build\classes\java\main\com\myecommerce\MyECommerce\service\member\MemberService.class]
    ↑     ↓
    |  securityConfig defined in file [C:\workspace\MyECommerce\build\classes\java\main\com\myecommerce\MyECommerce\config\SecurityConfig.class]
    └─────┘
    Action:
    Relying upon circular references is discouraged and they are prohibited by default. Update your application to remove the dependency cycle between beans. As a last resort, it may be possible to break the cycle automatically by setting spring.main.allow-circular-references to true.
    Disconnected from the target VM, address: '127.0.0.1:60863', transport: 'socket'
    Process finished with exit code 1
    
2. 발생원인
   - **JwtAuthenticationFilter**가 토큰 유효성 검사 및 JWT 토큰 정보를 스프링 시큐리티 인증정보로 변환하기 위해 JwtAuthenticationProvider를 의존하고 있습니다.
   - **JwtAuthenticationProvider**는 JWT 토큰 정보를 스프링 시큐리티 인증정보로 변환을 위한 사용자조회(loadUserByUsername())를 위해 MemberService를 의존하고 있습니다.
   - **MemberService**는 비밀번호 암호화를 위해 SecurityConfig에서 빈으로 정의해 등록한 passwordEncoder()를 사용하고 있기에 간접적으로 SecurityConfig를 의존하고 있습니다.
     > MemberService의 의존관계를 확인해보면 아래의 4가지에 의존적이다.
     > 
     > private final PasswordEncoder passwordEncoder;
     > private final MemberMapper memberMapper;
     > private final MemberRepository memberRepository;
     > private final MemberAuthorityRepository memberAuthorityRepository;
     > 
     > 확인해보면 SecurityConfig를 직접적으로 참조하거나 주입하는 부분은 없다.
     > 하지만 MemberService와 SecurityConfig 간의 간접적인 관계는 있을 수 있다.
     > 실제로 PasswordEncoder를 SecurityConfig에서 정의하고 빈등록하였기에 간접적으로 참조하는 것으로 보여진다.
   - **SecurityConfig**는 JWT토큰인증을 위해 .addFilterBefore() 메서드를 통해 jwtAuthenticationFilter 필터를 추가하기 위해 다시 JwtAuthenticationFilter를 의존하고 있습니다.
   - 이러한 의존성 구조는 순환 참조로, Spring이 bean을 초기화할 수 없게 만듭니다.

3. 해결방법
   - 위의 순환관계를 끊어내기 위해 의존관게 재구성이 필요.
   - 기존 MemberService를 UserDetailsService 인터페이스를 구현하도록 했다.
   - 하지만 위의 순환참조가 발생하여 UserDetailsService 인터페이스의 구현체가 PasswordEncoder를 의존하지 않도록 분리가 필요하다.
   - 따라서 MemberService는 UserDetailsService를 구현하지 않고 SignInAuthenticationService를 신규 생성해 UserDetailsService를 구현한다.
   - 그러면 JwtAuthenticationProvider에서 loadUserByUsername() 호출 시 MemberService가 아닌 SignInAuthenticationService에 의존하므로 순환관계가 끊긴다.


---
### < 계층별 별도 DTO 생성 >
>   선택한 controller->service DTO 사용 방법   
>   Service 레이어에서 도메인으로 변환한 뒤 Repository로 전달   
    Service에서 DTO를 도메인(Entity)으로 변경하고, Repository는 도메인을 받고, 도메인을 응답한다.
    이렇게 되면 Repository는 DTO <-> Entity 변환 작업은 하지 않고 DB 관련 작업만 수행하게된다.
>
>   Service가 전체적인 비즈니스 플로우를 관리하고,
    Repository는 온전히 데이터베이스와 소통하는 역할만 수행.
>
>   다만, **Service가 Controller에 종속적.**
    Controller가 받아오는 데이터에 따라 Service가 받는 데이터 형태가 결정되기 때문이다.
    따라서 **Service가 원하는 방식으로 정보를 받지 못할 수 있다.**
> 
>   만약 하나의 서비스가 여러 컨트롤러에서 호출된다면, 
    서비스가 Entity를 받아야 컨트롤러별 API 형태와 관계 없이 동작할 것이다.
>
>   현재 내 프로젝트에서 하나의 서비스는 하나의 컨트롤러에서만 사용된다.
    **컨트롤러와 서비스의 기능이 1:1로 매칭**되므로 원하는 형태의 데이터를 바로 받아오는 것에 대해 문제가 없다고 판단된다.
    따라서 **controller -> service로 데이터를 전달하는 과정에 추가적인 DTO 변환 작업 없이 서비스까지 전달**하기로 한다.

- 참고블로그   
  https://shyun00.tistory.com/214


---
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
유효한 로그인 토큰 정보와 장바구니 내용을 분리해 저장하려는 경우, Redis의 네임스페이스(혹은 데이터베이스)를 활용하는 것이 좋은 방법입니다. 여러 가지 이유로 이를 분리하는 것이 유리합니다:

- 논리적 분리
   로그인 토큰과 장바구니 내용은 서로 다른 도메인과 관련된 데이터이기 때문에, 각기 다른 데이터베이스(네임스페이스)로 분리하는 것이 관리에 용이합니다. 예를 들어, 로그인 토큰은 인증과 관련된 데이터이고, 장바구니 내용은 사용자 행동과 관련된 데이터입니다. 이 둘을 별개의 데이터베이스에 저장하면 데이터의 역할과 목적에 맞는 관리가 가능합니다.
- 성능 최적화
   Redis는 데이터베이스(네임스페이스) 간에 완전히 독립적이지 않지만, 데이터베이스를 나누어 놓으면 한 쪽에서 불필요한 데이터 조회나 수정이 발생해도 다른 데이터베이스에 영향을 미치지 않습니다.
   예를 들어, 로그인 토큰이 많이 만료되거나 갱신되는 작업이 있을 때, 그와 관련된 네임스페이스에만 영향을 미치고 장바구니 데이터는 영향을 받지 않습니다. 이를 통해 성능적인 장점도 있을 수 있습니다. 
- 데이터 삭제 및 만료 정책 관리
   로그인 토큰은 보통 일정 시간이 지나면 만료되거나 삭제됩니다. 이를 하나의 네임스페이스에서 처리하고, 장바구니 데이터는 사용자 세션이 종료될 때까지 유지될 수 있습니다.
   각 데이터베이스에서 개별적인 TTL(Time To Live) 설정을 통해, 로그인 토큰은 짧은 만료 시간을 설정하고 장바구니는 좀 더 긴 만료 시간을 설정하는 식으로 유연한 만료 정책을 적용할 수 있습니다.
   예시
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


---
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


---
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
     - value는 객체이므로, Jackson2JsonRedisSerializer를 사용하여 객체를 JSON 형태로 직렬화합니다. 이 설정은 객체를 JSON으로 변환하여 Redis에 저장하고, 다시 Redis에서 가져올 때 JSON을 객체로 역직렬화합니다.

