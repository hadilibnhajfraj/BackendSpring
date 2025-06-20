package tn.esprit.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbonnementResponseDTO {
    String nom;
    String prenom;
    String email;
    String nomTournoi;
    List<MatchDetailDTO> matchs;
}
