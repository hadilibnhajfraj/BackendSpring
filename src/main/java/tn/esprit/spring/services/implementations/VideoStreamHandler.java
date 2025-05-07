package tn.esprit.spring.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        Map<String, Object> data = objectMapper.readValue(message.getPayload(), new TypeReference<>() {});

        String type = (String) data.get("type");

        switch (type) {
            case "startLive":
                String userId = (String) data.get("userId");
                String liveStartedMessage = "{\"type\":\"liveStarted\",\"userId\":\"" + userId + "\"}";
                broadcastMessage(new TextMessage(liveStartedMessage));
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
            default:
                // Handle any unexpected message type
                System.err.println("Unknown message type: " + type);
                break;
        }
    }


    private void broadcastMessage(TextMessage message) throws IOException {
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                try {
                    s.sendMessage(message);
                } catch (IOException e) {
                    // Log or handle error if needed
                    System.err.println("Error sending message: " + e.getMessage());
                }
            }
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }
}
