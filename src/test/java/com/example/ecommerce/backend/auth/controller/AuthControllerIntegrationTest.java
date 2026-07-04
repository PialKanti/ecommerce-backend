package com.example.ecommerce.backend.auth.controller;

import com.example.ecommerce.backend.auth.dto.request.RegisterRequest;
import com.example.ecommerce.backend.auth.entity.User;
import com.example.ecommerce.backend.auth.enums.RoleCode;
import com.example.ecommerce.backend.auth.repository.UserRepository;
import com.example.ecommerce.backend.common.constants.ApiEndpoints;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link AuthController}.
 *
 * @author Pial Kanti Samadder
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Auth Controller Integration Tests")
class AuthControllerIntegrationTest {
    private static final String REGISTER_ENDPOINT = ApiEndpoints.Auth.BASE_AUTH + "/register";
    private static final String RAW_PASSWORD = "StrongPass123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Should register user successfully and persist encoded password")
    void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = createValidRegisterRequest("customer");

        performRegister(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.firstName").value(request.firstName()))
                .andExpect(jsonPath("$.data.lastName").value(request.lastName()))
                .andExpect(jsonPath("$.data.username").value(request.username()))
                .andExpect(jsonPath("$.data.email").value(request.email()))
                .andExpect(jsonPath("$.data.phoneNumber").value(request.phoneNumber()))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andExpect(jsonPath("$.data.roles").isArray())
                .andExpect(jsonPath("$.data.roles", hasItem(RoleCode.CUSTOMER.name())))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.modifiedAt").exists())
                .andExpect(jsonPath("$.data.password").doesNotExist());

        User persistedUser = userRepository.findByUsername(request.username()).orElseThrow();

        assertThat(persistedUser.getEmail()).isEqualTo(request.email());
        assertThat(persistedUser.getFirstName()).isEqualTo(request.firstName());
        assertThat(persistedUser.getLastName()).isEqualTo(request.lastName());
        assertThat(persistedUser.getPhoneNumber()).isEqualTo(request.phoneNumber());
        assertThat(persistedUser.getIsActive()).isTrue();
        assertThat(persistedUser.getPassword()).isNotEqualTo(RAW_PASSWORD);
        assertThat(passwordEncoder.matches(RAW_PASSWORD, persistedUser.getPassword())).isTrue();
        assertThat(persistedUser.getRoles())
                .extracting(role -> role.getCode().name())
                .containsExactlyInAnyOrder(RoleCode.CUSTOMER.name());
    }

    @Test
    @DisplayName("Should return bad request when registration payload is invalid")
    void shouldReturnBadRequestWhenPayloadIsInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "",
                "Example",
                "ab",
                "not-an-email",
                "+8801700000000",
                "short"
        );

        performRegister(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"));

        assertThat(userRepository.existsByUsername(request.username())).isFalse();
        assertThat(userRepository.existsByEmail(request.email())).isFalse();
    }

    @Test
    @DisplayName("Should return conflict when username already exists")
    void shouldReturnConflictWhenUsernameAlreadyExists() throws Exception {
        RegisterRequest existingUser = createValidRegisterRequest("duplicate-username");
        RegisterRequest duplicateUsername = new RegisterRequest(
                "Nadia",
                "Rahman",
                existingUser.username(),
                generateUniqueEmail("duplicate-username"),
                "+8801711111111",
                RAW_PASSWORD
        );

        performRegister(existingUser)
                .andExpect(status().isOk());

        performRegister(duplicateUsername)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Resource conflict"))
                .andExpect(jsonPath("$.detail", containsString("User with username '" + existingUser.username() + "' already exists.")));
    }

    @Test
    @DisplayName("Should return conflict when email already exists")
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        RegisterRequest existingUser = createValidRegisterRequest("duplicate-email");
        RegisterRequest duplicateEmail = new RegisterRequest(
                "Nadia",
                "Rahman",
                generateUniqueUsername("duplicate-email"),
                existingUser.email(),
                "+8801711111111",
                RAW_PASSWORD
        );

        performRegister(existingUser)
                .andExpect(status().isOk());

        performRegister(duplicateEmail)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Resource conflict"))
                .andExpect(jsonPath("$.detail", containsString("User with email '" + existingUser.email() + "' already exists.")));
    }

    @Test
    @DisplayName("Should allow anonymous user to access register endpoint")
    void shouldAllowAnonymousUserToRegister() throws Exception {
        RegisterRequest request = createValidRegisterRequest("anonymous");

        performRegister(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value(request.username()));
    }

    private ResultActions performRegister(RegisterRequest request) throws Exception {
        return mockMvc.perform(post(REGISTER_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    private RegisterRequest createValidRegisterRequest(String usernamePrefix) {
        return new RegisterRequest(
                "Ayesha",
                "Rahman",
                generateUniqueUsername(usernamePrefix),
                generateUniqueEmail(usernamePrefix),
                "+8801700000000",
                RAW_PASSWORD
        );
    }

    private String generateUniqueUsername(String prefix) {
        return prefix + "-" + generateUniqueSuffix();
    }

    private String generateUniqueEmail(String prefix) {
        return prefix + "-" + generateUniqueSuffix() + "@example.com";
    }

    private String generateUniqueSuffix() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
