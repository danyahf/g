package com.danya.exception;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException(String message) {
        super(message);
    }

    public BadCredentialsException() {
        super("Bad credentials");
    }
}
