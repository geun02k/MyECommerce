package com.myecommerce.MyECommerce.controller;

import com.myecommerce.MyECommerce.dto.production.RequestProductionDto;
import com.myecommerce.MyECommerce.dto.production.ResponseProductionDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.service.production.ProductionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/production")
@RequiredArgsConstructor
public class ProductionController {

    private final ProductionService productionService;

    /**
     * 상품, 상품옵션목록 등록 post /production
     **/
    @PostMapping
    public ResponseEntity<ResponseProductionDto> registerProduction(
            @RequestBody @Valid RequestProductionDto production,
            @AuthenticationPrincipal Member member) {

        return ResponseEntity.ok(
                productionService.saveProduction(production, member));
    }

    /**
     * 상품, 상품옵션 수정 put /production/{id}
     **/

    /**
     * 상품, 상품옵션 삭제 delete /production/{id}
     **/

    /**
     * 상품상세 조회 get /production/{id}
     **/

    /**
     * 상품목록 조회 get /production
     **/
}
