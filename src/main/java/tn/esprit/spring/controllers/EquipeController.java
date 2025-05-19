package tn.esprit.spring.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.spring.entities.Equipe;
import tn.esprit.spring.repositories.EquipeRepository;
import tn.esprit.spring.services.implementations.EquipeService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/equipes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}) // Ajoute cette ligne
public class EquipeController {
    private final EquipeService equipeService;
    private final EquipeRepository equipeRepository;

    // Créer une nouvelle équipe
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Equipe> ajouterEquipe(
            @RequestPart("equipe") Equipe equipe,
            @RequestPart("logo") MultipartFile logo) {

        // Sauvegarder le nom du fichier dans l'entité
        if (logo != null && !logo.isEmpty()) {
            equipe.setLogo(logo.getOriginalFilename());

            // Optionnel : tu peux sauvegarder le fichier sur le disque ici
            // Path path = Paths.get("uploads/" + logo.getOriginalFilename());
            // Files.write(path, logo.getBytes());
        }

        Equipe savedEquipe = equipeService.ajouterEquipe(equipe);
        return ResponseEntity.ok(savedEquipe);
    }


    // Récupérer toutes les équipes
    @GetMapping("/all")
    public ResponseEntity<List<Equipe>> getAllEquipes() {
        return ResponseEntity.ok(equipeRepository.findAllWithUsers());
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

    @PostMapping("/{id}/addPlayer/{playerId}")
    public ResponseEntity<String> ajouterJoueur(@PathVariable Integer id, @PathVariable Integer playerId) {
        try {
             equipeService.ajouterJoueur(id, playerId);
            return ResponseEntity.ok("{\"message\": \"Joueur ajouté avec succès à l'équipe\"}");
        } catch (RuntimeException e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND)
                   .body("Player not found or already in the team");
        }
    }
    // Retirer un joueur d'une équipe
    @PostMapping("/{id}/removePlayer/{playerId}")
    public ResponseEntity<Equipe> retirerJoueur(@PathVariable Integer id, @PathVariable Integer playerId) {
        return ResponseEntity.ok(equipeService.retirerJoueur(id, playerId));
    }
}
