package com.myecommerce.MyECommerce.dto.order;

import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.type.OrderStatusType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseOrderDto {

    private Long id;

    private String orderNumber;

    private Member buyer;

    private BigDecimal totalPrice;

    private OrderStatusType orderStatus;

    private LocalDateTime orderedAt;

}
