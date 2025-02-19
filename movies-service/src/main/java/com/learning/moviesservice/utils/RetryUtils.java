package com.learning.moviesservice.utils;

import com.learning.moviesservice.exceptions.MoviesInfoServerException;
import com.learning.moviesservice.exceptions.ReviewsServerException;
import reactor.core.Exceptions;
import reactor.util.retry.Retry;

import java.time.Duration;

public class RetryUtils {

    public static Retry retrySpec() {
        return Retry.fixedDelay(3, Duration.ofSeconds(1))
                .filter(exception -> exception instanceof MoviesInfoServerException || exception instanceof ReviewsServerException)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        Exceptions.propagate(retrySignal.failure()));
    }
}
