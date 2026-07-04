package com.example.ecommerce.backend.order.security;

import com.example.ecommerce.backend.auth.security.AuthenticatedUser;
import com.example.ecommerce.backend.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Reusable authorization service for order ownership checks.
 *
 * <p>Customers may access only their own orders. Administrative users with
 * order permissions can access orders through admin workflows.</p>
 *
 * @author Pial Kanti Samadder
 */
@Service("orderSecurityService")
@RequiredArgsConstructor
public class OrderSecurityService {
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String PERMISSION_ORDER_READ = "PERMISSION_ORDER_READ";
    private static final String PERMISSION_ORDER_CANCEL = "PERMISSION_ORDER_CANCEL";
    private static final String PERMISSION_ORDER_UPDATE = "PERMISSION_ORDER_UPDATE";

    private final OrderRepository orderRepository;

    /**
     * Checks whether the authenticated user can access an order.
     *
     * @param orderId        order identifier
     * @param authentication current authentication
     * @return {@code true} when access is allowed
     */
    @Transactional(readOnly = true)
    public boolean canAccessOrder(Long orderId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            return false;
        }

        if (hasAnyAuthority(authentication, ROLE_ADMIN, PERMISSION_ORDER_READ, PERMISSION_ORDER_CANCEL, PERMISSION_ORDER_UPDATE)) {
            return true;
        }

        return orderRepository.findById(orderId)
                .map(order -> order.getUserId().equals(user.id()))
                .orElse(false);
    }

    private boolean hasAnyAuthority(Authentication authentication, String... authorities) {
        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            for (String authority : authorities) {
                if (authority.equals(grantedAuthority.getAuthority())) {
                    return true;
                }
            }
        }
        return false;
    }
}
