package works.jayesh.demo.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import works.jayesh.demo.common.exception.DuplicateResourceException;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.order.model.entity.OrderItem;
import works.jayesh.demo.order.repository.OrderItemRepository;
import works.jayesh.demo.product.model.entity.Product;
import works.jayesh.demo.product.repository.ProductRepository;
import works.jayesh.demo.review.model.dto.ReviewRequest;
import works.jayesh.demo.review.model.dto.ReviewResponse;
import works.jayesh.demo.review.model.entity.Review;
import works.jayesh.demo.review.repository.ReviewRepository;
import works.jayesh.demo.user.model.entity.User;
import works.jayesh.demo.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService Unit Tests")
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User testUser;
    private Product testProduct;
    private Review testReview;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .email("user@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .sku("TEST-001")
                .build();

        testReview = Review.builder()
                .id(1L)
                .product(testProduct)
                .user(testUser)
                .rating(5)
                .title("Great product!")
                .comment("I love this product")
                .verified(true)
                .approved(false)
                .build();

        reviewRequest = new ReviewRequest();
        reviewRequest.setProductId(1L);
        reviewRequest.setRating(5);
        reviewRequest.setTitle("Great product!");
        reviewRequest.setComment("I love this product");
    }

    // ==================== CREATE REVIEW TESTS ====================

    @Test
    @DisplayName("Should create review successfully")
    void createReview_Success() {
        // Given
        when(reviewRepository.existsByProductIdAndUserId(1L, 1L)).thenReturn(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(orderItemRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(Arrays.asList(new OrderItem()));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        ReviewResponse response = reviewService.createReview(1L, reviewRequest);

        // Then
        assertNotNull(response);
        assertEquals(5, response.getRating());
        assertEquals("Great product!", response.getTitle());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("Should throw exception when user already reviewed product")
    void createReview_AlreadyReviewed() {
        // Given
        when(reviewRepository.existsByProductIdAndUserId(1L, 1L)).thenReturn(true);

        // When & Then
        assertThrows(DuplicateResourceException.class, () -> {
            reviewService.createReview(1L, reviewRequest);
        });

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Should throw exception when product not found")
    void createReview_ProductNotFound() {
        // Given
        when(reviewRepository.existsByProductIdAndUserId(1L, 1L)).thenReturn(false);
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.createReview(1L, reviewRequest);
        });

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void createReview_UserNotFound() {
        // Given
        when(reviewRepository.existsByProductIdAndUserId(1L, 1L)).thenReturn(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.createReview(1L, reviewRequest);
        });

        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    @DisplayName("Should create unverified review when user has not purchased")
    void createReview_UnverifiedPurchase() {
        // Given
        when(reviewRepository.existsByProductIdAndUserId(1L, 1L)).thenReturn(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(orderItemRepository.findByUserIdAndProductId(1L, 1L)).thenReturn(new ArrayList<>());
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        ReviewResponse response = reviewService.createReview(1L, reviewRequest);

        // Then
        assertNotNull(response);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    // ==================== GET REVIEW TESTS ====================

    @Test
    @DisplayName("Should get review by ID successfully")
    void getReviewById_Success() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // When
        ReviewResponse response = reviewService.getReviewById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when review not found")
    void getReviewById_NotFound() {
        // Given
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.getReviewById(999L);
        });
    }

    @Test
    @DisplayName("Should get product reviews with pagination")
    void getProductReviews_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Review> reviews = Arrays.asList(testReview);
        Page<Review> reviewPage = new PageImpl<>(reviews, pageable, 1);

        when(reviewRepository.findByProductIdAndApprovedTrue(1L, pageable)).thenReturn(reviewPage);

        // When
        Page<ReviewResponse> response = reviewService.getProductReviews(1L, pageable);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(reviewRepository, times(1)).findByProductIdAndApprovedTrue(1L, pageable);
    }

    @Test
    @DisplayName("Should get user reviews with pagination")
    void getUserReviews_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Review> reviews = Arrays.asList(testReview);
        Page<Review> reviewPage = new PageImpl<>(reviews, pageable, 1);

        when(reviewRepository.findByUserId(1L, pageable)).thenReturn(reviewPage);

        // When
        Page<ReviewResponse> response = reviewService.getUserReviews(1L, pageable);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(reviewRepository, times(1)).findByUserId(1L, pageable);
    }

    // ==================== UPDATE REVIEW TESTS ====================

    @Test
    @DisplayName("Should update review successfully")
    void updateReview_Success() {
        // Given
        reviewRequest.setRating(4);
        reviewRequest.setTitle("Updated title");
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // When
        ReviewResponse response = reviewService.updateReview(1L, reviewRequest);

        // Then
        assertNotNull(response);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent review")
    void updateReview_NotFound() {
        // Given
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.updateReview(999L, reviewRequest);
        });
    }

    // ==================== DELETE REVIEW TESTS ====================

    @Test
    @DisplayName("Should delete review successfully")
    void deleteReview_Success() {
        // Given
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(4.0);
        when(reviewRepository.countByProductIdAndApprovedTrue(1L)).thenReturn(5L);
        doNothing().when(reviewRepository).delete(any(Review.class));

        // When
        assertDoesNotThrow(() -> reviewService.deleteReview(1L));

        // Then
        verify(reviewRepository, times(1)).delete(testReview);
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent review")
    void deleteReview_NotFound() {
        // Given
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.deleteReview(999L);
        });

        verify(reviewRepository, never()).delete(any(Review.class));
    }

    // ==================== APPROVE REVIEW TESTS ====================

    @Test
    @DisplayName("Should approve review successfully")
    void approveReview_Success() {
        // Given
        testReview.setApproved(false);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(4.5);
        when(reviewRepository.countByProductIdAndApprovedTrue(1L)).thenReturn(10L);

        // When
        assertDoesNotThrow(() -> reviewService.approveReview(1L));

        // Then
        verify(reviewRepository, times(1)).save(testReview);
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    @DisplayName("Should throw exception when approving non-existent review")
    void approveReview_NotFound() {
        // Given
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.approveReview(999L);
        });
    }
}
