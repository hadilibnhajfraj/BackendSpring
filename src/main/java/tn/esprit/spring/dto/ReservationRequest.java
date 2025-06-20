package tn.esprit.spring.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReservationRequest {
    @NotBlank(message = "Nom est obligatoire")
    private String nom;

    @NotBlank(message = "Prénom est obligatoire")
    private String prenom;

    @Email(message = "Email invalide")
    @NotBlank(message = "Email est obligatoire")
    private String email;

    @Min(value = 1, message = "Nombre minimum de places est 1")
    @Max(value = 5, message = "Nombre maximum de places est 5")
    private int nombrePlaces;

    @NotNull(message = "Zone obligatoire")
    private Integer zoneId; // ID de la zone choisie
}
