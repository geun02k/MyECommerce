package com.myecommerce.MyECommerce.repository.production;

import com.myecommerce.MyECommerce.entity.production.Production;
import com.myecommerce.MyECommerce.type.ProductionCategoryType;
import com.myecommerce.MyECommerce.type.ProductionSaleStatusType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductionRepository extends JpaRepository<Production, Long> {
    // 판매자의 동일 상품코드 조회
    Optional<Production> findBySellerAndCode(long id, String code);

    // 판매자, 상품ID에 일치하는 상품 조회
    Optional<Production> findByIdAndSeller(Long id, Long sellerId);

    // keyword를 포함하는 상품목록조회 - 최신등록순
    Page<Production> findByNameLikeAndSaleStatusAndCategoryOrderByCreateDt(
            String name, ProductionSaleStatusType saleStatus,
            ProductionCategoryType category, Pageable pageable);

    // keyword를 포함하는 상품목록조회 - 낮은가격순
    @Query(value =
            " SELECT prd" +
            " FROM Production prd" +
            " INNER JOIN prd.options option " +
            " WHERE prd.name LIKE CONCAT('%', :name, '%')" +
            " AND prd.saleStatus = 'ON_SALE'" +
            " AND prd.category = :category" +
            " GROUP BY prd.id" +
            " ORDER BY MIN(option.price)")
    Page<Production> findByNameOrderByPrice(
            String name, ProductionCategoryType category, Pageable pageable);

    // keyword를 포함하는 상품목록조회 - 높은가격순
    @Query(value =
            " SELECT prd" +
            " FROM Production prd" +
            " INNER JOIN prd.options option " +
            " WHERE prd.name LIKE CONCAT('%', :name, '%')" +
            " AND prd.saleStatus = 'ON_SALE'" +
            " AND prd.category = :category" +
            " GROUP BY prd.id" +
            " ORDER BY MIN(option.price) DESC")
    Page<Production> findByNameOrderByPriceDesc(
            String name, ProductionCategoryType category, Pageable pageable);

    // keyword를 포함하는 상품목록조회 - 정확도순 (키워드 외 일치하지 않는 문자 개수로 판단)
    @Query(value =
            " SELECT prd " +
            " FROM Production prd " +
            " WHERE prd.name LIKE CONCAT('%', :name, '%') " +
            " AND prd.saleStatus = 'ON_SALE' " +
            " AND prd.category = :category" +
            " ORDER BY (LENGTH(prd.name) - LENGTH(:name)) ")
    Page<Production> findByNameOrderByCalculatedAccuracyDesc(
           String name, ProductionCategoryType category, Pageable pageable);
}
