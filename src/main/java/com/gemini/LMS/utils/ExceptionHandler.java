package com.gemini.LMS.utils;

import com.gemini.LMS.exception.LibraryException;
import com.gemini.LMS.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class ExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> libraryException(final LibraryException e) {
        LOGGER.error("Library exception occurred due to {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(false);
        errorResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponse, e.getHttpStatus());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler
    public ResponseEntity<ErrorResponse> defaultException(final Exception e) {
        LOGGER.error("Default exception occurred due to {}", e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setStatus(false);
        errorResponse.setMessage(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
