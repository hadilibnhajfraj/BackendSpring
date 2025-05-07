package tn.esprit.spring.controllers;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Commentaire;
import tn.esprit.spring.services.implementations.CommentaireService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/commentaires")
@RequiredArgsConstructor
public class CommentaireController {

    private final CommentaireService commentaireService;

    // Ajouter un commentaire

    @PostMapping("/ajouter/{publicationId}")
    public Commentaire ajouterCommentaire(@RequestBody Commentaire commentaire,
                                          @PathVariable int publicationId) {
        return commentaireService.ajouterCommentaire(commentaire, publicationId);
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
}
