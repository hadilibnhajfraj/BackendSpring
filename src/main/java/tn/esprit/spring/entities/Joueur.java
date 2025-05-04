package tn.esprit.spring.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Joueur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int idJoueur;

    float taille;
    float poids;
    String piedFort;
    String poste;

    @Column(length = 1000)
    String description;

    @ManyToOne
    @JoinColumn(name = "id_equipe")
    Equipe equipe;
    @ManyToOne
    private User user;
}
