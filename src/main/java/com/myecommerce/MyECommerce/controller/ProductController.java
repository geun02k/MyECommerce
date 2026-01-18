package com.myecommerce.MyECommerce.controller;

import com.myecommerce.MyECommerce.dto.product.RequestModifyProductDto;
import com.myecommerce.MyECommerce.dto.product.RequestProductDto;
import com.myecommerce.MyECommerce.dto.product.RequestSearchProductDto;
import com.myecommerce.MyECommerce.dto.product.ResponseProductDto;
import com.myecommerce.MyECommerce.dto.product.ResponseSearchDetailProductDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.service.product.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    /**
     * 상품, 상품옵션목록 등록 post /product
     **/
    @PostMapping
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<ResponseProductDto> registerProduction(
            @RequestBody @Valid RequestProductDto production,
            @AuthenticationPrincipal Member member) {

        return ResponseEntity.ok(
                productService.registerProduct(production, member));
    }

    /**
     * 상품, 상품옵션 수정 put /product/{id}
     **/
    @PutMapping
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<ResponseProductDto> modifyProduction(
            @RequestBody @Valid RequestModifyProductDto production,
            @AuthenticationPrincipal Member member) {

        return ResponseEntity.ok(
                productService.modifyProduct(production, member));
    }

    /**
     * 상품, 상품옵션 삭제 delete /product/{id}
     **/

    /**
     * 상품상세 조회 get /product/{id}
     **/
    @GetMapping("/{id}")
    public ResponseEntity<ResponseSearchDetailProductDto> searchDetailProduction(
            @PathVariable @Positive(message = "{validation.product.id.positive}") Long id) {
        return ResponseEntity.ok(
                productService.searchDetailProduct(id));
    }

    /**
     * 상품목록 조회 get /product
     **/
    @GetMapping
    public ResponseEntity<Page<ResponseProductDto>> searchProductionList(
            @Valid RequestSearchProductDto requestSearchProductionDto) {
        return ResponseEntity.ok(
                productService.searchProductList(requestSearchProductionDto));
    }

}
