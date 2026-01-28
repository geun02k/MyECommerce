# MY E-COMMERCE PROJECT JPA STUDY
2025 간단한 이커머스 서비스를 만들면서 접한 JPA 공부내용.


---
## < 목차 >
1. JPA (Java Persistence API)
2. 공통으로 사용하는 추적정보를 위한 BaseEntity 생성
3. 테이블 연관관계
    - 회원권한테이블 추가
    - 두 테이블의 연관관계 설정 후 FK값이 null로 저장되는 문제
    - 연관관계에 있는 두 Entity의 데이터 조회
    - 연관관계의 Entity 조회방식
    - 연관관계에 있는 Entity에서 부모만 조회하기
    - JPA 연관관계에서 지연로딩, 즉시로딩
    - 연관관계에 있는 Entity의 저장방식
    - 부모-자식 관계의 기본적인 동작
4. 무한 순환 방지
5. FETCH
6. 상품수정 옵션 저장 시 발생한 에러 ObjectOptimisticLockingFailureException
7. update문을 명시적으로 우선 수행헀는데 실제로는 후에 호출한 insert문을 먼저 수행하는 이유
8. dirty 체크
9. @Transaction 어노테이션을 이용한 flush()
10. @Query에서 파라미터 바인딩 방법
11. JPA에서 Entity에 없는 필드인 계산된 값을 사용하기
12. 쿼리조회
    - 조회 시 고려사항
    - 쿼리 검색성능 개선하기
    - 쿼리 정확도순 정렬하기
13. 테이블 풀스캔 (Full Table Scan)
14. 인덱스
    - 대용량 데이터에서 인덱싱을 통한 성능 최적화
    - 인덱스와 PK
    - 복합 인덱스
    - JPA 인덱스 생성
    - LIKE 검색과 인덱스
    - 클러스티드 인덱스와 넌클러스티드 인덱스
15. Spring Data JPA에서 DTO Projection + FETCH JOIN으로 인한 QueryCreationException 해결
16. Entity와 초기화와 상태변경

---
### < JPA (Java Persistence API) >
자바에서 **객체 관계 매핑(ORM)**을 처리하기 위한 표준 API.
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
      JPA는 영속성 컨텍스트 내에서 엔티티를 캐싱하여, 
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
      JPA는 SQL을 직접 작성하지 않고도 객체와 관계형 데이터베이스 간의 데이터를 쉽게 처리할 수 있어 개발 생산성을 높임.
   2. 유지보수 용이   
      엔티티 클래스를 사용하여 객체 지향적인 방식으로 데이터를 처리하므로, 비즈니스 로직과 데이터베이스 로직을 분리할 수 있어 유지보수가 용이.
   3. DB 벤더 독립성    
      JPA는 특정 데이터베이스에 의존하지 않기 때문에, 데이터베이스가 변경되어도 코드 수정 없이 쉽게 대체가능.


---
###  < 공통으로 사용하는 추적정보를 위한 BaseEntity 생성 >
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
   해당 클래스에 Auditing 기능을 포함.
   1) JPA Auditing 활성화   
      @SpringBootApplication 어노테이션이 있는 Application.java 파일에 @EnableJpaAuditing 어노테이션을 추가해
      JPA Auditing 기능을 활성화 해야 사용가능.
   2) JPA의 Auditing       
      JPA에서는 Audit 기능을 제공한다.   
      Audit은 spring data JPA에서 시간에 대해 자동으로 값을 넣어주는 기능이다.   
      도메인을 영속성 컨텍스트에 저장하거나 조회를 수행한 후 update 하는 경우 매번 시간 데이터를 입력해주어야하는데
      이 때 자동으로 시간을 매핑하여 데이터베이스의 테이블을 넣어주게 된다.
   3) @CreatedDate    
      Entity가 생성되어 저장될 때 시간 자동 저장.
   4) @LastModifiedDate   
      조회한 Entity의 값을 변경할 때 시간 자동 저장.
   - 참고 블로그 : https://webcoding-start.tistory.com/53


---
## 테이블 연관관계
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


### < 두 테이블의 연관관계 설정 후 FK값이 null로 저장되는 문제 >
1. 주인 Entity
   1) @ManyToOne
   2) @JoinColumn(name="memberId")   
     테이블 매핑 시 foreign key 지정 / 해당 외래 키를 관리하는 주인 Entity에 설정
   3) @JsonBackReference
      - 양방향 연관관계에서 발생할 수 있는 무한 순환 참조 문제를 방지하기 위해 사용.
      - 주로 직렬화(Serialization, 객체를 JSON으로 변환) 시, 서로 참조하는 두 객체가 무한히 참조하면서 순환하는 것을 막고자 할 때 유용.
      - 직렬화 시 MemberAuthority 객체는 포함되지 않음. (없으면 무한호출됨)
2. 비주인 Entity
   1) @OneToMany(cascade = CascadeType.ALL, mappedBy = "member")
      - 테이블 연관관계 1(회원):N(권한) 설정.   
        member_authority(사용자권한테이블) 테이블과 join해서 권한정보 가져옴.
      - mappedBy : 비주인 설정
      - cascade = CascadeType.ALL : Member가 저장될 때 관련된 MemberAuthority도 자동으로 저장.
      - orphanRemoval = true :  Member에서 MemberRole을 제거할 때 MemberRole이 삭제.
        테이블 연관관계 1:N 설정 / mappedBy : 비주인
    2) @JsonManagedReference   
       직렬화 시 MemberAuthority 리스트는 포함되지만, Member 객체는 포함되지 않음.

3. 연관관계의 주인을 N으로 둔다는 것
   - 1:N 관게에서 N(다수)을 연관관게의 주인으로 설정하는 것. (Member 비주인, MemberAuthority 주인)
   - @OneToMany에서 mappedBy 옵션으로 비주인임을 설정.
   - @ManyToOne 어노테이션을 작성한 Entity에서 @JoinColumn에서 name 옵션에 FK를 작성해 외래키를 관리하는 주인으로 설정.
   - 주인 클래스인 MemberAuthority에서 비주인인 Member를 변경하거나 삭제할 때만 외래키 수정.
   - Member가 저장될 때 불필요한 update 쿼리가 발생 x.

4. 기존 회원, 회원권한 저장 시 문제
   1) 문제 발생 원인   
      controller에서 우선으로 MemberAuthority(권한)을 생성해 Member(회원) 객체에 우선 데이터를 넣은 후 회원 저장을 수행.
      따라서 MemberAuthority 클래스의 member 필드값이 memberRepository.save() 의 반환값 객체가 아니라
      요청 시 전달받은 member 객체라 id 필드 값이 비어있기 때문에 잘못된 객체 전달로 인해 FK인 memberId값이 null로 저장된 것으로 판단.
    2) 문제 해결   
      memberRepository.save()의 반환값인 Member 객체를 MemberAuthority 객체의 member 필드에 저장해
      memberAuthorityRepository.save()를 수행해 FK값이 null로 저장되는 문제를 해결.

5. 주인 Entity에 FK 필드 선언 제거
   - 연관관계 설정 시 @JoinColumn(name="memberId") 어노테이션을 통해 join column을 설정해주면 FK 필드가 자동 생성됨.   
     띠라서 memberId라는 별도의 필드가 필요없음.   
   - 기존 작성했던 FK 필드 제거.


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
         Entity 클래스명, 필드명을 사용해도 아래의 오류 발생.
         > org.springframework.core.convert.ConversionFailedException: 
           Failed to convert from type [java.lang.Object[]] to type [com.myecommerce.MyECommerce.entity.production.Production] for value [{...}]
         - LEFT JOIN FETCH를 사용하여 Production과 ProductionOption을 함께 로딩.
         - Production 엔티티를 직접 반환하므로, Production 객체를 그대로 반환할 수 있습니다.
         - LEFT JOIN FETCH를 사용하면 productionOptions 컬렉션이 지연 로딩 대신 즉시 로딩됨.
   
   4. Entity의 연관관계를 맺어줬는데도 JOIN해 조회가 불가한 문제
      1) 발생에러
         > org.springframework.dao.InvalidDataAccessApiUsageException:
           org.hibernate.query.SemanticException:
           Entity join did not specify a join condition
           [SqmEntityJoin(com.myecommerce.MyECommerce.entity.production.ProductionOption(OPTION))]
           (specify a join condition with 'on' or use 'cross join')
    
      2) 발생원인
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
      
      3) 해결방법
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
   

## < 연관관계에 있는 Entity에서 부모만 조회하기 >
Production 엔티티가 productionOptions와 연관 관계가 있을 경우,
prd만 선택해도 연관된 ProductionOption 정보가 함께 로드될 수 있습니다.

- 부모 Entity 정보만 로드하는 방법   
  이를 방지하려면, **JPQL 쿼리에서 productionOptions를 제외하거나 fetch를 제한**하는 방법이 필요합니다.

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
   @Query에서는 JOIN FETCH를 사용하지 않으면 자식이 자동으로 로드되지 않습니다. 

LAZY 로딩을 설정하면 ProductionOption이 즉시 로드되지 않도록 할 수 있으며, 필요한 경우에만 로드되게 할 수 있습니다.
FETCH 전략을 LAZY로 설정하고, 쿼리에서 JOIN만 사용하면 ProductionOption이 쿼리 결과에 포함되지 않습니다.
따라서, @OneToMany나 @ManyToOne 관계에서 LAZY 로딩을 설정하는 것이 연관된 데이터를 지연 로딩하도록 하는 가장 좋은 방법입니다.


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


---
### < 무한 순환 방지 >
- 아래 두 개의 어노테이션은 한 세트로 적용해야함.
- 한쪽에서는 직렬화를 포함시키고, 다른 한쪽에서는 제외시켜 무한 순환을 방지.
1. 직렬화(Serialization)
    - 객체를 바이트 스트림이나 다른 저장 가능한 형식(JSON, XML)으로 변환하는 과정
    - 직렬화의 목적은 객체를 메모리 내에서 외부로 내보내거나(저장) 외부에서 불러오는 것(복원).
2. @JsonManagedReference
    - "출발점" 객체에 사용되며, 직렬화 시 이 객체는 포함됨.
    - 이 객체의 참조는 직렬화 과정에서 포함되고, 해당 객체가 참조하는 다른 객체들은 @JsonBackReference로 지정된 객체에 의해 직렬화에서 제외됨.
3. @JsonBackReference
    - "대상" 객체에 사용되며, 직렬화 시 이 객체의 참조는 미포함.
    - 이 객체를 직렬화할 때 해당 객체가 참조하는 다른 객체는 직렬화에서 제외됨.


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

2. SQL JOIN과 JPA FETCH
- JPQL은 서브쿼리 내에서 FETCH를 사용할 수 없으며,
  GROUP BY나 집계 함수와 관련된 쿼리를 FETCH와 함께 사용할 때 문제가 발생할 수 있습니다.

- INNER JOIN FETCH
    - 연관된 엔티티를 가져오기 위해 사용됩니다.
- FETCH
    - 단일 엔티티를 연관된 엔티티와 함께 로드하는 데만 사용.
- 서브쿼리와 JOIN FETCH
    - 일반적으로 서브쿼리는 SELECT에서 바로 사용할 수는 있지만, FETCH 와 결합하는 것은 허용되지 않습니다.

- GROUP BY와 집계 함수
    - JPQL에서 서브쿼리 내에서 집계 함수를 사용할 수 있지만, FETCH와 결합하는 것은 불가능합니다.

- 해결 방법
    - 서브쿼리에서 MIN(option.price) 값을 계산하고,
      이를 결과에 포함시키기 위해서는 서브쿼리를 JOIN으로 처리하는 방식으로 접근해야 합니다.
    - FETCH는 연관 관계를 가져오는 데 사용되므로, 서브쿼리와 함께 사용하지 않고,
      서브쿼리 결과를 JOIN한 후 ORDER BY를 적용하는 방법이 적합합니다.
  ~~~
  SELECT prd
  FROM Production prd
  JOIN prd.productionOptions option
  WHERE prd.name LIKE CONCAT('%', ?1, '%')
  AND prd.saleStatus = 'ON_SALE'
  GROUP BY prd
  ORDER BY MIN(option.price)
  ~~~
    - JOIN prd.productionOptions option
        - Production 엔티티와 연관된 ProductionOption 엔티티를 JOIN합니다.
        - 이때 INNER JOIN이 기본적으로 적용됩니다.
    - 서브쿼리
        - 서브쿼리를 사용하여 각 Production에 대해 **가장 낮은 가격을 가진 ProductionOption**을 찾습니다.


FETCH는 연관된 엔티티를 함께 로드할 때 사용되며, 집계 함수나 서브쿼리와는 잘 결합되지 않습니다.
대신 JOIN을 사용하여 연관된 데이터를 가져오는 방식으로 처리합니다.
서브쿼리에서 MIN() 함수와 GROUP BY를 사용할 때는 JOIN을 통해 결과를 비교하고 필터링하는 방식이 적합합니다.
이렇게 수정된 쿼리는 각 Production에 대해 관련된 ProductionOption 중 가장 낮은 가격을 가진 옵션을 찾아 그 가격을 기준으로 정렬된 결과를 반환합니다.


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
  Entity가 변경되었음을 JPA가 인식하고, DB에 반영될 준비가 된 상태.
- ProductionOption 엔티티에서 quantity 값을 변경했다면, 해당 엔티티는 "dirty" 상태로 간주.
- JPA는 해당 엔티티가 변경되었음을 추적합니다.

1. JPA가 dirty check를 인식할 수 있는 이유
   - JPA는 영속성 컨텍스트라는 메커니즘을 사용하여 엔티티의 상태를 추적. 
   - 영속성 컨텍스트는 데이터베이스와의 연결을 관리하는 메모리상의 객체 상태를 의미. 
   - 이 컨텍스트 내에서 엔티티의 상태가 변경되면, JPA는 이를 추적하고,  
     flush() 또는 save()가 호출될 때 자동으로 DB에 반영합니다.

2. Entity의 상태
   1) Entity가 처음 로드된 경우   
      데이터베이스에서 엔티티를 가져오면 영속성 컨텍스트에 보관되고, 
      이 상태에서 **Entity는 변경되지 않은 상태**.
   2) Entity가 수정된 경우
      - Entity의 필드값을 변경하면, JPA는 이 Entity가 "dirty" 상태로 바뀌었다고 인식. 
      - 필드값이 변경되었으므로 **수정된 상태는 flush()나 save()가 호출될 때 DB에 변경사항이 반영**됨.
    3) Entity에 저장되지 않은 변경사항이 있는 경우   
       Entity가 "dirty" 상태로 **변경되었지만 save() 또는 flush()가 호출되지 않으면
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
   @Transactional이 적용된 메서드 내에서 이미 영속성 컨텍스트에 관리되는 엔티티가 수정되면, 
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
### < @Query에서 파라미터 바인딩 방법 >
1. ?1 (위치 기반 바인딩)
    - 순서대로 파라미터를 바인딩가능. (?1 -> 첫 번째 파라미터 참조)
    - 순서를 명확히 알 수 있다면 짧고 직관적.
    - 파라미터 순서가 바뀌면 오류 발생가능.
    - 쿼리에서 직접 파라미터를 사용할 때 순서를 정확히 맞춰야 하기 때문에 가독성이 떨어질 수 있음.
   ~~~
   @Query("SELECT p FROM Product p WHERE p.name = ?1")
   List<Product> findByName(String name);
   ~~~

2. :name (명명된 파라미터)
    - 쿼리 내에서 파라미터의 이름을 명시적으로 지정하고, 이를 메서드 파라미터와 연결.
    - 파라미터 순서에 구애받지 않으며, 쿼리의 가독성이 높음.
    - 파라미터 이름이 명확하게 나타나므로 쿼리 수정 시 안전.
    - 파라미터 이름을 지정해주어야 하므로, @Param 어노테이션을 사용해야 할 경우가 많음.
   ~~~
   @Query("SELECT p FROM Product p WHERE p.name = :name")
   List<Product> findByName(@Param("name") String name);
   ~~~


---
### < JPA에서 Entity에 없는 필드인 계산된 값을 사용하기 >
> 쿼리에서 사용되는 값이 실제로 엔티티에 존재하지 않으면 해당 값을 **계산된 값으로 처리**해야함.
> @Query에서 직접 계산할 수 있거나, 애플리케이션에서 후처리하여 정렬가능.
> 동적 쿼리나 Criteria API, Querydsl을 사용하여 계산된 값을 정렬 기준으로 활용가능.

- accuracy는 실제로 Production 엔티티의 필드가 아니고, 단지 쿼리 정렬을 위한 임시 값으로 사용.
- 정렬 기준으로 사용되는 accuracy가 실제 필드가 아닌 계산된 값이기 때문에, 이를 쿼리에서 직접 사용은 뷸가능.

- accuracy는 실제 엔티티의 필드가 아니므로, 이를 기준으로 정렬을 하려고 하면 @Query에서 사용불가.
- 정렬 기준으로 사용될 값을 계산하거나 다른 방식으로 해당 값을 처리는 가능.

1. 쿼리에서 accuracy를 계산된 값으로 사용하기 (내가 사용한 방법)
    - 만약 accuracy가 계산된 값이라면, @Query에서 그 값을 직접 계산하거나 별도로 처리할 수 있도록 쿼리를 작성하는 방법.
    - accuracy가 어떤 계산식에 의해 정해진다면, 그 계산식을 쿼리에서 직접 구현가능.
    - 실제 accuracy처럼 **계산된 값을 @Query 내에서 사용하려면, 계산 수식을 ( )에 포함해 직접 쿼리 안에서 정의**필요.
    - accuracy가 실제로 **계산식에 의존하는 값**이라면, 그 계산식을 쿼리 내에서 직접 계산하여 정렬 기준으로 사용가능.
   ~~~
    // someField1과 someField2를 더한 값을 정렬 기준으로 사용.
    @Query("SELECT p FROM Production p WHERE p.name = :name ORDER BY (p.someField1 + p.someField2) DESC")
    Page<Production> findByNameOrderByCalculatedAccuracyDesc(@Param("name") String name, Pageable pageable);
   ~~~

2. 애플리케이션에서 accuracy를 계산하여 전달하기
    - accuracy가 엔티티 내에 존재하지 않는 값이지만 애플리케이션 내에서 계산할 수 있는 값이라면,
      애플리케이션 코드에서 먼저 해당 값을 계산하고 정렬을 위해 별도로 처리한 뒤 페이징된 결과를 반환가능.
    - accuracy 값이 복잡한 계산이나 외부 데이터를 참조하는 경우
      쿼리에서는 계산된 값을 사용하지 않고 결과를 애플리케이션에서 후처리하여 정렬을 구현가능.
   ~~~
    // accuracy 값을 애플리케이션에서 계산하여 정렬한 후, PageImpl을 사용하여 페이징 처리된 결과를 반환.
    public Page<Production> findByNameOrderByAccuracyDesc(String name, Pageable pageable) {
        Page<Production> productions = productionRepository.findByName(name, pageable); 

        // accuracy 값을 계산하고 정렬
        List<Production> sortedProductions = productions.stream()
                .sorted((p1, p2) -> Double.compare(calculateAccuracy(p2), calculateAccuracy(p1)))
                .collect(Collectors.toList());

        return new PageImpl<>(sortedProductions, pageable, productions.getTotalElements());
    }
   
    private double calculateAccuracy(Production production) {
        // accuracy를 계산하는 로직
        return production.getSomeField1() * production.getSomeField2() / production.getSomeField3();
    }
   ~~~ 

3. Criteria API 또는 Querydsl을 사용한 동적 쿼리 처리
    - Criteria API나 Querydsl을 사용하면, 동적으로 복잡한 계산을 기반으로 정렬가능.
   ~~~
    // Criteria API를 사용하여 동적으로 계산된 값(someField1 * someField2)을 기준으로 정렬 수행
    public Page<Production> findByNameWithCalculatedAccuracy(String name, Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Production> cq = cb.createQuery(Production.class);
        Root<Production> root = cq.from(Production.class);
   
        // 조건 설정
        Predicate namePredicate = cb.equal(root.get("name"), name);
        cq.where(namePredicate);
    
        // 계산된 값으로 정렬 (예: someField1 * someField2 )
        cq.orderBy(cb.desc(cb.prod(root.get("someField1"), root.get("someField2"))));
    
        TypedQuery<Production> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
     
        return new PageImpl<>(query.getResultList(), pageable, countTotalResults());
    }
   ~~~


---
## 쿼리 조회
### < 조회 시 고려사항 >
1. 검색 성능 최적화 (효율성)
    - 대량의 데이터가 있을 경우 검색 성능을 고려해야 합니다.
    1. **인덱싱**을 통해 빠르게 검색할 수 있는 방법을 고려
    2. **데이터 정렬**
    3. **이진 탐색** 활용
    4. ...

2. 확장성
    - 유연한 검색 시스템을 위해 **검색 조건을 별도의 클래스나 객체로 추상화**해 추후 조건 추가가 용이하도록 코드 설계 권장.

3. Stream API 필터링 vs 데이터베이스 쿼리 필터링
    1. Stream API 필터링
        - 모든 데이터를 한 번에 메모리로 로드한 후 필터링하기 때문에, 데이터 양이 많아질수록 성능 저하.
        - 특히 대용량 데이터 처리 시 메모리 사용량 또한 크게 늘어나고, **CPU 성능에 부담**을 줄 수 있음.

    2. **데이터베이스 쿼리 필터링**
        - 네트워크 부담 감소
            - 데이터베이스 쿼리에서 조건을 사용한 필터링은 불필요한 데이터를 서버 측에서 미리 걸러내기 때문에
              **불필요한 데이터를 클라이언트로 전송하지 않기 때문에 네트워크 비용이 절감.**
        - 메모리 절약
            - 서버 또는 클라이언트에서 필터링을 하지 않고 **필요한 데이터만 메모리에 로딩하기 때문에 메모리 사용량 감소.**
        - 성능 향상
            - 데이터베이스는 대량의 데이터를 처리하는 데 최적화되어 있기 때문에 **대규모 데이터셋을 필터링하는 데 더 빠르고 효율적**.
            - 데이터가 커질수록 DB에서 필터링하는 방식을 선호하는 것이 성능 면에서 더 우수.
            - 데이터베이스는 **인덱싱과 최적화된 검색 기능**을 가지고 있기 때문에 필터링 성능이 매우 뛰어납니다.


### < 쿼리 검색성능 개선하기 >
- 추후 반영해서 구현해볼 것
- 참고블로그   
  https://velog.io/@juhyeon1114/MySQL-%EA%B2%80%EC%83%89-%EC%84%B1%EB%8A%A5-%EA%B0%9C%EC%84%A0%ED%95%98%EA%B8%B0-like%EC%99%80-full-text-search


### < 쿼리 정확도순 정렬하기 >
- like로 검색 후 유사한 결과 먼저 정렬하기
    - 참고블로그   
      https://blog.naver.com/rorean/221566107729

- like 검색의 단점과 FullTextSearch (10000건 정도되면 like검색과 차이난다고 함.)
    - 참고블로그
      https://annajin.tistory.com/218


---
### < 테이블 풀스캔 (Full Table Scan) >
- 데이터베이스에서 쿼리를 실행할 때 인덱스를 사용하지 않고 테이블의 모든 행을 순차적으로 읽는 작업. 
- 테이블에 있는 **모든 데이터를 검사하여 조건에 맞는 결과를 찾아내는 방식.**
- 쿼리 성능 최적화를 위해 풀스캔이 발생하는지 확인하고, 필요하다면 인덱스 최적화나 쿼리 변경을 고려하는 것이 중요.

1. 풀스캔이 발생하는 경우
   1) 인덱스가 없을 때   
      쿼리에서 사용한 컬럼에 인덱스가 없다면, 데이터베이스는 해당 컬럼을 기준으로 테이블의 모든 데이터를 읽어야 하므로 풀스캔이 발생.
   2) 인덱스를 무시할 때    
      쿼리에서 인덱스를 사용하지 않도록 유도할 수 있는 힌트나 조건이 있을 때, DB는 인덱스를 사용하지 않고 풀스캔 실행가능.
   3) 전체 테이블을 조회하는 경우   
      SELECT * FROM 테이블명처럼 전체 데이터 조회 쿼리 실행 시 데이터베이스는 테이블의 모든 행을 가져오기 위해 풀스캔을 사용.
   4) 필터 조건이 불효율적일 때   
      WHERE 절에 사용된 조건이 대부분의 행에 대해 참이어서 인덱스를 사용하는 것보다 풀스캔이 더 효율적일 경우에도 풀스캔 발생가능.

2. 풀스캔의 문제점
   1) 성능 저하   
      테이블에 데이터가 많을 경우, 풀스캔은 모든 행을 읽어야 하므로 성능저하.
   2) 리소스 사용   
      CPU와 I/O 자원을 많이 사용하게 되어 서버의 성능에 부하를 줄 수 있음.

3. 풀스캔을 방지하는 방법
    1) 적절한 인덱스 사용   
       자주 쿼리에서 사용되는 컬럼에 인덱스를 추가하여 인덱스를 통한 검색을 유도.
    2) 쿼리 최적화   
       조건을 최적화하여 인덱스를 잘 활용할 수 있도록 함.
    3) 테이블 파티셔닝   
       데이터 양이 너무 많을 경우, 테이블을 파티셔닝하여 부분적으로 데이터를 나누어 관리가능.

4. 풀스캔이 유리한 경우
   - 데이터의 양이 적을 때
   - 테이블에 인덱스를 만들기 위한 비용이 너무 크거나, 인덱스를 사용해도 성능이 향상되지 않을 때
   - 모든 데이터를 조회하는 쿼리일 경우


---
## 인덱스
### < 대용량 데이터에서 인덱싱을 통한 성능 최적화 >
1. 인덱스
   - 인덱스는 **테이블의 특정 컬럼에 대한 검색을 빠르게 하기 위한 데이터 구조.**
   - 데이터베이스에서 **검색 성능을 크게 향상**시킬 수 있는 강력한 도구.
   - **데이터를 효율적으로 찾을 수 있게 도와주는 자료구조**.
   - **검색 속도를 개선**하고 **쿼리 성능을 최적화**하는 데 중요.
   - 대용량 데이터를 다룰 때는 반드시 인덱스를 활용하는 것이 좋음.
   - 데이터베이스에서 필터링과 정렬을 효율적으로 처리하기 위해 인덱스를 적절히 사용하면 성능을 크게 향상시킬 수 있음.
   > 기본적으로 책의 목차와 같은 역할.     
   책에서 특정 내용을 찾을 때 목차를 보고 해당 페이지를 바로 찾는 것처럼,
   데이터베이스에서도 인덱스를 사용하면 원하는 데이터를 더 빠르게 검색가능.

2. 인덱스가 성능을 최적화하는 방식
   - 인덱스를 사용하면 데이터베이스가 데이터를
     전체 테이블 스캔**(Full Table Scan)하지 않고 인덱스를 이용한 검색**으로 빠르게 원하는 데이터를 찾을 수 있음.
   - 특히, 대용량 데이터에서 이 차이는 매우 큼.
   > 100만 개 이상의 상품 데이터에서 특정 가격 이상의 상품을 찾는 경우,
   인덱스가 없으면 모든 상품을 하나씩 확인해야 하지만, **인덱스가 있으면 필요한 범위의 데이터만 빠르게 찾아낼 수 있음.**

3. 인덱스의 종류
   - 인덱스는 여러 종류가 있으며, 각각의 경우에 따라 성능 최적화 방식 상이.
   - 주요 인덱스 종류
     1) B-Tree 인덱스
        - 가장 일반적으로 사용되는 인덱스 형태
        - 범위검색(ex) 가격 범위)과 정렬된 데이터에 최적화.
     2) Hash 인덱스
        - 주로 등가 비교(=)에 최적화된 인덱스.
        - 값이 정확히 일치하는 데이터를 빠르게 찾을 수 있음.
     3) Bitmap 인덱스
        - 작은 데이터셋에 대해 효과적인 인덱스
        - 다중 조건 검색에 유리.
     4) GiST (Generalized Search Tree) 인덱스
        - 공간 검색(ex)위치 기반 검색)이나 비정형 데이터에 최적화된 인덱스.
   > 가격 필터링을 위한 인덱스 예시
   >
   > 만약 "가격이 10000 이상인 상품" 을 찾는 경우, **가격 컬럼에 인덱스를 생성**하면 쿼리 성능을 크게 향상가능.
   > 아래의 쿼리로 조회할 때 price 컬럼에 인덱스를 추가하면,
   데이터베이스는 전체 테이블을 스캔하지 않고 인덱스를 통해 효율적으로 가격 조건에 맞는 데이터만 검색가능.
   > ~~~
   > SELECT * FROM products WHERE price >= 10000;
   > ~~~

4. 인덱스 생성 예시 (MySQL, PostgreSQL 등)
   - MySQL에서 price 컬럼에 인덱스를 추가하려면 다음과 같은 SQL을 사용.
   - price 컬럼에 대해 B-Tree 인덱스 생성.
     ~~~
     CREATE INDEX idx_price ON products(price);
     ~~~

5. 인덱스의 장점
   1) 검색 성능 향상   
      인덱스는 데이터를 빠르게 찾을 수 있게 도움.
   2) 정렬 성능 향상   
      데이터베이스에서 데이터를 정렬하는 쿼리도 인덱스를 활용해 더 효율적으로 처리.
   3) 범위 쿼리 최적화   
      범위 기반 검색(ex) 가격 범위, 날짜 범위)을 매우 빠르게 처리가능.

6. 인덱스의 단점
   1) 디스크 공간 사용   
      - 인덱스는 별도의 공간을 차지.
      - 많은 인덱스를 생성하면 디스크 공간이 많이 소모될 수 있음.
   2) 쓰기 성능 저하   
      데이터베이스에 **데이터를 삽입, 수정, 삭제할 때마다 인덱스를 갱신**해야 하므로 쓰기 성능이 떨어질 수 있음.
   3) 과도한 인덱스 생성   
      너무 많은 인덱스를 생성하면 **관리가 복잡**해지고, **성능이 저하**될 수 있음.

7. 인덱스 사용 시 주의사항
   1) 필요한 컬럼에만 인덱스 생성   
      모든 컬럼에 인덱스를 생성하는 것이 아니라, **실제로 자주 검색되는 검색 조건에 해당하는 컬럼에만 인덱스를 생성**하는 것이 좋음.
   2) 쿼리 성능 모니터링 후 불필요 인덱스 제거   
      인덱스를 생성하고 나서 실제로 쿼리 성능이 어떻게 달라지는지 모니터링하고, 불필요한 인덱스는 삭제하는 것이 중요합.
   3) 복합 인덱스   
      여러 컬럼을 동시에 자주 검색하는 경우, 복합 인덱스를 사용하는 시 성능 향상가능.
      > 가격과 카테고리로 동시에 검색하는 경우
      > ~~~
      > CREATE INDEX idx_price_category ON products(price, category);
      > ~~~


### < 인덱스와 PK >
- 기본 키(Primary Key, PK)는 인덱스 역할을 수행.
- 실제로 대부분의 데이터베이스에서는 Primary Key가 자동으로 클러스터드 인덱스(Clustered Index)를 생성.

1. 인덱스와 PK의 관계
   - Primary Key는 각 레코드의 고유성을 보장하고, 이를 통해 조회 성능 향상.
   - 대부분의 데이터베이스 시스템에서 Primary Key는 자동으로 인덱스가 생성되며 이 인덱스는 기본적으로 클러스터드 인덱스임.
   - 데이터베이스 내의 실제 데이터 행들이 해당 인덱스의 순서대로 저장됨.

2. 인덱스와 PK의 차이
   1) PK
      - 기본적으로 고유성을 보장하기 위한 제약 조건.
      - 기본적으로 NULL 값을 허용하지 않으며, 데이터의 고유한 식별자로 사용.
      - PK는 이미 인덱스를 가지고 있기 때문에 별도로 추가적인 인덱스를 만들지 않아도 됨.
    2) 인덱스
       - 데이터베이스에서 검색 성능을 최적화하기 위해 사용하는 자료 구조.
       - 기본 키뿐만 아니라, 다른 컬럼에도 인덱스를 추가가능.


### < 복합 인덱스 >
> 복합 인덱스는 하나 이상의 컬럼을 동시에 자주 조회하는 쿼리에서 성능을 크게 향상시킬 수 있는 합리적인 선택.
> 각각의 인덱스를 만드는 것보다 복합 인덱스를 사용하는 것이 더 효율적. 
> **복합 인덱스는 쿼리 성능을 개선하고, 인덱스 관리 비용 저하**에 도움이 됨.
> 다만, **인덱스의 순서와 쿼리 패턴에 따라 성능이 달라질 수 있음**에 인덱스 순서를 잘 설정하는 것이 중요합니다.

1. 복합 인덱스 사용 이유
   - 복합 인덱스는 두 개 이상의 컬럼을 하나의 인덱스로 묶어서 검색 성능을 최적화할 수 있는 방법. 
   - 각각 인덱스를 만드는 대신 복합 인덱스를 사용하는 이유는 
     **두 컬럼을 결합하여 함께 자주 조회하는 쿼리가 많을 때 
     두 컬럼을 결합한 검색이 더 효율적으로 수행되어 성능을 더욱 향상시킬 수 있기 때문**입니다.

2. 복합 인덱스의 동작 원리
   - 복합 인덱스는 두 컬럼을 포함한 하나의 인덱스를 통해 검색을 빠르게 수행. 
   - 인덱스가 두 컬럼을 동시에 포함하고 있으므로, DB는 해당 인덱스를 통해 조건을 만족하는 데이터를 효율적으로 찾아낼 수 있음.
   > 인덱스의 순서는 매우 중요. 
   > 인덱스가 (saleStatus, category) 순서로 생성된다면, 
     DB는 먼저 saleStatus 컬럼을 사용해 데이터를 좁힌 후 category 컬럼을 사용해 나머지 조건을 처리.

3. 복합 인덱스가 합리적인 이유
   - **두 컬럼이 항상 함께 조건절에 포함되는 경우** 두 컬럼을 포함하는 인덱스를 사용하면, DB가 **두 조건을 동시에 처리**할 수 있게 되어 성능이 크게 향상.

4. 각각의 컬럼에 인덱스를 생성하는 경우
   - 두 컬럼에 대해 **각각에 인덱스 생성하는 경우 DB는 쿼리를 실행 시 인덱스를 따로따로 사용해야함.** 
     > 먼저 saleStatus로 데이터를 필터링하고, 그 결과를 category 인덱스와 병합해야 하는 추가 작업이 필요.0
   - 반면 복합 인덱스를 사용 시 DB는 두 조건을 한 번에 처리할 수 있어 더 빠르고 효율적으로 쿼리를 처리가능.
   
5. 읽기 성능 최적화
   - 복합 인덱스는 읽기 성능을 크게 개선. 
   - saleStatus와 category가 함께 조건절에 자주 사용되는 경우 복합 인덱스가 있으면 검색 성능이 빠름, 
   - DB는 전체 테이블을 스캔하는 풀 스캔을 피할 수 있음. 
   - 인덱스 크기가 각각의 인덱스를 두 개 만든 것보다 더 작아지므로, 디스크 I/O나 캐시 효율성 측면에서도 유리.

6. 인덱스의 크기와 관리 비용
   - 복합 인덱스를 사용하면 인덱스 개수가 줄어들어 인덱스 관리 비용이 줄어듦. 
   - 각각의 인덱스를 별도로 만들면 DB는 각 인덱스에 대해 별도로 업데이트하고 관리해야 하기 때문에 성능에 부담이 될 수 있음.

3. 주의사항
   - 인덱스 순서
     - **복합 인덱스를 만들 때는 컬럼 순서가 매우 중요**. 
     > 예를 들어 (saleStatus, category) 순서로 인덱스를 만들면, 
       saleStatus로 먼저 필터링 후 category를 필터링하는 방식으로 작동. 
     > 만약 쿼리가 category로 먼저 필터링되고 그 후 saleStatus로 필터링된다면, 복합 인덱스를 제대로 활용하지 못할 수 있음. 
     > 따라서 쿼리의 조건절에서 어떤 컬럼이 먼저 오는지에 맞게 인덱스를 설계 필요.
  - 빈번하게 사용되는 쿼리에만 인덱스를 추가하는 것이 효율적.     
    너무 많은 인덱스는 데이터 삽입, 수정, 삭제 시 성능을 저하 가능성 있음.


### < LIKE 검색과 인덱스 >
> 인덱스 컬럼을 활용한 검색 최적화
> LIKE 연산자 대신 = 연산자나 BETWEEN 등으로 검색 범위를 좁히는 방식이 성능이 더 좋을 수 있음.
> 와일드카드가 뒤에만 있을 때는 인덱스가 잘 활용됨. (LIKE 'xxx%')
> 와일드카드가 와일드카드가 앞이나 중간에 있을 때는 인덱스 활용이 어려워지고 풀 스캔이 일어날 수 있음. (LIKE '%xxx' | LIKE '%xxx%')
> 
> 따라서 **LIKE 검색을 사용할 때 성능을 고려하려면, 인덱스를 활용할 수 있는 검색 패턴을 사용하는 것이 중요**합니다.

- LIKE 검색을 할 때 인덱스가 어떻게 동작하는지는 검색 패턴에 따라 상이. 
- LIKE 검색 시 인덱스를 효율적으로 활용할 수 있는 경우와 그렇지 않은 경우 존재.

1. LIKE의 시작 부분에 와일드카드 (%)가 없는 경우
   - 인덱스를 효과적으로 사용가능.
   ~~~
    SELECT * FROM employees WHERE name LIKE 'John%';
    이 경우 name 컬럼에 LIKE가 'John%'로 시작하므로, 데이터베이스는 name 컬럼에 인덱스를 사용하여 John으로 시작하는 값들을 빠르게 찾을 수 있습니다. 이는 인덱스 범위 검색이 가능하기 때문에 성능이 좋습니다.
   ~~~

2. LIKE의 시작 부분에 와일드카드 (%)가 있는 경우
    - 인덱스를 제대로 활용하기 어려움
   ~~~
    SELECT * FROM employees WHERE name LIKE '%John';
    이 쿼리는 name 컬럼의 값이 끝에 John이 포함된 모든 값을 찾는 검색이기 때문에, 데이터베이스는 인덱스를 사용할 수 없고, 테이블을 풀 스캔해야 할 수 있습니다. %가 앞에 오면 검색 조건이 앞에서부터 일치하는 값을 찾지 않기 때문에, 인덱스를 효율적으로 사용할 수 없게 됩니다.
   ~~~
   
3. 중간에 와일드카드가 있는 경우
   - LIKE 검색에서 %가 중간에 있는 경우도 인덱스를 사용불가.
   ~~~
    SELECT * FROM employees WHERE name LIKE '%John%';
    이와 같은 경우도 테이블의 모든 행을 확인해야 하므로 풀 스캔이 일어나게 됩니다. 인덱스는 처음부터 끝까지 일치하는 패턴을 빠르게 찾는 데 유리하기 때문에, %가 중간에 있을 경우 성능이 크게 떨어집니다.
   ~~~


### < JPA 인덱스 생성 >
- JPA에서 인덱스를 생성하려면 @Table 어노테이션과 함께 **@Index 어노테이션을 사용**하여 
  엔티티 클래스의 필드에 인덱스를 정의해 엔티티에 대한 **테이블 생성 시 인덱스를 지정가능.**
  ~~~
    import javax.persistence.Entity;
    import javax.persistence.Id;
    import javax.persistence.Table;
    import javax.persistence.Index;
    
    // username 컬럼에 대해 idx_username이라는 이름의 인덱스 생성.
    @Entity
    @Table(name = "user", indexes = {@Index(name = "idx_username", columnList = "username")})
    public class User {
        @Id
        private Long id;
    
        private String username;
        private String email;
    
        // Getters and Setters
    }
  ~~~
  - @Table 어노테이션에 indexes 속성을 추가하여 인덱스를 정의가능.
  - @Index 어노테이션
    - name 속성 : 인덱스 이름을 지정
    - columnList 속성 : 인덱스를 적용할 컬럼을 지정.

- 여러 개의 인덱스 생성가능.
  ~~~
    @Table(name = "user", indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_email", columnList = "email")
    })
    // ...
  ~~~
  

### < 클러스티드 인덱스와 넌클러스티드 인덱스 >
- 데이터베이스에서 인덱스의 두 가지 주요 유형이 클러스터드 인덱스와 비클러스터드 인덱스.
- 데이터가 저장되는 방식과 쿼리 성능에 미치는 영향에 따라 상이함.

1. **클러스터드 인덱스** (Clustered Index)
   > 클러스터드 인덱스는 데이터를 실제로 정렬하는 방식이기 때문에 데이터가 많이 변경되지 않고 조회가 범위 기반일 때 유리.
   > 테이블에서 주요 식별자로 자주 사용되는 컬럼에 대해 사용합니다. 보통 Primary Key나 Unique Key 필드에 사용.
   1) 물리적인 정렬
      - 데이터는 클러스터드 인덱스의 순서대로 저장되기 때문에, 인덱스와 데이터가 같은 구조를 가짐.
      - 데이터가 인덱스 순서대로 물리적으로 저장되는 방식으로 **저장 순서를 결정.**
   2) 하나의 테이블에 하나만 존재   
      각 테이블은 하나의 클러스터드 인덱스만 가질 수 있음. (클러스티드 인덱스 = PK)
   3) 수정 비용 발생 가능
      - 데이터를 삽입하거나 수정할 때 인덱스를 유지하기 위해 데이터를 재정렬해야 하므로 비용이 발생가능.
      - 수정 시 영향을 줄 수 있지만 조회 성능 향상가능.
   4) 빠른 범위 조회   
      WHERE 절에 **범위 조건(BETWEEN, >=, <=)을 사용하는 쿼리에서 매우 빠르게 동작.**
   
2. **비클러스티드 인덱스** (Non-clustered Index)
   > 비클러스터드 인덱스는 특정 컬럼에 대한 빠른 검색을 지원하며, 여러 개를 만들 수 있기 때문에 다양한 쿼리 성능 최적화에 유용.
    - 인덱스가 **데이터의 물리적인 순서와 별도로 존재**하는 방식.
   - 검색 성능 최적화를 위해 특정 컬럼에 추가적인 비클러스터드 인덱스 생성가능.
     > username이나 email 컬럼에 인덱스를 추가하면 해당 컬럼에 대한 검색 성능향상.
   - 여러 개의 인덱스를 사용하여 여러 필드에 대한 조회 성능을 높일 수 있습니다.
   - 비클러스터드 인덱스는 인덱스 파일과 실제 데이터 파일이 별개로 존재. 
   - 인덱스에는 데이터의 참조만 포함되어 있으며, 데이터의 실제 위치를 찾을 수 있는 포인터를 제공. 
   - 인덱스와 데이터가 분리되어 있어, 여러 개의 비클러스터드 인덱스를 생성가능.
   - 빠른 **단일 값 조회**
     - 주로 특정 값에 대한 빠른 조회가 필요한 경우 사용.
   - 범위 조회 성능
     - 클러스터드 인덱스보다 범위 조회 성능이 저하 가능. 
     - 여러 테이블을 조인하거나 여러 인덱스를 사용하는 경우 성능이 영향을 받을 수 있음.
   - 성능 향상
     - 자주 조회되는 컬럼에 인덱스를 추가하여 조회 성능 향상가능.
   - 수정 시 인덱스만 수정하므로 수정비용 낮음.
   - 일반적으로 더 많은 저장 공간 요구 가능.
   ~~~ 
   CREATE INDEX idx_username ON Users (username);   -- 비클러스터드 인덱스
   ~~~


---
## 15. Spring Data JPA에서 DTO Projection + FETCH JOIN으로 인한 QueryCreationException 해결
1. 발생오류
   ~~~
    Caused by: org.springframework.beans.factory.BeanCreationException: 
        Error creating bean with name 'productOptionRepository' defined in com.myecommerce.MyECommerce.repository.product.ProductOptionRepository defined in @EnableJpaRepositories declared on JpaRepositoriesRegistrar.EnableJpaRepositoriesConfiguration: Could not create query for public abstract java.util.Optional com.myecommerce.MyECommerce.repository.product.ProductOptionRepository.findByProductCodeAndOptionCodeOfOnSale(java.lang.String,java.lang.String); 
        Reason: Validation failed for query for method public abstract java.util.Optional com.myecommerce.MyECommerce.repository.product.ProductOptionRepository.findByProductCodeAndOptionCodeOfOnSale(java.lang.String,java.lang.String) at app//org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1808) at app//org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:601) at app//org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:523) at app//org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:336) at app//org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:289) at app//org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:334) at app//org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:199) at app//org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1573) at app//org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1519) at app//org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:913) at app//org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:791) ... 58 more    
    Caused by: org.springframework.data.repository.query.QueryCreationException: Could not create query for public abstract java.util.Optional com.myecommerce.MyECommerce.repository.product.ProductOptionRepository.findByProductCodeAndOptionCodeOfOnSale(java.lang.String,java.lang.String); Reason: Validation failed for query for method public abstract java.util.Optional com.myecommerce.MyECommerce.repository.product.ProductOptionRepository.findByProductCodeAndOptionCodeOfOnSale(java.lang.String,java.lang.String) at app//org.springframework.data.repository.query.QueryCreationException.create(QueryCreationException.java:101) at app//org.springframework.data.repository.core.support.QueryExecutorMethodInterceptor.lookupQuery(QueryExecutorMethodInterceptor.java:120) at app//org.springframework.data.repository.core.support.QueryExecutorMethodInterceptor.mapMethodsToQuery(QueryExecutorMethodInterceptor.java:104) at app//org.springframework.data.repository.core.support.QueryExecutorMethodInterceptor.lambda$new$0(QueryExecutorMethodInterceptor.java:92)
   ~~~

2. 발생원인
   - JPQL과 반환 타입이 서로 맞지 않아서 Repository 빈 생성 단계에서 실패
   1) JPQL 반환타입
      - 엔티티 반환
      - constructor expression (new)
      - 인터페이스 기반 Projection
   1) FETCH JOIN + DTO Projection은 같이 쓰면 안 됨   
      FETCH JOIN은 엔티티 그래프 로딩용.
      그런데 지금은 DTO Projection을 하고 있음.
      FETCH JOIN + DTO Projection은 JPQL 문법적으로 허용되지 않아,
      이 조합만으로도 Query validation 실패.

3. 해결방법
   1) 에러 발생 코드   
      JPQL에서 엔티티가 아닌 개별 컬럼들을 SELECT 하고 있는데
      반환 타입은 Optional<RedisCartDto> 입니다.
      JPA는 이걸 어떻게 RedisCartDto로 매핑해야 할지 모릅니다.
      ~~~
      @Query(" SELECT PRD.id as productId, PRD.code as productCode, PRD.name as productName, " +
           "        OPTION.id as optionId, OPTION.optionCode, OPTION.optionName, " +
           "        OPTION.price, OPTION.quantity " +
           " FROM Product PRD" +
           " INNER JOIN FETCH PRD.options OPTION" +
           " WHERE PRD.code = ?1" +
           " AND OPTION.optionCode = ?2" +
           " AND PRD.saleStatus = 'ON_SALE'")
      Optional<RedisCartDto> findByProductCodeAndOptionCodeOfOnSale(String productId, String optionId);
      ~~~
   2) 반영 코드
      ~~~
        // 상품ID에 해당하는 상품옵션 단건 조회
        @Query(" SELECT new com.myecommerce.MyECommerce.dto.cart.RedisCartDto( " +
                "       PRD.id, PRD.code, PRD.name, " +
                "       OPTION.id, OPTION.optionCode, OPTION.optionName, " +
                "       OPTION.price, OPTION.quantity, null" +
                ")" +
                " FROM Product PRD" +
                " INNER JOIN PRD.options OPTION" +
                " WHERE PRD.code = ?1" +
                " AND OPTION.optionCode = ?2" +
                " AND PRD.saleStatus = 'ON_SALE'")
        Optional<RedisCartDto> findByProductCodeAndOptionCodeOfOnSale(
                String productCdoe, String optionCode);
      ~~~


---
## 16. Entity와 초기화와 상태변경
JPA Entity의 컬렉션은 NPE 방지, 객체의 항상 유효한 상태 유지를 위해 
Entity 내부에서 초기화하는 것이 보편적인 관례이다.

1. Entity   
   Entity는 항상 유효한 상태여야 한다.
   생성되는 순간부터 의미적으로 완전해야 하므로, 컬렉션 초기화 책임은 Entity 자신에게 있다.
   초기화 된 컬렉션이 훨씬 안전하고 예측 가능하다.
   ~~~
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();
   ~~~
   
2. @Builder   
   Builder로 객체를 생성하면, 초기화한 필드값이 의미가 없어진다. null이 된다.
   필드 초기화를 유지하면서 @Builder 어노테이션을 사용하려면 
   아래와 같이 초기화 필드에 대해 Builder.Default 설정이 필요하다.
   ~~~
   @Builder
   public class Order {
        @Builder.Default
        private List<OrderItem> items = new ArrayList<>();
   }
   ~~~

3. @Setter
   상태 변경이 중요한 경우, 제거. 필요한 변경은 별도 메서드를 생성해 지원.

