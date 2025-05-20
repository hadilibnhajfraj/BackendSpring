package tn.esprit.spring.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReactionCommentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String type; // ❤️, 😂, 😢, 😡

    @ManyToOne
    private Commentaire commentaire;

    @ManyToOne
    private User user;
}
