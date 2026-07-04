package com.example.ecommerce.backend.auth.service;

import com.example.ecommerce.backend.auth.dto.request.PermissionRequest;
import com.example.ecommerce.backend.auth.dto.response.PermissionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service contract for permission catalog management.
 *
 * @author Pial Kanti Samadder
 */
public interface PermissionService {
    Page<PermissionResponse> getAll(Pageable pageable);

    PermissionResponse getById(Long id);

    PermissionResponse create(PermissionRequest request);

    PermissionResponse update(Long id, PermissionRequest request);

    void delete(Long id);
}
