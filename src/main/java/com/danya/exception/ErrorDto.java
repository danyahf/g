package com.danya.exception;

public record ErrorDto(
        int status,
        String error,
        String message,
        String path
) {
}
