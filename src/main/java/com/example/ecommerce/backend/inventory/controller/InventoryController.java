package com.example.ecommerce.backend.inventory.controller;

import com.example.ecommerce.backend.common.constants.ApiEndpoints;
import com.example.ecommerce.backend.common.dto.response.ApiResponse;
import com.example.ecommerce.backend.inventory.dto.response.InventoryResponse;
import com.example.ecommerce.backend.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authenticated inventory lookup.
 *
 * <p>Inventory mutation operations are exposed through the dedicated admin
 * inventory controller.</p>
 *
 * @author Pial Kanti Samadder
 */
@RestController
@RequestMapping(ApiEndpoints.Inventory.BASE_INVENTORY)
@RequiredArgsConstructor
@Tag(
        name = "Inventory",
        description = "Operations for reading product inventory"
)
public class InventoryController {
    private final InventoryService inventoryService;

    /**
     * Retrieves inventory by product identifier.
     *
     * @param productId product identifier
     * @return response containing the matching inventory
     */
    @Operation(
            summary = "Get inventory by product ID",
            description = "Retrieves the inventory record that belongs to the supplied product identifier.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Inventory retrieved successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = InventoryResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "No inventory exists for the supplied product identifier",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventoryByProductId(
            @Parameter(description = "Unique identifier of the product.", example = "1", required = true)
            @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success(inventoryService.getByProductId(productId)));
    }
}
