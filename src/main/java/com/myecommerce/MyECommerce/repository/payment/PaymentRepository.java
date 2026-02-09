package com.myecommerce.MyECommerce.repository.payment;

import com.myecommerce.MyECommerce.entity.payment.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 주문에 대한 결제 내역 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Payment> findLockedAllByOrderId(Long orderId);
}
