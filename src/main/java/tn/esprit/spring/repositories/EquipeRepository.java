package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entities.Equipe;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {
}
