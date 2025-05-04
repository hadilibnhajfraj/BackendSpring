package tn.esprit.spring.services.implimentations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Disponibilite_terrain;
import tn.esprit.spring.entities.Terrain;
import tn.esprit.spring.repositories.DisponibiliteTerrainRepository;
import tn.esprit.spring.repositories.TerrainRepository;
import tn.esprit.spring.services.interfaces.ITerrainService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class TerrainService implements ITerrainService {



        @Autowired
        TerrainRepository terrainRepository;

        @Autowired
        DisponibiliteTerrainRepository disponibiliteTerrainRepository; // CORRECTION

        @Override
        public Terrain addTerrain(Terrain terrain) {
            return terrainRepository.save(terrain);
        }

        @Override
        public List<Terrain> getAllTerrains() {
            return terrainRepository.findAll();
        }

        @Override
        public List<Disponibilite_terrain> getDisponibilitesByTerrainAndDate(int terrainId, LocalDate date) {
            Terrain terrain = terrainRepository.findById(terrainId)
                    .orElseThrow(() -> new IllegalArgumentException("Terrain non trouvé"));
            return disponibiliteTerrainRepository.findByTerrainAndDate(terrain, date); // CORRECTION
        }

        @Override
        public void reserverTerrain(int terrainId, LocalDate date, LocalTime heure) {
            Terrain terrain = terrainRepository.findById(terrainId)
                    .orElseThrow(() -> new IllegalArgumentException("Terrain non trouvé"));

            Disponibilite_terrain disponibilite = disponibiliteTerrainRepository
                    .findByTerrainAndDateAndHeureDebut(terrain, date, heure)
                    .orElseThrow(() -> new IllegalArgumentException("Disponibilité non trouvée"));

            disponibilite.setDisponible(false); // Marquer comme réservé
            disponibiliteTerrainRepository.save(disponibilite); // CORRECTION
        }

    @Override
    public Terrain getTerrainById(int terrainId) {
        return terrainRepository.findById(terrainId)
                .orElseThrow(() -> new IllegalArgumentException("Terrain non trouvé"));
    }

    public void reserverTerrainParPeriode(int terrainId, LocalDate dateDebut, LocalTime heureDebut,
                                          LocalDate dateFin, LocalTime heureFin) {

        Terrain terrain = terrainRepository.findById(terrainId)
                .orElseThrow(() -> new IllegalArgumentException("Terrain non trouvé"));

        List<Disponibilite_terrain> disponibilites = disponibiliteTerrainRepository
                .findByTerrainAndDateBetweenAndHeureDebutGreaterThanEqualAndHeureDebutLessThan(
                        terrain, dateDebut, dateFin, heureDebut, heureFin);

        if (disponibilites.isEmpty()) {
            throw new IllegalArgumentException("Aucune disponibilité trouvée pour cette période");
        }

        for (Disponibilite_terrain d : disponibilites) {
            if (!d.isDisponible()) {
                throw new IllegalStateException("Un ou plusieurs créneaux sont déjà réservés");
            }
            d.setDisponible(false); // Marque comme réservé
            disponibiliteTerrainRepository.save(d);
        }
    }


}



