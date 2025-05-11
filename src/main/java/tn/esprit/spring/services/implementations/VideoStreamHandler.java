package tn.esprit.spring.services.implementations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tn.esprit.spring.entities.Commentaire;
import tn.esprit.spring.entities.Publication;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.repositories.PublicationRepository;
import tn.esprit.spring.repositories.UserRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class VideoStreamHandler extends TextWebSocketHandler {

    private static final Set<WebSocketSession> sessions = new HashSet<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final CommentaireService commentaireService;
    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;
    private final UserService userService;
    private final PublicationService publicationService;
    private final JwtService jwtService;
    private Integer currentPublicationId; // Pour stocker le dernier ID de publication reçu

    public VideoStreamHandler(CommentaireService commentaireService,
                              UserRepository userRepository,
                              UserService userService,
                              PublicationService publicationService,
                              PublicationRepository publicationRepository,
                              JwtService jwtService) {
        this.commentaireService = commentaireService;
        this.userRepository = userRepository;
        this.publicationRepository = publicationRepository;
        this.userService = userService;
        this.publicationService = publicationService;
        this.jwtService = jwtService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("Nouvelle connexion WebSocket établie : " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Map<String, Object> data;

        // Affichage du message reçu pour le débogage
        System.out.println("Message reçu: " + message.getPayload());

        try {
            // Tentative de parsing du message WebSocket reçu
            data = objectMapper.readValue(message.getPayload(), new TypeReference<>() {});
        } catch (Exception e) {
            // Si le parsing échoue, afficher un message d'erreur et ne pas continuer
            System.err.println("Erreur de parsing JSON, message invalide: " + message.getPayload());
            e.printStackTrace();
            return;
        }

        // Vérification du type de message
        String type = (String) data.get("type");

        // Selon le type de message, effectuer l'action correspondante
        switch (type) {
            case "startLive" -> handleStartLive(data);
            case "stopLive" -> handleStopLive();
            case "comment" -> handleComment(data);
            case "publication-id" -> handlePublicationId(data);
            case "offer", "answer", "ice-candidate" -> handleIceCandidates(message);
            default -> System.err.println("Type de message inconnu: " + type);
        }
    }

    private void handleStartLive(Map<String, Object> data) {
        String userId = (String) data.get("userId");
        String liveStartedMessage = "{\"type\":\"liveStarted\",\"userId\":\"" + userId + "\"}";
        broadcastMessage(new TextMessage(liveStartedMessage));
    }

    private void handleStopLive() {
        broadcastMessage(new TextMessage("{\"type\":\"liveStopped\"}"));
    }

    private void handleComment(Map<String, Object> data) {
        // Ajouter publicationId si il est manquant et utiliser le currentPublicationId
        String publicationIdStr = (String) data.get("publicationId");
        if (publicationIdStr == null && currentPublicationId != null) {
            publicationIdStr = String.valueOf(currentPublicationId);
        }

        // Sauvegarder le commentaire en base de données
        saveCommentToDatabase(data, publicationIdStr);
        // Diffuser le message à tous les utilisateurs connectés
        broadcastMessage(new TextMessage(data.toString())); // Utilisation du même message pour la diffusion
    }

    private void handleIceCandidates(TextMessage message) {
        // Diffuser les messages des ICE candidates à tous les utilisateurs connectés
        broadcastMessage(message);
    }

    private void saveCommentToDatabase(Map<String, Object> data, String publicationIdStr) {
        try {
            // Récupération du texte du commentaire et des autres champs nécessaires
            Object rawCommentData = data.get("data");
            String commentText = null;
            if (rawCommentData instanceof String) {
                commentText = (String) rawCommentData;
            }

            // Log des informations reçues
            System.out.println("Message reçu : " + data);

            // Vérification des champs requis
            String userEmail = (String) data.get("userId");  // Récupérer l'email de l'utilisateur
            if (userEmail == null) {
                System.out.println("Champs manquant : userId (email)");
            } else {
                System.out.println("userEmail : " + userEmail);
            }

            if (commentText == null || commentText.trim().isEmpty()) {
                System.out.println("Champs manquant : commentaire (data)");
            } else {
                System.out.println("Commentaire : " + commentText);
            }

            // Si un champ est manquant, on arrête le traitement
            if (publicationIdStr == null || userEmail == null || commentText == null || commentText.trim().isEmpty()) {
                System.out.println("Erreur : champs manquants pour l'utilisateur ou la publication");
                return;
            }

            // Parsing de publicationId
            Integer publicationId = Integer.parseInt(publicationIdStr);

            // Récupérer l'utilisateur à partir de son email
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email : " + userEmail));

            // Récupération de la publication
            Publication publication = publicationRepository.findById(publicationId)
                    .orElseThrow(() -> new RuntimeException("Publication non trouvée"));

            // Création du commentaire
            Commentaire commentaire = new Commentaire(
                    null, // ID auto-généré
                    commentText,
                    LocalDate.now(),
                    0, // Statut ou évaluation du commentaire
                    user,
                    publication
            );

            // Sauvegarde du commentaire
            commentaireService.save(commentaire);
            String responseMessage = "{\"type\":\"commentAdded\",\"publicationId\":\"" + publicationId + "\"}";
            broadcastMessage(new TextMessage(responseMessage));

        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement du commentaire : " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void handlePublicationId(Map<String, Object> data) {
        Object pubIdObj = data.get("data");
        if (pubIdObj != null) {
            String publicationId = pubIdObj.toString().trim();
            try {
                Long.parseLong(publicationId); // Validation simple de l'ID
                currentPublicationId = Integer.parseInt(publicationId); // Enregistrement de publicationId
                System.out.println("ID de publication reçu : " + publicationId);
            } catch (NumberFormatException e) {
                System.err.println("ID de publication invalide : " + publicationId);
            }
        } else {
            System.err.println("Champ 'data' manquant pour publication-id.");
        }
    }

    private void broadcastMessage(TextMessage message) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    System.err.println("Erreur d'envoi du message : " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("Connexion WebSocket fermée : " + session.getId());
    }
}
