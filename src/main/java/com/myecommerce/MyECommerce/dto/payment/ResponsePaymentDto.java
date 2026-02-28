package com.myecommerce.MyECommerce.dto.payment;

import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.type.PaymentStatusType;
import lombok.*;

@Getter
@Builder(access = AccessLevel.PRIVATE) // Builder 접근 제한
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 리플렉션으로만(?) 접근 가능
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Lombok Builder의 내부 구현으로 @Builder 사용 시 필수
public class ResponsePaymentDto { // 결제를 계속 진행할 수 있는 수단 반환

    private Long paymentId;

    private Long orderId;

    private PaymentStatusType paymentStatus;

    private String redirectUrl;

    private String failCode;

    private String failMessage;

    /** 응답 객체 생성 **/
    public static ResponsePaymentDto from(Payment payment,
                                          PgApiResponse<PgResult> pgApiResponse) {

        if (payment == null) {
            throw new IllegalArgumentException("payment must not be null");
        }

        if (pgApiResponse == null) {
            throw new IllegalArgumentException("pgApiResponse must not be null");
        }

        ResponsePaymentDto response;
        if (pgApiResponse.isSuccess()) {
            PgResult pgResult = pgApiResponse.getData();
            response = base(payment)
                    .redirectUrl(pgResult != null ? pgResult.getRedirectUrl() : null)
                    .build();
        } else {
            PgError pgError = pgApiResponse.getError();
            response = base(payment)
                    .failCode(pgError != null ? pgError.getCode() : null)
                    .failMessage(pgError != null ? pgError.getMessage() : null)
                    .build();
        }
        return response;
    }

    // 공통 응답 데이터 셋팅
    private static ResponsePaymentDtoBuilder base(Payment payment) {
        return ResponsePaymentDto.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrder().getId())
                .paymentStatus(payment.getPaymentStatus());
    }
}
