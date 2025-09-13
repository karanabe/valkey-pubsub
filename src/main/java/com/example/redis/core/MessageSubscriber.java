package com.example.redis.core;

public interface MessageSubscriber {
  @FunctionalInterface
  interface Handler {
    void onMessage(String channel, String message);
  }

  /** 非同期で購読を開始し、停止用ハンドルを返す */
  SubscriptionHandle subscribe(String channel, Handler handler);

  interface SubscriptionHandle extends AutoCloseable {
    @Override void close();
  }
}
