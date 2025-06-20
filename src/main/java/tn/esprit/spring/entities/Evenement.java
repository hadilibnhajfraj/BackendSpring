package tn.esprit.spring.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @OneToOne
    MatchFo match;

    String nomEquipe1;
    String nomEquipe2;
    @ManyToOne
    // Clé étrangère pour le tournoi
    Tournoi tournoi;  // Référence au tournoi

    @ManyToOne
    private Terrain terrain;

    int nbSpectateurActuel;
    boolean reservable = true;

    LocalDate dateDebut;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    Date heureDebut;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    Date heureFin;

    boolean estTournoi;

    @OneToMany(mappedBy = "evenement", cascade = CascadeType.ALL)
    List<Zone> zones;
}
