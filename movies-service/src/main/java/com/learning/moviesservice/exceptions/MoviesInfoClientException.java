package com.learning.moviesservice.exceptions;

import lombok.Getter;

public class MoviesInfoClientException extends RuntimeException {

    private final String message;
    @Getter
    private final Integer statusCode;

    public MoviesInfoClientException(String message, Integer statusCode) {
        this.message = message;
        this.statusCode = statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
