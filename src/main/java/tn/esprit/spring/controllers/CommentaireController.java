package tn.esprit.spring.controllers;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.entities.Commentaire;
import tn.esprit.spring.entities.Publication;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.repositories.CommentaireRepository;
import tn.esprit.spring.repositories.PublicationRepository;
import tn.esprit.spring.repositories.UserRepository;
import tn.esprit.spring.services.implementations.CommentaireService;
import tn.esprit.spring.services.implementations.JwtService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/commentaires")
@RequiredArgsConstructor
public class CommentaireController {

    private final CommentaireService commentaireService;

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;
    private final CommentaireRepository commentaireRepository;
    // Ajouter un commentaire

    @PostMapping("/commentaires/add/{publicationId}")
    public ResponseEntity<Commentaire> ajouterCommentaire(@RequestBody Commentaire commentaire,
                                                          @PathVariable int publicationId) {
        // Vérification du rôle
        if (!"Spectateur".equals(jwtService.getAuthenticatedUserRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }

        // Récupérer l'utilisateur connecté via son email dans le token
        String email = jwtService.getEmailFromAuthenticatedUser();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer la publication cible
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));

        // Associer l'utilisateur et la publication au commentaire
        commentaire.setUser(user);
        commentaire.setPublication(publication);
        commentaire.setDateCommentaire(LocalDate.now());

        // Sauvegarder et retourner le commentaire
        Commentaire savedCommentaire = commentaireRepository.save(commentaire);
        return ResponseEntity.ok(savedCommentaire);
    }

    // Récupérer tous les commentaires
    @GetMapping("/all")
    public ResponseEntity<List<Commentaire>> getAllCommentaires() {
        return ResponseEntity.ok(commentaireService.getAllCommentaires());
    }

    // Récupérer un commentaire par ID
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Commentaire>> getCommentaireById(@PathVariable Integer id) {
        return ResponseEntity.ok(commentaireService.getCommentaireById(id));
    }

    // Mettre à jour un commentaire
    @PutMapping("/update/{id}")
    public ResponseEntity<Commentaire> updateCommentaire(@PathVariable Integer id, @RequestBody Commentaire commentaire) {
        return ResponseEntity.ok(commentaireService.updateCommentaire(id, commentaire));
    }

    // Supprimer un commentaire
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCommentaire(@PathVariable Integer id) {
        commentaireService.deleteCommentaire(id);
        return ResponseEntity.ok("Commentaire supprimé avec succès");
    }

    // Ajouter une réaction (like)
    @PostMapping("/{id}/like")
    public ResponseEntity<Commentaire> ajouterReaction(@PathVariable Integer id) {
        return ResponseEntity.ok(commentaireService.ajouterReaction(id));
    }

    // Enlever une réaction (dislike)
    @PostMapping("/{id}/dislike")
    public ResponseEntity<Commentaire> enleverReaction(@PathVariable Integer id) {
        return ResponseEntity.ok(commentaireService.enleverReaction(id));
    }

    @PostMapping("/ajouter")
    public ResponseEntity<?> ajouterCommentaire(@RequestBody Map<String, String> request) {
        String emailCommentateur = jwtService.getEmailFromAuthenticatedUser();
        User userCommentateur = userRepository.findByEmail(emailCommentateur)
                .orElseThrow(() -> new RuntimeException("Utilisateur commentateur introuvable"));

        // Récupère le journaliste (optionnel si différent du commentateur)
        User journaliste = userRepository.findByEmail("hadil.ibnhajfraj@gmail.com")
                .orElseThrow(() -> new RuntimeException("Journaliste introuvable"));

        // Récupère la publication live du journaliste
        Publication publicationLive = publicationRepository.findByUserAndIsLiveTrue(journaliste)
                .orElseThrow(() -> new RuntimeException("Aucune publication live trouvée"));

        // Crée le commentaire
        Commentaire commentaire = new Commentaire();
        commentaire.setTexte(request.get("texte"));
        commentaire.setUser(userCommentateur);
        commentaire.setPublication(publicationLive);
        commentaire.setDateCommentaire(LocalDate.now());

        commentaireRepository.save(commentaire);

        System.out.println("Nouveau commentaire: " + commentaire.getTexte());
        System.out.println("Utilisateur: " + emailCommentateur + " Publication ID: " + publicationLive.getId());

        return ResponseEntity.ok(commentaire);
    }
}
