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
public class Equipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int idEquipe;

    String nom;
    String logo;
    String adresse;
    int nb_joueur;
}
