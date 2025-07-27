package tn.esprit.spring.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private Long id;  // Change this to Long instead of int

    private String nom;
    private String prenom;
    private LocalDate dateNaissance;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(name = "reset_token")
    private String resetToken;

    // New fields for email confirmation
    private String confirmationCode; // To store the confirmation code sent to the user's email
    private LocalDateTime confirmationCodeExpiration; // To store the expiration time of the confirmation code

    private Boolean active = false;

    @ManyToOne
    @JsonIgnore
    private Equipe equipe;

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "', role=" + role + "}";
    }

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

    // Getters and setters...

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(String confirmationCode) {
        this.confirmationCode = confirmationCode;
    }

    public LocalDateTime getConfirmationCodeExpiration() {
        return confirmationCodeExpiration;
    }

    public void setConfirmationCodeExpiration(LocalDateTime expirationTimeMillis) {
        this.confirmationCodeExpiration = expirationTimeMillis;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
