package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.entities.Commentaire;
import tn.esprit.spring.entities.Publication;
import tn.esprit.spring.entities.ReactionCommentaire;
import tn.esprit.spring.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReactionCommentaireRepository extends JpaRepository<ReactionCommentaire, Integer> {
    // Pour supprimer l’ancienne réaction d’un utilisateur sur un commentaire
    void deleteByUserAndCommentaire(User user, Commentaire commentaire);

    // Pour récupérer les réactions par commentaire
    List<ReactionCommentaire> findByCommentaireId(Integer commentaireId);
    void deleteByUserAndCommentaireAndType(User user, Commentaire commentaire, String type);
    long countByCommentaire(Commentaire commentaire);
    Optional<ReactionCommentaire> findByUserAndCommentaire(User user, Commentaire commentaire);
    Optional<ReactionCommentaire> findByUserIdAndCommentaireId(Integer userId, Integer commentaireId);
    Optional<ReactionCommentaire> findByUserEmailAndCommentaireId(String email, Integer commentaireId);

}
