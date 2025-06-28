package tn.esprit.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.PublicationRepository;
import tn.esprit.spring.repositories.ReactionPublicationRepository;
import tn.esprit.spring.repositories.UserRepository;
import tn.esprit.spring.services.implementations.CommentaireService;
import tn.esprit.spring.services.implementations.JwtService;
import tn.esprit.spring.services.interfaces.PublicationInterface;
import org.springframework.mock.web.MockMultipartFile;

// JUnit
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

// Mockito
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

// Spring
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;


class PublicationControllerTest {

    @InjectMocks
    private PublicationController publicationController;

    @Mock
    private PublicationInterface publicationService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentaireService commentaireService;

    @Mock
    private PublicationRepository publicationRepository;

    @Mock
    private ReactionPublicationRepository reactionPublicationRepository;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testAddPublication_PresseUser_ReturnsCreatedPublication() throws Exception {
        String json = "{\"contenu\":\"Test\"}";
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "dummy".getBytes());

        User user = new User();
        user.setEmail("presse@example.com");

        Publication pub = new Publication();
        pub.setContenu("Test");
        pub.setUser(user);

        when(jwtService.getAuthenticatedUserRole()).thenReturn("Presse");
        when(jwtService.getEmailFromAuthenticatedUser()).thenReturn("presse@example.com");
        when(userRepository.findByEmail("presse@example.com")).thenReturn(Optional.of(user));
        when(publicationService.addPublication(any(), eq(file))).thenReturn(pub);

        ResponseEntity<Publication> response = publicationController.addPublication(json, file);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test", response.getBody().getContenu());
    }

    @Test
    void testUpdatePublication_AsPresse() throws Exception {
        String json = "{\"contenu\":\"Updated\"}";
        MockMultipartFile file = new MockMultipartFile("file", "image.jpg", "image/jpeg", "test".getBytes());

        User user = new User();
        user.setEmail("presse@example.com");

        Publication updated = new Publication();
        updated.setId(1);
        updated.setContenu("Updated");
        updated.setUser(user);

        when(jwtService.getAuthenticatedUserRole()).thenReturn("Presse");
        when(jwtService.getEmailFromAuthenticatedUser()).thenReturn("presse@example.com");
        when(userRepository.findByEmail("presse@example.com")).thenReturn(Optional.of(user));
        when(publicationService.updatePublication(any(), eq(file))).thenReturn(updated);

        ResponseEntity<Publication> response = publicationController.updatePublication(1, json, file);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Updated", response.getBody().getContenu());
    }

    @Test
    void testGetMyPublications_AsPresse() {
        User user = new User();
        user.setEmail("presse@example.com");
        user.setRole(Role.Presse);
        Publication pub = new Publication();
        pub.setContenu("Test");
        user.setPublications(List.of(pub));

        when(jwtService.getEmailFromAuthenticatedUser()).thenReturn("presse@example.com");
        when(userRepository.findByEmail("presse@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<List<Publication>> response = publicationController.getMyPublications("Bearer token");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testAjouterCommentaire_AsSpectateur() {
        Publication pub = new Publication();
        pub.setContenu("Contenu de la publication");

        Commentaire comment = new Commentaire();
        comment.setContenu("Bien dit!");
        comment.setPublication(pub);  // <-- IMPORTANT

        when(jwtService.getAuthenticatedUserRole()).thenReturn("Spectateur");
        when(commentaireService.ajouterCommentaire(eq(1), any())).thenReturn(comment);

        ResponseEntity<Commentaire> response = publicationController.ajouterCommentaire(1, comment);
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Contenu de la publication", response.getBody().getPublication().getContenu());
    }


    @Test
    void testReactToPublication_NewReaction() {
        User user = new User();
        user.setId(1);
        user.setRole(Role.Spectateur);
        Publication pub = new Publication();
        pub.setId(2);

        when(userRepository.findByEmail("spect@example.com")).thenReturn(Optional.of(user));
        when(publicationRepository.findById(2)).thenReturn(Optional.of(pub));
        when(reactionPublicationRepository.findByUserAndPublication(user, pub)).thenReturn(Optional.empty());
        when(reactionPublicationRepository.countByPublication(pub)).thenReturn(1L);

        Map<String, String> payload = Map.of("reaction", "like", "email", "spect@example.com");
        ResponseEntity<?> response = publicationController.reactToPublication(2, payload);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).get("message").toString().contains("Réaction"));
    }
}
