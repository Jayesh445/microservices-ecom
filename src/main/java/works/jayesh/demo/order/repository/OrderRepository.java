package works.jayesh.demo.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import works.jayesh.demo.order.model.entity.Order;
import works.jayesh.demo.order.model.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Page<Order> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findUserOrdersByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    long countByStatus(OrderStatus status);

    long countByUserId(Long userId);

    boolean existsByOrderNumber(String orderNumber);
}
