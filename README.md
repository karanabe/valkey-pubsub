<br />
<h1 align="center">valkey-pubsub</h1>
<h3 align="center">Sample code for Jedis pub/sub</h3>
<br />
<br />

### Project Description
valkey-pubsub provides a minimal example of how to use the Jedis client to publish and subscribe to messages on a Valkey (Redis) server. The project wraps Jedis with simple interfaces for message publishing and subscribing and exposes a command-line utility built with Picocli.

### Features
- Publish messages to a channel using Jedis
- Subscribe to channels and print incoming messages
- CLI options for host, port, SSL, authentication, database, timeouts, and connection pooling
- Structured logging with trace IDs
- Executable fat JAR via Maven Shade plugin

### Prerequisites
- Java 21 or later
- Maven 3.8+ (for building)

### Build
```bash
mvn clean package
```
This produces `target/valkey-pubsub-0.1.0.jar`.

### Usage
Publish a JSON payload to a channel:

```bash
java -jar target/valkey-pubsub-0.1.0.jar publish --host 127.0.0.1 --port 6379 test '{"robotId":"R1"}'
```

Subscribe to a channel:

```bash
java -jar target/valkey-pubsub-0.1.0.jar subscribe --host 127.0.0.1 --port 6379 test
```

### Programmatic Example
The project can also be used directly from application code. The following class keeps a single Jedis pool and exposes a simple `publish` method:

```java
// MyPublisher.java
import com.example.valkey.core.MessagePublisher;
import com.example.valkey.jedis.*;
import redis.clients.jedis.JedisPool;

public final class MyPublisher implements AutoCloseable {
    private final JedisPool pool;
    private final MessagePublisher publisher;

    public MyPublisher(ValkeyProps props) {
        this.pool = JedisPools.create(props);
        this.publisher = new JedisMessagePublisher(pool);
    }

    public long publish(String channel, String message) {
        return publisher.publish(channel, message);
    }

    @Override
    public void close() {
        pool.close();
    }
}
```

Call it from other code and handle the runtime exceptions declared by `MessagePublisher`:

```java
ValkeyProps props = ValkeyProps.defaults();
try (MyPublisher pub = new MyPublisher(props)) {
    pub.publish("test", "{\"robotId\":\"R1\"}");
} catch (ValkeyUnavailableException | PublishException e) {
    // handle connection or publish failures
} catch (NullPointerException e) {
    // handle null channel or message
}
```

### License
This project is licensed under the MIT License. See [LICENSE](LICENSE).

