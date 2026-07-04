package com.example.ecommerce.backend.auth.controller;

import com.example.ecommerce.backend.auth.constants.AuthorizationExpressions;
import com.example.ecommerce.backend.auth.dto.request.PermissionRequest;
import com.example.ecommerce.backend.auth.dto.response.PermissionResponse;
import com.example.ecommerce.backend.auth.service.PermissionService;
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

/**
 * Admin controller for permission lifecycle APIs.
 *
 * @author Pial Kanti Samadder
 */
@RestController
@RequestMapping(ApiEndpoints.Admin.BASE_ADMIN_PERMISSIONS)
@RequiredArgsConstructor
@Tag(name = "Permission Management", description = "Administrative RBAC permission operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminPermissionController {
    private final PermissionService permissionService;

    /**
     * Lists permissions with pagination.
     *
     * @param page zero-based page index
     * @param size page size
     * @return paginated permissions
     */
    @Operation(summary = "List permissions", description = "Requires PERMISSION_READ permission.")
    @GetMapping
    @PreAuthorize(AuthorizationExpressions.HAS_ADMIN_AND_PERMISSION_READ)
    public ResponseEntity<ApiResponse<PaginatedResponse<PermissionResponse>>> listPermissions(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(PaginatedResponse.of(permissionService.getAll(pageable))));
    }

    /**
     * Retrieves a permission by identifier.
     *
     * @param id permission identifier
     * @return matching permission
     */
    @Operation(summary = "Get permission by ID", description = "Requires PERMISSION_READ permission.")
    @GetMapping("/{id}")
    @PreAuthorize(AuthorizationExpressions.HAS_ADMIN_AND_PERMISSION_READ)
    public ResponseEntity<ApiResponse<PermissionResponse>> getPermission(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.getById(id)));
    }

    /**
     * Creates a permission.
     *
     * @param request permission payload
     * @return created permission
     */
    @Operation(
            summary = "Create permission",
            description = "Requires PERMISSION_CREATE permission.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PermissionRequest.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "name": "Create Product",
                                      "code": "PERMISSION_PRODUCT_CREATE",
                                      "description": "Allows creation of product catalog records."
                                    }
                                    """)
                    )
            )
    )
    @PostMapping
    @PreAuthorize(AuthorizationExpressions.HAS_ADMIN_AND_PERMISSION_CREATE)
    public ResponseEntity<ApiResponse<PermissionResponse>> createPermission(
            @Valid @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.create(request)));
    }

    /**
     * Updates a permission.
     *
     * @param id permission identifier
     * @param request permission payload
     * @return updated permission
     */
    @Operation(summary = "Update permission", description = "Requires PERMISSION_UPDATE permission.")
    @PutMapping("/{id}")
    @PreAuthorize(AuthorizationExpressions.HAS_ADMIN_AND_PERMISSION_UPDATE)
    public ResponseEntity<ApiResponse<PermissionResponse>> updatePermission(
            @PathVariable Long id,
            @Valid @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(permissionService.update(id, request)));
    }

    /**
     * Deletes a permission.
     *
     * @param id permission identifier
     * @return empty response
     */
    @Operation(summary = "Delete permission", description = "Requires PERMISSION_DELETE permission.")
    @DeleteMapping("/{id}")
    @PreAuthorize(AuthorizationExpressions.HAS_ADMIN_AND_PERMISSION_DELETE)
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
