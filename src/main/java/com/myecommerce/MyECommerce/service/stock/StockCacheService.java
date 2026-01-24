package com.myecommerce.MyECommerce.service.stock;

import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.service.redis.RedisSingleDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.myecommerce.MyECommerce.type.RedisNamespaceType.STOCK;

/** 재고 캐시 데이터 관리 서비스 **/
@Service
@RequiredArgsConstructor
public class StockCacheService {

    private final RedisSingleDataService redisSingleDataService;

    private final ProductOptionRepository productOptionRepository;

    /** Redis 상품 재고 등록 **/
    public void saveProductStock(Product product) {
        // 옵션목록조회
        List<ProductOption> options =
                productOptionRepository.findByProductId(product.getId());

        // 상품옵션 재고등록
        options.forEach(option -> {
            redisSingleDataService.saveSingleData(
                    STOCK,
                    createStockRedisKey(product.getCode(), option.getOptionCode()),
                    option.getQuantity());
        });
    }

    /** Redis 상품 재고 삭제 **/
    public void deleteProductStock(Product product) {
        // 옵션목록조회
        List<ProductOption> options =
                productOptionRepository.findByProductId(product.getId());

        // 상품옵션 재고삭제
        options.forEach(option -> {
            redisSingleDataService.deleteSingleData(
                    STOCK,
                    createStockRedisKey(product.getCode(), option.getOptionCode()));
        });
    }

    // 재고 Redis key 생성
    public String createStockRedisKey(String productCode, String optionCode) {
        return productCode + ":" + optionCode;
    }
}
