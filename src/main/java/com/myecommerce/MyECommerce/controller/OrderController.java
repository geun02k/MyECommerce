package com.myecommerce.MyECommerce.controller;

import com.myecommerce.MyECommerce.dto.order.RequestOrderDto;
import com.myecommerce.MyECommerce.dto.order.ResponseOrderDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 주문 생성 post /order
     **/
    @PostMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER')")
    public ResponseEntity<ResponseOrderDto> createOrder(
            @RequestBody @Valid List<RequestOrderDto> requestOrderDto,
            @AuthenticationPrincipal Member member) {

        ResponseOrderDto responseOrderDto =
                orderService.createOrder(requestOrderDto, member);

        return ResponseEntity.ok(responseOrderDto);
    }
}
