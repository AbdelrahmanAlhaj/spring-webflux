package com.learning.moviesreview.router.unit;

import com.learning.moviesreview.domain.Review;
import com.learning.moviesreview.handler.ReviewHandler;
import com.learning.moviesreview.repository.ReviewRepository;
import com.learning.moviesreview.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockitoBean
    private ReviewRepository reviewRepository;

    @Autowired
    private WebTestClient webTestClient;

    private final String REVIEW_URL = "/v1/reviews";

    @Test
    void test_addReview() {
        //given
        var newMovieReview = Review.builder()
                .reviewId("1")
                .movieInfoId(1L)
                .comment("First movie review")
                .rating(7.0)
                .build();
        //when
        when(reviewRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(newMovieReview));

        //then
        webTestClient
                .post()
                .uri(REVIEW_URL)
                .bodyValue(newMovieReview)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(response -> {
                    assert response != null;
                    Review savedReview = response.getResponseBody();
                    assert savedReview != null;
                    assert savedReview.getReviewId() != null;
                    assertEquals(savedReview.getReviewId(), newMovieReview.getReviewId());
                });
//                .expectBody()
//                .jsonPath("$.comment").isEqualTo(newMovieReview.getComment())
//                .jsonPath("$.rating").isEqualTo(newMovieReview.getRating());

    }

    @Test
    void test_getAllReview() {
        var reviewList = List.of(
                new Review("1", 1L, "Awesome movie1", 7.0),
                new Review("2", 2L, "Awesome movie2", 8.0),
                new Review("3", 2L, "Awesome movie3", 9.0)
        );
        //when
        when(reviewRepository.findAll())
                .thenReturn(Flux.fromIterable(reviewList));

        //then
        webTestClient
                .get()
                .uri(REVIEW_URL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .hasSize(3)
                .consumeWith(response -> {
                    assert response != null;
                    List<Review> responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.size() == 3;
                    assertEquals(responseBody.getFirst().getComment(), reviewList.getFirst().getComment());
                });
    }

    @Test
    void test_getReviewById() {
        var reviewList = List.of(
                new Review("1", 1L, "Awesome movie1", 7.0)
        );
        //when
        when(reviewRepository.findByMovieInfoId(isA(Long.class)))
                .thenReturn(Flux.fromIterable(reviewList));

        //then
        webTestClient
                .get()
                .uri(REVIEW_URL + "?movieInfoId=1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .hasSize(1)
                .consumeWith(response -> {
                    assert response != null;
                    List<Review> responseBody = response.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.size() == 1;
                    assertEquals(responseBody.getFirst().getComment(), reviewList.getFirst().getComment());
                });
    }

    @Test
    void test_deleteReview() {
        Review reviewMovie = new Review("1", 1L, "Awesome movie1", 7.0);

        //when
        when(reviewRepository.findById(isA(String.class)))
                .thenReturn(Mono.just(reviewMovie));

        when(reviewRepository.deleteById(isA(String.class)))
                .thenReturn(Mono.empty());
        //then
        webTestClient
                .delete()
                .uri(REVIEW_URL + "/1")
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void test_updateReview() {
        //given
        var existingMovie = Review.builder()
                .reviewId("1")
                .movieInfoId(1L)
                .comment("movie review")
                .rating(9.0)
                .build();
        var updatedMovieReview = Review.builder()
                .reviewId("3")
                .movieInfoId(3L)
                .comment("Updated movie review")
                .rating(7.0)
                .build();
        //when
        when(reviewRepository.findById(isA(String.class)))
                .thenReturn(Mono.just(existingMovie));

        when(reviewRepository.save(isA(Review.class)))
                .thenReturn(Mono.just(updatedMovieReview));

        //then
        webTestClient
                .put()
                .uri(REVIEW_URL + "/1")
                .bodyValue(updatedMovieReview)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.comment").isEqualTo(updatedMovieReview.getComment())
                .jsonPath("$.rating").isEqualTo(updatedMovieReview.getRating());

    }

}
