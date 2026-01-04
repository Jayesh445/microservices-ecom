package works.jayesh.demo.payment.controller;

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
import works.jayesh.demo.common.exception.GlobalExceptionHandler;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.payment.model.dto.PaymentRequest;
import works.jayesh.demo.payment.model.dto.PaymentResponse;
import works.jayesh.demo.payment.model.entity.PaymentMethod;
import works.jayesh.demo.payment.model.entity.PaymentStatus;
import works.jayesh.demo.payment.service.PaymentService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentController API Tests")
class PaymentControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentResponse paymentResponse;
    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();

        paymentResponse = PaymentResponse.builder()
                .id(1L)
                .orderId(1L)
                .amount(new BigDecimal("299.99"))
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .transactionId("TXN-123456")
                .build();

        paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(1L);
        paymentRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD);
    }

    @Test
    @DisplayName("Should create payment successfully")
    void createPayment_Success() throws Exception {
        when(paymentService.createPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(1))
                .andExpect(jsonPath("$.data.status").value("PENDING"));
    }

    @Test
    @DisplayName("Should return 400 when creating payment with missing orderId")
    void createPayment_MissingOrderId() throws Exception {
        paymentRequest.setOrderId(null);

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return 400 when creating payment with missing payment method")
    void createPayment_MissingPaymentMethod() throws Exception {
        paymentRequest.setPaymentMethod(null);

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should process payment successfully")
    void processPayment_Success() throws Exception {
        paymentResponse.setStatus(PaymentStatus.COMPLETED);
        when(paymentService.processPayment(1L)).thenReturn(paymentResponse);

        mockMvc.perform(post("/api/payments/1/process"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("Should return 404 when processing non-existent payment")
    void processPayment_NotFound() throws Exception {
        when(paymentService.processPayment(999L))
                .thenThrow(new ResourceNotFoundException("Payment not found with id: 999"));

        mockMvc.perform(post("/api/payments/999/process"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get payment by ID successfully")
    void getPaymentById_Success() throws Exception {
        when(paymentService.getPaymentById(1L)).thenReturn(paymentResponse);

        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.transactionId").value("TXN-123456"));
    }

    @Test
    @DisplayName("Should return 404 when payment not found")
    void getPaymentById_NotFound() throws Exception {
        when(paymentService.getPaymentById(999L))
                .thenThrow(new ResourceNotFoundException("Payment not found with id: 999"));

        mockMvc.perform(get("/api/payments/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get payment by order ID successfully")
    void getPaymentByOrderId_Success() throws Exception {
        when(paymentService.getPaymentByOrderId(1L)).thenReturn(paymentResponse);

        mockMvc.perform(get("/api/payments/order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(1));
    }

    @Test
    @DisplayName("Should return 404 when payment not found for order")
    void getPaymentByOrderId_NotFound() throws Exception {
        when(paymentService.getPaymentByOrderId(999L))
                .thenThrow(new ResourceNotFoundException("Payment not found for order: 999"));

        mockMvc.perform(get("/api/payments/order/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should refund payment successfully")
    void refundPayment_Success() throws Exception {
        paymentResponse.setStatus(PaymentStatus.REFUNDED);
        when(paymentService.refundPayment(1L, "Customer request")).thenReturn(paymentResponse);

        mockMvc.perform(post("/api/payments/1/refund")
                .param("reason", "Customer request"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("REFUNDED"));
    }

    @Test
    @DisplayName("Should return 404 when refunding non-existent payment")
    void refundPayment_NotFound() throws Exception {
        when(paymentService.refundPayment(999L, "Customer request"))
                .thenThrow(new ResourceNotFoundException("Payment not found with id: 999"));

        mockMvc.perform(post("/api/payments/999/refund")
                .param("reason", "Customer request"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle malformed JSON")
    void createPayment_MalformedJson() throws Exception {
        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle malformed payment ID")
    void getPaymentById_InvalidId() throws Exception {
        mockMvc.perform(get("/api/payments/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle malformed order ID")
    void getPaymentByOrderId_InvalidOrderId() throws Exception {
        mockMvc.perform(get("/api/payments/order/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
