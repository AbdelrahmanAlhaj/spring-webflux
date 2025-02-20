package com.learning.moviesinfoservice.controller;

import com.learning.moviesinfoservice.domain.MovieInfo;
import com.learning.moviesinfoservice.service.MoviesInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@RequiredArgsConstructor
@RequestMapping("/v1/movie-info")
@RestController
public class MoviesInfoController {

    private final MoviesInfoService moviesInfoService;
    Sinks.Many<MovieInfo> movieInfoSink = Sinks.many().replay().all();

    @GetMapping
    public Flux<MovieInfo> getAllMoviesInfo() {
        return moviesInfoService.getAllMoviesInfo();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfo(@PathVariable("id") String id) {
        return moviesInfoService.getMovieInfoById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<MovieInfo> getMovieInfoStream() {
        return movieInfoSink.asFlux().log();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return moviesInfoService.addMovieInfo(movieInfo)
                .doOnNext(savedMovie -> movieInfoSink.tryEmitNext(savedMovie))
                .log();
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@PathVariable("id") String id,
                                                           @RequestBody MovieInfo movieInfo) {
        return moviesInfoService.updateMovieInfo(id, movieInfo)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteMovieInfo(@PathVariable("id") String id) {
        return moviesInfoService.deleteMovieInfo(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }


}
