package com.example.valkey.core;

/**
 * Valkey の pub/sub にメッセージを公開するためのコンポーネントを表すインターフェース。
 */
public interface MessagePublisher {
    /**
     * 指定されたチャネルにメッセージを公開する。
     *
     * @param channel 送信先チャネル
     * @param message 送信するメッセージ
     * @return publish に反応した subscriber 数
     * @throws ValkeyUnavailableException Valkey が利用できない場合
     * @throws PublishException publish 処理に失敗した場合
     * @throws NullPointerException channel または message が null の場合
     */
    long publish(String channel, String message);
}
