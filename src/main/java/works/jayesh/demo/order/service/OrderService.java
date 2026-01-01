package works.jayesh.demo.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import works.jayesh.demo.address.model.entity.Address;
import works.jayesh.demo.address.repository.AddressRepository;
import works.jayesh.demo.common.exception.InsufficientStockException;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.order.model.dto.*;
import works.jayesh.demo.order.model.entity.Order;
import works.jayesh.demo.order.model.entity.OrderItem;
import works.jayesh.demo.order.model.entity.OrderStatus;
import works.jayesh.demo.order.repository.OrderRepository;
import works.jayesh.demo.product.model.entity.Product;
import works.jayesh.demo.product.repository.ProductRepository;
import works.jayesh.demo.user.model.entity.User;
import works.jayesh.demo.user.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10"); // 10% tax
    private static final BigDecimal SHIPPING_COST = new BigDecimal("10.00");

    public OrderResponse createOrder(OrderCreateRequest request) {
        log.info("Creating new order for user: {}", request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getUserId()));

        Address shippingAddress = addressRepository.findById(request.getShippingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Shipping address not found: " + request.getShippingAddressId()));

        Address billingAddress = addressRepository.findById(request.getBillingAddressId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Billing address not found: " + request.getBillingAddressId()));

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .shippingAddress(shippingAddress)
                .billingAddress(billingAddress)
                .notes(request.getNotes())
                .orderItems(new ArrayList<>())
                .shippingCost(SHIPPING_COST)
                .discount(BigDecimal.ZERO)
                .build();

        // Create order items
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Product not found: " + itemRequest.getProductId()));

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
            }

            BigDecimal unitPrice = product.getEffectivePrice();
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .productSku(product.getSku())
                    .productImage(product.getImages().isEmpty() ? null : product.getImages().get(0))
                    .unitPrice(unitPrice)
                    .quantity(itemRequest.getQuantity())
                    .totalPrice(unitPrice.multiply(BigDecimal.valueOf(itemRequest.getQuantity())))
                    .build();

            order.getOrderItems().add(orderItem);

            // Update product stock
            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            product.setTotalSold(product.getTotalSold() + itemRequest.getQuantity());
        }

        // Calculate subtotal first
        order.setSubtotal(order.getOrderItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // Set tax based on subtotal
        order.setTax(order.getSubtotal().multiply(TAX_RATE));

        // Now calculate final total
        order.setTotalAmount(order.getSubtotal().add(order.getTax()).add(order.getShippingCost()));

        // Save order (order number will be generated automatically by @PrePersist)
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with order number: {}", savedOrder.getOrderNumber());

        return mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = findOrderById(orderId);
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with number: " + orderNumber));
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable).map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::mapToResponse);
    }

    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Updating order {} status to {}", orderId, newStatus);

        Order order = findOrderById(orderId);
        order.setStatus(newStatus);

        switch (newStatus) {
            case SHIPPED:
                order.setShippedAt(LocalDateTime.now());
                break;
            case DELIVERED:
                order.setDeliveredAt(LocalDateTime.now());
                break;
            case CANCELLED:
                order.setCancelledAt(LocalDateTime.now());
                restoreStock(order);
                break;
            default:
                break;
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated successfully");

        return mapToResponse(updatedOrder);
    }

    public OrderResponse updateTrackingInfo(Long orderId, String trackingNumber, String shippingCarrier) {
        log.info("Updating tracking info for order: {}", orderId);

        Order order = findOrderById(orderId);
        order.setTrackingNumber(trackingNumber);
        order.setShippingCarrier(shippingCarrier);

        Order updatedOrder = orderRepository.save(order);
        log.info("Tracking info updated successfully");

        return mapToResponse(updatedOrder);
    }

    public OrderResponse cancelOrder(Long orderId, String reason) {
        log.info("Cancelling order: {}", orderId);

        Order order = findOrderById(orderId);

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel order in current status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancellationReason(reason);

        restoreStock(order);

        Order cancelledOrder = orderRepository.save(order);
        log.info("Order cancelled successfully");

        return mapToResponse(cancelledOrder);
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            product.setTotalSold(product.getTotalSold() - item.getQuantity());
            productRepository.save(product);
        }
    }

    private Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .userId(order.getUser().getId())
                .userName(order.getUser().getFirstName() + " " + order.getUser().getLastName())
                .items(order.getOrderItems().stream().map(this::mapItemToResponse).toList())
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .tax(order.getTax())
                .shippingCost(order.getShippingCost())
                .discount(order.getDiscount())
                .totalAmount(order.getTotalAmount())
                .shippingAddressId(order.getShippingAddress().getId())
                .billingAddressId(order.getBillingAddress().getId())
                .trackingNumber(order.getTrackingNumber())
                .shippingCarrier(order.getShippingCarrier())
                .shippedAt(order.getShippedAt())
                .deliveredAt(order.getDeliveredAt())
                .cancelledAt(order.getCancelledAt())
                .cancellationReason(order.getCancellationReason())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    private OrderItemResponse mapItemToResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProductName())
                .productSku(item.getProductSku())
                .productImage(item.getProductImage())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}
