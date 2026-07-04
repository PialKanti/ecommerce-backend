package com.example.ecommerce.backend.inventory.controller;

import com.example.ecommerce.backend.auth.constants.AuthorizationExpressions;
import com.example.ecommerce.backend.common.constants.ApiEndpoints;
import com.example.ecommerce.backend.common.dto.response.ApiResponse;
import com.example.ecommerce.backend.inventory.dto.request.InventoryCreateRequest;
import com.example.ecommerce.backend.inventory.dto.request.InventoryQuantityRequest;
import com.example.ecommerce.backend.inventory.dto.response.InventoryResponse;
import com.example.ecommerce.backend.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin controller for inventory creation and stock movement operations.
 *
 * @author Pial Kanti Samadder
 */
@RestController
@RequestMapping(ApiEndpoints.Admin.BASE_ADMIN_INVENTORY)
@RequiredArgsConstructor
@Tag(name = "Admin Inventory", description = "Administrative inventory operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminInventoryController {
    private final InventoryService inventoryService;

    /**
     * Creates inventory for a product.
     *
     * @param request inventory creation payload
     * @return created inventory
     */
    @Operation(
            summary = "Create product inventory",
            description = "Requires PERMISSION_INVENTORY_MANAGE permission.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = InventoryCreateRequest.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "productId": 1,
                                      "totalQuantity": 100
                                    }
                                    """)
                    )
            )
    )
    @PostMapping
    @PreAuthorize(AuthorizationExpressions.HAS_INVENTORY_MANAGE)
    public ResponseEntity<ApiResponse<InventoryResponse>> createInventory(
            @Valid @RequestBody InventoryCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.create(request)));
    }

    /**
     * Increases inventory quantity.
     *
     * @param productId product identifier
     * @param request quantity payload
     * @return updated inventory
     */
    @Operation(summary = "Increase inventory quantity", description = "Requires PERMISSION_INVENTORY_MANAGE permission.")
    @PostMapping("/{productId}/increase")
    @PreAuthorize(AuthorizationExpressions.HAS_INVENTORY_MANAGE)
    public ResponseEntity<ApiResponse<InventoryResponse>> increaseInventory(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryQuantityRequest request) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.increase(productId, request)));
    }

    /**
     * Decreases inventory quantity.
     *
     * @param productId product identifier
     * @param request quantity payload
     * @return updated inventory
     */
    @Operation(summary = "Decrease inventory quantity", description = "Requires PERMISSION_INVENTORY_MANAGE permission.")
    @PostMapping("/{productId}/decrease")
    @PreAuthorize(AuthorizationExpressions.HAS_INVENTORY_MANAGE)
    public ResponseEntity<ApiResponse<InventoryResponse>> decreaseInventory(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryQuantityRequest request) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.decrease(productId, request)));
    }

    /**
     * Reserves available inventory quantity.
     *
     * @param productId product identifier
     * @param request quantity payload
     * @return updated inventory
     */
    @Operation(summary = "Reserve inventory quantity", description = "Requires PERMISSION_INVENTORY_MANAGE permission.")
    @PostMapping("/{productId}/reserve")
    @PreAuthorize(AuthorizationExpressions.HAS_INVENTORY_MANAGE)
    public ResponseEntity<ApiResponse<InventoryResponse>> reserveInventory(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryQuantityRequest request) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.reserve(productId, request)));
    }

    /**
     * Releases reserved inventory quantity.
     *
     * @param productId product identifier
     * @param request quantity payload
     * @return updated inventory
     */
    @Operation(summary = "Release reserved inventory quantity", description = "Requires PERMISSION_INVENTORY_MANAGE permission.")
    @PostMapping("/{productId}/release")
    @PreAuthorize(AuthorizationExpressions.HAS_INVENTORY_MANAGE)
    public ResponseEntity<ApiResponse<InventoryResponse>> releaseInventory(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryQuantityRequest request) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.release(productId, request)));
    }
}
