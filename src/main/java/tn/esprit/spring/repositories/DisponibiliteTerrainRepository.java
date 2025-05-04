package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entities.Disponibilite_terrain;
import tn.esprit.spring.entities.Terrain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DisponibiliteTerrainRepository extends JpaRepository<Disponibilite_terrain, Integer> {

    List<Disponibilite_terrain> findByTerrainAndDate(Terrain terrain, LocalDate date);

    Optional<Disponibilite_terrain> findByTerrainAndDateAndHeureDebut(Terrain terrain, LocalDate date, LocalTime heureDebut);

    List<Disponibilite_terrain> findByTerrainAndDateBetweenAndHeureDebutGreaterThanEqualAndHeureDebutLessThan(
            Terrain terrain,
            LocalDate dateDebut,
            LocalDate dateFin,
            LocalTime heureDebut,
            LocalTime heureFin
    );
}
