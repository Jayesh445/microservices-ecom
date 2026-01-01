package works.jayesh.demo.product.model.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDimensions {
    private Double length;
    private Double width;
    private Double height;
    private String unit; // cm, inch, etc.
}
