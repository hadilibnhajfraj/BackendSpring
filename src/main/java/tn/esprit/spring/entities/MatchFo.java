package tn.esprit.spring.entities;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MatchFo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int idMatch;
    String nom;
    String logo;
    String adresse;
    int nb_joueur;
    int scoreEquipe1;
    int scoreEquipe2;
    LocalDate dateMatch;
    @Temporal(TemporalType.TIME)
    Date heureMatch;

    @ManyToOne
    // @JoinColumn(name = "id_planning")
    Planning planning;

    @ManyToOne
    //@JoinColumn(name = "id_tournoi")
    Tournoi tournoi;

    @ManyToMany
    // @JoinColumn(name = "id_equipe1")
    List<Equipe> equipes1;

    @ManyToOne
    //@JoinColumn(name = "id_terrain")
    Terrain terrain;

    @OneToMany(mappedBy = "matchf", cascade = CascadeType.ALL)
    List<Billet> billets;


}
