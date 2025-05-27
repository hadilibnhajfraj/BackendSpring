package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entities.Terrain;
import tn.esprit.spring.entities.Tournoi;

import java.util.List;

public interface TerrainRepository extends JpaRepository<Terrain, Integer> {
    //List<Terrain> findAllByTournoi(Tournoi tournoi);
}
