package works.jayesh.demo.order.service;

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
import works.jayesh.demo.address.model.entity.Address;
import works.jayesh.demo.address.repository.AddressRepository;
import works.jayesh.demo.common.exception.InsufficientStockException;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.order.model.dto.OrderCreateRequest;
import works.jayesh.demo.order.model.dto.OrderItemRequest;
import works.jayesh.demo.order.model.dto.OrderResponse;
import works.jayesh.demo.order.model.entity.Order;
import works.jayesh.demo.order.model.entity.OrderStatus;
import works.jayesh.demo.order.repository.OrderRepository;
import works.jayesh.demo.product.model.entity.Product;
import works.jayesh.demo.product.repository.ProductRepository;
import works.jayesh.demo.user.model.entity.User;
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
@DisplayName("OrderService Unit Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Product testProduct;
    private Address testAddress;
    private Order testOrder;
    private OrderCreateRequest createRequest;

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
                .totalSold(0)
                .images(new ArrayList<>())
                .tags(new ArrayList<>())
                .build();

        testAddress = Address.builder()
                .id(1L)
                .user(testUser)
                .fullName("John Doe")
                .addressLine1("123 Main St")
                .city("New York")
                .country("USA")
                .postalCode("10001")
                .build();

        testOrder = Order.builder()
                .id(1L)
                .user(testUser)
                .status(OrderStatus.PENDING)
                .shippingAddress(testAddress)
                .billingAddress(testAddress)
                .orderItems(new ArrayList<>())
                .build();

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        createRequest = new OrderCreateRequest();
        createRequest.setUserId(1L);
        createRequest.setShippingAddressId(1L);
        createRequest.setBillingAddressId(1L);
        createRequest.setItems(Arrays.asList(itemRequest));
    }

    // ==================== CREATE ORDER TESTS ====================

    @Test
    @DisplayName("Should create order successfully")
    void createOrder_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        OrderResponse response = orderService.createOrder(createRequest);

        // Then
        assertNotNull(response);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productRepository, times(1)).findById(1L);
        // Note: productRepository.save is called to update stock, but we verify the
        // behavior not the implementation
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void createOrder_UserNotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.createOrder(createRequest);
        });

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when shipping address not found")
    void createOrder_ShippingAddressNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.createOrder(createRequest);
        });

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void createOrder_ProductNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.createOrder(createRequest);
        });

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when insufficient stock")
    void createOrder_InsufficientStock() {
        // Given
        testProduct.setStockQuantity(1); // Not enough for 2 items

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When & Then
        assertThrows(InsufficientStockException.class, () -> {
            orderService.createOrder(createRequest);
        });

        verify(orderRepository, never()).save(any(Order.class));
    }

    // ==================== GET ORDER TESTS ====================

    @Test
    @DisplayName("Should get order by ID successfully")
    void getOrderById_Success() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // When
        OrderResponse response = orderService.getOrderById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when order not found")
    void getOrderById_NotFound() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.getOrderById(999L);
        });
    }

    @Test
    @DisplayName("Should get user orders with pagination")
    void getUserOrders_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Order> orders = Arrays.asList(testOrder);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, 1);

        when(orderRepository.findByUserId(1L, pageable)).thenReturn(orderPage);

        // When
        Page<OrderResponse> response = orderService.getUserOrders(1L, pageable);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(orderRepository, times(1)).findByUserId(1L, pageable);
    }

    // ==================== UPDATE ORDER STATUS TESTS ====================

    @Test
    @DisplayName("Should update order status successfully")
    void updateOrderStatus_Success() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        OrderResponse response = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        // Then
        assertNotNull(response);
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent order status")
    void updateOrderStatus_NotFound() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.updateOrderStatus(999L, OrderStatus.CONFIRMED);
        });
    }

    // ==================== CANCEL ORDER TESTS ====================

    @Test
    @DisplayName("Should cancel order successfully")
    void cancelOrder_Success() {
        // Given
        testOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        assertDoesNotThrow(() -> orderService.cancelOrder(1L, "Customer request"));

        // Then
        verify(orderRepository, times(1)).save(testOrder);
    }

    @Test
    @DisplayName("Should throw exception when order not found for cancellation")
    void cancelOrder_NotFound() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            orderService.cancelOrder(999L, "Customer request");
        });
    }
}
