package com.example.ecommerce.backend.auth.dto.response;

import java.time.LocalDateTime;

/**
 * Response payload containing permission details.
 *
 * @author Pial Kanti Samadder
 */
public record PermissionResponse(
        Long id,
        String name,
        String code,
        String description,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Long createdBy,
        Long modifiedBy
) {
}
