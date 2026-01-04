package works.jayesh.demo.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.order.model.entity.Order;
import works.jayesh.demo.order.model.entity.OrderStatus;
import works.jayesh.demo.order.repository.OrderRepository;
import works.jayesh.demo.payment.model.dto.PaymentRequest;
import works.jayesh.demo.payment.model.dto.PaymentResponse;
import works.jayesh.demo.payment.model.entity.Payment;
import works.jayesh.demo.payment.model.entity.PaymentStatus;
import works.jayesh.demo.payment.repository.PaymentRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentResponse createPayment(PaymentRequest request) {
        log.info("Creating payment for order: {}", request.getOrderId());

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + request.getOrderId()));

        if (paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new IllegalStateException("Payment already exists for this order");
        }

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .amount(order.getTotalAmount())
                .paymentGateway(request.getPaymentGateway())
                .notes(request.getNotes())
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created successfully with ID: {}", savedPayment.getId());

        return mapToResponse(savedPayment);
    }

    public PaymentResponse processPayment(Long paymentId) {
        log.info("Processing payment: {}", paymentId);

        Payment payment = findPaymentById(paymentId);

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is not in pending status");
        }

        payment.setStatus(PaymentStatus.PROCESSING);

        // Here you would integrate with actual payment gateway
        // For now, simulating successful payment
        boolean paymentSuccess = processWithPaymentGateway(payment);

        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setPaidAt(LocalDateTime.now());

            // Update order status
            Order order = payment.getOrder();
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);

            log.info("Payment processed successfully");
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Payment gateway declined the transaction");
            log.error("Payment processing failed");
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return mapToResponse(updatedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = findPaymentById(paymentId);
        return mapToResponse(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));
        return mapToResponse(payment);
    }

    public PaymentResponse refundPayment(Long paymentId, String reason) {
        log.info("Refunding payment: {}", paymentId);

        Payment payment = findPaymentById(paymentId);

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Can only refund completed payments");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setNotes(payment.getNotes() + " | Refund reason: " + reason);

        // Update order status
        Order order = payment.getOrder();
        order.setStatus(OrderStatus.REFUNDED);
        orderRepository.save(order);

        Payment refundedPayment = paymentRepository.save(payment);
        log.info("Payment refunded successfully");

        return mapToResponse(refundedPayment);
    }

    private boolean processWithPaymentGateway(Payment payment) {
        // Simulate payment gateway processing
        // In real implementation, integrate with Stripe, PayPal, etc.
        return true;
    }

    private Payment findPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .transactionId(payment.getTransactionId())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentGateway(payment.getPaymentGateway())
                .gatewayTransactionId(payment.getGatewayTransactionId())
                .cardLast4Digits(payment.getCardLast4Digits())
                .cardBrand(payment.getCardBrand())
                .paidAt(payment.getPaidAt())
                .failureReason(payment.getFailureReason())
                .notes(payment.getNotes())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
