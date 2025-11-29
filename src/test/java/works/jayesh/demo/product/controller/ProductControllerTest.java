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
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import works.jayesh.demo.common.exception.GlobalExceptionHandler;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.product.model.dto.ProductCreateRequest;
import works.jayesh.demo.product.model.dto.ProductResponse;
import works.jayesh.demo.product.service.ProductService;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

/**
 * Controller tests for ProductController
 * Tests REST API contracts: HTTP status codes, validation, response structure
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController API Tests")
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ObjectMapper objectMapper;
    private ProductResponse testProduct;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        testProduct = new ProductResponse();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setSku("TEST-001");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStockQuantity(100);
    }

    // ==================== CREATE TESTS ====================

    @Test
    @DisplayName("POST /api/products - Should return 201 with product response")
    void createProduct_Success() throws Exception {
        // Given
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Test Product");
        request.setSku("TEST-001");
        request.setDescription("Test description");
        request.setBrand("TestBrand");
        request.setPrice(new BigDecimal("99.99"));
        request.setStockQuantity(100);
        request.setCategoryId(1L);
        request.setSellerId(1L);

        when(productService.createProduct(any(ProductCreateRequest.class)))
                .thenReturn(testProduct);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product created successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Product"))
                .andExpect(jsonPath("$.data.sku").value("TEST-001"))
                .andExpect(jsonPath("$.data.price").value(99.99));

        verify(productService, times(1)).createProduct(any(ProductCreateRequest.class));
    }

    @Test
    @DisplayName("POST /api/products - Should return 400 for missing required fields")
    void createProduct_MissingFields() throws Exception {
        // Given - Empty request
        ProductCreateRequest request = new ProductCreateRequest();

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any());
    }

    @Test
    @DisplayName("POST /api/products - Should return 400 for negative price")
    void createProduct_NegativePrice() throws Exception {
        // Given
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Test Product");
        request.setSku("TEST-001");
        request.setDescription("Test description");
        request.setBrand("TestBrand");
        request.setPrice(new BigDecimal("-10.00"));
        request.setStockQuantity(100);
        request.setCategoryId(1L);
        request.setSellerId(1L);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any());
    }

    @Test
    @DisplayName("POST /api/products - Should return 400 for negative stock")
    void createProduct_NegativeStock() throws Exception {
        // Given
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName("Test Product");
        request.setSku("TEST-001");
        request.setDescription("Test description");
        request.setBrand("TestBrand");
        request.setPrice(new BigDecimal("99.99"));
        request.setStockQuantity(-1);
        request.setCategoryId(1L);
        request.setSellerId(1L);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any());
    }

    // ==================== READ TESTS ====================

    @Test
    @DisplayName("GET /api/products/{productId} - Should return 200 with product response")
    void getProductById_Success() throws Exception {
        // Given
        when(productService.getProductById(1L)).thenReturn(testProduct);

        // When & Then
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Product"));

        verify(productService, times(1)).getProductById(1L);
    }

    @Test
    @DisplayName("GET /api/products/{productId} - Should return 404 when product not found")
    void getProductById_NotFound() throws Exception {
        // Given
        when(productService.getProductById(999L))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 999"));

        // When & Then
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).getProductById(999L);
    }

    @Test
    @DisplayName("GET /api/products/slug/{slug} - Should return 200 with product response")
    void getProductBySlug_Success() throws Exception {
        // Given
        when(productService.getProductBySlug("test-product")).thenReturn(testProduct);

        // When & Then
        mockMvc.perform(get("/api/products/slug/test-product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Test Product"));

        verify(productService, times(1)).getProductBySlug("test-product");
    }

    @Test
    @DisplayName("GET /api/products - Should return 200 with paginated products")
    void getAllProducts_Success() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<ProductResponse> page = new PageImpl<>(Collections.singletonList(testProduct), pageable, 1);
        when(productService.getAllProducts(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Products retrieved successfully"))
                .andExpect(jsonPath("$.data.content[0].id").value(1));

        verify(productService, times(1)).getAllProducts(any());
    }

    @Test
    @DisplayName("GET /api/products - Should handle pagination parameters")
    void getAllProducts_WithPagination() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<ProductResponse> page = new PageImpl<>(Collections.singletonList(testProduct), pageable, 1);
        when(productService.getAllProducts(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());

        verify(productService, times(1)).getAllProducts(any());
    }

    @Test
    @DisplayName("GET /api/products/search - Should return 200 with search results")
    void searchProducts_Success() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<ProductResponse> page = new PageImpl<>(Collections.singletonList(testProduct), pageable, 1);
        when(productService.searchProducts(eq("Test"), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/products/search")
                .param("keyword", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Search completed successfully"))
                .andExpect(jsonPath("$.data.content[0].name").value("Test Product"));

        verify(productService, times(1)).searchProducts(eq("Test"), any());
    }

    // ==================== UPDATE STOCK TESTS ====================

    @Test
    @DisplayName("PATCH /api/products/{productId}/stock - Should return 200")
    void updateStock_Success() throws Exception {
        // Given
        doNothing().when(productService).updateStock(1L, 50);

        // When & Then
        mockMvc.perform(patch("/api/products/1/stock")
                .param("quantity", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Stock updated successfully"));

        verify(productService, times(1)).updateStock(1L, 50);
    }

    @Test
    @DisplayName("PATCH /api/products/{productId}/stock - Should return 404 when product not found")
    void updateStock_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Product not found with id: 999"))
                .when(productService).updateStock(999L, 50);

        // When & Then
        mockMvc.perform(patch("/api/products/999/stock")
                .param("quantity", "50"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).updateStock(999L, 50);
    }

    // ==================== DELETE TESTS ====================

    @Test
    @DisplayName("DELETE /api/products/{productId} - Should return 200")
    void deleteProduct_Success() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1L);

        // When & Then
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Product deleted successfully"));

        verify(productService, times(1)).deleteProduct(1L);
    }

    @Test
    @DisplayName("DELETE /api/products/{productId} - Should return 404 when product not found")
    void deleteProduct_NotFound() throws Exception {
        // Given
        doThrow(new ResourceNotFoundException("Product not found with id: 999"))
                .when(productService).deleteProduct(999L);

        // When & Then
        mockMvc.perform(delete("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).deleteProduct(999L);
    }

    // ==================== EDGE CASES ====================

    @Test
    @DisplayName("GET /api/products/{productId} - Should handle malformed ID")
    void getProductById_MalformedId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/products/invalid"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).getProductById(anyLong());
    }

    @Test
    @DisplayName("POST /api/products - Should handle malformed JSON")
    void createProduct_MalformedJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid-json"))
                .andExpect(status().isBadRequest());

        verify(productService, never()).createProduct(any());
    }
}
