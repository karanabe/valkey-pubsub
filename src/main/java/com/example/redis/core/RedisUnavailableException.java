package com.example.redis.core;

public class RedisUnavailableException extends RuntimeException {
  public RedisUnavailableException(String message, Throwable cause) { super(message, cause); }
}
