package com.myecommerce.MyECommerce.repository.production;

import com.myecommerce.MyECommerce.entity.production.Production;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionRepository extends JpaRepository<Production, Long> {

}
