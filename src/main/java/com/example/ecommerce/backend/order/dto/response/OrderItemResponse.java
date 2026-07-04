package com.example.ecommerce.backend.order.dto.response;

/**
 * Response payload representing one product snapshot line in an order.
 *
 * @author Pial Kanti Samadder
 */
public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        Double unitPrice,
        Integer quantity,
        Double totalPrice
) {
}
