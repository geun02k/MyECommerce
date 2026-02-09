package com.myecommerce.MyECommerce.controller;

import com.myecommerce.MyECommerce.dto.payment.PgApprovalResult;
import com.myecommerce.MyECommerce.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<Void> handlePgApprovalWebhook(
            @RequestBody PgApprovalResult pgApprovalResult) {

        paymentService.handlePgWebHook(pgApprovalResult);

        return ResponseEntity.ok().build();
    }

}
