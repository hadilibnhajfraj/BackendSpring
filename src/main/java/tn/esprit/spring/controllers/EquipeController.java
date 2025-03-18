package tn.esprit.spring.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Commentaire;
import tn.esprit.spring.entities.Equipe;
import tn.esprit.spring.services.implementations.EquipeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/equipes")
@RequiredArgsConstructor


public class EquipeController {
    private final EquipeService equipeService;

    // Créer une nouvelle équipe
    @PostMapping("/add")
    public ResponseEntity<Equipe> ajouterEquipe(@RequestBody Equipe equipe) {
        return ResponseEntity.ok(equipeService.ajouterEquipe(equipe));
    }


    // Récupérer toutes les équipes
    @GetMapping("/all")
    public ResponseEntity<List<Equipe>> getAllEquipes() {
        return ResponseEntity.ok(equipeService.getAllEquipes());
    }

    // Récupérer une équipe par ID
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Equipe>> getEquipeById(@PathVariable Integer id) {
        return ResponseEntity.ok(equipeService.getEquipeById(id));
    }

    // Mettre à jour une équipe
    @PutMapping("/update/{id}")
    public ResponseEntity<Equipe> updateEquipe(@PathVariable Integer id, @RequestBody Equipe equipe) {
        return ResponseEntity.ok(equipeService.updateEquipe(id, equipe));
    }

    // Supprimer une équipe
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEquipe(@PathVariable Integer id) {
        equipeService.deleteEquipe(id);
        return ResponseEntity.ok("Équipe supprimée avec succès");
    }

    // Ajouter un joueur à une équipe
    @PostMapping("/{id}/addPlayer/{playerId}")
    public ResponseEntity<Equipe> ajouterJoueur(@PathVariable Integer id, @PathVariable Integer playerId) {
        return ResponseEntity.ok(equipeService.ajouterJoueur(id, playerId));
    }

    // Retirer un joueur d'une équipe
    @PostMapping("/{id}/removePlayer/{playerId}")
    public ResponseEntity<Equipe> retirerJoueur(@PathVariable Integer id, @PathVariable Integer playerId) {
        return ResponseEntity.ok(equipeService.retirerJoueur(id, playerId));
    }
}
