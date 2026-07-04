package com.example.ecommerce.backend.inventory.repository;

import com.example.ecommerce.backend.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for product inventory persistence operations.
 *
 * @author Pial Kanti Samadder
 */
@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    /**
     * Retrieves inventory by the owning product identifier.
     *
     * @param productId product identifier
     * @return matching inventory when present
     */
    Optional<Inventory> findByProductId(Long productId);

    /**
     * Checks whether inventory already exists for a product.
     *
     * @param productId product identifier
     * @return {@code true} when the product already has inventory
     */
    boolean existsByProductId(Long productId);
}
