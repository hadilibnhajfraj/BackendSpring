package tn.esprit.spring.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tn.esprit.spring.entities.Equipe;

import java.util.List;

@Repository
public interface EquipeRepository extends JpaRepository<Equipe, Integer> {
    @Query("SELECT e FROM Equipe e LEFT JOIN FETCH e.users")
    List<Equipe> findAllWithUsers();

}


