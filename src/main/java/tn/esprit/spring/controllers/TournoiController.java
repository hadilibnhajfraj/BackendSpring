package tn.esprit.spring.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Tournoi;
import tn.esprit.spring.services.interfaces.ITournoiService;

import java.util.List;

@RestController
@RequestMapping("/tournois")
@RequiredArgsConstructor
public class TournoiController {
    private final ITournoiService tournoiService;

    @PostMapping("/createTournoi")
    public ResponseEntity<Tournoi> createTournoi(@RequestBody Tournoi tournoi) {
        return ResponseEntity.ok(tournoiService.createTournoi(tournoi));
    }

    @GetMapping("/getAllTournois")
    public List<Tournoi> getAllTournois() {
        return tournoiService.getAllTournois();
    }
    @GetMapping("/getTournoiById/{id}")
    public ResponseEntity<Tournoi> getTournoiById(@PathVariable Integer id) {
        return ResponseEntity.ok(tournoiService.getTournoiById(id));
    }

    @PutMapping("/updateTournoi/{id}")
    public ResponseEntity<Tournoi> updateTournoi(@PathVariable Integer id, @RequestBody Tournoi tournoi) {
        return ResponseEntity.ok(tournoiService.updateTournoi(id, tournoi));
    }

    @DeleteMapping("/deleteTournoi/{id}")
    public ResponseEntity<Void> deleteTournoi(@PathVariable Integer id) {
        tournoiService.deleteTournoi(id);
        return ResponseEntity.noContent().build();
    }
}
