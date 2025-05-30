package tn.esprit.spring.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Time;
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
    //int scoreEquipe1;
    //int scoreEquipe2;
  //  LocalDate dateMatch;
  //  @Temporal(TemporalType.TIME)
   // Date heureMatch;
    @Column(nullable = false)
    private int tour;

    @ManyToOne
    @JsonIgnore
    // @JoinColumn(name = "id_planning")
    Planning planning;

    @ManyToOne
    @JsonIgnore
    //@JoinColumn(name = "id_tournoi")
    Tournoi tournoi;

    @ManyToMany
    // @JoinColumn(name = "id_equipe1")
    List<Equipe> equipes1;
    @Column( nullable = true)
    private Integer scoreEquipe1 = null ;
    @Column( nullable = true)
    private Integer scoreEquipe2 = null ;

    @Column(nullable = true)
    private Integer cartonsJaunesEquipe1 = 0;

    @Column(nullable = true)
    private Integer cartonsRougesEquipe1 = 0;

    @Column(nullable = true)
    private Integer cornersEquipe1 = 0;

    @Column(nullable = true)
    private Integer cartonsJaunesEquipe2 = 0;

    @Column(nullable = true)
    private Integer cartonsRougesEquipe2 = 0;

    @Column(nullable = true)
    private Integer cornersEquipe2 = 0;


    private LocalDate dateMatch;
    private Time heureMatch;
    @ManyToOne
    //@JoinColumn(name = "id_terrain")
    Terrain terrain;

    @OneToMany(mappedBy = "matchf", cascade = CascadeType.ALL)
    List<Billet> billets;

    public int getTour() {
        return tour;
    }

    public void setTour(int tour) {
        this.tour = tour;
    }
    public Equipe getEquipe1() {
        return (equipes1 != null && equipes1.size() > 0) ? equipes1.get(0) : null;
    }

    public Equipe getEquipe2() {
        return (equipes1 != null && equipes1.size() > 1) ? equipes1.get(1) : null;
    }


}
