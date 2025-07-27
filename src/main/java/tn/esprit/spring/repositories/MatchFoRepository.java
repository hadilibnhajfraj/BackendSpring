package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.entities.MatchFo;

@Repository
public interface MatchFoRepository extends JpaRepository<MatchFo, Integer> {
}