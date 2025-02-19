package com.learning.moviesservice.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.learning.moviesservice.domain.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084)
@TestPropertySource(
        properties = {
                "restClient.moviesInfoUrl=http://localhost:8084/v1/movie-info",
                "restClient.reviewsInfoUrl=http://localhost:8084/v1/reviews"
        }
)
public class MoviesControllerIntegrationTesting {

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    void resetWireMock() {
        WireMock.reset();
    }

    @Test
    void retrieveMovieById() {
        //given
        var movieId = "1";
        stubFor(get(urlPathEqualTo("/v1/movie-info/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movieinfo.json")
                ));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("reviews.json")
                ));

        //when
        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    var movie = movieEntityExchangeResult.getResponseBody();
                    assertNotNull(movie);
                    assertEquals(2, movie.getReviewList().size());
                    assertEquals("Law abiding citizen", movie.getMovieInfo().getName());
                });

        //then
    }

    @Test
    void retrieveMovieById_404() {
        //given
        var movieId = "1";
        stubFor(get(urlPathEqualTo("/v1/movie-info/" + movieId))
                .willReturn(aResponse()
                        .withStatus(404)
                ));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("reviews.json")
                ));

        //when
        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .is4xxClientError()
                .expectBody(String.class)
                .isEqualTo("There is no movie available for the passed Id 1");

        WireMock.verify(1, getRequestedFor(urlPathEqualTo("/v1/movie-info/" + movieId)));
        //then
    }

    @Test
    void retrieveMovieById_empty_reviews() {
        //given
        var movieId = "1";
        stubFor(get(urlPathEqualTo("/v1/movie-info/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movieinfo.json")
                ));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withStatus(404)
                ));

        //when
        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    var movie = movieEntityExchangeResult.getResponseBody();
                    assertNotNull(movie);
                    assertEquals(0, movie.getReviewList().size());
                    assertEquals("Law abiding citizen", movie.getMovieInfo().getName());
                });

        //then
    }

    @Test
    void retrieveMovieById_5XX() {
        //given
        var movieId = "1";
        stubFor(get(urlPathEqualTo("/v1/movie-info/" + movieId))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Movies Service Unavailable")
                ));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("reviews.json")
                ));

        //when
        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Server Exception in MoviesInfoService Movies Service Unavailable");

        WireMock.verify(4, getRequestedFor(urlPathEqualTo("/v1/movie-info/" + movieId)));
    }

    @Test
    void retrieveMovieById_reviews_5XX() {
        //given
        var movieId = "1";
        stubFor(get(urlPathEqualTo("/v1/movie-info/" + movieId))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("movieinfo.json")
                ));

        stubFor(get(urlPathEqualTo("/v1/reviews"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("ReviewService Movies Service Unavailable")
                ));

        //when
        webTestClient.get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .is5xxServerError()
                .expectBody(String.class)
                .isEqualTo("Server Exception in ReviewService ReviewService Movies Service Unavailable");

        WireMock.verify(4, getRequestedFor(urlPathMatching("/v1/reviews*")));
    }

}

