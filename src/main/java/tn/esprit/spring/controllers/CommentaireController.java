package tn.esprit.spring.controllers;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Commentaire;
import tn.esprit.spring.entities.Publication;
import tn.esprit.spring.entities.ReactionCommentaire;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.repositories.CommentaireRepository;
import tn.esprit.spring.repositories.PublicationRepository;
import tn.esprit.spring.repositories.ReactionCommentaireRepository;
import tn.esprit.spring.repositories.UserRepository;
import tn.esprit.spring.services.implementations.CommentaireService;
import tn.esprit.spring.services.implementations.JwtService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/commentaires")
@RequiredArgsConstructor
public class CommentaireController {

    private final CommentaireService commentaireService;

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PublicationRepository publicationRepository;
    private final CommentaireRepository commentaireRepository;
    private final ReactionCommentaireRepository reactionCommentaireRepository;
    // Ajouter un commentaire

    /*  @PostMapping("/commentaires/add/{publicationId}")
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
  */
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
/*    @PutMapping("/update/{id}")
    public ResponseEntity<Commentaire> updateCommentaire(@PathVariable Integer id, @RequestBody Commentaire commentaire) {
        return ResponseEntity.ok(commentaireService.updateCommentaire(id, commentaire));
    }*/

    // Supprimer un commentaire


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

  /*  @PostMapping("/ajouter")
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
*/


    @PutMapping("/{id}")
    public ResponseEntity<Commentaire> modifierCommentaire(@PathVariable int id, @RequestBody Commentaire commentaire) {
        Optional<Commentaire> commentaireExistant = commentaireRepository.findById(id);
        if (commentaireExistant.isPresent()) {
            Commentaire c = commentaireExistant.get();
            c.setTexte(commentaire.getTexte());
            return ResponseEntity.ok(commentaireRepository.save(c));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Supprimer un commentaire
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommentaire(@PathVariable Integer id) {
        commentaireService.deleteCommentaire(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/add")
    public ResponseEntity<?> ajouterCommentaire(@RequestBody Map<String, Object> payload) {
        try {
            // ✅ Récupérer le token JWT envoyé par le client
            String jwtToken = (String) payload.get("userId"); // userId contient en fait le token

            // ✅ Extraire l'email à partir du token JWT
            String userEmail = (String) payload.get("userId");  // Récupérer l'email de l'utilisateur
            if (userEmail == null) {
                System.out.println("Champs manquant : userId (email)");
            } else {
                System.out.println("userEmail : " + userEmail);
            }

            String commentText = (String) payload.get("data");
            Object pubIdObj = payload.get("publicationId");

            // ✅ Validation des champs
            if (userEmail == null || commentText == null || commentText.trim().isEmpty() || pubIdObj == null) {
                return ResponseEntity.badRequest().body("Champs requis manquants");
            }

            Integer publicationId = Integer.parseInt(pubIdObj.toString());

            // ✅ Récupération des entités
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email : " + userEmail));

            Publication publication = publicationRepository.findById(publicationId)
                    .orElseThrow(() -> new RuntimeException("Publication non trouvée avec l'ID : " + publicationId));

            // ✅ Création et sauvegarde du commentaire
            Commentaire commentaire = new Commentaire(
                    null,
                    commentText,
                    LocalDate.now(),
                    0,
                    user,
                    publication
            );

            commentaireService.save(commentaire);

            return ResponseEntity.ok(Map.of(
                    "message", "Commentaire ajouté avec succès",
                    "publicationId", publicationId
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'ajout du commentaire : " + e.getMessage());
        }
    }

    @PutMapping("/{id}/reaction/increment")
    public ResponseEntity<Commentaire> incrementerReaction(@PathVariable Integer id) {
        return ResponseEntity.ok(commentaireService.incrementerReaction(id));
    }


    @PutMapping("/{id}/reaction/decrement")
    public ResponseEntity<Commentaire> decrementerReaction(@PathVariable Integer id) {
        return ResponseEntity.ok(commentaireService.decrementerReaction(id));
    }

    @PostMapping("/commentaires/{id}/react")
    @Transactional
    public ResponseEntity<?> reactToComment(@PathVariable Integer id, @RequestBody Map<String, String> payload) {
        String emoji = payload.get("reaction");
        String email = payload.get("email");

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Commentaire commentaire = commentaireRepository.findById(id).orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));

        // ✅ Vérification du rôle
        if (!"Spectateur".equalsIgnoreCase(String.valueOf(user.getRole()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Seuls les spectateurs peuvent réagir à un commentaire.");
        }

        // Cherche une réaction existante
        Optional<ReactionCommentaire> existingReaction = reactionCommentaireRepository.findByUserAndCommentaire(user, commentaire);

        if (existingReaction.isPresent()) {
            ReactionCommentaire reaction = existingReaction.get();
            if (!reaction.getType().equals(emoji)) {
                reaction.setType(emoji);
                reactionCommentaireRepository.save(reaction);
            }
        } else {
            ReactionCommentaire newReaction = new ReactionCommentaire();
            newReaction.setUser(user);
            newReaction.setCommentaire(commentaire);
            newReaction.setType(emoji);
            reactionCommentaireRepository.save(newReaction);
        }

        // Mettre à jour le nombre total de réactions
        long count = reactionCommentaireRepository.countByCommentaire(commentaire);
        commentaire.setNombreReactions((int) count);
        commentaireRepository.save(commentaire);

        return ResponseEntity.ok(Map.of("message", "Réaction enregistrée ou modifiée."));

    }




    @GetMapping("/{id}/reactions")
    public ResponseEntity<Map<String, Long>> getReactionsCount(@PathVariable Integer id) {
        List<ReactionCommentaire> reactions = reactionCommentaireRepository.findByCommentaireId(id);

        Map<String, Long> counts = reactions.stream()
                .collect(Collectors.groupingBy(ReactionCommentaire::getType, Collectors.counting()));

        return ResponseEntity.ok(counts);
    }
    @GetMapping("/commentaires/{commentId}/reaction/user")
    public ResponseEntity<ReactionCommentaire> getReactionByUser(
            @PathVariable Integer commentId,
            @RequestParam String email) {
        return reactionCommentaireRepository.findByUserEmailAndCommentaireId(email, commentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }



}