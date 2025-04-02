package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.entities.MatchFo;
import tn.esprit.spring.entities.Terrain;
import tn.esprit.spring.entities.Tournoi;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface MatchFoRepository extends JpaRepository<MatchFo, Integer> {



    List<MatchFo> findByTournoi(Tournoi tournoi);

    // Trouver le dernier tour joué dans un tournoi
    @Query("SELECT COALESCE(MAX(m.tour), 0) FROM MatchFo m WHERE m.tournoi.idTournoi = :tournoiId")
    int findMaxTourParTournoi(@Param("tournoiId") Integer tournoiId);

    // Récupérer tous les matchs d'un tournoi pour un tour donné
    List<MatchFo> findByTournoiAndTour(Tournoi tournoi, int tour);

    boolean existsByTerrainAndDateMatchAndHeureMatch(Terrain terrain, LocalDate dateMatch, Time heureMatch);
    boolean existsByTournoi_IdTournoi(Integer idTournoi);




}
