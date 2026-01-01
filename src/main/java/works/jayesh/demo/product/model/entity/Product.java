package works.jayesh.demo.product.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import works.jayesh.demo.review.model.entity.Review;
import works.jayesh.demo.user.model.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_product_category_id", columnList = "category_id"),
        @Index(name = "idx_product_seller_id", columnList = "seller_id"),
        @Index(name = "idx_product_slug", columnList = "slug"),
        @Index(name = "idx_product_sku", columnList = "sku"),
        @Index(name = "idx_product_active", columnList = "active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(length = 5000)
    private String description;

    @Column(length = 2000)
    private String shortDescription;

    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal discountPrice;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

    @Column(nullable = false)
    private String brand;

    private String manufacturer;

    private Double weight;

    @Embedded
    private ProductDimensions dimensions;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private boolean featured = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    private Double averageRating;

    private Integer totalReviews;

    private Integer totalSold;

    @ElementCollection
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = ProductStatus.DRAFT;
        }
        if (averageRating == null) {
            averageRating = 0.0;
        }
        if (totalReviews == null) {
            totalReviews = 0;
        }
        if (totalSold == null) {
            totalSold = 0;
        }
    }

    public boolean isInStock() {
        return stockQuantity > 0;
    }

    public BigDecimal getEffectivePrice() {
        return discountPrice != null ? discountPrice : price;
    }
}
