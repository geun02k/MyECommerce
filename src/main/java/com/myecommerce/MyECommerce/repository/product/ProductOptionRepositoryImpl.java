package com.myecommerce.MyECommerce.repository.product;

import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.vo.order.ProductOptionKey;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductOptionRepositoryImpl implements ProductOptionRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<ProductOption> findOptionsWithLock(List<ProductOptionKey> optionKeys) {
        // sql 작성
        StringBuffer jpql = new StringBuffer("""
                SELECT option
                FROM ProductOption option
                JOIN FETCH option.product product
                WHERE  
        """);

        for(int i = 0; i < optionKeys.size(); i++) {
            jpql.append("(product.id = :productId").append(i)
                .append(" AND option.optionCode = :optionCode").append(i)
                .append(")");

            if(i < optionKeys.size() - 1) {
                jpql.append(" OR ");
            }
        }

        // 비관적 락 쿼리 생성
        TypedQuery<ProductOption> query =
                em.createQuery(jpql.toString(), ProductOption.class)
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE);

        // 동적 파라미터 생성
        for(int i = 0; i < optionKeys.size(); i++) {
           query.setParameter("productId" + i, optionKeys.get(i).productId());
           query.setParameter("optionCode" + i, optionKeys.get(i).optionCode());
        }

        return query.getResultList();
    }
}
