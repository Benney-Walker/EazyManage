package com.bbquantum.eazymanage.Services;

import com.bbquantum.eazymanage.Constants.NotificationPriority;
import com.bbquantum.eazymanage.DTO.WebSocket.ActiveSession;
import com.bbquantum.eazymanage.DTO.WebSocket.Broadcast;
import com.bbquantum.eazymanage.DTO.WebSocket.BroadcastRequest;
import com.bbquantum.eazymanage.Shell.NotificationCenter;
import com.bbquantum.eazymanage.Utitity.SessionManager;
import com.bbquantum.eazymanage.Utitity.UtilityClass;
import com.bbquantum.eazymanage.WebSocket.SessionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class WebSocketService {

    public static final String STOMP_URL              = deriveWsUrl();
    public static final String TOPIC_BROADCAST_ALL    = "/topic/broadcasts";
    public static final String QUEUE_BROADCAST_USER   = "/user/queue/broadcasts";
    public static final String TOPIC_ACTIVE_SESSIONS  = "/topic/active-sessions";
    public static final String APP_BROADCAST_SEND     = "/app/broadcasts/send";
    public static final String APP_ACTIVE_SESSIONS_PING = "/app/active-sessions/ping";

    public static final long PING_INTERVAL_SECONDS    = 30;
    public static final long INITIAL_RECONNECT_DELAY_MS = 2_000;
    public static final long MAX_RECONNECT_DELAY_MS   = 30_000;

    // ------------------------------------------------------------------
    // Singleton
    // ------------------------------------------------------------------
    private static final WebSocketService INSTANCE = new WebSocketService();
    public static WebSocketService getInstance() { return INSTANCE; }

    private final WebSocketStompClient stompClient;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ScheduledExecutorService scheduler;

    private volatile StompSession session;
    private volatile ScheduledFuture<?> pingTask;
    private volatile ScheduledFuture<?> reconnectTask;
    private final AtomicBoolean shouldRun = new AtomicBoolean(false);
    private final AtomicInteger reconnectAttempt = new AtomicInteger(0);

    private WebSocketService() {
        // Standard JSR-356 client; tomcat-embed-websocket provides the impl.
        StandardWebSocketClient transport = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(transport);

        // Jackson converter handles {Broadcast, ActiveSession, ...} ↔ JSON.
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        stompClient.setMessageConverter(converter);

        // The scheduler is needed for receipt-handling and reconnect timing.
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(2);
        taskScheduler.setThreadNamePrefix("eazymanage-stomp-");
        taskScheduler.setDaemon(true);
        taskScheduler.initialize();
        stompClient.setTaskScheduler(taskScheduler);
        this.scheduler = taskScheduler.getScheduledExecutor();
    }

    // ==================================================================
    // Public API
    // ==================================================================

    /**
     * Initiate the connection if not already connected. Safe to call multiple
     * times — duplicate calls are no-ops while a session is alive.
     */
    public synchronized void connect() {
        if (shouldRun.getAndSet(true)) return;
        connectInternal();
    }

    /**
     * Tear down the connection cleanly. Cancels pending reconnects and the
     * ping schedule. Safe to call when not connected.
     */
    public synchronized void disconnect() {
        shouldRun.set(false);
        cancelPing();
        cancelReconnect();
        StompSession s = session;
        session = null;
        if (s != null && s.isConnected()) {
            try { s.disconnect(); } catch (Exception ignored) {}
        }
        PresenceTracker.getInstance().clear();
    }

    public boolean isConnected() {
        StompSession s = session;
        return s != null && s.isConnected();
    }

    /**
     * Publish a new broadcast. The server will fan it out to the configured
     * audience and the sender will receive their own message back via the
     * normal subscription path — which makes "sent" feedback automatic.
     */
    public void sendBroadcast(BroadcastRequest request) {
        StompSession s = session;
        if (s == null || !s.isConnected()) return;
        s.send(APP_BROADCAST_SEND, request);
    }

    // ==================================================================
    // Connection lifecycle
    // ==================================================================

    private void connectInternal() {
        if (!shouldRun.get()) return;

        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add("Authorization", "Bearer " + SessionManager.getToken());

        try {
            // connectAsync is the blocking-safe alternative the JavaDoc steers
            // you to; .get() bounds the wait at the configured handshake timeout.
            session = stompClient.connectAsync(
                    STOMP_URL,
                    handshakeHeaders,
                    connectHeaders,
                    new SessionHandler()).get(10, TimeUnit.SECONDS);

            // Connected — reset backoff so the next failure starts fresh.
            reconnectAttempt.set(0);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            ex.printStackTrace();
            session = null;
            scheduleReconnect();
        }
    }

    public void scheduleReconnect() {
        if (!shouldRun.get()) return;
        cancelReconnect();

        int attempt = reconnectAttempt.incrementAndGet();
        long delay = Math.min(MAX_RECONNECT_DELAY_MS,
                INITIAL_RECONNECT_DELAY_MS * (1L << Math.min(5, attempt - 1)));

        reconnectTask = scheduler.schedule(this::connectInternal,
                delay, TimeUnit.MILLISECONDS);
    }

    private void cancelReconnect() {
        ScheduledFuture<?> t = reconnectTask;
        reconnectTask = null;
        if (t != null) t.cancel(false);
    }

    private void cancelPing() {
        ScheduledFuture<?> t = pingTask;
        pingTask = null;
        if (t != null) t.cancel(false);
    }

    public void startPing() {
        cancelPing();
        pingTask = scheduler.scheduleAtFixedRate(() -> {
            StompSession s = session;
            if (s != null && s.isConnected()) {
                try {
                    s.send(APP_ACTIVE_SESSIONS_PING, "");
                } catch (Exception ignored) {
                    // Failures here flow into the session's exception handler
                    // and the reconnect path.
                }
            }
        }, PING_INTERVAL_SECONDS, PING_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    // ==================================================================
    // Payload handlers
    // ==================================================================

    public static void onBroadcast(Broadcast b) {
        if (b == null) return;
        NotificationPriority priority = mapPriority(b.getPriority());
        // Fold the broadcast into the existing in-app notification list. The
        // bell badge, popup, and toast surfaces are already bound to this.
        NotificationCenter.getInstance().post(
                safe(b.getTitle()),
                bodyWithAttribution(b),
                priority);
    }

    public static void onPresence(List<ActiveSession> sessions) {
        PresenceTracker.getInstance().replaceWith(sessions);
    }

    private static NotificationPriority mapPriority(String s) {
        if (s == null) return NotificationPriority.INFO;
        return switch (s.toUpperCase()) {
            case "URGENT"  -> NotificationPriority.URGENT;
            case "WARNING" -> NotificationPriority.WARNING;
            default        -> NotificationPriority.INFO;
        };
    }

    private static String bodyWithAttribution(Broadcast b) {
        String body = safe(b.getBody());
        String sender = b.getSenderName();
        if (sender == null || sender.isBlank()) return body;
        return body + "\n— " + sender;
    }

    private static String safe(String s) { return s == null ? "" : s; }

    /**
     * Derive a ws:// URL from the existing HTTP base. {@code UtilityClass.BASE_URL}
     * is the canonical source of truth for "where the backend lives" — using
     * it here avoids drift between phases.
     */
    private static String deriveWsUrl() {
        String base = UtilityClass.BASE_URL;
        // Convert {http,https} → {ws,wss} and strip the trailing /api segment
        // since the STOMP endpoint is configured at the application root.
        String ws = base.replaceFirst("^http", "ws");
        if (ws.endsWith("/api")) ws = ws.substring(0, ws.length() - 4);
        if (ws.endsWith("/"))    ws = ws.substring(0, ws.length() - 1);
        return ws + "/ws";
    }
}
