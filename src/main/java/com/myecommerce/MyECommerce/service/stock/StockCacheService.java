package com.myecommerce.MyECommerce.service.stock;

import com.myecommerce.MyECommerce.entity.order.OrderItem;
import com.myecommerce.MyECommerce.entity.product.Product;
import com.myecommerce.MyECommerce.entity.product.ProductOption;
import com.myecommerce.MyECommerce.repository.product.ProductOptionRepository;
import com.myecommerce.MyECommerce.service.redis.RedisMultiDataService;
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
    private final RedisMultiDataService redisMultiDataService;

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

    /** Redis 상품 재고 감소 **/
    public void decrementProductStock(List<OrderItem> orderItems) {
        for(OrderItem orderItem : orderItems) {
            String productCode = orderItem.getProduct().getCode();
            String optionCode = orderItem.getOption().getOptionCode();

            String redisKey = createStockRedisKey(productCode, optionCode);
            Long orderQuantity = (long) orderItem.getQuantity();

            // 원자적 감소
            redisMultiDataService.decrementData(STOCK, redisKey, orderQuantity);
        }
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

    /** Redis 상품 재고 목록 조회 (키 목록에 대한 다건 조회) **/
    public List<Object> getProductStockList(List<String> itemKeys) {
        return redisMultiDataService.getMultiData(itemKeys);
    }

    // 재고 Redis key 생성
    private String createStockRedisKey(String productCode, String optionCode) {
        return productCode + ":" + optionCode;
    }
}
