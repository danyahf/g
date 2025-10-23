package com.danya.exception;

public class InvalidCurrentPasswordException extends RuntimeException {
    public InvalidCurrentPasswordException() {
        super("The provided password does not match the current one");
    }
}
