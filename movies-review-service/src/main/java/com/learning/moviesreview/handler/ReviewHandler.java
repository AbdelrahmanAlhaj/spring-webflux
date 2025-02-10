package com.learning.moviesreview.handler;

import com.learning.moviesreview.domain.Review;
import com.learning.moviesreview.exception.ReviewNotFoundException;
import com.learning.moviesreview.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Component
public class ReviewHandler {
    private final ReviewRepository reviewRepository;

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .flatMap(reviewRepository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
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
}
