package com.example.ecommerce.backend.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload used to add a product item to the current cart.
 *
 * <p>The user identifier is intentionally excluded. Until authentication is
 * introduced, the controller supplies the temporary current user identifier.</p>
 *
 * @author Pial Kanti Samadder
 */
public record CartItemAddRequest(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be greater than zero")
        Integer quantity
) {
}
