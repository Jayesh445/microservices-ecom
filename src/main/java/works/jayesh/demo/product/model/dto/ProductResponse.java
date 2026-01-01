package works.jayesh.demo.product.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import works.jayesh.demo.product.model.entity.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String sku;
    private String description;
    private String shortDescription;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private Integer stockQuantity;
    private String slug;
    private Long categoryId;
    private String categoryName;
    private Long sellerId;
    private String sellerName;
    private List<String> images;
    private String brand;
    private String manufacturer;
    private Double weight;
    private boolean active;
    private boolean featured;
    private ProductStatus status;
    private Double averageRating;
    private Integer totalReviews;
    private Integer totalSold;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
