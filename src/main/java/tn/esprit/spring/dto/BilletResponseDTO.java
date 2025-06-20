package tn.esprit.spring.dto;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BilletResponseDTO {
    private String nom;
    private String prenom;
    private String email;
    private String zone;
    private String nomEquipe1;
    private String logoEquipe1;
    private String nomEquipe2;
    private String logoEquipe2;
    private String nomTerain;
    private String adressTerain;
    private String nomMatch;
    private String nomTournoi;
}
