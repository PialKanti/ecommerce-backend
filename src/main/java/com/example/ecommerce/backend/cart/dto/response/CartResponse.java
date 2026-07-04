package com.example.ecommerce.backend.cart.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response payload representing the current user's shopping cart.
 *
 * <p>The response includes derived totals so clients do not need to recalculate
 * item quantities or monetary subtotals from individual lines.</p>
 *
 * @author Pial Kanti Samadder
 */
public record CartResponse(
        Long id,
        Long userId,
        List<CartItemResponse> items,
        Integer totalQuantity,
        Double subtotal,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Long createdBy,
        Long modifiedBy
) {
}
