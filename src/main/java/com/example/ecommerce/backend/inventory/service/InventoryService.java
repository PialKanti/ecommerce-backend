package com.example.ecommerce.backend.inventory.service;

import com.example.ecommerce.backend.inventory.dto.request.InventoryCreateRequest;
import com.example.ecommerce.backend.inventory.dto.request.InventoryQuantityRequest;
import com.example.ecommerce.backend.inventory.dto.response.InventoryResponse;

/**
 * Service interface for product inventory management.
 *
 * <p>Provides inventory creation, lookup, stock adjustment, reservation, and
 * release operations while exposing DTO responses to API consumers.</p>
 *
 * @author Pial Kanti Samadder
 */
public interface InventoryService {
    /**
     * Creates inventory for a product.
     *
     * @param request inventory creation details
     * @return newly created inventory response
     * @throws com.example.ecommerce.backend.common.exception.ResourceConflictException if inventory already exists
     * @throws jakarta.persistence.EntityNotFoundException if product not found
     */
    InventoryResponse create(InventoryCreateRequest request);

    /**
     * Retrieves inventory by product ID.
     *
     * @param productId product identifier
     * @return matching inventory response
     * @throws jakarta.persistence.EntityNotFoundException if inventory not found
     */
    InventoryResponse getByProductId(Long productId);

    /**
     * Increases total inventory quantity.
     *
     * @param productId product identifier
     * @param request quantity adjustment details
     * @return updated inventory response
     */
    InventoryResponse increase(Long productId, InventoryQuantityRequest request);

    /**
     * Decreases total inventory quantity.
     *
     * @param productId product identifier
     * @param request quantity adjustment details
     * @return updated inventory response
     * @throws com.example.ecommerce.backend.common.exception.ResourceConflictException if total would fall below reserved
     */
    InventoryResponse decrease(Long productId, InventoryQuantityRequest request);

    /**
     * Reserves available inventory quantity.
     *
     * @param productId product identifier
     * @param request quantity reservation details
     * @return updated inventory response
     * @throws com.example.ecommerce.backend.common.exception.ResourceConflictException if available quantity is insufficient
     */
    InventoryResponse reserve(Long productId, InventoryQuantityRequest request);

    /**
     * Releases reserved inventory quantity.
     *
     * @param productId product identifier
     * @param request quantity release details
     * @return updated inventory response
     * @throws com.example.ecommerce.backend.common.exception.ResourceConflictException if reserved quantity is insufficient
     */
    InventoryResponse release(Long productId, InventoryQuantityRequest request);
}
