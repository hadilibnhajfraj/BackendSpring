package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entities.Equipe;
import tn.esprit.spring.entities.Reservation;
import tn.esprit.spring.entities.Terrain;
import java.time.LocalDate;
import java.time.LocalTime;
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {


}
