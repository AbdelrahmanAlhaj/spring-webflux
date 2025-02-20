package com.learning.moviesreview.handler;

import com.learning.moviesreview.domain.Review;
import com.learning.moviesreview.exception.ReviewException;
import com.learning.moviesreview.exception.ReviewNotFoundException;
import com.learning.moviesreview.repository.ReviewRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReviewHandler {

    private final Validator validator;
    private final ReviewRepository reviewRepository;
    Sinks.Many<Review> reviewSink = Sinks.many().replay().latest();

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validateReview)
                .flatMap(reviewRepository::save)
                .doOnNext(reviewSink::tryEmitNext)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    private void validateReview(Review review) {
        var constraintViolations = validator.validate(review);
        if (!constraintViolations.isEmpty()) {
            log.info("Validation errors: {}", constraintViolations);
            var errors = constraintViolations.stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(","));
            throw new ReviewException(errors);
        }
    }

    public Mono<ServerResponse> getReviews(ServerRequest request) {
        var movieId = request.queryParam("movieInfoId");
        Flux<Review> reviews;
        if (movieId.isPresent()) {
            reviews = reviewRepository.findByMovieInfoId(Long.parseLong(movieId.get()));
        } else {
            reviews = reviewRepository.findAll();
        }
        return ServerResponse.ok().body(reviews, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {
        var reviewId = request.pathVariable("reviewId");
        var existingReview = reviewRepository.findById(reviewId)
                .switchIfEmpty(Mono.error(new ReviewNotFoundException("Review not found for the give review id " + reviewId)));

        return existingReview
                .flatMap(review -> request.bodyToMono(Review.class)
                        .map(reqReview -> {
                            review.setComment(reqReview.getComment());
                            review.setRating(reqReview.getRating());
                            review.setMovieInfoId(reqReview.getMovieInfoId());
                            return review;
                        })
                        .flatMap(reviewRepository::save)
                        .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))
                );
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        var reviewId = request.pathVariable("reviewId");

        var existingReview = reviewRepository.findById(reviewId);

        return existingReview
                .flatMap(review -> reviewRepository.deleteById(reviewId)
                        .then(ServerResponse.noContent().build()));

    }

    public Mono<ServerResponse> getStreamReviews(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_NDJSON)
                .body(reviewSink.asFlux(), Review.class)
                .log();
    }
}
