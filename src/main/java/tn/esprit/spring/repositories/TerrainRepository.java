package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entities.Terrain;

public interface TerrainRepository extends JpaRepository<Terrain,Long> {
}
