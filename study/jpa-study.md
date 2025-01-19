# MY E-COMMERCE PROJECT JPA STUDY
2025 간단한 이커머스 서비스를 만들면서 접한 JPA 공부내용.


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

