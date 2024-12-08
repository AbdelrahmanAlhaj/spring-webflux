package com.learning.moviesinfoservice.controller;

import com.learning.moviesinfoservice.domain.MovieInfo;
import com.learning.moviesinfoservice.exceptions.ResourceNotFoundException;
import com.learning.moviesinfoservice.repository.MovieInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class MoviesInfoService {
    private final MovieInfoRepository movieInfoRepository;

    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMoviesInfo() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String id) {
        return movieInfoRepository.findById(id);
    }

    public Mono<MovieInfo> updateMovieInfo(String id, MovieInfo updatedMovieInfo) {
        return movieInfoRepository.findById(id)
                .flatMap(movieInfo -> {
                    movieInfo.setCast(updatedMovieInfo.getCast());
                    movieInfo.setName(updatedMovieInfo.getName());
                    movieInfo.setYear(updatedMovieInfo.getYear());
                    movieInfo.setReleaseDate(updatedMovieInfo.getReleaseDate());
                    return movieInfoRepository.save(movieInfo);
                });
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return movieInfoRepository.findById(id)
                .switchIfEmpty(Mono.error(ResourceNotFoundException::new))
                .flatMap(movieInfoRepository::delete);
    }
}
