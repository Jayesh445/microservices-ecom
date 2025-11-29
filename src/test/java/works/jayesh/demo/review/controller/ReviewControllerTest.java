package works.jayesh.demo.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import works.jayesh.demo.common.exception.GlobalExceptionHandler;
import works.jayesh.demo.common.exception.ResourceNotFoundException;
import works.jayesh.demo.review.model.dto.ReviewRequest;
import works.jayesh.demo.review.model.dto.ReviewResponse;
import works.jayesh.demo.review.service.ReviewService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewController API Tests")
class ReviewControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private ReviewService reviewService;

    @InjectMocks
    private ReviewController reviewController;

    private ReviewResponse reviewResponse;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
        objectMapper = new ObjectMapper();

        reviewResponse = ReviewResponse.builder()
                .id(1L)
                .productId(1L)
                .userId(1L)
                .userName("John Doe")
                .rating(5)
                .title("Excellent Product")
                .comment("Great quality and fast shipping!")
                .approved(false)
                .verified(true)
                .build();

        reviewRequest = new ReviewRequest();
        reviewRequest.setProductId(1L);
        reviewRequest.setRating(5);
        reviewRequest.setTitle("Excellent Product");
        reviewRequest.setComment("Great quality and fast shipping!");
    }

    @Test
    @DisplayName("Should create review successfully")
    void createReview_Success() throws Exception {
        when(reviewService.createReview(eq(1L), any(ReviewRequest.class))).thenReturn(reviewResponse);

        mockMvc.perform(post("/api/reviews/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.rating").value(5))
                .andExpect(jsonPath("$.data.title").value("Excellent Product"));
    }

    @Test
    @DisplayName("Should return 400 when creating review with missing productId")
    void createReview_MissingProductId() throws Exception {
        reviewRequest.setProductId(null);

        mockMvc.perform(post("/api/reviews/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return 400 when creating review with invalid rating")
    void createReview_InvalidRating() throws Exception {
        reviewRequest.setRating(6);

        mockMvc.perform(post("/api/reviews/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return 400 when creating review with missing title")
    void createReview_MissingTitle() throws Exception {
        reviewRequest.setTitle(null);

        mockMvc.perform(post("/api/reviews/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get review by ID successfully")
    void getReviewById_Success() throws Exception {
        when(reviewService.getReviewById(1L)).thenReturn(reviewResponse);

        mockMvc.perform(get("/api/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Excellent Product"));
    }

    @Test
    @DisplayName("Should return 404 when review not found")
    void getReviewById_NotFound() throws Exception {
        when(reviewService.getReviewById(999L))
                .thenThrow(new ResourceNotFoundException("Review not found with id: 999"));

        mockMvc.perform(get("/api/reviews/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get product reviews with pagination")
    void getProductReviews_Success() throws Exception {
        List<ReviewResponse> list = Arrays.asList(reviewResponse);
        Page<ReviewResponse> page = new PageImpl<>(list, PageRequest.of(0, 20), 1);

        when(reviewService.getProductReviews(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/api/reviews/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].productId").value(1));
    }

    @Test
    @DisplayName("Should get user reviews with pagination")
    void getUserReviews_Success() throws Exception {
        List<ReviewResponse> list = Arrays.asList(reviewResponse);
        Page<ReviewResponse> page = new PageImpl<>(list, PageRequest.of(0, 20), 1);

        when(reviewService.getUserReviews(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/api/reviews/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].userId").value(1));
    }

    @Test
    @DisplayName("Should update review successfully")
    void updateReview_Success() throws Exception {
        reviewResponse.setTitle("Updated Review");
        when(reviewService.updateReview(eq(1L), any(ReviewRequest.class))).thenReturn(reviewResponse);

        mockMvc.perform(put("/api/reviews/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent review")
    void updateReview_NotFound() throws Exception {
        when(reviewService.updateReview(eq(999L), any(ReviewRequest.class)))
                .thenThrow(new ResourceNotFoundException("Review not found with id: 999"));

        mockMvc.perform(put("/api/reviews/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should approve review successfully")
    void approveReview_Success() throws Exception {
        reviewResponse.setApproved(true);
        when(reviewService.approveReview(1L)).thenReturn(reviewResponse);

        mockMvc.perform(patch("/api/reviews/1/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.approved").value(true));
    }

    @Test
    @DisplayName("Should return 404 when approving non-existent review")
    void approveReview_NotFound() throws Exception {
        when(reviewService.approveReview(999L))
                .thenThrow(new ResourceNotFoundException("Review not found with id: 999"));

        mockMvc.perform(patch("/api/reviews/999/approve"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should mark review as helpful successfully")
    void markHelpful_Success() throws Exception {
        mockMvc.perform(patch("/api/reviews/1/helpful"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should return 404 when marking non-existent review as helpful")
    void markHelpful_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Review not found with id: 999"))
                .when(reviewService).markHelpful(999L);

        mockMvc.perform(patch("/api/reviews/999/helpful"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should delete review successfully")
    void deleteReview_Success() throws Exception {
        mockMvc.perform(delete("/api/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existent review")
    void deleteReview_NotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Review not found with id: 999"))
                .when(reviewService).deleteReview(999L);

        mockMvc.perform(delete("/api/reviews/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle malformed JSON")
    void createReview_MalformedJson() throws Exception {
        mockMvc.perform(post("/api/reviews/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle malformed review ID")
    void getReviewById_InvalidId() throws Exception {
        mockMvc.perform(get("/api/reviews/invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
