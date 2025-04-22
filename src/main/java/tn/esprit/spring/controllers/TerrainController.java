package tn.esprit.spring.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Terrain;
import tn.esprit.spring.services.Iservice;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/terrain")
public class TerrainController {

    @Autowired
    private Iservice terrainService;

    // Ajouter un terrain
    @PostMapping("/addTerrain")
    public ResponseEntity<Terrain> ajouterTerrain(@RequestBody Terrain terrain) {
        Terrain savedTerrain = terrainService.ajouterTerrain(terrain);
        return ResponseEntity.ok(savedTerrain);
    }

    // Récupérer tous les terrains

    @GetMapping("/all")
    public ResponseEntity<List<Terrain>> getAllTerrains() {
        List<Terrain> terrains = terrainService.getAllTerrains();
        return ResponseEntity.ok(terrains);
    }

    // Récupérer un terrain par ID
    @GetMapping("/{id}")
    public ResponseEntity<Terrain> getTerrainById(@PathVariable Integer id) {
        Optional<Terrain> terrain = terrainService.getTerrainById(id);
        if (terrain.isPresent()) {
            return ResponseEntity.ok(terrain.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Mettre à jour un terrain
    @PutMapping("/update/{id}")
    public ResponseEntity<Terrain> updateTerrain(@PathVariable Integer id, @RequestBody Terrain terrain) {
        Terrain updatedTerrain = terrainService.updateTerrain(id, terrain);
        return ResponseEntity.ok(updatedTerrain);
    }

    // Supprimer un terrain
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTerrain(@PathVariable Integer id) {
        terrainService.deleteTerrain(id);
        return ResponseEntity.ok("Terrain supprimé");
    }
}
