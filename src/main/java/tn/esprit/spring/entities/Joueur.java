package tn.esprit.spring.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    Integer idJoueur;
    String nom;
    String prenom;
    float taille;
    float poids;
    String piedFort;
    String poste;
    String mail;
    Long Tel;
    @Column(length = 1000)
    String description;
    @ManyToOne
    @JoinColumn(name = "idEquipe")
    Equipe equipe;
    // In Joueur.java
    @OneToOne
    @JoinColumn(name = "user_id")  // This will create the foreign key in Joueur table
    @JsonIgnoreProperties({"publications", "commentaires", "billets", "tournois", "terrains"})
    private User user;
    @Override
    public String toString() {
        return "Joueur{id=" + idJoueur + ", name='" + nom + "'}";
    }

}
