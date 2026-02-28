package com.myecommerce.MyECommerce.dto.payment;

import com.myecommerce.MyECommerce.type.PaymentStatusType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PgApprovalResult {

    private String pgTransactionId;

    private BigDecimal paidAmount;

    private BigDecimal vatAmount;

    private PaymentStatusType approvalStatus;

    private LocalDateTime approvalAt;

    /** PG 요청 DTO -> 내부 전달 DTO로 변환 **/
    public static PgApprovalResult from(PgWebHookRequestDto requestDto) {
        return PgApprovalResult.builder()
                .pgTransactionId(requestDto.getPgTransactionId())
                .paidAmount(requestDto.getPaidAmount())
                .vatAmount(requestDto.getVatAmount())
                .approvalStatus(requestDto.getApprovalStatus())
                .approvalAt(requestDto.getApprovalAt())
                .build();
    }
}
