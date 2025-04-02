package tn.esprit.spring.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private int nbEquipeRestant;
    private int frais;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    @Transient // Ne pas stocker en base de données
    private boolean hasMatchs;

    @OneToMany(mappedBy = "tournoi", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<MatchFo> matchFos;

    @OneToMany(mappedBy = "tournoi", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TournoiEquipe> tournoiEquipe;

    @ManyToOne
    @JsonIgnore
    private User user;

    public void setNbEquipe(int nbEquipe) {
        this.nbEquipe = nbEquipe;
        this.nbEquipeRestant = nbEquipe; // Initialise le nombre restant
    }

    @OneToMany(mappedBy = "tournoi", cascade = CascadeType.ALL)
    private List<TournoiEquipe> tournoiEquipes;

    public boolean isHasMatchs() {
        return hasMatchs;
    }

    public void setHasMatchs(boolean hasMatchs) {
        this.hasMatchs = hasMatchs;
    }
}