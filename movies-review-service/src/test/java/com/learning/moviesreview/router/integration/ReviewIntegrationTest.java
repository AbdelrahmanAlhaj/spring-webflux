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


    @BeforeEach
    void setUp() {
        var reviewList = List.of(
                new Review(null, 1L, "Awesome movie", 9.0),
                new Review(null, 2L, "Awesome movie1", 9.0),
                new Review(null, 2L, "Awesome movie", 8.0)
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
                .uri("/v1/reviews")
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
}