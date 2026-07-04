package com.example.ecommerce.backend.auth.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Centralized Spring Security expression constants used by method security.
 *
 * @author Pial Kanti Samadder
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthorizationExpressions {
    public static final String HAS_ADMIN_ROLE = "hasRole('ADMIN')";
    public static final String HAS_PRODUCT_CREATE = "hasAuthority('PERMISSION_PRODUCT_CREATE')";
    public static final String HAS_PRODUCT_UPDATE = "hasAuthority('PERMISSION_PRODUCT_UPDATE')";
    public static final String HAS_PRODUCT_DELETE = "hasAuthority('PERMISSION_PRODUCT_DELETE')";
    public static final String HAS_CATEGORY_CREATE = "hasAuthority('PERMISSION_CATEGORY_CREATE')";
    public static final String HAS_CATEGORY_UPDATE = "hasAuthority('PERMISSION_CATEGORY_UPDATE')";
    public static final String HAS_CATEGORY_DELETE = "hasAuthority('PERMISSION_CATEGORY_DELETE')";
    public static final String HAS_INVENTORY_MANAGE = "hasAuthority('PERMISSION_INVENTORY_MANAGE')";
    public static final String HAS_ORDER_READ = "hasAuthority('PERMISSION_ORDER_READ')";
    public static final String HAS_ORDER_UPDATE = "hasAuthority('PERMISSION_ORDER_UPDATE')";
    public static final String HAS_ORDER_CANCEL = "hasAuthority('PERMISSION_ORDER_CANCEL')";
    public static final String HAS_ROLE_READ = "hasAuthority('PERMISSION_ROLE_READ')";
    public static final String HAS_ROLE_CREATE = "hasAuthority('PERMISSION_ROLE_CREATE')";
    public static final String HAS_ROLE_UPDATE = "hasAuthority('PERMISSION_ROLE_UPDATE')";
    public static final String HAS_ROLE_DELETE = "hasAuthority('PERMISSION_ROLE_DELETE')";
    public static final String HAS_PERMISSION_READ = "hasAuthority('PERMISSION_READ')";
    public static final String HAS_PERMISSION_CREATE = "hasAuthority('PERMISSION_CREATE')";
    public static final String HAS_PERMISSION_UPDATE = "hasAuthority('PERMISSION_UPDATE')";
    public static final String HAS_PERMISSION_DELETE = "hasAuthority('PERMISSION_DELETE')";
    public static final String HAS_ROLE_ASSIGN = "hasAuthority('PERMISSION_ROLE_ASSIGN')";
    public static final String HAS_PERMISSION_ASSIGN = "hasAuthority('PERMISSION_ASSIGN')";
    public static final String HAS_ADMIN_AND_ROLE_READ = "hasRole('ADMIN') and hasAuthority('PERMISSION_ROLE_READ')";
    public static final String HAS_ADMIN_AND_ROLE_CREATE = "hasRole('ADMIN') and hasAuthority('PERMISSION_ROLE_CREATE')";
    public static final String HAS_ADMIN_AND_ROLE_UPDATE = "hasRole('ADMIN') and hasAuthority('PERMISSION_ROLE_UPDATE')";
    public static final String HAS_ADMIN_AND_ROLE_DELETE = "hasRole('ADMIN') and hasAuthority('PERMISSION_ROLE_DELETE')";
    public static final String HAS_ADMIN_AND_PERMISSION_READ = "hasRole('ADMIN') and hasAuthority('PERMISSION_READ')";
    public static final String HAS_ADMIN_AND_PERMISSION_CREATE = "hasRole('ADMIN') and hasAuthority('PERMISSION_CREATE')";
    public static final String HAS_ADMIN_AND_PERMISSION_UPDATE = "hasRole('ADMIN') and hasAuthority('PERMISSION_UPDATE')";
    public static final String HAS_ADMIN_AND_PERMISSION_DELETE = "hasRole('ADMIN') and hasAuthority('PERMISSION_DELETE')";
    public static final String HAS_ADMIN_AND_ROLE_ASSIGN = "hasRole('ADMIN') and hasAuthority('PERMISSION_ROLE_ASSIGN')";
    public static final String HAS_ADMIN_AND_PERMISSION_ASSIGN = "hasRole('ADMIN') and hasAuthority('PERMISSION_ASSIGN')";
}
