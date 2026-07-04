package com.example.ecommerce.backend.order.mapper;

import com.example.ecommerce.backend.order.dto.response.OrderItemResponse;
import com.example.ecommerce.backend.order.dto.response.OrderResponse;
import com.example.ecommerce.backend.order.entity.Order;
import com.example.ecommerce.backend.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for converting order entities to API responses.
 *
 * @author Pial Kanti Samadder
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
    /**
     * Converts an order entity to its API response representation.
     *
     * @param order order entity
     * @return order response DTO
     */
    default OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(this::toItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCancelledAt(),
                items,
                order.getCreatedAt(),
                order.getModifiedAt(),
                order.getCreatedBy(),
                order.getModifiedBy()
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getTotalPrice()
        );
    }
}
