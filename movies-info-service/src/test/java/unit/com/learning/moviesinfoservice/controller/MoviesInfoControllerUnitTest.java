package com.learning.moviesinfoservice.controller;


import com.learning.moviesinfoservice.domain.MovieInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MoviesInfoController.class)
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private MoviesInfoService moviesInfoServiceMocked;

    private final static String MOVIES_INFO_URL = "/v1/movie-info";

    @Test
    void getAllMoviesInfo() {
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

        when(moviesInfoServiceMocked.getAllMoviesInfo())
                .thenReturn(Flux.fromIterable(movies));

        webTestClient
                .get()
                .uri(MOVIES_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieInfoById() {
        MovieInfo movieInfo3 = MovieInfo.builder()
                .movieInfoId("abc")
                .name("Batman Begins3")
                .year(2012)
                .cast(List.of("Christian Bale3", "Michael Cane3"))
                .releaseDate(LocalDate.parse("2012-06-15"))
                .build();

        when(moviesInfoServiceMocked.getMovieInfoById(isA(String.class)))
                .thenReturn(Mono.just(movieInfo3));

        webTestClient
                .get()
                .uri(MOVIES_INFO_URL + "/abc")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Batman Begins3")
                .jsonPath("$.year").isEqualTo(2012)
                .jsonPath("$.movieInfoId").isEqualTo("abc");


//                .expectBody(MovieInfo.class)
//                .consumeWith(movieInfoEntityExchangeResult -> {
//                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
//                    assert movieInfo != null;
//                    assertEquals("abc", movieInfo.getMovieInfoId());
//                    assertEquals(2012, movieInfo.getYear());
//                    assertEquals("Batman Begins3", movieInfo.getName());
//                    assertEquals(LocalDate.parse("2012-06-15"), movieInfo.getReleaseDate());
//                });
    }

    @Test
    void addMovieInfo() {
        //given
        var newMovieInfo = MovieInfo.builder()
                .name("Batman Begins3")
                .year(2012)
                .cast(List.of("Christian Bale3", "Michael Cane3"))
                .releaseDate(LocalDate.parse("2012-06-15"))
                .build();

        when(moviesInfoServiceMocked.addMovieInfo(isA(MovieInfo.class)))
                .thenReturn(Mono.just(newMovieInfo));

        webTestClient
                .post()
                .uri(MOVIES_INFO_URL)
                .bodyValue(newMovieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo(newMovieInfo.getName())
                .jsonPath("$.year").isEqualTo(newMovieInfo.getYear());
    }

    @Test
    void updateMovieInfo() {
        //given
        var movieId = "abc";
        var updatedMovie = MovieInfo.builder()
                .movieInfoId(movieId)
                .name("Updated names Test")
                .year(2012)
                .cast(List.of("Christian Bale3", "Michael Cane3"))
                .releaseDate(LocalDate.parse("2012-06-15"))
                .build();

        when(moviesInfoServiceMocked.updateMovieInfo(isA(String.class), isA(MovieInfo.class)))
                .thenReturn(Mono.just(updatedMovie));

        webTestClient
                .put()
                .uri(MOVIES_INFO_URL + "/{id}", movieId)
                .bodyValue(updatedMovie)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo(updatedMovie.getName())
                .jsonPath("$.year").isEqualTo(updatedMovie.getYear());
    }

    @Test
    void deleteMovieInfo() {
        //given
        var movieId = "abc";
        when(moviesInfoServiceMocked.deleteMovieInfo(isA(String.class)))
                .thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri(MOVIES_INFO_URL + "/{id}", movieId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}
