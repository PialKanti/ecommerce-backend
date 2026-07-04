package com.example.ecommerce.backend.auth.enums;

/**
 * Predefined role codes used by the ecommerce authorization model.
 *
 * <p>Role codes are stored without Spring Security's {@code ROLE_} prefix.
 * The prefix is added only when converting roles into granted authorities.</p>
 *
 * @author Pial Kanti Samadder
 */
public enum RoleCode {
    ADMIN,
    CUSTOMER,
    PRODUCT_MANAGER,
    INVENTORY_MANAGER,
    SUPPORT_AGENT
}
