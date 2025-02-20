package com.learning.moviesservice.errorhandler;

import com.learning.moviesservice.exceptions.MoviesInfoClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MoviesInfoClientException.class)
    public ResponseEntity<String> handleMoviesInfoClientException(MoviesInfoClientException e) {
        log.error("Exception Caught in handleClientException: {}", e.getMessage());
        return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleMoviesServerClientException(RuntimeException e) {
        log.error("Exception Caught in handleMovieInfoException: {}", e.getMessage());
        return ResponseEntity.internalServerError().body(e.getMessage());
    }
}
