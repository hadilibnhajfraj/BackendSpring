package tn.esprit.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Disponibilite_terrain;
import tn.esprit.spring.entities.Terrain;
import tn.esprit.spring.services.Iservice;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/disponibiliteTerrain")
public class DisponibiliteTerrainController {

    @Autowired
    private Iservice disponibiliteTerrainService;


    @PostMapping("/addDisponibilte")
    public ResponseEntity<Disponibilite_terrain> ajouterDisponibiliteTerrain(@RequestBody Disponibilite_terrain disponibiliteTerrain) {
        Disponibilite_terrain savedDisponibiliteTerrain = disponibiliteTerrainService.ajouterDisponibilteTerrain(disponibiliteTerrain);
        return ResponseEntity.ok(savedDisponibiliteTerrain);
    }
    @PutMapping("/updateDisponibilite/{id}")
    public ResponseEntity<Disponibilite_terrain> updateDisponibiliteTerrain(@PathVariable Integer id, @RequestBody Disponibilite_terrain disponibiliteTerrain) {
        Disponibilite_terrain updated = disponibiliteTerrainService.updateDisponibiliteTerrain(id, disponibiliteTerrain);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Disponibilite_terrain>> getAllDisponibilites() {
        List<Disponibilite_terrain> disponibilites = disponibiliteTerrainService.getAllDisponibiliteTerrain();
        return ResponseEntity.ok(disponibilites);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Disponibilite_terrain> getDisponibiliteById(@PathVariable Integer id) {
        return disponibiliteTerrainService.getDisponibiliteTerrainById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deleteDisponibilite/{id}")
    public ResponseEntity<Void> deleteDisponibilite(@PathVariable Integer id) {
        disponibiliteTerrainService.deleteDisponibiliteTerrain(id);
        return ResponseEntity.noContent().build();
    }

}
