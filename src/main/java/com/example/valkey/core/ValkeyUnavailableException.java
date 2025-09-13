package com.example.valkey.core;

/** Indicates that the Valkey server cannot be reached or is otherwise unavailable. */
public class ValkeyUnavailableException extends RuntimeException {
    /**
     * Creates a new exception with the specified detail message and cause.
     *
     * @param message description of the error
     * @param cause the underlying cause of the failure
     */
    public ValkeyUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
