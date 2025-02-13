# MY E-COMMERCE PROJECT JPA STUDY
2025 간단한 이커머스 서비스를 만들면서 접한 JPA 공부내용.


---
### < JPA >
- Java Persistence API
- 자바에서 **객체 관계 매핑(ORM)**을 처리하기 위한 표준 API.
- 객체지향 언어인 자바와 관계형 데이터베이스 간의 데이터를 변환하고, 관리하는 데 도움을 주는 기술.
- 데이터베이스와 자바 객체 간의 매핑을 자동화하여, 
  개발자가 SQL 쿼리를 직접 작성하지 않고도 객체를 데이터베이스에 저장하고 조회가능.
  객체-관계 매핑(ORM) JPA는 자바 클래스(엔티티 클래스)와 관계형 데이터베이스의 테이블 간의 매핑을 설정합니다. 이 매핑을 통해 데이터베이스의 레코드가 자바 객체로, 자바 객체가 데이터베이스의 레코드로 변환됩니다.

1. JPA 핵심개념
   1. Entity(엔티티) 
      - 데이터베이스의 테이블에 대응되는 자바 클래스. 
      - 엔티티 클래스는 @Entity 어노테이션을 사용하여 정의. 
      - 각 엔티티는 하나 이상의 필드(속성)를 갖고, 이 속성은 데이터베이스의 컬럼에 대응.
   2. **EntityManager** 
      - JPA에서 엔티티를 관리하는 주요 인터페이스. 
      - 엔티티를 영속화(persist)하고 조회(find), 삭제(remove)하는 등의 작업 수행하며 이를 통해 데이터베이스와의 상호작용 가능.
   3. **영속성 컨텍스트(Persistence Context)** 
      - EntityManager가 관리하는 엔티티 객체들의 상태를 보관하는 공간. 
      - 엔티티가 영속성 컨텍스트에 들어가면 JPA가 이를 데이터베이스와 동기화하여 관리합.
   4. **JPQL(Java Persistence Query Language)** 
      - JPA에서 사용하는 쿼리 언어. 
      - SQL과 비슷하지만 데이터베이스의 테이블이 아니라 자바 객체를 대상으로 쿼리를 작성.    
        즉, 엔티티 객체를 대상으로 SELECT, INSERT, UPDATE, DELETE 등의 작업을 수행가능.

2. JPA의 주요 기능
   1. 객체와 관계형 데이터베이스 간의 매핑 
      - JPA는 자바 클래스와 데이터베이스 테이블 간의 매핑을 자동으로 처리.    
        예를 들어, 자바 클래스의 필드는 데이터베이스 테이블의 컬럼에 매핑됨.
   2. 트랜잭션 관리 
      - JPA는 트랜잭션을 관리.
      - 엔티티의 상태를 영속화하고 변경 사항을 데이터베이스에 반영할 때 트랜잭션 단위로 처리.
   3. 캐시 기능 
      - JPA는 영속성 컨텍스트 내에서 엔티티를 캐싱하여, 
        동일한 엔티티를 여러 번 조회할 때 데이터베이스에 대한 불필요한 쿼리를 줄임.
   4. 쿼리 기능 
      - JPA는 JPQL을 사용하여 객체 지향적으로 데이터를 조회하고 수정가능. 
      - Criteria API를 사용하여 타입 안전한 쿼리 작성이 가능.
   5. 지연 로딩 및 즉시 로딩 
      - JPA는 연관된 엔티티의 로딩 전략을 설정가능. 
      - 기본적으로 지연 로딩(Lazy Loading) 방식이 사용되며, 필요할 때만 연관된 데이터를 로딩. 
      - 즉시 로딩(Eager Loading)은 연관된 데이터를 즉시 로딩.

3. JPA와 Hibernate
   - JPA는 표준 API이고, Hibernate는 JPA의 구현체. 
   - Hibernate는 JPA 규격을 구현하는 라이브러리.
   - JPA의 모든 기능을 지원.
   - Hibernate 외에도 EclipseLink, OpenJPA와 같은 다른 구현체들이 있지만, Hibernate가 가장 널리 사용.

4. JPA 사용 시의 장점
   1. 생산성 향상 
      - JPA는 SQL을 직접 작성하지 않고도 객체와 관계형 데이터베이스 간의 데이터를 쉽게 처리할 수 있어 개발 생산성을 높임.
   2. 유지보수 용이 
      - 엔티티 클래스를 사용하여 객체 지향적인 방식으로 데이터를 처리하므로, 비즈니스 로직과 데이터베이스 로직을 분리할 수 있어 유지보수가 용이.
   3. DB 벤더 독립성 
      - JPA는 특정 데이터베이스에 의존하지 않기 때문에, 데이터베이스가 변경되어도 코드 수정 없이 쉽게 대체가능.


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


---
### < 연관관계에 있는 두 Entity의 데이터 조회 >
- 에러발생 소스코드
    ~~~
    @Repository
    public interface ProductionOptionRepository extends JpaRepository<ProductionOption, Long> {
        // 판매자 상품의 동일 상품옵션코드 조회
        @Query(" SELECT PRD.ID AS PRODUCTION_ID, " +
                "       PRD.SELLER," +
                "       PRD.CODE AS PRODUCTION_CODE, " +
                "       OPTION.ID AS OPTION_ID," +
                "       OPTION.OPTION_CODE" +
                " FROM RPODUCTION PRD" +
                " LEFT JOIN PRODUCTION_OPTION OPTION" +
                " ON PRD.ID = OPTION.PRODUCTION_ID" +
                " WHERE PRD.CODE = ?1" +
                " AND OPTION.OPTION_CODE IN (?2)")
        Optional<List<ProductionOption>> findByProductionIdAndOptionCodeIn(String productionCode, String optionCodes);
    }
    ~~~
  
- 해결코드
    ~~~
    @Repository
    public interface ProductionOptionRepository extends JpaRepository<ProductionOption, Long> {
        @Query(" SELECT PRD " +
                " FROM Production PRD" +
                " LEFT JOIN FETCH PRD.options OPTION" +
                " WHERE PRD.code = ?1" +
                " AND OPTION.optionCode IN (?2)")
        List<Production> findByProductionCodeAndOptionCodeIn(String productionCode, List<String> optionCodes);
    }
    ~~~

1. 발생에러
    - Caused by: org.hibernate.query.sqm.UnknownEntityException:
      Could not resolve root entity 'RPODUCTION'

2. 발생원인
   - Hibernate가 RPODUCTION이라는 엔티티를 찾을 수 없다는 오류를 발생시키고 있습니다. 이는 다음과 같은 이유로 발생할 수 있습니다.
   - 실제로 존재하는 엔티티 클래스와 RPODUCTION 테이블의 이름이 다른 경우 발생가능.

3. 해결방법
   1. 엔티티 클래스와 JPQL에서 사용하는 이름 불일치하므로 이를 수정해줘야한다.
      - JPQL 쿼리에서 사용하는 엔티티 이름은 반드시 클래스명이어야 하며, 대소문자를 구분합니다. 
        예를 들어, 실제 엔티티 클래스명이 Production이라면 RPODUCTION이 아니라 Production을 사용해야 합니다.
      - 따라서 RPODUCTION을 Production으로 변경해야 합니다. 
        만약 엔티티 클래스명이 Production이라면, 쿼리에서 엔티티 이름도 대소문자를 맞춰야 합니다.
   
   2. JPA List 타입 반환 시 Optional 사용x
      - Optional은 보통 **단일 값을 반환 시 사용**되며, 값이 없을 경우에 대비한 안전한 처리 방식.
      - 하나의 객체를 조회할 때 사용되며, 값이 없을 경우 Optional.empty()로 반환.
      - 리스트는 기본적으로 비어 있을 수 있습니다. 만약 리스트가 비어 있다면, 그것은 단순히 아무 항목도 없는 상태일 뿐, 값이 "없다"라는 의미와는 다릅니다. 
      - JPA에서는 컬렉션이 비어 있을 경우 자동으로 빈 리스트를 반환하기 때문에, Optional을 사용할 필요가 없습니다.
      - 사용한다고해서 오류가 발생하지는 않지만 List의 경우는 isEmpty()로 검증 시 값이 없을 때 true로 작동하지 않음.
      
   3. Production 엔티티 전체를 반환하려면, 
      SELECT 구문에서 Production과 ProductionOption을 엔티티로 반환하도록 해야함.
      - 발생에러
        - Entity 클래스명, 필드명을 사용해도 아래의 오류 발생.
        > org.springframework.core.convert.ConversionFailedException: 
          Failed to convert from type [java.lang.Object[]] to type [com.myecommerce.MyECommerce.entity.production.Production] for value [{...}]
     - LEFT JOIN FETCH를 사용하여 Production과 ProductionOption을 함께 로딩.
     - Production 엔티티를 직접 반환하므로, Production 객체를 그대로 반환할 수 있습니다.
     - LEFT JOIN FETCH를 사용하면 productionOptions 컬렉션이 지연 로딩 대신 즉시 로딩됨.
   
   4. Entity의 연관관계를 맺어줬는데도 JOIN해 조회가 불가한 문제
      - 발생에러
        > org.springframework.dao.InvalidDataAccessApiUsageException:
          org.hibernate.query.SemanticException:
          Entity join did not specify a join condition
          [SqmEntityJoin(com.myecommerce.MyECommerce.entity.production.ProductionOption(OPTION))]
          (specify a join condition with 'on' or use 'cross join')
    
      - 발생원인
        - 본래 위의 오류는 Hibernate에서 엔티티 간의 JOIN을 사용할 때, 조인 조건을 명시하지 않았다는 의미입니다. 
          즉, LEFT JOIN FETCH를 사용했으나, Production과 ProductionOption 사이의 조인 조건이 명확하지 않기 때문에 발생합니다. 
          따라서 JOIN 시 ON 조건을 명시해 해결할 수 있습니다. 
        - 하지만 JPA에서 두 테이블의 연관관계를 설정해놓았다면, 일반적으로 ON 절을 명시적으로 사용할 필요는 없습니다.
          연관 관계가 이미 엔티티 간에 설정되어 있다면, JPA는 이를 기반으로 내부적으로 JOIN을 처리합니다. 
          ON 절은 주로 연관 관계가 없거나, 더 복잡한 조건을 추가할 때 사용됩니다.
          LEFT JOIN FETCH와 같은 fetch join을 사용할 때는 연관 관계가 이미 설정되어 있으면 ON 절을 명시할 필요는 없습니다.
        - 이번 경우에는 ON절을 작성하면 쿼리가 정상 동작하고 ON절을 작성하지 않으면 쿼리에서 오류를 발생시킵니다.
        - 주된 문제는 **LEFT JOIN FETCH 사용 시 연관 관계를 정확히 지정하지 않았기 때문**입니다.
          즉, ProductionOption에 Production에 대한 연관 관계가 설정되어 있어야 합니다.
          만약 설정이 없다면 조인에 문제가 발생할 수 있습니다.
      
      - 해결방법
        - Entity에서 연관 관계 자체는 잘 설정되어 있지만, 쿼리의 **LEFT JOIN FETCH**에서 ProductionOption과 Production을 정확히 어떻게 조인할지를 명시적으로 지정해야 합니다.   
          즉, **LEFT JOIN FETCH가 제대로 작동하려면 ProductionOption에서 Production을 참조하고 있어야 합니다.**
        - LEFT JOIN FETCH에서 ProductionOption을 Production과 연결할 때, ProductionOption이 Production을 참조하는 필드 production을 명확하게 사용해야 합니다.
        - 이렇게 PRD.ptions와 같이 LEFT JOIN FETCH에서 PRD의 옵션들을 명시적으로 참조하는 방식으로 변경 시 해결 가능했습니다.
          Production 엔티티에서 options라는 필드가 ProductionOption 리스트를 참조하고 있으므로, prd.options로 ProductionOption과의 연관 관계를 가져와야 합니다.   
          Production과 ProductionOption을 조인하는 부분으로, prd.options를 사용하여 두 테이블을 연결합니다.
          해당 방식이 좀 더 명확하고, option이 Production에 종속적인 구조라면 잘 동작하게 됩니다.
        ~~~
        // - 연관관계를 명시적으로 작성한 것의 예시
        //   : Production Entity의 필드로 List<ProductionOption> 타입의 options 변수가 존재할 경우
        SELECT PRD
        FROM Production PRD
        LEFT JOIN FETCH PRD.options OPTION
        ~~~


---
### < FETCH >
- JPA에서 FETCH는 지연 로딩을 피하고 즉시 로딩을 하겠다는 의미. 
- 기본적으로 JPA는 연관된 엔티티를 지연 로딩 방식으로 처리하는데, 
  FETCH를 사용하면 연관된 엔티티들을 즉시 로딩하여 한 번의 쿼리로 결과를 가져오게 됨.

1. LEFT JOIN FETCH
   - LEFT JOIN FETCH는 SQL의 LEFT JOIN과 JPA의 FETCH 전략을 결합한 것.
   - SQL에서 LEFT JOIN을 사용하면서 동시에 JPA의 FETCH 전략을 적용하여, 연관된 엔티티를 즉시 로딩하는 방식. 
   - 주로 @OneToMany, @ManyToMany 관계에서 사용.
     - Post가 여러 Comment를 가지는 경우 Comment들을 한 번의 쿼리로 가져올 수 있음.
   - 연관된 엔티티가 너무 많으면 쿼리가 비효율적일 수 있으므로 필요에 따라 적절한 사용이 필요함.
   - 연관된 엔티티를 한 번의 쿼리로 가져오기에 N+1 문제를 해결가능.
   ~~~
   SELECT p 
   FROM Post p 
   LEFT JOIN FETCH p.comments // Post.java Entity에서 comments로 선언한 필드 = List<Comments> comments;
   ~~~

---
### < 연관관계의 Entity 조회방식 >
1. JPA 단순조회
    - 연관 관계가 잘 설정되어 있다면, **별도의 쿼리를 작성하지 않고 JPA가 자동으로 연관된 엔티티를 로드(조회)**.
2. JPQL 또는 @Query
    - **특정 조건에 맞는 데이터를 조회**하고자 할 때는 JPQL이나 @Query를 사용.
    - 연관관계가 있는 두 엔티티가 존재할 때 Production(one)과 관련된 특정 ProductionOption(many)만 조회하고 싶을 때는
      직접 **@Query**를 사용하여 JPQL을 작성할 수 있습니다.
3. @EntityGraph 또는 연관관게 설정 시 FetchType.EAGER 옵션 사용
    - 연관된 엔티티를 명시적으로 로드하려면 **@EntityGraph**나 **FetchType.EAGER**를 활용할 수 있습니다.
    - @EntityGraph를 사용한 성능 최적화
        - 특정 연관 관계를 명시적으로 로딩(조회)하려면 **@EntityGraph**를 사용하는 방법도 있습니다.
        - @EntityGraph는 연관된 엔티티를 명시적으로 로드할 수 있도록 해주며, 이를 통해 성능 최적화도 가능합니다.
        - @EntityGraph를 사용하면 연관관계에 있는 Production 엔티티의 필드인 List<ProductionOption> 타입의 options 변수를 명시적으로 로딩하도록 설정할 수 있습니다.
          이 방법은 EAGER 로딩의 성능 문제를 피하면서, 특정 필드만을 즉시 로딩할 수 있게 도와줍니다.
   ~~~
    // EntityGraph를 사용한 연관 관계 로딩
    
    @EntityGraph(attributePaths = {"options"})
    @Query("SELECT prd FROM Production prd WHERE prd.code = ?1")
    List<Production> findByProductionCodeWithOptions(String productionCode);
   ~~~
   

---
## < 연관관계에 있는 Entity에서 부모만 조회하기 >
- Production 엔티티가 productionOptions와 연관 관계가 있을 경우, 
  prd만 선택해도 연관된 ProductionOption 정보가 함께 로드될 수 있습니다.

- 부모 Entity 정보만 로드하는 방법
  - 이를 방지하려면, **JPQL 쿼리에서 productionOptions를 제외하거나 fetch를 제한**하는 방법이 필요합니다.

1. JPQL 쿼리에서 자식 Entity 제외
   - JOIN을 단순히 사용하여 부모만 선택하고, 연관된 자식을 결과에 포함시키지 않도록 할 수 있습니다.
   - 연관된 ProductionOption을 JOIN FETCH하지 않고
     단순히 **JOIN만 사용**하면 ProductionOption(자식) **데이터는 데이터베이스에서 가져와 로드하지만 결과에는 포함되지 않습니다.**
   ~~~
    SELECT prd
    FROM Production prd
    JOIN prd.productionOptions option
    WHERE prd.name LIKE CONCAT('%', ?1, '%')
    AND prd.saleStatus = 'ON_SALE'
    GROUP BY prd
    ORDER BY MIN(option.price)
   ~~~
   > 이 쿼리에서 JOIN은 Production과 ProductionOption을 연관시킵니다.     
   > 하지만 JOIN FETCH가 아니기 때문에, ProductionOption 데이터는 쿼리 결과에 포함되지 않으며
     ProductionOption의 필드 값은 반환되지 않고 prd만 반환됩니다.
   
2. fetch 제한하기
   - 자식이 @OneToMany와 같은 관계에서 FETCH 전략으로 로드되고 있을 때, 
     fetch 전략을 LAZY로 설정하여 즉시 로드되지 않도록 할 수 있습니다.
   - @OneToMany 관계에서 fetch = FetchType.LAZY로 설정
   ~~~
    @Entity
    public class Production {
        @OneToMany(mappedBy = "production", fetch = FetchType.LAZY)
        private Set<ProductionOption> productionOptions;
    }
   ~~~
   > FetchType.LAZY를 설정하면 ProductionOption은 지연 로딩되며, 쿼리 결과에 포함되지 않습니다.
   > LAZY 로딩은 실제로 연관된 엔티티가 필요할 때 로드되므로, ProductionOption 필드는 결과에 포함되지 않으며 필요할 때만 쿼리가 실행됩니다. 

3. Query에서 fetch 전략 제어
   - @Query에서는 JOIN FETCH를 사용하지 않으면 자식이 자동으로 로드되지 않습니다. 

LAZY 로딩을 설정하면 ProductionOption이 즉시 로드되지 않도록 할 수 있으며, 필요한 경우에만 로드되게 할 수 있습니다.
FETCH 전략을 LAZY로 설정하고, 쿼리에서 JOIN만 사용하면 ProductionOption이 쿼리 결과에 포함되지 않습니다.
따라서, @OneToMany나 @ManyToOne 관계에서 LAZY 로딩을 설정하는 것이 연관된 데이터를 지연 로딩하도록 하는 가장 좋은 방법입니다.


---
### < JPA 연관관계에서 지연로딩, 즉시로딩 >
- 엔티티를 언제 로드할지를 정의하는 방식.
- JPA는 기본적으로 @OneToMany, @ManyToOne 관계를 인식하고, 조회 시 연관된 엔티티도 함께 가져올 수 있도록 설정가능. 
- 이 때 FetchType.LAZY(지연 로딩)와 FetchType.EAGER(즉시 로딩) 설정에 따라 로딩 방식이 상이함.
- 로딩의 기본값
  - @OneToMany, @ManyToMany 관계 : 기본적으로 지연 로딩이 기본값으로 설정.
  - @OneToOne, @ManyToOne 관계 : EAGER 로딩이 기본값인 경우가 많음.

1. 지연로딩
   - 지연 로딩은 **연관된 엔티티를 필요할 때 로딩**하는 방식.
   - 부모 엔티티를 조회할 때 연관된 자식 엔티티는 초기에는 로드되지 않으며, 실제로 자식 엔티티에 접근할 때 로드됨.
   - 연관된 데이터를 실제로 필요할 때만 조회하므로 불필요한 데이터 조회를 피할 수 있음.
   - 성능
       - 지연 로딩은 필요할 때만 데이터를 로드하므로 성능에 유리하지만, 이를 잘못 사용하면 N+1 문제가 발생할 수 있습니다.
   - N+1 문제
     - 부모 엔티티 하나를 조회한 뒤 연관된 자식 엔티티를 조회하려고 할 때, 
       자식 엔티티들을 각각 개별적으로 추가 쿼리로 조회하게 되어 성능 문제가 발생가능.    
       이를 해결하려면 fetch join을 사용하거나, 
       @EntityGraph를 사용하여 연관된 엔티티를 한 번의 쿼리로 모두 로드할 수 있습니다.

2. 즉시로딩
   - 즉시 로딩은 연관된 엔티티를 즉시 로딩하는 방식.
   - 별도의 쿼리를 작성할 필요 없이 연관된 엔티티를 자동으로 로드 가능.
   - **부모 엔티티를 조회 시 연관된 자식 엔티티도 함께 즉시 로드.**     
     ex) EAGER 로딩을 사용하면, Production을 조회할 때 **ProductionOption**이 자동으로 로드(조회).
   - 성능
     - 즉시 로딩은 연관된 데이터를 항상 함께 로드하기 때문에 N+1 문제를 방지할 수 있지만,
       연관된 모든 불필요하게 많은 데이터를 한 번에 조회하게 될 수 있어 성능에 부담을 줄 수 있습니다.

~~~
@Entity
public class Order {
    @OneToMany(fetch = FetchType.EAGER)
    private List<OrderItem> items;
}
~~~    


---
### < 상품수정 옵션 저장 시 발생한 에러 ObjectOptimisticLockingFailureException >
1. 발생에러
   - org.springframework.orm.ObjectOptimisticLockingFailureException: 
     Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect): 
     [com.myecommerce.MyECommerce.entity.production.ProductionOption#5]

2. 발생원인
   - ObjectOptimisticLockingFailureException은 JPA에서의 낙관적 락(Optimistic Locking)을 사용하고 있을 때 발생할 수 있는 예외입니다.    
     이 예외는 두 개 이상의 트랜잭션이 동시에 같은 데이터에 접근하려고 할 때 발생합니다.    
     특히, 데이터가 동시에 변경될 가능성이 있을 때, 낙관적 락을 통해 이러한 충돌을 방지하는 방식입니다.    
   
   - updateOption과 insertOption에 반대의 데이터를 넣는 실수를 하면서 발생한 에러입니다. 
     update해야하는 데이터에는 id가 null값이고, insert해야하는 데이터에는 id값이 존재해 데이터를 변경하는 과정에서 에러가 발생한 것 같습니다.  
   > 트랜잭션 A가 Product 객체를 읽고 name을 변경합니다.
   > 트랜잭션 B도 같은 Product 객체를 읽고 name을 변경합니다.
   > 트랜잭션 B가 먼저 커밋됩니다.
   > 트랜잭션 A가 커밋하려고 할 때, version 값이 달라져서 ObjectOptimisticLockingFailureException이 발생합니다.

3. 해결방법
   - updateOption과 insertOption에 올바른 정보를 넣으면서 해결했습니다.


---
### < update문을 명시적으로 우선 수행헀는데 실제로는 후에 호출한 insert문을 먼저 수행하는 이유 >
> insert가 먼저 실행되는 이유는 영속성 컨텍스트가 수정된 엔티티를 처리하지 않고, 새로운 엔티티로 처리하고 있기 때문입니다.    
  이를 해결하려면, 트랜잭션 내에서 엔티티 상태를 관리하고 flush()나 saveOrUpdate() 메서드를 적절히 사용하여 DB에 반영되는 시점을 제어해야 합니다.

> 실제 문제 해결
> 현재의 경우에는 딱히 쿼리 순서가 영향을 미치지 않으므로 수정하지 않는다. 그냥 이유가 궁금해서 알아본다.

- 이 문제는 JPA의 **영속성 컨텍스트(Persistence Context)와 플러시(Flush) 동작과 관련**이 있습니다. 
- Hibernate가 insert문을 먼저 실행한 이유는 JPA에서 엔티티 상태 변화와 DB에 반영되는 시점 간의 차이 때문일 수 있습니다.

- JPA는 영속성 컨텍스트 내에서 엔티티의 상태를 추적합니다. 
  즉, 엔티티가 수정되거나 새로 추가될 때 이를 영속성 컨텍스트에 반영하고, 
  실제 DB에 변경 사항을 적용하는 시점은 플러시(flush) 동작을 통해 일어납니다.
- JPA의 기본 동작은 flush가 실행될 때 변경된 엔티티가 DB에 반영됩니다. 
  이 플러시가 트랜잭션 종료 시점에 일어날 수도 있고, 특정 시점에서 명시적으로 호출할 수도 있습니다.

- Insert가 먼저 실행된 이유
  - JPA에서 상태가 관리되지 않는 엔티티가 DB에 처음으로 추가되는 경우, 
    JPA는 이 **엔티티를 영속성 컨텍스트에 저장한 후, flush를 통해 insert 쿼리를 실행**합니다.
  - **insert가 발생한 엔티티는 새로 생성된 객체일 수 있으며, 
    수정된 엔티티를 동일 트랜잭션 내에서 업데이트하기 전에 insert가 먼저 실행될 수 있습니다.**
  - 문제상황
      ~~~
        @Transactional
        public ResponseProductionDto modifyProduction(RequestModifyProductionDto requestProductionDto,
                                                      Member member) {
            // 상품 조회
            Production production = productionRepository.findByIdAndSeller(
                    requestProductionDto.getId(), member.getId())
                    .orElseThrow(() -> new ProductionException(NO_EDIT_PERMISSION));
      
            // 상품옵션 수량 변경 (update)
            // 기존 데이터와 id가 일치하는 입력데이터 찾아 값 입력
            for (int i = 0; i < requestUpdateOptionList.size(); i++) {
                for(int j = 0; j < production.getOptions().size(); j++) {
                    if(production.getOptions().get(j).getId().equals(requestUpdateOptionList.get(i).getId())) {
                        production.getOptions().get(j).setQuantity(requestUpdateOptionList.get(i).getQuantity());
                        // 상품옵션목록 등록(수정) -> 명시적 저장
                        productionOptionRepository.save(optionList.get(j));
                        break;
                    }
                }
            }
    
            // 상품옵션 추가 등록 (insert) -> 명시적 저장
            saveProductionOption(requestInsertOptionList, production);
        }
      ~~~
          
- 엔티티 상태와 플러시 시점
  - 만약 수정하려는 ProductionOption이 영속성 컨텍스트에서 새로 추가된 엔티티로 처리되었거나 연관된 엔티티가 먼저 플러시된 경우,  
    Hibernate는 insert를 먼저 실행할 수 있습니다.
  - 예를 들어, productionOption 객체가 영속성 상태에서 새로 생성된 객체로 간주되면, 
    insert가 수정 이전에 먼저 실행될 수 있습니다.

- 엔티티 상태 확인
  - insert가 먼저 실행되는 이유가 엔티티의 상태가 새로 생성된 객체로 처리되었기 때문일 수 있습니다. 
  - 이를 해결하려면 엔티티 상태를 명시적으로 관리하고, insert가 아니라 update로 처리하도록 해야 합니다.
  - 예를 들어, JPA의 merge() 메서드를 사용할 때도 insert가 발생할 수 있기 때문에, 
    엔티티가 영속 상태로 존재하는지 확인 후 적절히 save() 혹은 update()로 처리해야 합니다.

- 쿼리 순서가 잘못된 경우
  - 만약 update 쿼리가 먼저 실행되어야 한다면,  
    flush()를 호출하거나 트랜잭션 범위 내에서 업데이트가 먼저 일어나도록 설정할 수 있습니다.

- 해결 방법
  - @Transactional을 사용한 트랜잭션 관리
    - 트랜잭션 범위 내에서 insert나 update 작업이 정상적으로 이루어지는지 확인합니다.    
      **JPA는 트랜잭션이 끝날 때까지 flush를 지연**시키므로, 명시적인 flush() 호출을 통해 해당 동작을 제어할 수 있습니다.
  - @Modifying 어노테이션과 flush 사용
    - 명시적으로 flush() 메서드를 호출하여 엔티티 상태를 DB에 반영할 시점을 제어할 수 있습니다.
    - 예를 들어, 엔티티의 상태를 업데이트한 후 flush()를 호출하여 업데이트가 먼저 실행되도록 할 수 있습니다.
  ~~~
  @Transactional
  public void modifyProductionOption(ProductionOption productionOption) {
      // 먼저 수정 작업 수행
      productionOptionRepository.save(productionOption);

        // flush()를 호출하여 즉시 DB에 반영
        entityManager.flush(); 
  }
  ~~~  


---
### < dirty 체크 >
- **Entity의 속성이 변경된 상태**
  - Entity가 변경되었음을 JPA가 인식하고, DB에 반영될 준비가 된 상태.
- ProductionOption 엔티티에서 quantity 값을 변경했다면, 해당 엔티티는 "dirty" 상태로 간주.
- JPA는 해당 엔티티가 변경되었음을 추적합니다.

1. JPA가 dirty check를 인식할 수 있는 이유
   - JPA는 영속성 컨텍스트라는 메커니즘을 사용하여 엔티티의 상태를 추적. 
   - 영속성 컨텍스트는 데이터베이스와의 연결을 관리하는 메모리상의 객체 상태를 의미. 
   - 이 컨텍스트 내에서 엔티티의 상태가 변경되면, JPA는 이를 추적하고,  
     flush() 또는 save()가 호출될 때 자동으로 DB에 반영합니다.

2. Entity의 상태
   - Entity가 처음 로드된 경우
     - 데이터베이스에서 엔티티를 가져오면 영속성 컨텍스트에 보관되고, 
       이 상태에서 **Entity는 변경되지 않은 상태**.
     
   - Entity가 수정된 경우
     - Entity의 필드값을 변경하면, JPA는 이 Entity가 "dirty" 상태로 바뀌었다고 인식. 
     - 필드값이 변경되었으므로 **수정된 상태는 flush()나 save()가 호출될 때 DB에 변경사항이 반영**됨.

   - Entity에 저장되지 않은 변경사항이 있는 경우
     - Entity가 "dirty" 상태로 **변경되었지만 save() 또는 flush()가 호출되지 않으면 
       변경사항은 DB에 반영되지 않음.**

3. dirty 상태 추적과 DB반영
   - JPA는 자동으로 변경된 엔티티를 감지하고 
     DB에 반영하기 위해 flush() 또는 save() 메서드를 호출할 때 이 변경사항 적용. 
   - 이를 통해 코드에서 명시적으로 Entity save()를 호출하지 않더라도, 
     영속성 컨텍스트 내에서 변경된 상태를 추적하고 반영 가능.


---
### < @Transaction 어노테이션을 이용한 flush() >
- **@Transactional은 엔티티가 영속성 컨텍스트에 의해 관리되는 경우에만 자동으로 flush하고 저장** 처리를 수행.
  - @Transactional 어노테이션을 사용 시 
    **save()를 명시적으로 호출하지 않더라도 엔티티의 변경 사항이 자동으로 DB에 저장 가능.** 
    하지만, 이 동작은 몇 가지 조건 하에서만 적용됩니다.

1. @Transactional의 동작 원리
   - @Transactional 어노테이션이 붙은 메서드는 트랜잭션을 관리하는 역할 수행. 
   - **트랜잭션이 완료될 때, 영속성 컨텍스트에 있는 모든 변경사항을 자동으로 DB에 반영.** 
   - 트랜잭션 범위 내에서 엔티티의 상태가 변경되면 **메서드가 끝날 때 자동으로 flush가 발생**하고 commit.

2. 영속성 컨텍스트에서 관리되는 Entity
   - @Transactional이 적용된 메서드 내에서 이미 영속성 컨텍스트에 관리되는 엔티티가 수정되면, 
     트랜잭션이 종료될 때 JPA가 자동으로 변경 사항을 감지하고, flush()를 호출하여 DB에 반영합니다.
   ~~~
    @Transactional
    public ResponseProductionDto modifyProduction(RequestModifyProductionDto requestProductionDto, Member member) {
        // 기존 옵션 목록 dto -> entity 변환
        List<ProductionOption> requestUpdateOptionList = requestProductionDto.getOptions().stream()
            .filter(option -> option.getId() != null && option.getId() > 0)
            .map(modifyProductionOptionMapper::toEntity)
            .toList();
    
        // 상품 수정 (영속성 컨텍스트에 의해 관리되는 상태)
        Production production = productionRepository.findByIdAndSeller(requestProductionDto.getId(), member.getId())
                .orElseThrow(() -> new ProductionException(NO_EDIT_PERMISSION));
    
        // 수정된 옵션 목록에 대한 변경
        for (ProductionOption option : requestUpdateOptionList) {
            // 이때, option은 "dirty" 상태로 변경됨
            option.setQuantity(newQuantity);  
        }
    
        // 트랜잭션이 끝날 때 JPA가 자동으로 flush() 호출 -> 변경사항이 DB에 반영됨
        return productionMapper.toDto(production);
    }
   ~~~
   - @Transactional이 적용된 메서드 내에서 production과 그 관련 ProductionOption 객체들을 수정하는 경우, 
     트랜잭션이 종료될 때 자동으로 변경사항이 DB에 반영됩니다. 
     여기서 save() 메서드를 명시적으로 호출하지 않더라도,  
     트랜잭션 범위 내에서 수정된 Entity들은 자동으로 DB에 저장됩니다.

3. 영속성 컨텍스트 외의 객체
   - 만약 Entity가 영속성 컨텍스트 외부에서 생성되었거나 별도로 관리되지 않는 Entity인 경우,  
     @Transactional은 그 엔티티의 변경사항을 자동으로 DB에 반영하지 않습니다. 
   - 이 경우, 명시적으로 save()를 호출해야 DB에 반영됩니다.


---
### < 연관관계에 있는 Entity의 저장방식 >
- ProductionOption의 경우 Production Entity와 연관관계가 있기 때문에, 
  JPA에서 관리하는 영속성 컨텍스트 내에서 Production Entity가 변경되면 
  연관된 ProductionOption도 함께 변경되어야 할 것 같습니다. 

- 하지만 연관관계에 있는 자식 Entity(ProductionOption)가 자동으로 저장되지 않도록 설정된 경우, 
  직접적으로 save() 호출을 해줘야 합니다.

1. 영속성 컨텍스트 내에서 자동 저장되는 경우
   - JPA에서는 부모 Entity가 자식 Entity와 연관관계가 있을 때,  
     cascade 속성이 설정된 경우 자식 Entity도 부모 Entity의 변경과 함께 자동으로 저장됩니다. 
   - @OneToMany 관계에서 cascade = CascadeType.PERSIST나 cascade = CascadeType.ALL이 설정되어 있다면, 
     부모 Entity가 저장될 때 자식 Entity도 자동으로 저장됩니다.
   ~~~
    public class Production extends BaseEntity {
        // ...
        
        // 상품:옵션 (1:N)
        // CascadeType.ALL로 설정 시, 부모 엔티티를 저장할 때 자식 엔티티도 자동 저장됨.
        @OneToMany(cascade = CascadeType.ALL, mappedBy = "production")
        private List<ProductionOption> options; // 한 상품이 여러 옵션을 가질 수 있음.
    }
   ~~~

2. 수동으로 save()를 호출해야 하는 경우
   - cascade 속성이 설정되지 않은 경우 Production 엔티티에 대한 변경만 이루어지고
     연관된 ProductionOption Entity는 따로 명시적으로 저장해야 합니다. 
   - ProductionOption의 변경 사항이 
     영속성 컨텍스트에서 관리되지 않도록 설정되어 있거나, cascade가 설정되지 않았다면, 
     productionOptionRepository.save(option)을 통해 수동으로 ProductionOption을 저장해야 합니다.


---
### < 부모-자식 관계의 기본적인 동작 >
- 부모-자식 관계의 기본적인 동작은 상황에 따라 달라질 수 있습니다. 
- 부모 엔티티를 수정한다고 해서 자식 엔티티(옵션 데이터)가 자동으로 저장되지 않으며, 
  JPA에서 변경된 상태를 명시적으로 추적하지 않으면 자동으로 저장되지 않습니다.

- 다만, 연관된 자식 엔티티가 영속성 컨텍스트에 의해 관리되고 있으면 트랜잭션 내에서 자식 엔티티도 자동으로 저장될 수 있습니다.

1. 부모 엔티티와 자식 엔티티의 저장
   - JPA에서 부모 엔티티가 수정되었다고 해서 자식 엔티티가 자동으로 저장되지 않습니다. 
   - 부모 엔티티가 수정되어도 자식 엔티티가 수정되지 않거나 변경된 상태로 추적되지 않으면 
     JPA는 자식 엔티티에 대한 save() 호출 없이 자동으로 DB에 반영하지 않습니다.

2. 연관 관계에서 자식 엔티티 자동 저장
   - 부모 엔티티와 자식 엔티티가 양방향 관계(예: @OneToMany와 @ManyToOne 등)일 때 
     영속성 컨텍스트에서 자식 엔티티가 이미 관리되고 있으면, 
     부모 엔티티의 상태가 변경되면서 자식 엔티티도 자동으로 저장될 수 있습니다. 
   - 다만, 이때도 자식 엔티티가 명시적으로 수정되었는지 여부를 확인하는 과정이 필요합니다.

3. @Transactional과 관련된 동작
   - @Transactional을 사용하면 트랜잭션이 종료될 때 영속성 컨텍스트에 변경된 상태가 자동으로 DB에 반영됩니다. 
   - 하지만 자식 엔티티의 변경 사항이 영속성 컨텍스트에 관리되지 않으면, 
     부모 엔티티의 수정만 반영되고 자식 엔티티는 따로 save()를 호출해줘야 저장됩니다.

4. 영속성 컨텍스트에 의해 관리되는 엔티티들
   - 부모 엔티티와 자식 엔티티가 모두 영속성 컨텍스트에 의해 관리되면, 
     트랜잭션이 끝날 때 JPA는 자동으로 부모와 자식 엔티티의 상태를 반영합니다. 
   - 즉, 자식 엔티티가 save()를 명시적으로 호출하지 않아도 JPA가 자동으로 DB에 반영합니다.
   
5. CascadeType 설정
   - 부모 엔티티에 @OneToMany, @ManyToMany 등 관계가 설정되어 있고 
     cascade = CascadeType.ALL 또는 cascade = CascadeType.PERSIST와 같은 Cascade 옵션이 설정되어 있으면 
     부모 엔티티를 저장할 때 자식 엔티티가 자동으로 저장됩니다.
   ~~~
   // 이 설정은 부모 엔티티인 Production이 저장될 때, 연관된 자식 엔티티인 options도 자동으로 저장되도록 합니다.
   @OneToMany(mappedBy = "production", cascade = CascadeType.ALL)
   private List<ProductionOption> options;
   ~~~

6. 부모 엔티티 수정만으로 자식 엔티티가 자동 저장을 위한 조건 
   - 자식 엔티티가 영속성 컨텍스트에 의해 관리되고, CascadeType이 적절히 설정되어야 합니다. 
     @Transactional이 적용된 메서드 내에서 영속성 컨텍스트가 관리하는 엔티티는 자동으로 저장됩니다.
   - 그러나 @Transactional을 사용하여 트랜잭션 범위 내에서 부모 엔티티의 수정만 이루어진다고 하더라도, 
     자식 엔티티가 영속성 컨텍스트에 의해 관리되고 Cascade 옵션이 활성화되었을 경우에만 자식 엔티티가 자동으로 DB에 반영됩니다.    
     그렇지 않으면 명시적으로 save()를 호출해야 자식 엔티티도 DB에 저장됩니다.

7. Production과 ProductionOption
   - production의 options 필드는 @OneToMany 또는 @ManyToMany 등의 관계로 **자식 엔티티(옵션 데이터)**와 연결된 경우,
     options 데이터는 연관된 자식 엔티티로 영속성 컨텍스트에 의해 관리되고 있기 때문에
     부모 엔티티인 production이 트랜잭션 내에서 수정되면, 자식 엔티티인 options도 자동으로 저장될 수 있습니다.

