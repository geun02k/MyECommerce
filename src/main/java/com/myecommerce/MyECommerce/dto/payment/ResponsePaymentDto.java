package com.myecommerce.MyECommerce.dto.payment;

import com.myecommerce.MyECommerce.type.PaymentStatusType;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePaymentDto { // 결제를 계속 진행할 수 있는 수단 반환

    private Long paymentId;

    private Long orderId;

    private PaymentStatusType paymentStatus;

    private String redirectUrl;

    private String failCode;

    private String failMessage;

}
