package com.learning.moviesinfoservice.repository;

import com.learning.moviesinfoservice.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {

    Flux<MovieInfo> findByYear(Integer year);

    Mono<MovieInfo> findByName(String name);
}
