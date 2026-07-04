package com.example.ecommerce.backend.auth.service;

import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Loads application users for both business logic and Spring Security.
 *
 * <p>Because it extends {@link UserDetailsService}, the same service can be used
 * during login and when resolving users from JWT-authenticated requests.
 *
 * @author Pial Kanti Samadder
 */
public interface UserService extends UserDetailsService {
}
