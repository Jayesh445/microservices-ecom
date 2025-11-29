package works.jayesh.demo.payment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.order.model.entity.Order;
import works.jayesh.demo.order.model.entity.OrderStatus;
import works.jayesh.demo.order.repository.OrderRepository;
import works.jayesh.demo.payment.model.dto.PaymentRequest;
import works.jayesh.demo.payment.model.dto.PaymentResponse;
import works.jayesh.demo.payment.model.entity.Payment;
import works.jayesh.demo.payment.model.entity.PaymentMethod;
import works.jayesh.demo.payment.model.entity.PaymentStatus;
import works.jayesh.demo.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Unit Tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private Order testOrder;
    private Payment testPayment;
    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        testOrder = Order.builder()
                .id(1L)
                .status(OrderStatus.PENDING)
                .totalAmount(new BigDecimal("199.99"))
                .build();

        testPayment = Payment.builder()
                .id(1L)
                .order(testOrder)
                .paymentMethod(PaymentMethod.CREDIT_CARD)
                .status(PaymentStatus.PENDING)
                .amount(new BigDecimal("199.99"))
                .paymentGateway("stripe")
                .build();

        paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(1L);
        paymentRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        paymentRequest.setPaymentGateway("stripe");
    }

    // ==================== CREATE PAYMENT TESTS ====================

    @Test
    @DisplayName("Should create payment successfully")
    void createPayment_Success() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        // When
        PaymentResponse response = paymentService.createPayment(paymentRequest);

        // Then
        assertNotNull(response);
        assertEquals(PaymentStatus.PENDING, response.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw exception when order not found")
    void createPayment_OrderNotFound() {
        // Given
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.createPayment(paymentRequest);
        });

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw exception when payment already exists for order")
    void createPayment_PaymentAlreadyExists() {
        // Given
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(testPayment));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            paymentService.createPayment(paymentRequest);
        });

        verify(paymentRepository, times(1)).findByOrderId(1L);
    }

    // ==================== PROCESS PAYMENT TESTS ====================

    @Test
    @DisplayName("Should process payment successfully")
    void processPayment_Success() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        PaymentResponse response = paymentService.processPayment(1L);

        // Then
        assertNotNull(response);
        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @DisplayName("Should throw exception when payment not found")
    void processPayment_NotFound() {
        // Given
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.processPayment(999L);
        });
    }

    @Test
    @DisplayName("Should throw exception when payment is not in pending status")
    void processPayment_NotPending() {
        // Given
        testPayment.setStatus(PaymentStatus.COMPLETED);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            paymentService.processPayment(1L);
        });

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    // ==================== GET PAYMENT TESTS ====================

    @Test
    @DisplayName("Should get payment by ID successfully")
    void getPaymentById_Success() {
        // Given
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        // When
        PaymentResponse response = paymentService.getPaymentById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when payment not found by ID")
    void getPaymentById_NotFound() {
        // Given
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.getPaymentById(999L);
        });
    }

    @Test
    @DisplayName("Should get payment by order ID successfully")
    void getPaymentByOrderId_Success() {
        // Given
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(testPayment));

        // When
        PaymentResponse response = paymentService.getPaymentByOrderId(1L);

        // Then
        assertNotNull(response);
        verify(paymentRepository, times(1)).findByOrderId(1L);
    }

    @Test
    @DisplayName("Should throw exception when payment not found by order ID")
    void getPaymentByOrderId_NotFound() {
        // Given
        when(paymentRepository.findByOrderId(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.getPaymentByOrderId(999L);
        });
    }

    // ==================== REFUND PAYMENT TESTS ====================

    @Test
    @DisplayName("Should refund payment successfully")
    void refundPayment_Success() {
        // Given
        testPayment.setStatus(PaymentStatus.COMPLETED);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // When
        PaymentResponse response = paymentService.refundPayment(1L, "Customer request");

        // Then
        assertNotNull(response);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("Should throw exception when refunding non-existent payment")
    void refundPayment_NotFound() {
        // Given
        when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            paymentService.refundPayment(999L, "Customer request");
        });
    }
}
