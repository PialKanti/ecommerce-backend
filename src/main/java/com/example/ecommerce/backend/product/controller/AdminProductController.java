package com.example.ecommerce.backend.product.controller;

import com.example.ecommerce.backend.auth.constants.AuthorizationExpressions;
import com.example.ecommerce.backend.common.constants.ApiEndpoints;
import com.example.ecommerce.backend.common.dto.response.ApiResponse;
import com.example.ecommerce.backend.product.dto.request.ProductCreateRequest;
import com.example.ecommerce.backend.product.dto.request.ProductUpdateRequest;
import com.example.ecommerce.backend.product.dto.response.ProductResponse;
import com.example.ecommerce.backend.product.service.ProductService;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin controller for product catalog mutations.
 *
 * <p>Route-level security protects the admin namespace while method-level
 * permissions protect each product operation.</p>
 *
 * @author Pial Kanti Samadder
 */
@RestController
@RequestMapping(ApiEndpoints.Admin.BASE_ADMIN_PRODUCTS)
@RequiredArgsConstructor
@Tag(name = "Admin Products", description = "Administrative product catalog operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminProductController {
    private final ProductService productService;

    /**
     * Creates a new product.
     *
     * @param request product creation payload
     * @return created product response
     */
    @Operation(
            summary = "Create product",
            description = "Requires PERMISSION_PRODUCT_CREATE permission.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ProductCreateRequest.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "sku": "PHONE-001",
                                      "name": "Smartphone",
                                      "description": "Android smartphone with 128GB storage.",
                                      "price": 499.99,
                                      "isActive": true,
                                      "categoryId": 1,
                                      "imageUrl": "https://example.com/products/phone-001.jpg"
                                    }
                                    """)
                    )
            )
    )
    @PostMapping
    @PreAuthorize(AuthorizationExpressions.HAS_PRODUCT_CREATE)
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.create(request)));
    }

    /**
     * Updates product details.
     *
     * @param id product identifier
     * @param request product update payload
     * @return updated product response
     */
    @Operation(summary = "Update product", description = "Requires PERMISSION_PRODUCT_UPDATE permission.")
    @PutMapping("/{id}")
    @PreAuthorize(AuthorizationExpressions.HAS_PRODUCT_UPDATE)
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(productService.update(id, request)));
    }

    /**
     * Deletes a product.
     *
     * @param id product identifier
     * @return empty response
     */
    @Operation(summary = "Delete product", description = "Requires PERMISSION_PRODUCT_DELETE permission.")
    @DeleteMapping("/{id}")
    @PreAuthorize(AuthorizationExpressions.HAS_PRODUCT_DELETE)
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
