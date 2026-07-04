package com.example.ecommerce.backend.inventory.dto.response;

import java.time.LocalDateTime;

/**
 * Response payload representing product inventory details.
 *
 * <p>Available quantity is calculated from total quantity minus reserved
 * quantity. The version is exposed for observability while remaining managed by
 * persistence.</p>
 *
 * @author Pial Kanti Samadder
 */
public record InventoryResponse(
        Long id,
        Long productId,
        Integer totalQuantity,
        Integer reservedQuantity,
        Integer availableQuantity,
        Long version,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Long createdBy,
        Long modifiedBy
) {
}
