package tn.esprit.spring.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import tn.esprit.spring.entities.NomZone;

@Data
public class ReservationTournoiRequest {

    @NotBlank(message = "Nom est obligatoire")
    private String nom;

    @NotBlank(message = "Prénom est obligatoire")
    private String prenom;

    @Email(message = "Email invalide")
    @NotBlank(message = "Email est obligatoire")
    private String email;

    @Min(value = 1, message = "Nombre minimum de places est 1")
    @Max(value = 5, message = "Nombre maximum de places est 5")
    private int nombrePlacesParEvenement;

    @NotNull(message = "Zone obligatoire")
    private NomZone nomZone; // ID de la zone choisie
}
