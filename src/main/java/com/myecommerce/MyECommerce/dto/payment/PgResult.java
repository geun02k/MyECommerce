package com.myecommerce.MyECommerce.dto.payment;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PgResult {

    private String pgTransactionId;

    // PG사 제공, 사용자를 PG 결제 화면으로 보내기 위한 URL
    // 민감 정보를 우리 서버가 만지지 않게 하기 위해 우리 서비스에서 PG 결제 화면으로 이동하도록 경로 제공
    private String redirectUrl;

}
