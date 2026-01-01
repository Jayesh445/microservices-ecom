package works.jayesh.demo.payment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import works.jayesh.demo.payment.model.entity.Payment;
import works.jayesh.demo.payment.model.entity.PaymentStatus;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByOrderId(Long orderId);

    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    boolean existsByTransactionId(String transactionId);
}
