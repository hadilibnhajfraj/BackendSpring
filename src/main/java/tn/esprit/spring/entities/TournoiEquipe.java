package tn.esprit.spring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TournoiEquipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JsonIgnore
    Tournoi tournoi;

    @ManyToOne
    @JsonIgnore
    Equipe equipe;

    LocalDate dateAffectation = LocalDate.now(); // Enregistre la date d'affectation
}
