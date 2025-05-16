package tn.esprit.spring.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.MatchFoRepository;
import tn.esprit.spring.services.interfaces.ITournoiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/tournois")
@RequiredArgsConstructor
public class TournoiController {

    private final ITournoiService tournoiService;
    private final MatchFoRepository matchFoRepository;

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/createTournoi")
    public ResponseEntity<Tournoi> createTournoi(@RequestBody Tournoi tournoi) {
        return ResponseEntity.ok(tournoiService.createTournoi(tournoi));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/getAllTournois")
    public List<Tournoi> getAllTournois() {
        List<Tournoi> tournois = tournoiService.getAllTournois();
        tournois.forEach(t -> System.out.println("Tournoi ID: " + t.getIdTournoi())); // Log chaque ID
        return tournois;
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/getTournoiById/{id}")
    public ResponseEntity<Tournoi> getTournoiById(@PathVariable Integer id) {
        return ResponseEntity.ok(tournoiService.getTournoiById(id));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/updateTournoi/{id}")
    public ResponseEntity<Tournoi> updateTournoi(@PathVariable Integer id, @RequestBody Tournoi tournoi) {
        return ResponseEntity.ok(tournoiService.updateTournoi(id, tournoi));
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/deleteTournoi/{id}")
    public ResponseEntity<Void> deleteTournoi(@PathVariable Integer id) {
        tournoiService.deleteTournoi(id);
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/affecterEquipes/{tournoiId}")
    public ResponseEntity<?> affecterEquipesATournoi(
            @PathVariable Integer tournoiId,
            @RequestBody List<Integer> equipeIds) {
        try {
            Tournoi tournoi = tournoiService.affecterEquipesATournoi(tournoiId, equipeIds);
            return ResponseEntity.ok(tournoi); // On renvoie directement l'objet si tout se passe bien
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erreur interne : " + e.getMessage() + "\"}");
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/desaffecterEquipe/{tournoiId}/{equipeId}")
    public ResponseEntity<String> desaffecterEquipeDuTournoi(
            @PathVariable Integer tournoiId,
            @PathVariable Integer equipeId) {
        try {
            tournoiService.desaffecterEquipeDuTournoi(tournoiId, equipeId);
            return ResponseEntity.ok("{\"message\": \"L'équipe a été retirée du tournoi avec succès.\"}");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erreur lors de la désaffectation de l'équipe : " + e.getMessage() + "\"}");
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{tournoiId}/equipesNonInscrites")
    public ResponseEntity<List<Equipe>> getEquipesNonInscrites(@PathVariable Integer tournoiId) {
        try {
            List<Equipe> equipesNonInscrites = tournoiService.getEquipesNonInscrites(tournoiId);
            return ResponseEntity.ok(equipesNonInscrites);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Retourner 404 si tournoi introuvable
        }
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/getEquipesParTournoi/{tournoiId}")
    public ResponseEntity<?> getEquipesParTournoi(@PathVariable Integer tournoiId) {
        try {
            List<Equipe> equipes = tournoiService.getEquipesParTournoi(tournoiId);
            if (equipes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Aucune équipe trouvée pour le tournoi avec ID: " + tournoiId);
            }
            return ResponseEntity.ok(equipes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur : " + e.getMessage());
        }
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/genererMatchs/{tournoiId}")
    public ResponseEntity<String> genererMatchs(@PathVariable Integer tournoiId) {
        try {
            String resultat = tournoiService.genererPremierTour(tournoiId);
            return ResponseEntity.ok("{\"message\": \"Matchs générés avec succès\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la génération des matchs : " + e.getMessage());
        }
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/mettreAJourScores/{matchId}")
    public ResponseEntity<String> mettreAJourScores(
            @PathVariable Integer matchId,
            @RequestParam int scoreEquipe1,
            @RequestParam int scoreEquipe2) {
        try {
            tournoiService.mettreAJourScores(matchId, scoreEquipe1, scoreEquipe2);
            return ResponseEntity.ok("{\"message\": \"Scores mis à jour avec succès !\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erreur lors de la mise à jour des scores : " + e.getMessage() + "\"}");
        }
    }



    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/genererTourSuivant/{tournoiId}")
    public ResponseEntity<String> genererTourSuivant(@PathVariable Integer tournoiId) {
        try {
            String resultat = tournoiService.genererTourSuivant(tournoiId);
            return ResponseEntity.ok("{\"message\": \"" + resultat + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"Erreur lors de la génération du tour suivant : " + e.getMessage() + "\"}");
        }
    }




    // Endpoint pour affecter un terrain à un match
    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/affecterTerrain/{matchId}/{terrainId}")
    public ResponseEntity<String> affecterTerrainAMatch(
            @PathVariable Integer matchId,
            @PathVariable Integer terrainId) {

        try {
            tournoiService.affecterTerrainAMatch(matchId, terrainId);
            return ResponseEntity.ok("Terrain affecté au match avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'affectation du terrain : " + e.getMessage());
        }
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{tournoiId}/matchs")
    public List<MatchFo> getMatchsParTournoi(@PathVariable Integer tournoiId) {
        return tournoiService.getMatchsParTournoi(tournoiId);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/tournois/{idTournoi}/hasMatchs")
    public ResponseEntity<Boolean> tournoiADejaDesMatchs(@PathVariable Integer idTournoi) {
        boolean hasMatchs = tournoiService.tournoiADejaDesMatchs(idTournoi);
        return ResponseEntity.ok(hasMatchs);
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/genererChampionnat/{tournoiId}")
    public ResponseEntity<String> genererPlanningChampionnat(
            @PathVariable Integer tournoiId,
            @RequestParam(defaultValue = "false") boolean allerRetour) {
        try {
            String resultat = tournoiService.genererPlanningChampionnat(tournoiId, allerRetour);

            // Encodage propre en JSON
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(Map.of("message", resultat));

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(json);

        } catch (Exception e) {
            String errorJson = "{\"error\": \"Erreur lors de la génération du planning de championnat : " + e.getMessage().replace("\"", "\\\"") + "\"}";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(errorJson);
        }
    }


    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{tournoiId}/classement")
    public ResponseEntity<List<ClassementEquipeDTO>> calculerClassement(@PathVariable Integer tournoiId) {
        try {
            List<ClassementEquipeDTO> classement = tournoiService.calculerClassement(tournoiId);
            return ResponseEntity.ok(classement);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }




}




