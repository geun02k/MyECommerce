package com.myecommerce.MyECommerce.controller;

import com.myecommerce.MyECommerce.dto.production.RequestModifyProductionDto;
import com.myecommerce.MyECommerce.dto.production.RequestProductionDto;
import com.myecommerce.MyECommerce.dto.production.ResponseProductionDto;
import com.myecommerce.MyECommerce.entity.member.Member;
import com.myecommerce.MyECommerce.service.production.ProductionService;
import com.myecommerce.MyECommerce.type.ProductionOrderByStdType;
import com.myecommerce.MyECommerce.validation.EnumValid;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<ResponseProductionDto> registerProduction(
            @RequestBody @Valid RequestProductionDto production,
            @AuthenticationPrincipal Member member) {

        return ResponseEntity.ok(
                productionService.registerProduction(production, member));
    }

    /**
     * 상품, 상품옵션 수정 put /production/{id}
     **/
    @PutMapping
    @PreAuthorize("hasAuthority('SELLER')")
    public ResponseEntity<ResponseProductionDto> modifyProduction(
            @RequestBody @Valid RequestModifyProductionDto production,
            @AuthenticationPrincipal Member member) {

        return ResponseEntity.ok(
                productionService.modifyProduction(production, member));
    }

    /**
     * 상품, 상품옵션 삭제 delete /production/{id}
     **/

    /**
     * 상품상세 조회 get /production/{id}
     **/

    /**
     * 상품목록 조회 get /production
     **/
    @GetMapping
    public ResponseEntity<Page<ResponseProductionDto>> searchProductionList(
            @RequestParam @EnumValid(enumClass = ProductionOrderByStdType.class)
            ProductionOrderByStdType orderByStd,
            @RequestParam @Pattern(regexp = "^[^\\s]+$", message = "검색어를 입력하세요.") // 최소 한글자이상 입력
            String keyword,
            @PageableDefault(size = 5, page = 0)
            Pageable pageable) {
        return ResponseEntity.ok(
                productionService.searchProductionList(orderByStd, keyword, pageable));
    }

}
