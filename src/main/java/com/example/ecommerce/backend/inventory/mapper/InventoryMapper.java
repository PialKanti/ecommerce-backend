package com.example.ecommerce.backend.inventory.mapper;

import com.example.ecommerce.backend.inventory.dto.request.InventoryCreateRequest;
import com.example.ecommerce.backend.inventory.dto.response.InventoryResponse;
import com.example.ecommerce.backend.inventory.entity.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for converting inventory request and entity models.
 *
 * @author Pial Kanti Samadder
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryMapper {
    /**
     * Converts an inventory creation request to an inventory entity.
     *
     * @param request inventory creation payload
     * @return inventory entity containing simple request fields
     */
    default Inventory toEntity(InventoryCreateRequest request) {
        Inventory inventory = new Inventory();
        inventory.setTotalQuantity(request.totalQuantity());
        inventory.setReservedQuantity(0);
        return inventory;
    }

    /**
     * Converts an inventory entity to its API response representation.
     *
     * @param inventory inventory entity
     * @return inventory response DTO
     */
    default InventoryResponse toResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getId(),
                inventory.getProduct().getId(),
                inventory.getTotalQuantity(),
                inventory.getReservedQuantity(),
                inventory.getTotalQuantity() - inventory.getReservedQuantity(),
                inventory.getVersion(),
                inventory.getCreatedAt(),
                inventory.getModifiedAt(),
                inventory.getCreatedBy(),
                inventory.getModifiedBy()
        );
    }
}
