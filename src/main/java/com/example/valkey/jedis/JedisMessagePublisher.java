package com.example.valkey.jedis;

import com.example.valkey.core.MessagePublisher;
import com.example.valkey.core.PublishException;
import com.example.valkey.core.ValkeyUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;

public final class JedisMessagePublisher implements MessagePublisher {
    private static final Logger log = LoggerFactory.getLogger(JedisMessagePublisher.class);
    private final JedisPool pool;

    public JedisMessagePublisher(JedisPool pool) {
        this.pool = pool;
    }

    @Override
    public long publish(String channel, String message) {
        try (Jedis j = pool.getResource()) {
            Long recv = j.publish(channel, message);
            long n = recv == null ? 0L : recv;
            if (n == 0) {
                log.warn(
                        "published to '{}' but no subscribers (traceId={})",
                        channel,
                        MDC.get("traceId"));
            } else {
                log.info("published to '{}' receivers={}", channel, n);
            }
            return n;
        } catch (JedisConnectionException e) {
            log.error("valkey unavailable on publish to '{}': {}", channel, e.toString());
            throw new ValkeyUnavailableException("valkey unavailable", e);
        } catch (JedisDataException e) {
            log.error("publish data error on '{}'", channel, e);
            throw new PublishException("publish data error", e);
        } catch (JedisException e) {
            log.error("publish data error on '{}'", channel, e);
            throw new PublishException("jedis exception", e);
        } catch (Exception e) {
            log.error("publish failed on '{}'", channel, e);
            throw new PublishException("publish failed", e);
        }
    }
}
