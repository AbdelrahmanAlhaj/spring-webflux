package com.learning.moviesinfoservice.controller;

import com.learning.moviesinfoservice.domain.MovieInfo;
import com.learning.moviesinfoservice.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private MovieInfoRepository movieInfoRepository;
    private final static String MOVIES_INFO_URL = "/v1/movie-info";

    @BeforeEach
    void setUp() {
        MovieInfo movieInfo1 = MovieInfo.builder()
                .movieInfoId(null)
                .name("Batman Begins1")
                .year(2005)
                .cast(List.of("Christian Bale1", "Michael Cane1"))
                .releaseDate(LocalDate.parse("2005-06-15"))
                .build();
        MovieInfo movieInfo2 = MovieInfo.builder()
                .movieInfoId(null)
                .name("Batman Begins2")
                .year(2008)
                .cast(List.of("Christian Bale2", "Michael Cane2"))
                .releaseDate(LocalDate.parse("2008-06-15"))
                .build();
        MovieInfo movieInfo3 = MovieInfo.builder()
                .movieInfoId("abc")
                .name("Batman Begins3")
                .year(2012)
                .cast(List.of("Christian Bale3", "Michael Cane3"))
                .releaseDate(LocalDate.parse("2012-06-15"))
                .build();

        var movies = List.of(movieInfo1, movieInfo2, movieInfo3);
        movieInfoRepository.saveAll(movies)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }


    @Test
    void addMovieInfo() {
        MovieInfo newMovie = MovieInfo.builder()
                .movieInfoId(null)
                .name("Batman Begins4")
                .year(2005)
                .cast(List.of("Christian Bale4", "Michael Cane4"))
                .releaseDate(LocalDate.parse("2005-06-15"))
                .build();

        webTestClient.post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(newMovie)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfo -> {
                    var savedMovieInfo = movieInfo.getResponseBody();
                    assert savedMovieInfo != null;
                    assert savedMovieInfo.getMovieInfoId() != null;

                });
    }

    @Test
    void getMoviesInfo() {
        webTestClient.get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMoviesInfoById() {
        var movieInfoId = "abc";
        webTestClient.get()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfo -> {
                    var fetchedMovieInfo = movieInfo.getResponseBody();
                    assertNotNull(fetchedMovieInfo);
                    assertEquals("abc", fetchedMovieInfo.getMovieInfoId());
                    assertEquals(2012, fetchedMovieInfo.getYear());
                });
    }

    @Test
    void getMoviesInfoById_test2() {
        var movieInfoId = "abc";
        var movieInfoName = "Batman Begins3";
        webTestClient.get()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.movieInfoId").isEqualTo(movieInfoId)
                .jsonPath("$.name").isEqualTo(movieInfoName)
                .jsonPath("$.year").isEqualTo(2012);

    }

    @Test
    void getMoviesInfoById_returnNotFound() {
        var movieInfoId = "abd";
        webTestClient.get()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void updateMoviesInfo() {
        var movieInfoId = "abc";
        MovieInfo updatedMovie = MovieInfo.builder()
                .name("Batman Begins updated")
                .year(2010)
                .cast(List.of("Christian Bale3", "Michael Cane3"))
                .releaseDate(LocalDate.parse("2012-06-15"))
                .build();

        webTestClient.put()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .bodyValue(updatedMovie)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo(updatedMovie.getName())
                .jsonPath("$.year").isEqualTo(updatedMovie.getYear());

    }

    @Test
    void updateMoviesInfo_returnNotFound() {
        var movieInfoId = "abc2";
        MovieInfo updatedMovie = MovieInfo.builder()
                .name("Batman Begins updated")
                .year(2010)
                .cast(List.of("Christian Bale3", "Michael Cane3"))
                .releaseDate(LocalDate.parse("2012-06-15"))
                .build();

        webTestClient.put()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .bodyValue(updatedMovie)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void deleteMovieInfo() {
        var movieInfoId = "abc";
        webTestClient.delete()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void deleteMovieInfo_returnNotFound() {
        var movieInfoId = "abd";
        webTestClient.delete()
                .uri(MOVIES_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
}