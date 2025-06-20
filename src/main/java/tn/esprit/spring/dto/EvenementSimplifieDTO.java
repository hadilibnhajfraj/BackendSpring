package tn.esprit.spring.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
@Getter
@Setter
public class EvenementSimplifieDTO {
    private int id;
    private String nomEquipe1;
    private String nomEquipe2;
    private String nomTerrain;
    private String adresseTerrain;
    private LocalDate dateDebut;
    private String heureDebut;
    private String nomTournoi;
    private int capaciteMax;
    private int nbSpectateurActuel;
    private String heureFin;
    private String nomMatch;
}
