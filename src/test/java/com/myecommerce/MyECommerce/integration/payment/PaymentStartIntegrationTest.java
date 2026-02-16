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
import org.springframework.transaction.annotation.Transactional;
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
    private OrderRepository orderRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /* ------------------
        Test Fixtures
       ------------------ */

    private Member savedCustomer;
    private Order savedOrder;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        // 결제를 위한 회원 등록
        savedCustomer = saveCustomer();
        // 결제를 위한 주문 등록
        savedOrder = saveOrder();
    }

    @AfterEach
    void cleanUp() {
        transactionTemplate.executeWithoutResult(status -> {
            Long productId = savedOrder.getItems().get(0).getProduct().getId();

            orderRepository.deleteById(savedOrder.getId());
            productRepository.deleteById(productId);
            memberRepository.deleteById(savedCustomer.getId());
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
    Order saveOrder() {
        // 상품등록
        Product savedProduct = saveProduct();
        ProductOption savedOption = savedProduct.getOptions().get(0);

        // 주문물품 생성
        OrderItem orderItem =
                OrderItem.createOrderItem(savedOption, 1);

        // 주문 생성 및 등록
        Order order = Order.createOrder(List.of(orderItem), savedCustomer);
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
        결제생성 Test
       ------------------ */

    @Test
    @DisplayName("결제생성 성공 - IN_PROGRESS 결제상태 및 PG 결제정보 반환")
    @Transactional // 테스트 끝나면 자동 롤백
    void startPayment_shouldReturnPgInfo_whenValidRequest() {
        // given
        RequestPaymentDto request = RequestPaymentDto.builder()
                .orderId(savedOrder.getId())
                .paymentMethod(CARD)
                .build();
        Member member = savedCustomer;

        // when
        ResponsePaymentDto response =
                paymentService.startPayment(request, member);

        // then
        assertEquals(savedOrder.getId(), response.getOrderId());
        assertEquals(IN_PROGRESS, response.getPaymentStatus());
        assertEquals("redirectUrl", response.getRedirectUrl());
    }

}
