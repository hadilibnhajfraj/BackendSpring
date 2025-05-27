package tn.esprit.spring.services.interfaces;

import tn.esprit.spring.entities.Disponibilite_terrain;
import tn.esprit.spring.entities.Terrain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ITerrainService {
    Terrain addTerrain(Terrain terrain);
    List<Terrain> getAllTerrains();

    List<Disponibilite_terrain> getDisponibilitesByTerrainAndDate(int terrainId, LocalDate date);

    void reserverTerrain(int terrainId, LocalDate date, LocalTime heure);
    Terrain getTerrainById(int terrainId);

    void reserverTerrainParPeriode(int terrainId, LocalDate dateDebut, LocalTime heureDebut,
                                   LocalDate dateFin, LocalTime heureFin);

}
