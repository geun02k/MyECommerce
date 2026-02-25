package com.myecommerce.MyECommerce.integration.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myecommerce.MyECommerce.dto.payment.PgApprovalResult;
import com.myecommerce.MyECommerce.dto.payment.PgResult;
import com.myecommerce.MyECommerce.dto.payment.PgWebHookRequestDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.order.OrderItem;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.integration.config.TestAuditingConfig;
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import com.myecommerce.MyECommerce.repository.member.MemberRepository;
import com.myecommerce.MyECommerce.repository.payment.PaymentRepository;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.OrderStatusType.CREATED;
import static com.myecommerce.MyECommerce.type.OrderStatusType.PAID;
import static com.myecommerce.MyECommerce.type.PaymentMethodType.*;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.*;
import static com.myecommerce.MyECommerce.type.PgProviderType.*;
import static com.myecommerce.MyECommerce.type.ProductCategoryType.WOMEN_CLOTHING;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Import(TestAuditingConfig.class)
public class PaymentPgApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    ObjectMapper objectMapper;

    private Member savedCustomer;
    private Product savedProduct;

    @BeforeEach
    void setUp() {
        savedCustomer = saveCustomer();
        savedProduct = saveProduct();
    }

    @AfterEach
    void cleanUp() {
        memberRepository.delete(savedCustomer);
        productRepository.delete(savedProduct);
    }

    /* ------------------
        Test Fixtures
       ------------------ */

    /** 주문 생성 및 등록 */
    Order createCreatedOrder() {
        // 고객등록
        Member customer = savedCustomer;
        // 상품등록
        Product product = savedProduct;
        ProductOption option = product.getOptions().get(0);

        // 주문물품 생성
        OrderItem orderItem =
                OrderItem.createOrderItem(option, 1);

        // 주문 생성 및 등록
        Order order = Order.createOrder(List.of(orderItem), customer);
        return orderRepository.save(order);
    }

    /** PG 결제요청 상태의 결제 생성 및 등록 */
    Payment createInProgressPayment(Order order) {
        // 결제 생성 (READY)
        Payment payment = Payment.createPayment(order, CARD, MOCK_PG);

        // PG 결제요청 (READY -> IN_PROGRESS)
        PgResult pgResult = PgResult.builder()
                .pgTransactionId("pgTransactionId")
                .build();
        payment.requestPgPayment(pgResult);

        // 결제 저장
        return paymentRepository.save(payment);
    }

    /** PG 결제실패 상태의 결제 생성 및 등록 */
    Payment createFailedPayment(Order order) {
        // PG 결제요청한 결제 생성
        Payment payment = createInProgressPayment(order);

        // PG 결제승인 상태로 변경
        PgApprovalResult pgApprovalResult = PgApprovalResult.builder()
                .pgTransactionId("pgTransactionId")
                .approvalStatus(FAILED)
                .build();
        payment.fail(pgApprovalResult);

        // 결제 저장
        return paymentRepository.save(payment);
    }

    /** 주문자 등록 */
    Member saveCustomer() {
        MemberAuthority authority = MemberAuthority.builder()
                .authority(CUSTOMER)
                .build();

        Member member = Member.builder()
                .userId("customer")
                .password("password")
                .name("name")
                .telephone("01011112222")
                .address("address")
                .roles(List.of(authority))
                .build();

        return memberRepository.save(member);
    }

    /** 상품 등록 */
    Product saveProduct() {
        Product product = Product.builder()
                .code("productCode")
                .name("productName")
                .saleStatus(ON_SALE)
                .category(WOMEN_CLOTHING)
                .seller(10L)
                .build();

        ProductOption productOption = ProductOption.builder()
                .optionCode("optionCode")
                .optionName("optionName")
                .price(new BigDecimal("10000"))
                .quantity(10)
                .product(product)
                .build();

        product.setOptions(List.of(productOption));

        return productRepository.save(product);
    }

    /* ------------------
        Helper Methods
       ------------------ */

   /* --------------------------
        PG 결제승인 웹훅 성공 Test
       ------------------------- */

    @Test
    @DisplayName("PG 결제승인 웹훅 성공 - 결제승인 웹훅 요청 시 주문, 결제상태 변경 후 200 OK 반환")
    void handlePgApprovalWebhook_shouldReturn200_whenApprovalRequest() throws Exception {
        // given
        // 주문
        Order order = createCreatedOrder();
        // 주문에 대한 PG 결제요청한 결제생성
        createInProgressPayment(order); // 트랜잭션ID = pgTransactionId인 결제

        // PG 승인 응답
        PgWebHookRequestDto request = PgWebHookRequestDto.builder()
                .pgTransactionId("pgTransactionId")
                .approvalStatus(APPROVED) // 승인완료
                .paidAmount(new BigDecimal("10000"))
                .vatAmount(new BigDecimal("1000"))
                .approvalAt(LocalDateTime.now())
                .build();

        // when
        // then
        // 결제승인 정상 처리되어 200 OK 응답
        mockMvc.perform(post("/api/webhook/pg/approval")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 결제상태 검증 (통합테스트의 목적은 흐름 검증으로, 도메인로직 검증이 아니므로 부가세 등의 변경은 미검증)
        Payment resultPayment = paymentRepository.findByPgTransactionId("pgTransactionId").orElseThrow();
        assertEquals(APPROVED, resultPayment.getPaymentStatus()); // 결제상태 IN_PROGRESS -> APPROVED

        // 주문상태 검증
        Long resultOrderId = resultPayment.getOrder().getId();
        Order resultOrder = orderRepository.findById(resultOrderId).orElseThrow();
        assertEquals(PAID, resultOrder.getOrderStatus()); // 주문상태 CREATED -> PAID
    }

    @Test
    @DisplayName("PG 결제승인 웹훅 성공 - 결제실패 웹훅 요청 시 주문상태 CREATED 유지 및 200 OK 반환")
    void handlePgApprovalWebhook_shouldReturn200_whenFailRequest() throws Exception {
        // given
        // 주문
        Order order = createCreatedOrder();
        // 주문에 대한 PG 결제요청한 결제생성
        createInProgressPayment(order); // 트랜잭션ID = pgTransactionId인 결제

        // PG 승인 응답
        PgWebHookRequestDto request = PgWebHookRequestDto.builder()
                .pgTransactionId("pgTransactionId")
                .approvalStatus(FAILED) // 승인실패
                .approvalAt(LocalDateTime.now())
                .build();

        // when
        // then
        // 결제실패 정상 처리되어 200 OK 응답
        mockMvc.perform(post("/api/webhook/pg/approval")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 결제상태 검증
        Payment resultPayment = paymentRepository.findByPgTransactionId("pgTransactionId").orElseThrow();
        assertEquals(FAILED, resultPayment.getPaymentStatus()); // 결제상태 IN_PROGRESS -> FAILED

        // 주문상태 검증
        Long resultOrderId = resultPayment.getOrder().getId();
        Order resultOrder = orderRepository.findById(resultOrderId).orElseThrow();
        assertEquals(CREATED, resultOrder.getOrderStatus()); // 주문상태 CREATED 유지
    }

   /* --------------------------------
        PG 결제승인 웹훅 멱등성 성공 Test
       ------------------------------- */

    @Test
    @DisplayName("PG 결제승인 웹훅 멱등성 성공 - 이미 결제종결된 경우 주문, 결제상태 변경하지 않고 200 OK 반환")
    void handlePgApprovalWebhook_shouldReturn200_whenDuplicatedRequest() throws Exception {
        // given
        // 결제
        Order order = createCreatedOrder();
        // 승인실패한 결제
        createFailedPayment(order); // 트랜잭션ID = pgTransactionId인 결제

        // PG 승인 응답 (이미 승인종결된 동일 트랜잭션ID에 대한 승인완료 응답)
        PgWebHookRequestDto request = PgWebHookRequestDto.builder()
                .pgTransactionId("pgTransactionId")
                .approvalStatus(APPROVED)
                .approvalAt(LocalDateTime.now())
                .build();

        // when
        // then
        // 결제실패 정상 처리되어 200 OK 응답
        mockMvc.perform(post("/api/webhook/pg/approval")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // 결제상태 검증
        Payment resultPayment = paymentRepository.findByPgTransactionId("pgTransactionId").orElseThrow();
        assertEquals(FAILED, resultPayment.getPaymentStatus()); // 결제상태 FAILD 유지 (상태역전 X)

        // 주문상태 검증
        Long resultOrderId = resultPayment.getOrder().getId();
        Order resultOrder = orderRepository.findById(resultOrderId).orElseThrow();
        assertEquals(CREATED, resultOrder.getOrderStatus()); // 주문상태 CREATED 유지
    }

   /* --------------------------
        PG 결제승인 웹훅 실패 Test
       ------------------------- */

    // 외부 PG가 잘못된 요청 보낼 수도 있어서 반드시 필요
    @Test
    @DisplayName("PG 결제승인 웹훅 실패 - 트랜잭션ID에 대한 결제 미존재 시 404 NotFound 반환")
    void handlePgApprovalWebhook_shouldReturn404_whenNotExistsTransactionId() throws Exception {
        // given
        // 결제
        Order order = createCreatedOrder();
        // 주문에 대한 PG 결제요청한 결제생성
        createInProgressPayment(order); // 트랜잭션ID = pgTransactionId인 결제

        // PG 승인 응답 (존재하지 않는 트랜잭션ID에 대한 승인 응답)
        PgWebHookRequestDto request = PgWebHookRequestDto.builder()
                .pgTransactionId("not_existing_transactionId")
                .approvalStatus(APPROVED)
                .approvalAt(LocalDateTime.now())
                .build();

        // when
        // then
        // 요청에 대한 수행 불가로 404 NotFound 응답
        mockMvc.perform(post("/api/webhook/pg/approval")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode")
                        .value("PG_TRANSACTION_ID_NOT_EXISTS"));

        // 결제상태 미변경 검증
        Payment resultPayment = paymentRepository.findByPgTransactionId("pgTransactionId").orElseThrow();
        assertEquals(IN_PROGRESS, resultPayment.getPaymentStatus()); // 결제상태 IN_PROGRESS 유지

        // 주문상태 미변경 검증
        Long resultOrderId = resultPayment.getOrder().getId();
        Order resultOrder = orderRepository.findById(resultOrderId).orElseThrow();
        assertEquals(CREATED, resultOrder.getOrderStatus()); // 주문상태 CREATED 유지
    }

    @Test
    @DisplayName("PG 결제승인 웹훅 실패 - 잘못된 승인상태 요청 시 400 BadRequest 반환")
    void handlePgApprovalWebhook_shouldReturn400_whenInvalidApprovalStatus() throws Exception {
        // given
        // PG 승인 응답
        String invalidJson = """ 
            {
                "pgTransactionId": "pgTransactionId",
                "approvalStatus": "INVALID_STATUS",
                "approvalAt": "2025-01-01T10:00:00"
            }
        """;
        // when
        // then
        // 입력값 검증 실패 시 400 BadRequest
        mockMvc.perform(post("/api/webhook/pg/approval")
                        .contentType(APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value("INVALID_VALUE"));
    }
}
