package com.learning.moviesservice.controller;

import com.learning.moviesservice.client.MoviesInfoClient;
import com.learning.moviesservice.client.ReviewInfoClient;
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
    private final MoviesInfoClient moviesInfoClient;
    private final ReviewInfoClient reviewInfoClient;

    @GetMapping("/{id}")
    public Mono<Movie> retrieveMovie(@PathVariable("id") String id) {
        return moviesInfoClient.retrieveMovieInfo(id)
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewListMono = reviewInfoClient.retrieveReviewInfo(movieInfo.getMovieInfoId())
                            .collectList();
                    return reviewListMono.map(review -> new Movie(movieInfo, review));
                });
    }

}
