package com.example.ecommerce.backend.payment.repository;

import com.example.ecommerce.backend.payment.entity.PaymentHistory;
import com.example.ecommerce.backend.payment.enums.PaymentStatus;
import com.example.ecommerce.backend.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for persisting and retrieving payment history records.
 *
 * <p>Payment history is keyed by Stripe checkout session identifiers for
 * status callback handling and by order/status for locating the latest active
 * payment attempt.</p>
 *
 * @author Pial Kanti Samadder
 */
@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    /**
     * Finds a payment attempt by its Stripe Checkout Session identifier.
     *
     * @param sessionId Stripe Checkout Session identifier
     * @return matching payment history when present
     */
    Optional<PaymentHistory> findBySessionId(String sessionId);

    /**
     * Finds the most recent payment attempt for an order with the given status.
     *
     * @param orderId order identifier
     * @param status payment status
     * @return latest matching payment history when present
     */
    Optional<PaymentHistory> findTopByOrderIdAndStatusOrderByCreatedAtDesc(Long orderId, PaymentStatus status);

    /**
     * Finds initiated payments whose expiration timestamp has passed while the
     * owning order is still awaiting payment.
     *
     * @param paymentStatus payment status to match
     * @param orderStatus order status to match
     * @param now current timestamp
     * @return expired payment attempts with orders and line items loaded
     */
    @Query("""
            select distinct paymentHistory
            from PaymentHistory paymentHistory
            join fetch paymentHistory.order paymentOrder
            left join fetch paymentOrder.items
            where paymentHistory.status = :paymentStatus
              and paymentOrder.status = :orderStatus
              and paymentHistory.expiresAt <= :now
            """)
    List<PaymentHistory> findExpiredPayments(
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("orderStatus") OrderStatus orderStatus,
            @Param("now") LocalDateTime now
    );
}
