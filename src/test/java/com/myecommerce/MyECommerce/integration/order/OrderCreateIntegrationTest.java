package com.myecommerce.MyECommerce.integration.order;

import com.myecommerce.MyECommerce.dto.order.RequestOrderDto;
import com.myecommerce.MyECommerce.dto.order.ResponseOrderDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.order.OrderItem;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.integration.config.TestAuditingConfig;
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import com.myecommerce.MyECommerce.repository.member.MemberRepository;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import com.myecommerce.MyECommerce.service.order.OrderService;
import com.myecommerce.MyECommerce.service.redis.RedisMultiDataService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;
import static com.myecommerce.MyECommerce.type.ProductCategoryType.WOMEN_CLOTHING;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static com.myecommerce.MyECommerce.type.RedisNamespaceType.STOCK;
import static org.junit.jupiter.api.Assertions.*;

// TODO: 가독성 향상을 위해 옵션코드와 수량, 금액이 표현될 수 있도록 객체를 테스트 메서드 내에서 생성하도록 개선할 것.
@SpringBootTest
@ActiveProfiles("test")
@Import(TestAuditingConfig.class)
public class OrderCreateIntegrationTest {

    private static final Logger log = LoggerFactory.getLogger(OrderCreateIntegrationTest.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private RedisMultiDataService redisMultiDataService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductOptionRepository productOptionRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /* ------------------
        Test Fixtures
       ------------------ */

    private Member savedMember;
    private Product savedProduct;
    private Map<String, Object> savedCacheStock;

    @BeforeEach
    void setUp() {
        // 주문을 위한 회원 등록
        savedMember = saveMember();
        // 주문을 위한 상품 등록
        savedProduct = saveProducts();
        // 캐시 재고 차감을 위한 캐시 재고 등록
        savedCacheStock = saveCacheStockOfOptions(savedProduct);
    }

    @AfterEach
    void cleanUp() {
        // 상품등록으로 인한 테스트 데이터 정리: redis 재고 데이터 delete
        redisMultiDataService.deleteMultiData(
                savedCacheStock.keySet().stream().toList());
        // 장바구니에 상품추가로 인한 장바구니 데이터 정리: redis 장바구니 데이터 delete
       redisMultiDataService.deleteMultiData(
               savedProduct.getOptions().stream()
                       .map(option -> String.valueOf(option.getId()))
                       .toList());
    }

    /** 고객권한 사용자 */
    Member customer(Long memberId) {
        return Member.builder()
                .id(memberId)
                .userId("customer")
                .roles(List.of(MemberAuthority.builder()
                        .authority(CUSTOMER)
                        .build()))
                .build();
    }

    /** optionIds로 전달된 옵션에 대한 요청 상품 옵션 목록 */
    List<RequestOrderDto> givenRequestOrders(List<Long> optionIds) {
        // 간접적으로 옵션 순서 순서 단정가능.
        // optionIds 리스트의 첫 번째 요소(get(0))에 수량 1,
        // 두 번째 요소(get(1))에 수량 2를 순서대로 할당합니다.
        List<RequestOrderDto> requestOrders = new ArrayList<>();
        int quantity = 1;
        for(Long optionId : optionIds) {
            RequestOrderDto request = RequestOrderDto.builder()
                    .productOptionId(optionId)
                    .quantity(quantity++)
                    .build();
            requestOrders.add(request);
        }
        return requestOrders;
    }

    /* ------------------
        Helper Method
       ------------------ */

    /** 주문자 등록 */
    Member saveMember() {
        Member member = Member.builder()
                .userId("customer")
                .password("password")
                .name("name")
                .telephone("01011112222")
                .address("address")
                .build();
        return memberRepository.save(member);
    }

    /** 1건의 상품 등록 및 하위 옵션 2건 등록 */
    Product saveProducts() {
        // 상품
        Product product = Product.builder()
                .code("productCode")
                .name("productName")
                .saleStatus(ON_SALE)
                .category(WOMEN_CLOTHING)
                .seller(1L)
                .build();

        // 상품 옵션
        List<ProductOption> options = new ArrayList<>();
        for(int o = 1; o <= 2; o++) {
            ProductOption option = ProductOption.builder()
                    .product(product)
                    .optionCode("optionCode" + o)
                    .optionName("optionName" + o)
                    .price(new BigDecimal("1000").multiply(BigDecimal.valueOf(o)))
                    .quantity(20)
                    .build();
            options.add(option);
        }
        product.setOptions(options);

        // 상품 및 옵션 등록
        return productRepository.save(product);
    }

    /** redis 캐시 재고 데이터 등록 */
    Map<String, Object> saveCacheStockOfOptions(Product product) {
        Map <String, Object> stockMap = new HashMap<>();

        for(ProductOption option : product.getOptions()) {
            String key = createStockRedisKey(option);
            stockMap.put(key, option.getQuantity());
        }
        redisMultiDataService.saveMultiData(stockMap);

        return stockMap;
    }

    /** 테스트 데이터 정리 */
    void cleanUpSavedDataForConcurrency(List<Long> orderIds,
                                        Product savedProduct,
                                        Member savedMember) {
        transactionTemplate.executeWithoutResult(status -> {
            orderRepository.deleteByIdIn(orderIds);
            productRepository.deleteById(savedProduct.getId());
            memberRepository.deleteById(savedMember.getId());
        });
    }

    // 금액을 BigDecimal, 첫째 자리에서 반올림
    BigDecimal price(String price) {
        return new BigDecimal(price).setScale(0, RoundingMode.HALF_UP);
    }

    // 상품 옵션 목록을 조회해 List -> Map으로 변환
    Map<String, ProductOption> findProductOptionMap(Long productId) {
        // 상품ID에 대한 옵션 조회
        List<ProductOption> options =
                productOptionRepository.findByProductId(productId);

        // 옵션 List -< Map 변환
        return options.stream()
                .collect(Collectors.toMap(
                        ProductOption::getOptionCode, o -> o));
    }
    // 주문 물품 List -> Map 으로 변환
    Map<Long, OrderItem> orderItemToMap(Order order) {
        return order.getItems().stream()
                .collect(Collectors.toMap(
                        item -> item.getOption().getId(),
                        item -> item
                ));
    }

    // 주문생성 반환값으로 주문 Entity 조회
    Order findSavedOrder(ResponseOrderDto responseOrderDto) {
        return orderRepository.findById(responseOrderDto.getId()).orElseThrow();
    }

    // 재고 redis 캐시 데이터 반환
    List<Object> returnRedisCacheStock(Map<String, ProductOption> productOptionMap) {
        ProductOption option1 = productOptionMap.get("optionCode1");
        ProductOption option2 = productOptionMap.get("optionCode2");

        String redisKey1 = createStockRedisKey(option1);
        String redisKey2 = createStockRedisKey(option2);
        List<String> redisKeys = List.of(redisKey1, redisKey2);

        return redisMultiDataService.getMultiData(redisKeys);
    }

    // 재고 redis key 생성
    String createStockRedisKey(ProductOption option) {
        return STOCK + ":" + option.getId();
    }

    // 상품에 대한 옵션 아이디 목록 조회
    List<Long> optionIds(Product product) {
        return product.getOptions().stream()
                .map(ProductOption::getId)
                .toList();
    }

    /** 주문요청 10건 동시 실행 -> 주문 10건 생성
     *  : 지저분한 기술적 코드를 메서드로 분리 */
    List<Long> executeConcurrentOrderRequests(List<RequestOrderDto> requestOrders,
                                              Member member) throws InterruptedException {
        // 트랜잭션 생성
        int threadCount = 10;
        // 동시에 실행될 스레드 풀
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        // 모든 스레드가 끝날 때까지 대기 (해당 코드 없으면 테스트가 중간에 끝남)
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        List<Long> orderIds = Collections.synchronizedList(new ArrayList<>());
        // 여러 트랜잭션 동시 실행. (각 submit -> 각 작업 트랜잭션을 큐에 넣기)
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> { // 생성한 작업 스레드를 큐에 넣고 비동기 실행.
                try {
                    readyLatch.countDown(); // 작업 스레드 생성 완료 알림
                    startSignal.await();    // 생성된 현재 작업 스레드 대기

                    // 주문 생성
                    ResponseOrderDto response =
                            orderService.createOrder(requestOrders, member);

                    // 데이터 일괄 삭제를 위한 주문 키 추가
                    orderIds.add(response.getId());

                } catch(Exception e) {
                    log.error(e.getMessage(), e);

                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();      // 작업 스레드 전체 생성 대기 (메인 스레드는 모든 작업 스레드가 큐에 등록되어 실행가능한 상태가 될 때까지 대기)
        startSignal.countDown(); // 전체 작업 스레드 일제히 시작
        doneLatch.await();       // 작업 스레드 전체 종료 대기 (메인 스레드는 모든 작업 스레드 doneLatch.countDown 완료될 때까지 대기)
        // 스레드풀 자원 종료
        executor.shutdown();

        return orderIds;
    }

    /* ------------------
        주문생성 Test
       ------------------ */

    @Test
    @DisplayName("주문생성 성공 - 상품옵션 시스템 식별자(productOptionId)로만 주문생성")
    @Transactional // 테스트 끝나면 자동 롤백
    void createOrder_shouldCreateOrder_whenUseOnlyProductOptionId() {
        // given
        // 요청 사용자
        Long memberId = savedMember.getId();
        Member member = customer(memberId);
        // 요청 주문 (단일 상품 2개의 옵션으로, 요청 주문 2건 생성)
        List<Long> optionIds = optionIds(savedProduct);
        List<RequestOrderDto> requestOrder = givenRequestOrders(optionIds);

        // when
        ResponseOrderDto response =
                orderService.createOrder(requestOrder, member);

        // then
        Order savedOrder = findSavedOrder(response);

        // 주문 물품의 상품옵션 식별자 연결 검증
        Map<Long, OrderItem> orderItems = orderItemToMap(savedOrder);
        OrderItem firstItem = orderItems.get(optionIds.get(0));
        OrderItem secondItem = orderItems.get(optionIds.get(1));
        // 1. 요청한 optionId에 해당하는 OrderItem이 생성되었는지 검증
        assertNotNull(firstItem);
        assertNotNull(secondItem);
        // 2. 요청한 optionId와 실제 orderItem이 참조하는 옵션 ID가 일치하는지 검증
        assertEquals(optionIds.get(0), firstItem.getOption().getId());
        assertEquals(optionIds.get(1), secondItem.getOption().getId());

        // 주문 생성 결과 검증
        assertNotNull(response.getId());
        assertNotNull(response.getOrderNumber());
        assertEquals(member.getUserId(), response.getBuyerUserId());
        assertEquals(price("5000"), response.getTotalPrice()); // 1000 + 2000*2 = 5000
        assertEquals(CREATED, response.getOrderStatus());
        assertNotNull(response.getOrderedAt());
        // 물품별 주문 수량 검증
        assertEquals(2, savedOrder.getItems().size());
        assertEquals(1, firstItem.getQuantity());
        assertEquals(2, secondItem.getQuantity());
        // 특정 주문 물품 금액 검증
        assertEquals(price("2000"), secondItem.getUnitPrice()); // 하나 당 2000원
        assertEquals(price("4000"), secondItem.getTotalPrice()); // 2000원 * 2개 = 4000원
    }

    @Test
    @DisplayName("주문생성 성공 - 주문 등록 시 옵션 재고 차감")
    @Transactional // 테스트 끝나면 자동 롤백
    void createOrder_shouldDecreaseOptionStock_whenOrderCreated() {
        // given
        // 요청 사용자
        Long memberId = savedMember.getId();
        Member member = customer(memberId);
        // 요청 주문 (단일 상품 2개의 옵션으로, 요청 주문 2건 생성)
        List<Long> optionIds = optionIds(savedProduct);
        List<RequestOrderDto> requestOrder = givenRequestOrders(optionIds);

        // when
        ResponseOrderDto response =
                orderService.createOrder(requestOrder, member);

        // then
        // 재고 차감 여부 검증
        Map<String, ProductOption> optionMapDecreasedStock =
                findProductOptionMap(savedProduct.getId());
        // 재고 20개에서 1개 차감
        assertEquals(19, optionMapDecreasedStock.get("optionCode1").getQuantity());
        // 재고 20개에서 2개 차감
        assertEquals(18, optionMapDecreasedStock.get("optionCode2").getQuantity());
    }

    // TODO: 통합테스트와 동시성 테스트 클래스 분리
    // TODO: 동시성 테스트와 영속성 컨텍스트 오염 방지를 위한 방법 고민해 개선하기
    @Test
    @DisplayName("주문생성 성공 - 동시에 여러 주문 요청 시 옵션 재고 차감")
    void createOrder_shouldDecreaseOptionStock_whenConcurrentlyOrderRequest()
            throws InterruptedException {
        // given
        // 요청 사용자
        Member member = customer(savedMember.getId());
        // 요청 주문 (단일 상품 2개의 옵션으로, 요청 주문 2건 생성)
        List<Long> optionIds = optionIds(savedProduct);
        List<RequestOrderDto> requestOrders = givenRequestOrders(optionIds);

        // when
        // 주문 요청 동시 10번 수행 (테스트 목적: 동시 요청 시 재고가 안전하게 차감되는가 -> 동시성 메서드를 테스트에서 분리)
        List<Long> orderIds = executeConcurrentOrderRequests(requestOrders, member);

        // then
        // 동시에 실행 요청했을 때 결과가 안전한지 검증.
        // 첫 트랜잭션이 옵션 행을 잡고 있을 것이므로 다른 트랜잭션은
        // 대기하고 첫 트랜잭션 작업 종류 후 순차 처리하므로 재고가 마이너스 되지 않음.
        // 재고 차감 여부 검증
        Map<String, ProductOption> optionMapDecreasedStock =
                findProductOptionMap(savedProduct.getId());
        ProductOption resultOption1 = optionMapDecreasedStock.get("optionCode1");
        ProductOption resultOption2 = optionMapDecreasedStock.get("optionCode2");
        // 1. DB 재고가 정상 차감 -> 락이 제대로 걸렸음을 확인 가능
        assertEquals(10, resultOption1.getQuantity());
        assertEquals(0, resultOption2.getQuantity());
        // 2. Redis Cache 재고 원자성 차감
        List<Object> cacheStock = returnRedisCacheStock(optionMapDecreasedStock);
        int option1CacheStock = Integer.parseInt(cacheStock.get(0).toString());
        int option2CacheStock = Integer.parseInt(cacheStock.get(1).toString());
        assertEquals(10, option1CacheStock);
        assertEquals(0, option2CacheStock);

        // @Transactional 적용 불가로 명시적 재고 delete
        cleanUpSavedDataForConcurrency(orderIds, savedProduct, savedMember);
    }

}
