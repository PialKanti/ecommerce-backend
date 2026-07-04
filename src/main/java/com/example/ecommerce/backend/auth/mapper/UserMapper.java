package com.example.ecommerce.backend.auth.mapper;

import com.example.ecommerce.backend.auth.dto.request.RegisterRequest;
import com.example.ecommerce.backend.auth.dto.response.UserResponse;
import com.example.ecommerce.backend.auth.entity.User;
import com.example.ecommerce.backend.auth.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for user request, entity, and response models.
 *
 * @author Pial Kanti Samadder
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    /**
     * Converts a registration request to a user entity.
     *
     * @param request registration payload
     * @return user entity containing simple request fields
     */
    @Mapping(target = "password", ignore = true)
    User toEntity(RegisterRequest request);

    /**
     * Converts a user entity to its safe API response representation.
     *
     * @param user user entity
     * @return safe user response
     */
    @Mapping(target = "roles", expression = "java(toRoleCodes(user))")
    UserResponse toResponse(User user);

    /**
     * Maps assigned roles to role code values.
     *
     * @param user user entity
     * @return assigned role codes
     */
    default Set<String> toRoleCodes(User user) {
        return user.getRoles()
                .stream()
                .map(Role::getCode)
                .map(Enum::name)
                .collect(Collectors.toCollection(java.util.TreeSet::new));
    }
}
