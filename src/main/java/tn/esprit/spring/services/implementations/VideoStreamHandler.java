package tn.esprit.spring.services.implementations;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.*;

@Component
public class VideoStreamHandler extends TextWebSocketHandler {

    private static final Set<WebSocketSession> sessions = new HashSet<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Map<String, Object> data = objectMapper.readValue(message.getPayload(), new TypeReference<Map<String, Object>>() {});

        String type = (String) data.get("type");

        switch (type) {
            case "startLive":
                String userId = (String) data.get("userId");
                broadcastMessage(new TextMessage("{\"type\":\"liveStarted\",\"userId\":\"" + userId + "\"}"));
                break;
            case "stopLive":
                broadcastMessage(new TextMessage("{\"type\":\"liveStopped\"}"));
                break;
            case "comment":
                broadcastMessage(message);
                break;
            case "offer":
            case "answer":
            case "ice-candidate":
                broadcastMessage(message);
                break;
        }
    }


    private void broadcastMessage(TextMessage message) throws IOException {
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) s.sendMessage(message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }
}

