package com.myecommerce.MyECommerce.repository.product;

import com.myecommerce.MyECommerce.dto.cart.RedisCartDto;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    // 판매자 상품의 동일 상품옵션코드 조회
    @Query(" SELECT PRD " +
            " FROM Product PRD" +
            " INNER JOIN FETCH PRD.options OPTION" +
            " WHERE PRD.code = ?1" +
            " AND OPTION.optionCode IN (?2)")
    List<Product> findByProductCodeAndOptionCodeIn(String productionCode, List<String> optionCodes);

    // 상품ID에 해당하는 상품옵션 단건 조회
    @Query(" SELECT new com.myecommerce.MyECommerce.dto.cart.RedisCartDto( " +
            "       PRD.id, PRD.code, PRD.name, " +
            "       OPTION.id, OPTION.optionCode, OPTION.optionName, " +
            "       OPTION.price, OPTION.quantity, null" +
            ")" +
            " FROM Product PRD" +
            " INNER JOIN PRD.options OPTION" +
            " WHERE PRD.code = ?1" +
            " AND OPTION.optionCode = ?2" +
            " AND PRD.saleStatus = 'ON_SALE'")
    Optional<RedisCartDto> findByProductCodeAndOptionCodeOfOnSale(
            String productCode, String optionCode);
}
