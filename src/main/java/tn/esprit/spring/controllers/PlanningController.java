package tn.esprit.spring.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.entities.MatchFo;
import tn.esprit.spring.entities.Planning;
import tn.esprit.spring.services.interfaces.IPlanningService;

import java.util.List;

@RestController
@RequestMapping("/planning")  // Assurez-vous que le mapping est bien défini
@RequiredArgsConstructor
public class PlanningController {

    private final IPlanningService planningService;


}
