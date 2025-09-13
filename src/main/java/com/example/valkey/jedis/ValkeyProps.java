package com.example.valkey.jedis;

public record ValkeyProps(
    String host,
    int port,
    boolean ssl,
    String username,
    String password,
    int database,
    int timeoutMillis,
    int poolMaxTotal,
    int poolMaxIdle,
    int poolMinIdle) {

  public static ValkeyProps defaults() {
    return new ValkeyProps("127.0.0.1", 6379, false, null, null, 0, 5000, 32, 16, 4);
  }
}
