package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entities.Billet;

public interface BilletRepository extends JpaRepository<Billet, Integer> {
}
