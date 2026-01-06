# MY E-COMMERCE PROJECT TROUBLE SHOOTING
2025 간단한 이커머스 서비스를 만들기위한 고민

## < 목차 >
1. 요구사항 정의
   - 요구사항 명세서 조건을 디테일하게 정리하는 이유
2. 조회
   - 정확도순 검색 구현방법 - 좀 더 검색해보고 고민해볼 것
   - 상품목록 조회 정렬방법 변경의 필요성 - 고민 필요
3. 장바구니 기능구현
   - 이커머스 장바구니에서 Redis와 DB 선택 기준
   - 상품검색 - 상품을 DB에 저장해 조회하는 이유
   - 로그인 토큰 정보와 장바구니 정보 분리 권장
   - 장바구니에서 상품 품절
   - 장바구니 설계
   - 장바구니 구현
4. 주문 시 추가 프로세스
5. DTO
   - Dto에 Entity->Dto 변환 메서드를 제거한 이유
   - 계층별 별도 DTO 생성
6. 도메인 관리방법
7. 보안
   - 노출되면 안되는 정보 관리 방법 결정
   - Base64로 인코딩만 한 정보는 보안에 취약
   - 암호화 알고리즘
   - Jasypt 암호화 라이브러리
   - Jasypt 암호화 라이브러리 사용하기
8. 오류해결
   - 날짜 데이터 역직렬화 에러해결
   - JWT 토큰인증필터 추가 시 순환참조 오류발생


---
## 1. 요구사항 정의
### < 요구사항 명세서 조건을 디테일하게 정리하는 이유 >
구현할 기능들에 대한 조건들이 나열되어야 기능 구현을 올바르게 했는지 체크할 수 있기 때문입니다.


---
## 2. 조회
### < 정확도순 검색 구현방법 - 좀 더 검색해보고 고민해볼 것 >
1. Java에서 Comparator, Comparable을 이용해 정확도에 따라 내림차순 정렬.
2. AWS OpenSearch에서 preference node를 지정해 정확도 점수로 정렬.
3. MySQL에서 문자열 검색 결과를 적합도 순 정렬.
   - 참고블로그 : https://m.blog.naver.com/jellyqueen/220426967592

> mysql에서 문자열 검색 결과를 적합도 순 정렬로 선택.      
> pageable 객체를 전달해 쿼리를 조회하므로, 쿼리에서 이미 데이터가 정렬된 상태에서 5건의 결과를 가져와야한다.
> 따라서 java에서 like 검색해 일치하는 전체 데이터를 가져온 후 내림차순 정렬해 5건의 데이터를 가져오는 것보다 
  쿼리를 통해 정렬하는 것이 더 효율적이라 생각했다.


### < 상품목록 조회 정렬방법 변경의 필요성 - 고민 필요 >
1. 멘토의 코멘트   
   검색시마다 order, review 를 join 하여 count 쿼리하는 것은 전체적으로 성능이 많이 안좋고, 개선이 필요할 것 같습니다.    
   만일 상품 하나당 주문이 1만건이고, 리뷰도 1천개면 어떻게 될까요?   
   상품 목록 조회시 응답에는 리뷰 수, 평균 별점의 정보를 내려줄때도 마찬가지입니다.    
   조회 API 를 호출할때마다 매번 table join 해서 계산하는 방법보다 다른 방법을 찾아볼 수 있을까요?

> - 고민에 대한 결과
>


---
## 3. 장바구니 기능구현
### < 이커머스 장바구니에서 Redis와 DB 선택 기준 >
redis 캐시를 사용할지 DB에 사용자별 장바구니 테이블을 사용할지 결정필요.
장바구니 기능을 Redis로 구현할지 데이터베이스로 구현할지는 프로젝트의 요구 사항과 특성에 따라 선택.

> 생각해보면 로그인, 로그아웃 여부에 관계없이 장바구니 내역을 유지해야하므로 DB에 저장하는 것이 바람직해보인다.
하지만 redis 캐싱 기능을 사용해보기 위해 redis를 이용해 장바구니 기능을 개발해본다.
추후 로그인하지 않은 사용자는 redis 캐시를 이용해 장바구니를 지원하고,
로그인한 사용자는 장바구니 DB 정보를 이용해 장바구니를 지원하도록 변경해본다.

- 필요한 추가 구현 내용
  구매자가 장바구니에 상품을 담는 경우 한 달 동안 유지되어야 한다.

1. 데이터베이스로 구현 시 특징
   ***데이터의 일관성 및 영속성이 보장되는 환경이 필요하다면 데이터베이스를 선택하는 것이 더 나은 선택이 될 수 있음.***
    1) 영속성
        - 데이터베이스는 장바구니 데이터 영구 저장 가능. (데이터를 디스크에 영속적으로 저장)
        - 데이터가 지속적으로 보관되므로, 서버가 재시작되거나 장애가 발생해도 데이터를 안전하게 보호가능.
    2) 복잡한 쿼리 / 복잡한 비즈니스 로직처리
        - 장바구니에서 할인이 적용되는 상품이나 주문 처리, 가격 계산 등의 복잡한 로직을 처리하는 데 유리.
        - 장바구니에 대한 복잡한 관계형 데이터를 관리하고, 트랜잭션을 통해 데이터의 일관성을 유지가능.
    3) 일관성
        - 데이터베이스의 ACID 특성(Atomicity, Consistency, Isolation, Durability)을 보장해 데이터 일관성 유지.
        - 장바구니 외에도 다른 주문 및 결제 관련 데이터를 함께 처리하는 경우에 유리.
    4) 성능
        - 대규모 트래픽에서 성능 문제 발생가능.     
          수백 명의 사용자가 동시에 장바구니에 상품을 추가하거나 수정하는 경우,
          **데이터베이스는 동시성 처리에서 병목 현상이 발생가능.**
        - 동시 요청이 많아져서 읽기 쓰기 업데이트가 빈번하게 일어나는 경우 데이터베이스 성능 저하.
        - 데이터베이스는 디스크 기반 저장소로 읽기/쓰기 **속도가 상대적으로 느림.**
        - 데이터베이스가 트랜잭션을 보장하는 대신, 그만큼 성능에서 제한이 있을 수 있음.
    5) 부하
        - 모든 사용자의 장바구니 상태를 데이터베이스에서 관리하면, 서버 부하가 증가하고 데이터베이스의 성능 저하를 초래 가능.
    6) 확장성
        - 데이터베이스는 수직 확장(CPU, RAM 증설)과 수평 확장(샤딩 또는 분산 시스템) 모두 가능.
        - 분산 시스템을 구축하기 위해 추가적인 설정이 필요.
        - 여러 서버에 걸쳐 데이터를 일관되게 유지하는 것도 까다로울 수 있음.
        - 대규모 트래픽을 처리하려면 데이터베이스 성능을 최적화하는 것이 중요.
        - 캐싱, 인덱싱 등을 적절한 활용이 필요.
    7) 사용자 경험
        - 데이터베이스는 상대적으로 응답 시간이 느릴 수 있음.
        - 장바구니 데이터를 조회하거나 업데이트할 때 사용자 경험이 저하될 수 있음.
        - 특히 많은 사용자가 동시에 장바구니에 접근하면 성능 저하로 인해 페이지가 느려지거나 응답이 지연될 수 있음.

2. Redis로 구현 시 특징
   ***영속성이 중요하지 않고 세션 관리가 중요한 이커머스 시스템에서는 매우 유용.***
   장바구니가 비교적 간단한 데이터 구조인 경우 적절.
    1) 속도
        - Redis는 메모리 기반 데이터 저장소이므로 데이터 접근 속도가 매우 빠름.
        - 빠른 응답 속도, 실시간 처리가 중요한 경우 유용.
        - 장바구니와 같이 실시간으로 빈번하게 변경되는 데이터를 처리할 때 빠른 성능을 보장.
    2) 세션 관리
        - 장바구니는 보통 사용자의 세션과 연관되므로, Redis를 사용하면 세션 관리와 장바구니 저장을 동시에 처리할 수 있어 편리.
        - 세션 기반 관리가 필요한 경우.   
          ex) 사용자가 로그인 상태에서 장바구니를 유지해야 할 때
    3) 스케일링 (확장성)
        - Redis는 확장이 용이하고, 여러 서버에서 분산 처리가 가능하므로 높은 트래픽을 처리하기 유리.
        - Redis는 분산 처리가 용이
        - 수평 확장(sharding) 방식으로 쉽게 성능을 확장 가능.
        - 여러 Redis 인스턴스를 클러스터로 운영하면, 높은 트래픽을 효율적으로 처리가능.
        - Redis는 캐시와 세션 관리를 함께 할 수 있어 장바구니 기능을 구현하는 데 매우 적합.
    4) 영속성 부족
        - Redis는 기본적으로 메모리 기반이므로, 서버가 재시작 또는 장애 발생 시 데이터가 사라질 수 있음.
        - 위의 문제를 해결하기 위해 AOF (Append-Only File)나 RDB (Redis Database Snapshot) 기능을 통해 데이터를 디스크에 백업할 수 있지만,
          기본적으로는 영속성 측면에서 데이터베이스보다 약함.
        - 영속성이 중요하지 않거나 적당한 백업 전략을 적용할 수 있는 경우 유용.
    5) 복잡한 쿼리 제한 / 복잡한 비즈니스 로직처리
        - Redis는 NoSQL 데이터베이스라서 복잡한 쿼리나 트랜잭션 처리가 어려운 경우가 많음.
        - 장바구니 내에서 복잡한 상품 할인 계산이나 가격 변경을 처리하려면 외부 시스템과의 결합이 필요할 수 있음.
        - Redis는 주로 단순한 키-값 저장 방식에 특화.
        - 복잡한 쿼리나 비즈니스 로직을 처리하기에는 적합하지 않음.
          장바구니에서 할인 계산이나 가격 변동과 같은 복잡한 처리에는 제한될 수 있음.
        - Redis는 주로 데이터를 빠르게 읽고 쓰는 용도로 사용됨.
        - 비즈니스 로직은 애플리케이션에서 처리하거나 다른 시스템에서 관리해야 할 수 있음.
    6) 성능
        - Redis는 메모리 기반 데이터 저장소.
        - 데이터베이스에 비해 매우 빠른 읽기/쓰기 수행.
        - 장바구니처럼 빠른 반응 속도가 중요한 경우, Redis는 뛰어난 성능을 보임.
          사용자가 장바구니에 상품을 추가할 때, Redis는 메모리에서 즉시 처리할 수 있어 데이터베이스보다 훨씬 빠르게 응답가능.
    7) 사용자 경험
        - Redis를 사용하면 장바구니에 상품을 추가하거나 변경하는 작업이 즉시 반영됨.
        - 빠른 응답 속도 덕분에 사용자 경험이 매우 향상됨.
          사용자가 장바구니에서 상품을 추가하거나 삭제할 때 빠르게 화면에 반영.

3. 혼합해 사용   
   Redis는 장바구니 데이터를 캐시 용도로 사용하고,
   데이터베이스는 최종적인 영속적 저장소로 사용하는 방식으로
   두 기술을 혼합해 사용하는 경우도 많음.


### < 상품검색 - 상품을 DB에 저장해 조회하는 이유 >
- 상품 데이터를 DB에 저장하는 이유   
  상품검색의 경우 api 요청이 가장 많으니 조회 성능이 무엇보다 중요한데 redis에 저장하지 않고 데이터베이스에 저장한다.   
  이는 상품 검색은 주로 대규모 데이터에 대한 복잡한 쿼리와 정확한 결과를 요구하기 때문에
  단순히 성능만 고려해서 Redis를 사용하는 것이 항상 적합하지 않을 수 있기 때문이다.   
  결과적으로 상품 검색 기능에 있어 조회 성능이 매우 중요하지만 데이터베이스를 사용하는 이유는 
  복잡한 검색 쿼리, 데이터의 일관성 유지, 영속성 보장, 그리고 인덱스 활용 등이 매우 중요한 역할을 하기 때문.

1. 상품 데이터의 복잡성
   - 상품 검색은 보통 **다양한 필터링 조건이나 정렬을 요구.**   
     Redis는 주로 단순한 키-값 저장소로, 복잡한 쿼리나 여러 조건을 조합한 검색에 최적화되어 있지 않음.
   - 데이터베이스는 SQL을 사용하여 복잡한 JOIN이나 서브쿼리, 인덱스 등을 활용하여 효율적인 검색을 지원.
     따라서 복잡한 쿼리나 집계 작업이 필요한 상품 검색에는 데이터베이스가 더 유리.
2. 데이터의 일관성 유지
   - 상품 데이터는 일관성이 매우 중요.
   - 가격 변동, 재고 수량 업데이트, 할인 적용 등과 같은 사항은 실시간으로 반영되어야 함.
   - Redis는 기본적으로 메모리 기반이기 때문에 데이터의 일관성을 보장하기 어렵고
     영속성을 위해서는 별도의 설정 필요하므로 서버가 재시작되거나 장애가 발생할 경우, 데이터 손실 발생가능.
   - 데이터베이스는 ACID(Atomicity, Consistency, Isolation, Durability) 특성을 보장하여,
     데이터 일관성과 영속성을 제공하므로 상품에 대한 변경 사항을 일관되게 관리가능.
3. 실시간 데이터 업데이트
   - 상품 가격, 재고, 할인 등의 정보는 자주 변경됨.
     이를 실시간으로 반영하려면, 데이터베이스에서 직접 조회하거나 변경 사항을 처리하는 것이 더 적합.
   - Redis는 주로 읽기 전용 캐시로 사용되며, 데이터베이스와 연동하여 데이터 캐싱을 수행가능.
   - 상품 검색을 위한 데이터 자체를 Redis에 저장하고
     그 데이터를 실시간으로 반영하는 것은 복잡한 관리가 필요하고, 영속성 문제가 발생가능.
4. 데이터베이스의 인덱싱
   - 데이터베이스는 상품 데이터에 인덱스를 추가하여 검색 성능을 최적화 가능.
   - Redis는 인덱싱 기능 부족.
     검색 성능을 높이려면 기존 데이터를 메모리에 모두 로드해야 하므로 메모리 사용량이 급증가능.
   - 대규모 상품 데이터에서는 Redis를 사용하는 것보다는 데이터베이스에서 인덱스를 활용한 검색이 더 효율적.
5. 검색 기능의 정확도
   - 상품 검색에서는 정확한 검색 결과와 검색어에 대한 정확한 매칭이 중요.
     사용자가 특정 키워드를 검색하면, 정확한 상품이 검색 결과로 나와야 함.
   - 데이터베이스는 SQL과 같은 쿼리 언어를 사용해 정확한 검색을 지원.
     정규 표현식이나 LIKE 등의 문자열 매칭 기능을 활용하여 세밀한 검색 구현가능.
   - Redis는 정확한 텍스트 검색에 대해 제한적.
     검색어 자동완성이나 복잡한 필터링 기능을 구현하려면 별도의 검색 엔진을 도입하는 것이 필요.
6. 서버 부하 분산
   - 상품 검색 트래픽은 높고 빈번하게 발생가능.
   - Redis를 캐시 용도로 사용하는 방법도 있지만 **데이터베이스가 기본적으로 상품 데이터를 저장**하고
     검색을 위한 최적화가 되어 있는 상태에서 **Redis를 캐시 계층으로 추가하는 것이 이상적.**
   - 데이터베이스에서 최적화된 쿼리와 인덱스를 사용하여 검색을 빠르게 처리하고,
     이를 Redis에 캐시하여 반복적인 검색 요청에 빠르게 응답하는 방식이 효율적.
7. 검색 관련 전문 기술 활용
   - 상품 검색에 대해 더 높은 성능과 정확도를 원할 경우, **검색 전문 기술인 Elasticsearch나 Solr 같은 검색 엔진을 사용**하는 것도 하나의 방법.
     이러한 검색 엔진은 대규모 데이터에서 빠르고 정확한 텍스트 검색을 지원.
   - Elasticsearch는 분산형 검색 엔진으로, 복잡한 텍스트 기반 검색에서 매우 높은 성능을 발휘.
   - 이러한 시스템은 상품 검색과 같은 대규모 데이터를 처리할 때 최적화된 성능을 제공하며, Redis는 이 시스템을 캐시로 사용가능.

- 데이터베이스를 사용하면서 검색성능 향상시키는 방법
  1) 데이터베이스에서 검색 성능 최적화   
     데이터베이스에서 상품 데이터를 저장하여 검색 성능을 최적화하려면 인덱스나 정규화된 데이터 모델을 사용하며,
     캐싱 계층으로 Redis를 두어 자주 조회되는 데이터를 빠르게 응답하는 방식이 이상적.
     Elasticsearch와 같은 전문 검색 엔진을 도입하면 검색 성능을 더욱 강화할 수 있음.


### < 로그인 토큰 정보와 장바구니 정보 분리 권장 >
- Redis 저장 정보 분리 권장     
  로그인 토큰 정보와 장바구니 정보를 같은 Redis 인스턴스에 저장하는 것은 가능하지만, 분리를 추천.   
  1) 명확한 네이밍 규칙과 데이터 구조를 통해 키 충돌을 방지 및 관리 용이.
  2) 로그인 토큰과 장바구니 데이터의 TTL을 다르게 설정해, 유효 기간에 맞는 데이터 삭제 가능.
  3) 보안과 성능을 고려하여 적절한 구조와 정책 적용가능.
  > **로그인 토큰과 장바구니 데이터를 동일한 Redis 인스턴스에 저장하되
    명확한 네이밍과 구조적인 분리**를 통해 관리하는 것이 가장 효율적이고 안전.

1. 데이터의 분리   
   동일한 Redis 인스턴스에 로그인 토큰 정보와 장바구니 정보를 저장가능.
   하지만 **로그인 토큰, 장바구니 정보를 명확히 분리**하는 것이 중요.   
   이렇게 분리하지 않으면, 나중에 **데이터 충돌이나 관리의 어려움 발생가능.**
   - 적용 방법   
     1) 네임스페이스 활용   
        Redis에서는 :, _ 등의 구분자를 사용하여 키를 네임스페이스처럼 관리가능.   
        session과 cart 키를 명확히 구분할 수 있어 데이터 충돌을 방지가능.   
     2) Hash 구조 사용   
        Redis에서 Hash 구조 사용 시, 한 사용자에 대한 여러 관련 데이터를 하나의 엔티티로 관리가능.   
        로그인 토큰 정보와 장바구니 정보를 같은 Hash에서 관리할 수도 있지만, 구분된 별도의 Hash로 관리하는 것이 좋음.
   ~~~
    session:token:<user_token> : 로그인 토큰 정보 저장
    cart:user:<user_id> : 장바구니 정보 저장     
   ~~~

2. TTL (Time To Live) 관리
   > 결정한 사항
   > 1. 로그인 토큰의 TTL   
   >    로그인 토큰의 TTL을 사용자가 로그인한 시간부터 일정 기간 동안만 유효하게 설정가능.   
   >    ex) 로그인 후 30분 동안 유효한 토큰.
   > 2. 장바구니 TTL   
   >    장바구니는 사용자가 로그인한 상태에서만 유효한 정보를 관리가능.
   >    사용자가 일정 시간 동안 활동하지 않으면 장바구니 정보를 자동으로 삭제할 수 있도록 TTL을 설정하는 것이 좋음.   
   >    ex) 사용자가 장바구니를 30분 동안 수정하지 않으면 삭제.
   1) 데이터 크기와 관리
      - 로그인 토큰 데이터는 보통 간단하고 작은 크기를 가집니다.
      - 장바구니 데이터는 여러 상품을 포함하고 있으며, 장바구니에 많은 상품을 담는 경우 그 크기가 커질 수 있음.
        따라서 **장바구니 데이터의 메모리 사용량을 고려하고 Redis 메모리 최적화(예: Redis의 maxmemory 설정, LRU 캐시 정책 등)를 활용이 필요.**
   2) 데이터 유지 기간의 차이
      - 로그인 토큰은 비교적 짧은 시간 동안만 유효하므로, TTL을 사용하여 만료되도록 설정하는 것이 적절.
      - 장바구니는 사용자 활동에 따라 유효 기간이 달라질 수 있으며 장바구니 데이터가 오래 지속되거나 사용자가 로그아웃하면 삭제되어야 할 수 있음.
      - 로그인 토큰, 장바구니는 TTL 값을 별도로 설정하여 각기 다른 관리 방식 적용 필요.
   3) 장바구니와 세션 데이터의 관계
      - 장바구니 정보는 보통 사용자 세션에 종속됨. 사용자가 로그인할 때 세션이 활성화.
      - 장바구니는 세션에 대한 데이터로 처리됨.
      - Redis에서 세션과 장바구니를 함께 관리하면 사용자 경험 향상가능.
      - 로그인 시, 세션 키(session:token:<user_token>)와 장바구니 키(cart:user:<user_id>)가 연결된 형태로 데이터를 관리하는 것이 바람직.
      - 예를 들어, 사용자가 로그인하면 session 정보와 cart 정보를 함께 Redis에 저장하고, 세션이 만료되면 장바구니도 삭제될 수 있도록 관리가능.
   4) 성능 고려
      - 분리된 데이터 구조   
        로그인 토큰과 장바구니 데이터를 같은 Redis 인스턴스에 저장하되
        데이터를 구조적으로 분리하고, 각 데이터의 TTL과 접근 빈도를 관리하면 성능에 유리.
      - 키 네이밍 규칙   
        명확한 네이밍 규칙을 사용하여 Redis에서 데이터를 조회할 때 불필요한 오버헤드를 줄이고, 키 충돌을 방지하는 것이 중요.
   5) 보안 고려
      - 토큰 보안   
        로그인 토큰은 보통 민감한 정보를 담고 있기 때문에, Redis에 저장될 때 암호화하거나 세션 보안을 고려해야 할 수 있음.
      - 장바구니 보안   
        장바구니에 담긴 상품 정보도 개인화된 데이터일 수 있으므로, 세션 쿠키와 함께 적절한 보안 조치를 취하는 것이 좋음.


### < 장바구니에서 상품 품절 >
장바구니에 담긴 상품을 조회할 때마다 실시간으로 판매 가능 수량을 확인하고, 
품절 상태를 업데이트하는 방식으로 구현됨.

- 품절된 상품을 장바구니에서 처리하는 방법   
  **판매 가능 수량을 실시간으로 조회**하여, **장바구니에 담을 때나 조회할 때 품절 상태를 반영**하는 방식으로 구현가능.
  **Redis의 Hash 구조를 활용하여 장바구니 데이터를 관리**하고, 상품의 재고 상태를 Redis에서 직접 조회하여 
  품절 상품을 처리하는 방법이 가장 효율적.
  1) 장바구니에 상품 추가 시   
     재고를 확인헤 품절된 상품은 추가하지 않거나 품절 상태로 처리.
  2) 장바구니를 조회 시   
     상품의 품절여부 우선 확인 후 상품상태 업데이트.
  3) 실시간 동기화   
     필요 시 Redis Pub/Sub을 활용하여 실시간으로 품절 상품 동기화 가능.


### < 장바구니 설계 >
1. 장바구니 보편적인 기본 프로세스 (고려사항)
   1) 판매 가능 수량 변경 시 (예: 상품이 팔리거나 재고가 추가될 때) 이를 데이터베이스에 반영하고, Redis에 해당 값을 업데이트.
   2) 장바구니에 상품을 추가하거나 조회할 때, Redis에서 판매 가능 수량을 조회.
   3) Redis에서 판매 가능 수량을 찾을 수 없거나 오래된 데이터일 경우, 데이터베이스에서 다시 조회하여 Redis에 최신 값을 저장.

2. Redis 장바구니 데이터 모델 및 상품 재고 관리 설계
   1) 장바구니 데이터 모델 정의   
      장바구니에 담긴 상품 정보는 Redis Hash 구조로 저장가능.
      ~~~
      // sold_out 필드는 품절 여부, quantity는 사용자가 장바구니에 담은 상품 수량.
      cart:user:<user_id>
      - product_id_1: { "quantity": 2, "sold_out": false }
      - product_id_2: { "quantity": 1, "sold_out": false }
      - product_id_3: { "quantity": 1, "sold_out": true }
      ~~~
   2) 판매 가능 수량 관리
      - 판매 가능 수량은 상품의 재고를 나타내는 정보로, **보통 데이터베이스에 저장**됨.
      - **Redis에는 상품별 판매 가능 수량을 관리하는 별도의 키를 두고, 재고가 변경될 때마다 업데이트.**
      - **상품 판매가능수량 정보를 Redis에 별도 저장하고, 장바구니를 조회하거나 업데이트할 때마다 이를 확인가능.**
        사용자가 상품을 장바구니에 추가 시 Redis에서 해당 상품의 판매 가능 수량을 조회.
        수량이 0이면 해당 상품을 품절 상태로 업데이트하고, 장바구니에 추가하지 않거나 품절 상태로 저장.
      - 사용자가 장바구니에 상품을 추가할 때, 먼저 해당 상품의 판매 가능 수량을 확인 후 추가.
      ~~~
       stock:product:<product_id>  # 상품의 판매 가능 수량
      ~~~
   3) 자동 품절 업데이트 (옵션)
      - 판매 가능 수량은 실시간으로 변동가능.
      - 판매 가능 수량이 0으로 변경될 때 장바구니 내 품절 처리를 자동으로 하는 방법도 고려가능.
      - Redis Pub/Sub을 활용하여 재고 변화에 따른 장바구니 품절 상태를 자동으로 업데이트 가능.
      - 구현방법
        - 재고 변경 이벤트가 발생할 시 마다 해당 상품의 품절 상태를 Redis Pub/Sub 메시지로 알림.
        - 구독자가 해당 메시지를 받으면 장바구니에 있는 상품의 품절 상태를 자동으로 업데이트.
        - 더 복잡하지만, 장바구니에 담긴 상품의 품절 상태를 실시간으로 동기화가능.
   4) 장바구니에서 품절된 상품 삭제 (옵션) -> 구현에서 제외
      - 장바구니에 담긴 상품의 품절 시 해당 상품을 자동으로 삭제하거나 품절 처리 가능.
      - 이는 사용자의 UX를 개선할 수 있으며, 품절된 상품을 장바구니에서 쉽게 제외가능.
   5) Redis 상품판매가능수량 관리
      - 상품 판매 가능 수량 정보를 Redis에서 관리하려면 판매 가능 수량을 데이터베이스에서 가져와 Redis에 저장하고
        Redis에서 효율적으로 관리해야함.
      - 이를 통해 장바구니를 조회하거나 업데이트할 때마다 Redis에서 판매 가능 수량을 빠르게 확인가능.


### < 장바구니 구현 >
> 장바구니 재고 관리 구현 결정 사항 (Redis + DB)      
> 아래의 방식으로 Redis와 데이터베이스를 적절히 활용하면, 장바구니 기능 구현 시 성능과 일관성을 유지가능.
> 1) 데이터베이스에서 판매 가능 수량을 조회하고, 이를 Redis에 캐시.
> 2) 장바구니에 상품을 추가할 때마다 Redis에서 판매 가능 수량을 조회하고 품절 여부를 확인.
> 3) 판매 시, Redis와 데이터베이스에서 재고를 차감하고 동기화.
> 4) Redis와 데이터베이스의 동기화를 주기적으로 관리하거나, TTL을 사용하여 자동 갱신.

1. 상품 판매 가능 수량을 데이터베이스에서 가져오는 과정
    - 판매 가능 수량은 보통 데이터베이스에 저장되어 있음.
    - 장바구니 기능에서 상품의 판매 가능 수량을 관리하려면
    - **Redis에 저장된 값을 최신 상태로 유지**해야 하므로, **데이터베이스에서 해당 정보를 주기적으로 가져와 Redis에 저장**하는 과정 필요.

2. Redis에 상품 판매 가능 수량 저장하기
   - Redis는 빠른 조회 성능을 제공하는 메모리 기반 저장소이기 때문에
     장바구니에 담을 때마다 판매 가능 수량을 실시간으로 확인가능.
   - 이를 위해 상품의 판매 가능 수량을 Redis에 캐시로 저장하고, 데이터베이스와 동기화 가능.

3. 판매 가능 수량을 Redis에 저장하는 방법
   - 상품 판매 가능 수량은 Redis의 문자열이나 해시로 저장가능.
   - 상품별 판매 가능 수량을 Redis에 key-value 형식으로 저장가능.
   ~~~
   //stock:product:<product_id>라는 키에 대해 값으로 판매 가능 수량을 설정.
   Redis Key: stock:product:<product_id>
   Redis Value: 판매 가능 수량 (예: 100)
   ~~~

4. Redis에 저장된 판매 가능 수량 동기화
   - Redis에 저장된 판매 가능 수량이 오래되거나 유효하지 않다면, 이를 주기적으로 데이터베이스와 동기화해 필요.
   - Redis에서 판매 가능 수량을 관리하고 있다면, TTL(Time To Live)을 설정하여
     일정 시간이 지난 후 자동으로 데이터베이스에서 최신 값을 가져와 갱신하도록 할 수 있음.
   - 해당 방법 사용 시 Redis와 데이터베이스 간의 동기화를 적절히 유지하면서도 빠르게 조회가능.

5. 데이터베이스에서 Redis로 초기화 (배치 방식)
   - 상품 수가 많다면, 모든 상품에 대해 판매 가능 수량을 Redis에 미리 배치로 로딩하는 방법도 고려가능.
   - 서버가 시작될 때 모든 상품의 판매 가능 수량을 한 번에 Redis에 로딩하는 방식.
   - 한 번에 데이터베이스에서 모든 상품의 판매 가능 수량을 가져와 Redis에 초기화 시 각 요청마다 빠르게 데이터를 조회가능.


---
## 4. 주문 시 추가 프로세스
- 추가가능 : 결제 완료 시 결제확인 이메일 발송
- 추가기능 : PG 결제 외 추가 결제수단 사용가능할 것


---
## 5. DTO
### < Dto에 Entity->Dto 변환 메서드를 제거한 이유 >
기존 Dto->Entity로 변환하는 메서드를 작성했다.   
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

1. **DTO에 ENTITY->DTO 변환 메서드를 작성하면 안되는 이유.**   
   사용에 불편하니 올바른 코드는 아닌 것 같은데 코드를 어떻게 잘못 작성한건지 이유를 모르겠어서 찾아봤다.   
   1) Entity를 DTO로 변환하는 메서드를 DTO 클래스에 작성하지 않는 이유   
      책임 분리, 유지보수성, 그리고 설계의 일관성을 고려한 원칙에 기반한다.    
      DTO 클래스는 일반적으로 단순한 데이터 전송 객체로 설계되어야한다.   
      변환 로직을 DTO에 포함시키는 것은 **단일 책임 원칙(SRP)과 같은 객체지향 설계 원칙을 위반**할 수 있다.   
      DTO에 작성한 **Entity->Dto 변환 로직은 서비스 계층이나 별도의 변환(Mapper) 클래스에서 처리하는 것이 더 적절**하다는 결과를 얻었다.
      또한 변환 로직을 분리하면 코드의 응집력이 높아지고, 확장성과 유지보수성도 향상된다.
   2) DTO와 변환로직 분리 시 이점(?)
      1. 단일책임원칙
      2. 책임분리
      3. 유지보수성 및 테스트 용이성
      4. DTO는 외부와의 인터페이스
      5. 객체지향 설계 원칙에 따른 최적화

2. 위반내용
   1) 단일 책임 원칙 (Single Responsibility Principle) 위반    
      DTO (Data Transfer Object) 는 데이터를 전송하는 역할을 한다.    
      일반적으로 DTO는 데이터를 표현하기 위한 단순한 POJO(Plain Old Java Object)로, 
      getter, setter, 생성자 및 필드만 포함되어야 한다.   
      DTO 클래스에 변환 로직(Entity에서 DTO로 변환)을 포함시키면, 그 클래스는 두 가지 책임을 가지게 된다.  
      이러한 **책임의 중복은 DTO 클래스를 복잡하게 만들며, 변경에 취약하게 만든다.**    
      설계가 복잡해지므로, 단일 책임 원칙(SRP)을 지키기 위해 변환 로직은 별도로 분리하는 것이 좋다.
      - 데이터를 전송하는 책임
      - 데이터를 변환하는 책임      
   2) 책임 분리 (Separation of Concerns)   
      DTO는 단순한 데이터 객체일 뿐, 데이터의 변환은 비즈니스 로직에 속한다.   
      DTO 클래스가 변환 로직을 포함하게 되면, 영속성 계층(Entity)과 비즈니스 계층(DTO 변환)이 결합되어 책임이 모호해질 수 있다.   
      변환 로직을 서비스 계층이나 매퍼 클래스(예: ModelMapper, MapStruct)에서 처리하면, **각 
      계층의 책임이 명확히 분리되고, 코드를 변경하거나 확장할 때 문제가 덜 발생**한다.
   3) 유지보수성 및 확장성   
      **변환 로직을 DTO에 넣으면, 데이터 변환 방식이 변경될 때마다 DTO 클래스의 수정이 필요**해진다.    
      예를 들어, 엔티티 클래스가 변경되면, 그에 맞는 DTO 변환 로직을 DTO 클래스에서 수정해야 할 수 있다.   
      이를 해결하기 위해 **변환 로직을 서비스 계층이나 매퍼 클래스에서 분리하면, 
      엔티티와 DTO 간 변환 로직을 한 곳에서 관리할 수 있어, 유지보수성이 높아지고, 향후 확장에도 유리**하다.
   4) DTO는 외부와의 인터페이스   
      **DTO**는 데이터 전송을 위한 객체이다.   
      **외부 시스템이나 클라이언트와의 데이터 교환을 목적으로 사용**된다. 
      변환 로직을 DTO에 넣으면, DTO가 내부 비즈니스 로직과 결합되는 문제가 발생할 수 있다.   
      예를 들어, **DTO에 변환 메서드를 추가하면, DTO를 사용하는 서비스에서 DTO가 엔티티와의 관계를 알고 있어야 한다.**   
      이는 DTO의 본래 역할인 단순한 데이터 전송과 충돌하며, **코드의 결합도가 높아**진다.
   5) 객체지향 설계 원칙에 따른 최적화   
      변환 로직을 서비스 클래스나 매퍼 클래스에 두면, 각 클래스가 자기 역할에 충실하게 된다.   
      **DTO는 데이터를 전송하는 역할에만 집중하고, Service나 Mapper는 변환 로직을 맡게된다.**
      또한, ModelMapper, MapStruct 등의 라이브러리를 사용하면, 변환 로직을 더 효율적으로 관리하고, 자동화할 수 있어 코드가 더 간결하고 유지보수하기 좋다.

3. **대안방법**   
   - Entity를 DTO로 변환하는 메서드를 DTO 클래스에 작성하지 않는 이유는 책임 분리, 유지보수성 향상, 설계의 일관성 등을 고려한 객체지향 설계 원칙에 기인한다.  
     DTO는 단순한 데이터 전송 객체로, 변환 로직은 서비스 계층이나 매퍼 클래스에서 처리하는 것이 더 나은 설계이다.    
     변환 로직을 분리하면 코드의 응집력이 높아지고, 확장성과 유지보수성도 향상된다.
   - DTO 변환을 별도의 서비스계층 or 유틸리티 클래스에 작성하여 Entity와 DTO 변환을 처리.
   1) 서비스계층에서 변환   
      service에서 Entity를 DTO로 변환가능.
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
   2) Mapper 클래스 사용
      1. 수동으로 변환 메서드 작성하기: 매핑을 직접 구현하는 방법.   
         변환 로직을 직접 작성하여 Entity와 DTO 간 변환을 처리. 
         유연성과 커스터마이징이 가능하지만, 코드가 다소 길어질 수 있음.
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
                // ...
  
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
                // ...
 
                return member;
            }
         }
         ~~~
      2) 라이브러리 사용하기   
         ModelMapper, MapStruct 등과 같은 라이브러리를 사용해 변환 로직을 외부 클래스로 분리하는 방법.
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


### < 계층별 별도 DTO 생성 >
> 고민과 결정 (설계방식결정)    
> Controller -> Service로 데이터 전달 시,
> Request DTO를 별도의 Service 전용 DTO로 변환하는 방식에 대해 고민했다.
> 그러나 현재 프로젝트에서는 하나의 Service가 하나의 Controller에서만 사용되는 구조이다.
> **Controller와 Service의 책임이 1:1로 매칭**되므로 
  Service가 필요로 하는 형태의 데이터를 Controller에서 직접 전달받는 것에 문제가 없다고 판단된다.
> 따라서 **Controller -> Service로 데이터를 전달하는 과정에 추가적인 DTO 변환 작업 없이 Request DTO를 그대로 전달**하기로 결정했다.

1. Controller-Service–Repository 책임 분리
   Service 계층의 Controller 종속성 문제와 데이터 전달 방식
   Service가 전체적인 비즈니스 플로우를 관리하고,
   Repository는 온전히 데이터베이스와 소통하는 역할만 수행.   
   이 때 **Service가 Controller에 종속적이다.**
   이는 Controller가 받아오는 데이터에 따라 Service가 받는 데이터 형태가 결정되기 때문이다.
   따라서 **Service가 원하는 방식으로 정보를 받지 못할 수 있다.**
   만약 하나의 서비스가 여러 컨트롤러에서 호출된다면,
   서비스가 Entity를 받아야 컨트롤러별 API 형태와 관계 없이 동작할 것이다.

2. DTO → 도메인 변환 책임을 Service에 두는 설계   
   Service에서 도메인으로 변환 후 Repository로 전달.   
   즉, Service에서 DTO를 도메인(Entity)으로 변환하고, Repository는 도메인을 받고 도메인을 응답한다.
   이렇게 되면 Repository는 DTO <-> Entity 변환 작업은 하지 않고 DB 관련 작업만 수행하게된다.

- 참고블로그   
  https://shyun00.tistory.com/214


---
## 6. 도메인 관리방법
DB에서 공통코드, Enum 두 방법 모두 장단점이 있기 때문에, 프로젝트의 요구 사항과 운영 환경에 맞게 선택해야한다.
DB에서 공통코드, Enum 사이에서 가장 중요한 부분은 **얼마나 자주 추가 및 변경이 일어나는가?** 이다.   
<br>
동적인 값 변경이 필요하고, 운영 환경에서 관리가 편리해야 한다면 DB에서 관리하는 것이 유리하다.
고정된 값이고, 타입 안정성과 성능이 중요한 경우 Enum을 사용하는 것이 더 좋다.
따라서, 값이 자주 변경될 가능성이 있거나 외부 시스템과 연동하는 경우라면 DB 관리가 더 유리하고, 
그렇지 않다면 Enum으로 관리하는 것이 좋다.   
<br>
또한 상태 값이 많이 필요하다면 많은 Enum 파일을 관리하는 것은 비효율적일 수 있기 때문에
도메인별로 너무 많은 Enum을 생성해 관리하는 대신 설정 파일이나 DB 테이블로 관리하는 방법도 고려할 수 있다.    
이렇게 하면 코드베이스의 Enum 파일 수를 줄일 수 있으며, 
상태 값이 변경될 때마다 애플리케이션을 재배포하지 않고도 시스템의 동작을 수정할 수 있다.   

> 고민과 결정
> 초기 설계 시 공통코드로 관리할지 enum으로 관리할지 고민했다.      
  그런데 추후 도메인이 계속 추가된다면 enum 클래스가 계속 추가되는 것 보다 DB에서 공통코드로 관리하는 것이 더 좋을 것이라고 생각했기 때문이다.
>
> 현재 프로젝트의 경우 도메인으로 관리해야하는 부분은 회원권한분류, 상품분류, 주문상태 정도이다.   
  해당 도메인들은 추가, 변경이 발생할 수는 있어도 자주 발생할 일은 없다.
> 
> 생각해보니 소규모 프로젝트로 추가적 확장의 여지가 거의 없고 해당 도메인들은 고정된 값이기에 타입 안정성의 정점을 가지는 ENUM 으로 관리하는 게 합리적일 수 있을 것으로 판단. 
  따라서 Enum으로 관리하도록 변경.

1. Java Enum으로 관리
   1) 타입 안정성   
      Enum을 사용하면 컴파일 시점에서 타입 체크가 이루어지기 때문에 오류를 미리 방지할 수 있습니다.
   2) 성능   
      Enum은 메모리에 상주하는 값이므로 DB 접근 없이 빠르게 값을 사용할 수 있습니다.
   3) 단순성   
      코드 내에서 관리되므로 DB와의 동기화 문제나 관리가 비교적 단순합니다.
   3) 유연성 부족   
      새로운 주문 상태가 필요할 때마다 애플리케이션을 수정하고 배포해야 하므로 유연성이 떨어집니다.
   4) 다국어 지원    
      Enum 자체에는 다국어 처리가 어렵기 때문에, 다국어를 지원하려면 별도로 추가적인 관리가 필요할 수 있습니다.

2. DB에서 공통코드로 관리
   1) 유연성   
      새로운 주문 상태나 코드 값을 추가하거나 변경할 때, 애플리케이션을 재배포하지 않고 DB만 수정하면 되기 때문에 유연합니다.
   2) 관리 용이성   
      관리자가 DB에서 직접 상태 코드나 값을 수정할 수 있으므로 비즈니스 측면에서 더 편리할 수 있습니다.    
      예를 들어, 주문 상태가 바뀔 때마다 코드 추가나 수정이 필요할 때 유용합니다.
   3) 다국어 지원   
      주문 상태에 대한 이름을 DB에서 관리하면서 다국어로 지원할 수 있습니다.   
      상태 코드만 DB에 두고, 각 언어에 맞는 상태명을 별도로 관리할 수 있습니다.   
   4) 추가적인 DB 접근 필요   
      상태 값을 조회할 때마다 DB에 접근해야 하므로 성능에 영향을 미칠 수 있습니다.
   5) 복잡성   
      DB에서 상태값을 관리하고, 이를 자바 코드와 동기화하는 작업이 추가적으로 필요합니다.    
      코드와 DB 상태가 일치하는지 관리해야 할 책임이 개발자에게 있습니다.


---
## 7. 보안
### < 노출되면 안되는 정보 관리 방법 결정 >
1. 환경 변수 사용 
   1) 설정 파일을 통한 노출을 방지.   
      코드베이스에서 민감한 정보를 직접 다루지 않기 때문에 코드가 노출되어도 비밀 정보가 유출되지 않음.
   2) **환경에 따라 다르게 설정 가능.**   
      서버의 접근 권한을 통해 환경 변수를 볼 수 있는 위험이 있으며, 
      관리하는 서버(운영 체제)에 대한 보안이 중요.   
      Java에서는 System.getenv("VARIABLE_NAME")를 사용하여 환경 변수 값을 읽을 수 있음.
   
2. 보안 파일 또는 키 관리 서비스 사용   
   클라우드 제공자들(AWS, GCP, Azure 등)은 비밀 키를 안전하게 관리할 수 있는 여러 보안 서비스들을 제공하기에 매우 안전.   
   (민감한 정보를 안전하게 저장하고 관리할 수 있으며, 필요한 경우에만 접근할 수 있도록 설정가능.)
   - 보안 서비스   
     ex) AWS의 Secrets Manager, Azure Key Vault 등
     1) 자동화된 관리   
        비밀번호, API 키, 인증서와 같은 민감한 데이터를 안전하게 저장하고, 애플리케이션에서 이를 동적으로 로드 가능.
     2) 암호화   
        비밀 정보를 자동으로 암호화하여 저장.   
        또한, 복호화 키를 안전하게 관리하고, 비밀 데이터에 대한 액세스 제어 가능.
     3) 접근 제어   
        각 애플리케이션이나 사용자에게 특정 비밀 정보에 대한 액세스를 제어할 수 있으며, 최소 권한 원칙(least privilege)을 따를 수 있음.
     4) 자동화된 로테이션   
        비밀번호나 API 키와 같은 민감한 정보의 주기를 설정하여 자동으로 교체 가능.
     5) 감사 로그   
        언제, 누가, 어떤 정보에 접근했는지에 대한 감사 로그를 자동으로 기록.

3. 암호화
   1) application.properties 파일에 민감정보 저장 시 보안 문제   
      application.properties 파일은 코드베이스에 포함되므로 민감한 정보를 저장하는 것은 보안상 좋지 않음.
      소스 코드가 외부에 노출되거나 관리되지 않은 상태에서 해당 정보가 유출될 수 있기 떄문.
   2) application.properties 파일 민감정보 암/복호화   
      - 만약 application.properties에 저장할 수밖에 없다면, 
        해당 정보를 암호화해 저장하고 프로그램 실행 중에 복호화하는 방법 사용 가능.   
      - 암호화된 설정 파일을 사용하는 방법도 가능하지만, 
        이 방법은 관리가 복잡하고 키 관리가 제대로 이루어지지 않으면 여전히 안전하지 않을 수 있음.
        또한 암호화된 파일을 읽을 수 있는 애플리케이션이 필요하고, 그 암호화 키가 유출되지 않도록 관리가 필요함.

4. **Spring Boot의 @Value와 application.yml 활용**   
   Spring Boot에서는 application.yml이나 application.properties 파일에 민감한 정보를 저장하는 것은 피하고
   대신, 보안 정보를 외부 환경 변수나 보안 시스템을 활용하는 방식이 사용 가능하며 더 안전하다.
   - 개발환경   
     application.properties 파일은 일반적으로 개발 환경에서 사용.
   - 운영 환경   
     운영 환경에서는 민감한 정보가 다른 방법으로 관리되어야 함.
     예를 들어, 운영 환경에서는 클라우드 비밀 관리 서비스나 환경 변수 등을 사용헤 민감한 정보를 관리하는 것이 더 안전.
   - 토이 프로젝트   
     토이 프로젝트라도 민감한 정보를 application.properties 파일에 저장하는 것은 피하는 것을 권장.
     대신 환경 변수나 비밀 관리 서비스, .env 파일 등을 사용하는 것이 보안상 더 안전하고, 실제 배포 환경에서도 유용한 방법임.   
     코드에 민감한 정보를 하드코딩하는 것은 최악의 선택이므로, 가능한 안전한 방법을 사용할 것.   
     만약 application.properties 파일에 민감한 정보를 작성했다면, 
     이를 .gitignore 파일에 추가하여 Git에 커밋되지 않도록 설정 필수.
   1) 환경 변수 사용   
      민감한 정보를 코드에서 하드코딩하지 않고 환경 변수로 관리하는 것이 훨씬 안전함. 
      이를 통해 각 환경별로 민감한 정보를 쉽게 다룰 수 있음.   
      예를 들어, application.properties에서 환경 변수로 값을 참조 가능.   
      운영 시스템에서는 DB_USERNAME과 DB_PASSWORD 환경 변수를 설정하여 해당 값을 사용 가능.
      ~~~
       application.properties에서 변수 호출 예시   
       spring.datasource.username=${DB_USERNAME}   
       spring.datasource.password=${DB_PASSWORD}
      ~~~
   2) .env 파일 사용   
      개발 환경에서는 .env 파일을 사용하여 민감한 정보 관리 가능.    
      .env 파일은 Git에 커밋하지 않도록 .gitignore에 추가 필수.   
      그 후 Java에서 이를 읽어오는 방식으로 사용 가능. 
      Spring Boot에서는 dotenv를 로드하는 라이브러리 사용 가능.
      ~~~
       .env 파일 설정 예시
       DB_USERNAME=your_db_username   
       DB_PASSWORD=your_db_password
      ~~~ 

> 고민과 결정 - secret key 노출되지 않도록 하기위해 선택한 방법.   
> 보안상으로는 키관리 서비스를 이용하는 것이 가장 안전하다.    
> 하지만 유료 서비스이며 개인 프로젝트에서는 오버 스펙으로 판단된다.   
> 그래서 남은 방법 중 최선인 환경변수사용 방법을 선택하려고 했지만, 
  스터디를 위한 프로젝트인 점을 감안해 application.properties 파일에 작성해도 무방하겠다고 생각했다.   
> 따라서 Mysql DB password 정보, JWT secret key는 민감정보임에도 불구하고 유료 api secret key 같은 정보가 아니므로 문제없다고 판단해 
  application.properties 파일에 작성하도록 한다.


---
### < Base64로 인코딩만 한 정보는 보안에 취약 >
1. Base64 인코딩   
   암호화가 아니라 단순히 데이터를 인간이 읽을 수 있는 텍스트 형식으로 변환하는 방법.   
   데이터를 보호하거나 보안을 강화하는 기능이 없어 암호화와는 다름.   
   암호화 알고리즘을 포함하지 않기 때문에 보안에 취약.
   즉, 데이터 전송 과정에서 바이너리 데이터를 텍스트로 변환하여 텍스트 기반 프로토콜을 통해 안전하게 전송할 수 있게 도와주지만, 보안을 위한 방법은 아님.
   보안을 위해서는 실제 암호화 알고리즘 (예: AES, RSA 등)을 사용이 필요.

2. Base64가 보안에 취약한 이유
   1) 암호화가 아님    
      Base64는 데이터를 변환할 뿐, 보안을 제공하지 않음.    
      데이터를 Base64로 인코딩하면 원본 데이터가 쉽게 복원될 수 있음. 
      누구나 Base64로 인코딩된 데이터를 쉽게 디코딩할 수 있기 때문에 실제 보안이 되지 않음.
   2) 복호화가 쉬움   
      Base64로 인코딩된 데이터를 복호화하는 것은 매우 간단. 
      이를 수행하는 도구나 라이브러리가 대부분의 프로그래밍 언어나 시스템에 기본적으로 제공되기 때문이다.    
      암호화된 데이터와 달리 복호화 과정이 특별한 키나 알고리즘을 필요로 하지 않음.
   3) 구성된 데이터는 원본 그대로임.    
      Base64로 인코딩된 데이터는 원본 데이터의 단순한 변환.   
      예를 들어, "Hello"라는 텍스트를 Base64로 인코딩하면 "SGVsbG8="가 되지만, 이를 다시 디코딩하면 원본 "Hello"를 그대로 얻을 수 있음.
      이 때문에 데이터의 내용이 외부에 노출되었을 때 정보 보호가 되지 않음.

3. 암호화와 인코딩의 목적
   1) 암호화(Encryption)   
      암호화는 **데이터를 보호**하기 위해 사용.    
      데이터를 읽을 수 없게 변환해 **허가되지 않은 사용자가 이를 해석할 수 없도록** 함.   
      암호화된 데이터를 복호화하려면 키나 알고리즘을 사용해야 하며, 그 과정이 안전하게 설계되어야 함.
      암호화는 보안이 중요한 환경에서 사용.
   2) 인코딩(Encoding)   
      인코딩은 **데이터를 특정 형식으로 변환**하는 방식.   
      예를 들어, Base64는 바이너리 데이터를 텍스트로 변환하는 데 사용됨.
      이 과정은 **데이터를 읽기 좋게 하거나 시스템에서 처리할 수 있는 형식으로 바꾸는 것**이지, 
      데이터를 보호하는 목적이 아님.   
      Base64는 복호화가 아주 간단하며, 암호화처럼 보안을 강화하지 않음.   
      따라서, Base64는 보안 기능을 제공하지 않기 때문에 암호화된 데이터나 중요한 정보를 보호하는 데는 적합하지 않음.
      만약 중요한 정보를 보호하려면, 암호화 알고리즘 사용이 필요.


### < 암호화 알고리즘 >
아래의 링크를 포함해 공부필요   
https://velog.io/@choidongkuen/Spring-AES-256-%EC%95%94%ED%98%B8%ED%99%94-%EC%95%8C%EA%B3%A0%EB%A6%AC%EC%A6%98%EC%97%90-%EB%8C%80%ED%95%B4


### < Jasypt 암호화 라이브러리 >
Base64로 인코딩한 값이 보안에 취약해 이를 보완하기위해 암호화를 별도 진행해야한다.
springBoot 환경 설정 파일의 민감정보를 암호화 하여 관리하며, 환경변수를 통해 복호화키를 설정하여 사용할 수 있도록 한다.

1. Jasypt(Java Simplified Encryption)   
   자신의 프로젝트(Java 어플리케이션)에서 설정파일의 속성값들을 암호화, 복호화할 수 있는 라이브러리.
   - api 문서   
     http://www.jasypt.org/ > http://www.jasypt.org/encrypting-texts.html

2. ASYPT, Jasypt Spring Boot Starter 라이브러리 차이   
   maven repository에 비슷한 이름의 여러 라이브러리들이 존재해 비교해본다.
   - readme.md    
     https://github.com/ulisesbocchio/jasypt-spring-boot
   
   1) ASYPT (Java Simplified Encryption) 라이브러리   
      JASYPT는 Java 애플리케이션에서 암호화 및 복호화를 간단히 구현할 수 있도록 돕는 라이브러리.   
      기본적으로 JASYPT는 다양한 암호화 알고리즘을 제공하며, 
      사용자가 별도의 복잡한 설정 없이 간단히 암호화를 구현할 수 있게 도와줌.
      - 단독 라이브러리    
        **Spring Framework와 관계없이** Java 애플리케이션에서 **독립적으로 사용 가능.**
      - 간편한 사용   
        복잡한 암호화/복호화 과정을 간소화하여, 코드에서 간단히 암호화 기능을 구현 가능.
      - 다양한 암호화 방식 지원   
        AES, DES 등 다양한 대칭키 및 비대칭키 암호화 방식 지원.
      - 기본 설정 및 기능 제공   
        기본적인 암호화 및 복호화 기능을 간단히 구현할 수 있도록 도와줌.

   2) Jasypt Spring Boot Starter 라이브러리   
      Jasypt Spring Boot Starter는 Spring Boot 애플리케이션에서 
      JASYPT 라이브러리를 더 쉽게 사용할 수 있도록 도와주는 Spring Boot 특화 라이브러리.   
      Spring Boot와의 통합을 통해 프로퍼티 파일에 저장된 암호화된 값을 쉽게 처리하고, 
      Spring Boot 애플리케이션 내에서 암호화된 데이터의 관리가 편리해짐.
      - Spring Boot 통합   
        **Spring Boot 환경에 최적화**된 라이브러리로, 
        **Spring Boot의 설정 파일 (application.properties, application.yml 등)에서 암호화된 값을 처리**할 수 있도록 지원.
      - 자동화된 설정   
        Spring Boot의 자동 설정 기능을 활용하여 암호화 및 복호화 설정을 쉽게 적용 가능.
      - 암호화된 프로퍼티 처리   
        데이터베이스 비밀번호나 API 키 등 민감한 정보를 암호화하여 프로퍼티 파일에 저장 시 유용.   
        Spring Boot 환경에서 암호화된 값을 자동으로 복호화해 사용 가능.
      - 스프링 보안 연동   
        Jasypt Spring Boot Starter는 Spring Security와도 잘 통합되어, 보안이 중요한 애플리케이션에서 유용하게 사용.

3. Encryptor Configuration   
   암호화 알고리즘 (Algorithm)으로,
   3.0 버전 부터 기본 알고리즘이 PBEWithMD5AndDES → PBEWithHMACSHA512AndAES_256로 변경됨.   
   비공개 키, 인코딩(encoding) 타입, 솔트(salt) 값 생성기 등을 설정 가능.


### < Jasypt 암호화 라이브러리 사용하기 >
1. 암호화 관련 설정
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

2. 암호화   
   아래의 메서드를 호출해 암호화 한 값을 application.properties에 등록. 
   ~~~
    private String encryptString(String plainText) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("암호화시 사용할 secret key");
        encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
        encryptor.setIvGenerator(new RandomIvGenerator());
 
        return encryptor.encrypt(plainText);
    }
   ~~~

3. 복호화
   - application.properties에서 ENC()를 통해 작성된 값은 변수호출 시 복호화된 평문으로 반환.
   - application.properties에서 복호화를 위해서는 
     application.properties에 jasypt.encryptor.bean=jasyptEncryptorAES 변수를 설정해 빈등록 필요.
   ~~~
    spring.datasource.url=ENC(암호화된값)
   ~~~

- 참고 블로그   
  https://goddaehee.tistory.com/321


---
## 8. 오류해결
### < 날짜 데이터 역직렬화 에러해결 >
1. 로그인 시 아래의 오류발생    
   로그인 시 JWT(Json Web Token) 토큰을 생성하는 과정에서 LocalDateTime 타입을 직렬화할 때 문제 발생.    
   **JWT 토큰을 생성하려면 일반적으로 사용자 정보를 Claims 객체에 넣어 JSON 형태로 직렬화**하는데, 
   이 과정에서 LocalDateTime 같은 Java 8의 날짜/시간 타입을 처리할 수 없어서 오류가 발생한 것.
 
2. 발생에러   
  (근데 데이터 반환을 회원가입때 하고 로그인 때는 토큰만 반환하는데 왜 로그인 때 에러가 났지..?)
   ~~~
   com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.LocalDateTime` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (through reference chain: io.jsonwebtoken.impl.DefaultClaims["roles"]->org.hibernate.collection.spi.PersistentBag[0]->com.shop.reservation.entity.MemberRole["createDate"])
   ~~~

3. 발생원인   
   LocalDateTime 데이터타입은 바이트화(직렬화) 할 때 어떤 규칙으로 진행할 것인지 
   Serialization에 대한 정의가 되어있지않아 발생한 에러.    
   LocalDateTime에 대해 어떤 데이터 타입으로 직렬화, 역직렬화 할 것인지 따로 지정이 필요.

4. 해결방법   
   1) 날짜타입의 직렬화 역직렬화를 위해 Entity에 아래의 어노테이션 추가   
      ~~~
      @JsonSerialize(using = LocalDateTimeSerializer.class) // 직렬화   
      @JsonDeserialize(using = LocalDateTimeDeserializer.class) // 역직렬화   
      @JsonFormat(pattern = "yyyy-MM-dd kk-mm:ss") // 원하는 형태의 LocalDateTime 설정
      ~~~

   2) Dto가 아닌 Entity에서 해당 문제를 해결하는 이유      
      Entity에 직렬화, 역직렬화 설정을 추가하는 이유는 LocalDateTime이 엔티티 필드일 경우, 
      해당 엔티티를 직접 JSON으로 변환하거나 직렬화/역직렬화할 때 발생할 수 있는 문제를 방지하기 위함이고 
      이로인해 일관성 유지가 가능하다.   
      DTO는 데이터를 전달하는 역할이므로 LocalDateTime과 같은 날짜/시간 타입을 
      Entity에서 직렬화/역직렬화 하도록 설정하는 것이 더 일관적이고 효율적이라고 생각했다.

   3) 다시 생각해보기
      > 다시 생각해보니 DTO에서 직렬화, 역직렬화 변환 타입을 지정해주는 것은 DTO 역할의 범위를 넘었다고 생각하지만
        그렇다고 데이터베이스와 매핑되는 Entity가 가져야 할 역할도 아니라는 생각이 들었다.    
        이렇게 해당 역할을 벗어나게 하는 것보다 새로운 객체를 만들어 역할을 분리하는 것이 좋아보인다.    
        (토큰 생성에 대한 객체를 따로 두고 해당 객체에서 직렬화, 역직렬화를 수행)

- Serialization(직렬화)   
  객체를 바이트 스트림이나 다른 저장 가능한 형식(JSON, XML)으로 변환하는 과정.   
  직렬화의 목적은 객체를 메모리 내에서 외부로 내보내거나(저장) 외부에서 불러오는 것(복원).

- 참고 블로그   
  https://justdo1tme.tistory.com/entry/Spring-Jackson-%EC%9D%B4%ED%95%B4-%EB%B0%8F-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0


### < JWT 토큰인증필터 추가 시 순환참조 오류발생 >
1. 발생오류
   ~~~
     Error starting ApplicationContext. To display the condition evaluation report re-run your application with 'debug' enabled.
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
   ~~~
    
2. 발생원인
   1) **JwtAuthenticationFilter**   
      토큰 유효성 검사 및 JWT 토큰 정보를 스프링 시큐리티 인증정보로 변환하기 위해 JwtAuthenticationProvider를 의존.
   2) **JwtAuthenticationProvider**   
      JWT 토큰 정보를 스프링 시큐리티 인증정보로 변환을 위한 사용자조회(loadUserByUsername())를 위해 MemberService를 의존. 
   3) **MemberService**   
      비밀번호 암호화를 위해 SecurityConfig에서 빈으로 정의해 등록한 passwordEncoder()를 사용하고 있기에 
      간접적으로 SecurityConfig를 의존.

   - 순환 참조 오류가 발생하는 이유    
     MemberService의 의존관계를 확인해보면 아래의 4가지에 의존적이다.
     ~~~
      private final PasswordEncoder passwordEncoder;
      private final MemberMapper memberMapper;
      private final MemberRepository memberRepository;
      private final MemberAuthorityRepository memberAuthorityRepository;
     ~~~
     확인해보면 SecurityConfig를 직접적으로 참조하거나 주입하는 부분은 없다.
     하지만 MemberService와 SecurityConfig 간의 간접적인 관계는 있을 수 있다.
     실제로 PasswordEncoder를 SecurityConfig에서 정의하고 빈등록하였기에 간접적으로 참조하는 것으로 보여진다.
     **SecurityConfig**는 JWT토큰인증을 위해 addFilterBefore() 메서드를 통해 jwtAuthenticationFilter 필터를 추가한다.
     이를 위해 다시 JwtAuthenticationFilter를 의존한다.
     이러한 의존성 구조는 순환 참조로, Spring이 bean을 초기화할 수 없게 만든다.

3. 해결방법   
   위의 순환관계를 끊어내기 위해 의존관게 재구성이 필요.   
   기존 MemberService를 UserDetailsService 인터페이스를 구현하도록 했다.
   하지만 위의 순환참조가 발생하여 UserDetailsService 인터페이스의 구현체가 PasswordEncoder를 의존하지 않도록 분리가 필요하다.   
   따라서 MemberService는 UserDetailsService를 구현하지 
   않고 SignInAuthenticationService를 신규 생성해 UserDetailsService를 구현한다.   
   그러면 JwtAuthenticationProvider에서 loadUserByUsername() 호출 시 
   MemberService가 아닌 SignInAuthenticationService에 의존하므로 순환관계가 끊긴다.


---
### < 아래는 상품수정 시 동작하는 쿼리의 변환과정 >
- save() 명시적 저장    
  update 하는 경우 select -> update 수행하므로 dirty check 때 보다 select 1번 더 함.
  
  1. 유효성 검증을 위한 상품조회
     select p1_0.id,p1_0.category,p1_0.code,p1_0.create_dt,p1_0.create_id,p1_0.description,p1_0.name,p1_0.sale_status,p1_0.seller,p1_0.update_dt,p1_0.update_id
     from production p1_0
     where p1_0.id=?
     and p1_0.seller=?
  2. 옵션중복체크
     select p1_0.id,p1_0.category,p1_0.code,p1_0.create_dt,p1_0.create_id,p1_0.description,p1_0.name,o1_0.production_id,o1_0.id,o1_0.create_dt,o1_0.create_id,
     o1_0.option_code,o1_0.option_name,o1_0.price,o1_0.quantity,o1_0.update_dt,o1_0.update_id,p1_0.sale_status,p1_0.seller,p1_0.update_dt,p1_0.update_id
     from production p1_0
     join production_option o1_0
     on p1_0.id=o1_0.production_id
     where p1_0.code=?
     and o1_0.option_code in (?)
  4. update production set category=?,code=?,description=?,name=?,sale_status=?,seller=?,update_dt=?,update_id=? where id=?
  5. 수정을 위한 상품조회
     // 두 테이블 join해 조회
     select po1_0.id,po1_0.create_dt,po1_0.create_id,po1_0.option_code,po1_0.option_name,po1_0.price,po1_0.production_id,po1_0.quantity,po1_0.update_dt,po1_0.update_id
     from production_option po1_0
     left join production p1_0
     on p1_0.id=po1_0.production_id
     where p1_0.id=?
  6. insert into production_option (create_dt,create_id,option_code,option_name,price,production_id,quantity,update_dt,update_id) values (?,?,?,?,?,?,?,?,?)
  7. select o1_0.production_id,o1_0.id,o1_0.create_dt,o1_0.create_id,o1_0.option_code,o1_0.option_name,o1_0.price,o1_0.quantity,o1_0.update_dt,o1_0.update_id
     from production_option o1_0
     where o1_0.production_id=?
  8. update production_option set option_code=?,option_name=?,price=?,production_id=?,quantity=?,update_dt=?,update_id=? where id=?
  
- dirty check 이용해 저장 시
  1. 유효성 검증을 위한 상품조회
     select p1_0.id,p1_0.category,p1_0.code,p1_0.create_dt,p1_0.create_id,p1_0.description,p1_0.name,p1_0.sale_status,p1_0.seller,p1_0.update_dt,p1_0.update_id
     from production p1_0
     where p1_0.id=?
     and p1_0.seller=?
  2. 옵션중복체크
     select p1_0.id,p1_0.category,p1_0.code,p1_0.create_dt,p1_0.create_id,p1_0.description,p1_0.name,o1_0.production_id,o1_0.id,o1_0.create_dt,o1_0.create_id,
     o1_0.option_code,o1_0.option_name,o1_0.price,o1_0.quantity,o1_0.update_dt,o1_0.update_id,p1_0.sale_status,p1_0.seller,p1_0.update_dt,p1_0.update_id
     from production p1_0
     join production_option o1_0
     on p1_0.id=o1_0.production_id
     where p1_0.code=?
     and o1_0.option_code
     in (?)
  3. 옵션목록수정을 위해 옵션필드 사용하니까 옵션목록조회
     select o1_0.production_id,o1_0.id,o1_0.create_dt,o1_0.create_id,o1_0.option_code,o1_0.option_name,o1_0.price,o1_0.quantity,o1_0.update_dt,o1_0.update_id
     from production_option o1_0
     where o1_0.production_id=?
  4. insert into production_option (create_dt,create_id,option_code,option_name,price,production_id,quantity,update_dt,update_id) values (?,?,?,?,?,?,?,?,?)
  5. update production set category=?,code=?,description=?,name=?,sale_status=?,seller=?,update_dt=?,update_id=? where id=?
  6. update production_option set option_code=?,option_name=?,price=?,production_id=?,quantity=?,update_dt=?,update_id=? where id=?

- FETCH -> LAZY로 변경
  1. 유효성 검증을 위한 상품조회 
     select p1_0.id,p1_0.category,p1_0.code,p1_0.create_dt,p1_0.create_id,p1_0.description,p1_0.name,p1_0.sale_status,p1_0.seller,p1_0.update_dt,p1_0.update_id
     from production p1_0
     where p1_0.id=? and p1_0.seller=?
  2. 옵션중복체크
     select p1_0.id,p1_0.category,p1_0.code,p1_0.create_dt,p1_0.create_id,p1_0.description,p1_0.name,o1_0.production_id,o1_0.id,o1_0.create_dt,o1_0.create_id,o1_0.option_code,o1_0.option_name,o1_0.price,o1_0.quantity,o1_0.update_dt,o1_0.update_id,p1_0.sale_status,p1_0.seller,p1_0.update_dt,p1_0.update_id
     from production p1_0
     join production_option o1_0 on p1_0.id=o1_0.production_id
     where p1_0.code=?
     and o1_0.option_code in (?)
  3. 옵션목록수정을 위해 옵션필드 사용하니까 옵션목록조회
     - 기존에 production, option join해서 조회하던 쿼리 -> 옵션목록은 필요 시 조회
     select o1_0.production_id,o1_0.id,o1_0.create_dt,o1_0.create_id,o1_0.option_code,o1_0.option_name,o1_0.price,o1_0.quantity,o1_0.update_dt,o1_0.update_id
     from production_option o1_0
     where o1_0.production_id=?
  4. 상품수정을 위해 repositoy.save() 명시적 호출했기에 update 전 select 쿼리 호출.
     select p1_0.id,p1_0.category,p1_0.code,p1_0.create_dt,p1_0.create_id,p1_0.description,p1_0.name,p1_0.sale_status,p1_0.seller,p1_0.update_dt,p1_0.update_id
     from production p1_0
     where p1_0.id=?
     and p1_0.seller=?
  5. insert into production_option (create_dt,create_id,option_code,option_name,price,production_id,quantity,update_dt,update_id) values (?,?,?,?,?,?,?,?,?)
  6. update production set category=?,code=?,description=?,name=?,sale_status=?,seller=?,update_dt=?,update_id=? where id=?
  7. update production_option set option_code=?,option_name=?,price=?,production_id=?,quantity=?,update_dt=?,update_id=? where id=?

    
---
### < 조회성능에 대한 고민과 조회쿼리 최적화 >
> 조회 개발기간이 늘어진 이유
> 
> 초기엔 그냥 단순히 조회 기능을 만드는 일은 시간이 하루 채 걸리지 않았다.
> 개발이 너무 빨리 끝나서 조회 성능에 대해 알아보니 생각보다 공부해야할 게 많아, 
> 실질적으로 3일 정도 전투적으로 개발에 임하지 않았기 때문에 기간이 밀렸다.

1. 검색 성능 향상을 위해 인덱싱 고려 이유
   1) 네트워크 부담 감소   
      데이터베이스 쿼리를 통해 필터링 하는 것은 
      불필요한 데이터를 서버 측에서 미리 걸러내 클라이언트로 전송되지 않아 네트워크 부담이 감소.
   2) 메모리 사용량 감소   
      서버, 클라이언트에서 필터링을 수행하지 않고 
      필요한 데이터만 메모리에 로딩하기에 메모리 사용량 감소.   
   3) 데이터베이스는 대량의 데이터를 처리에 최적화
      데이터베이스는 대량의 데이터를 처리에 최적화되어, 대규모 데이터셋을 필터링하는 데 더 빠르고 효율적.
      이 떄, ***인덱스가 없다면 테이블 풀스캔이 발생해 조회 성능이 떨어짐.***
   
   > 고민 및 결정
   > 상품판매상태, 카테고리에 대해 복합인덱스를 생성해 활용.   
   > 상품명에 대해 인덱스 사용을 고려했으나, 
     LIKE 검색을 수행하는 경우 앞, 뒤에 와일드카드로 인해 인덱스 활용이 어려움.   
   > 따라서 문자열에 대한 검색을 수행해야하는 경우 Full-Text Search 사용.
     (ULLTEXT 인덱스를 활용하여 LIKE 보다 더 빠르고 정확한 텍스트 검색을 제공)
   >
   > JPA에서 JPQL은 SQL 표준에 따른 쿼리 언어이기 때문에,
     MySQL의 특정 기능인 Full-Text Search (MATCH와 AGAINST)를 JPQL 쿼리로 직접 작성하는 데 제한 존재.
   > native query를 사용하여 MySQL 고유의 쿼리 문법을 그대로 사용하도록 함.
   > 
   > nativeQuery = true를 사용하면 SQL 문법을 그대로 사용할 수 있기 때문에, 데이터베이스에 종속적인 코드가 되어
     MySQL에서만 동작하며, 다른 데이터베이스(DB)에서는 동작하지 않을 수 있음.
   > 따라서 데이터베이스 변경같은 경우 유지보수가 어려울 수 있음.
   > 
   > JPA의 가장 큰 장점 중 하나는 객체 관계 매핑(ORM)을 통해 데이터베이스와의 상호작용을 추상화한다는 점인데,
     nativeQuery 사용 시 JPA의 추상화가 약화되어 JPA의 이점을 잃게됨.
   > 
   > 그 외 추가적 문제점들이 있지만 이커머스 특성상 유저는 상품에 대한 검색을 주로 수행하게 되므로 
     검색성능을 우선시 하는 것이 좋을 것으로 판단함.
   
2. 기존 조회 쿼리
   ~~~
    @Query(value =
    "SELECT prd " +
    "FROM Production prd " +
    "WHERE MATCH(prd.name) AGAINST(:name IN BOOLEAN MODE) " +
    "AND prd.saleStatus = 'ON_SALE' " +
    "AND prd.category = :category " +
    "ORDER BY (LENGTH(prd.name) - LENGTH(:name)) DESC")
    Page<Production> findByNameOrderByCalculatedAccuracyDesc(String name, ProductionCategoryType category, Pageable pageable);
   ~~~ 

3. 변경한 조회 쿼리
   - category, saleStatus에 대해 복합인덱스 생성. 
   - name은 LIKE 검색하지 않고 Full-Text 인덱스를 생성해 full-text-search를 적용.
     MATCH...AGAINST 구문을 이용해 name 컬럼에서 지정된 텍스트와 가장 일치하는 레코드를 빠르게 찾을 수 있도록 함. 
   - native query를 사용하여 MySQL 고유의 쿼리 문법을 그대로 사용.
   ~~~     
      @Query(value = "SELECT prd.* " +
                     "FROM production prd " +
                     "WHERE MATCH(prd.name) AGAINST(:name IN NATURAL LANGUAGE MODE) " +
                     "AND prd.saleStatus = 'ON_SALE' " +
                     "AND prd.category = :category " +
                     "ORDER BY MATCH(prd.name) AGAINST(:name IN NATURAL LANGUAGE MODE) DESC",  
            nativeQuery = true)
      Page<Production> findByNameOrderByAccuracyDesc(
               String name, ProductionCategoryType category, Pageable pageable);
   ~~~
   

