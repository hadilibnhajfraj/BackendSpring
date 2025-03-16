package tn.esprit.spring.services.interfaces;
import tn.esprit.spring.entities.Publication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
public interface PublicationInterface {
    Publication addPublication(Publication publication, MultipartFile file) throws IOException;
    List<Publication> retrievePublications();
    Publication updatePublication(Publication publication, MultipartFile file) throws IOException;
    Publication retrievePublication(int idPublication);
    void removePublication(int idPublication);
    List<Publication> getPublicationsByUserId(Long userId);

}
