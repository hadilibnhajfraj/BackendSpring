package tn.esprit.spring.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.*;
import tn.esprit.spring.repositories.MatchFoRepository;
import tn.esprit.spring.services.interfaces.ITournoiService;

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


    @PutMapping("/affecterEquipes/{tournoiId}")
    public ResponseEntity<?> affecterEquipesATournoi(@PathVariable Integer tournoiId, @RequestBody List<Integer> equipeIds) {
        try {
            Tournoi tournoi = tournoiService.affecterEquipesATournoi(tournoiId, equipeIds);
            return ResponseEntity.ok(tournoi);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur interne : " + e.getMessage());
        }
    }


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

    @PutMapping("/genererMatchs/{tournoiId}")
    public ResponseEntity<String> genererMatchs(@PathVariable Integer tournoiId) {
        try {
            String resultat = tournoiService.genererPremierTour(tournoiId);
            return ResponseEntity.ok(resultat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la génération des matchs : " + e.getMessage());
        }
    }

    @PutMapping("/mettreAJourScores/{matchId}")
    public ResponseEntity<String> mettreAJourScores(
            @PathVariable Integer matchId,
            @RequestParam int scoreEquipe1,
            @RequestParam int scoreEquipe2) {
        try {
            tournoiService.mettreAJourScores(matchId, scoreEquipe1, scoreEquipe2);
            return ResponseEntity.ok("Scores mis à jour avec succès !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la mise à jour des scores : " + e.getMessage());
        }
    }

    @PutMapping("/genererTourSuivant/{tournoiId}")
    public ResponseEntity<String> genererTourSuivant(@PathVariable Integer tournoiId) {
        try {
            String resultat = tournoiService.genererTourSuivant(tournoiId);
            return ResponseEntity.ok(resultat);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la génération du tour suivant : " + e.getMessage());
        }
    }
   /* @GetMapping("/terrainsDisponibles/{tournoiId}")
    public ResponseEntity<List<Terrain>> getTerrainsDisponibles(
            @PathVariable Integer tournoiId,
            @RequestParam String dateMatch,
            @RequestParam String heureMatch) {

        // Convertir les paramètres en LocalDate et LocalTime
        LocalDate date = LocalDate.parse(dateMatch);
        LocalTime heure = LocalTime.parse(heureMatch);

        try {
            List<Terrain> terrainsDisponibles = tournoiService.getTerrainsDisponiblesPourTournoi(tournoiId, date, heure);
            return ResponseEntity.ok(terrainsDisponibles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }
    */


    // Endpoint pour affecter un terrain à un match
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



}




