package com.myecommerce.MyECommerce.repository.Order;

import com.myecommerce.MyECommerce.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 주문 ID 목록 삭제
    void deleteByIdIn(List<Long> ids);

}
