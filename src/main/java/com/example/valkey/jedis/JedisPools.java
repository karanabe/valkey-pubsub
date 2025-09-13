package com.example.valkey.jedis;

import java.time.Duration;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPool;

public final class JedisPools {
    private JedisPools() {}

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
