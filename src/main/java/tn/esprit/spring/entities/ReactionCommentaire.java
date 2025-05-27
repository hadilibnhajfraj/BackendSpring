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
    @JoinColumn(name = "commentaire_id", nullable = false)
    private Commentaire commentaire;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
