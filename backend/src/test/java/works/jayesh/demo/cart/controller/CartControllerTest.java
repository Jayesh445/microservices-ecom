package works.jayesh.demo.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import works.jayesh.demo.cart.model.dto.AddToCartRequest;
import works.jayesh.demo.cart.model.dto.CartItemResponse;
import works.jayesh.demo.cart.model.dto.CartResponse;
import works.jayesh.demo.cart.service.CartService;
import works.jayesh.demo.common.exception.GlobalExceptionHandler;
import works.jayesh.demo.common.exception.ResourceNotFoundException;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartController API Tests")
class CartControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private CartResponse cartResponse;
    private AddToCartRequest addToCartRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(cartController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        CartItemResponse item = CartItemResponse.builder()
                .id(1L)
                .productId(1L)
                .productName("Test Product")
                .quantity(2)
                .price(new BigDecimal("99.99"))
                .totalPrice(new BigDecimal("199.98"))
                .build();

        cartResponse = CartResponse.builder()
                .id(1L)
                .userId(1L)
                .items(Arrays.asList(item))
                .totalItems(2)
                .totalAmount(new BigDecimal("199.98"))
                .build();

        addToCartRequest = new AddToCartRequest();
        addToCartRequest.setProductId(1L);
        addToCartRequest.setQuantity(2);
    }

    @Test
    @DisplayName("Should get cart successfully")
    void getCart_Success() throws Exception {
        when(cartService.getCart(1L)).thenReturn(cartResponse);

        mockMvc.perform(get("/api/cart/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.totalItems").value(2));
    }

    @Test
    @DisplayName("Should return 404 when user cart not found")
    void getCart_NotFound() throws Exception {
        when(cartService.getCart(999L))
                .thenThrow(new ResourceNotFoundException("Cart not found for user: 999"));

        mockMvc.perform(get("/api/cart/user/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should add product to cart successfully")
    void addToCart_Success() throws Exception {
        when(cartService.addToCart(eq(1L), any(AddToCartRequest.class))).thenReturn(cartResponse);

        mockMvc.perform(post("/api/cart/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.items").isArray());
    }

    @Test
    @DisplayName("Should return 400 when adding product with missing productId")
    void addToCart_MissingProductId() throws Exception {
        addToCartRequest.setProductId(null);

        mockMvc.perform(post("/api/cart/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addToCartRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return 400 when adding product with invalid quantity")
    void addToCart_InvalidQuantity() throws Exception {
        addToCartRequest.setQuantity(0);

        mockMvc.perform(post("/api/cart/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addToCartRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should update cart item quantity successfully")
    void updateCartItemQuantity_Success() throws Exception {
        when(cartService.updateCartItemQuantity(1L, 1L, 5)).thenReturn(cartResponse);

        mockMvc.perform(put("/api/cart/user/1/product/1")
                .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent product")
    void updateCartItemQuantity_ProductNotFound() throws Exception {
        when(cartService.updateCartItemQuantity(1L, 999L, 5))
                .thenThrow(new ResourceNotFoundException("Product not found in cart: 999"));

        mockMvc.perform(put("/api/cart/user/1/product/999")
                .param("quantity", "5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should remove product from cart successfully")
    void removeFromCart_Success() throws Exception {
        CartResponse emptyCart = CartResponse.builder()
                .id(1L)
                .userId(1L)
                .items(Arrays.asList())
                .totalItems(0)
                .totalAmount(BigDecimal.ZERO)
                .build();

        when(cartService.removeFromCart(1L, 1L)).thenReturn(emptyCart);

        mockMvc.perform(delete("/api/cart/user/1/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalItems").value(0));
    }

    @Test
    @DisplayName("Should return 404 when removing non-existent product")
    void removeFromCart_ProductNotFound() throws Exception {
        when(cartService.removeFromCart(1L, 999L))
                .thenThrow(new ResourceNotFoundException("Product not found in cart: 999"));

        mockMvc.perform(delete("/api/cart/user/1/product/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should clear cart successfully")
    void clearCart_Success() throws Exception {
        mockMvc.perform(delete("/api/cart/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should return 404 when clearing non-existent cart")
    void clearCart_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Cart not found for user: 999"))
                .when(cartService).clearCart(999L);

        mockMvc.perform(delete("/api/cart/user/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle malformed JSON")
    void addToCart_MalformedJson() throws Exception {
        mockMvc.perform(post("/api/cart/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle malformed user ID")
    void getCart_InvalidUserId() throws Exception {
        mockMvc.perform(get("/api/cart/user/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle malformed product ID")
    void removeFromCart_InvalidProductId() throws Exception {
        mockMvc.perform(delete("/api/cart/user/1/product/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
