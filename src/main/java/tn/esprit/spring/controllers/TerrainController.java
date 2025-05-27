package tn.esprit.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Disponibilite_terrain;
import tn.esprit.spring.entities.Terrain;
import tn.esprit.spring.services.interfaces.ITerrainService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/terrains")
public class TerrainController {

    @Autowired
    ITerrainService terrainService;

    // Endpoint pour ajouter un terrain
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/add")
    public Terrain addTerrain(@RequestBody Terrain terrain) {
        return terrainService.addTerrain(terrain);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/getter")
    public List<Terrain> getTerrains() {
        return terrainService.getAllTerrains();
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{terrainId}/disponibilites")
    public List<Disponibilite_terrain> getDisponibilites(@PathVariable int terrainId, @RequestParam String date) {
        LocalDate selectedDate = LocalDate.parse(date);
        return terrainService.getDisponibilitesByTerrainAndDate(terrainId, selectedDate);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/{terrainId}/reservations")
    public String reserverTerrain(@PathVariable int terrainId, @RequestParam String date, @RequestParam String heure) {
        LocalDate selectedDate = LocalDate.parse(date);
        LocalTime selectedHeure = LocalTime.parse(heure);
        terrainService.reserverTerrain(terrainId, selectedDate, selectedHeure);
        return "Réservation effectuée avec succès!";
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/{terrainId}")
    public Terrain getTerrainById(@PathVariable int terrainId) {
        return terrainService.getTerrainById(terrainId);  // Ajout de cette méthode
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/{terrainId}/reservation-periode")
    public String reserverTerrainParPeriode(
            @PathVariable int terrainId,
            @RequestParam String dateDebut,
            @RequestParam String heureDebut,
            @RequestParam String dateFin,
            @RequestParam String heureFin) {

        LocalDate debut = LocalDate.parse(dateDebut);
        LocalTime hDebut = LocalTime.parse(heureDebut);
        LocalDate fin = LocalDate.parse(dateFin);
        LocalTime hFin = LocalTime.parse(heureFin);

        terrainService.reserverTerrainParPeriode(terrainId, debut, hDebut, fin, hFin);
        return "Réservation effectuée pour la période !";
    }

}


