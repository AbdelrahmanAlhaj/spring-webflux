package com.learning.moviesreview.router.integration;

import com.learning.moviesreview.domain.Review;
import com.learning.moviesreview.repository.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
@ActiveProfiles("test")
class ReviewIntegrationTest {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private WebTestClient webTestClient;

    List<Review> reviewList;
    private final String REVIEW_URL = "/v1/reviews";

    @BeforeEach
    void setUp() {
        reviewList = List.of(
                new Review("1", 1L, "Awesome movie1", 7.0),
                new Review("2", 2L, "Awesome movie2", 8.0),
                new Review("3", 2L, "Awesome movie3", 9.0)
        );
        reviewRepository.saveAll(reviewList).blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll().block();
    }

    @Test
    void test_addReview_returnCreated() {
        var newReview = new Review(null, 1L, "Awesome movie", 9.0);

        webTestClient
                .post()
                .uri(REVIEW_URL)
                .bodyValue(newReview)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(Review.class)
                .consumeWith(dbReview -> {
                    assert dbReview != null;
                    Review responseBody = dbReview.getResponseBody();
                    assert responseBody != null;
                    assertEquals(newReview.getMovieInfoId(), responseBody.getMovieInfoId());
                    assertEquals(newReview.getComment(), responseBody.getComment());
                });
    }

    @Test
    void test_getReview_viewsList() {
        webTestClient
                .get()
                .uri(REVIEW_URL)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .hasSize(reviewList.size())
                .consumeWith(dbReview -> {
                    assert dbReview.getResponseBody() != null;
                    assertEquals(dbReview.getResponseBody().getFirst().getMovieInfoId(), reviewList.getFirst().getMovieInfoId());
                });
    }

    @Test
    void test_getReview_ByMovieInfoId() {
        var movieInfoId = 2;
        webTestClient
                .get()
                .uri(REVIEW_URL + "?movieInfoId=" + movieInfoId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Review.class)
                .hasSize(2)
                .consumeWith(dbReview -> {
                    assert dbReview.getResponseBody() != null;
                    assertEquals(dbReview.getResponseBody().getFirst().getComment(), reviewList.get(1).getComment());
                    assertEquals(dbReview.getResponseBody().get(1).getComment(), reviewList.get(2).getComment());
                });
    }

    @Test
    void test_updateReview() {
        var updatedReviewReq = new Review(null, 5L, "Updated Awesome movie", 10.0);
        webTestClient
                .put()
                .uri(REVIEW_URL + "/{reviewId}", reviewList.getFirst().getReviewId())
                .bodyValue(updatedReviewReq)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Review.class)
                .consumeWith(dbReview -> {
                    Review updatedReviewResponse = dbReview.getResponseBody();
                    assert updatedReviewResponse != null;
                    assertEquals(updatedReviewResponse.getMovieInfoId(), updatedReviewReq.getMovieInfoId());
                    assertEquals(updatedReviewResponse.getComment(), updatedReviewReq.getComment());
                    assertEquals(updatedReviewResponse.getRating(), updatedReviewReq.getRating());
                });
    }

    @Test
    void test_deleteReview() {
        var reviewId = 1;
        webTestClient
                .delete()
                .uri(REVIEW_URL + "/{reviewId}", reviewId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

}