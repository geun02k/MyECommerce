# MY E-COMMERCE PROJECT TROUBLE SHOOTING
2025 간단한 이커머스 서비스를 만들기위한 고민


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
