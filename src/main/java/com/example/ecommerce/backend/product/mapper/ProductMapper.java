package com.example.ecommerce.backend.product.mapper;

import com.example.ecommerce.backend.product.dto.request.ProductCreateRequest;
import com.example.ecommerce.backend.product.dto.response.ProductResponse;
import com.example.ecommerce.backend.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for converting product request and entity models.
 *
 * @author Pial Kanti Samadder
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
    /**
     * Converts a product creation request to a product entity.
     *
     * @param request product creation payload
     * @return product entity containing simple request fields
     */
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductCreateRequest request);

    /**
     * Converts a product entity to its API response representation.
     *
     * @param product product entity
     * @return product response DTO
     */
    @Mapping(target = "categoryId", source = "category.id")
    ProductResponse toResponse(Product product);
}
