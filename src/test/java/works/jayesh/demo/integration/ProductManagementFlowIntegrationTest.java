package works.jayesh.demo.integration;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import works.jayesh.demo.product.model.entity.Category;
import works.jayesh.demo.product.model.entity.Product;
import works.jayesh.demo.product.model.entity.ProductStatus;
import works.jayesh.demo.product.repository.CategoryRepository;
import works.jayesh.demo.product.repository.ProductRepository;
import works.jayesh.demo.review.model.entity.Review;
import works.jayesh.demo.review.repository.ReviewRepository;
import works.jayesh.demo.user.model.entity.User;
import works.jayesh.demo.user.model.entity.UserRole;
import works.jayesh.demo.user.model.entity.UserStatus;
import works.jayesh.demo.user.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Product Management Flow Integration Tests
 * Tests product lifecycle: Create → List → Update → Review → Search
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Product Management Flow Integration Tests")
class ProductManagementFlowIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testSeller;
    private User testCustomer;
    private Category testCategory;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Create test seller
        testSeller = User.builder()
                .firstName("Jane")
                .lastName("Seller")
                .email("seller" + System.currentTimeMillis() + "@test.com")
                .password(passwordEncoder.encode("password123"))
                .phoneNumber("9876543210")
                .role(UserRole.SELLER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .phoneVerified(false)
                .twoFactorEnabled(false)
                .build();
        testSeller = userRepository.save(testSeller);

        // Create test customer
        testCustomer = User.builder()
                .firstName("John")
                .lastName("Customer")
                .email("customer" + System.currentTimeMillis() + "@test.com")
                .password(passwordEncoder.encode("password123"))
                .phoneNumber("1234567890")
                .role(UserRole.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .phoneVerified(false)
                .twoFactorEnabled(false)
                .build();
        testCustomer = userRepository.save(testCustomer);

        // Create test category
        testCategory = Category.builder()
                .name("Electronics " + System.currentTimeMillis())
                .slug("electronics-" + System.currentTimeMillis())
                .description("Electronic products")
                .active(true)
                .displayOrder(1)
                .build();
        testCategory = categoryRepository.save(testCategory);

        // Create test product
        testProduct = Product.builder()
                .name("Test Product")
                .sku("SKU-" + System.currentTimeMillis())
                .description("Test product description")
                .price(new BigDecimal("99.99"))
                .stockQuantity(100)
                .slug("test-product-" + System.currentTimeMillis())
                .category(testCategory)
                .seller(testSeller)
                .brand("TestBrand")
                .active(true)
                .featured(false)
                .status(ProductStatus.ACTIVE)
                .totalReviews(0)
                .totalSold(0)
                .build();
        testProduct = productRepository.save(testProduct);
    }

    @Test
    @Order(1)
    @DisplayName("Flow 1: Category can be created and retrieved")
    void testCategoryCreationFlow() {
        // Create new category
        Category newCategory = Category.builder()
                .name("Books " + System.currentTimeMillis())
                .slug("books-" + System.currentTimeMillis())
                .description("Book collection")
                .active(true)
                .displayOrder(2)
                .build();
        newCategory = categoryRepository.save(newCategory);

        // Verify category was saved
        assertThat(newCategory.getId()).isNotNull();
        assertThat(newCategory.getName()).startsWith("Books");

        // Retrieve category
        Category found = categoryRepository.findById(newCategory.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getSlug()).startsWith("books-");
    }

    @Test
    @Order(2)
    @DisplayName("Flow 2: Product can be created by seller")
    void testProductCreationFlow() {
        // Create product
        Product newProduct = Product.builder()
                .name("Wireless Mouse")
                .sku("MOUSE-" + System.currentTimeMillis())
                .description("Ergonomic wireless mouse")
                .shortDescription("Best mouse 2026")
                .price(new BigDecimal("29.99"))
                .stockQuantity(200)
                .slug("wireless-mouse-" + System.currentTimeMillis())
                .category(testCategory)
                .seller(testSeller)
                .brand("TechBrand")
                .active(true)
                .featured(false)
                .status(ProductStatus.ACTIVE)
                .totalReviews(0)
                .totalSold(0)
                .build();
        newProduct = productRepository.save(newProduct);

        // Verify product was saved
        assertThat(newProduct.getId()).isNotNull();
        assertThat(newProduct.getName()).isEqualTo("Wireless Mouse");
        assertThat(newProduct.getSeller().getId()).isEqualTo(testSeller.getId());
        assertThat(newProduct.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    @Order(3)
    @DisplayName("Flow 3: Product details can be updated")
    void testProductUpdateFlow() {
        // Update product
        testProduct.setName("Updated Product Name");
        testProduct.setPrice(new BigDecimal("149.99"));
        testProduct.setStockQuantity(50);
        testProduct = productRepository.save(testProduct);

        // Verify updates
        Product updated = productRepository.findById(testProduct.getId()).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Updated Product Name");
        assertThat(updated.getPrice()).isEqualByComparingTo(new BigDecimal("149.99"));
        assertThat(updated.getStockQuantity()).isEqualTo(50);
    }

    @Test
    @Order(4)
    @DisplayName("Flow 4: Product can be featured/unfeatured")
    void testProductFeaturedFlow() {
        // Feature product
        testProduct.setFeatured(true);
        productRepository.save(testProduct);

        // Verify product is featured
        Product featured = productRepository.findById(testProduct.getId()).orElse(null);
        assertThat(featured).isNotNull();
        assertThat(featured.isFeatured()).isTrue();

        // Unfeature product
        testProduct.setFeatured(false);
        productRepository.save(testProduct);

        Product unfeatured = productRepository.findById(testProduct.getId()).orElse(null);
        assertThat(unfeatured.isFeatured()).isFalse();
    }

    @Test
    @Order(5)
    @DisplayName("Flow 5: Product can be activated/deactivated")
    void testProductActivationFlow() {
        // Deactivate product
        testProduct.setActive(false);
        testProduct.setStatus(ProductStatus.ARCHIVED);
        productRepository.save(testProduct);

        Product inactive = productRepository.findById(testProduct.getId()).orElse(null);
        assertThat(inactive).isNotNull();
        assertThat(inactive.isActive()).isFalse();
        assertThat(inactive.getStatus()).isEqualTo(ProductStatus.ARCHIVED);

        // Reactivate product
        testProduct.setActive(true);
        testProduct.setStatus(ProductStatus.ACTIVE);
        productRepository.save(testProduct);

        Product active = productRepository.findById(testProduct.getId()).orElse(null);
        assertThat(active.isActive()).isTrue();
        assertThat(active.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    @Order(6)
    @DisplayName("Flow 6: Review can be added to product")
    void testAddReviewFlow() {
        // Add review
        Review review = Review.builder()
                .product(testProduct)
                .user(testCustomer)
                .rating(5)
                .title("Great product!")
                .comment("Very satisfied with this purchase")
                .approved(true)
                .verified(true)
                .build();
        review = reviewRepository.save(review);

        // Verify review was saved
        assertThat(review.getId()).isNotNull();
        assertThat(review.getRating()).isEqualTo(5);
        assertThat(review.getProduct().getId()).isEqualTo(testProduct.getId());
        assertThat(review.getUser().getId()).isEqualTo(testCustomer.getId());
    }

    @Test
    @Order(7)
    @DisplayName("Flow 7: Product rating updates with reviews")
    void testProductRatingUpdateFlow() {
        // Add multiple reviews
        Review review1 = Review.builder()
                .product(testProduct)
                .user(testCustomer)
                .rating(5)
                .title("Excellent")
                .comment("Perfect")
                .approved(true)
                .verified(true)
                .build();
        reviewRepository.save(review1);

        // Create another customer
        User customer2 = User.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice" + System.currentTimeMillis() + "@test.com")
                .password(passwordEncoder.encode("password123"))
                .phoneNumber("5551234567")
                .role(UserRole.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .phoneVerified(false)
                .twoFactorEnabled(false)
                .build();
        customer2 = userRepository.save(customer2);

        Review review2 = Review.builder()
                .product(testProduct)
                .user(customer2)
                .rating(4)
                .title("Very good")
                .comment("Recommended")
                .approved(true)
                .verified(true)
                .build();
        reviewRepository.save(review2);

        // Calculate average rating
        Pageable pageable = PageRequest.of(0, 100);
        Page<Review> reviewsPage = reviewRepository.findByProductId(testProduct.getId(), pageable);
        List<Review> reviews = reviewsPage.getContent();
        double avgRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        // Update product with calculated rating
        testProduct.setAverageRating(avgRating);
        testProduct.setTotalReviews(reviews.size());
        productRepository.save(testProduct);

        // Verify product rating
        Product updated = productRepository.findById(testProduct.getId()).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getAverageRating()).isEqualTo(4.50);
        assertThat(updated.getTotalReviews()).isEqualTo(2);
    }

    @Test
    @Order(8)
    @DisplayName("Flow 8: Products can be searched by category")
    void testSearchByCategoryFlow() {
        // Create multiple products in category
        Product product2 = Product.builder()
                .name("Keyboard")
                .sku("KB-" + System.currentTimeMillis())
                .description("Mechanical keyboard")
                .price(new BigDecimal("79.99"))
                .stockQuantity(150)
                .slug("keyboard-" + System.currentTimeMillis())
                .category(testCategory)
                .seller(testSeller)
                .brand("TechBrand")
                .active(true)
                .featured(false)
                .status(ProductStatus.ACTIVE)
                .build();
        productRepository.save(product2);

        // Search products by category
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> categoryProducts = productRepository.findByCategoryId(testCategory.getId(), pageable);

        assertThat(categoryProducts.getContent()).hasSizeGreaterThanOrEqualTo(2);
        assertThat(categoryProducts.getContent()).allMatch(p -> p.getCategory().getId().equals(testCategory.getId()));
    }

    @Test
    @Order(9)
    @DisplayName("Flow 9: Products can be searched by seller")
    void testSearchBySellerFlow() {
        // Get all products by seller
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> sellerProducts = productRepository.findBySellerId(testSeller.getId(), pageable);

        assertThat(sellerProducts.getContent()).isNotEmpty();
        assertThat(sellerProducts.getContent()).allMatch(p -> p.getSeller().getId().equals(testSeller.getId()));
    }

    @Test
    @Order(10)
    @DisplayName("Flow 10: Product can be found by slug")
    void testFindBySlugFlow() {
        // Find product by slug
        Product found = productRepository.findBySlug(testProduct.getSlug()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(testProduct.getId());
        assertThat(found.getName()).isEqualTo(testProduct.getName());
    }

    @Test
    @Order(11)
    @DisplayName("Flow 11: Product can be found by SKU")
    void testFindBySkuFlow() {
        // Find product by SKU
        Product found = productRepository.findBySku(testProduct.getSku()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(testProduct.getId());
        assertThat(found.getSku()).isEqualTo(testProduct.getSku());
    }

    @Test
    @Order(12)
    @DisplayName("Flow 12: Product review lifecycle")
    void testReviewLifecycleFlow() {
        // Step 1: Customer writes review
        Review review = Review.builder()
                .product(testProduct)
                .user(testCustomer)
                .rating(4)
                .title("Good product")
                .comment("Works as expected")
                .approved(false) // Pending approval
                .verified(false)
                .build();
        review = reviewRepository.save(review);
        assertThat(review.isApproved()).isFalse();

        // Step 2: Admin approves review
        review.setApproved(true);
        review = reviewRepository.save(review);

        // Step 3: Verify review is approved
        Review approved = reviewRepository.findById(review.getId()).orElse(null);
        assertThat(approved).isNotNull();
        assertThat(approved.isApproved()).isTrue();

        // Step 4: Get all approved reviews for product
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> productReviewsPage = reviewRepository.findByProductId(testProduct.getId(), pageable);
        List<Review> productReviews = productReviewsPage.getContent();
        long approvedCount = productReviews.stream().filter(Review::isApproved).count();
        assertThat(approvedCount).isGreaterThan(0);
    }

    @Test
    @Order(13)
    @DisplayName("Flow 13: Featured products can be retrieved")
    void testFeaturedProductsFlow() {
        // Feature the test product
        testProduct.setFeatured(true);
        productRepository.save(testProduct);

        // Create and feature another product
        Product featured2 = Product.builder()
                .name("Featured Mouse")
                .sku("FEAT-" + System.currentTimeMillis())
                .description("Featured product")
                .price(new BigDecimal("49.99"))
                .stockQuantity(75)
                .slug("featured-" + System.currentTimeMillis())
                .category(testCategory)
                .seller(testSeller)
                .brand("TechBrand")
                .active(true)
                .featured(true)
                .status(ProductStatus.ACTIVE)
                .build();
        productRepository.save(featured2);

        // Get all featured products
        List<Product> allProducts = productRepository.findAll();
        List<Product> featuredProducts = allProducts.stream()
                .filter(Product::isFeatured)
                .toList();

        assertThat(featuredProducts).hasSizeGreaterThanOrEqualTo(2);
        assertThat(featuredProducts).allMatch(Product::isFeatured);
    }

    @Test
    @Order(14)
    @DisplayName("Flow 14: Product stock management")
    void testStockManagementFlow() {
        int originalStock = testProduct.getStockQuantity();

        // Reduce stock after sale
        int soldQuantity = 10;
        testProduct.setStockQuantity(originalStock - soldQuantity);
        testProduct.setTotalSold(testProduct.getTotalSold() + soldQuantity);
        productRepository.save(testProduct);

        // Verify stock and sales
        Product updated = productRepository.findById(testProduct.getId()).orElse(null);
        assertThat(updated).isNotNull();
        assertThat(updated.getStockQuantity()).isEqualTo(originalStock - soldQuantity);
        assertThat(updated.getTotalSold()).isEqualTo(soldQuantity);

        // Restock
        int restockQuantity = 50;
        testProduct.setStockQuantity(testProduct.getStockQuantity() + restockQuantity);
        productRepository.save(testProduct);

        Product restocked = productRepository.findById(testProduct.getId()).orElse(null);
        assertThat(restocked.getStockQuantity()).isEqualTo(originalStock - soldQuantity + restockQuantity);
    }
}
