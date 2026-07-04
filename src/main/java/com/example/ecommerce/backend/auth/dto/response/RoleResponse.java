package com.example.ecommerce.backend.auth.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Response payload containing role details and assigned permissions.
 *
 * @author Pial Kanti Samadder
 */
public record RoleResponse(
        Long id,
        String name,
        String code,
        String description,
        Set<String> permissions,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Long createdBy,
        Long modifiedBy
) {
}
