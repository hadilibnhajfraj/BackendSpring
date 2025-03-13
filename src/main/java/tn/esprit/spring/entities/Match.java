package tn.esprit.spring.entities;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor


@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int idMatch;

    String nom;
    String logo;
    String adresse;
    int nb_joueur;
}
