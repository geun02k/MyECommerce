package com.myecommerce.MyECommerce.repository.Order;

import com.myecommerce.MyECommerce.entity.order.Order;
import com.myecommerce.MyECommerce.type.OrderStatusType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // 주문 ID 목록 삭제
    void deleteByIdIn(List<Long> ids);

    // 생성된 상태의 주문 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Order> findLockedByIdAndOrderStatus(Long id, OrderStatusType orderStatus);

}
