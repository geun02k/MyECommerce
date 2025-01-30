# MY E-COMMERCE PROJECT JPA STUDY
2025 간단한 이커머스 서비스를 만들면서 접한 JPA 공부내용.


---
###  <공통으로 사용하는 추적정보를 위한 BaseEntity 생성 >
1. @MappedSuperclass
    > - 객체 입장에서 공통 매핑 정보 필요 시 사용.
    공통 매핑 정보 필요 시, 부모 클래스에 선언하고 속성만 상속받아 사용하고 싶을 때 사용.
    > - 해당 어노테이션이 선언되어 있는 클래스는 엔티티가 아니다.   
    따라서 테이블과 별도로 매핑되지 않는다.
    > - 직접 생성해 사용할 일이 없으므로 추상 클래스로 만드는 것이 권장된다.
    > - JPA에서 @Entity 클래스는 @Entity 클래스 또는 @MappedSuperclass로 지정한 클래스만 상속가능.   
    > - ex) 생성자, 생성시간, 수정자, 수정시간 등
    > - 참고 블로그 : https://ict-nroo.tistory.com/129

2. @EntityListeners(AuditingEntityListener.class)   
   > - 해당 클래스에 Auditing 기능을 포함.
   > - JPA Auditing 활성화   
   >   : @SpringBootApplication 어노테이션이 있는 Application.java 파일에 @EnableJpaAuditing 어노테이션을 추가해
      JPA Auditing 기능을 활성화 해야 사용가능.
   > - JPA의 Auditing       
   >   : JPA에서는 Audit 기능을 제공한다.   
      Audit은 spring data JPA에서 시간에 대해 자동으로 값을 넣어주는 기능이다.   
      도메인을 영속성 컨텍스트에 저장하거나 조회를 수행한 후 update 하는 경우 매번 시간 데이터를 입력해주어야하는데
      이 때 자동으로 시간을 매핑하여 데이터베이스의 테이블을 넣어주게 된다.
   > - @CreatedDate    
   >   : Entity가 생성되어 저장될 때 시간 자동 저장.   
   > - @LastModifiedDate   
   >   : 조회한 Entity의 값을 변경할 때 시간 자동 저장.
   > - 참고 블로그 : https://webcoding-start.tistory.com/53


---
### < 회원권한테이블 추가 >
- 유저와 권한은 다대다 관계.
  한 유저가 여러 권한을 가질 수 있고, 한 권한도 여러 유저가 사용가능. 
  사용자와 권한은 many to many 관계를 가진다고 볼 수 있지만 DB에서 many to many 관계를 직접적으로 가질수는 없고 반드시 중간에 mapping 테이블을 가져하고 사용자와 mapping 테이블은 one to many 관계를 가져야 하고 역할과 mapping 테이블도 one to many 관계를 가지도록 설계해야 한다.

- 회원가입 권한추가
    - 참고 블로그   
      https://xooxpeak.tistory.com/entry/%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85-%EA%B6%8C%ED%95%9CRole-%EC%B6%94%EA%B0%80

- 회원과 권한 일대다 관계
    - 참고 블로그   
      https://ttl-blog.tistory.com/126


--- 
### < 두 테이블의 연관관계 설정 후 FK값이 null로 저장되는 문제 >
- 주인 Entity
  - @ManyToOne
  - @JoinColumn(name="memberId")
    - 테이블 매핑 시 foreign key 지정 / 해당 외래 키를 관리하는 주인 Entity에 설정
  - @JsonBackReference
    - 양방향 연관관계에서 발생할 수 있는 무한 순환 참조 문제를 방지하기 위해 사용.
    - 주로 직렬화(Serialization, 객체를 JSON으로 변환) 시, 서로 참조하는 두 객체가 무한히 참조하면서 순환하는 것을 막고자 할 때 유용.
    - 직렬화 시 MemberAuthority 객체는 포함되지 않음. (없으면 무한호출됨)
- 비주인 Entity
  - @OneToMany(cascade = CascadeType.ALL, mappedBy = "member") 
    - 테이블 연관관계 1(회원):N(권한) 설정.   
      member_authority(사용자권한테이블) 테이블과 join해서 권한정보 가져옴.
    - mappedBy : 비주인 설정
    - cascade = CascadeType.ALL : Member가 저장될 때 관련된 MemberAuthority도 자동으로 저장.
    - orphanRemoval = true :  Member에서 MemberRole을 제거할 때 MemberRole이 삭제.
       테이블 연관관계 1:N 설정 / mappedBy : 비주인
  - @JsonManagedReference
    - 직렬화 시 MemberAuthority 리스트는 포함되지만, Member 객체는 포함되지 않음.

1. 연관관계의 주인을 N으로 둔다는 것
   - 1:N 관게에서 N(다수)을 연관관게의 주인으로 설정하는 것. (Member 비주인, MemberAuthority 주인)
   - @OneToMany에서 mappedBy 옵션으로 비주인임을 설정.
   - @ManyToOne 어노테이션을 작성한 Entity에서 @JoinColumn에서 name 옵션에 FK를 작성해 외래키를 관리하는 주인으로 설정.
   - 주인 클래스인 MemberAuthority에서 비주인인 Member를 변경하거나 삭제할 때만 외래키 수정.
   - Member가 저장될 때 불필요한 update 쿼리가 발생 x.

2. 기존 회원, 회원권한 저장 시 문제 
   - 문제 발생 원인
     - controller에서 우선으로 MemberAuthority(권한)을 생성해 Member(회원) 객체에 우선 데이터를 넣은 후 회원 저장을 수행.
       따라서 MemberAuthority 클래스의 member 필드값이 memberRepository.save() 의 반환값 객체가 아니라
       요청 시 전달받은 member 객체라 id 필드 값이 비어있기 때문에 잘못된 객체 전달로 인해 FK인 memberId값이 null로 저장된 것으로 판단.
   - 문제 해결
     >  memberRepository.save()의 반환값인 Member 객체를 MemberAuthority 객체의 member 필드에 저장해 
        memberAuthorityRepository.save()를 수행해 FK값이 null로 저장되는 문제를 해결.

3. 주인 Entity에 FK 필드 선언 제거
   - 연관관계 설정 시 @JoinColumn(name="memberId") 어노테이션을 통해 join column을 설정해주면 FK 필드가 자동 생성됨.   
     띠라서 memberId라는 별도의 필드가 필요없음.   
   - 기존 작성했던 FK 필드 제거.


---
### < 무한 순환 방지 >
- 아래 두 개의 어노테이션은 한 세트로 적용해야함.
- 한쪽에서는 직렬화를 포함시키고, 다른 한쪽에서는 제외시켜 무한 순환을 방지.
- 직렬화(Serialization)
  - 객체를 바이트 스트림이나 다른 저장 가능한 형식(JSON, XML)으로 변환하는 과정
  - 직렬화의 목적은 객체를 메모리 내에서 외부로 내보내거나(저장) 외부에서 불러오는 것(복원).
- @JsonManagedReference
  - "출발점" 객체에 사용되며, 직렬화 시 이 객체는 포함됨.
  - 이 객체의 참조는 직렬화 과정에서 포함되고, 해당 객체가 참조하는 다른 객체들은 @JsonBackReference로 지정된 객체에 의해 직렬화에서 제외됨.
- @JsonBackReference
  - "대상" 객체에 사용되며, 직렬화 시 이 객체의 참조는 미포함. 
  - 이 객체를 직렬화할 때 해당 객체가 참조하는 다른 객체는 직렬화에서 제외됨.

