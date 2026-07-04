package com.example.ecommerce.backend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request payload used to authenticate an existing user.
 *
 * @author Pial Kanti Samadder
 */
public record LoginRequest(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {
}
