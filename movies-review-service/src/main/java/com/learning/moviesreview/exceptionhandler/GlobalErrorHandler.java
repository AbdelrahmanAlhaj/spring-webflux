package com.learning.moviesreview.exceptionhandler;

import com.learning.moviesreview.exception.ReviewException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @NonNull
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        DataBuffer errorMessage = dataBufferFactory.wrap(ex.getMessage().getBytes());

        if (ex instanceof ReviewException) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return exchange.getResponse().writeWith(Mono.just(errorMessage));
        }
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return exchange.getResponse().writeWith(Mono.just(errorMessage));
    }
}
