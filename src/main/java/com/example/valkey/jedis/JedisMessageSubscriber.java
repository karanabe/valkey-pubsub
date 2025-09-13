// jedis/JedisMessageSubscriber.java
package com.example.valkey.jedis;

import com.example.valkey.core.MessageSubscriber;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * {@link MessageSubscriber} implementation that uses a dedicated Jedis connection
 * to subscribe to a channel and dispatch messages to a handler.
 */
public final class JedisMessageSubscriber implements MessageSubscriber {
    private static final Logger log = LoggerFactory.getLogger(JedisMessageSubscriber.class);
    private final ValkeyProps props;

    /**
     * Creates a subscriber configured by the given Valkey properties.
     *
     * @param props connection properties used to configure Jedis
     */
    public JedisMessageSubscriber(ValkeyProps props) {
        this.props = Objects.requireNonNull(props);
    }

    /**
     * Subscribe to the given channel asynchronously. A dedicated daemon thread
     * listens for messages and invokes the supplied handler.
     *
     * @param channel channel name to subscribe to
     * @param handler callback invoked for each received message
     * @return handle that stops the subscription when closed
     */
    @Override
    public SubscriptionHandle subscribe(String channel, Handler handler) {
        var stop = new AtomicBoolean(false);

        var cfg =
                DefaultJedisClientConfig.builder()
                        .ssl(props.ssl())
                        .user(props.username())
                        .password(props.password())
                        .database(props.database())
                        .timeoutMillis(props.timeoutMillis())
                        .build();
        var hap = new HostAndPort(props.host(), props.port());

        var pubsub =
                new JedisPubSub() {
                    @Override
                    public void onMessage(String ch, String msg) {
                        try {
                            handler.onMessage(ch, msg);
                        } catch (Throwable t) {
                            log.error("subscriber handler threw", t);
                        }
                    }
                };

        Thread t =
                new Thread(
                        () -> {
                            try (Jedis j = new Jedis(hap, cfg)) {
                                log.info("subscribe start channel='{}'", channel);
                                j.subscribe(pubsub, channel); // ブロッキング
                            } catch (Exception e) {
                                if (!stop.get()) log.error("subscribe loop error", e);
                            } finally {
                                log.info("subscribe end channel='{}'", channel);
                            }
                        },
                        "valkey-sub-" + channel);

        t.setDaemon(true);
        t.start();

        return () -> {
            stop.set(true);
            try {
                pubsub.unsubscribe();
            } catch (Exception ignore) {
            }
            try {
                t.join(2000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        };
    }
}
