package com.myecommerce.MyECommerce.dto.production;

import com.myecommerce.MyECommerce.entity.member.Member;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseProductionOptionDto {

    private Long id;

    private String optionCode;

    private String optionName;

    private BigDecimal price;

    private int quantity;

    private int seq;

    private Member member;

    private Long createId;

    private LocalDateTime createDt;

    private Long updateId;

    private LocalDateTime updateDt;

}
