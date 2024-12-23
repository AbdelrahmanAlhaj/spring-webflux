package com.learning.moviesreview.repository;

import com.learning.moviesreview.domain.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ReviewRepository extends ReactiveMongoRepository<Review, String> {

}
