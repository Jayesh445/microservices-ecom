package works.jayesh.demo.cart.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import works.jayesh.demo.cart.model.dto.AddToCartRequest;
import works.jayesh.demo.cart.model.dto.CartResponse;
import works.jayesh.demo.cart.model.entity.Cart;
import works.jayesh.demo.cart.model.entity.CartItem;
import works.jayesh.demo.cart.repository.CartItemRepository;
import works.jayesh.demo.cart.repository.CartRepository;
import works.jayesh.demo.common.exception.InsufficientStockException;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.product.model.entity.Product;
import works.jayesh.demo.product.repository.ProductRepository;
import works.jayesh.demo.user.model.entity.User;
import works.jayesh.demo.user.repository.UserRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Unit Tests")
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Product testProduct;
    private Cart testCart;
    private CartItem testCartItem;
    private AddToCartRequest addToCartRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .sku("TEST-001")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .images(new ArrayList<>())
                .tags(new ArrayList<>())
                .build();

        testCart = Cart.builder()
                .id(1L)
                .user(testUser)
                .items(new ArrayList<>())
                .build();

        testCartItem = CartItem.builder()
                .id(1L)
                .cart(testCart)
                .product(testProduct)
                .quantity(2)
                .price(new BigDecimal("99.99"))
                .build();

        addToCartRequest = new AddToCartRequest();
        addToCartRequest.setProductId(1L);
        addToCartRequest.setQuantity(2);
    }

    // ==================== ADD TO CART TESTS ====================

    @Test
    @DisplayName("Should add product to cart successfully")
    void addToCart_Success() {
        // Given
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);

        // When
        CartResponse response = cartService.addToCart(1L, addToCartRequest);

        // Then
        assertNotNull(response);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should update quantity when product already in cart")
    void addToCart_UpdateExistingItem() {
        // Given
        testCart.getItems().add(testCartItem);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);

        // When
        CartResponse response = cartService.addToCart(1L, addToCartRequest);

        // Then
        assertNotNull(response);
        verify(cartItemRepository, times(1)).save(testCartItem);
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void addToCart_ProductNotFound() {
        // Given
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.addToCart(1L, addToCartRequest);
        });

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw exception when insufficient stock")
    void addToCart_InsufficientStock() {
        // Given
        testProduct.setStockQuantity(1); // Not enough for 2 items
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When & Then
        assertThrows(InsufficientStockException.class, () -> {
            cartService.addToCart(1L, addToCartRequest);
        });

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should create new cart if not exists")
    void addToCart_CreateNewCart() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);

        // When
        CartResponse response = cartService.addToCart(1L, addToCartRequest);

        // Then
        assertNotNull(response);
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    // ==================== UPDATE CART ITEM QUANTITY TESTS ====================

    @Test
    @DisplayName("Should update cart item quantity successfully")
    void updateCartItemQuantity_Success() {
        // Given
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong()))
                .thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);

        // When
        CartResponse response = cartService.updateCartItemQuantity(1L, 1L, 5);

        // Then
        assertNotNull(response);
        verify(cartItemRepository, times(1)).save(testCartItem);
    }

    @Test
    @DisplayName("Should remove item when quantity is zero")
    void updateCartItemQuantity_RemoveWhenZero() {
        // Given
        testCart.getItems().add(testCartItem);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong()))
                .thenReturn(Optional.of(testCartItem));
        doNothing().when(cartItemRepository).delete(any(CartItem.class));

        // When
        CartResponse response = cartService.updateCartItemQuantity(1L, 1L, 0);

        // Then
        assertNotNull(response);
        verify(cartItemRepository, times(1)).delete(testCartItem);
    }

    @Test
    @DisplayName("Should throw exception when cart item not found")
    void updateCartItemQuantity_NotFound() {
        // Given
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.updateCartItemQuantity(1L, 1L, 5);
        });
    }

    @Test
    @DisplayName("Should throw exception when updating with insufficient stock")
    void updateCartItemQuantity_InsufficientStock() {
        // Given
        testProduct.setStockQuantity(3); // Not enough for 5 items
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong()))
                .thenReturn(Optional.of(testCartItem));

        // When & Then
        assertThrows(InsufficientStockException.class, () -> {
            cartService.updateCartItemQuantity(1L, 1L, 5);
        });
    }

    // ==================== REMOVE FROM CART TESTS ====================

    @Test
    @DisplayName("Should remove product from cart successfully")
    void removeFromCart_Success() {
        // Given
        testCart.getItems().add(testCartItem);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong()))
                .thenReturn(Optional.of(testCartItem));
        doNothing().when(cartItemRepository).delete(any(CartItem.class));

        // When
        CartResponse response = cartService.removeFromCart(1L, 1L);

        // Then
        assertNotNull(response);
        verify(cartItemRepository, times(1)).delete(testCartItem);
    }

    @Test
    @DisplayName("Should throw exception when removing non-existent item")
    void removeFromCart_NotFound() {
        // Given
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        when(cartItemRepository.findByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.removeFromCart(1L, 1L);
        });

        verify(cartItemRepository, never()).delete(any(CartItem.class));
    }

    // ==================== GET CART TESTS ====================

    @Test
    @DisplayName("Should get cart successfully")
    void getCart_Success() {
        // Given
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));

        // When
        CartResponse response = cartService.getCart(1L);

        // Then
        assertNotNull(response);
        verify(cartRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Should throw exception when cart not found")
    void getCart_NotFound() {
        // Given
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.getCart(999L);
        });
    }

    // ==================== CLEAR CART TESTS ====================

    @Test
    @DisplayName("Should clear cart successfully")
    void clearCart_Success() {
        // Given
        testCart.getItems().add(testCartItem);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(testCart));
        doNothing().when(cartItemRepository).deleteAll(anyList());

        // When
        assertDoesNotThrow(() -> cartService.clearCart(1L));

        // Then
        verify(cartItemRepository, times(1)).deleteAll(anyList());
    }

    @Test
    @DisplayName("Should throw exception when clearing non-existent cart")
    void clearCart_NotFound() {
        // Given
        when(cartRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.clearCart(999L);
        });
    }
}
