package com.example.ecommerce.backend.product.controller;

import com.example.ecommerce.backend.common.constants.ApiEndpoints;
import com.example.ecommerce.backend.common.dto.response.ApiResponse;
import com.example.ecommerce.backend.common.dto.response.PaginatedResponse;
import com.example.ecommerce.backend.product.entity.Category;
import com.example.ecommerce.backend.product.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public REST controller for browsing product categories.
 *
 * <p>Administrative category mutations are exposed separately under
 * {@code /api/v1/admin/categories}.</p>
 *
 * @author Pial Kanti Samadder
 */
@RestController
@RequestMapping(ApiEndpoints.Category.BASE_CATEGORY)
@RequiredArgsConstructor
@Tag(
        name = "Category",
        description = "Public operations for browsing product categories"
)
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * Retrieves a category by its identifier.
     *
     * @param id category identifier
     * @return response containing the matching category
     */
    @Operation(
            summary = "Get category by ID",
            description = "Retrieves a product category by its unique database identifier.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Category retrieved successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = Category.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "No category exists for the supplied identifier",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(
            @Parameter(description = "Unique identifier of the category.", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getById(id)));
    }

    /**
     * Retrieves categories using page-based pagination.
     *
     * @param page zero-based page index
     * @param size number of records per page
     * @return response containing paginated category data
     */
    @Operation(
            summary = "List categories",
            description = "Retrieves product categories using zero-based pagination.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Categories listed successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaginatedResponse.class)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<Category>>> listCategories(
            @Parameter(description = "Zero-based page index.", example = "0")
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of categories to return per page.", example = "10")
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAll(pageable)));
    }
}
