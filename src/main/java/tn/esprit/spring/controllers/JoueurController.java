package tn.esprit.spring.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Joueur;
import tn.esprit.spring.entities.Role;
import tn.esprit.spring.entities.User;
import tn.esprit.spring.services.implementations.JoueurService;

import java.util.List;

@RestController
@RequestMapping("/joueurs")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class JoueurController {

    private final JoueurService joueurService;

    @PostMapping("/add")
    public ResponseEntity<Joueur> ajouterJoueur(@RequestBody Joueur joueur) {
        // Ensure basic user data is set
        if (joueur.getUser() == null) {
            joueur.setUser(new User());
        }
        joueur.getUser().setNom(joueur.getNom());
        joueur.getUser().setPrenom(joueur.getPrenom());
        joueur.getUser().setRole(Role.Joueur);

        return ResponseEntity.ok(joueurService.addJoueur(joueur));
    }
    //GET  ALL PLAYERS (tous les joueurs)
    @GetMapping("/all")
    public ResponseEntity<List<Joueur>> getAllJoueurs() {
        return ResponseEntity.ok(joueurService.getAllJoueurs());
    }
    //GET  ALL  non assigned PLAYERS (non affectues)
    @GetMapping("/all-non-assigned")
    public ResponseEntity<List<Joueur>> getAllNonAssignedJoueurs() {
        return ResponseEntity.ok(joueurService.findAllNonAssignedJoueurs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Joueur> getJoueurById(@PathVariable Integer id) {
        return ResponseEntity.ok(joueurService.getJoueurById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Joueur> updateJoueur(@PathVariable Integer id, @RequestBody Joueur joueurUpdates) {
        // Get the existing joueur
        Joueur existingJoueur = joueurService.getJoueurById(id);

        // Update user information if provided
        if (joueurUpdates.getUser() != null) {
            User userUpdates = joueurUpdates.getUser();

            // If updating an existing user
            if (existingJoueur.getUser() != null) {
                User existingUser = existingJoueur.getUser();

                // Update user fields if they're provided in the request
                if (userUpdates.getNom() != null) existingUser.setNom(userUpdates.getNom());
                if (userUpdates.getPrenom() != null) existingUser.setPrenom(userUpdates.getPrenom());
                if (userUpdates.getDateNaissance() != null) existingUser.setDateNaissance(userUpdates.getDateNaissance());
               // if (userUpdates.ge  () != null) existingUser.setMail(userUpdates.getMail());

                // Ensure role remains as Joueur
                existingUser.setRole(Role.Joueur);

                // Update the user in the joueur object
                existingJoueur.setUser(existingUser);
            }
            // If creating a new user association
            else {
                userUpdates.setRole(Role.Joueur);
                existingJoueur.setUser(userUpdates);
            }
        }

        // Update joueur fields
        if (joueurUpdates.getNom() != null) existingJoueur.setNom(joueurUpdates.getNom());
        if (joueurUpdates.getPrenom() != null) existingJoueur.setPrenom(joueurUpdates.getPrenom());
        if (joueurUpdates.getTaille() != 0) existingJoueur.setTaille(joueurUpdates.getTaille());
        if (joueurUpdates.getPoids() != 0) existingJoueur.setPoids(joueurUpdates.getPoids());
        if (joueurUpdates.getPiedFort() != null) existingJoueur.setPiedFort(joueurUpdates.getPiedFort());
        if (joueurUpdates.getPoste() != null) existingJoueur.setPoste(joueurUpdates.getPoste());
        if (joueurUpdates.getDescription() != null) existingJoueur.setDescription(joueurUpdates.getDescription());
        if (joueurUpdates.getMail() != null) existingJoueur.setMail(joueurUpdates.getMail());
        if (joueurUpdates.getTel() != null) existingJoueur.setTel(joueurUpdates.getTel());

        Joueur updatedJoueur = joueurService.updateJoueur(id, existingJoueur);
        return ResponseEntity.ok(updatedJoueur);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteJoueur(@PathVariable Integer id) {
        joueurService.deleteJoueur(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/assignTeam/{teamId}")
    public ResponseEntity<String> assignJoueurToEquipe(
            @PathVariable Integer id,
            @PathVariable Integer teamId) {
        joueurService.assignJoueurToEquipe(id, teamId);
        return ResponseEntity.ok("Joueur associé à l'équipe avec succès ! " );
    }

    @PostMapping("/{id}/removeTeam")
    public ResponseEntity<Joueur> removeJoueurFromEquipe(@PathVariable Integer id) {
        return ResponseEntity.ok(joueurService.removeJoueurFromEquipe(id));
    }

    @GetMapping("/team/{idEquipe}")
    public ResponseEntity<List<Joueur>> getAllPlayerByTeam(@PathVariable Integer idEquipe) {
        return ResponseEntity.ok(joueurService.getAllPlayerByTeam(idEquipe));
    }
}