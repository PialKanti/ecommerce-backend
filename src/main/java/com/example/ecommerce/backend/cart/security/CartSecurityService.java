package com.example.ecommerce.backend.cart.security;

import com.example.ecommerce.backend.auth.security.AuthenticatedUser;
import com.example.ecommerce.backend.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reusable authorization service for cart ownership checks.
 *
 * <p>Cart ownership is evaluated by Spring Security method authorization so
 * controllers do not become the only line of defense.</p>
 *
 * @author Pial Kanti Samadder
 */
@Service("cartSecurityService")
@RequiredArgsConstructor
public class CartSecurityService {
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final CartRepository cartRepository;

    /**
     * Checks whether the authenticated user can access a cart.
     *
     * @param cartId cart identifier
     * @param authentication current authentication
     * @return {@code true} when access is allowed
     */
    @Transactional(readOnly = true)
    public boolean canAccessCart(Long cartId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return false;
        }
        if (hasAuthority(authentication, ROLE_ADMIN)) {
            return true;
        }
        return cartRepository.findById(cartId)
                .map(cart -> cart.getUserId().equals(user.id()))
                .orElse(false);
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority::equals);
    }
}
