package tn.esprit.spring.entities;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(indexes = @Index(name = "idx_reservation_email", columnList = "email"))
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String nom;
    String prenom;

    @Column(nullable = false)
    @Email(message = "Email invalide")
    @NotBlank(message = "Email est obligatoire")
    String email;


    LocalDateTime dateCreation;
    LocalDateTime dateExpiration;
    LocalDateTime dateReservation;

    @Min(1)
    @Max(5)
    private int nombrePlaces;

    @Enumerated(EnumType.STRING)
    private EtatReservation etat = EtatReservation.EN_ATTENTE;

    @ManyToOne
    Evenement evenement;

    @ManyToOne
    private Zone zone;

    @ManyToOne
    @JoinColumn(name = "abonnement_id")
    @JsonBackReference
    private Abonnement abonnement;
    @Column(unique = true)
    private String token;


}