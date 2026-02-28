package com.myecommerce.MyECommerce.controller;

import com.myecommerce.MyECommerce.dto.payment.RequestPaymentDto;
import com.myecommerce.MyECommerce.dto.payment.ResponsePaymentDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 결제 생성 post /payment
     **/
    @PostMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<ResponsePaymentDto> createPayment(
            @RequestBody @Valid RequestPaymentDto requestPaymentDto,
            @AuthenticationPrincipal Member member) {

        ResponsePaymentDto response =
                paymentService.startPayment(requestPaymentDto, member);

        return ResponseEntity.ok(response);
    }

}
