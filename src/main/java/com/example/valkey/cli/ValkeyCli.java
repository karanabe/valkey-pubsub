package com.example.valkey.cli;

import com.example.valkey.core.MessagePublisher;
import com.example.valkey.core.MessageSubscriber;
import com.example.valkey.jedis.JedisMessagePublisher;
import com.example.valkey.jedis.JedisMessageSubscriber;
import com.example.valkey.jedis.JedisPools;
import com.example.valkey.jedis.ValkeyProps;
import java.util.UUID;
import java.util.concurrent.Callable;
import org.slf4j.MDC;
import picocli.CommandLine;
import picocli.CommandLine.*;
import redis.clients.jedis.JedisPool;

@Command(
        name = "valkey-cli",
        version = "valkey-pubsub 0.1.0",
        mixinStandardHelpOptions = true,
        subcommands = {ValkeyCli.Publish.class, ValkeyCli.Subscribe.class})
public final class ValkeyCli implements Runnable {

    @Option(names = "--trace-id", description = "trace id for logs (default: random uuid)")
    String traceId;

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new ValkeyCli());
        cmd.setExecutionExceptionHandler(new ShortErrorHandler());
        int exit = cmd.execute(args);
        System.exit(exit);
    }

    static final class ShortErrorHandler implements IExecutionExceptionHandler {
        @Override
        public int handleExecutionException(
                Exception ex, CommandLine commandLine, ParseResult parseResult) {
            ExceptionReporter.print(ex);
            return commandLine.getCommandSpec().exitCodeOnExecutionException();
        }
    }

    @Override
    public void run() {
        // ルートで何もしない（help表示は picocli が対応）
    }

    static ValkeyProps buildProps(Common c) {
        return new ValkeyProps(
                c.host,
                c.port,
                c.ssl,
                c.username,
                c.password,
                c.database,
                c.timeoutMillis,
                c.poolMaxTotal,
                c.poolMaxIdle,
                c.poolMinIdle);
    }

    abstract static class Common {
        @Option(
                names = {"-h", "--host"},
                defaultValue = "127.0.0.1")
        String host;

        @Option(
                names = {"-p", "--port"},
                defaultValue = "6379")
        int port;

        @Option(names = "--ssl", defaultValue = "false")
        boolean ssl;

        @Option(names = "--username")
        String username;

        @Option(names = "--password")
        String password;

        @Option(names = "--database", defaultValue = "0")
        int database;

        @Option(names = "--timeout-ms", defaultValue = "5000")
        int timeoutMillis;

        @Option(names = "--pool-max-total", defaultValue = "32")
        int poolMaxTotal;

        @Option(names = "--pool-max-idle", defaultValue = "16")
        int poolMaxIdle;

        @Option(names = "--pool-min-idle", defaultValue = "4")
        int poolMinIdle;
    }

    @Command(name = "publish", description = "Publish a message to a channel")
    static final class Publish extends Common implements Callable<Integer> {
        @ParentCommand ValkeyCli parent;

        @Parameters(index = "0", paramLabel = "CHANNEL")
        String channel;

        @Parameters(index = "1", paramLabel = "MESSAGE")
        String message;

        @Override
        public Integer call() {
            String tid = parent.traceId != null ? parent.traceId : UUID.randomUUID().toString();
            MDC.put("traceId", tid);
            try (JedisPool pool = JedisPools.create(buildProps(this))) {
                MessagePublisher pub = new JedisMessagePublisher(pool);
                long n = pub.publish(channel, message);
                System.out.printf("OK receivers=%d traceId=%s%n", n, tid);
                return 0;
            } catch (Exception e) {
                ExceptionReporter.print(e);
                return 1;
            } finally {
                MDC.remove("traceId");
            }
        }
    }

    @Command(name = "subscribe", description = "Subscribe and print messages")
    static final class Subscribe extends Common implements Callable<Integer> {
        @ParentCommand ValkeyCli parent;

        @Parameters(index = "0", paramLabel = "CHANNEL")
        String channel;

        @Override
        public Integer call() {
            String tid = parent.traceId != null ? parent.traceId : UUID.randomUUID().toString();
            MDC.put("traceId", tid);
            var props = buildProps(this);
            MessageSubscriber sub = new JedisMessageSubscriber(props);
            var handle =
                    sub.subscribe(
                            channel,
                            (ch, msg) ->
                                    System.out.printf("ch=%s msg=%s traceId=%s%n", ch, msg, tid));
            Runtime.getRuntime().addShutdownHook(new Thread(handle::close));
            System.out.println("Subscribed. Ctrl+C to quit.");
            try {
                Thread.currentThread().join();
                return 0;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return 130;
            } finally {
                MDC.remove("traceId");
            }
        }
    }
}
