package tn.esprit.spring.services.interfaces;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.entities.Commentaire;

import java.io.IOException;
import java.util.List;
public interface CommentaireInterface {
    Commentaire addCommentaire(Commentaire commentaire, MultipartFile file) throws IOException;

    Commentaire ajouterCommentaire(Commentaire commentaire, int publicationId);

    Commentaire updateCommentaire(Commentaire commentaire, MultipartFile file) throws IOException;

    Commentaire retrieveCommentaire(int idCommentaire);

    void removeCommentaire(int idCommentaire);

    List<Commentaire> getCommentairesByUserId(Long userId);

    List<Commentaire> getCommentairesByPublicationId(int publicationId);

    Commentaire ajouterReaction(int idCommentaire);

    Commentaire enleverReaction(int idCommentaire);
}
