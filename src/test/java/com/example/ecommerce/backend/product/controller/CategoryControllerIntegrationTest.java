package com.example.ecommerce.backend.product.controller;

import com.example.ecommerce.backend.common.constants.ApiEndpoints;
import com.example.ecommerce.backend.product.dto.request.CategoryCreateRequest;
import com.example.ecommerce.backend.product.entity.Category;
import com.example.ecommerce.backend.product.repository.CategoryRepository;
import com.example.ecommerce.backend.product.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link CategoryController}.
 *
 * @author Pial Kanti Samadder
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Category Controller Integration Tests")
class CategoryControllerIntegrationTest {

    private static final String BASE_URL = ApiEndpoints.Category.BASE_CATEGORY;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    @Test
    @DisplayName("Should return 200 and full category body for a valid ID")
    void shouldReturnCategoryForValidId() throws Exception {
        Category saved = persistCategory("Electronics", "ELECTRONICS");

        performGetById(saved.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.data.id").value(saved.getId()))
                .andExpect(jsonPath("$.data.name").value("Electronics"))
                .andExpect(jsonPath("$.data.code").value("ELECTRONICS"))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.modifiedAt").exists());
    }

    @Test
    @DisplayName("Should return 404 when category ID does not exist")
    void shouldReturn404WhenCategoryIdDoesNotExist() throws Exception {
        performGetById(999999L)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"));
    }

    @Test
    @DisplayName("Should allow anonymous user to access GET /api/v1/categories/{id}")
    void shouldAllowAnonymousUserToAccessCategoryById() throws Exception {
        Category saved = persistCategory("Books", "BOOKS");

        performGetById(saved.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should return all expected fields in single-category response")
    void shouldReturnAllExpectedFieldsInGetByIdResponse() throws Exception {
        Category saved = persistCategory("Home Appliances", "HOME_APPLIANCES", "Devices for home use");

        performGetById(saved.getId())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.name").value("Home Appliances"))
                .andExpect(jsonPath("$.data.code").value("HOME_APPLIANCES"))
                .andExpect(jsonPath("$.data.description").value("Devices for home use"))
                .andExpect(jsonPath("$.data.isActive").value(true))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.modifiedAt").exists());
    }

    @Test
    @DisplayName("Should return 200 and paginated list containing all persisted categories")
    void shouldReturnPaginatedListWithCategories() throws Exception {
        persistCategory("Clothing", "CLOTHING");
        persistCategory("Sports", "SPORTS");

        performListCategories(0, 10)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Operation successful"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(2));
    }

    @Test
    @DisplayName("Should return empty content list when no categories exist")
    void shouldReturnEmptyListWhenNoCategoriesExist() throws Exception {
        performListCategories(0, 10)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    @DisplayName("Should reflect default page=0 and size=10 in pagination metadata")
    void shouldReflectDefaultPaginationMetadata() throws Exception {
        persistCategory("Toys", "TOYS");

        performListCategories(0, 10)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(10));
    }

    @Test
    @DisplayName("Should respect custom page and size query parameters")
    void shouldRespectCustomPageAndSizeParams() throws Exception {
        persistCategory("Category A", "CAT_A");
        persistCategory("Category B", "CAT_B");
        persistCategory("Category C", "CAT_C");

        performListCategories(1, 2)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(1))
                .andExpect(jsonPath("$.data.size").value(2))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }

    @Test
    @DisplayName("Should return correct pagination metadata — totalElements, totalPages, page, size")
    void shouldReturnCorrectPaginationMetadata() throws Exception {
        persistCategory("Alpha", "ALPHA");
        persistCategory("Beta", "BETA");
        persistCategory("Gamma", "GAMMA");

        performListCategories(0, 2)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.totalPages").value(2))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(2))
                .andExpect(jsonPath("$.data.content.length()").value(2));
    }

    @Test
    @DisplayName("Should allow anonymous user to access GET /api/v1/categories")
    void shouldAllowAnonymousUserToListCategories() throws Exception {
        performListCategories(0, 10)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should include all expected fields for each category in list response")
    void shouldIncludeAllExpectedFieldsInListResponse() throws Exception {
        persistCategory("Garden", "GARDEN", "Outdoor and garden supplies");

        performListCategories(0, 10)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").isNumber())
                .andExpect(jsonPath("$.data.content[0].name").value("Garden"))
                .andExpect(jsonPath("$.data.content[0].code").value("GARDEN"))
                .andExpect(jsonPath("$.data.content[0].description").value("Outdoor and garden supplies"))
                .andExpect(jsonPath("$.data.content[0].isActive").value(true))
                .andExpect(jsonPath("$.data.content[0].createdAt").exists())
                .andExpect(jsonPath("$.data.content[0].modifiedAt").exists());
    }

    private ResultActions performGetById(Long id) throws Exception {
        return mockMvc.perform(get(BASE_URL + "/{id}", id));
    }

    private ResultActions performListCategories(int page, int size) throws Exception {
        return mockMvc.perform(get(BASE_URL)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));
    }

    private Category persistCategory(String name, String code) {
        return categoryService.create(new CategoryCreateRequest(name, code, null));
    }

    private Category persistCategory(String name, String code, String description) {
        return categoryService.create(new CategoryCreateRequest(name, code, description));
    }
}
