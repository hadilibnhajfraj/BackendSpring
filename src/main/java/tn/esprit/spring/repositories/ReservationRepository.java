package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit.spring.entities.Reservation;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    
    @Query("SELECT SUM(r.terrain.prix) FROM Reservation r")
    Double sumTotalRevenue();
}

