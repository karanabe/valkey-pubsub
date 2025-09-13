package com.example.valkey.core;

/** Interface representing a component that publishes messages to Valkey pub/sub. */
public interface MessagePublisher {
    /**
     * Publish a message to the specified channel.
     *
     * @param channel target channel
     * @param message message to send
     * @return number of subscribers that responded to the publish
     * @throws ValkeyUnavailableException if Valkey is unavailable
     * @throws PublishException if the publish operation fails
     * @throws NullPointerException if channel or message is {@code null}
     */
    long publish(String channel, String message);
}
