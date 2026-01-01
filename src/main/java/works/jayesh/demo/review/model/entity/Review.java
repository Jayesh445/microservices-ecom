package works.jayesh.demo.review.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import works.jayesh.demo.product.model.entity.Product;
import works.jayesh.demo.user.model.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews", indexes = {
        @Index(name = "idx_review_product_id", columnList = "product_id"),
        @Index(name = "idx_review_user_id", columnList = "user_id"),
        @Index(name = "idx_review_approved", columnList = "approved"),
        @Index(name = "idx_review_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer rating; // 1-5

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String comment;

    private boolean verified; // Verified purchase

    private boolean approved = false;

    private Integer helpfulCount = 0;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (helpfulCount == null) {
            helpfulCount = 0;
        }
    }
}
