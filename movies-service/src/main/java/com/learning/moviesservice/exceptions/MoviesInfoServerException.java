package com.learning.moviesservice.exceptions;

public class MoviesInfoServerException extends RuntimeException {

    private final String message;

    public MoviesInfoServerException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
