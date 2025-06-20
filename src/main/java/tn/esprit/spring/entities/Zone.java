package tn.esprit.spring.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Enumerated(EnumType.STRING)
    NomZone nom; // Enum : A, B, ABONNEMENT_A, ABONNEMENT_B

    @ManyToOne
    Evenement evenement;

    boolean estPleine;

    int capaciteMax; // Capacité totale de la zone
    int nbPlacesReservees; // Nombre de places déjà réservées

}
