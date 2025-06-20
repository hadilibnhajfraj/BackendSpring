package tn.esprit.spring.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.spring.entities.Evenement;
import tn.esprit.spring.entities.NomZone;
import tn.esprit.spring.entities.Zone;

import java.util.List;

public interface ZoneRepository extends JpaRepository<Zone, Integer> {
    List<Zone> findByEvenement(Evenement evenement);
    List<Zone> findByEvenementAndNomIn(Evenement evenement, List<NomZone> noms);

}
