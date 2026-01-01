package works.jayesh.demo.payment.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import works.jayesh.demo.payment.model.entity.PaymentMethod;
import works.jayesh.demo.payment.model.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String transactionId;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private BigDecimal amount;
    private String currency;
    private String paymentGateway;
    private String gatewayTransactionId;
    private String cardLast4Digits;
    private String cardBrand;
    private LocalDateTime paidAt;
    private String failureReason;
    private String notes;
    private LocalDateTime createdAt;
}
