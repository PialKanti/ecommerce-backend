package com.example.ecommerce.backend.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for inventory quantity adjustments.
 *
 * <p>The entity version is intentionally not accepted from clients. Concurrent
 * inventory writes are protected by JPA optimistic locking through the
 * inventory entity.</p>
 *
 * @author Pial Kanti Samadder
 */
public record InventoryQuantityRequest(
        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be greater than zero")
        Integer quantity
) {
}
