package tn.esprit.spring.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Role;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.services.implementations.JoueurService;


import java.util.List;

@RestController
@RequestMapping("/joueurs")
@RequiredArgsConstructor



public class JoueurController {


    private final JoueurService joueurService;


    // Créer un nouveau joueur
    @PostMapping("/add")
    public ResponseEntity<User> ajouterJoueur(@RequestBody User joueur) {
        joueur.setRole(Role.Joueur);
        return ResponseEntity.ok(joueurService.addJoueur(joueur));
    }

    // Récupérer tous les joueurs
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllJoueurs() {
        return ResponseEntity.ok(joueurService.getAllJoueurs());
    }

    // Récupérer un joueur par ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getJoueurById(@PathVariable Integer id) {
        return ResponseEntity.ok(joueurService.getJoueurById(id));
    }

    // Mettre à jour un joueur
    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateJoueur(@PathVariable Integer id, @RequestBody User joueur) {
        joueur.setRole(Role.Joueur);
        return ResponseEntity.ok(joueurService.updateJoueur(id, joueur));
    }

    // Supprimer un joueur
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteJoueur(@PathVariable Integer id) {
        joueurService.deleteJoueur(id);
        return ResponseEntity.ok("Joueur supprimé avec succès");
    }

    // Associer un joueur à une équipe
    @PostMapping("/{id}/assignTeam/{teamId}")
    public ResponseEntity<User> assignJoueurToEquipe(@PathVariable Integer id, @PathVariable Integer teamId) {
        return ResponseEntity.ok(joueurService.assignJoueurToEquipe(id, teamId));
    }

    // Retirer un joueur d'une équipe
    @PostMapping("/{id}/removeTeam")
    public ResponseEntity<User> removeJoueurFromEquipe(@PathVariable Integer id) {
        return ResponseEntity.ok(joueurService.removeJoueurFromEquipe(id));
    }
}
