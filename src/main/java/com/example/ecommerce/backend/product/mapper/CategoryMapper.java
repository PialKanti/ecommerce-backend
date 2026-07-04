package com.example.ecommerce.backend.product.mapper;

import com.example.ecommerce.backend.product.dto.request.CategoryCreateRequest;
import com.example.ecommerce.backend.product.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    Category toEntity(CategoryCreateRequest request);
}
