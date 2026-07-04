package com.example.ecommerce.backend.order.repository;

import com.example.ecommerce.backend.order.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for order persistence operations.
 *
 * @author Pial Kanti Samadder
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Retrieves an order by identifier with line items eagerly loaded.
     *
     * @param id order identifier
     * @return matching order when present
     */
    @Override
    @EntityGraph(value = "Order.withItems")
    Optional<Order> findById(Long id);
}
