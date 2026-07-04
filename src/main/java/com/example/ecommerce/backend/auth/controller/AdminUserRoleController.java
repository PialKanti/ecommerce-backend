package com.example.ecommerce.backend.auth.controller;

import com.example.ecommerce.backend.auth.constants.AuthorizationExpressions;
import com.example.ecommerce.backend.auth.dto.response.RoleResponse;
import com.example.ecommerce.backend.auth.service.UserRoleService;
import com.example.ecommerce.backend.common.constants.ApiEndpoints;
import com.example.ecommerce.backend.common.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Admin controller for assigning and removing roles from users.
 *
 * @author Pial Kanti Samadder
 */
@RestController
@RequestMapping(ApiEndpoints.Admin.BASE_ADMIN_USERS)
@RequiredArgsConstructor
@Tag(name = "User Role Assignment", description = "Administrative user-role assignment operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserRoleController {
    private final UserRoleService userRoleService;

    /**
     * Lists roles assigned to a user.
     *
     * @param userId user identifier
     * @return assigned roles
     */
    @Operation(summary = "List user roles", description = "Requires PERMISSION_ROLE_ASSIGN permission.")
    @GetMapping("/{userId}/roles")
    @PreAuthorize(AuthorizationExpressions.HAS_ADMIN_AND_ROLE_ASSIGN)
    public ResponseEntity<ApiResponse<List<RoleResponse>>> getUserRoles(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(userRoleService.getRoles(userId)));
    }

    /**
     * Assigns a role to a user.
     *
     * @param userId user identifier
     * @param roleId role identifier
     * @return updated role list
     */
    @Operation(summary = "Assign role to user", description = "Requires PERMISSION_ROLE_ASSIGN permission.")
    @PostMapping("/{userId}/roles/{roleId}")
    @PreAuthorize(AuthorizationExpressions.HAS_ADMIN_AND_ROLE_ASSIGN)
    public ResponseEntity<ApiResponse<List<RoleResponse>>> assignRole(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        return ResponseEntity.ok(ApiResponse.success(userRoleService.assignRole(userId, roleId)));
    }

    /**
     * Removes a role from a user.
     *
     * @param userId user identifier
     * @param roleId role identifier
     * @return updated role list
     */
    @Operation(summary = "Remove role from user", description = "Requires PERMISSION_ROLE_ASSIGN permission.")
    @DeleteMapping("/{userId}/roles/{roleId}")
    @PreAuthorize(AuthorizationExpressions.HAS_ADMIN_AND_ROLE_ASSIGN)
    public ResponseEntity<ApiResponse<List<RoleResponse>>> removeRole(
            @PathVariable Long userId,
            @PathVariable Long roleId) {
        return ResponseEntity.ok(ApiResponse.success(userRoleService.removeRole(userId, roleId)));
    }
}
