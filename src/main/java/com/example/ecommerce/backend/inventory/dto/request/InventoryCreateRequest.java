package com.example.ecommerce.backend.inventory.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload used to create inventory for a product.
 *
 * <p>Each product can have only one inventory record. Reserved quantity starts
 * at zero when inventory is first created.</p>
 *
 * @author Pial Kanti Samadder
 */
public record InventoryCreateRequest(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotNull(message = "Total quantity is required")
        @Min(value = 0, message = "Total quantity cannot be negative")
        Integer totalQuantity
) {
}
