package tn.esprit.spring.repositories;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.entities.Publication;

import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Integer> {
    //List<Publication> findByUserId(Long userId);
    Publication findFirstByIsLiveTrueOrderByDatePublicationDesc();
    Publication findFirstByTypeMediaOrderByDatePublicationDesc(String type);
    List<Publication> findByUserId(int userId);

}
