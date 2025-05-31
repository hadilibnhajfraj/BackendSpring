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
    @Column(unique = true)
    private String email;
    private String password;
    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', role=" + role + "}";
    }

    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(unique = true)
    private String email;
    private String password;
    @Column(name = "reset_token")
    private String resetToken;

    @ManyToOne
    @JsonIgnore
    // @JoinColumn(name = "id_equipe")
    private Equipe equipe;
    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', role=" + role + "}";
    }
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Publication> publications;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonIgnore
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Joueur> joueurs;
}
