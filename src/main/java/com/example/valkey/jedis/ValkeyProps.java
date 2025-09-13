package com.example.valkey.jedis;

/**
 * Connection configuration for Valkey.
 *
 * @param host Valkey server hostname
 * @param port Valkey server port
 * @param ssl whether to enable TLS
 * @param username username for authentication or {@code null}
 * @param password password for authentication or {@code null}
 * @param database zero-indexed database number
 * @param timeoutMillis socket timeout in milliseconds
 * @param poolMaxTotal maximum total connections in the pool
 * @param poolMaxIdle maximum idle connections in the pool
 * @param poolMinIdle minimum idle connections in the pool
 */
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

    /**
     * Create a {@code ValkeyProps} instance using library defaults. Defaults use localhost on port
     * {@code 6379}, disable SSL, no authentication, database {@code 0}, a 5-second timeout, and a
     * connection pool of 32 maximum total connections with 16 maximum idle and 4 minimum idle.
     *
     * @return default property values
     */
    public static ValkeyProps defaults() {
        return new ValkeyProps("127.0.0.1", 6379, false, null, null, 0, 5000, 32, 16, 4);
    }
}
