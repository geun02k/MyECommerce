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


