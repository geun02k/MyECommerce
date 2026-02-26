package com.myecommerce.MyECommerce.repository.payment;

import com.myecommerce.MyECommerce.entity.payment.Payment;
import com.myecommerce.MyECommerce.type.PaymentMethodType;
import com.myecommerce.MyECommerce.type.PaymentStatusType;
import com.myecommerce.MyECommerce.type.PgProviderType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

    // 조건부 결제상태 업데이트
    @Modifying(clearAutomatically = true, flushAutomatically = true)  // Spring Data JPA에서 변경 쿼리임을 알려주는 어노테이션
    @Query("""
            UPDATE Payment p
            SET p.paymentStatus = :status
            WHERE p.id = :id
            AND p.paymentStatus = 'IN_PROGRESS'
    """)
    int approveIfInProgress(Long id, PaymentStatusType status);
}
