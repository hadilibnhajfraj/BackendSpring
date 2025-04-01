package tn.esprit.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Terrain;
import tn.esprit.spring.services.interfaces.ITerrainService;

@RestController
@RequestMapping("/terrains")
public class TerrainController {

    @Autowired
    ITerrainService terrainService;

    // Endpoint pour ajouter un terrain
    @PostMapping("/add")
    public Terrain addTerrain(@RequestBody Terrain terrain) {
        return terrainService.addTerrain(terrain);
    }
}
