package com.example.ecommerce.backend.product.controller;

import com.example.ecommerce.backend.common.constants.ApiEndpoints;
import com.example.ecommerce.backend.common.dto.response.ApiResponse;
import com.example.ecommerce.backend.common.dto.response.PaginatedResponse;
import com.example.ecommerce.backend.product.dto.response.ProductResponse;
import com.example.ecommerce.backend.product.service.ProductService;
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
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing product catalog items.
 *
 * <p>Exposes versioned product endpoints under
 * {@link ApiEndpoints.Product#BASE_PRODUCT} and wraps successful responses with
 * the common {@link ApiResponse} structure used by the API.</p>
 *
 * @author Pial Kanti Samadder
 */
@RestController
@RequestMapping(ApiEndpoints.Product.BASE_PRODUCT)
@RequiredArgsConstructor
@Tag(
        name = "Product",
        description = "Operations for managing product catalog items"
)
public class ProductController {
    private final ProductService productService;

    /**
     * Retrieves a product by its identifier.
     *
     * @param id product identifier
     * @return response containing the matching product
     * @throws jakarta.persistence.EntityNotFoundException when no product exists for the identifier
     */
    @Operation(
            summary = "Get product by ID",
            description = "Retrieves a product catalog item by its unique database identifier.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Product retrieved successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponse.class)
                            )
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "404",
                            description = "No product exists for the supplied identifier",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(
            @Parameter(description = "Unique identifier of the product.", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
    }

    /**
     * Retrieves products using page-based pagination.
     *
     * @param page zero-based page index
     * @param size number of records per page
     * @return response containing paginated product data
     */
    @Operation(
            summary = "List products",
            description = "Retrieves product catalog items using zero-based pagination.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200",
                            description = "Products listed successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = PaginatedResponse.class)
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<ApiResponse<PaginatedResponse<ProductResponse>>> listProducts(
            @Parameter(description = "Zero-based page index.", example = "0")
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Number of products to return per page.", example = "10")
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(PaginatedResponse.of(productService.getAll(pageable))));
    }

}
