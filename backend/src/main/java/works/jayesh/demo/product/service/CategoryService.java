package works.jayesh.demo.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import works.jayesh.demo.common.exception.DuplicateResourceException;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.product.model.dto.CategoryRequest;
import works.jayesh.demo.product.model.dto.CategoryResponse;
import works.jayesh.demo.product.model.entity.Category;
import works.jayesh.demo.product.repository.CategoryRepository;
import works.jayesh.demo.product.repository.ProductRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Creating new category: {}", request.getName());

        if (categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category already exists: " + request.getName());
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .slug(generateSlug(request.getName()))
                .imageUrl(request.getImageUrl())
                .active(request.isActive())
                .displayOrder(request.getDisplayOrder())
                .build();

        if (request.getParentCategoryId() != null) {
            Category parentCategory = findCategoryById(request.getParentCategoryId());
            category.setParentCategory(parentCategory);
        }

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created successfully with ID: {}", savedCategory.getId());

        return mapToResponse(savedCategory);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long categoryId) {
        Category category = findCategoryById(categoryId);
        return mapToResponse(category);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
        return mapToResponse(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getTopLevelCategories() {
        return categoryRepository.findTopLevelCategories()
                .stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getSubCategories(Long parentCategoryId) {
        return categoryRepository.findByParentCategoryId(parentCategoryId)
                .stream().map(this::mapToResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(this::mapToResponse);
    }

    public CategoryResponse updateCategory(Long categoryId, CategoryRequest request) {
        log.info("Updating category with ID: {}", categoryId);

        Category category = findCategoryById(categoryId);

        if (!category.getName().equals(request.getName()) &&
                categoryRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Category name already exists: " + request.getName());
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setActive(request.isActive());
        category.setDisplayOrder(request.getDisplayOrder());

        if (request.getParentCategoryId() != null) {
            Category parentCategory = findCategoryById(request.getParentCategoryId());
            category.setParentCategory(parentCategory);
        }

        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully with ID: {}", categoryId);

        return mapToResponse(updatedCategory);
    }

    public void deleteCategory(Long categoryId) {
        log.info("Deleting category with ID: {}", categoryId);
        Category category = findCategoryById(categoryId);

        long productCount = productRepository.countByCategoryId(categoryId);
        if (productCount > 0) {
            throw new IllegalStateException("Cannot delete category with existing products");
        }

        categoryRepository.delete(category);
        log.info("Category deleted successfully with ID: {}", categoryId);
    }

    private Category findCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + categoryId));
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }

    private CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .slug(category.getSlug())
                .imageUrl(category.getImageUrl())
                .parentCategoryId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .parentCategoryName(
                        category.getParentCategory() != null ? category.getParentCategory().getName() : null)
                .active(category.isActive())
                .displayOrder(category.getDisplayOrder())
                .productCount(category.getProducts().size())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
