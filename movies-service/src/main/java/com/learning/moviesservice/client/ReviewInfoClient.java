package com.learning.moviesservice.client;

import com.learning.moviesservice.domain.Review;
import com.learning.moviesservice.exceptions.ReviewsClientException;
import com.learning.moviesservice.exceptions.ReviewsServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
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
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.empty();
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage ->
                                    Mono.error(new ReviewsClientException(responseMessage)))
                            ;
                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    log.info("Error response from review server with status code: {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage ->
                                    Mono.error(new ReviewsServerException("Server Exception in ReviewService " + responseMessage))
                            );
                })
                .bodyToFlux(Review.class);

    }
}
