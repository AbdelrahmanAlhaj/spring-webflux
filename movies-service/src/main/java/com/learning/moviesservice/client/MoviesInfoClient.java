package com.learning.moviesservice.client;

import com.learning.moviesservice.domain.MovieInfo;
import com.learning.moviesservice.exceptions.MoviesInfoClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class MoviesInfoClient {
    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;

    private final WebClient webClient;

    public MoviesInfoClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {
        var baseUrl = moviesInfoUrl.concat("/{id}");

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
                .bodyToMono(MovieInfo.class);
    }
}
