package tn.esprit.spring.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Commentaire;
import tn.esprit.spring.repositories.CommentaireRepository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentaireService {

    private final CommentaireRepository commentaireRepository;

    // Ajouter un commentaire
    public Commentaire ajouterCommentaire(Commentaire commentaire) {
        commentaire.setDateCommentaire(LocalDate.now()); // Définir la date automatiquement
        return commentaireRepository.save(commentaire);
    }

    // Récupérer tous les commentaires
    public List<Commentaire> getAllCommentaires() {
        return commentaireRepository.findAll();
    }

    // Récupérer un commentaire par ID
    public Optional<Commentaire> getCommentaireById(Integer id) {
        return commentaireRepository.findById(id);
    }

    // Mettre à jour un commentaire
    public Commentaire updateCommentaire(Integer id, Commentaire commentaireDetails) {
        return commentaireRepository.findById(id).map(commentaire -> {
            commentaire.setTexte(commentaireDetails.getTexte());
            return commentaireRepository.save(commentaire);
        }).orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));
    }

    // Supprimer un commentaire
    public void deleteCommentaire(Integer id) {
        commentaireRepository.deleteById(id);
    }

    // Ajouter une réaction (like) à un commentaire
    public Commentaire ajouterReaction(Integer id) {
        return commentaireRepository.findById(id).map(commentaire -> {
            commentaire.setNombreReactions(commentaire.getNombreReactions() + 1);
            return commentaireRepository.save(commentaire);
        }).orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));
    }

    // Enlever une réaction (dislike)
    public Commentaire enleverReaction(Integer id) {
        return commentaireRepository.findById(id).map(commentaire -> {
            if (commentaire.getNombreReactions() > 0) {
                commentaire.setNombreReactions(commentaire.getNombreReactions() - 1);
            }
            return commentaireRepository.save(commentaire);
        }).orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));
    }
}
