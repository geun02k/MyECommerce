package com.myecommerce.MyECommerce.repository.payment;

import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.type.PaymentMethodType;
import com.myecommerce.MyECommerce.type.PgProviderType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 주문에 대한 결제 내역 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Payment> findLockedAllByOrderId(Long orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Payment> findLockedById(Long id);

    Optional<Payment> findByPgTransactionId(String pgTransactionId);

    List<Payment> findByOrderIdAndPaymentMethodAndPgProvider(
            Long orderId, PaymentMethodType paymentMethod, PgProviderType pgProvider);
}
