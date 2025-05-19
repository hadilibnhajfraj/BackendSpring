package tn.esprit.spring.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

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

    @OneToMany(mappedBy = "equipe", cascade = CascadeType.ALL)
    @JsonManagedReference  // <-- Garder ça si tu veux, ou même le supprimer temporairement pour tester
    List<User> users;


    @ManyToMany(mappedBy = "equipes1")
    List<MatchFo> matchsEquipe1;

    @ManyToMany
  /*  @JoinTable(
            name = "Tournoi_Equipe",
            joinColumns = @JoinColumn(name = "id_equipe"),
            inverseJoinColumns = @JoinColumn(name = "id_tournoi")
    )*/
    private List<Tournoi> tournois;
    // In Equipe.java
    @Override
    public String toString() {
        return "Equipe{id=" + idEquipe + ", name='" + nom + "'}";
    }

}