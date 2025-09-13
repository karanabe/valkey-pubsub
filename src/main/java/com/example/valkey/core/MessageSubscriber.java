package com.example.valkey.core;

/**
 * Provides asynchronous subscriptions to channels.
 * Implementations deliver messages to registered handlers without blocking the caller.
 */
public interface MessageSubscriber {

    /**
     * Callback invoked whenever a subscribed channel receives a message.
     */
    @FunctionalInterface
    interface Handler {
        /**
         * Handle a message published to a channel.
         *
         * @param channel the channel from which the message was received
         * @param message the payload delivered on the channel
         */
        void onMessage(String channel, String message);
    }

    /**
     * Begin an asynchronous subscription and return a handle that can stop it.
     *
     * @param channel channel to subscribe to
     * @param handler callback invoked for each received message
     * @return handle used to close the subscription
     */
    SubscriptionHandle subscribe(String channel, Handler handler);

    /**
     * Handle used to manage an active subscription.
     */
    interface SubscriptionHandle extends AutoCloseable {
        /**
         * Cancel the subscription and release any underlying resources.
         */
        @Override
        void close();
    }
}
