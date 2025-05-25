package com.myecommerce.MyECommerce.controller;

import com.myecommerce.MyECommerce.dto.cart.RequestCartDto;
import com.myecommerce.MyECommerce.dto.cart.ResponseCartDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.service.cart.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 상품 단건 추가 post /cart
     **/
    @PostMapping
    @PreAuthorize("hasAuthority('CUSTOMER')")
    public ResponseEntity<String> addCart(
            @RequestBody @Valid RequestCartDto requestCartDto,
            @AuthenticationPrincipal Member member) {

        ResponseCartDto responseCartDto =
                cartService.addCart(requestCartDto, member);

        return ResponseEntity.ok(
                "[" + responseCartDto.getProductName() + "]"
                + " 상품이 장바구니에 추가되었습니다.");
    }

    /**
     * 장바구니 상품 목록 삭제 delete /cart/{redisKey}
     **/

    /**
     * 장바구니 상품 목록 조회 get /cart/{id}
     **/

}
