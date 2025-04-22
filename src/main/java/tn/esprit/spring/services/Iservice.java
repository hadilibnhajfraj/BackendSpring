package tn.esprit.spring.services;

import tn.esprit.spring.entities.Disponibilite_terrain;
import tn.esprit.spring.entities.MatchFo;
import tn.esprit.spring.entities.Terrain;
import java.util.List;
import java.util.Optional;

public interface Iservice {
    Terrain ajouterTerrain(Terrain terrain);
    Terrain updateTerrain(Integer id, Terrain terrain);
    List<Terrain> getAllTerrains();
    Optional<Terrain> getTerrainById(Integer id);
    void deleteTerrain(Integer id);

    Disponibilite_terrain ajouterDisponibilteTerrain(Disponibilite_terrain disponibiliteTerrain);

    Disponibilite_terrain updateDisponibiliteTerrain(Integer id, Disponibilite_terrain disponibiliteTerrain);

    List<Disponibilite_terrain> getAllDisponibiliteTerrain();

    Optional<Disponibilite_terrain> getDisponibiliteTerrainById(Integer id);

    void deleteDisponibiliteTerrain(Integer id);

    MatchFo ajouterMatch(MatchFo matchFo);

    MatchFo updateMatch(Integer id, MatchFo matchFo);
    List<MatchFo> getAllMatch();
    Optional<MatchFo> getMatchId(Integer id);
    void deleteMatch(Integer id);
}
