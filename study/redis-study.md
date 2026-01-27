# MY E-COMMERCE PROJECT REDIS STUDY
2025 간단한 이커머스 서비스를 만들면서 NoSQL인 Redis에 대해 공부한 내용   
<br>

Spring Data Redis 공식문서   
https://docs.spring.io/spring-data/redis/reference/redis.html?utm_source=chatgpt.com     


---
## < 목차 >
1. RDB와 NoSQL
    - RDB ACID 트랜잭션 특성
    - RDB와 NoSQL 공통점
    - RDB와 NoSQL 차이점
    - NoSQL과 Redis
2. Redis
    - Redis
    - Redis 의존성 추가
    - Redis 실행하기
    - Java에서 redis client
3. redis 설정파일
    - redis 설정파일
    - 설정클래스(설정파일)을 직접 의존성 주입하는 방식에 대한 우려
    - Redis 역직렬화 오류해결
4. Redis 연결과 해제
5. Redis Pub/Sub
6. Redis 인스턴스와 네임스페이스
    - Redis 인스턴스와 네임스페이스
    - Redis에서 네임스페이스 분리 구현하기
7. Redis와 장바구니
   - 장바구니 특징과 Redis vs RDB
8. Redis 사용 용도
9. Redis 멀티 조회 방법
   - 장바구니 상품에 대한 재고 조회
   - Redis MGET 전달 키 수 제한 확인


---
## 1. RDB와 NoSQL
### < RDB ACID 트랜잭션 특성 >
- ACID는 관계형 데이터베이스(RDB)에서 트랜잭션(데이터베이스에서 실행되는 하나의 연산 또는 작업 단위)의 특성을 정의하는 네 가지 핵심 원칙을 나타냄.
- ACID 특성은 트랜잭션을 안전하게 처리하고, 데이터의 일관성과 무결성을 유지하는 데 매우 중요한 역할을 함.
  이를 통해 여러 사용자나 애플리케이션이 동시에 데이터베이스를 사용하더라도 안전하고 예측 가능한 방식으로 데이터를 관리할 수 있음.

> ACID 원칙의 중요성
> - 데이터 무결성: ACID 원칙은 데이터의 정확성과 일관성을 보장하는 데 중요한 역할을 수행.
> - 트랜잭션 안정성: 복잡한 시스템에서 트랜잭션이 여러 연산을 포함할 때, 중간에 오류가 발생하더라도 데이터는 손상되지 않도록 보호.
> - 동시성 제어: 여러 사용자가 동시에 데이터를 다룰 때, 각각의 트랜잭션이 독립적으로 안전하게 실행될 수 있도록 보장.

1. Atomicity (원자성)
    - **트랜잭션 내의 모든 작업이 하나의 단위로 실행되어야 한다는 원칙.**
    - 트랜잭션 내의 모든 연산이 성공하거나, 실패하면 모든 연산이 취소되어야 함.   
      만약 트랜잭션 도중에 오류 발생 시 그 이전에 수행된 모든 작업도 취소되며, 데이터베이스는 트랜잭션 시작 전 상태로 롤백됨.

2. Consistency (일관성)
    - 트랜잭션이 완료된 후 데이터베이스가 일관성 있는 상태를 유지해야 한다는 원칙.
    - 트랜잭션이 시작되기 전에 데이터베이스가 일관성 있는 상태여야 하며, 트랜잭션이 완료된 후에도 여전히 일관성 있는 상태여야함.
    - 데이터베이스의 규칙이나 제약조건(예: 외래 키 제약, 데이터 타입 제약 등)을 준수하는 상태로 유지되어야 하며,
      트랜잭션을 수행하는 동안 데이터는 항상 유효한 상태여야함.
    - 예를 들면, 은행 계좌 이체에서 출금액이 계좌 잔액보다 많으면, 트랜잭션이 실패하여 계좌 잔액이 음수로 바뀌지 않도록 해야함.

3. Isolation (격리성)
    - 트랜잭션이 독립적으로 실행되어야 한다는 원칙.
    - 트랜잭션이 동시에 실행되더라도 서로 영향을 미치지 않아야함.
    - 여러 트랜잭션이 동시에 실행되면, 각각의 트랜잭션은 다른 트랜잭션의 중간 상태를 볼 수 없음.
      다른 트랜잭션의 작업이 완료되기 전에 진행 중인 트랜잭션에 영향을 미치지 않도록 보장.
    - 데이터베이스는 트랜잭션의 격리 수준(Isolation Level) 설정 가능.
      격리 수준에 따라 다른 트랜잭션의 영향을 받는 정도가 상이함.
      예를 들어, Read Uncommitted부터 Serializable까지 다양한 격리 수준이 있음.
    - 예를 들면, 두 사람이 동시에 같은 계좌에서 돈을 인출하려고 할 때,
      한 사람이 트랜잭션을 완료하고 그 후 다른 사람이 트랜잭션을 완료하는 방식으로 격리되어야함.
      중간 상태에서 두 사람이 동시에 돈을 뽑는 일이 없도록 해야함.

4. Durability (지속성)
    - 트랜잭션이 완료되면, 그 결과가 영구적으로 저장되어야 한다는 원칙.
    - 트랜잭션이 커밋(성공적으로 완료)되면, 해당 트랜잭션에 의해 변경된 데이터는 시스템 장애가 발생하더라도 영구적으로 보존됨.
    - 트랜잭션이 완료된 후, 시스템 장애나 전원 꺼짐 등의 상황에서도 데이터는 안전하게 복구되어야 함.
    - 예를 들면, 은행에서 이체가 완료된 후, 서버가 갑자기 다운되더라도 이체 내역은 영구적으로 기록되어야 하며, 시스템이 복구될 때 이체 내역은 변경되지 않고 유지되어야 합니다.


### < RDB와 NoSQL 공통점 >
> 두 시스템은 데이터 저장과 관리라는 공통된 목적을 가짐.
1. CRUD 작업 지원 (데이터 저장)
    - CRUD(Create, Read, Update, Delete) 작업을 지원하여, 데이터를 생성하고, 읽기, 수정, 삭제 가능.

2. 데이터 일관성
    - 데이터의 무결성을 보장하고, 오류가 발생하지 않도록 해 데이터 일관성을 유지.
    - RDB는 ACID 속성(Atomicity, Consistency, Isolation, Durability)을 준수하여 높은 데이터 일관성을 보장.
      그러나 이러한 일관성 보장을 위해서는 성능 저하 가능성 있음.
      특히 분산 환경에서 트랜잭션 처리가 복잡해지고, 동시성 처리에 문제가 생길 수 있음.
    - NoSQL은 대부분의 NoSQL 시스템은 일관성보다 가용성과 성능을 우선.
      이는 CAP 이론(Consistency, Availability, Partition Tolerance)에 따른 선택으로,
      분산 시스템에서는 일관성을 완벽히 유지하기 어려운 상황에서 가용성과 파티션 내구성을 보장하려는 전략을 채택.
      예를 들어, Eventual Consistency(결국 일관성)을 제공하여 쓰기 성능을 높이고 분산 처리 성능을 향상.

    1. RDB(관계형 데이터베이스)와 일관성
        - RDB는 기본적으로 ACID(Atomicity, Consistency, Isolation, Durability) 특성을 제공하는데 이 중에서 Consistency는 일관성을 의미.
          트랜잭션이 완료되면 데이터베이스는 항상 일관성 있는 상태여야함.
          예를 들어, 데이터를 업데이트하거나 삽입하는 과정에서 오류가 발생하면 트랜잭션이 롤백되어 이전 상태로 돌아감.
          이를 통해 데이터가 잘못된 상태로 남지 않도록 보장.
          따라서, RDB는 트랜잭션이 완료되면 항상 정합성 있는 데이터 상태를 유지함.
          예를 들어, 은행의 거래 시스템에서 두 계좌의 금액을 조정할 때,
          중간에 시스템 장애가 발생하면 전체 트랜잭션이 롤백되어 두 계좌 모두 일관성 있게 유지됨.

    2. NoSQL과 일관성
        - NoSQL 데이터베이스는 ACID보다는 BASE(Basically Available, Soft state, Eventually consistent) 모델을 따르는 경우가 많음.
          이는 **일관성을 완전하게 보장하기보다는, 결국 일관성(Eventually Consistent)을 추구함.**
          즉, 데이터가 여러 노드에 분산되어 있을 때,
          업데이트가 즉시 모든 노드에 반영되지 않더라도
          일정 시간이 지나면 결국 모든 노드에 동일한 데이터가 반영되어 일관성을 유지하게 되는데 이를 최종 일관성이라고 함.      
          예를 들어, 분산된 데이터베이스 시스템에서 하나의 서버에 데이터가 업데이트되면 그 업데이트가 다른 서버에 전파되는 데 시간이 걸릴 수 있음.
          이 때 시스템은 일시적으로 데이터 불일치가 발생할 수 있지만, 시간이 지나면서 모든 데이터가 일관성 있는 상태로 정리됨.

        - NoSQL의 주요 일관성 접근 방법
            - Cassandra는 최종 일관성을 제공.
              데이터를 여러 노드에 분산하여 저장하고, 변경된 데이터를 모든 노드에 전파하는 데 시간이 걸릴 수 있음.
            - MongoDB는 기본적으로 일관성을 제공하지만, 데이터베이스가 분산될 경우 일시적인 불일치가 발생할 수 있음.
              그러나 적절한 설정을 통해 강력한 일관성 보장을 선택할 수도 있음.
            - Couchbase는 일관성 모델을 조정할 수 있으며, 기본적으로 최종 일관성을 따름.
            - **Redis의 일관성(consistency)에 관한 접근 방식은 분산 시스템의 설정과 구성에 따라 달라짐.**
              Redis는 단일 노드에서 실행될 때, 기본적으로 강한 일관성을 보장.
              그러나 클러스터 모드나 분산 환경에서는 일관성과 관련하여 설정에 따라 차이가 있을 수 있음.
              > Redis의 일관성 보장 방법
              > - 단일 노드: 강한 일관성을 보장합니다. 데이터를 메모리에 저장하고, 쓰기 작업이 완료된 후 바로 데이터를 읽을 수 있습니다.
              > - 복제 환경: 비동기적으로 데이터를 복제하므로, 일시적인 불일치가 발생할 수 있습니다. 이를 해결하려면 읽기를 마스터에서만 수행하거나, WAIT 명령어를 통해 슬레이브의 데이터 복제를 보장할 수 있습니다.
              > - Redis Cluster: 최종 일관성을 따릅니다. 여러 샤드와 노드에서 데이터를 분산하여 처리하므로, 일시적인 데이터 불일치가 발생할 수 있습니다. WAIT 명령어로 복제 상태를 기다릴 수 있으며, quorum 설정을 통해 일관성 수준을 조정할 수 있습니다.

                1. 단일 노드에서의 일관성
                    - **단일 Redis 인스턴스(Redis 서버가 하나만 실행되는 환경)에서는 일관성에 대해 걱정할 필요없음.**
                      > 내 케이스!!!!!!
                    - Redis는 ACID에서의 일관성을 보장하는 것은 아니지만, 강한 일관성을 제공함.
                      즉, Redis에서 한 번의 쓰기 작업이 완료되면, 그 데이터를 바로 읽기 가능.
                      이는 Redis가 데이터를 메모리에 저장하고 처리하기 때문에 빠르고 일관성 있게 동작하는 특징임.

                2. Redis Replication(복제)에서의 일관성
                    - Redis는 마스터-슬레이브 복제를 지원.
                    - 복제된 슬레이브 노드는 마스터 노드의 데이터를 비동기적으로 복제하여, 읽기 성능을 향상시키고 고가용성을 제공.
                    - 비동기 복제
                        - 복제는 비동기적으로 이루어지기 때문에, 마스터 노드에서 데이터를 쓴 후 바로 슬레이브 노드에 반영되지 않을 수 있음.      
                          이로 인해, 마스터에서 데이터가 업데이트되었지만 슬레이브에서는 아직 반영되지 않은 상태에서 읽기가 이루어질 수 있음.
                          이때 읽기 일관성이 부족할 수 있음.     
                          즉, 마스터에서 데이터 쓰기가 이루어진 후에 슬레이브 노드로 데이터가 전파되기까지 일시적인 불일치가 발생 가능.      
                          이를 해결하기 위해 Redis는 READ FROM MASTER 설정을 통해, 읽기 작업을 항상 마스터에서 수행하도록 할 수 있음.
                          또한, WAIT 명령어를 사용하면 지정된 수의 슬레이브 노드가 데이터를 복제한 후에 작업을 완료하도록 할 수 있음.

                3. Redis Cluster에서의 일관성
                    - Redis Cluster는 수평 확장성을 지원하는 Redis의 분산 시스템.
                      Redis Cluster에서는 데이터가 여러 노드에 분산되어 저장되며, 클러스터 내에서 데이터 일관성에 대한 처리가 다소 복잡해질 수 있음.

                4. 강한 일관성 vs eventual consistency
                    - Redis Cluster는 강한 일관성을 보장하는 것은 아니며, 기본적으로 **최종 일관성(eventual consistency)**을 따름.
                    - 클러스터 내에서 여러 샤드(Shard)가 서로 독립적으로 운영되므로,
                      특정 노드에서 데이터를 업데이트하더라도 다른 노드에 전파되는 데 시간이 걸릴 수 있음.
                      이로 인해 일시적인 불일치가 발생할 수 있음.

                5. 파티셔닝과 데이터 복제
                    - Redis Cluster는 데이터 샤딩(sharding)과 복제를 통해 가용성을 높임.
                    - 각 샤드는 마스터-슬레이브 복제를 통해 데이터를 복제.
                    - 데이터가 변경되면, 이를 클러스터 내의 여러 노드로 비동기적으로 복제.

                6. 일관성 설정
                    - READ 요청을 특정 노드에서 처리하거나, WAIT 명령어를 사용해
                      슬레이브가 업데이트를 완료한 후 결과를 반환하도록 설정가능.
                    - 그러나 기본적으로는 최종 일관성이므로, 클러스터의 모든 노드가 업데이트된 데이터를 즉시 반영하지 않을 수 있음.

3. 복잡한 쿼리 처리
    - 데이터 검색 및 조작을 위한 기능을 제공하지만, 그 방식과 쿼리 언어는 상이함.
      (RDB는 SQL, NoSQL은 다른 쿼리 방식을 사용)


### < RDB와 NoSQL 차이점 >
> RDB, NoSQL 선택은 시스템의 요구 사항에 따라 달라집니다.
> 예를 들어, 정형화된 데이터를 다루고 트랜잭션의 일관성이 중요한 경우에는 RDB가 적합하고,
> 대규모 분산 시스템에서 빠른 성능이 중요한 경우에는 NoSQL이 더 적합합니다.

1. 데이터 모델
    - RDB **데이터는 테이블 형식으로 저장**되며, 각 테이블 간에 관계(foreign key, join 등)를 미리 정의하고 데이터는 엄격하게 스키마(schema)에 맞춰야 함.
      따라서 데이터를 삽입, 수정할 때마다 이 스키마에 맞춰야 하며, 이는 쓰기 성능에 영향을 미칠 수 있음.
      또한 복잡한 조인 연산이 필요한 경우에는 성능 저하가 발생 가능.
    - NoSQL **데이터는 여러 가지 모델로 저장 가능.**
      예를 들어, 문서 기반(MongoDB), **키-값(Redis)**, 열 기반(Cassandra), 그래프 기반(Neo4j) 등이 있으며, 스키마가 유연하거나 없을 수 있음.
      Redis는 관계형 데이터베이스처럼 테이블 간의 관계를 정의하고, SQL로 데이터를 처리하는 RDB 방식과는 상이함.
      키-값 기반 NoSQL(Redis)은 간단한 키-값 쌍으로 데이터를 저장할 수 있어 빠르고 효율적인 데이터 처리와 쿼리가 가능.
      이렇게 유연한 데이터 모델은 데이터의 읽기, 쓰기 성능을 극대화 함.
      따라서, 주로 캐시, 세션 저장소, 큐 시스템 등 다양한 용도로 사용됨.
      데이터 구조가 미리 정의된 스키마를 강제로 따르지 않기 때문에 다양한 유형의 데이터를 쉽게 저장하고 처리가능.

2. 스키마 및 데이터 구조
    - RDB는 스키마가 고정되어 있으며, 데이터는 미리 정의된 테이블 구조에 맞춰 저장됨.
      각 테이블의 데이터 타입과 구조가 엄격히 정의되어 있어, 데이터 모델링이 정형적.
    - NoSQL는 스키마가 유연하거나, 비정형 데이터를 저장 가능해 다양한 데이터 구조를 지원.
      데이터 모델은 각 NoSQL 시스템에 따라 다르고, 데이터 구조가 자유로움.
      예를 들어 문자열, 리스트, 셋(Set), 해시, 정렬된 셋(sorted set) 등을 다룰 수 있음.

3. 확장성
    - RDB는 **수직 확장(vertical scaling)을** 주로 사용됨.
      성능 향상을 위해 더 강력한 서버를 사용하는 방식.
      데이터가 커질수록 하나의 서버에서 처리할 수 있는 성능 한계에 도달하므로 확장성에는 물리적인 한계가 있을 수 있음.
      수평 확장이 상대적으로 어렵고, 이를 위한 복잡한 클러스터링이나 샤딩을 도입해야 하므로 확장이 상대적으로 불편하고 성능도 떨어질 수 있음.
    - NoSQL는 대규모 분산 환경에서 데이터를 분할하여 여러 서버에 저장할 수 있는 **수평 확장(horizontal scaling)을 지원.**
      데이터가 많아지면, 추가적인 서버를 쉽게 추가할 수 있어 성능 향상.
      Cassandra나 MongoDB 같은 NoSQL 데이터베이스는 데이터를 여러 노드에 분산시키고 자동으로 로드 밸런싱을 처리함.
      이로 인해 고속 데이터 처리가 가능하고, 대규모 분산 처리를 효율적으로 지원.
      따라서 데이터를 여러 서버에 분산하여 저장하고 처리할 수 있어, **대규모 트래픽을 처리 시 유리.**

4. 쿼리 언어
    - **RDB**는 **SQL(Structured Query Language)을 사용**하여 데이터를 쿼리하고 처리.
      SQL은 데이터베이스 간에 일관된 언어로, 복잡한 조인, 트랜잭션, 그룹화 등을 지원.
    - NoSQL는 각 **NoSQL 시스템마다 고유한 쿼리 방식을 사용.**
      예를 들어, MongoDB는 쿼리 언어가 있고, Redis는 명령어 기반 인터페이스를 사용.

5. 트랜잭션 지원
    - RDB는 ACID(Atomicity, Consistency, Isolation, Durability) 특성을 제공하며, 트랜잭션을 완벽하게 지원.
      여러 작업을 하나의 트랜잭션으로 묶어 처리가능.
    - 대부분의 NoSQL 시스템은 ACID 특성을 완전하게 지원하지 않음.
      대신 BASE(Basically Available, Soft state, Eventually consistent) 모델을 따르며,
      데이터 일관성을 약간 희생하더라도 고가용성을 우선시하는 경우가 많음.

6. 성능
    1. 대규모 데이터처리 성능
        - RDB는 정형 데이터를 처리하는 데 매우 강력함.
          그러나 대규모 분산 처리나 고속 데이터 처리에는 상대적으로 성능이 떨어질 수 있음.
        - **NoSQL**은 주로 **빠른 읽기/쓰기 성능을 제공**하며,
          특히 대규모 데이터 처리나 분산 처리에서 뛰어난 성능을 발휘함.
    2. 쓰기 성능
        - NoSQL 시스템은 대개 쓰기 성능이 매우 뛰어나며, 이는 시스템이 분산 저장되기 때문.
          데이터를 여러 노드에 동시에 분산시키면서 비동기적으로 데이터를 처리하므로, 대규모 데이터를 빠르게 쓰고 읽을 수 있음.
          예를 들어, Redis나 Cassandra와 같은 시스템은 많은 양의 데이터를 동시에 빠르게 처리가능.
        - RDB는 트랜잭션을 지원하고, 데이터 무결성을 보장하는 데 중점을 둠.
          그 결과, 많은 양의 데이터를 동시에 처리하는 데 있어 쓰기 성능이 떨어질 수 있음.
          특히 복잡한 트랜잭션을 처리하거나 조인 연산을 수행해야 할 때 성능 저하가 발생가능.
    3. 쿼리 성능
        - NoSQL 데이터베이스는 보통 단순한 쿼리나 특정 유형의 쿼리에 최적화되어 있음.
          예를 들어, Redis는 키-값 쌍을 저장하고 이를 빠르게 조회하는 데 특화되어 있음.
          MongoDB는 JSON 형태의 데이터를 효율적으로 저장하고 쿼리할 수 있는 기능을 제공. 이 때문에 빠른 읽기/쓰기 성능을 자랑함.
        - RDB는 복잡한 JOIN 연산이나 집계 함수 등 복잡한 쿼리 처리에 유리.
          그러나 이러한 복잡한 쿼리를 처리할 때는 성능 저하가 발생가능.
          또한, 인덱스를 잘 설정하지 않으면 쿼리 성능이 떨어질 수 있음.

7. 사용 사례
    - RDB는 트랜잭션 일관성이 중요한 시스템(은행, 회계 시스템 등)이나 복잡한 관계를 처리해야 하는 애플리케이션에 적합.
    - NoSQL는 대규모 트래픽 처리, 비정형 데이터 저장, 빠른 캐싱, 소셜 네트워크, IoT 데이터 처리 등에서 유리.



### < NoSQL과 Redis >
> **Redis가 가진 특징이 NoSQL 데이터베이스의 일반적인 특징 중 일부이긴 하지만,
모든 NoSQL 데이터베이스가 동일한 특징을 가진다고 말할 수는 없습니다.**

1. NoSQL 데이터베이스 특징
    - NoSQL 데이터베이스는 매우 다양하고 각기 다른 특징을 가지고 있음.
      이들 중 어떤 특성이 중요한지는 선택한 NoSQL 데이터베이스의 종류와 사용 용도에 따라 달라집니다.
    1. 스키마가 없거나 유연한 스키마
        - 데이터를 미리 정의된 테이블 형식이 아닌 다양한 형식으로 저장 가능
    2. 수평 확장성
        - 데이터를 여러 서버에 분산하여 확장할 수 있음.
    3. 비정형 데이터
        - 관계형 데이터베이스와 달리 비정형 또는 반정형 데이터를 처리하는 데 강점.
    4. 빠른 성능
        - 데이터 저장 및 검색에서 높은 성능을 보이는 경우가 많음 (예: Redis).

2. NoSQL 데이터베이스 유형
    - 각 NoSQL 데이터베이스는 자신만의 독특한 데이터 모델과 용도에 맞는 특성을 가지고 있음.
    - 여러가지 유형으로 구분가능한데 크게 문서 기반(Document-oriented), 키-값 저장소(Key-Value Store), 열 기반(Column-family), 그래프 기반(Graph Database) 등이 있음.

    1. Redis (key-value 저장소)
        - Redis는 NoSQL의 여러가지 유형 중 키-값 저장소에 속하는 NoSQL 데이터베이스.
        - 빠른 성능과 다양한 데이터 구조를 지원.

    2. MongoDB (문서 기반 NoSQL)
        - 데이터를 JSON 형식(혹은 BSON 형식)으로 저장하는 문서 기반 데이터베이스.
        - 관계형 데이터베이스의 테이블 대신, 데이터를 컬렉션(collection)과 문서(document)로 저장.
        - 이는 Redis와 다르게 구조화된 데이터를 저장하는 데 적합.

    3. Cassandra (열 기반 NoSQL)
        - Cassandra는 열 기반 데이터베이스.
        - 분산 시스템에 최적화된 특성을 가짐.
        - 대규모의 데이터를 수평적으로 분산하여 저장할 수 있으며, 고가용성과 확장성이 뛰어남.
        - Redis와 달리 관계형 데이터 모델을 지원하지 않으며, 데이터를 열과 행의 구조로 저장.

    4. Neo4j (그래프 기반 NoSQL)
        - Neo4j는 그래프 기반 데이터베이스.
        - 데이터를 노드와 엣지(관계)로 모델링하여 복잡한 관계를 효율적으로 다룰 수 있음.
        - Redis의 키-값 쌍 저장 방식과는 전혀 다른 접근 방식.


---
## 2. Redis
> redis는 빠른성능, 다양한 데이터 구조 지원이 가장 큰 특징.

1. Redis(Remote Dictionary Server)
    - NoSQL 데이터베이스 중 하나이며 오픈소스 소프트웨어.
    - **키-값(Key-Value)** 형태로 데이터를 저장.
      다양한 종류의 value 저장 가능.
      value로 문자열, 리스트, 셋, 정렬된 셋, 해시 등의 타입을 지원.
    - 데이터를 **인-메모리 데이터 저장소**에 저장.
      서버의 메인 메모리에 모든 데이터를 저장하므로, 디스크 I/O를 거치는 다른 데이터베이스 시스템보다 훨씬 빠른 성능을 보여줌.
    - 메모리에 저장되는 데이터 베이스는 디스크에 저장하여 휘발성으로 사용되는 **데이터를 영구적으로 저장**하는 기능을 제공하여 서버가 다운되더라도 데이터를 복구가능.

2. redis 의존성 추가
- spring-boot-starter-data-redis
    - Spring Data Redis를 쉽게 사용할 수 있도록 도와주는 라이브러리.
    - 내장 Lettuce Client를 사용해 Redis의 데이터를 키-값 형태의 저장소로 이용이 가능.
    - Lettuce Client를 이용해 Redis 데이터베이스에 접근하고 데이터를 저장, 조회하는 등의 작업을 수행가능.
    - Redis 서버와 연결하여 데이터를 처리하기 위해 해당 서버의 호스트와 포트 정보를 설정 필요.

3. redis 실행하기
   redis를 설치했음을 가정한다.
    - redis 서버 실행
        - C:\Program Files\Redis\redis-server.exe
    - redis client 실행
        - C:\Program Files\Redis\redis-cli.exe
    - redis insight
        - redis 데이터베이스를 시각화하고 관리하기 위한 GUI 도구. (redis client로 보면 될듯)
- 참고 블로그   
  https://seodaeya.tistory.com/248

3. Java에서 redis client (Lettuce 선택)
    - redis client를 통해 할 수 있는 작업을 Java로 작성가능하도록 한다.
    1. Lettuce
        - 동기, 비동기 통신을 모두 지원하는 non-blocking 자바 레디스 클라이언트.
        - 대량의 요청과 응답 처리에 있어서, Lettuce가 더욱 유리하다. TPS, CPU, Connection 수, 응답속도 등 모든 면에서 Lettuce가 우위이기 때문이다.
    2. Jedis
        - 사용 편의성을 위해 설계된 레디스 자바 클라이언트.
        - 동기식으로만 작동.
        - 타 redis client에 비해 가벼움.
- 참고블로그    
  https://devforme.tistory.com/46

- 참고블로그   
  https://adjh54.tistory.com/459   
  https://adjh54.tistory.com/448

- redis 사용 명령어
  https://jang8584.tistory.com/290


---
## 3. redis 설정파일
### < redis 설정파일 >
1. RedisConnectionFactory 인터페이스
    - Redis와의 연결을 위한 Connection을 생성하고 관리.

2. LettuceConnectionFactory
    - 해당 클래스는 RedisConnectionFactory 인터페이스를 구현한 클래스.

3. RedisTemplate<String, Object>
    - Spring Data Redis에서 제공하는 클래스.
    - Redis 데이터베이스에 접근하고 데이터를 처리하는 데 사용.
    - 타입 안전성을 제공하고, 직렬화 및 역직렬화를 처리하며, Redis의 기본적인 데이터 구조를 지원.

- 발생오류
  > ava.lang.IllegalArgumentException: template not initialized; call afterPropertiesSet() before using it
  at org.springframework.util.Assert.isTrue(Assert.java:116) ~[spring-core-6.2.1.jar:6.2.1]
  at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:394) ~[spring-data-redis-3.4.1.jar:3.4.1]
  at org.springframework.data.redis.core.RedisTemplate.execute(RedisTemplate.java:378) ~[spring-data-redis-3.4.1.jar:3.4.1]
  at org.springframework.data.redis.core.AbstractOperations.execute(AbstractOperations.java:117) ~[spring-data-redis-3.4.1.jar:3.4.1]
  at org.springframework.data.redis.core.DefaultValueOperations.set(DefaultValueOperations.java:208) ~[spring-data-redis-3.4.1.jar:3.4.1]
  at org.springframework.data.redis.core.ValueOperations.set(ValueOperations.java:75) ~[spring-data-redis-3.4.1.jar:3.4.1]
  at com.myecommerce.MyECommerce.service.redis.RedisSingleDataService.saveSingleData(RedisSingleDataService.java:18) ~[main/:na]

- 발생원인
    - RedisTemplate 또는 유사한 Spring Bean이 초기화되지 않았거나 설정되지 않은 상태에서 사용하려고 시도했을 때 발생

- 해결방법
    - RedisConfig.java에 설정한 redisTemplate() 메서드 호출 시 오류발생.
    - 빈으로 등록하지 않아 발생한 오류.
    - redisTemplate() 위에 @Bean 어노테이션 추가해 해결.


### < 설정클래스(설정파일)을 직접 의존성 주입하는 방식에 대한 우려 >
1. 설정클래스(설정파일)을 직접 의존성 주입
    - 보통 설정 파일(또는 설정 클래스)을 직접 의존성 주입하는 것은 권장되지 않습니다.
    - 그 이유는 설정 파일은 주로 애플리케이션의 설정을 구성하고 빈을 등록하는 역할을 하기 때문입니다.
    - 설정 파일은 일반적으로 스프링 컨테이너에 의해 자동으로 관리되며, 해당 설정이 적용된 빈을 다른 클래스에서 사용할 수 있도록 하는 것이 목표입니다.
    - 설정 파일을 직접적으로 의존성 주입하는 것은 애플리케이션의 설계 원칙에 위배될 수 있고, 유지보수 및 확장성 측면에서 바람직하지 않습니다.
    - **설정 클래스에서 관리하는 빈을 다른 컴포넌트에서 주입받는 것이 권장**되는 방법입니다.

2. 설정파일을 직접 의존성 주입하는 방법이 권장되지 않는 이유
    1. 설정 파일의 책임
        - 설정 클래스는 주로 빈 등록과 환경 설정을 담당합니다.
        - 직접적으로 비즈니스 로직을 담당하는 클래스에 의존성을 주입하는 것은 관심사의 분리 원칙을 위반할 수 있습니다.
        - 설정 클래스는 애플리케이션의 설정을 제공하는 역할에 집중해야 합니다.
    2. 스프링 컨테이너의 관리
        - 스프링 컨테이너는 설정 클래스를 관리하고, 이를 기반으로 필요한 빈을 자동으로 생성하여 주입합니다.
        - 설정 클래스를 직접적으로 주입하는 대신, 스프링 컨테이너가 관리하는 빈(예: RedisTemplate)을 주입받는 것이 더 자연스럽고,
          이를 통해 의존성 주입이 잘 이루어집니다.
    3. 재사용성
        - 설정 클래스를 다른 서비스나 컴포넌트에서 직접 주입받는다면, 해당 설정을 사용할 때마다 불필요하게 설정 파일을 직접 의존하게 됩니다.
        - 반면, 설정 클래스에서 필요한 빈을 @Bean으로 등록해 두면, 스프링 컨테이너가 해당 빈을 관리하고
          필요한 클래스에서 이 빈만 주입받아 사용하면 됩니다.

3. 설정파일의 직접 의존성 주입을 피하는 방식
    - 설정 클래스를 의존성 주입하는 대신, 설정 클래스는 단지 빈을 등록하고 관리하는 역할만 합니다.
    - 예를 들어, RedisConfig 클래스를 통해 RedisTemplate을 생성하고, 다른 클래스에서 이를 주입받아 사용하는 방식이 이상적입니다.
    - 이렇게 설정 클래스에서 RedisTemplate을 @Bean으로 등록한 후, RedisTemplate을 사용하는 다른 서비스 클래스에서는 다음과 같이 주입받습니다.

4. 편리한 테스트 및 유지보수
    - 설정 클래스를 직접 주입받지 않고, 빈을 주입받는 방식은 단위 테스트와 유지보수 측면에서도 더 유리합니다.
    - 설정 클래스는 구현 세부사항을 숨기고, 빈을 주입하는 방식으로 구성 요소들의 독립성을 높이는 데 유리합니다.

5. Redis 설정파일 직접 의존하지 않도록 변경하기    
   RedisTemplate을 @Bean으로 등록했기 때문에 RedisSingleDataService에서 redisConfig.redisTemplate()처럼 직접 주입하는 것이 아니라,
   필요한 곳에서 RedisTemplate을 주입하는 방식이 더 나은 설계입니다.

현재 RedisConfig 클래스에서 @Bean을 사용하여 redisTemplate을 생성 및 등록하고 있습니다.     
스프링의 의존성 주입 방식 중 redisTemplate을 다른 컴포넌트에서 사용하기 위해서 설정 클래스에서 빈을 등록합니다.

설정 클래스인 RedisConfig를 다른 컴포넌트에서 의존성으로 주입받는 대신,
redisTemplate을 직접 필요한 클래스에서 주입받도록 합니다.
RedisConfig를 다른 클래스에서 직접 주입받는 것보다 RedisTemplate을 주입받는 것이 더 자연스럽고, 더 직관적인 방식입니다.

**설정 클래스에서 @Bean으로 RedisTemplate을 정의**했기 때문에, 이를 **필요한 클래스에서 @Autowired나 생성자 주입을 통해 주입받는 것이 좋습니다.**
RedisTemplate을 @Bean으로 등록하면 스프링 컨테이너에서 자동으로 관리되기 때문에 다른 클래스에서 이를 주입받아 사용하면 됩니다.
따라서 RedisTemplate을 사용하면 되는데 굳이 RedisConfig 클래스를 의존성 주입으로 사용할 필요는 없습니다.

- redis 설정
    ~~~
    @Configuration
    public class RedisConfig {
    
        @Value("${spring.data.redis.host}")
        private String host;
        @Value("${spring.data.redis.port}")
        private int port;
    
        /** Lettuce를 사용한 Connection 객체 생성 **/
        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
            final RedisStandaloneConfiguration redisStandaloneConfig = new RedisStandaloneConfiguration();
    
            // ...
            
            return new LettuceConnectionFactory(redisStandaloneConfig);
        }
    
        /** RedisTemplate 설정
         *  : Redis에 저장할 데이터형식 제공 **/
        @Bean
        public RedisTemplate<String, Object> redisTemplate() {
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
    
            // 사용할 Connection 설정
            redisTemplate.setConnectionFactory(redisConnectionFactory());
             
            // ...
             
            return redisTemplate;
        }
    }
    ~~~
- redis 사용부분 올바르게 수정
    ~~~
    @Service
    @RequiredArgsConstructor
    public class RedisSingleDataService {
        // 설정파일이 아닌 bean에 의존하도록 의존성주입
        // 기존 : private final RedisConfig redisConfig;
        private final RedisTemplate<String, Object> redisTemplate;
    
        /** Redis 단일 데이터 등록 **/
        public void saveSingleData(String key, Object value, Duration duration) {
            // 기존 : redisConfig.redisTemplate().opsForValue().set(key, value, duration);        
            redisTemplate.opsForValue().set(key, value, duration);
        }
    
        // ... 
    }
    ~~~


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
        - value는 객체이므로, Jackson2JsonRedisSerializer를 사용하여 객체를 JSON 형태로 직렬화합니다.
          이 설정은 객체를 JSON으로 변환하여 Redis에 저장하고, 다시 Redis에서 가져올 때 JSON을 객체로 역직렬화합니다.


---
## 4. Redis 연결과 해제
- Spring Data Redis에서 자동 연결 관리를 수행하기 때문에
  RedisTemplate을 사용하게되면 Redis 연결을 명시적으로 생성하거나 끊는 로직은 일반적으로 불필요.

1. Spring Data Redis의 자동 연결 관리
    - RedisTemplate은 RedisConnectionFactory를 통해 Redis와의 연결을 자동으로 관리.
    - Spring Data Redis는 연결 풀(connection pool)을 사용하여 Redis와의 연결을 효율적으로 처리.
      이로 인해 별도의 연결 및 종료 로직 없이도 Redis와의 연결이 자동으로 관리됨.

2. RedisTemplate과 connection pool
    - RedisTemplate은 기본적으로 Lettuce나 Jedis와 같은 Redis 클라이언트를 사용.
      이들 클라이언트는 연결 풀을 사용하여 연결을 재사용.
      즉, 연결을 반복적으로 생성하고 끊는 대신, 연결 풀에서 연결을 가져오고 반환하는 방식으로 동작.
    - Spring에서는 LettuceConnectionFactory 또는 JedisConnectionFactory를 사용하여 연결 풀을 설정.
      Redis 연결이 필요한 작업을 할 때마다 RedisTemplate은 자동으로 이 연결을 사용하고, 작업이 끝난 후 연결을 풀에 반환.

3. connection pool의 이점
    - Redis 서버와의 연결을 효율적으로 재사용하여 성능을 최적화 가능.
    - 연결 풀을 사용하면 새로운 연결을 생성하는 데 드는 비용을 줄이고, 자원의 낭비를 막을 수 있음.
    - Redis 서버와의 연결이 자주 생성되고 끊어지는 대신, 한 번 연결된 세션을 여러 작업에서 재사용 가능.

4. 연결을 끊는 로직이 필요한 경우
    - 특정 종료 작업이 필요한 경우가 있을 수 있습니다.
    - Redis와의 연결을 명시적으로 종료하고자 할 때는 @PreDestroy 어노테이션 사용 가능.
    - 하지만 대부분의 경우, Spring의 연결 관리 시스템에 맡겨두면 충분.
    - 예외상황
        - Redis 서버와의 연결을 명시적으로 종료하고자 할 때 (예: 애플리케이션 종료 시).
        - 테스트 환경에서 Redis 연결을 명시적으로 종료해야 할 경우.
        - 네트워크 문제로 Redis 연결이 끊어졌을 때, 다시 연결을 시도하는 로직이 필요할 수 있습니다.

   ~~~ 
    @PreDestroy
    public void close() {
        // 직접적으로 연결 종료가 필요한 경우
        if (redisConnectionFactory != null) {
            redisConnectionFactory.destroy();
        }
    }
   ~~~


---
## 5. Redis Pub/Sub
- **발행/구독(Publish/Subscribe) 패턴을 Redis에서 구현한 기능.**
- 메시지 시스템을 구현하여 서로 다른 애플리케이션 간 쉽게 통신가능.
- "발행자(publisher)"가 메시지를 보내면, 이를 구독하는 "구독자(subscriber)"가 메시지를 받아 처리하는 구조.

1. Redis Pub/Sub 기본 개념
    - Publisher (발행자): 특정 채널에 메시지를 "발행"하는 역할을 합니다.
    - Subscriber (구독자): 특정 채널을 "구독"하고, 해당 채널에서 발행된 메시지를 받는 역할을 합니다.

2. Pub/Sub 흐름
    1. 발행자는 Redis에 특정 "채널"에 메시지를 발행.
    2. 구독자는 해당 채널을 구독하고, 메시지가 발행될 때 이를 실시간으로 수신.
    3. 메시지는 구독자가 구독한 채널에 실시간으로 전달되며, 구독자는 메시지 처리.
    4. 발행자는 상품의 판매 가능 수량이 변경되었을 때, 특정 채널에 메시지를 발행.
    5. 구독자는 이 채널을 구독하고, 판매 가능 수량이 변경되었음을 알게 되면 해당 상품을 장바구니에서 품절 처리.

3. Redis Pub/Sub 사용하기
    - **PUBLISH, SUBSCRIBE, PSUBSCRIBE** 명령어를 통해 Pub/Sub 기능을 사용가능.

4. 장바구니와 품절 상태 동기화
    - Redis Pub/Sub을 사용하면 실시간으로 품절 상태를 동기화 가능.
    - 사용자 장바구니에 이미 담겨 있는 상품이 품절되었을 경우, Redis Pub/Sub을 통해 실시간으로 해당 상태를 반영가능.

5. 장점
    - 실시간 업데이트
        - 상품이 품절되면, Redis Pub/Sub을 통해 실시간으로 장바구니에 반영되므로 빠르고 즉각적인 동기화가 가능.
    - 비동기 처리
        - 발행자와 구독자가 서로 독립적으로 작업할 수 있기 때문에 비동기적 처리가 가능. 이는 시스템의 성능과 확장성을 높이는 데 유리.
    - 효율적인 품절 상태 처리
        - 상품 품절 상태를 실시간으로 처리할 수 있어 사용자 경험이 향상.



---
## 6. Redis 인스턴스와 네임스페이스
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
유효한 로그인 토큰 정보와 장바구니 내용을 분리해 저장하려는 경우, Redis의 네임스페이스(혹은 데이터베이스)를 활용하는 것이 좋은 방법입니다. 여러 가지 이유로 이를 분리하는 것이 유리합니다

- 논리적 분리  
  로그인 토큰과 장바구니 내용은 서로 다른 도메인과 관련된 데이터이기 때문에, 각기 다른 데이터베이스(네임스페이스)로 분리하는 것이 관리에 용이합니다. 예를 들어, 로그인 토큰은 인증과 관련된 데이터이고, 장바구니 내용은 사용자 행동과 관련된 데이터입니다. 이 둘을 별개의 데이터베이스에 저장하면 데이터의 역할과 목적에 맞는 관리가 가능합니다.
- 성능 최적화  
  Redis는 데이터베이스(네임스페이스) 간에 완전히 독립적이지 않지만, 데이터베이스를 나누어 놓으면 한 쪽에서 불필요한 데이터 조회나 수정이 발생해도 다른 데이터베이스에 영향을 미치지 않습니다.
  예를 들어, 로그인 토큰이 많이 만료되거나 갱신되는 작업이 있을 때, 그와 관련된 네임스페이스에만 영향을 미치고 장바구니 데이터는 영향을 받지 않습니다. 이를 통해 성능적인 장점도 있을 수 있습니다.
- 데이터 삭제 및 만료 정책 관리  
  로그인 토큰은 보통 일정 시간이 지나면 만료되거나 삭제됩니다. 이를 하나의 네임스페이스에서 처리하고, 장바구니 데이터는 사용자 세션이 종료될 때까지 유지될 수 있습니다.
  각 데이터베이스에서 개별적인 TTL(Time To Live) 설정을 통해, 로그인 토큰은 짧은 만료 시간을 설정하고 장바구니는 좀 더 긴 만료 시간을 설정하는 식으로 유연한 만료 정책을 적용할 수 있습니다.  
  예시 )  
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
## 7. Redis와 장바구니
### < 장바구니 특징과 Redis vs RDB >
1. 초고빈도 읽기/쓰기   
   장바구니는 상품 추가, 수량 변경, 삭제, 페이지 이동할 때마다 조회, 로그인 전,후 병합 등으로 인해 요청 빈도와 응답 속도가 매우 중요.
   Redis는 메모리 기반으로 ms -> sub-ms 수준이며 단순 key-value 방식으로 접근하므로 동시 사용자가 많아도 안정적이다.
   RDB를 이용하면 디스크 I/O 사용, 인덱스 관리 비용, 트랜잭션/락 비용이 필요해 체감 성능 차이가 크다.
2. 휘발성 데이터에 최적   
   장바구니 데이터는 본질적으로 일정 기간 미사용 시 삭제해도 문제가 없다.
   Redis는 TTL(Time To Live)를 지원하므로 자동 만료 시 배치가 불필요하다.
   반면 RDB는 직접 삭제 배치가 필요하고 사용자가 많아지면 테이블이 계속 비대해지며 이력 관리와 불필요한 데이터가 혼재하게 된다.
   따라서 잃어도 되는 데이터는 Redis가 자연스럽다.
3. 동시성 처리에 유리
   장바구니는 여러 탭에서 수량 변경, 앱/웹 동시 접근 등 동시성 이슈가 자주 발생한다.
   Redis는 단일 스레드(하나의 메인 스레드가 모든 명령 순차 처리) 기반으로 명령 단위 원자성(하나의 명령을 쪼개지 않고 한 번에 실행)을 보장해 락 없이 안전하다.
   반면 RDB는 SELECT 수행 후 UPDATE가 필요하며 락 경쟁, 트랜잭션 설계가 필요하다.
   따라서 단순 증감/변경 로직에 대해서는 Redis가 압도적으로 단순하다.
4. Reids의 데이터 구조와 장바구니가 잘 맞음


### < 레디스 구조에 맞춰 장바구니 크기 제한 정책 변경 >
레디스의 장점을 살리기 위해 장바구니 정책의 의미를 변경했다.
기존 모든 장바구니 상품의 옵션 수를 제한하는 정책에서 장바구니 상품의 옵션 종류의 수를 제한하도록 변경했다.
장바구니의 모든 데이터를 가져와 상품 수량을 읽지 않고, 레디스에서 전체 데이터 수를 카운팅해 가져오기 위함이다.
멘토님꼐 장바구니 구현에 지적받는 부분이 코드가 레디스 친화적이지 않고 RDB스럽다는 부분이었기 때문이다.
그렇게 레디스를 장점을 살리는 구조로 장바구니를 구현하기로 방향을 설정했다.
특히, 불필요한 조회, 반복적인 조회는 제거하거나 초기에 한 번만 조회하도록 해 성능을 개선한다.

1. 기존 장바구니 상품 수량 제한 정책   
   1) 동작 방식   
      레디스 해시 전체값을 조회해 모든 옵션 데이터를 가져와 옵션 수량의 합계를 계산하고, 100개 이상이면 예외를 발생시켰다.
   2) 문제점
      getHashEntries()를 이용해 모든 데이터를 가져와 자바에서 계산을 수행했다.
      장바구니가 커지면 레디스 조회 비용이 높아져(HGETALL이므로 O(N)) 네트워크 비용이 늘어난다.
      이는 장바구니가 커지거나 동시 사용자가 많아지면 성능 병목 현상이 발생 가능하다.
      레디스의 장점인 O(1) 조회 활용을 못해 레디스 장점을 상실했다.

2. 변경한 장바구니 상품 종류 수 제한 정책
   1) 동작 방식   
      레디스의 HLEN(getSizeOfHashData())을 활용해 해시 내 필드 수를 바로 조회한다.
      장바구니 아이템 종류 수를 기준으로 제한해 자바에서 불필요한 연산을 제거해 성능을 개선했다.
   2) 장점
      1. O(1) 호출로 조회량을 최소화해 대용량 장바구니에서도 성능이 안정적이다.
      2. getSizeOfHashData() 호출로 코드를 단순화해 기존 진행하던 ObjectMapper 변환, stream() 연산을 제거했다.
         따라서 모킹 대상이 줄어 테스트도 단순해졌다. 

3. 학습 관점
   1) 레디스 구조 이해
      - Hash, String, Set, Sorted Set 등 데이터 구조 특성을 고려한 설계 경험
      - O(1) 조회, HLEN/HGET/HSET 등 연산과 정책 설계 연결 경험
   2) 정책 설계 능력
      - 단순히 정책 그대로 구현하는 게 아니라
      - “정책 의도는 유지하면서, 레디스 특성에 맞춰 단위를 조정할 수 있다”는 점을 보여줌
      - 실제 산업에서도 매우 중요한 능력
   3) 성능/확장성 고려
      - 장바구니 크기 제한을 ‘옵션 수 기준’으로 바꾼 것 → 대용량 장바구니에서도 안정적
      - 레디스 활용 방식과 정책 설계가 맞물리는 경험

4. 주의할 점
   1) 정책 의도와 다르게 해석하면 안 됨
      - 예: 단순히 “레디스 편하려고 총 수량 → 종류 수”로 바꿨는데, 실제 서비스 정책 의도와 완전히 다르면 안 됨
      - “정책 의도는 그대로, 계산 단위만 레디스 친화적으로 바꾼 것”은 OK
   2) 설계 변경 이유를 명확히 기록
      - 포트폴리오 README나 주석에 남기면 설계 사고 과정까지 보여줄 수 있음
      > “장바구니 크기 제한 정책을 레디스 Hash 구조에 맞춰 옵션 종류 기준으로 변경, 성능 최적화 및 O(1) 조회 활용” 


### < 상품별 만료 필드를 배치로 일괄 제거 시 문제점 >
1. 기존 구조
   상품 옵션마다 DTO의 만료일자 필드 값을 입력하고, 배치로 전체 장바구니를 스캔해 옵션별 만료 여부를 판단해 만료된 옵션을 삭제했다.

2. 문제점   
   > Redis를 단순히 빠른 메모리 DB 처럼만 사용한 구조.

   기존에 장바구니 데이터는 Redis에서 관리하는데 상품별 만료 필드를 가지고 데일리 스케줄 삭제를 하고있다.
   이는 Redis TTL, 자동정리, 실시간성, 단순성을 포기하고 단순히 빠른 RDB처럼 쓰게 만드는 행위이다.
    - 가장 큰 장점인 자동 만료를 직접 구현하는 행위
    - 만료 시점과 삭제 시점 사이 시간차가 발생
    - 전체 스캔으로 인해 사용자 수와 상품 수 만큼 비용이 증가하므로 트래픽이 많아져 Redis CPU 부담이 증가
    - 스케줄 기반으로 배치 서버 필요, 실패 시 재시도, 중복 실행 방지, 스케줄 누락 모니터링 등 Redis를 쓰면서도 RDB 배치 운영 비용을 그대로 떠안음
    1) Redis TTL 미활용   
       Redis 핵심 기능인 Key-level expiration 사용하지 않음.
    2) 배치 필요   
       Redis를 쓰고도 RDB 배치 패턴 그대로 유지.
    3) 전체 스캔   
       장바구니를 전체 조회해 Redis O(1) 장점 상실.
    4) 이중 만료 관리
       Redis와 애플리케이션 만료일을 동시 관리.

3. 개선된 구조가 Redis 친화적인 이유
   기존 만료 관리는 앱 로직에서 진행했으며 데이터 수명은 필드 기반이고 설계 관점은 RDB 사고 방식이었다.
   개선된 구조는 만료 관리를 Reids 엔진에서 수행하며 Key 기반으로 데이터 수명을 관리하고 이는 Redis적 사고 방식으로 볼 수 있다.
   따라서 Redis의 TTL과 Key 생명주기를 활용해 장바구니 만료 정책을 Redis에 위임했다
   ~~~
   redisSingleDataService.setExpire(
        redisKey, Duration.ofDays(EXPIRATION_PERIOD));
   ~~~
   - HashKey 자체에 TTL을 부여
   - 장바구니에 변화가 있을 때 TTL 갱신
   - 30일 동안 추가/변경 없으면 Redis가 자동 삭제
   1) TTL을 Redis에 위임    
      만료 책임을 애플리케이션에서 Redis로 이동해 배치를 제거해 운영 복잡도를 낮출 수 있음.
   2) 장바구니를 하나의 생명주기로 모델링   
      장바구니는 유저의 임시 상태 데이터로, 옵션별 만료가 아닌 장바구니 단위 만료시켜야 도메인 모델링도 더 자연스러워짐.
   3) Redis 자료구조 철학에 부함   
      Hash는 관련된 데이터를 한 Key 아래 묶는 구조이다.
      TTL은 Key 단위로 괸라하는 것이 정석이다.
      따라서 개선된 구조는 Redis 설계 철학을 그대로 따라간 구조이다.
   4) 성능 관점 개선
      기존은 만료 판단 시 전체 조회 후 배치에서 만료시켜 복잡도가 O(N)이며, 
      개선된 구조는 Redis 내부 TTL이 만료 판단을 수행하므로 O(1)의 복잡도를 갖는다.
   
     

4. 해결방법   
   사용자가 상품을 추가/수정할 때마다 Expire를 다시 걸어 TTL을 연장한다.
   구현이 단순하고 Redis 철학에 잘 맞아 실무에서 가장 많이 사용된다.
   상품변 만료는 불가하므로 오래 방치된 장바구니는 통째로 삭제해도되는 경우 적합하다.
   현재 프로젝트는 단순하게 설계되어 할인 정책, 한정 수량 및 기간 등 상품별 만료가 꼭 필요한 경우가 아니다.
   따라서 복잡하게 Redis TTL 대신 시간을 데이터로 관리하는 Sorted Set으로 만료 시점을 관리하는 방식이 필요하지 않다.
   따라서 단순하게 장바구니가 30일간 추가 변동 사항이 없다면 만료되게끔 Redis에서 지원하는 TTL을 사용하도록 한다.


의도·설계·트레이드오프가 명확한 좋은 변경

### < 장바구니에서 Set이 아닌 Hash 사용 이유 >


---
## 8. Redis 사용 용도
Redis는 캐시 서버가 아닌 인메모리 데이터 플랫폼이다.
따라서 Redis에서 관리한다고 전부 캐시 데이터는 아니다.

1. Redis 활용 방법
   Redis는 캐시도로 쓰고 저장소로도 쓴다. 
   1) 로그인 토큰 - 세션 저장소     
      DB에 원본이 없고, Redis가 유일한 진실이며 TTL 지나면 만료되는 것이 정상 동작이므로, Redis를 세션 저장소로 쓰는 경우이다. 
   2) 장바구니 - 인메모리 데이터 저장소     
      DB에 저장하지 않고 Redis에만 존재하며 TTL 기반으로 자동 삭제한다.
      개념적으로는 주 저장소 역할을 한다.
   3) 재고 - 캐시   
      DB에 원본이 존재하고 Redis는 복제본일 뿐이다.
      Redis 데이터가 틀려도 추문 시 DB에서 최종 검증하므로, Redis는 빠른 조회용으로, 최종 판단은 DB에서 수행한다.

2. Redis와 캐시   
   캐시는 원본 데이터(DB)가 존재하고 Redis 데이터는 없어져도 다시 만들 수 있는 경우 사용한다.
   따라서 Redis가 캐시냐 아니냐는 데이터 성격의 문제로, Redis 기술의 문제가 아니다.
   

---
## 9. Redis 멀티 조회 방법
### < 장바구니 상품에 대한 재고 조회 >
1. 장바구니 100건 조회 시 고려할 점
   Redis 캐시는 메모리 기반이라 조회 속도가 매우 빠르다.
   N+1 방식으로 100번 조회도 가능하지만, 불필요하게 반복 호출하는 건 네트워크 왕복 비용 때문에 느려져 CPU/네트워크 부담이 발생한다.
   따라서 최적화 방법은 한 번에 필요한 데이터만 가져오는 것이다.

2. Redis에서 효율적 다건 조회 전략
   1) Multi-get (MGET) 사용 (장바구니 재고 조회 방법으로 선택)  
      Redis Key를 장바구니 상품/옵션 ID 기준으로 구성해 한 번에 데이터를 가져올 수 있다.
      Redis 호출 1번으로 다 건의 데이터를 모두 가져와 메모리 조회 속도가 빠르다.
      즉, Redis 명령 1번이므로 네트워크 왕복도 1번 수행하며, 코드는 가장 단순하고 Redis 서버 입장에서도 최적화된 명령이다.
      ~~~
      redisTemplate.opsForValue().multiGet(keys);
      ~~~
   2) Hash 구조 사용
      상품ID/옵션ID를 해시 필드로 관리하는 방법.
      메서드를 통해 한 번에 데이터를 가져올 수 있다.
      MGET과 비슷하지만 관리에 용이하다.
      ~~~
      HMGET(STOCK, field1, field2, ..., fieldN)
      ~~~
   3) Redis Pipeline 사용   
      파이프라인은 여러 Redis 명령을 묶어서 보내는 기술이다.
      MGET이 안 될 때, 서로 다른 명령을 같이 써야할 때, 키 구족 너무 복잡해 단일 명령이 불가할 때 사용한다.
      따라서 코드가 복잡하고 가독성이 떨어지며, MGET보다 Redis 내부 최적화가 덜하다.
      다중 조회 시 파이프라인을 사용해 네트워크 왕복을 최소화한다.
      다수 키 조회 시 성능 최적화 가능하다.
      ~~~
      redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
          for (String key : keys) {
              connection.get(key.getBytes());
          }
          return null;
      });
      ~~~


### < Redis MGET 전달 키 수 제한 확인 - 현재 상황에선 고려 필요없음. >
1. Redis 차원의 하드 리밋 
   Redis 명령어에는 MGET 최대 제한 수는 없다.
   실제 제한은 프로토콜 최대 요청 크기, 서버 메모리, 네트워크 패킷 크기이다.
   이 기준으로보면 100개 정도는 정석에 가깝다. 500, 1000개도 일반적으로 문제 없다.

2. Redis 프로토콜 최대 요청 크기
   1) Redis 공식 제한    
      Redis는 단일 요청 크기 512MB 제한으로, 
      Redis 프로토콜 (RESP)기준 명령어 + 모든 키 + 모든 인자 합산 크기를 포함한다.
      즉, 전체 문자열이 512MB 이하면 괜찮다.    
      보수적으로 키 길이가 (상품코드 + 옵션코드) * 키 수 = (100 + 100) * 100 = 20,000 Byte / 1024 = 19.5 KB    
      현실적으로 512MB 대비 약 0.0039% 이하로 현실적으로 절대 문제될 수 없다.       
   2) 서버 메모리 (Redis 인스턴스 관점)    
      MGET 요청 자체가 아닌 응답으로 반환되는 값들의 총 크기이다.
      Redis 입장에서는 이미 메모리에 있는 데이터를 잠깐 클라이언트로 직렬화해서 내보내는 수준의 작업을 한다.
      값이 수십 KB ~ MB 단위이거나, 한 번에 수천 개를 조회하는 경우는 위험해질 수 있으나 장바구니 재고 조회 정도는 무관하다.
      장바구니 재고 조회의 경우는, 보수적으로 value 하나 당 4byte(천 단위 수량), 길이정보 5~8Byte로 대량 10~15Byte로 
      100개 응답해도 1.5KB 정도밖에 안되기 때문이다.
      - bash에서 아래의 명령어로 서버 메모리 확인 가능     
        주요 항목은 used_memory, maxmemory, mem_fragmentation_ratio.
      ~~~
      INFO memory
      ~~~
   3) 네트워크 패킷 크기    
      - TCP 패킷 기본
        MTU = 1,500 Bytes로 응답이 크면 자동으로 여러 패킷으로 분할된다. (문제안됨)
        성능에 영향이 있는 경우는, RTT가 큰 환경, 응답이 수 MB 이상인 경우, 호출 빈도가 초당 수천 이상인 경우이다.
        애플리케이션 레벨에서 직접 확인할 필요는 거의 없지만, 필요하면 wireshark로 확인. (실무에선 거의 안씀)

   