package tn.esprit.spring.entities;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor


@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tournoi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTournoi;

    private String nom;
    private int nbEquipe;
    private int frais;
    private LocalDate dateDebut;
    private LocalDate dateFin;
}
