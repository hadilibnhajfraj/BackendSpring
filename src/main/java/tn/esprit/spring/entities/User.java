package tn.esprit.spring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    // @JoinColumn(name = "id_equipe")
    private Equipe equipe;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Publication> publications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Commentaire> commentaires;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Billet> billets;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Tournoi> tournois;

    @OneToMany(mappedBy = "proprietaire")
    @JsonIgnore
    private List<Terrain> terrains;
}
