package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entities.Abonnement;

public interface AbonnementRepository extends JpaRepository<Abonnement,Integer> {
}
