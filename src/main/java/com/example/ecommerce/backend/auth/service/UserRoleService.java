package com.example.ecommerce.backend.auth.service;

import com.example.ecommerce.backend.auth.dto.response.RoleResponse;

import java.util.List;

/**
 * Service contract for assigning roles to application users.
 *
 * @author Pial Kanti Samadder
 */
public interface UserRoleService {
    List<RoleResponse> getRoles(Long userId);

    List<RoleResponse> assignRole(Long userId, Long roleId);

    List<RoleResponse> removeRole(Long userId, Long roleId);
}
