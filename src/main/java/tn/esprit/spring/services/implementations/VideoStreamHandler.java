package tn.esprit.spring.services.implementations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tn.esprit.spring.entities.Commentaire;
import tn.esprit.spring.services.implementations.CommentaireService;  // Assurez-vous que ce service existe.

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class VideoStreamHandler extends TextWebSocketHandler {

    private static final Set<WebSocketSession> sessions = new HashSet<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CommentaireService commentaireService;  // Service pour gérer les commentaires

    // Injecter CommentaireService dans le constructeur
    public VideoStreamHandler(CommentaireService commentaireService) {
        this.commentaireService = commentaireService;
    }

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
                // Enregistrer le commentaire dans la base de données
                saveCommentToDatabase(data);

                // Diffuser le commentaire à tous les clients
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

    private void saveCommentToDatabase(Map<String, Object> data) {
        // Récupérer les informations du commentaire depuis le message
        String texte = (String) data.get("data");
        String userId = (String) data.get("userId");  // Assurez-vous que vous avez un ID d'utilisateur pour le commentaire
        Integer publicationId = (Integer) data.get("publicationId");  // ID de la publication (si nécessaire)

        // Créer un objet Commentaire
        Commentaire commentaire = new Commentaire();
        commentaire.setTexte(texte);
        commentaire.setDateCommentaire(LocalDate.now());

        // Ici, vous devez récupérer l'utilisateur et la publication depuis la base de données, par exemple via un service
        // Exemple :
        // User user = userService.findById(userId);
        // Publication publication = publicationService.findById(publicationId);
        // commentaire.setUser(user);
        // commentaire.setPublication(publication);

        // Sauvegarder le commentaire
        commentaireService.save(commentaire);
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
