package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.entities.Evenement;
import tn.esprit.spring.entities.MatchFo;
import tn.esprit.spring.entities.Tournoi;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EvenementRepository extends JpaRepository<Evenement, Integer> {
    @Query("SELECT e FROM Evenement e WHERE e.dateDebut > :today OR (e.dateDebut = :today AND e.heureDebut > :now)")
    List<Evenement> findEvenementsAVenir(@Param("today") LocalDate today, @Param("now") Date now);
    boolean existsByMatch(MatchFo match);
    Evenement findByMatch(MatchFo match);
    boolean existsByTournoi(Tournoi t);
    Optional<Evenement> findById(int evenementId);

    List<Evenement> findByTournoi(Tournoi tournoi);
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM Evenement e WHERE e.tournoi = :tournoi AND e.reservable = true")
    boolean existsByTournoiAndReservableTrue(@Param("tournoi") Tournoi tournoi);
    List<Evenement> findByTournoi_IdTournoi(Integer idTournoi);
}

