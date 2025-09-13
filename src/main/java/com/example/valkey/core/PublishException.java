package com.example.valkey.core;

/**
 * Thrown when a publish operation fails to deliver a message to Valkey.
 */
public class PublishException extends RuntimeException {
    /**
     * Creates a new exception with the specified detail message and cause.
     *
     * @param message description of the error
     * @param cause the underlying cause of the failure
     */
    public PublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
