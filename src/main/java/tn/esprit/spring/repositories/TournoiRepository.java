package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.entities.Tournoi;

@Repository
public interface TournoiRepository extends JpaRepository<Tournoi, Integer> {
}
