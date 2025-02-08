package com.myecommerce.MyECommerce.repository.production;

import com.myecommerce.MyECommerce.entity.production.ProductionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductionOptionRepository extends JpaRepository<ProductionOption, Long> {
    // 판매자 상품의 동일 상품옵션코드 조회
    Optional<ProductionOption> findByProductionIdAndOptionCode(Long productionId, String optionCode);

}
