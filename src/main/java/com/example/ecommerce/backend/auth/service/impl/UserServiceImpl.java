package com.example.ecommerce.backend.auth.service.impl;

import com.example.ecommerce.backend.auth.entity.User;
import com.example.ecommerce.backend.auth.entity.Permission;
import com.example.ecommerce.backend.auth.entity.Role;
import com.example.ecommerce.backend.auth.repository.UserRepository;
import com.example.ecommerce.backend.auth.security.AuthenticatedUser;
import com.example.ecommerce.backend.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Loads authenticated users and maps database roles and permissions to Spring
 * Security authorities.
 *
 * @author Pial Kanti Samadder
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String ROLE_PREFIX = "ROLE_";

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!Boolean.TRUE.equals(user.getIsActive())) {
            throw new DisabledException("User account is inactive.");
        }

        return new AuthenticatedUser(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                mapAuthorities(user));
    }

    private Set<SimpleGrantedAuthority> mapAuthorities(User user) {
        Set<SimpleGrantedAuthority> authorities = new LinkedHashSet<>();
        for (Role role : user.getRoles()) {
            authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.getCode().name()));
            role.getPermissions()
                    .stream()
                    .map(Permission::getCode)
                    .map(Enum::name)
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }
        return authorities;
    }
}
