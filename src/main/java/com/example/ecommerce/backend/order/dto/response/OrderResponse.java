package com.example.ecommerce.backend.order.dto.response;

import com.example.ecommerce.backend.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response payload representing a customer order.
 *
 * <p>Contains order-level state, audit details, and stable item snapshots. No
 * persistence entities are exposed directly to API consumers.</p>
 *
 * @author Pial Kanti Samadder
 */
public record OrderResponse(
        Long id,
        UUID orderNumber,
        Long userId,
        OrderStatus status,
        Double totalAmount,
        LocalDateTime cancelledAt,
        List<OrderItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt,
        Long createdBy,
        Long modifiedBy
) {
}
