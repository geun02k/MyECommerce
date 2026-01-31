package com.myecommerce.MyECommerce.repository.product;

import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.vo.order.ProductOptionKey;

import java.util.List;

public interface ProductOptionRepositoryCustom {

    // (상품ID, 옵션코드) 쌍을 기반으로 상품 옵션 목록 조회
    List<ProductOption> findOptionsWithLock(List<ProductOptionKey> optionKeys);

}
