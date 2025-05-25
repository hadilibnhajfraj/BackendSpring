package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entities.Publication;
import tn.esprit.spring.entities.ReactionPublication;
import tn.esprit.spring.entities.User;

import java.util.Optional;

public interface ReactionPublicationRepository extends JpaRepository<ReactionPublication, Integer> {
    Optional<ReactionPublication> findByUserAndPublication(User user, Publication publication);
    long countByPublication(Publication publication);
}
