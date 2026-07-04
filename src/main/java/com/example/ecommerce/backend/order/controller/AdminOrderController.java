package com.example.ecommerce.backend.order.controller;

import com.example.ecommerce.backend.auth.constants.AuthorizationExpressions;
import com.example.ecommerce.backend.auth.security.AuthenticatedUser;
import com.example.ecommerce.backend.common.constants.ApiEndpoints;
import com.example.ecommerce.backend.common.dto.response.ApiResponse;
import com.example.ecommerce.backend.order.dto.response.OrderResponse;
import com.example.ecommerce.backend.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin controller for support and order administration workflows.
 *
 * @author Pial Kanti Samadder
 */
@RestController
@RequestMapping(ApiEndpoints.Admin.BASE_ADMIN_ORDERS)
@RequiredArgsConstructor
@Tag(name = "Admin Orders", description = "Administrative order support operations")
@SecurityRequirement(name = "bearerAuth")
public class AdminOrderController {
    private final OrderService orderService;

    /**
     * Retrieves any order by identifier.
     *
     * @param id order identifier
     * @return matching order
     */
    @Operation(
            summary = "Get order for administration",
            description = "Requires PERMISSION_ORDER_READ permission.",
            responses = @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Order retrieved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponse.class)
                    )
            )
    )
    @GetMapping("/{id}")
    @PreAuthorize(AuthorizationExpressions.HAS_ORDER_READ)
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.getOrderForAdministration(id)));
    }

    /**
     * Cancels any confirmed order.
     *
     * @param id order identifier
     * @return cancelled order
     */
    @Operation(summary = "Cancel order for administration", description = "Requires PERMISSION_ORDER_CANCEL permission.")
    @PostMapping("/{id}/cancel")
    @PreAuthorize(AuthorizationExpressions.HAS_ORDER_CANCEL)
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(orderService.cancelOrderForAdministration(currentUserId(), id)));
    }

    private Long currentUserId() {
        AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        return user.id();
    }
}
