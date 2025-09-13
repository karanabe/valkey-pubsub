package com.example.valkey.core;

public interface MessagePublisher {
    /**
     * @return publish に反応した subscriber 数
     */
    long publish(String channel, String message);
}
