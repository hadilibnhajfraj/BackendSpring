package tn.esprit.spring.services.interfaces;

import jakarta.transaction.Transactional;
import tn.esprit.spring.entities.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ITournoiService {
    Tournoi createTournoi(Tournoi tournoi);
    List<Tournoi> getAllTournois();
    Tournoi getTournoiById(Integer id);
    Tournoi updateTournoi(Integer id, Tournoi tournoi);
    void deleteTournoi(Integer id);
    Tournoi affecterEquipesATournoi(Integer tournoiId, List<Integer> equipeIds);
    void desaffecterEquipeDuTournoi(Integer tournoiId, Integer equipeId);

    // Récupère les équipes inscrites à un tournoi
    List<Equipe> getEquipesParTournoi(Integer tournoiId);
    String genererTourSuivant(Integer tournoiId);
    public String mettreAJourScores(Integer matchId, int scoreEquipe1, int scoreEquipe2);
    String genererPremierTour(Integer tournoiId);
 //   List<Terrain> getTerrainsDisponiblesPourTournoi(Integer tournoiId, LocalDate dateMatch, LocalTime heureMatch);

    void affecterTerrainAMatch(Integer matchId, Integer terrainId);

    List<MatchFo> getMatchsParTournoi(Integer tournoiId);

    boolean tournoiADejaDesMatchs(Integer idTournoi);
    List<Equipe> getEquipesNonInscrites(Integer tournoiId);

    byte[] genererRecapQRCode(Integer idTournoi) throws Exception;

    String genererPlanningChampionnat(Integer tournoiId, boolean allerRetour);
    List<ClassementEquipeDTO> calculerClassement(Integer tournoiId);










}
