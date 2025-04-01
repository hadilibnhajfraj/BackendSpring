package tn.esprit.spring.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entities.Tournoi;
import tn.esprit.spring.entities.TournoiEquipe;

import java.util.List;

public interface TournoiEquipeRepository extends JpaRepository<TournoiEquipe, Integer> {
    List<TournoiEquipe> findAllByTournoi(Tournoi tournoi);
}

