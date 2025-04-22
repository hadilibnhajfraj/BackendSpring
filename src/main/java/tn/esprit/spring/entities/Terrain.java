package tn.esprit.spring.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Terrain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nom;
    private String logo;
    private String adresse;
    private int nbSpectateur;
    private int prix;

    @OneToMany(mappedBy = "terrain", cascade = CascadeType.ALL)
    List<MatchFo> matchFos;

    @OneToMany(mappedBy = "terrain", cascade = CascadeType.ALL)
    List<Reservation> reservations;


    @OneToMany(mappedBy = "terrain", cascade = CascadeType.ALL)
    private List<Disponibilite_terrain> disponibilites; // Liste des créneaux

    @ManyToOne
    private User proprietaire;

    public Object getSurface() {
        return null;
    }

    public void setSurface(Object surface) {
    }
}
