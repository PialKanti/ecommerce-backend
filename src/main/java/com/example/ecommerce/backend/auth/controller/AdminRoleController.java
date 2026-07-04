package com.example.ecommerce.backend.auth.controller;

import com.example.ecommerce.backend.auth.constants.AuthorizationExpressions;
import com.example.ecommerce.backend.auth.dto.request.RoleRequest;
import com.example.ecommerce.backend.auth.dto.response.PermissionResponse;
import com.example.ecommerce.backend.auth.dto.response.RoleResponse;
import com.example.ecommerce.backend.auth.service.RoleService;
import com.example.ecommerce.backend.common.constants.ApiEndpoints;
import com.example.ecommerce.backend.common.dto.response.ApiResponse;
import com.example.ecommerce.backend.common.dto.response.PaginatedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Admin controller for role lifecycle and role-permission assignment APIs.
 *
 * @author Pial Kanti Samadder
 */
@RestController
@RequestMapping(ApiEndpoints.Admin.BASE_ADMIN_ROLES)
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "Administrative RBAC role operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminRoleController {
    private final RoleService roleService;

    /**
     * Lists roles with pagination.
     *
     * @param page zero-based page index
     * @param size page size
     * @return paginated role response
     */
    @Operation(summary = "List roles", description = "Requires PERMISSION_ROLE_READ permission.")
    @GetMapping
    @PreAuthorize(AuthorizationExpressions.HAS_ADMIN_AND_ROLE_READ)
    public ResponseEntity<ApiResponse<PaginatedResponse<RoleResponse>>> listRoles(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(PaginatedResponse.of(roleService.getAll(pageable))));
    }

    /**
     * Retrieves a role by identifier.
     *
     * @param id role identifier
     * @return matching role
     */
    @Operation(summary = "Get role by ID", description = "Requires PERMISSION_ROLE_READ permission.")
    @GetMapping("/{id}")
    @PreAuthorize(AuthorizationExpressions.HAS_ADMIN_AND_ROLE_READ)
    public ResponseEntity<ApiResponse<RoleResponse>> getRole(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(roleService.getById(id)));
    }

    /**
     * Creates a role.
     *
     * @param request role payload
     * @return created role
     */
    @Operation(
            summary = "Create role",
            description = "Requires PERMISSION_ROLE_CREATE permission.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = RoleRequest.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "name": "Support Agent",
                                      "code": "SUPPORT_AGENT",
                                      "description": "Can read and cancel customer orders."
                                    }
                                    """)
                    )
            )
    )
    @PostMapping
    @PreAuthorize(AuthorizationExpressions.HAS_ADMIN_AND_ROLE_CREATE)
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(roleService.create(request)));
    }

    /**
     * Updates a role.
     *
     * @param id role identifier
     * @param request role payload
     * @return updated role
     */
    @Operation(summary = "Update role", description = "Requires PERMISSION_ROLE_UPDATE permission.")
    @PutMapping("/{id}")
    @PreAuthorize(AuthorizationExpressions.HAS_ADMIN_AND_ROLE_UPDATE)
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(roleService.update(id, request)));
    }

    /**
     * Deletes a role.
     *
     * @param id role identifier
     * @return empty response
     */
    @Operation(summary = "Delete role", description = "Requires PERMISSION_ROLE_DELETE permission.")
    @DeleteMapping("/{id}")
    @PreAuthorize(AuthorizationExpressions.HAS_ADMIN_AND_ROLE_DELETE)
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Lists permissions assigned to a role.
     *
     * @param roleId role identifier
     * @return assigned permissions
     */
    @Operation(summary = "List role permissions", description = "Requires PERMISSION_ASSIGN permission.")
    @GetMapping("/{roleId}/permissions")
    @PreAuthorize(AuthorizationExpressions.HAS_ADMIN_AND_PERMISSION_ASSIGN)
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> getPermissions(@PathVariable Long roleId) {
        return ResponseEntity.ok(ApiResponse.success(roleService.getPermissions(roleId)));
    }

    /**
     * Assigns a permission to a role.
     *
     * @param roleId role identifier
     * @param permissionId permission identifier
     * @return updated role
     */
    @Operation(summary = "Assign permission to role", description = "Requires PERMISSION_ASSIGN permission.")
    @PostMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize(AuthorizationExpressions.HAS_ADMIN_AND_PERMISSION_ASSIGN)
    public ResponseEntity<ApiResponse<RoleResponse>> assignPermission(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        return ResponseEntity.ok(ApiResponse.success(roleService.assignPermission(roleId, permissionId)));
    }

    /**
     * Removes a permission from a role.
     *
     * @param roleId role identifier
     * @param permissionId permission identifier
     * @return updated role
     */
    @Operation(summary = "Remove permission from role", description = "Requires PERMISSION_ASSIGN permission.")
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @PreAuthorize(AuthorizationExpressions.HAS_ADMIN_AND_PERMISSION_ASSIGN)
    public ResponseEntity<ApiResponse<RoleResponse>> removePermission(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        return ResponseEntity.ok(ApiResponse.success(roleService.removePermission(roleId, permissionId)));
    }
}
