package com.myecommerce.MyECommerce.repository.production;

import com.myecommerce.MyECommerce.entity.production.Production;
import com.myecommerce.MyECommerce.entity.production.ProductionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductionOptionRepository extends JpaRepository<ProductionOption, Long> {
    // 판매자 상품의 동일 상품옵션코드 조회
    @Query(" SELECT PRD " +
            " FROM Production PRD" +
            " INNER JOIN FETCH PRD.options OPTION" +
            " WHERE PRD.code = ?1" +
            " AND OPTION.optionCode IN (?2)")
    List<Production> findByProductionCodeAndOptionCodeIn(String productionCode, List<String> optionCodes);
}
