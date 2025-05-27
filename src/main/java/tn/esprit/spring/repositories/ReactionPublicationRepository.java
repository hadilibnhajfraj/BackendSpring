package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entities.Publication;
import tn.esprit.spring.entities.ReactionPublication;
import tn.esprit.spring.entities.User;

import java.util.List;
import java.util.Optional;

public interface ReactionPublicationRepository extends JpaRepository<ReactionPublication, Integer> {
    Optional<ReactionPublication> findByUserAndPublication(User user, Publication publication);
    long countByPublication(Publication publication);

    // Pour getPublicationReactionsCount
    List<ReactionPublication> findByPublicationId(Integer publicationId);

    // Pour getUserReactionToPublication
    Optional<ReactionPublication> findByUserIdAndPublicationId(Integer userId, Integer publicationId);

    // (Optionnel) si tu veux faire des suppressions ou updates


}
