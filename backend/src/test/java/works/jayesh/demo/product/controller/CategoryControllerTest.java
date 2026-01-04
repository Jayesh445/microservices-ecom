package works.jayesh.demo.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import works.jayesh.demo.common.exception.GlobalExceptionHandler;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.product.model.dto.CategoryRequest;
import works.jayesh.demo.product.model.dto.CategoryResponse;
import works.jayesh.demo.product.service.CategoryService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryController API Tests")
class CategoryControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private CategoryResponse categoryResponse;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();

        categoryResponse = CategoryResponse.builder()
                .id(1L)
                .name("Electronics")
                .slug("electronics")
                .description("Electronic devices and gadgets")
                .active(true)
                .build();

        categoryRequest = new CategoryRequest();
        categoryRequest.setName("Electronics");
        categoryRequest.setDescription("Electronic devices and gadgets");
        categoryRequest.setActive(true);
    }

    @Test
    @DisplayName("Should create category successfully")
    void createCategory_Success() throws Exception {
        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(categoryResponse);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Electronics"));
    }

    @Test
    @DisplayName("Should return 400 when category name is missing")
    void createCategory_MissingName() throws Exception {
        categoryRequest.setName(null);

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get category by ID successfully")
    void getCategoryById_Success() throws Exception {
        when(categoryService.getCategoryById(1L)).thenReturn(categoryResponse);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Electronics"));
    }

    @Test
    @DisplayName("Should return 404 when category not found")
    void getCategoryById_NotFound() throws Exception {
        when(categoryService.getCategoryById(999L))
                .thenThrow(new ResourceNotFoundException("Category not found with id: 999"));

        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get category by slug successfully")
    void getCategoryBySlug_Success() throws Exception {
        when(categoryService.getCategoryBySlug("electronics")).thenReturn(categoryResponse);

        mockMvc.perform(get("/api/categories/slug/electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.slug").value("electronics"));
    }

    @Test
    @DisplayName("Should get top level categories successfully")
    void getTopLevelCategories_Success() throws Exception {
        List<CategoryResponse> categories = Arrays.asList(categoryResponse);
        when(categoryService.getTopLevelCategories()).thenReturn(categories);

        mockMvc.perform(get("/api/categories/top-level"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("Electronics"));
    }

    @Test
    @DisplayName("Should get subcategories successfully")
    void getSubCategories_Success() throws Exception {
        CategoryResponse subCategory = CategoryResponse.builder()
                .id(2L)
                .name("Laptops")
                .slug("laptops")
                .parentCategoryId(1L)
                .build();

        List<CategoryResponse> subCategories = Arrays.asList(subCategory);
        when(categoryService.getSubCategories(1L)).thenReturn(subCategories);

        mockMvc.perform(get("/api/categories/1/subcategories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].parentCategoryId").value(1));
    }

    @Test
    @DisplayName("Should get all categories with pagination")
    void getAllCategories_Success() throws Exception {
        List<CategoryResponse> list = Arrays.asList(categoryResponse);
        Page<CategoryResponse> page = new PageImpl<>(list, PageRequest.of(0, 20), 1);

        when(categoryService.getAllCategories(any())).thenReturn(page);

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("Should update category successfully")
    void updateCategory_Success() throws Exception {
        categoryResponse.setName("Updated Electronics");
        when(categoryService.updateCategory(eq(1L), any(CategoryRequest.class))).thenReturn(categoryResponse);

        mockMvc.perform(put("/api/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent category")
    void updateCategory_NotFound() throws Exception {
        when(categoryService.updateCategory(eq(999L), any(CategoryRequest.class)))
                .thenThrow(new ResourceNotFoundException("Category not found with id: 999"));

        mockMvc.perform(put("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should delete category successfully")
    void deleteCategory_Success() throws Exception {
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent category")
    void deleteCategory_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Category not found with id: 999"))
                .when(categoryService).deleteCategory(999L);

        mockMvc.perform(delete("/api/categories/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle malformed JSON")
    void createCategory_MalformedJson() throws Exception {
        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle malformed ID in path")
    void getCategoryById_InvalidId() throws Exception {
        mockMvc.perform(get("/api/categories/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
