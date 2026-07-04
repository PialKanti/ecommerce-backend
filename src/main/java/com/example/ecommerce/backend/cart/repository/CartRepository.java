package com.example.ecommerce.backend.cart.repository;

import com.example.ecommerce.backend.cart.entity.Cart;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for cart persistence operations.
 *
 * @author Pial Kanti Samadder
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    /**
     * Retrieves a cart by identifier with items eagerly loaded.
     *
     * @param id cart identifier
     * @return matching cart when present
     */
    @Override
    @EntityGraph(value = "Cart.withItems")
    Optional<Cart> findById(Long id);

    /**
     * Retrieves a cart by its owner user identifier with items eagerly loaded.
     *
     * @param userId owner user identifier
     * @return matching cart when present
     */
    @EntityGraph(value = "Cart.withItems")
    Optional<Cart> findByUserId(Long userId);
}
