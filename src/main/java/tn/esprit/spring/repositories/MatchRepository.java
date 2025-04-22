package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entities.MatchFo;

public interface MatchRepository extends JpaRepository<MatchFo , Long> {
}
