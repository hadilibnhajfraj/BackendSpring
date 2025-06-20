package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entities.Tournoi;

public interface TournoiRepository extends JpaRepository<Tournoi, Integer> {
}
