package com.gemini.LMS.exception;

import org.springframework.http.HttpStatus;

public final class LibraryException extends RuntimeException{
    private final HttpStatus httpStatus;

    public LibraryException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
