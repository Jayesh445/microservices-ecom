package works.jayesh.demo.product.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private String slug;
    private String imageUrl;
    private Long parentCategoryId;
    private String parentCategoryName;
    private boolean active;
    private Integer displayOrder;
    private Integer productCount;
    private LocalDateTime createdAt;
}
