package com.learning.moviesreview.router;

import com.learning.moviesreview.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ReviewRouter {

    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler) {
        return route()
                .GET("/v1/helloworld", (request -> ServerResponse.ok().bodyValue("Hello world")))
                .POST("/v1/reviews", reviewHandler::addReview)
                .build();
    }

}
