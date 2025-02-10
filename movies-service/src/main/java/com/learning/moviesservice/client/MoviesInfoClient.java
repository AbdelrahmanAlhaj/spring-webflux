package com.learning.moviesservice.client;

import com.learning.moviesservice.domain.MovieInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
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
                .bodyToMono(MovieInfo.class);
    }
}
