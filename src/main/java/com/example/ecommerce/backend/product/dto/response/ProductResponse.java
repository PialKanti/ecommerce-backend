package com.example.ecommerce.backend.product.dto.response;

import java.time.LocalDateTime;

/**
 * Response payload representing product catalog details exposed by the API.
 *
 * <p>The category relationship is represented by its primary key to avoid
 * exposing nested entity graphs and lazy-loading details in API responses.</p>
 *
 * @author Pial Kanti Samadder
 */
public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        Double price,
        Boolean isActive,
        Long categoryId,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Long createdBy,
        Long modifiedBy
) {
}
