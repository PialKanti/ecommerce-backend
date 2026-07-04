package com.example.ecommerce.backend.order.service;

import com.example.ecommerce.backend.common.exception.ResourceConflictException;
import com.example.ecommerce.backend.inventory.entity.Inventory;
import com.example.ecommerce.backend.inventory.repository.InventoryRepository;
import com.example.ecommerce.backend.order.entity.Order;
import com.example.ecommerce.backend.order.entity.OrderStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCancellationService {
    private final InventoryRepository inventoryRepository;

    public void cancelConfirmedOrder(Order order, Long modifiedBy) {
        if (order.getStatus() != OrderStatus.CONFIRMED) {
            log.warn("Order cancellation rejected for orderId={} because status={}", order.getId(), order.getStatus());
            throw new ResourceConflictException("Only confirmed orders can be cancelled.");
        }

        order.getItems().forEach(item -> releaseInventory(item.getProductId(), item.getQuantity()));
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setModifiedBy(modifiedBy);
    }

    private void releaseInventory(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product: " + productId));

        if (quantity > inventory.getReservedQuantity()) {
            log.warn("Cannot release inventory for productId={}, requested={}, reserved={}",
                    productId, quantity, inventory.getReservedQuantity());
            throw new ResourceConflictException("Cannot release more inventory than currently reserved.");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventoryRepository.saveAndFlush(inventory);
    }
}
