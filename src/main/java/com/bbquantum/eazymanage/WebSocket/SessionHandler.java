package com.bbquantum.eazymanage.WebSocket;

import com.bbquantum.eazymanage.DTO.WebSocket.Broadcast;
import com.bbquantum.eazymanage.Services.WebSocketService;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import static com.bbquantum.eazymanage.Services.WebSocketService.*;

public class SessionHandler extends StompSessionHandlerAdapter{

    private final WebSocketService webSocketService;

    public SessionHandler() {
        this.webSocketService = null;
    }

    public SessionHandler(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        // All three subscriptions; each gets its own frame handler so the
        // payload type can be expressed declaratively.
        session.subscribe(TOPIC_BROADCAST_ALL,    new TypedHandler<>(Broadcast.class,    WebSocketService::onBroadcast));
        session.subscribe(QUEUE_BROADCAST_USER,   new TypedHandler<>(Broadcast.class,    WebSocketService::onBroadcast));
        session.subscribe(TOPIC_ACTIVE_SESSIONS,  new TypedHandler<>(ListOfActiveSessions.class, WebSocketService::onPresence));

        webSocketService.startPing();
    }

    @Override
    public void handleTransportError(StompSession session, Throwable exception) {
        exception.printStackTrace();
        webSocketService.scheduleReconnect();
    }

    @Override
    public void handleException(StompSession session, StompCommand command,
                                StompHeaders headers, byte[] payload, Throwable exception) {
        exception.printStackTrace();
        // Don't reconnect on payload-level errors; only on transport failures.
    }
}
