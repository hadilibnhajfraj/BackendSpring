package tn.esprit.spring.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.entities.User;

@Repository
public interface JoueurRepository extends JpaRepository<User, Integer> {
}
