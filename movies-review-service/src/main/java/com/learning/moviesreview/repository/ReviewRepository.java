package com.learning.moviesreview.repository;

import com.learning.moviesreview.domain.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ReviewRepository extends ReactiveMongoRepository<Review, String> {

    Flux<Review> findByMovieInfoId(Long movieId);
}
