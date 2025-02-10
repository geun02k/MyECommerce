package com.myecommerce.MyECommerce.repository.production;

import com.myecommerce.MyECommerce.entity.production.Production;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductionRepository extends JpaRepository<Production, Long> {
    // 판매자의 동일 상품코드 조회
    Optional<Production> findBySellerAndCode(long id, String code);
}
