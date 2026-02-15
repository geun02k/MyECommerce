package com.myecommerce.MyECommerce.service.payment;

import com.myecommerce.MyECommerce.dto.payment.PgApiResponse;
import com.myecommerce.MyECommerce.dto.payment.PgApprovalResult;
import com.myecommerce.MyECommerce.dto.payment.PgResult;
import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.type.PgProviderType;

public interface PgClient {

    /** PG 결제대행사 코드 반환 **/
    PgProviderType getProvider();

    /** 결제 요청 **/
    PgApiResponse<PgResult> requestPayment(Payment payment);

    /** 결제 승인 **/
    PgApprovalResult approvePayment(Payment payment);

    // 결제 취소

    // 부분 결제 취소

}
