package tn.esprit.spring.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.entities.Commentaire;
import tn.esprit.spring.entities.Publication;

import java.util.List;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Integer> {

    List<Commentaire> findByPublication(Publication publication);
}
