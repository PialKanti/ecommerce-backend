package com.example.ecommerce.backend.order.repository;

import com.example.ecommerce.backend.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for order item persistence operations.
 *
 * <p>Order items are normally persisted through {@code Order} cascading, but a
 * dedicated repository keeps the persistence layer complete for the order
 * domain.</p>
 *
 * @author Pial Kanti Samadder
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
