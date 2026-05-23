package com.bbquantum.eazymanage.WebSocket;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.function.Consumer;

public class TypedHandler<T> implements StompFrameHandler {

    private final Class<T> payloadType;

    private final Consumer<T> consumer;

    public TypedHandler(Class<T> payloadType, Consumer<T> consumer) {
        this.payloadType = payloadType;
        this.consumer = consumer;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return payloadType;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        if (payload != null) consumer.accept((T) payload);
    }
}
