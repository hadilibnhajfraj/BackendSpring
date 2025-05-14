package tn.esprit.spring.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.spring.entities.Commentaire;
import tn.esprit.spring.entities.Publication;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.repositories.CommentaireRepository;
import tn.esprit.spring.repositories.PublicationRepository;
import tn.esprit.spring.repositories.UserRepository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentaireService {

    private final CommentaireRepository commentaireRepository;
    private final PublicationRepository publicationRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public Commentaire ajouterCommentaire(Commentaire commentaire, int publicationId) {
        // Vérifier que seul un spectateur peut commenter
        if (!"Spectateur".equals(jwtService.getAuthenticatedUserRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé");
        }

        // Récupérer l'utilisateur connecté via l'email dans le token JWT
        String email = jwtService.getEmailFromAuthenticatedUser();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer la publication ciblée
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));

        // Lier l'utilisateur, la publication, et la date au commentaire
        commentaire.setUser(user);
        commentaire.setPublication(publication);
        commentaire.setDateCommentaire(LocalDate.now());

        // Sauvegarder et retourner le commentaire
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
    public Commentaire updateCommentaire(Integer id, String newText) {
        Commentaire commentaire = commentaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));
        commentaire.setTexte(newText);
        return commentaireRepository.save(commentaire);
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
    public void save(Commentaire commentaire) {
        commentaireRepository.save(commentaire);
    }
    public void ajouterCommentaireViaWebSocket(String texte, String userId, String publicationId) {
        Commentaire commentaire = new Commentaire();
        commentaire.setTexte(texte);
        commentaire.setDateCommentaire(LocalDate.now());

        // Convertir les IDs selon leur type réel
        int pubId = Integer.parseInt(publicationId);
        int uId = Integer.parseInt(userId);

        User user = userRepository.findById(uId).orElse(null);
        Publication publication = publicationRepository.findById(pubId).orElse(null);

        if (user != null && publication != null) {
            commentaire.setUser(user);
            commentaire.setPublication(publication);
            commentaireRepository.save(commentaire);
        }
    }
    public List<Commentaire> getCommentairesByPublicationId(int publicationId) {
        // Récupérer la publication par son ID
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));

        // Récupérer les commentaires associés à cette publication
        return commentaireRepository.findByPublication(publication);
    }
    public Commentaire ajouterCommentaire(int publicationId, Commentaire commentaire) {
        // Récupérer la publication par son ID
        Publication publication = publicationRepository.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));

        // Associer le commentaire à la publication
        commentaire.setPublication(publication);
        return commentaireRepository.save(commentaire);
    }
}
