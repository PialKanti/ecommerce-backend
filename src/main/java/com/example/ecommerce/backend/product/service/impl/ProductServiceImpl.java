package com.example.ecommerce.backend.product.service.impl;

import com.example.ecommerce.backend.common.config.CacheConfig;
import com.example.ecommerce.backend.common.exception.ResourceConflictException;
import com.example.ecommerce.backend.product.dto.request.ProductCreateRequest;
import com.example.ecommerce.backend.product.dto.request.ProductUpdateRequest;
import com.example.ecommerce.backend.product.dto.response.ProductResponse;
import com.example.ecommerce.backend.product.entity.Category;
import com.example.ecommerce.backend.product.entity.Product;
import com.example.ecommerce.backend.product.mapper.ProductMapper;
import com.example.ecommerce.backend.product.repository.CategoryRepository;
import com.example.ecommerce.backend.product.repository.ProductRepository;
import com.example.ecommerce.backend.product.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ProductService} for managing product catalog items.
 *
 * <p>Handles SKU uniqueness checks, category lookup, persistence, and response
 * mapping for product CRUD workflows.</p>
 *
 * @author Pial Kanti Samadder
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse create(ProductCreateRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new ResourceConflictException("Product with SKU '" + request.sku() + "' already exists.");
        }

        Product product = productMapper.toEntity(request);
        product.setIsActive(request.isActive() != null ? request.isActive() : Boolean.TRUE);
        product.setCategory(getCategoryById(request.categoryId()));
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @Cacheable(value = CacheConfig.CACHE_PRODUCTS, key = "#id")
    public ProductResponse getById(Long id) {
        return productMapper.toResponse(getProductById(id));
    }

    @Override
    public Page<ProductResponse> getAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @CacheEvict(value = CacheConfig.CACHE_PRODUCTS, key = "#id")
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = getProductById(id);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setIsActive(request.isActive());
        product.setImageUrl(request.imageUrl());
        product.setCategory(getCategoryById(request.categoryId()));
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    @CacheEvict(value = CacheConfig.CACHE_PRODUCTS, key = "#id")
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new EntityNotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }

    private Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + categoryId));
    }
}
