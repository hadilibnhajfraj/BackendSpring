package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.spring.entities.*;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    @Query("SELECT COALESCE(SUM(r.nombrePlaces), 0) FROM Reservation r WHERE r.evenement = :evenement AND r.zone = :zone AND r.etat = :etat")
    int sumPlacesByEvenementAndZoneAndEtat(@Param("evenement") Evenement evenement,
                                           @Param("zone") Zone zone,
                                           @Param("etat") EtatReservation etat);

    @Query("SELECT COALESCE(SUM(r.nombrePlaces), 0) FROM Reservation r WHERE r.evenement = :evenement AND r.etat = :etat")
    int sumPlacesByEvenementAndEtat(@Param("evenement") Evenement evenement,
                                    @Param("etat") EtatReservation etat);
    @Query("SELECT COALESCE(SUM(r.nombrePlaces), 0) FROM Reservation r " +
            "WHERE r.email = :email AND r.etat = :etat")
    int sumPlacesByEmailAndEtat(@Param("email") String email,
                                @Param("etat") EtatReservation etat);

    List<Reservation> findByEmailAndEtatAndEvenement_Tournoi(String email, EtatReservation etat, Tournoi tournoi);
    List<Reservation> findByEvenement_Tournoi_IdTournoiAndEmail(int idTournoi, String email);
}
