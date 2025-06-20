package tn.esprit.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchDetailDTO {
    String nomMatch;
    String nomEquipe1;
    String logoEquipe1;
    String nomEquipe2;
    String logoEquipe2;
    String nomTerrain;
    String adresseTerrain;
}
