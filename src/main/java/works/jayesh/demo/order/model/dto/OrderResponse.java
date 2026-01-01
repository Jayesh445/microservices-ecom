package works.jayesh.demo.order.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import works.jayesh.demo.order.model.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private Long userId;
    private String userName;
    private List<OrderItemResponse> items;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal shippingCost;
    private BigDecimal discount;
    private BigDecimal totalAmount;
    private Long shippingAddressId;
    private Long billingAddressId;
    private String trackingNumber;
    private String shippingCarrier;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
