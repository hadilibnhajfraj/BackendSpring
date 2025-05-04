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
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    // @JoinColumn(name = "id_equipe")
    private Equipe equipe;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Publication> publications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Commentaire> commentaires;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Billet> billets;

    @OneToMany(mappedBy = "user")
    private List<Tournoi> tournois;

    @OneToMany(mappedBy = "proprietaire")
    private List<Terrain> terrains;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Joueur> joueurs;
}
