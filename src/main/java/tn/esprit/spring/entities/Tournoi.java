package tn.esprit.spring.entities;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Tournoi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTournoi;
    private String nom;
    private int nbEquipe;
    private int frais;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    @OneToMany(mappedBy = "tournoi", cascade = CascadeType.ALL)
    private List<MatchFo> matchFos;

    @ManyToMany(mappedBy = "tournois")
    private List<Equipe> equipes;

    @ManyToOne
    private User user;
}