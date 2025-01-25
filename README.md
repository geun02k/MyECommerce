# MY E-COMMERCE PROJECT
2025 간단한 이커머스 서비스

## 개요
    간단한 이커머스 서비스 제공 프로젝트.
    
    Goal : 상품을 구입가능한 서버를 구축한다.

    Use : Spring Boot
          Java 17
          Jpa
          Database - RDB : Mysql
          Build - Gradle
          Spring Security
          Login Token - JWT
          Test - Junit5
          Test UI - PostMan

    산출물 :  DB ERD, api 문서



## < 프로젝트 스터디 목표 >
### 1. 필수목표 
> - [ ] 테스트코드 작성
>   - 일일이 api를 호출하지 않고 기능의 정확성을 확인하기 위해
> - [ ] JWT토큰 및 스프링 시큐리티 사용을 통한 인증,인가
>   - 인증, 인가되지 않은 사용자의 접근을 막기위해
> - [ ] redis를 이용한 캐싱
>   - 조회속도 높이기 위해
> - [ ] redis를 이용한 동시성 제어
>   - 결제가 동시에 발생하지 않도록 하기위해
> - [ ] 스프링 배치 사용하기
>   - 일정시간마다 반복하는 기능 만들기
> - [ ] 로그파일생성
>   - 모니터링을 위한 로그파일 생성하기 

### 2. 추가목표
> - [ ] 소셜로그인
>   - 사용자가 로그인을 좀 더 쉽게 진행하기 위해
> - [ ] 이미지 파일업로드
>   - AWS S3를 이용한 클라우드 서버에 저장 -> 1년 무료이기 때문에 추후 로컬 서버컴퓨터에 저장하도록 변경해보기
> - [ ] 회원가입 시 추가 인증절차 진행
>   - 이메일 또는 전화번호를 이용한 본인확인 인증절차 진행 후 회원가입 완료되도록 처리하기
> - [ ] 간단한 정산처리
>   - 배치 순차처리 및 정산처리로직 만들어보기 



## < 프로젝트 요구사항 정의 >
### 1. 기본구현기능
    필요 테이블 : 회원테이블(판매자,고객 동시관리), 회원별권한테이블,
                 상품테이블, 상품옵션테이블, 주문테이블, 공지알림테이블,
                 공통코드테이블, 상세공통코드테이블

#### 공통
- [ ] **로그인**
    - [ ] 아이디, 패스워드 정보 일치 시 로그인 가능
    - [ ] 로그인 시 토큰 발생
    - [ ] 로그인 시 토큰을 이용한 접근권한제어
        - 로그인하지 않은 고객은 상품 조회만 가능.
        - 로그인한 회원은 상품조회 외 페이지 접근가능. (단, 권한에 따른 접근페이지 상이)
        - JWT, Filter 이용해 간략하게 진행.
- [ ] **로그아웃**
    - [ ] 생성된 토큰 삭제로 로그아웃처리(?)
- [ ] 회원정보 수정
    - 수정가능 데이터 : 비밀번호, 전화번호, 주소
- [ ] 회원정보 삭제
    - 회원정보 삭제여부='Y'로 변경해 논리적 삭제. 
- [ ] 회원정보 조회
    - 회원테이블의 컬럼 전체 조회가능. 
- [ ] 로그생성
    - ERROR 로그 파일 별도 생성.
    - ERROR 외 로그 파일 생성.
    - 파일이 일정 사이즈에 도달하는 경우 새로운 로그파일 생성.
    - 자동 일자별 로그파일 압축.
    - 일자별 로그파일 최대 보관주기(~일), 해당 설정일 이상된 파일은 자동으로 제거.

---
#### 판매자
- [x] 셀러(회원) 가입 
    - 회원 아이디는 unique.
    - 전화번호 중복불가 등 유효성검증.
    - 비밀번호 암호화 처리.
    - 입력데이터 : 사용자아이디, 비밀번호, 이름, 전화번호, 주소.
    - 전체데이터 필수입력.
    - 회원가입 시 삭제여부 = 'N'
    - 회원가입 시 SELLER(판매자) 권한 부여. (권한코드 enum으로 관리)
- [ ] 상품 등록 
    - 셀러 회원가입 후 등록가능.
    - 입력데이터 : 상품코드, 상품명, 가격, 수량, 분류, 설명.
    - 설명을 제외한 데이터는 필수입력.
    - 상품등록 시 상품판매상태 = 'ON_SALE'로  기본설정.
    - 한 상품 등록 시 대한 여러 상품옵션 등록가능.
    - 상품판매상태 도메인 : ON_SALE(판매중) / DELETION(삭제)
    - 상품분류 도메인 : 1(여성의류) / 2(남성의류) / 3(패션잡화) / 4(뷰티) / 5(전자제품) / 6(아웃도어) / 7(유아용품) / 99(기타)   
                     공통코드로 관리.
- [ ] 상품 수정
    - 판매자 본인이 등록한 상품만 수정가능.
    - 가격이 변경되거나 상품명 변경 시에는 상품을 수정하지 않고 새로 등록하도록 함.
    - 수정가능 데이터 : 수량, 설명.
- [ ] 상품 삭제 
    - 판매자 본인이 등록한 상품만 삭제가능.
    - 주문 테이블이 존재하므로 데이터 유지를 위해 상품등록 시 상품판매상태 = 'ON_SALE'로 변경해 논리적 삭제.
    - 상품 삭제 시 상품옵션도 논리적 삭제.
- [ ] 주문내역 조회
    - 일자별 결제완료된 주문내역 조회가능.
    - pageable 이용해 5건씩 출력.
    - 최신 주문 순 정렬.
    - 본인이 등록한 상품에 대한 주문내역만 조회가능.
- [ ] 주문 알림 
    - 10분 마다 결제완료된 상품이 있는 판매자에게 알림 전송. (배치처리)
    - 판매자에게 알림을 전송하기 위해 공지테이블에 결제완료된 주문 건 수를 포함한 알림저장.
    - ex) '상품명(상품코드)에 대한 3건의 주문이 있습니다.'
- [ ] 주문상태수정 - 배송중 (배송관련처리)
    - 주문상태 도메인 : 1(결제완료) / 2(배송중) / 3(배송완료) / 4(구매확정) / 5(결제취소)
                     11(교환요청) / 12(교환완료)      
                     21(반품요청) / 22(반품완료)      
                     공통코드로 관리.
    - 판매자 본인의 판매물품에 대한 결제완료된 주문에 대해 배송가능.
    - 주문확인 후 상품에 대한 배송한 경우 판매자는 주문상태코드를 배송완료로 변경.
- [ ] 주문상태수정 - 배송완료 (배송관련처리)
  - 판매자 본인의 판매물품에 대한 배송중인 주문에 대해 배송완료가능.
  - 배송중인 주문에 대해 판매자는 주문상태코드를 배송완료로 변경.
- [ ] 주문상태수정 - 반품완료 (반품관련처리)
    - 판매자 본인의 판매물품 주문에 대한 반품요청 건에 대해 상품을 반품받은 경우 반품완료처리 가능.
    - 주문에 대한 반품완료 시 해당 상품에 대한 수량을 주문했던 수량만큼 증가.
    - 반품완료 시 결제금액 환불.
- [ ] 주문상태수정 - 교환완료 (교환관련처리) 
    - 판매자 본인의 판매물품 주문에 대한 교환요청 건에 대해 상품을 교환해준 경우 교환완료처리 가능.
    - 교환완료 처리 시 기존 주문건에 대한 상품옵션 수량증가 처리. (수량복구)

---
#### 고객
- [x] 고객(회원) 가입
    - 회원 아이디는 unique.
    - 전화번호 중복불가 등 유효성검증.
    - 비밀번호 암호화 처리.
    - 입력데이터 : 사용자아이디, 비밀번호, 이름, 전화번호, 주소.
    - 전체데이터 필수입력.
    - 회원가입 시 삭제여부 = 'N'
    - 회원가입 시 CUSTOMER(소비자) 권한 부여. (권한코드 enum으로 관리)
- [ ] **상품 검색**
    - 권한에 관계없이 검색가능.  
    - 키워드를 입력받아 LIKE 검색.
    - pageable 이용해 5건씩 출력.
    - 정확도순으로 기본 정렬    
      LIKE 검색으로 일치하는 글자수가 많은 순서로 정렬.   
      mysql 문자열 검색 적합도 순으로 정렬예정.
    - 판매여부='Y'인 상품에 대해서만 검색가능.
- [ ] 상품상세정보 조회
    - 권한에 관계없이 조회가능.
    - 조회 정보 : 상품ID, 판매자, 상품코드, 상품명, 분류, 설명, 상품ID에 해당하는 상품옵션목록.
- [ ] **장바구니에 상품추가**
    - 로그인 한 회원만 장바구니에 상품추가 가능.
    - 상품상세정보 조회 화면에서 상품추가 가능.
    - 상품추가를 위해 옵션 및 수량 필수 선택.
    - 입력 데이터 : 상품옵션ID, 구매수량
    - redis 캐싱 기능 이용해 구현.
- [ ] **장바구니에서 상품삭제**
    - 고객 본인의 장바구니 상품만 삭제가능.
    - redis 캐싱 기능 이용해 구현.
- [ ] **장바구니 상품목록 조회**
    - 고객 본인의 장바구니 상품만 조회가능.
    - 품절된 상품, 판매하지않는 상품에 대해 주문불가하도록 판매가능여부='Y'로 설정해 전달.
    - 판매가능한 상품에 대해 주문가능하도록 판매가능여부='N'로 설정해 전달.
    - redis 캐싱 기능 이용해 구현.
- [ ] 상품 주문
    - 상품주문을 위해 회원가입 필수.
    - 상품주문은 상품상세정보 페이지에서 가능.
    - 품절된 상품, 판매하지않는 상품(판매가능여부='Y')에 대해 주문불가처리.
    - 주문하려는 상품의 재고가 판매가능한 재고보다 많은 경우 주문불가.
    - 상품이 주문된 경우 판매재고량 감소.
    - 상품주문 시 필요정보 : 상품id, 상품수량, 주문자, 주문일시
- [ ] 상품 결제
    - PG사 연동 결제 api를 이용한 결제서비스 생성해 결제진행.
    - 결제 실패 시 상품주문을 취소처리.
    - 동시성제어 필요여부확인 후 개발진행.
    - 참고블로그   
      https://velog.io/@jonghyun3668/%EA%B2%B0%EC%A0%9C-%EA%B3%BC%EC%A0%95-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B8%B0PG%EC%82%AC-%EC%97%B0%EB%8F%99
    - 결제 완료 후 주문상태 '결제완료'로 변경.
- [ ] 상품 주문취소
    - 결제완료되고 배송중이지 않은 상품에 대해 결제취소가능.
    - PG사 연동 결제 api를 이용한 결제서비스 생성해 결제취소진행.
- [ ] 상품 반품신청
    - 배송완료된 상품에 대해 반품요청가능.
- [ ] 상품 교환신청
    - 배송완료된 상품에 대해 교환요청가능.
    - 동일 상품에 대해 옵션만 변경해 교환요청가능.
    - 교환요청 시 동일상품에 대해 변경한 옵션에 신규주문생성.
    - 기존주문 데이터에 교환요청한 신규주문의 주문아이디 update. (주문테이블 교환주문아이디 입력)
    - 상품에서 선택한 옵션에 대한 금액이 상이한 경우, 추가결제 혹은 부분결제취소.   
      (가능여부확인필요. 부분결제취소 불가한 경우 옵션별 동일할 수 있도록 상품등록 시 체크로직추가 필수.    
       혹은 상품옵션테이블 가격정보 제거 및 상품테이블에 가격컬럼추가.)



  
## < 주문 프로세스 FLOW CHART >
![ORDER_PROCESS_FLOW_CHART](./output/order-process-flow-chart.png)
    


## < DB ERD >
ERD 편집 : https://www.erdcloud.com/d/LuE5DFSZNinSPhYMe

![ERD](./output/my-e-commerce-erd.png)



## < 일정표 >
JIRA 일정관리 : https://glyceria.atlassian.net/jira/software/projects/MYCMP/boards/1/timeline?epic=COMPLETE3M

![PLANNER](./output/my-e-commerce-project-planner.png)


---
### 2. 고도화 (정산) - 추후 개발 시 정리필요 & 플로우차트필요
#### 판매자
- [ ] 월별 판매자별 매출확정내역 조회
  - 판매자 본인 매출확정내역만 조회가능.
  - 이전달 구매확정된 주문에 대해 매출확정하기위한 주문목록 조회. (월별판매자매출확정 테이블 조회)
- [ ] 월별 판매자별 매출정산 조회 
  - 판매자 본인 매출정산내역만 조회가능.
  - 월별판매자매출확정 내역에 대한 월별정산내역 조회. (월별판매자정산 테이블 조회)
- [ ] 월별 판매자별 정산 확정처리
  - 판매자 본인 정산내역만 확정처리가능.
  - 이전달 구매확정된 주문에 대해서만 정산처리.
  - 월별판매자정산 테이블의 확정여부 = 'Y'로 변경.

#### 관리자
- [ ] 회원 가입
  - 회원 아이디는 unique.
  - 회원가입 시 MASTER(관리자) 권한 부여.
- [ ] 월별 전체 판매자별 정산금액 조회
  - 전체 판매자들의 정산내역 조회가능.

#### 배치
- [ ] 월별 판매자별 매출확정내역 생성 (매월 10일 새벽 배치처리)
  - 이전달 구매확정된 주문에 대해서만 매출확정내역 생성.
- [ ] 월별 판매자별 정산내역 생성 (매월 10일 새벽 배치처리)
  - 월별 판매자별 매출확정내역 생성 후 배치 실행되도록 처리.
  - 월별판매자정산 테이블의 확정여부 = 'N'으로 초기생성.


---
### 3. 고도화 (인증강화)
#### 공통
- [ ] 회원가입 시 인증절차 추가
  - 이메일 또는 핸드폰 인증 진행. (추후 확정 예정)

---
### 4. 고도화 (리뷰 및 문의사항등록, 즐겨찾기)
#### 고객
- [ ] 상품목록 정렬해 조회
  - 가나다순, 평점순, 가격순으로 정렬가능.
- [ ] 상품 문의사항 등록
- [ ] 상품 주문확정 후 리뷰 작성
  - [ ] 리뷰 작성 : 주문 후 상품 주문확정한 주문자만 작성 가능.
  - [ ] 리뷰 수정 : 리뷰 작성자만 가능.
  - [ ] 리뷰 삭제 : 리뷰 작성자, 관리자만 가능.

---
### 5. 고도화 (할인)
#### 고객
- [ ] 상품할인
  - 추후 가능한 경우 설계해 진행예정.

---
### 6. 고도화 (기타)
#### 관리자
- [ ] 전체 상품 삭제 가능
  - 모든 판매자의 상품에 대해 삭제가능.

