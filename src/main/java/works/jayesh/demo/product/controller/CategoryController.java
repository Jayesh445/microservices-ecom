package works.jayesh.demo.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import works.jayesh.demo.common.model.ApiResponse;
import works.jayesh.demo.product.model.dto.CategoryRequest;
import works.jayesh.demo.product.model.dto.CategoryResponse;
import works.jayesh.demo.product.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Category created successfully", category));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long categoryId) {
        CategoryResponse category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryBySlug(@PathVariable String slug) {
        CategoryResponse category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success("Category retrieved successfully", category));
    }

    @GetMapping("/top-level")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getTopLevelCategories() {
        List<CategoryResponse> categories = categoryService.getTopLevelCategories();
        return ResponseEntity.ok(ApiResponse.success("Top level categories retrieved successfully", categories));
    }

    @GetMapping("/{parentCategoryId}/subcategories")
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getSubCategories(@PathVariable Long parentCategoryId) {
        List<CategoryResponse> categories = categoryService.getSubCategories(parentCategoryId);
        return ResponseEntity.ok(ApiResponse.success("Subcategories retrieved successfully", categories));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CategoryResponse>>> getAllCategories(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CategoryResponse> categories = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories));
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse category = categoryService.updateCategory(categoryId, request);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully", category));
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
    }
}
