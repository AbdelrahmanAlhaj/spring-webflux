package com.learning.moviesservice.client;

import com.learning.moviesservice.domain.MovieInfo;
import com.learning.moviesservice.exceptions.MoviesInfoClientException;
import com.learning.moviesservice.exceptions.MoviesInfoServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@Slf4j
@Configuration
public class MoviesInfoRestClient {
    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;

    private final WebClient webClient;

    public MoviesInfoRestClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        var baseUrl = moviesInfoUrl.concat("/{id}");

        RetryBackoffSpec retrySpec = Retry
                .fixedDelay(3, Duration.ofSeconds(1))
                .filter(exception -> exception instanceof MoviesInfoServerException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        Exceptions.propagate(retrySignal.failure()));

        return webClient.get()
                .uri(baseUrl, movieId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (HttpStatus.NOT_FOUND.equals(clientResponse.statusCode())) {
                        return Mono.error(
                                new MoviesInfoClientException("There is no movie available for the passed Id " + movieId,
                                        clientResponse.statusCode().value()
                                ));
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage -> Mono.error(
                                    new MoviesInfoClientException(responseMessage, clientResponse.statusCode().value()))
                            );

                })
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    log.info("Movie info retrieval failed with status code {}", clientResponse.statusCode());
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMessage -> Mono.error(
                                    new MoviesInfoServerException("Server Exception in MoviesInfoService " + responseMessage))
                            );

                })
                .bodyToMono(MovieInfo.class)
//                .retry(3);
                .retryWhen(retrySpec);
    }
}
