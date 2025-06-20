package tn.esprit.spring.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(exclude = "reservations") // Évite boucle infinie dans les logs
public class Abonnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    LocalDate dateDeCreation;

    @Enumerated(EnumType.STRING)
    Etat_Presence etat = Etat_Presence.NOT_PRESENT;

    @ManyToOne
    Tournoi tournoi; // Lien vers le tournoi

    @OneToMany(mappedBy = "abonnement", cascade = CascadeType.ALL)
    @JsonManagedReference
    List<Reservation> reservations;
}
