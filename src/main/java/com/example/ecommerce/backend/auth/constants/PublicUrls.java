package com.example.ecommerce.backend.auth.constants;

import com.example.ecommerce.backend.common.constants.ApiEndpoints;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Public endpoint registry used by Spring Security configuration.
 *
 * @author Pial Kanti Samadder
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PublicUrls {
    /**
     * Endpoints that do not require authentication.
     */
    public static final String[] PUBLIC_ENDPOINTS = {
            ApiEndpoints.Auth.BASE_AUTH + "/register",
            ApiEndpoints.Auth.BASE_AUTH + "/login",
            ApiEndpoints.Auth.BASE_AUTH + "/refresh",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
}
