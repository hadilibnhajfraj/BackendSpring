package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit.spring.entities.Equipe;

import java.util.List;

public interface EquipeRepository extends JpaRepository<Equipe, Integer> {

    @Query  ("SELECT e FROM Equipe e JOIN e.tournoiEquipes te WHERE te.tournoi.idTournoi = :tournoiId")
    List<Equipe> findEquipesByTournoiId(Integer tournoiId);
}
