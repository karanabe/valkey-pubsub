package com.example.valkey.core;

public class ValkeyUnavailableException extends RuntimeException {
    public ValkeyUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
