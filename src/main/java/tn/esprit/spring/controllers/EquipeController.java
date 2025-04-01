package tn.esprit.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.Equipe;
import tn.esprit.spring.services.interfaces.IEquipeService;

@RestController
@RequestMapping("/equipes")
public class EquipeController {

    @Autowired
    IEquipeService equipeService;

    // Endpoint pour ajouter une équipe
    @PostMapping("/add")
    public Equipe addEquipe(@RequestBody Equipe equipe) {
        return equipeService.addEquipe(equipe);
    }
}
