package com.learning.moviesreview.exception;

public class ReviewException extends RuntimeException {

    private String message;

    public ReviewException(String message) {
        super(message);
        this.message = message;
    }
}
