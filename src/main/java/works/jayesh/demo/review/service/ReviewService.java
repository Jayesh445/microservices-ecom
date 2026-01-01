package works.jayesh.demo.review.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    public ReviewResponse createReview(Long userId, ReviewRequest request) {
        log.info("Creating review for product {} by user {}", request.getProductId(), userId);

        if (reviewRepository.existsByProductIdAndUserId(request.getProductId(), userId)) {
            throw new DuplicateResourceException("You have already reviewed this product");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getProductId()));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        // Check if user has purchased this product
        List<OrderItem> purchasedItems = orderItemRepository.findByUserIdAndProductId(userId, request.getProductId());
        boolean verified = !purchasedItems.isEmpty();

        Review review = Review.builder()
                .product(product)
                .user(user)
                .rating(request.getRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .verified(verified)
                .approved(false) // Requires admin approval
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("Review created successfully with ID: {}", savedReview.getId());

        return mapToResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long reviewId) {
        Review review = findReviewById(reviewId);
        return mapToResponse(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getProductReviews(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdAndApprovedTrue(productId, pageable)
                .map(this::mapToResponse);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getUserReviews(Long userId, Pageable pageable) {
        return reviewRepository.findByUserId(userId, pageable).map(this::mapToResponse);
    }

    public ReviewResponse updateReview(Long reviewId, ReviewRequest request) {
        log.info("Updating review with ID: {}", reviewId);

        Review review = findReviewById(reviewId);

        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setComment(request.getComment());
        review.setApproved(false); // Reset approval on update

        Review updatedReview = reviewRepository.save(review);
        log.info("Review updated successfully");

        return mapToResponse(updatedReview);
    }

    public ReviewResponse approveReview(Long reviewId) {
        log.info("Approving review: {}", reviewId);

        Review review = findReviewById(reviewId);
        review.setApproved(true);

        Review approvedReview = reviewRepository.save(review);

        // Update product rating
        updateProductRating(review.getProduct().getId());

        log.info("Review approved successfully");
        return mapToResponse(approvedReview);
    }

    public void deleteReview(Long reviewId) {
        log.info("Deleting review with ID: {}", reviewId);

        Review review = findReviewById(reviewId);
        Long productId = review.getProduct().getId();

        reviewRepository.delete(review);

        // Update product rating after deletion
        updateProductRating(productId);

        log.info("Review deleted successfully");
    }

    public void markHelpful(Long reviewId) {
        Review review = findReviewById(reviewId);
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(review);
    }

    private void updateProductRating(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        Double averageRating = reviewRepository.getAverageRatingByProductId(productId);
        long totalReviews = reviewRepository.countByProductIdAndApprovedTrue(productId);

        product.setAverageRating(averageRating != null ? averageRating : 0.0);
        product.setTotalReviews((int) totalReviews);

        productRepository.save(product);
    }

    private Review findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + reviewId));
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .userId(review.getUser().getId())
                .userName(review.getUser().getFirstName() + " " + review.getUser().getLastName())
                .rating(review.getRating())
                .title(review.getTitle())
                .comment(review.getComment())
                .verified(review.isVerified())
                .approved(review.isApproved())
                .helpfulCount(review.getHelpfulCount())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
