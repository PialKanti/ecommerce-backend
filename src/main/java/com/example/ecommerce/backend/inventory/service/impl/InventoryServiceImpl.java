package com.example.ecommerce.backend.inventory.service.impl;

import com.example.ecommerce.backend.common.exception.ResourceConflictException;
import com.example.ecommerce.backend.inventory.dto.request.InventoryCreateRequest;
import com.example.ecommerce.backend.inventory.dto.request.InventoryQuantityRequest;
import com.example.ecommerce.backend.inventory.dto.response.InventoryResponse;
import com.example.ecommerce.backend.inventory.entity.Inventory;
import com.example.ecommerce.backend.inventory.mapper.InventoryMapper;
import com.example.ecommerce.backend.inventory.repository.InventoryRepository;
import com.example.ecommerce.backend.inventory.service.InventoryService;
import com.example.ecommerce.backend.product.entity.Product;
import com.example.ecommerce.backend.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link InventoryService} for managing product inventory.
 *
 * <p>Handles product lookup, one-inventory-per-product validation, inventory
 * quantity transitions, persistence, and response mapping. Concurrent writes
 * are protected by the inventory entity's optimistic locking version.</p>
 *
 * @author Pial Kanti Samadder
 */
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional
    public InventoryResponse create(InventoryCreateRequest request) {
        if (inventoryRepository.existsByProductId(request.productId())) {
            throw new ResourceConflictException("Inventory already exists for product: " + request.productId());
        }

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + request.productId()));

        Inventory inventory = inventoryMapper.toEntity(request);
        inventory.setProduct(product);
        return inventoryMapper.toResponse(inventoryRepository.saveAndFlush(inventory));
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponse getByProductId(Long productId) {
        return inventoryMapper.toResponse(getInventoryByProductId(productId));
    }

    @Override
    @Transactional
    public InventoryResponse increase(Long productId, InventoryQuantityRequest request) {
        Inventory inventory = getInventoryByProductId(productId);
        inventory.setTotalQuantity(inventory.getTotalQuantity() + request.quantity());
        return inventoryMapper.toResponse(inventoryRepository.saveAndFlush(inventory));
    }

    @Override
    @Transactional
    public InventoryResponse decrease(Long productId, InventoryQuantityRequest request) {
        Inventory inventory = getInventoryByProductId(productId);
        int newTotalQuantity = inventory.getTotalQuantity() - request.quantity();
        if (newTotalQuantity < inventory.getReservedQuantity()) {
            throw new ResourceConflictException("Cannot decrease inventory below reserved quantity.");
        }

        inventory.setTotalQuantity(newTotalQuantity);
        return inventoryMapper.toResponse(inventoryRepository.saveAndFlush(inventory));
    }

    @Override
    @Transactional
    public InventoryResponse reserve(Long productId, InventoryQuantityRequest request) {
        Inventory inventory = getInventoryByProductId(productId);
        if (request.quantity() > getAvailableQuantity(inventory)) {
            throw new ResourceConflictException("Insufficient available inventory to reserve requested quantity.");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() + request.quantity());
        return inventoryMapper.toResponse(inventoryRepository.saveAndFlush(inventory));
    }

    @Override
    @Transactional
    public InventoryResponse release(Long productId, InventoryQuantityRequest request) {
        Inventory inventory = getInventoryByProductId(productId);
        if (request.quantity() > inventory.getReservedQuantity()) {
            throw new ResourceConflictException("Cannot release more inventory than currently reserved.");
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - request.quantity());
        return inventoryMapper.toResponse(inventoryRepository.saveAndFlush(inventory));
    }

    private Inventory getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product: " + productId));
    }

    private int getAvailableQuantity(Inventory inventory) {
        return inventory.getTotalQuantity() - inventory.getReservedQuantity();
    }
}
