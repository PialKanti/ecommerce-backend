package com.example.ecommerce.backend.product.controller;

import com.example.ecommerce.backend.auth.dto.request.LoginRequest;
import com.example.ecommerce.backend.auth.dto.request.RegisterRequest;
import com.example.ecommerce.backend.auth.entity.Role;
import com.example.ecommerce.backend.auth.entity.User;
import com.example.ecommerce.backend.auth.enums.RoleCode;
import com.example.ecommerce.backend.auth.repository.RoleRepository;
import com.example.ecommerce.backend.auth.repository.UserRepository;
import com.example.ecommerce.backend.auth.service.TokenBlacklistService;
import com.example.ecommerce.backend.common.constants.ApiEndpoints;
import com.example.ecommerce.backend.product.dto.request.CategoryCreateRequest;
import com.example.ecommerce.backend.product.dto.request.CategoryUpdateRequest;
import com.example.ecommerce.backend.product.entity.Category;
import com.example.ecommerce.backend.product.repository.CategoryRepository;
import com.example.ecommerce.backend.product.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link AdminCategoryController}.
 *
 * @author Pial Kanti Samadder
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Admin Category Controller Integration Tests")
class AdminCategoryControllerIntegrationTest {

    private static final String BASE_URL = ApiEndpoints.Admin.BASE_ADMIN_CATEGORIES;
    private static final String LOGIN_ENDPOINT = ApiEndpoints.Auth.BASE_AUTH + "/login";
    private static final String REGISTER_ENDPOINT = ApiEndpoints.Auth.BASE_AUTH + "/register";
    private static final String RAW_PASSWORD = "StrongPass123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        categoryRepository.deleteAll();
        adminToken = createUserWithRoleAndLogin(RoleCode.ADMIN, "admin");
    }

    @Test
    @DisplayName("Should create category successfully and persist it to the database")
    void shouldCreateCategorySuccessfully() throws Exception {
        CategoryCreateRequest request = new CategoryCreateRequest("Electronics", "ELECTRONICS", "Devices and gadgets");

        performCreateCategory(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.name").value("Electronics"))
                .andExpect(jsonPath("$.data.code").value("ELECTRONICS"))
                .andExpect(jsonPath("$.data.description").value("Devices and gadgets"))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.modifiedAt").exists());

        assertThat(categoryRepository.existsByCode("ELECTRONICS")).isTrue();
    }

    @Test
    @DisplayName("Should return 400 when create payload has blank name or code")
    void shouldReturnValidationErrorForInvalidCreatePayload() throws Exception {
        CategoryCreateRequest request = new CategoryCreateRequest("", "", null);

        performCreateCategory(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    @Test
    @DisplayName("Should return 409 when category code already exists")
    void shouldReturnConflictWhenCategoryCodeAlreadyExists() throws Exception {
        persistCategory("Electronics", "ELECTRONICS");

        performCreateCategory(new CategoryCreateRequest("Electronics 2", "ELECTRONICS", null))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Resource conflict"))
                .andExpect(jsonPath("$.detail", containsString("ELECTRONICS")));
    }

    @Test
    @DisplayName("Should return 401 when request carries no authentication token")
    void shouldReturn401WhenRequestHasNoToken() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CategoryCreateRequest("Electronics", "ELECTRONICS", null))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return 403 when authenticated user lacks the required admin role")
    void shouldReturn403WhenUserLacksAdminRole() throws Exception {
        String customerToken = createUserWithRoleAndLogin(RoleCode.CUSTOMER, "customer");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CategoryCreateRequest("Electronics", "ELECTRONICS", null)))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + customerToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should update category name and description while preserving code")
    void shouldUpdateCategorySuccessfully() throws Exception {
        Category saved = persistCategory("Old Name", "OLDCODE");

        performUpdateCategory(saved.getId(), new CategoryUpdateRequest("New Name", "Updated description"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(saved.getId()))
                .andExpect(jsonPath("$.data.name").value("New Name"))
                .andExpect(jsonPath("$.data.code").value("OLDCODE"))
                .andExpect(jsonPath("$.data.description").value("Updated description"));
    }

    @Test
    @DisplayName("Should return 404 when updating a non-existent category")
    void shouldReturn404WhenUpdatingNonExistentCategory() throws Exception {
        performUpdateCategory(999999L, new CategoryUpdateRequest("Name", null))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
    }

    @Test
    @DisplayName("Should return 400 when update payload has blank name")
    void shouldReturnValidationErrorForInvalidUpdatePayload() throws Exception {
        Category saved = persistCategory("Electronics", "ELECTRONICS");

        performUpdateCategory(saved.getId(), new CategoryUpdateRequest("", null))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"));
    }

    // ── PUT /api/v1/admin/categories/{id}/status ──────────────────────────────

    @Test
    @DisplayName("Should deactivate an active category")
    void shouldDeactivateCategorySuccessfully() throws Exception {
        Category saved = persistCategory("Electronics", "ELECTRONICS");

        performToggleStatus(saved.getId(), false)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(saved.getId()))
                .andExpect(jsonPath("$.data.isActive").value(false));
    }

    @Test
    @DisplayName("Should activate a previously deactivated category")
    void shouldActivatePreviouslyDeactivatedCategory() throws Exception {
        Category saved = persistCategory("Electronics", "ELECTRONICS");
        saved.setIsActive(false);
        categoryRepository.save(saved);

        performToggleStatus(saved.getId(), true)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(saved.getId()))
                .andExpect(jsonPath("$.data.isActive").value(true));
    }

    @Test
    @DisplayName("Should delete category and confirm removal from the database")
    void shouldDeleteCategoryAndVerifyRemovalFromDatabase() throws Exception {
        Category saved = persistCategory("Electronics", "ELECTRONICS");

        performDeleteCategory(saved.getId())
                .andExpect(status().isNoContent());

        assertThat(categoryRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    @DisplayName("Should return 404 when deleting a non-existent category")
    void shouldReturn404WhenDeletingNonExistentCategory() throws Exception {
        performDeleteCategory(999999L)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
    }

    private ResultActions performCreateCategory(CategoryCreateRequest body) throws Exception {
        return mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .content(objectMapper.writeValueAsString(body)));
    }

    private ResultActions performUpdateCategory(Long id, CategoryUpdateRequest body) throws Exception {
        return mockMvc.perform(put(BASE_URL + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                .content(objectMapper.writeValueAsString(body)));
    }

    private ResultActions performToggleStatus(Long id, boolean isActive) throws Exception {
        return mockMvc.perform(put(BASE_URL + "/{id}/status", id)
                .param("isActive", String.valueOf(isActive))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken));
    }

    private ResultActions performDeleteCategory(Long id) throws Exception {
        return mockMvc.perform(delete(BASE_URL + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken));
    }

    private String createUserWithRoleAndLogin(RoleCode roleCode, String prefix) throws Exception {
        String username = generateUniqueUsername(prefix);
        String email = generateUniqueEmail(prefix);

        mockMvc.perform(post(REGISTER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest(
                                "Test", "User", username, email, "+8801700000000", RAW_PASSWORD))))
                .andExpect(status().isOk());

        if (roleCode != RoleCode.CUSTOMER) {
            User user = userRepository.findByUsername(username).orElseThrow();
            Role role = roleRepository.findByCode(roleCode).orElseThrow();
            user.getRoles().add(role);
            userRepository.save(user);
        }

        return loginAndGetToken(username, RAW_PASSWORD);
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        String responseJson = mockMvc.perform(post(LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(username, password))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(responseJson).path("data").path("accessToken").asText();
    }

    private Category persistCategory(String name, String code) {
        return categoryService.create(new CategoryCreateRequest(name, code, null));
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
