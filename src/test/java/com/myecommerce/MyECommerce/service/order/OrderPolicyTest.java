package com.myecommerce.MyECommerce.service.order;

import com.myecommerce.MyECommerce.dto.order.RequestOrderDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.entity.member.MemberAuthority;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.repository.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.myecommerce.MyECommerce.type.MemberAuthorityType.CUSTOMER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderPolicyTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    private OrderPolicy orderPolicy;

    /* ------------------
        Test Fixtures
       ------------------ */

    /** productRepository.findByIdIn() 예상 결과 */
    private List<Product> expectedProductFoundByIdIn() {
        return List.of(Product.builder()
                .id(1L)
                .code("productCode")
                .options(List.of(ProductOption.builder()
                        .optionCode("optionCode")
                        .quantity(20)
                        .build()))
                .build());
    }

    /* ------------------
        Helper Methods
       ------------------ */
    /* ----------------------
        주문 생성 Tests
       ---------------------- */
    @Test
    @DisplayName("주문생성 정책 통과 - 유효한 정책 요청 시 정책 통과")
    void validateCreate_shouldPass_whenAllValid() {
        // given
        // 요청 고객
        Member member = Member.builder()
                .userId("customer")
                .roles(List.of(MemberAuthority.builder()
                        .authority(CUSTOMER)
                        .build()))
                .build();
        // 요청 주문물품
        RequestOrderDto requestItem = RequestOrderDto.builder()
                .productId(1L)
                .optionCode("optionCode")
                .quantity(10)
                .build();

        given(productRepository.findByIdIn(any()))
                .willReturn(expectedProductFoundByIdIn());

        // when
        // then
        assertDoesNotThrow(() ->
                orderPolicy.validateCreate(List.of(requestItem), member));
    }

}