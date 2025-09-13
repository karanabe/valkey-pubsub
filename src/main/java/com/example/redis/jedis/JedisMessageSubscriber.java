// jedis/JedisMessageSubscriber.java
package com.example.redis.jedis;

import com.example.redis.core.MessageSubscriber;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public final class JedisMessageSubscriber implements MessageSubscriber {
  private static final Logger log = LoggerFactory.getLogger(JedisMessageSubscriber.class);
  private final RedisProps props;

  public JedisMessageSubscriber(RedisProps props) { this.props = Objects.requireNonNull(props); }

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
            "redis-sub-" + channel);

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
