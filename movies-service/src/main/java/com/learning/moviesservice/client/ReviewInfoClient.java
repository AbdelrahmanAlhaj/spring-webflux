package com.learning.moviesservice.client;

import com.learning.moviesservice.domain.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Component
public class ReviewInfoClient {

    @Value("${restClient.reviewsInfoUrl}")
    private String reviewUrl;
    private final WebClient webClient;

    public Flux<Review> retrieveReviewInfo(String movieId) {
        var baseUrl = UriComponentsBuilder.fromUriString(reviewUrl)
                .queryParam("movieInfoId", movieId)
                .buildAndExpand()
                .toUriString();

        return webClient.get()
                .uri(baseUrl)
                .retrieve()
                .bodyToFlux(Review.class);

    }
}
