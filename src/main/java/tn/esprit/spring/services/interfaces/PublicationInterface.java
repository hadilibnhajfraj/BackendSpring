package tn.esprit.spring.services.interfaces;
import tn.esprit.spring.entities.Publication;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.entities.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface PublicationInterface {
    Publication addPublication(Publication publication, MultipartFile file) throws IOException;
    List<Publication> retrievePublications();
    Publication updatePublication(Publication publication, MultipartFile file) throws IOException;

    void removePublication(int idPublication);
    Optional<Publication> retrievePublicationById(int idPublication);
    List<Publication> getPublicationsByUserId(int userId);
    Publication getLiveMatch();
    Publication getLatestVideo();

    Publication findById(int id);

}
