package works.jayesh.demo.product.service;

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
import org.springframework.data.domain.Pageable;

import works.jayesh.demo.common.exception.DuplicateResourceException;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.product.model.dto.ProductCreateRequest;
import works.jayesh.demo.product.model.dto.ProductResponse;
import works.jayesh.demo.product.model.entity.*;
import works.jayesh.demo.product.repository.CategoryRepository;
import works.jayesh.demo.product.repository.ProductRepository;
import works.jayesh.demo.user.model.entity.User;
import works.jayesh.demo.user.model.entity.UserRole;
import works.jayesh.demo.user.repository.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Category testCategory;
    private User testSeller;
    private ProductCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id(1L)
                .name("Electronics")
                .slug("electronics")
                .active(true)
                .build();

        testSeller = User.builder()
                .id(1L)
                .email("seller@example.com")
                .firstName("John")
                .lastName("Seller")
                .role(UserRole.SELLER)
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .sku("TEST-SKU-001")
                .description("Test description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .slug("test-product")
                .category(testCategory)
                .seller(testSeller)
                .status(ProductStatus.ACTIVE)
                .featured(false)
                .images(new ArrayList<>())
                .tags(new ArrayList<>())
                .build();

        createRequest = new ProductCreateRequest();
        createRequest.setName("Test Product");
        createRequest.setSku("TEST-SKU-001");
        createRequest.setDescription("Test description");
        createRequest.setPrice(new BigDecimal("99.99"));
        createRequest.setStockQuantity(100);
        createRequest.setCategoryId(1L);
        createRequest.setSellerId(1L);
    }

    // ==================== CREATE PRODUCT TESTS ====================

    @Test
    @DisplayName("Should create product successfully")
    void createProduct_Success() {
        // Given
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testSeller));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        ProductResponse response = productService.createProduct(createRequest);

        // Then
        assertNotNull(response);
        assertEquals("Test Product", response.getName());
        assertEquals("TEST-SKU-001", response.getSku());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when SKU already exists")
    void createProduct_DuplicateSku() {
        // Given
        when(productRepository.existsBySku(anyString())).thenReturn(true);

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> {
            productService.createProduct(createRequest);
        });

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when category not found")
    void createProduct_CategoryNotFound() {
        // Given
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.createProduct(createRequest);
        });

        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when seller not found")
    void createProduct_SellerNotFound() {
        // Given
        when(productRepository.existsBySku(anyString())).thenReturn(false);
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.createProduct(createRequest);
        });

        verify(productRepository, never()).save(any(Product.class));
    }

    // ==================== GET PRODUCT TESTS ====================

    @Test
    @DisplayName("Should get product by ID successfully")
    void getProductById_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        ProductResponse response = productService.getProductById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Test Product", response.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when product not found by ID")
    void getProductById_NotFound() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.getProductById(999L);
        });
    }

    @Test
    @DisplayName("Should get product by slug successfully")
    void getProductBySlug_Success() {
        // Given
        when(productRepository.findBySlug("test-product")).thenReturn(Optional.of(testProduct));

        // When
        ProductResponse response = productService.getProductBySlug("test-product");

        // Then
        assertNotNull(response);
        assertEquals("test-product", response.getSlug());
        verify(productRepository, times(1)).findBySlug("test-product");
    }

    @Test
    @DisplayName("Should get all products with pagination")
    void getAllProducts_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, 1);

        when(productRepository.findByActiveTrue(pageable)).thenReturn(productPage);

        // When
        Page<ProductResponse> response = productService.getAllProducts(pageable);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(productRepository, times(1)).findByActiveTrue(pageable);
    }

    // ==================== DELETE PRODUCT TESTS ====================

    @Test
    @DisplayName("Should delete product successfully")
    void deleteProduct_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        assertDoesNotThrow(() -> productService.deleteProduct(1L));

        // Then
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent product")
    void deleteProduct_NotFound() {
        // Given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.deleteProduct(999L);
        });

        verify(productRepository, never()).delete(any(Product.class));
    }

    // ==================== SEARCH TESTS ====================

    @Test
    @DisplayName("Should search products by keyword")
    void searchProducts_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(products, pageable, 1);

        when(productRepository.searchProducts("test", pageable)).thenReturn(productPage);

        // When
        Page<ProductResponse> response = productService.searchProducts("test", pageable);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(productRepository, times(1)).searchProducts("test", pageable);
    }

    // ==================== STOCK MANAGEMENT TESTS ====================

    @Test
    @DisplayName("Should update product stock successfully")
    void updateStock_Success() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        assertDoesNotThrow(() -> productService.updateStock(1L, 150));

        // Then
        verify(productRepository, times(1)).save(testProduct);
    }
}
