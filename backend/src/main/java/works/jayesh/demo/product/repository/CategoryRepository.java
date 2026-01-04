package works.jayesh.demo.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import works.jayesh.demo.product.model.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    Optional<Category> findByName(String name);

    List<Category> findByParentCategoryIsNull();

    List<Category> findByParentCategoryId(Long parentCategoryId);

    Page<Category> findByActiveTrue(Pageable pageable);

    @Query("SELECT c FROM Category c WHERE c.parentCategory IS NULL AND c.active = true " +
            "ORDER BY c.displayOrder ASC")
    List<Category> findTopLevelCategories();

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    long countByParentCategoryId(Long parentCategoryId);
}
