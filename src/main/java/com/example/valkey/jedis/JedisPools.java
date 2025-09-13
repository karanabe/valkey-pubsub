package com.example.valkey.jedis;

import java.time.Duration;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPool;

/**
 * Factory for building configured {@link JedisPool} instances.
 *
 * <p>Each pool is created from the supplied {@link ValkeyProps} so that callers do not need to deal
 * with the lower level Jedis configuration classes.
 */
public final class JedisPools {
    private JedisPools() {}

    /**
     * Create a {@link JedisPool} configured from the provided {@link ValkeyProps}.
     *
     * <p>The following options are applied:
     *
     * <ul>
     *   <li>{@link ValkeyProps#host()} and {@link ValkeyProps#port()} for the node address
     *   <li>{@link ValkeyProps#ssl()} to enable TLS
     *   <li>{@link ValkeyProps#username()} and {@link ValkeyProps#password()} for authentication
     *   <li>{@link ValkeyProps#database()} for the database index
     *   <li>{@link ValkeyProps#timeoutMillis()} for connection and socket timeouts
     *   <li>{@link ValkeyProps#poolMaxTotal()}, {@link ValkeyProps#poolMaxIdle()}, and {@link
     *       ValkeyProps#poolMinIdle()} for pool sizing
     * </ul>
     *
     * @param p valkey connection and pooling options
     * @return a ready to use {@link JedisPool}
     */
    public static JedisPool create(ValkeyProps p) {
        var hap = new HostAndPort(p.host(), p.port());
        JedisClientConfig cfg =
                DefaultJedisClientConfig.builder()
                        .ssl(p.ssl())
                        .user(p.username())
                        .password(p.password())
                        .database(p.database())
                        .timeoutMillis(p.timeoutMillis())
                        .build();

        var pc = new GenericObjectPoolConfig<redis.clients.jedis.Jedis>();
        pc.setMaxTotal(p.poolMaxTotal());
        pc.setMaxIdle(p.poolMaxIdle());
        pc.setMinIdle(p.poolMinIdle());
        pc.setTestOnBorrow(true);
        pc.setTestWhileIdle(true);
        pc.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
        pc.setMinEvictableIdleDuration(Duration.ofMinutes(1));

        return new JedisPool(pc, hap, cfg);
    }
}
