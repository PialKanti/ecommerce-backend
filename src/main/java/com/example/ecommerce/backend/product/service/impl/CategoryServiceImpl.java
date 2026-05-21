package com.example.ecommerce.backend.product.service.impl;

import com.example.ecommerce.backend.common.config.CacheConfig;
import com.example.ecommerce.backend.common.dto.response.PaginatedResponse;
import com.example.ecommerce.backend.common.exception.ResourceConflictException;
import com.example.ecommerce.backend.product.dto.request.CategoryCreateRequest;
import com.example.ecommerce.backend.product.dto.request.CategoryUpdateRequest;
import com.example.ecommerce.backend.product.entity.Category;
import com.example.ecommerce.backend.product.mapper.CategoryMapper;
import com.example.ecommerce.backend.product.repository.CategoryRepository;
import com.example.ecommerce.backend.product.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link CategoryService} for managing product categories.
 *
 * <p>Provides CRUD operations and status management for categories
 * with unique code validation.</p>
 *
 * @author Pial Kanti Samadder
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @CacheEvict(value = CacheConfig.CACHE_CATEGORIES_LIST, allEntries = true)
    public Category create(CategoryCreateRequest request) {
        if (categoryRepository.existsByCode(request.code())) {
            throw new ResourceConflictException("Category with Code '" + request.code() + "' already exists.");
        }

        Category category = categoryMapper.toEntity(request);
        return categoryRepository.save(category);
    }

    @Override
    @Cacheable(value = CacheConfig.CACHE_CATEGORIES, key = "#id")
    public Category getById(Long id) {
        return getCategoryById(id);
    }

    @Override
    @Cacheable(value = CacheConfig.CACHE_CATEGORIES_LIST, key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public PaginatedResponse<Category> getAll(Pageable pageable) {
        return PaginatedResponse.of(categoryRepository.findAll(pageable));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CACHE_CATEGORIES, key = "#id"),
            @CacheEvict(value = CacheConfig.CACHE_CATEGORIES_LIST, allEntries = true)
    })
    public Category update(Long id, CategoryUpdateRequest request) {
        Category category = getCategoryById(id);
        category.setName(request.name());
        category.setDescription(request.description());
        return categoryRepository.save(category);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CACHE_CATEGORIES, key = "#id"),
            @CacheEvict(value = CacheConfig.CACHE_CATEGORIES_LIST, allEntries = true)
    })
    public Category toggleStatus(Long id, Boolean isActive) {
        Category category = getCategoryById(id);
        category.setIsActive(isActive);
        return categoryRepository.save(category);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConfig.CACHE_CATEGORIES, key = "#id"),
            @CacheEvict(value = CacheConfig.CACHE_CATEGORIES_LIST, allEntries = true)
    })
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }

    private Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + id));
    }
}
