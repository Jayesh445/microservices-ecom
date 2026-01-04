package works.jayesh.demo.product.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import works.jayesh.demo.common.exception.DuplicateResourceException;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.product.model.dto.CategoryRequest;
import works.jayesh.demo.product.model.dto.CategoryResponse;
import works.jayesh.demo.product.model.entity.Category;
import works.jayesh.demo.product.repository.CategoryRepository;
import works.jayesh.demo.product.repository.ProductRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Unit Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic products")
                .slug("electronics")
                .active(true)
                .displayOrder(1)
                .products(new ArrayList<>())
                .build();

        categoryRequest = new CategoryRequest();
        categoryRequest.setName("Electronics");
        categoryRequest.setDescription("Electronic products");
        categoryRequest.setActive(true);
        categoryRequest.setDisplayOrder(1);
    }

    // ==================== CREATE CATEGORY TESTS ====================

    @Test
    @DisplayName("Should create category successfully")
    void createCategory_Success() {
        // Given
        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // When
        CategoryResponse response = categoryService.createCategory(categoryRequest);

        // Then
        assertNotNull(response);
        assertEquals("Electronics", response.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw exception when category name already exists")
    void createCategory_DuplicateName() {
        // Given
        when(categoryRepository.existsByName(anyString())).thenReturn(true);

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> {
            categoryService.createCategory(categoryRequest);
        });

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Should create category with parent category")
    void createCategory_WithParent() {
        // Given
        Category parentCategory = Category.builder()
                .id(2L)
                .name("Parent Category")
                .build();

        categoryRequest.setParentCategoryId(2L);

        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // When
        CategoryResponse response = categoryService.createCategory(categoryRequest);

        // Then
        assertNotNull(response);
        verify(categoryRepository, times(1)).findById(2L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw exception when parent category not found")
    void createCategory_ParentNotFound() {
        // Given
        categoryRequest.setParentCategoryId(999L);
        when(categoryRepository.existsByName(anyString())).thenReturn(false);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.createCategory(categoryRequest);
        });
    }

    // ==================== GET CATEGORY TESTS ====================

    @Test
    @DisplayName("Should get category by ID successfully")
    void getCategoryById_Success() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // When
        CategoryResponse response = categoryService.getCategoryById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when category not found by ID")
    void getCategoryById_NotFound() {
        // Given
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.getCategoryById(999L);
        });
    }

    @Test
    @DisplayName("Should get category by slug successfully")
    void getCategoryBySlug_Success() {
        // Given
        when(categoryRepository.findBySlug("electronics")).thenReturn(Optional.of(testCategory));

        // When
        CategoryResponse response = categoryService.getCategoryBySlug("electronics");

        // Then
        assertNotNull(response);
        assertEquals("electronics", response.getSlug());
        verify(categoryRepository, times(1)).findBySlug("electronics");
    }

    @Test
    @DisplayName("Should get top level categories")
    void getTopLevelCategories_Success() {
        // Given
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepository.findTopLevelCategories()).thenReturn(categories);

        // When
        List<CategoryResponse> response = categoryService.getTopLevelCategories();

        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(categoryRepository, times(1)).findTopLevelCategories();
    }

    @Test
    @DisplayName("Should get subcategories")
    void getSubCategories_Success() {
        // Given
        Category subCategory = Category.builder()
                .id(2L)
                .name("Laptops")
                .parentCategory(testCategory)
                .products(new ArrayList<>())
                .build();

        List<Category> subCategories = Arrays.asList(subCategory);
        when(categoryRepository.findByParentCategoryId(1L)).thenReturn(subCategories);

        // When
        List<CategoryResponse> response = categoryService.getSubCategories(1L);

        // Then
        assertNotNull(response);
        assertEquals(1, response.size());
        verify(categoryRepository, times(1)).findByParentCategoryId(1L);
    }

    // ==================== UPDATE CATEGORY TESTS ====================

    @Test
    @DisplayName("Should update category successfully")
    void updateCategory_Success() {
        // Given
        categoryRequest.setName("Updated Electronics");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // When
        CategoryResponse response = categoryService.updateCategory(1L, categoryRequest);

        // Then
        assertNotNull(response);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent category")
    void updateCategory_NotFound() {
        // Given
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.updateCategory(999L, categoryRequest);
        });
    }

    // ==================== DELETE CATEGORY TESTS ====================

    @Test
    @DisplayName("Should delete category successfully")
    void deleteCategory_Success() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.countByCategoryId(1L)).thenReturn(0L);
        doNothing().when(categoryRepository).delete(any(Category.class));

        // When
        assertDoesNotThrow(() -> categoryService.deleteCategory(1L));

        // Then
        verify(categoryRepository, times(1)).delete(testCategory);
    }

    @Test
    @DisplayName("Should throw exception when deleting category with products")
    void deleteCategory_HasProducts() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.countByCategoryId(1L)).thenReturn(5L);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            categoryService.deleteCategory(1L);
        });

        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent category")
    void deleteCategory_NotFound() {
        // Given
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.deleteCategory(999L);
        });

        verify(categoryRepository, never()).delete(any(Category.class));
    }
}
