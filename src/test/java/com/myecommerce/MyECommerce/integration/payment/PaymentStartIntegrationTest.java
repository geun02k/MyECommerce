package com.myecommerce.MyECommerce.integration.payment;

import com.myecommerce.MyECommerce.dto.payment.RequestPaymentDto;
import com.myecommerce.MyECommerce.dto.payment.ResponsePaymentDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.entity.order.OrderItem;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.integration.config.TestAuditingConfig;
import com.myecommerce.MyECommerce.repository.Order.OrderRepository;
import com.myecommerce.MyECommerce.repository.member.MemberRepository;
import com.myecommerce.MyECommerce.repository.payment.PaymentRepository;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import com.myecommerce.MyECommerce.service.payment.PaymentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;

import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static com.myecommerce.MyECommerce.type.PaymentMethodType.*;
import static com.myecommerce.MyECommerce.type.PaymentStatusType.IN_PROGRESS;
import static com.myecommerce.MyECommerce.type.ProductCategoryType.WOMEN_CLOTHING;
import static com.myecommerce.MyECommerce.type.ProductSaleStatusType.ON_SALE;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestAuditingConfig.class)
public class PaymentStartIntegrationTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /* ------------------
        Test Fixtures
       ------------------ */

    private Member savedMember;
    private Product savedProduct;
    private Order savedOrder;
    private Long createdPaymentId;

    @BeforeEach
    void setUp() {
        // given
        // 결제를 위한 회원
        savedMember = saveCustomer();
        // 주문을 위한 상품
        savedProduct = saveProduct();

        // 결제를 위한 주문 등록
        savedOrder  = saveOrder(savedMember, savedProduct.getOptions().get(0));
    }

    @AfterEach
    void cleanUp() {
        transactionTemplate.executeWithoutResult(status -> {
            paymentRepository.deleteById(createdPaymentId);
            orderRepository.deleteById(savedOrder.getId());
            productRepository.deleteById(savedProduct.getId());
            memberRepository.deleteById(savedMember.getId());
        });
    }
    /* ------------------
        Helper Methods
       ------------------ */

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

    /** 주문 등록 */
    Order saveOrder(Member member, ProductOption option) {
        // 주문물품 생성
        OrderItem orderItem =
                OrderItem.createOrderItem(option, 1);

        // 주문 생성 및 등록
        Order order = Order.createOrder(List.of(orderItem), member);
        return orderRepository.save(order);
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
        결제시작 Test
       ------------------ */

    @Test
    @DisplayName("결제시작 성공 - IN_PROGRESS 결제상태 및 PG 결제정보 반환")
    void startPayment_shouldReturnPgInfo_whenValidRequest() {
        // given
        // 결제를 위한 회원
        Member member = savedMember;
        // 결제를 위한 주문 등록
        Order order = savedOrder;

        // 결제 요청
        RequestPaymentDto request = RequestPaymentDto.builder()
                .orderId(order.getId())
                .paymentMethod(CARD)
                .build();

        // when
        ResponsePaymentDto response = paymentService.startPayment(request, member);

        // 생성된 결제 데이터 수동 삭제를 위한 변수 할당
        createdPaymentId = response.getPaymentId();

        // then
        assertEquals(order.getId(), response.getOrderId());
        assertEquals(IN_PROGRESS, response.getPaymentStatus());
        assertEquals("redirectUrl", response.getRedirectUrl());
    }

}
