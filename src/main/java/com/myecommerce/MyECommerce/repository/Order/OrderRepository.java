package com.myecommerce.MyECommerce.repository.Order;

import com.myecommerce.MyECommerce.entity.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
