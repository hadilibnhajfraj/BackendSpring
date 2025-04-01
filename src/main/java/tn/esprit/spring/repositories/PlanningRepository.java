package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.entities.Planning;

@Repository
public interface PlanningRepository extends JpaRepository<Planning, Integer> {
}
