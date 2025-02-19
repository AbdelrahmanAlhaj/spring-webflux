package com.learning.moviesservice.controller;

import com.learning.moviesservice.client.MoviesInfoRestClient;
import com.learning.moviesservice.client.ReviewInfoRestClient;
import com.learning.moviesservice.domain.Movie;
import com.learning.moviesservice.domain.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/movies")
public class MoviesController {
    private final MoviesInfoRestClient moviesInfoRestClient;
    private final ReviewInfoRestClient reviewInfoRestClient;

    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovie(@PathVariable("id") String id) {
        return moviesInfoRestClient.retrieveMovieInfo(id)
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewListMono = reviewInfoRestClient.retrieveReviewInfo(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewListMono.map(review -> new Movie(movieInfo, review));
                });
    }

}
