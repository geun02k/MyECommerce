package com.myecommerce.MyECommerce.repository.production;

import com.myecommerce.MyECommerce.entity.production.ProductionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductionOptionRepository extends JpaRepository<ProductionOption, Long> {

}
