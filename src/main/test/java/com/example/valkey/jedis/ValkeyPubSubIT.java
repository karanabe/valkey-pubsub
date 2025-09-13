package com.example.valkey.jedis;

import static org.junit.jupiter.api.Assertions.*;

import com.example.valkey.core.MessagePublisher;
import com.example.valkey.core.MessageSubscriber;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import redis.clients.jedis.JedisPool;

@Testcontainers
class ValkeyPubSubIT {

  @Container
  static GenericContainer<?> valkey =
      new GenericContainer<>("valkey/valkey:7-alpine").withExposedPorts(6379);

  @Test
  void publish_and_receive() throws Exception {
      var props =
          new ValkeyProps(valkey.getHost(), valkey.getFirstMappedPort(), false, null, null, 0, 5000, 8, 4, 1);

    try (JedisPool pool = JedisPools.create(props)) {
        MessagePublisher pub = new JedisMessagePublisher(pool);
        MessageSubscriber sub = new JedisMessageSubscriber(props);

      String channel = "it";
      var latch = new CountDownLatch(1);

      var h =
          sub.subscribe(
              channel,
              (ch, msg) -> {
                if ("hello".equals(msg)) latch.countDown();
              });

      long receivers = pub.publish(channel, "hello");
      assertTrue(receivers >= 1);
      assertTrue(latch.await(2, TimeUnit.SECONDS));
      h.close();
    }
  }
}
