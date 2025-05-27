package tn.esprit.spring.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Commentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String texte;
    private LocalDate dateCommentaire;

    private int nombreReactions = 0; // Nombre de réactions par défaut à 0

    @ManyToOne
    private User user;

    @ManyToOne
    private Publication publication;
}
