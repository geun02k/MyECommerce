package com.myecommerce.MyECommerce.controller;

import com.myecommerce.MyECommerce.dto.payment.PgApprovalResult;
import com.myecommerce.MyECommerce.dto.payment.PgWebHookRequestDto;
import com.myecommerce.MyECommerce.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebHookController {

    private final PaymentService paymentService;

    /**
     * PG 결제 요청 응답 웹훅 post /api/webhook/pg/approval
     **/
    @PostMapping("/pg/approval")
    public ResponseEntity<Void> handlePgApprovalWebhook(
            @RequestBody @Validated PgWebHookRequestDto request) {

        // TODO: 웹훅 보안을 위해 PG 서명 검증 필수

        // PG 요청 DTO -> 내부 전달 DTO로 변환
        PgApprovalResult pgApprovalResult = PgApprovalResult.from(request);

        paymentService.handlePgWebHook(pgApprovalResult);

        return ResponseEntity.ok().build();
    }

}
