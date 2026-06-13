package org.mryrt.airbnb.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.transport.stomp.Stomp;
import org.apache.activemq.transport.stomp.StompConnection;
import org.apache.activemq.transport.stomp.StompFrame;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class StompNotificationSender implements InitializingBean, DisposableBean {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String destination;
    private final ObjectMapper objectMapper;

    private StompConnection connection;

    @Override
    public void afterPropertiesSet() throws Exception {
        connect();
    }

    private void connect() throws Exception {
        closeQuietly();
        StompConnection c = new StompConnection();
        c.open(host, port);
        c.connect(username, password);
        connection = c;
        log.info("STOMP connection established to {}:{}", host, port);
    }

    private void closeQuietly() {
        if (connection == null) {
            return;
        }
        try {
            connection.disconnect();
        } catch (Exception ignored) {
        }
        try {
            connection.close();
        } catch (Exception ignored) {
        }
        connection = null;
    }

    public synchronized void send(NotificationPayload payload) throws Exception {
        String json = objectMapper.writeValueAsString(payload);
        try {
            doSend(json);
        } catch (IOException e) {
            log.warn("STOMP send failed: {}. Reconnecting and retrying...", e.getMessage());
            connect();
            doSend(json);
        }
    }

    private void doSend(String json) throws Exception {
        String receiptId = UUID.randomUUID().toString();
        log.info("Sending STOMP message to '{}', receipt-id={}", destination, receiptId);
        connection.send(destination, json, null, new HashMap<>(Map.of(
                "content-type", "application/json",
                "destination-type", "ANYCAST",
                "receipt", receiptId,
                "_type", "email"
        )));
        StompFrame response = connection.receive(5000);
        if (Stomp.Responses.RECEIPT.equals(response.getAction())) {
            log.info("STOMP RECEIPT confirmed for receipt-id={}", receiptId);
        } else {
            log.error("STOMP broker responded with {} instead of RECEIPT: {}", response.getAction(), response.getBody());
            throw new RuntimeException("STOMP send not confirmed: " + response.getAction() + " - " + response.getBody());
        }
    }

    @Override
    public void destroy() throws Exception {
        closeQuietly();
    }
}
