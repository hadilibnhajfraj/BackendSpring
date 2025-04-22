package tn.esprit.spring.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.spring.entities.Disponibilite_terrain;
import tn.esprit.spring.entities.MatchFo;
import tn.esprit.spring.entities.Terrain;
import tn.esprit.spring.repositories.DisponibiliteTerrainRepository;
import tn.esprit.spring.repositories.MatchRepository;
import tn.esprit.spring.repositories.TerrainRepository;

import java.util.List;
import java.util.Optional;

@Service

public class ServiceIMP implements Iservice {

    private final TerrainRepository terrainRepository;
    private final DisponibiliteTerrainRepository disponibiliteTerrainRepository;

    private final MatchRepository matchRepository;

    @Autowired
    public ServiceIMP(
            TerrainRepository terrainRepository ,
            DisponibiliteTerrainRepository disponibiliteTerrainRepository ,
            MatchRepository matchRepository) {
        this.terrainRepository = terrainRepository;
        this.disponibiliteTerrainRepository = disponibiliteTerrainRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    public Terrain ajouterTerrain(Terrain terrain) {
        return terrainRepository.save(terrain);
    }

    @Override
    public Terrain updateTerrain(Integer id, Terrain terrain) {
        return terrainRepository.findById(Long.valueOf(id))
                .map(existingTerrain -> {
                    existingTerrain.setNom(terrain.getNom());
                    existingTerrain.setAdresse(terrain.getAdresse());
                    existingTerrain.setSurface(terrain.getSurface());
                    return terrainRepository.save(existingTerrain);
                })
                .orElseThrow(() -> new RuntimeException("Terrain avec ID " + id + " non trouvé"));
    }

    @Override
    public List<Terrain> getAllTerrains() {
        return terrainRepository.findAll();
    }

    @Override
    public Optional<Terrain> getTerrainById(Integer id) {
        return terrainRepository.findById(Long.valueOf(id));
    }

    @Override
    public void deleteTerrain(Integer id) {
        terrainRepository.deleteById(Long.valueOf(id));
    }

    @Override
    public Disponibilite_terrain ajouterDisponibilteTerrain(Disponibilite_terrain disponibiliteTerrain) {
        return disponibiliteTerrainRepository.save(disponibiliteTerrain);
    }

    @Override
    public Disponibilite_terrain updateDisponibiliteTerrain(Integer id, Disponibilite_terrain disponibiliteTerrain) {
        return disponibiliteTerrainRepository.findById(Long.valueOf(id))
                .map(existingDisponibiliteTerrain -> {
                    existingDisponibiliteTerrain.setDate(disponibiliteTerrain.getDate());
                    existingDisponibiliteTerrain.setJour(disponibiliteTerrain.getJour());
                    existingDisponibiliteTerrain.setHeureDebut(disponibiliteTerrain.getHeureDebut());
                    existingDisponibiliteTerrain.setHeureFin(disponibiliteTerrain.getHeureFin());
                    existingDisponibiliteTerrain.setDisponible(disponibiliteTerrain.isDisponible());
                    return disponibiliteTerrainRepository.save(existingDisponibiliteTerrain);
                })
                .orElseThrow(() -> new RuntimeException("Disponibilite Terrain avec ID " + id + " non trouvé"));
    }

    @Override
    public List<Disponibilite_terrain> getAllDisponibiliteTerrain() {
        return disponibiliteTerrainRepository.findAll();
    }

    @Override
    public Optional<Disponibilite_terrain> getDisponibiliteTerrainById(Integer id) {
        return disponibiliteTerrainRepository.findById(Long.valueOf(id));
    }

    @Override
    public void deleteDisponibiliteTerrain(Integer id) {
        disponibiliteTerrainRepository.deleteById(Long.valueOf(id));
    }

    @Override
    public MatchFo ajouterMatch(MatchFo matchFo) {
        return matchRepository.save(matchFo);
    }

    @Override
    public MatchFo updateMatch(Integer id, MatchFo matchFo) {
        return matchRepository.findById(Long.valueOf(id))
                .map(existingMatch -> {
                    existingMatch.setNom(matchFo.getNom());
                    existingMatch.setLogo(matchFo.getLogo());
                    existingMatch.setAdresse(matchFo.getAdresse());
                    existingMatch.setNb_joueur(matchFo.getNb_joueur());
                    existingMatch.setScoreEquipe1(matchFo.getScoreEquipe1());
                    existingMatch.setScoreEquipe2(matchFo.getScoreEquipe2());
                    existingMatch.setDateMatch(matchFo.getDateMatch());
                    existingMatch.setHeureMatch(matchFo.getHeureMatch());
                    existingMatch.setPlanning(matchFo.getPlanning());
                    existingMatch.setTournoi(matchFo.getTournoi());
                    existingMatch.setEquipes1(matchFo.getEquipes1());
                    existingMatch.setTerrain(matchFo.getTerrain());
                    return matchRepository.save(existingMatch);
                })
                .orElseThrow(() -> new RuntimeException("Match avec ID " + id + " non trouvé"));
    }


    @Override
    public List<MatchFo> getAllMatch() {
        return matchRepository.findAll();
    }

    @Override
    public Optional<MatchFo> getMatchId(Integer id) {
        return matchRepository.findById(Long.valueOf(id));
    }

    @Override
    public void deleteMatch(Integer id) {
        matchRepository.deleteById(Long.valueOf(id));
    }
}
