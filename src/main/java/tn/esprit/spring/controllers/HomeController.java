package tn.esprit.spring.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "Bienvenue sur l'API Spring Boot sécurisée avec JWT !";
    }
}
