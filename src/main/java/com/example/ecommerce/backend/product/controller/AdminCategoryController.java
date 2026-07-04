package com.example.ecommerce.backend.product.controller;

import com.example.ecommerce.backend.auth.constants.AuthorizationExpressions;
import com.example.ecommerce.backend.common.constants.ApiEndpoints;
import com.example.ecommerce.backend.common.dto.response.ApiResponse;
import com.example.ecommerce.backend.product.dto.request.CategoryCreateRequest;
import com.example.ecommerce.backend.product.dto.request.CategoryUpdateRequest;
import com.example.ecommerce.backend.product.entity.Category;
import com.example.ecommerce.backend.product.service.CategoryService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin controller for category mutations.
 *
 * @author Pial Kanti Samadder
 */
@RestController
@RequestMapping(ApiEndpoints.Admin.BASE_ADMIN_CATEGORIES)
@RequiredArgsConstructor
@Tag(name = "Admin Categories", description = "Administrative product category operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminCategoryController {
    private final CategoryService categoryService;

    /**
     * Creates a category.
     *
     * @param request category creation payload
     * @return created category
     */
    @Operation(
            summary = "Create category",
            description = "Requires PERMISSION_CATEGORY_CREATE permission.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CategoryCreateRequest.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "name": "Electronics",
                                      "code": "ELEC",
                                      "description": "Devices and gadgets."
                                    }
                                    """)
                    )
            )
    )
    @PostMapping
    @PreAuthorize(AuthorizationExpressions.HAS_CATEGORY_CREATE)
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.create(request)));
    }

    /**
     * Updates a category.
     *
     * @param id category identifier
     * @param request category update payload
     * @return updated category
     */
    @Operation(summary = "Update category", description = "Requires PERMISSION_CATEGORY_UPDATE permission.")
    @PutMapping("/{id}")
    @PreAuthorize(AuthorizationExpressions.HAS_CATEGORY_UPDATE)
    public ResponseEntity<ApiResponse<Category>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.update(id, request)));
    }

    /**
     * Updates category active status.
     *
     * @param id category identifier
     * @param isActive desired active status
     * @return updated category
     */
    @Operation(summary = "Activate or deactivate category", description = "Requires PERMISSION_CATEGORY_UPDATE permission.")
    @PutMapping("/{id}/status")
    @PreAuthorize(AuthorizationExpressions.HAS_CATEGORY_UPDATE)
    public ResponseEntity<ApiResponse<Category>> toggleCategoryStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.toggleStatus(id, isActive)));
    }

    /**
     * Deletes a category.
     *
     * @param id category identifier
     * @return empty response
     */
    @Operation(summary = "Delete category", description = "Requires PERMISSION_CATEGORY_DELETE permission.")
    @DeleteMapping("/{id}")
    @PreAuthorize(AuthorizationExpressions.HAS_CATEGORY_DELETE)
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
