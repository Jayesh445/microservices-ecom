package works.jayesh.demo.order.controller;

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
import works.jayesh.demo.order.model.dto.OrderCreateRequest;
import works.jayesh.demo.order.model.dto.OrderItemRequest;
import works.jayesh.demo.order.model.dto.OrderResponse;
import works.jayesh.demo.order.model.entity.OrderStatus;
import works.jayesh.demo.order.service.OrderService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderController API Tests")
class OrderControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private OrderResponse orderResponse;
    private OrderCreateRequest orderCreateRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();

        orderResponse = OrderResponse.builder()
                .id(1L)
                .orderNumber("ORD-123456")
                .userId(1L)
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("299.99"))
                .build();

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        orderCreateRequest = new OrderCreateRequest();
        orderCreateRequest.setUserId(1L);
        orderCreateRequest.setShippingAddressId(1L);
        orderCreateRequest.setBillingAddressId(1L);
        orderCreateRequest.setItems(Arrays.asList(itemRequest));
    }

    @Test
    @DisplayName("Should create order successfully")
    void createOrder_Success() throws Exception {
        when(orderService.createOrder(any(OrderCreateRequest.class))).thenReturn(orderResponse);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD-123456"))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    @DisplayName("Should return 400 when creating order with missing userId")
    void createOrder_MissingUserId() throws Exception {
        orderCreateRequest.setUserId(null);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return 400 when creating order with empty items")
    void createOrder_EmptyItems() throws Exception {
        orderCreateRequest.setItems(Arrays.asList());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get order by ID successfully")
    void getOrderById_Success() throws Exception {
        when(orderService.getOrderById(1L)).thenReturn(orderResponse);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD-123456"));
    }

    @Test
    @DisplayName("Should return 404 when order not found")
    void getOrderById_NotFound() throws Exception {
        when(orderService.getOrderById(999L))
                .thenThrow(new ResourceNotFoundException("Order not found with id: 999"));

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get order by order number successfully")
    void getOrderByOrderNumber_Success() throws Exception {
        when(orderService.getOrderByOrderNumber("ORD-123456")).thenReturn(orderResponse);

        mockMvc.perform(get("/api/orders/number/ORD-123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value("ORD-123456"));
    }

    @Test
    @DisplayName("Should return 404 when order number not found")
    void getOrderByOrderNumber_NotFound() throws Exception {
        when(orderService.getOrderByOrderNumber("INVALID"))
                .thenThrow(new ResourceNotFoundException("Order not found with number: INVALID"));

        mockMvc.perform(get("/api/orders/number/INVALID"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get user orders with pagination")
    void getUserOrders_Success() throws Exception {
        List<OrderResponse> list = Arrays.asList(orderResponse);
        Page<OrderResponse> page = new PageImpl<>(list, PageRequest.of(0, 20), 1);

        when(orderService.getUserOrders(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/api/orders/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].userId").value(1));
    }

    @Test
    @DisplayName("Should get orders by status with pagination")
    void getOrdersByStatus_Success() throws Exception {
        List<OrderResponse> list = Arrays.asList(orderResponse);
        Page<OrderResponse> page = new PageImpl<>(list, PageRequest.of(0, 20), 1);

        when(orderService.getOrdersByStatus(eq(OrderStatus.PENDING), any())).thenReturn(page);

        mockMvc.perform(get("/api/orders/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("Should get all orders with pagination")
    void getAllOrders_Success() throws Exception {
        List<OrderResponse> list = Arrays.asList(orderResponse);
        Page<OrderResponse> page = new PageImpl<>(list, PageRequest.of(0, 20), 1);

        when(orderService.getAllOrders(any())).thenReturn(page);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @DisplayName("Should update order status successfully")
    void updateOrderStatus_Success() throws Exception {
        orderResponse.setStatus(OrderStatus.CONFIRMED);
        when(orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED)).thenReturn(orderResponse);

        mockMvc.perform(patch("/api/orders/1/status")
                .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent order status")
    void updateOrderStatus_NotFound() throws Exception {
        when(orderService.updateOrderStatus(999L, OrderStatus.CONFIRMED))
                .thenThrow(new ResourceNotFoundException("Order not found with id: 999"));

        mockMvc.perform(patch("/api/orders/999/status")
                .param("status", "CONFIRMED"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle malformed JSON")
    void createOrder_MalformedJson() throws Exception {
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle malformed order ID")
    void getOrderById_InvalidId() throws Exception {
        mockMvc.perform(get("/api/orders/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
